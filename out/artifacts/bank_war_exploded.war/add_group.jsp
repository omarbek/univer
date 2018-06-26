<%@ page import="java.util.List" %>
<%@ page import="eurosport.entities.Group" %>
<%@ page import="eurosport.entities.Schedule" %>
<%@ page import="eurosport.entities.WeekDay" %>
<%@ page import="eurosport.entities.Teacher" %><%--
  Created by IntelliJ IDEA.
  User: Omarbek
  Date: 27.02.2018
  Time: 16:42
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Group</title>
</head>
<body>
<a href="info?page=index">Main</a>
<a href="info?page=schedule">schedule</a>
<a href="info?page=teacher">add_teacher</a>
<a href="info?page=subject">add_subject</a>
<a href="info?page=room">add_room</a>
<a href="info?page=add_group">add_group</a>
<a href="info?page=filter">add_filter</a>
<br>
<p>Add a group</p>
<form method="post" action="info">
    <input type="hidden" name="page" value="add_group">
    name: <input type="text" name="name"><br>
    number_of_students: <input type="text" name="number_of_students"><br>
    <input type="submit" value="add">
</form>
<%
    if (request.getAttribute("groups") != null) {
        List<Group> groups = (List<Group>) request.getAttribute("groups");
        for (int i = 0; i < groups.size(); i++) {
            out.print(i + 1 + ". " + groups.get(i).getName()+" ("+groups.get(i).getNumberOfStudents()+")");
%>
<form method="post" action="info">
    <input type="hidden" name="page" value="delete_group">
    <input type="hidden" name="id" value="<%=groups.get(i).getId()%>"><br>
    <input type="submit" value="delete">
</form>
<%
        }
    }
%>
</body>
</html>
