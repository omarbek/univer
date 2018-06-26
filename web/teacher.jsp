<%@ page import="java.util.List" %>
<%@ page import="eurosport.entities.Group" %>
<%@ page import="eurosport.entities.Schedule" %>
<%@ page import="eurosport.entities.WeekDay" %>
<%@ page import="javax.swing.text.TabExpander" %>
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
    <title>Teacher</title>
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
<p>Add a teacher</p>
<form method="post" action="info">
    <input type="hidden" name="page" value="add_teacher">
    fio: <input type="text" name="fio">
    <input type="submit" value="add">
</form>
<%
    if (request.getAttribute("teachers") != null) {
        List<Teacher> teachers = (List<Teacher>) request.getAttribute("teachers");
        for (int i = 0; i < teachers.size(); i++) {
            out.print(i + 1 + ". " + teachers.get(i).getFio());
%>
<form method="post" action="info">
    <input type="hidden" name="page" value="delete_teacher">
    <input type="hidden" name="id" value="<%=teachers.get(i).getId()%>"><br>
    <input type="submit" value="delete">
</form>
<%
        }
    }
%>
</body>
</html>
