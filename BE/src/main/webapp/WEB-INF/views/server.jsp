<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
</head>
<body>
    <form method="post" action="/api/server/" enctype="multipart/form-data">
        서버 이름 <input name="name">
        유저 seq <input name="hostSeq">
        파일 <input type="file" name="file">
        <input type="submit">
    </form>
</body>
</html>