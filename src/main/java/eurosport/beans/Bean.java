package eurosport.beans;

import eurosport.entities.*;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

/**
 * Session Bean implementation class UserBean
 */
@Stateless
@LocalBean
public class Bean implements BeanLocal {

    @PersistenceContext(unitName = "eurosportPU")
    private EntityManager em;

    private static final int LECTURE = 1;
    private static final int PRACTICE = 3;

    @Override
    public void addFilter(Subject subject, List<Group> groups, int lectures, int practices, Teacher teacher) {
        Filter filter = new Filter();
        filter.setSubject(subject);
        filter.setLectures(lectures);
        filter.setPractices(practices);
        filter.setTeacher(teacher);
        em.persist(filter);

        for (Group group : groups) {
            FilterGroup filterGroups = new FilterGroup();
            filterGroups.setFilter(filter);
            filterGroups.setGroup(group);
            em.persist(filterGroups);
        }
    }

    @Override
    public void removeAllFilterGroups() {
        for (FilterGroup filterGroup : getFilterGroups()) {
            em.remove(filterGroup);
        }
    }

    @Override
    public List<Filter> getFilters() {
        return em.createNativeQuery("select f.* from filters f" +
                " left join subjects s on s.id=f.subject_id" +
                " order by s.need_computer desc," +
                " f.lectures desc, f.practices desc", Filter.class).getResultList();
    }

    @Override
    public List<FilterGroup> getFilterGroups() {
        return em.createQuery("select fg from FilterGroup fg", FilterGroup.class).getResultList();
    }

    @Override
    public List<Group> getAllGroups() {
        return em.createQuery("select g from Group g", Group.class).getResultList();
    }

    @Override
    public List<Schedule> getAllSchedules() {
        return em.createQuery("select s from Schedule s", Schedule.class).getResultList();
    }

    @Override
    public List<Schedule> getSchedulesByGroup(Group group) {
        return em.createNativeQuery("select * from schedules" +
                " where group_id=:groupId", Schedule.class).
                setParameter("groupId", group.getId()).getResultList();
    }

    @Override
    public List<Group> getAllGroupsInSchedules() {
        return (List<Group>) em.createNativeQuery("select distinct gr.* from schedules sched" +
                " left join groups gr on gr.id=sched.group_id", Group.class).getResultList();
    }

    @Override
    public List<ASD> getASDs() {
        return em.createQuery("select s from ASD s", ASD.class).getResultList();
    }

    @Override
    public List<Group> getGroupsByFilter(Filter filter) {
        return em.createNativeQuery("select gr.* from filter_groups fg" +
                " left join groups gr on gr.id=fg.group_id" +
                " where fg.filter_id=:filter_id", Group.class)
                .setParameter("filter_id", filter.getId()).getResultList();
    }

    @Override
    public Subject getSubjectById(Long subjectId) {
        return (Subject) em.createQuery("select s from Subject s where s.id=:subjectId").setParameter("subjectId", subjectId)
                .getSingleResult();
    }

    @Override
    public Teacher getTeacherById(Long teacherId) {
        return (Teacher) em.createQuery("select s from Teacher s where s.id=:teacherId").setParameter("teacherId", teacherId)
                .getSingleResult();
    }

    @Override
    public Group getGroupById(Long groupId) {
        return (Group) em.createQuery("select s from Group s where s.id=:groupId").setParameter("groupId", groupId)
                .getSingleResult();
    }

    @Override
    public void clearAsd() {
        for (ASD asd : getASDs()) {
            em.remove(asd);
        }
    }

    @Override
    public void removeAllFilters() {
        for (Filter filter : getFilters()) {
            em.remove(filter);
        }
    }

    @Override
    public void removeAllSchedules() {
        for (Schedule schedule : getAllSchedules()) {
            em.remove(schedule);
        }
    }

    @Override
    public List<WeekDay> getWeekDays() {
        return em.createQuery("select s from WeekDay s", WeekDay.class).getResultList();
    }

    @Override
    public boolean scheduleAlreadyHas(Teacher teacher, Group group, WeekDay weekDay, int lessonOrder, Subject subject) {
        List resultList = em.createNativeQuery("SELECT 1 FROM schedules WHERE  " +
                "(teacher_id=:teacherId and week_day_id=:weekDayId and lesson_order=:lessonOrder)|| " +
                "(group_id=:groupId and week_day_id=:weekDayId and lesson_order=:lessonOrder)|| " +
                "(subject_id=:subjectId and week_day_id=:weekDayId and lesson_order=:lessonOrder)")
                .setParameter("teacherId", teacher.getId()).setParameter("weekDayId", weekDay.getId())
                .setParameter("lessonOrder", lessonOrder).setParameter("groupId", group.getId())
                .setParameter("subjectId", subject.getId()).getResultList();
        if (resultList.size() > 0) {
            return true;
        }
        return false;
    }

    @Override
    public void addSchedule(Group group, Room room, Subject subject, Teacher teacher, WeekDay weekDay, int lessonOrder,
                            int roomTypeId) {
        try {
            Schedule schedule = new Schedule();
            schedule.setGroup(group);
            schedule.setRoom(room);
            schedule.setSubject(subject);
            schedule.setTeacher(teacher);
            schedule.setWeekDay(weekDay);
            schedule.setLessonOrder(lessonOrder);
            RoomType roomType = getRoomType(roomTypeId);
            schedule.setRoomType(roomType);

            em.persist(schedule);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public List<Room> getLectureRooms(int numberOfStudents) {
        return getRooms(numberOfStudents, PRACTICE);
    }

    @Override
    public List<Room> getPracticeRooms(int numberOfStudents) {
        return getRooms(numberOfStudents, LECTURE);
    }

    @Override
    public boolean scheduleAlreadyHas(Room room, WeekDay weekDay, int lessonOrder) {
        List resultList = em.createNativeQuery("SELECT 1 FROM schedules WHERE  " +
                "(room_id=:roomId and week_day_id=:weekDayId and lesson_order=:lessonOrder)")
                .setParameter("roomId", room.getId()).setParameter("weekDayId", weekDay.getId())
                .setParameter("lessonOrder", lessonOrder).getResultList();
        if (resultList.size() > 0) {
            return true;
        }
        return false;
    }

    @Override
    public List<WeekDay> getAllWeekDays() {
        return em.createQuery("select g from WeekDay g", WeekDay.class).getResultList();
    }

    @Override
    public BigDecimal getNumberOfStudentsByFilter(Filter filter) {
        BigDecimal size = (BigDecimal) em.createNativeQuery("SELECT sum(gr.number_of_students) from groups gr " +
                "left join filter_groups fg on fg.group_id=gr.id " +
                "left join filters f on f.id=fg.filter_id " +
                "where f.id=:filterId").setParameter("filterId", filter.getId()).getSingleResult();

        return size;
    }

    @Override
    public boolean groupAlreadyHasNLectures(WeekDay weekDay, Group group, int times) {
        BigInteger size = (BigInteger) em.createNativeQuery("select count(1) from schedules " +
                "where week_day_id=:weekDayId and group_id=:groupId and room_type_id=:roomTypeId")
                .setParameter("weekDayId", weekDay.getId()).setParameter("groupId", group.getId())
                .setParameter("roomTypeId", LECTURE).getSingleResult();
        if (size.intValue() == times) {
            return true;
        }
        return false;
    }

    private List getRooms(int numberOfStudents, int roomTypeId) {
        return em.createNativeQuery("SELECT * FROM rooms " +
                "where room_type_id!=:roomTypeId and capacity>:numberOfStudents " +
                "order by capacity", Room.class)
                .setParameter("roomTypeId", roomTypeId).setParameter("numberOfStudents", numberOfStudents)
                .getResultList();
    }

    private RoomType getRoomType(int roomTypeId) {
        return (RoomType) em.createQuery("select r from RoomType r where r.id=:roomTypeId")
                .setParameter("roomTypeId", (long) roomTypeId).getSingleResult();
    }
}
