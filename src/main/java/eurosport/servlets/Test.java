package eurosport.servlets;

import eurosport.beans.BeanLocal;
import eurosport.entities.*;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Servlet implementation class Test
 */
@WebServlet("/info")
public class Test extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final int LEC = 1;
    private static final int PRAC = 3;

    @EJB
    private BeanLocal bean;

    public Test() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String page = request.getParameter("page");

        if (page.startsWith("group")) {
            List<Group> groups = bean.getAllGroups();
            for (Group group : groups) {
                if (page.equals("group-" + group.toString())) {
                    List<Schedule> schedulesByGroup = bean.getSchedulesByGroup(group);
                    List<WeekDay> weekDays = bean.getAllWeekDays();

                    request.setAttribute("schedulesByGroup", schedulesByGroup);
                    request.setAttribute("weekDays", weekDays);
                    request.setAttribute("groups", bean.getAllGroupsInSchedules());
                    request.getRequestDispatcher("schedule.jsp").forward(request, response);
                }
            }
        } else if (page.equals("index")) {
            request.getRequestDispatcher("index.jsp").forward(request, response);
        } else if (page.equals("schedule")) {
            request.getRequestDispatcher("schedule.jsp").forward(request, response);
        } else if (page.equals("teacher")) {
            List<Teacher> teachers = bean.getAllTeachers();
            request.setAttribute("teachers", teachers);
            request.getRequestDispatcher("teacher.jsp").forward(request, response);
        } else if (page.equals("subject")) {
            request.getRequestDispatcher("subject.jsp").forward(request, response);
        } else if (page.equals("room")) {
            List<Room> rooms = bean.getAllRooms();
            List<RoomType> roomTypes = bean.getAllRoomTypes();
            request.setAttribute("rooms", rooms);
            request.setAttribute("roomTypes", roomTypes);
            request.getRequestDispatcher("room.jsp").forward(request, response);
        } else if (page.equals("add_group")) {
            List<Group> groups = bean.getAllGroups();
            request.setAttribute("groups", groups);
            request.getRequestDispatcher("add_group.jsp").forward(request, response);
        } else if (page.equals("filter")) {
            request.getRequestDispatcher("filter.jsp").forward(request, response);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String page = request.getParameter("page");
        if (page.equals("generate")) {
//            String schedules=bean.getASDs().get(0).getRoomType().getName();
//            bean.clearAsd();
            createEntities();
            bean.removeAllSchedules();
            generate();
            List<Group> groups = bean.getAllGroupsInSchedules();

            request.setAttribute("groups", groups);
            request.getRequestDispatcher("schedule.jsp").forward(request, response);
        } else if (page.equals("add_teacher")) {
            String fio = request.getParameter("fio");
            bean.addTeacher(fio);

            List<Teacher> teachers = bean.getAllTeachers();
            request.setAttribute("teachers", teachers);
            request.getRequestDispatcher("teacher.jsp").forward(request, response);
        } else if (page.equals("delete_teacher")) {
            Long id = Long.parseLong(request.getParameter("id"));
            bean.deleteTeacher(id);

            List<Teacher> teachers = bean.getAllTeachers();
            request.setAttribute("teachers", teachers);
            request.getRequestDispatcher("teacher.jsp").forward(request, response);
        } else if (page.equals("add_group")) {
            String name = request.getParameter("name");
            Integer numberOfStudents = Integer.parseInt(request.getParameter("number_of_students"));
            bean.addGroup(name, numberOfStudents);

            List<Group> groups = bean.getAllGroups();
            request.setAttribute("groups", groups);
            request.getRequestDispatcher("add_group.jsp").forward(request, response);
        } else if (page.equals("delete_group")) {
            Long id = Long.parseLong(request.getParameter("id"));
            bean.deleteGroup(id);

            List<Group> groups = bean.getAllGroups();
            request.setAttribute("groups", groups);
            request.getRequestDispatcher("add_group.jsp").forward(request, response);
        }
    }

    private void generate() {
        for (Filter filter : bean.getFilters()) {
            List<Group> groups = bean.getGroupsByFilter(filter);
            generateSubjects(filter, null, groups);
            for (Group group : groups) {
                generateSubjects(filter, group, groups);
            }
        }
    }

    private void generateSubjects(Filter filter, Group group, List<Group> groups) {
        if (filter.getSubject().getNeedComputer() == 1) {//computer
            addLectureAndPractice(filter, group, false, groups);
        } else {
            addLectureAndPractice(filter, group, true, groups);
        }
    }

    private void addLectureAndPractice(Filter filter, Group group, boolean notComputer, List<Group> groups) {
        if (group == null) {
            for (int i = 0; i < filter.getLectures() / 15; i++) {
                if (choosedDayAndTime(filter, null, notComputer, groups)) {
                    //TODO change choosedDayAndTime to void
                }
            }
        } else {
            for (int i = 0; i < filter.getPractices() / groups.size() / 15; i++) {
                if (choosedDayAndTime(filter, group, notComputer, groups)) {
                    //TODO change choosedDayAndTime to void
                }
            }
        }
    }

    private boolean choosedDayAndTime(Filter filter, Group group, boolean notComputer, List<Group> groups) {
        for (WeekDay weekDay : bean.getWeekDays()) {
            for (int time = 0; time < 5; time++) {
                if (group == null) {
                    BigDecimal size = bean.getNumberOfStudentsByFilter(filter);
                    if (added(groups, filter.getSubject(), filter.getTeacher(), weekDay, time + 1, LEC,
                            size.intValue(), notComputer)) {
                        return true;
                    }
                } else {
                    groups = new ArrayList<>();
                    groups.add(group);
                    if (added(groups, filter.getSubject(), filter.getTeacher(), weekDay, time + 1, PRAC,
                            group.getNumberOfStudents(), notComputer)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean added(List<Group> groups, Subject subject, Teacher teacher, WeekDay
            weekDay, int time, int roomType, int numberOfStudents, boolean notComputer) {
        for (Group group : groups) {
            if (bean.scheduleAlreadyHas(teacher, group, weekDay, time, subject)) {
                return false;
            }
            if (roomType == LEC && bean.groupAlreadyHasNLectures(weekDay, group, 2)) {
                return false;
            }
            if (roomType == PRAC && bean.lectureAfterThisTime(group, subject, weekDay, time)) {
                return false;
            }
        }
        Room room = getRoom(weekDay, time, roomType, numberOfStudents, notComputer);
        for (Group group : groups) {
            bean.addSchedule(group, room, subject, teacher, weekDay, time, roomType);
        }
        return true;
    }

    private Room getRoom(WeekDay weekDay, int time, int roomType, int numberOfStudents, boolean notComputer) {
        if (roomType == LEC) {
            List<Room> lectureRooms = bean.getLectureRooms(numberOfStudents);
            for (Room room : lectureRooms) {
                if (!bean.scheduleAlreadyHas(room, weekDay, time)) {
                    return room;
                }
            }
            return lectureRooms.get(0);
        } else {
            List<Room> practiceRooms = bean.getPracticeRooms(numberOfStudents);
            for (Room room : practiceRooms) {
                if (!bean.scheduleAlreadyHas(room, weekDay, time)) {
                    if (room.getHasComputer() == 1 || notComputer) {
                        return room;
                    }
                }
            }
        }
        return new Room();
    }

    private void createEntities() {
        Long g_101_71 = 1L;
        Long g_101_72 = 2L;
        Long g_101_73 = 3L;
        Long g_102_71 = 4L;
        Long g_102_72 = 5L;
        Long g_102_73 = 6L;
        Long g_102_74 = 7L;

        Long maul = 1L;
        Long ism = 2L;
        Long tol = 3L;
        Long rys = 4L;
        Long sars = 5L;
        Long kish = 6L;
        Long djiyan = 7L;
        Long rus = 8L;
        Long tur = 9L;
        Long bay = 10L;
        Long alim = 11L;

        Long otbasy = 1L;
        Long semia = 2L;
        Long doshkolnaia = 3L;
        Long mektepke_deiyngi = 4L;
        Long razvitie = 5L;
        Long vozrast = 6L;
        Long zhas = 7L;
        Long fiz_damu = 8L;
        Long mat_bastauish = 9L;
        Long english = 10L;
        Long russian = 11L;
        Long kazakh = 12L;

        bean.removeAllFilterGroups();
        bean.removeAllFilters();
        createFilter(mat_bastauish, createGroups(g_102_71, g_102_72, g_102_73, g_102_74), 15, 120, kish);
        createFilter(mektepke_deiyngi, createGroups(g_101_71, g_101_72), 30, 60, tol);
        createFilter(doshkolnaia, createGroups(g_101_73), 30, 30, ism);
        createFilter(semia, createGroups(g_101_73), 30, 15, maul);
//        createFilter(otbasy, createGroups(g_101_71, g_101_72), 30, 0, maul);
//        createFilter(fiz_damu, createGroups(g_102_71, g_102_72, g_102_73), 15, 45, rys);
//        createFilter(zhas, createGroups(g_101_71, g_101_72), 15, 30, sars);
//        createFilter(razvitie, createGroups(g_102_74), 15, 15, rys);
//        createFilter(vozrast, createGroups(g_101_73), 15, 15, rys);
//        createFilter(english, createGroups(g_101_73, g_102_74), 0, 45, djiyan);
//        createFilter(kazakh, createGroups(g_102_72, g_102_73), 0, 120, tur);
//        createFilter(english, createGroups(g_101_71, g_101_72), 0, 90, djiyan);
//        createFilter(russian, createGroups(g_102_74), 0, 60, rus);
//        createFilter(kazakh, createGroups(g_102_71), 0, 60, bay);
//        createFilter(english, createGroups(g_102_71, g_102_72, g_102_73), 0, 135, djiyan);
//        createFilter(otbasy, createGroups(g_101_71, g_101_72), 0, 30, alim);
    }

    private void createFilter(Long subjectId, List<Group> filterGroups, int lectures, int practices, Long teacherId) {
        Subject subject = bean.getSubjectById(subjectId);
        Teacher teacher = bean.getTeacherById(teacherId);
        bean.addFilter(subject, filterGroups, lectures, practices, teacher);
    }

    private List<Group> createGroups(Long groupId) {
        List<Group> groups = new ArrayList<>();
        Group group = bean.getGroupById(groupId);
        groups.add(group);
        return groups;
    }

    private List<Group> createGroups(Long group1, Long group2) {
        List<Group> groups = new ArrayList<>();
        groups.add(bean.getGroupById(group1));
        groups.add(bean.getGroupById(group2));
        return groups;
    }

    private List<Group> createGroups(Long group1, Long group2, Long group3) {
        List<Group> groups = new ArrayList<>();
        groups.add(bean.getGroupById(group1));
        groups.add(bean.getGroupById(group2));
        groups.add(bean.getGroupById(group3));
        return groups;
    }

    private List<Group> createGroups(Long group1, Long group2, Long group3, Long group4) {
        List<Group> groups = new ArrayList<>();
        groups.add(bean.getGroupById(group1));
        groups.add(bean.getGroupById(group2));
        groups.add(bean.getGroupById(group3));
        groups.add(bean.getGroupById(group4));
        return groups;
    }
}
