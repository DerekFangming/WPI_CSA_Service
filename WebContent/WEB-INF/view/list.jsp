<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
  <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0-alpha.6/css/bootstrap.min.css" >
  <script src="https://code.jquery.com/jquery-3.1.1.slim.min.js"></script>
  <script src="https://cdnjs.cloudflare.com/ajax/libs/tether/1.4.0/js/tether.min.js" ></script>
  <script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0-alpha.6/js/bootstrap.min.js"></script>
</head>
<body>
  <br/>
  <h2>Dumpling Event tickets, total sale count : ${count}</h2>

  <br/>
  <table class="table table-striped table-bordered">
    <thead class="thead-default">
      <tr>
        <th>Name</th>
        <th>Email</th>
      </tr>
    </thead>
    <tbody>
      <c:forEach items="${nameList}" var="user">
        <tr>
          <th>${user.name}</th>
          <th>${user.username}</th>
        </tr>
      </c:forEach>
    </tbody>
  </table>


</body>
</html>