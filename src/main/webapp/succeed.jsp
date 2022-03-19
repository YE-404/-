<%--
  Created by IntelliJ IDEA.
  User: sy
  Date: 2021/11/26
  Time: 下午1:39
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Title</title>
</head>
<body>
<h1>succeed</h1>
<h1>${username}</h1>
<form action="http://localhost:8080/mydemo_war/user/login" method="post" enctype="multipart/form-data">
    <input type="input" name="username">
    <input type="file" name="uploadFiles" multiple="multiple"><br>
<%--    <input type="file" name="uploadFile" multiple="multiple" ><br>--%>
    <input type="submit" value="submit">
</form>

</body>
</html>
