<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="en">

<head>

    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <meta name="description" content="">
    <meta name="author" content="">

    <title>WPI CSA - Life</title>

    <!--<link href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0-beta.2/css/bootstrap.min.css" rel="stylesheet">-->
    <link href="/resources/css/local/bootstrap.min.css" rel="stylesheet">
    
    <link href="//netdna.bootstrapcdn.com/font-awesome/3.2.1/css/font-awesome.css" rel="stylesheet">

    <link href="/resources/css/main.css" rel="stylesheet">
	
    <!--<script src="https://code.jquery.com/jquery-3.2.1.min.js"></script>-->
	<script src="/resources/css/local/jquery-3.2.1.min.js"></script>
	
	<script src="/resources/js/common.js"></script>

</head>

<body>
    <!-- Navigation -->
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark fixed-top">
        <div class="container">
            <a class="navbar-brand" href="./">WPI CSA</a>
            <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarResponsive" aria-controls="navbarResponsive" aria-expanded="false" aria-label="Toggle navigation">
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
                    <c:if test="${loggedIn}">
    					<%@include file="subview/navUserLoggedIn.jsp" %>
    					</c:if>
    					<c:if test="${!loggedIn}">
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
		
		<div class="card top-buffer">
			<div class="card-header">
				<h1><a href="https://www.google.com">Some title up here</a></h1>
			</div>
			<div class="row">
				<div class="col">
					<div class="feed-image">
						<img class="tall" src="/Volumes/Data/testImages/50.jpg" />
					</div>
				</div>
				<div class="col">
					<p class="lead top-buffer">This site is created as web version of WPI CSA mobile apps. It has limited features for now comparing with mobile apps.</p>
				</div>
			</div>
		</div>
		
		
		<div class="card top-buffer">
			<div class="card-header">
				<h1>Some title up here</h1>
			</div>
			<div class="row">
				<div class="col">
					<div class="feed-image">
						<img class="tall" src="http://placehold.it/800x400" />
					</div>
				</div>
				<div class="col">
					<p class="lead">This site is created as web version of WPI CSA mobile apps. It has limited features for now comparing with mobile apps.</p>
				</div>
			</div>
		</div>
		
		<div class="card top-buffer">
			<div class="row">
				<div class="col">
					<h1>Some title up here</h1>
				</div>
			</div>
			<div class="row">
				<div class="col">
					<img class = "center-block" src="http://placehold.it/500x300"  />
				</div>
				<div class="col">
					<p class="lead">This site is created as web version of WPI CSA mobile apps. It has limited features for now comparing with mobile apps.</p>
				</div>
			</div>
		</div>

        <!-- Page Features -->
        
        <!-- /.row -->

    </div>
    <!-- /.container -->

    <c:if test="${!loggedIn}">
    <%@include file="subview/formLogin.jsp" %>
    </c:if>
    
    <%@include file="subview/popup.jsp" %><!-- This must be placed below all other modals -->

    <%@include file="subview/footer.jsp" %>

    <!--<script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0-beta.2/js/bootstrap.bundle.min.js"></script>-->
    <script src="/resources/css/local//bootstrap.bundle.min.js"></script>
    
    <c:if test="${!loggedIn || (loggedIn && !user.emailConfirmed)}">
    <script src="/resources/js/verifyEmail.js"></script>
    </c:if>
    
    <c:if test="${!loggedIn}">
    <script src="/resources/js/formLogin.js"></script>
    </c:if>

</body>

</html>