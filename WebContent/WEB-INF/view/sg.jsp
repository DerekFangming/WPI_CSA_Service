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
		<title>WPI CSA - Survival Guide</title>
		<link href="<%=BScss %>" rel="stylesheet">
		<script src="<%=JQjs %>"></script>
		<link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css" rel="stylesheet">
		<link href="/resources/css/main.css?v=2" rel="stylesheet">
		<script src="/resources/js/common.js?v=2"></script>
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
						<li class="nav-item">
							<a class="nav-link" href="./">Life</a>
						</li>
						<li class="nav-item active">
							<a class="nav-link" href="#">Survival Guide</a>
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
			<div class="row mt-3">
				<div class="col-lg-4 col-sm-12">
					<div class="input-group">
						<input type="text" id="sgSearchInput" placeholder="Enter some keyword" class="form-control">
						<div class="input-group-append">
							<button class="btn btn-outline-secondary" type="button" id="searchSGBtn"><i class="fa fa-search"></i></button>
							<button class="btn btn-outline-secondary" type="button" id="createSGBtn"><i class="fa fa-plus"></i></button>
						</div>
					</div>
					<div class="sg-menu mt-3" id="sgMenuDiv">
						${menuList}
					</div>
				</div>
				<div class="col-lg-8 clm-sm-12">
					<div class="card">
						<div class="card-header">
							<h1 class="lead mb-0"><big><big id="sgTitle" style="display:none;"></big></big></h1>
							<div class="dropdown one-line" id="sgHistDropdown" style="display:none;">
								<button class="btn btn-light btn-sm dropdown-toggle" type="button" data-toggle="dropdown">
									<small class="text-muted" id="sgSubTitle"></small>
								</button>
								<div class="dropdown-menu">
									<a class="dropdown-item" href="#"><small class="text-muted">No more editing history found</small></a>
								</div>
							</div>
							<a role="button" id="sgEditBtn" class="btn btn-sm btn-outline-secondary ml-3" style="display:none;">Edit</a>
							<div id="sgLoading"><center><i class="fa fa-refresh fa-3x fa-spin" style="color:black"></i></center></div>
						</div>
					</div>
					<div class="card mt-3 sg-body">
						<div class="card-body" id="sgContent"></div>
					</div>
				</div>
			</div>
			<c:choose>
				<c:when test="${user != null}">
					<input type="hidden" id="userEmailConfirmed" value="${user.emailConfirmed}">
				</c:when>
				<c:otherwise>
					<input type="hidden" id="userEmailConfirmed" value="false">
				</c:otherwise>
			</c:choose>
			<input type="hidden" id="initialId" value="${initialId}">
		</div>
		<!-- /.container -->
		<c:if test="${user == null}">
			<%@include file="subview/formLogin.jsp" %>
		</c:if>
		<%@include file="subview/popup.jsp" %><!-- This must be placed below all other modals -->
		<%@include file="subview/footer.jsp" %>
		<script src="<%=BSjs %>"></script>
		<c:if test="${user == null || (user != null && !user.emailConfirmed)}">
			<script src="/resources/js/verifyEmail.js?v=2"></script>
		</c:if>
		<c:if test="${user == null}">
			<script src="/resources/js/formLogin.js?v=2"></script>
		</c:if>
		<script src="/resources/js/sg.js?v=2"></script>
		<script src="/resources/js/parser.js?v=2"></script>
	</body>
</html>