<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="en">

<head>

    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <meta name="description" content="">
    <meta name="author" content="">

    <title>WPI CSA - Feed</title>

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
                        <a class="nav-link" href="./">Life</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="#">Survival Guide</a>
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
            <center><h1 class="display-4">${feed.title}</h1></center>
        </header>
        
        <div class="card top-buffer">
			<div class="card-header">
				${feed.body}
			</div>
		</div>
		
		<c:if test="${event != null}">
    	<div class="card top-buffer">
			<div class="card-header">
				Event:
			</div>
			<div class="card-block">
    			<h4 class="card-title left-buffer top-buffer">${event.title}</h4>
    			<p class="card-text left-buffer">Time: <script> parseDate( '${event.startTime}' ); </script> to <script> parseDate( '${event.endTime}' ); </script></p>
    			<p class="card-text left-buffer">Location: ${event.description}</p>
    			<c:choose>
  				<c:when test="${event.fee == -1}">
  				</c:when>
  				<c:when test="${event.fee == 0}">
  				<a href="#" class="btn btn-primary left-buffer">Free - Get ticket</a>
  				<input type="hidden" id="eventId" value="${event.id}">
  				</c:when>
  				<c:otherwise>
  				<a href="#" class="btn btn-primary left-buffer">$${event.fee} - Pay and get ticket</a>
  				</c:otherwise>
				</c:choose>
    			
    			<p></p>
  			</div>
		</div>
    	</c:if>
        
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