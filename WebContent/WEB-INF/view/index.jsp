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

    <title>WPI CSA - Life</title>
	
    <link href="<%=BScss %>" rel="stylesheet">
    <script src="<%=JQjs %>"></script>
    
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css" rel="stylesheet">

    <link href="/resources/css/main.css?v=1" rel="stylesheet">
	
	<script src="/resources/js/common.js?v=1"></script>

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
                        <a class="nav-link" href="#">Life</a>
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

        <!-- Jumbotron Header -->
        <header class="jumbotron my-4">
            <h1 class="display-3">A Warm Welcome!</h1>
            <p class="lead">This site is created as web version of WPI CSA mobile apps. It has limited features for now comparing with mobile apps.</p>
			<p class="lead">If you see any errors & bugs, don't forget to report to CSA!</p>
        </header>
        
        <c:forEach items="${feedList}" var="fm">
        <div class="card top-buffer">
			<div class="card-header">
				<h1><a href="./feed?id=${fm.feed.id}">${fm.feed.title}</a></h1>
			</div>
			<div class="row">
				<div class="col-lg-6 col-sm-12">
					<div class="life-img-container">
						<img class="aspect-fill" src="./images/${fm.coverImageId}.jpg" />
					</div>
				</div>
				<div class="col-lg-6 clm-sm-12">
					<div class="card-body life-desc">${fm.feed.body}</div>
				</div>
			</div>
		</div>
        
      	</c:forEach>
		

    </div>
    <!-- /.container -->

    <c:if test="${user == null}">
    <%@include file="subview/formLogin.jsp" %>
    </c:if>
    
    <%@include file="subview/popup.jsp" %><!-- This must be placed below all other modals -->

    <%@include file="subview/footer.jsp" %>

    <script src="<%=BSjs %>"></script>
    
    <c:if test="${user == null || (user != null && !user.emailConfirmed)}">
    <script src="/resources/js/verifyEmail.js?v=1"></script>
    </c:if>
    
    <c:if test="${user == null}">
    <script src="/resources/js/formLogin.js?v=1"></script>
    </c:if>

</body>

</html>