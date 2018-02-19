<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import = "java.util.ResourceBundle" %>
<% ResourceBundle resource = ResourceBundle.getBundle("dataSource");
	String BScss=resource.getString("BScss");String BSjs=resource.getString("BSjs");String JQjs=resource.getString("JQjs");
	String Fjs=resource.getString("Fjs");%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="en">
	<head>
		<meta charset="utf-8">
		<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
		<meta name="description" content="">
		<meta name="author" content="">
		<c:choose>
			<c:when test="${editMode}">
				<title>WPI CSA - Edit Article</title>
			</c:when>
			<c:otherwise>
				<title>WPI CSA - New Article</title>
			</c:otherwise>
		</c:choose>
		<link href="<%=BScss %>" rel="stylesheet">
		<script src="<%=JQjs %>"></script>
		<link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css" rel="stylesheet">
		<link href="https://cdnjs.cloudflare.com/ajax/libs/froala-editor/2.5.1/css/froala_editor.pkgd.min.css" rel="stylesheet" type="text/css" />
		<link href="https://cdnjs.cloudflare.com/ajax/libs/froala-editor/2.5.1/css/froala_style.min.css" rel="stylesheet" type="text/css" />
		<link href="/resources/css/main.css?v=2" rel="stylesheet">
		<script src="/resources/js/common.js?v=2"></script>
		<script src="/resources/js/editor.js?v=2"></script>
		<script src="<%=Fjs %>"></script>
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
							<a class="nav-link" href="./sg">Survival Guide</a>
						</li>
						<%@include file="subview/navUserLoggedIn.jsp" %>
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
					<div class = row>
						<div class="col-lg-6 col-sm-12">
							<div class="card mt-3">
								<div class="card-header">
									<p class="lead editor-header"><font size="6">Location&emsp;</font>Where your article is placed</p>
								</div>
								<div class="card-body" style="min-height:300px">
									<div class="row">
										<p class="lead col-xs-3 ml-2">Create</p>
										<div class="dropdown col-xs-3">
											<button class="btn btn-outline-secondary dropdown-toggle ml-2" type="button" id="sgType" data-toggle="dropdown">Select one</button>
											<div class="dropdown-menu">
												<a class="dropdown-item" onclick="selectSgType(1);" href="#">an article</a>
												<a class="dropdown-item" onclick="selectSgType(2);" href="#">a menu item</a>
											</div>
										</div>
										
										<p class="lead col-xs-3 ml-2" id="menuCalledLbl" style="display:none;">called</p>
										<div class="form-group col-xs-3">
											<input type="text" class="form-control ml-2" placeholder="Menu name" id="menuNameIn" style="display:none;">
										</div>
										
									</div>
									<div class="row">
										<p class="lead ml-2">And place it</p>
										<div class="dropdown col-xs-3">
											<button class="btn btn-outline-secondary dropdown-toggle ml-2" type="button" id="sgRelLoc" data-toggle="dropdown">after</button>
											<div class="dropdown-menu">
												<a class="dropdown-item" onclick="selectRelLoc(1);" href="#">after</a>
												<a class="dropdown-item" onclick="selectRelLoc(2);" href="#">before</a>
											</div>
										</div>
										<button type="button" class="btn btn-outline-secondary ml-2 mb-2" onclick="toggleMenuList()">Select one</button>
									</div>
								</div>
							</div>
						</div>
						<div class="col-lg-6 col-sm-12">
							<div class="card mt-3">
								<div class="card-header">
									<p class="lead editor-header"><font size="6">Title&emsp;</font>Title and menu name</p>
								</div>
								<div style="min-height:300px">
									
								</div>
							</div>
						</div>
					</div>
					<div class = row>
						<div class="col">
							<div class="card mt-3">
								<div class="card-header">
									<p class="lead editor-header"><font size="6">Content&emsp;</font>Rich text editor. Press <i class="fa fa-expand" style="font-size:18px;"></i> to enter full screen mode.</p>
								</div>
								<div style="min-height:300px">
									<textarea></textarea>
									<div id="editorDefaultText" style="display:none">${fm.feed.body}</div>
									<input type="hidden" id="editorHTMLOption" value="${editorHTMLOption}">
								</div>
							</div>
						</div>
					</div>
					
					
				</c:otherwise>
			</c:choose>
		</div>
		<!-- /.container -->
		<div class="modal fade" id="locPickerModal" role="dialog">
			<div class="modal-dialog modal-dialog-centered">
				<!-- Modal content-->
				<div class="modal-content">
					<div class="modal-header">
						<h4 class="modal-title">Select where to place the new article</h4>
						<button type="button" class="close" data-dismiss="modal">&times;</button>
					</div>
					<div class="modal-body" id="menuList">
						${menuList}
					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
					</div>
				</div>
			</div>
		</div>

		<%@include file="subview/popup.jsp" %><!-- This must be placed below all other modals -->
		<%@include file="subview/footer.jsp" %>
		<script src="/resources/js/sgEditor.js?v=2"></script>
		<script src="<%=BSjs %>"></script>
	</body>
</html>