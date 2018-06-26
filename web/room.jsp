<%@ page import="java.util.List" %>
<%@ page import="eurosport.entities.*" %>
<%@ page import="javax.swing.text.rtf.RTFEditorKit" %><%--
  Created by IntelliJ IDEA.
  User: Omarbek
  Date: 27.02.2018
  Time: 16:42
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Room</title>
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
<p>Add a room</p>
<form method="post" action="info">
    <input type="hidden" name="page" value="add_room">
    name: <input type="text" name="name"><br>
    capacity: <input type="text" name="capacity"><br>
    computer: <input type="checkbox" name="computer"><br>
    room type:
    <select name="room_type">
        <%
            if(request.getAttribute("roomTypes")!=null){
                List<RoomType> roomTypes=(List<RoomType> )request.getAttribute("roomTypes");
                for(RoomType roomType:roomTypes){
        %>
                <option value="<%=roomType.getId()%>"><%=roomType%></option>
        <%
                }
            }
        %>
        <option></option>
    </select>
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
