<%@ page import="java.util.List" %>
<%@ page import="eurosport.entities.Group" %>
<%@ page import="eurosport.entities.Schedule" %>
<%@ page import="eurosport.entities.WeekDay" %><%--
  Created by IntelliJ IDEA.
  User: Omarbek
  Date: 27.02.2018
  Time: 16:42
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Schedule</title>
</head>
<style>
    table, th, td {
        border: 1px solid black;
        border-collapse: collapse;
    }

    th, td {
        padding: 5px;
        text-align: left;
    }
</style>
<body>
<a href="info?page=index">Main</a>
<a href="info?page=schedule">schedule</a>
<a href="info?page=teacher">add_teacher</a>
<a href="info?page=subject">add_subject</a>
<a href="info?page=room">add_room</a>
<a href="info?page=add_group">add_group</a>
<a href="info?page=filter">add_filter</a>
<form method="post" action="info">
    <input type="hidden" name="page" value="generate">
    <input type="submit" value="generate">
</form>
<%
    //    String name=(String) request.getAttribute("schedules");
//    out.print(name);
    if (request.getAttribute("groups") != null) {
        List<Group> groups = (List<Group>) request.getAttribute("groups");
        for (Group group : groups) {
%>
<a href="info?page=group-<%=group%>"><%=group%>
</a>
<%
        }
    }
%><br>
<%
    if (request.getAttribute("schedulesByGroup") != null) {
        List<Schedule> schedules = (List<Schedule>) request.getAttribute("schedulesByGroup");
        List<WeekDay> weekDays = (List<WeekDay>) request.getAttribute("weekDays");
%>
<table style="width:100%">
    <tr>
        <th rowspan="2"></th>
        <th>Уроки</th>
        <th colspan="3"><%= schedules.get(0).getGroup() + " (" + schedules.get(0).getGroup().getNumberOfStudents() + ")" %>
        </th>
    </tr>
    <tr>
        <td></td>
        <td>Предмет</td>
        <td>Препод</td>
        <td>№</td>
    </tr>

    <%
        for (WeekDay weekDay : weekDays) {
    %>
    <tr>
        <th rowspan="6"><%=weekDay%>
        </th>
    </tr>
    <%
        for (int time = 0; time < 5; time++) {
    %>
    <tr>
        <td><%=time + 1%>
        </td>
        <%
            boolean draw = false;
            for (Schedule schedule : schedules) {
                if ((time + 1) == schedule.getLessonOrder() && schedule.getWeekDay().getName().equals(weekDay.getName())) {
                    draw = true;
        %>
        <td><%=schedule.getSubject() + " (" + schedule.getRoomType() + ")"%>
        </td>
        <td><%=schedule.getTeacher()%>
        </td>
        <td><%=schedule.getRoom()%>
        </td>
        <%
                }
            }
            if (!draw) {
        %>
        <td></td>
        <td></td>
        <td></td>
        <%
            }
        %>
    </tr>
    <%
        }
    %>
    <%
        }
    %>

</table>
<%
    }
%>

</body>
</html>
