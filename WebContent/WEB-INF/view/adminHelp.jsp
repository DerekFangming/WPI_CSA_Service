<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import = "java.util.ResourceBundle" %>
<% ResourceBundle resource = ResourceBundle.getBundle("dataSource");
	String BScss=resource.getString("BScss");String BSjs=resource.getString("BSjs");String JQjs=resource.getString("JQjs"); %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="en">
	<head>
		<meta charset="utf-8">
		<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
		<meta name="description" content="">
		<meta name="author" content="">
		<title>WPI CSA - Admin Portal</title>
		<link href="<%=BScss %>" rel="stylesheet">
		<script src="<%=JQjs %>"></script>
		<link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css" rel="stylesheet">
		<link href="/resources/css/main.css?v=3" rel="stylesheet">
		<script src="/resources/js/common.js?v=3"></script>
	</head>
	<body>
		<!-- Navigation -->
		<nav class="navbar navbar-expand-md navbar-dark bg-primary fixed-top">
			<div class="container">
				<a class="navbar-brand" href="../../">WPI CSA Admin</a>
				<button class="navbar-toggler navbar-toggler-right" type="button" data-toggle="collapse" data-target="#navbarResponsive" aria-controls="navbarResponsive" aria-expanded="false" aria-label="Toggle navigation">
				<span class="navbar-toggler-icon"></span>
				</button>
				<div class="collapse navbar-collapse" id="navbarResponsive">
					<ul class="navbar-nav ml-auto">
						<li class="nav-item">
							<a class="nav-link" href="./event">Event</a>
						</li>
						<li class="nav-item">
							<a class="nav-link" href="./user">User</a>
						</li>
						<li class="nav-item active">
							<a class="nav-link" href="#">Help</a>
						</li>
						<c:if test="${user != null}">
							<li class="nav-item dropdown">
								<a class="nav-link dropdown-toggle" href="#" id="navbarDropdownMenuLink" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
								${user.name}
								</a>
								<div class="dropdown-menu" aria-labelledby="navbarDropdownMenuLink">
									<a class="dropdown-item" href="../../"><i class="fa fa-chevron-circle-left"></i>&nbsp;Back to main site</a>
									<a class="dropdown-item" href="../../logout"><i class="fa fa-sign-out"></i>&nbsp;Log out</a>
								</div>
							</li>
						</c:if>
					</ul>
				</div>
			</div>
		</nav>
		<!-- Page Content -->
		<div class="container">
			<c:choose>
				<c:when test="${user == null}">
					<div class="card mt-3" style="max-width:500px;margin:0 auto;">
						<div class="card-header">
							Please login first
						</div>
						<div class="card-body">
							<form id="loginForm">
								<div class="form-group">
									<label for="recipient-name" class="col-form-label">Username:</label>
									<input type="text"  required="" id="username" name="username" class="form-control" id="username" placeholder="Your username \ email">
								</div>
								<div class="form-group">
									<label for="message-text" class="col-form-label">Password:</label>
									<input type="password" required="" id="password" name="password" class="form-control" id="password" placeholder="Your password">
								</div>
								<div class="form-group">
									<input type="hidden" id="refreshAfterLogin" value="true">
									<button id="signinButton" name="signin" class="btn btn-primary">Log in</button>
								</div>
							</form>
						</div>
					</div>
				</c:when>
				<c:otherwise>
					<div class="row">
						<div class="col-lg-6 col-sm-12 mt-3">
							<div class="card">
								<div class="card-header">
									<h3>Go to Admin Portal and go back</h3>
								</div>
								<div class="card-body">
									<img class="fit-width border" src="https://i.imgur.com/tVWSDh8.gif" alt="adminPortal.gif">
								</div>
							</div>
						</div>
						<div class="col-lg-6 col-sm-12 mt-3">
							<div class="card">
								<div class="card-header">
									<h3>Check information of events</h3>
								</div>
								<div class="card-body">
									<img class="fit-width border" src="https://i.imgur.com/CNGITA6.gif" alt="checkEventInfo.gif">
								</div>
							</div>
						</div>
						<div class="col-lg-6 col-sm-12 mt-3">
							<div class="card">
								<div class="card-header">
									<h3>Control and edit events</h3>
								</div>
								<div class="card-body">
									<img class="fit-width border" src="https://i.imgur.com/dROGJxz.gif" alt="controlEvent.gif">
								</div>
							</div>
						</div>
						<div class="col-lg-6 col-sm-12 mt-3">
							<div class="card">
								<div class="card-header">
									<h3>Manage users</h3>
								</div>
								<div class="card-body">
									<img class="fit-width border" src="https://i.imgur.com/fZsNbIQ.gif" alt="controlUser.gif">
								</div>
							</div>
						</div>
						<div class="col-lg-6 col-sm-12 mt-3">
							<div class="card">
								<div class="card-header">
									<h3>Create article with event and ticket</h3>
								</div>
								<div class="card-body">
									<img class="fit-width border" src="https://i.imgur.com/T4YSQXJ.gif" alt="createEvent.gif">
								</div>
							</div>
						</div>
						<div class="col-lg-6 col-sm-12 mt-3">
							<div class="card">
								<div class="card-header">
									<h3>Intended left blank</h3>
								</div>
								<div class="card-body">
									<img class="fit-width border" src="https://i.imgur.com/m826wT6.gif" alt="intendedBlank.gif">
								</div>
							</div>
						</div>
					</div>
				</c:otherwise>
			</c:choose>
		</div>
		<!-- /.container -->
		<%@include file="subview/popup.jsp" %><!-- This must be placed below all other modals -->
		<%@include file="subview/footer.jsp" %>
		<script src="<%=BSjs %>"></script>
		<c:if test="${user == null}">
			<script>
				$('#loginForm').submit(function (e) {
					$.ajax({
						type: "POST",
						url: "../../web_login",
						data: $("#loginForm").serialize(),
						success: function (data) {
							if (data.startsWith("{")) {
								var obj = $.parseJSON(data);
								showErrorPopup(obj['error']);
							} else {
								location.reload();
							}
						},
						error: function (jqXHR, textStatus, errorThrown) {
							showErrorPopup('Unknown error occured. Please contact support');
						}
					});
					e.preventDefault();
				});
			</script>
		</c:if>
	</body>
</html>