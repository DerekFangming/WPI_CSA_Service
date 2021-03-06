<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import = "java.util.ResourceBundle" %>
<% ResourceBundle resource = ResourceBundle.getBundle("dataSource");
	String BScss=resource.getString("BScss"); String rootDir=resource.getString("rootDir"); %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="en">
	<head>
		<meta charset="utf-8">
		<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
		<meta name="description" content="">
		<meta name="author" content="">
		<title>Oops 403 Error</title>
		<link href="<%=BScss %>" rel="stylesheet">
		<link href="/resources/css/main.css" rel="stylesheet">
	</head>
	<body>
		<!-- Navigation -->
		<nav class="navbar navbar-expand-md navbar-dark bg-dark fixed-top">
			<div class="container">
				<a class="navbar-brand" href="<%=rootDir %>">WPI CSA</a>
				<button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarResponsive" aria-controls="navbarResponsive" aria-expanded="false" aria-label="Toggle navigation">
				<span class="navbar-toggler-icon"></span>
				</button>
			</div>
		</nav>
		<!-- Page Content -->
		<div class="container">
			<!-- Jumbotron Header -->
			<header class="jumbotron my-4">
				<center>
					<h1 class="display-3">Oops, forbidden</h1>
				</center>
				<br><br><br>
				<center>
					<p class="lead">You do not have permission to access this page.${errorMessage} If you believe this is an error, please contact CSA</p>
				</center>
				<br>
				<center><a class="btn btn-primary" href="<%=rootDir %>" role="button">Take me back to home page</a></center>
			</header>
		</div>
		<!-- /.container -->
		<%@include file="../subview/footer.jsp" %>
	</body>
</html>