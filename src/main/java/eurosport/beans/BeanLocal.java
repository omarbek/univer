package eurosport.beans;

import eurosport.entities.*;

import javax.ejb.Local;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

@Local
public interface BeanLocal {

    void addFilter(Subject subject, List<Group> groups, int lectures, int practices, Teacher teacher);

    void removeAllFilterGroups();

    List<Filter> getFilters();

    List<FilterGroup> getFilterGroups();

    List<Group> getAllGroups();

    List<Schedule> getAllSchedules();

    List<Schedule> getSchedulesByGroup(Group group);

    List<Group> getAllGroupsInSchedules();

    List<ASD> getASDs();

    List<Group> getGroupsByFilter(Filter filter);

    Subject getSubjectById(Long subjectId);

    Teacher getTeacherById(Long subjectId);

    Group getGroupById(Long group1);

    void clearAsd();

    void removeAllFilters();

    void removeAllSchedules();

    List<WeekDay> getWeekDays();

    boolean scheduleAlreadyHas(Teacher teacher, Group group, WeekDay weekDay, int lessonOrder, Subject subject);

    void addSchedule(Group group, Room room, Subject subject, Teacher teacher, WeekDay weekDay, int lessonOrder, int roomType);

    List<Room> getLectureRooms(int numberOfStudents);

    List<Room> getPracticeRooms(int numberOfStudents);

    boolean scheduleAlreadyHas(Room room, WeekDay weekDay, int lessonOrder);

    List<WeekDay> getAllWeekDays();

    BigDecimal getNumberOfStudentsByFilter(Filter filter);

    boolean groupAlreadyHasNLectures(WeekDay weekDay, Group group, int times);
}
