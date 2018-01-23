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
    
	<c:choose>
	<c:when test="${prodMode}">
    <link href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0-alpha.6/css/bootstrap.min.css" rel="stylesheet">
    <script src="https://code.jquery.com/jquery-3.2.1.min.js"></script>
	</c:when>
	<c:otherwise>
    <link href="/resources/css/local/bootstrap.min.css" rel="stylesheet">
    <script src="/resources/css/local/jquery-3.2.1.min.js"></script>
	</c:otherwise>
	</c:choose>
	
	<script src="https://js.braintreegateway.com/web/dropin/1.9.2/js/dropin.min.js"></script>
    
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css" rel="stylesheet">

    <link href="/resources/css/main.css" rel="stylesheet">
	
	<script src="/resources/js/common.js"></script>

</head>

<body>
    <!-- Navigation -->
    <nav class="navbar navbar-toggleable-md navbar-inverse bg-inverse fixed-top">
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
    			<h4 class="card-title">${event.title}</h4>
    			<p class="card-text">Time: <script> parseDate( '${event.startTime}' ); </script> to <script> parseDate( '${event.endTime}' ); </script></p>
    			<p class="card-text">Location: ${event.location}</p>
    			<p class="card-text">Description: ${event.description}</p>
    			<c:choose>
  				<c:when test="${event.fee == -1}">
  				</c:when>
  				<c:when test="${event.fee == 0}">
  				<a id="payButton" href="#" class="btn btn-primary"><i id="ticketSpinner" class=""></i>&nbsp;Free - Get ticket</a>
  				<input type="hidden" id="eventId" value="${event.id}">
  				</c:when>
  				<c:otherwise>
  				<a id="payButton" href="#" class="btn btn-primary"><i id="ticketSpinner" class=""></i>&nbsp;$${event.fee} - Pay and get ticket</a>
  				<input type="hidden" id="eventId" value="${event.id}">
  				</c:otherwise>
				</c:choose>
    			<input type="hidden" id="ticketFee" value="${event.fee}">
				
				<c:if test="${user != null}">
    			<input type="hidden" id="userEmailAddr" value="${user.username}">
    			<input type="hidden" id="userEmailConfirmed" value="${user.emailConfirmed}">
    			</c:if>
    			
    			<p></p>
  			</div>
		</div>
    	</c:if>
        
    </div>
    <!-- /.container -->

    <c:if test="${user == null}">
    <%@include file="subview/formLogin.jsp" %>
    </c:if>
    
    <%@include file="subview/payment.jsp" %>
    
    <%@include file="subview/downloadTicket.jsp" %>
    
    <%@include file="subview/popup.jsp" %><!-- This must be placed below all other modals -->

    <%@include file="subview/footer.jsp" %>

	<c:choose>
	<c:when test="${prodMode}">
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0-beta.2/js/bootstrap.bundle.min.js"></script>
	</c:when>
	<c:otherwise>
    <script src="/resources/css/local/bootstrap.bundle.min.js"></script>
	</c:otherwise>
	</c:choose>
    
    <c:if test="${user == null || (user != null && !user.emailConfirmed)}">
    <script src="/resources/js/verifyEmail.js"></script>
    </c:if>
    
    <c:if test="${user == null}">
    <input type="hidden" id="refreshAfterLogin" value="true">
    <script src="/resources/js/formLogin.js"></script>
    </c:if>
    
    <script src="/resources/js/payment.js"></script>

</body>

</html>