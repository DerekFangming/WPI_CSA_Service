<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import = "java.util.ResourceBundle" %>
<% ResourceBundle resource = ResourceBundle.getBundle("dataSource");
	String BScss=resource.getString("BScss");String BSjs=resource.getString("BSjs");String JQjs=resource.getString("JQjs");
	String tokenKey=resource.getString("tokenKey");%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="en">
	<head>
		<meta charset="utf-8">
		<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
		<meta name="description" content="">
		<meta name="author" content="">
		<title>WPI CSA - Feed</title>
		<link href="<%=BScss %>" rel="stylesheet">
		<script src="<%=JQjs %>"></script>
		<script src="https://js.braintreegateway.com/web/dropin/1.9.2/js/dropin.min.js"></script>
		<link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css" rel="stylesheet">
		<link href="/resources/css/main.css?v=3" rel="stylesheet">
		<script src="/resources/js/common.js?v=3"></script>
		<script src="/resources/js/parser.js?v=3"></script>
	</head>
	<body>
		<!-- Navigation -->
		<nav class="navbar navbar-expand-md navbar-dark bg-dark fixed-top">
			<div class="container">
				<a class="navbar-brand" href="./">WPI CSA</a>
				<button class="navbar-toggler navbar-toggler-right" type="button" data-toggle="collapse" data-target="#navbarResponsive" aria-controls="navbarResponsive" aria-expanded="false" aria-label="Toggle navigation">
				<span class="navbar-toggler-icon"></span>
				</button>
				<div class="collapse navbar-collapse" id="navbarResponsive">
					<ul class="navbar-nav ml-auto">
						<li class="nav-item active">
							<a class="nav-link" href="./">Life</a>
						</li>
						<li class="nav-item">
							<a class="nav-link" href="./sg">Survival Guide</a>
						</li>
						<c:if test="${user != null}">
							<%@include file="subview/navUserLoggedIn.jsp" %>
						</c:if>
						<c:if test="${user == null}">
							<%@include file="subview/navLogin.jsp" %>
						</c:if>
					</ul>
				</div>
			</div>
		</nav>
		<!-- Page Content -->
		<div class="container">
			<c:choose>
				<c:when test="${notFound}">
					<header class="jumbotron my-4">
						<center>
							<h1 class="display-4 mb-0">The article you are looking for does not exist</h1>
							<p class="lead mt-2 mb-0">If you believe this is an error, please contact admin@fmning.com</p>
						</center>
					</header>
				</c:when>
				<c:otherwise>
					<!-- Jumbotron Header -->
					<header class="jumbotron my-4">
						<center>
							<h1 class="display-4 mb-0">${fm.feed.title}</h1>
							<p class="mb-0">
								<span class="badge badge-secondary">${fm.feed.type}</span>
								<span class="text-muted">
									&nbsp; Created by <b>${fm.ownerName}</b> on <script> parseDate( '${fm.feed.createdAt}' ); </script>
								</span>
							</p>
						</center>
					</header>
					<div class="card mt-3">
						<div class="card-header">
							<script type="text/javascript">
								var body = '${fm.feed.body}';
								document.write(processContent(body));
							</script>
						</div>
					</div>
					<c:if test="${fm.event != null}">
						<div class="card mt-3">
							<div class="card-header">
								Event:
							</div>
							<div class="card-body">
								<h4 class="card-title">${fm.event.title}</h4>
								<p class="card-text">
									Time: <script> parseDate( '${fm.event.startTime}' ); </script> to <script> parseDate( '${fm.event.endTime}' ); </script>
								</p>
								<p class="card-text">Location: ${fm.event.location}</p>
								<p class="card-text">Description: ${fm.event.description}</p>
								<c:if test="${fm.event.active}">
									<c:choose>
										<c:when test="${fm.event.fee == 0}">
											<a id="payButton" href="#" class="btn btn-primary">Free - Get ticket</a>
											<input type="hidden" id="eventId" value="${fm.event.id}">
										</c:when>
										<c:otherwise>
											<a id="payButton" href="#" class="btn btn-primary">$${fm.event.fee} - Pay and get ticket</a>
											<input type="hidden" id="eventId" value="${fm.event.id}">
											<input type="hidden" id="tokenKey" value="<%=tokenKey %>">
										</c:otherwise>
									</c:choose>
									<input type="hidden" id="ticketFee" value="${fm.event.fee}">
								</c:if>
								<c:if test="${user != null}">
									<input type="hidden" id="userEmailAddr" value="${user.username}">
									<input type="hidden" id="userEmailConfirmed" value="${user.emailConfirmed}">
								</c:if>
								<p></p>
							</div>
						</div>
					</c:if>
				</c:otherwise>
			</c:choose>
		</div>
		<!-- /.container -->
		<c:if test="${user == null}">
			<%@include file="subview/formLogin.jsp" %>
		</c:if>
		<%@include file="subview/payment.jsp" %>
		<%@include file="subview/downloadTicket.jsp" %>
		<%@include file="subview/popup.jsp" %><!-- This must be placed below all other modals -->
		<%@include file="subview/footer.jsp" %>
		<script src="<%=BSjs %>"></script>
		<c:if test="${user == null || (user != null && !user.emailConfirmed)}">
			<script src="/resources/js/verifyEmail.js?v=3"></script>
		</c:if>
		<c:if test="${user == null}">
			<input type="hidden" id="refreshAfterLogin" value="true">
			<script src="/resources/js/formLogin.js?v=3"></script>
		</c:if>
		<script src="/resources/js/payment.js?v=3"></script>
	</body>
</html>