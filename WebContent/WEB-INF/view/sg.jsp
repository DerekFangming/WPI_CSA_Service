<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="en">

<head>

    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <meta name="description" content="">
    <meta name="author" content="">

    <title>WPI CSA - Survival Guide</title>
	
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
    
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css" rel="stylesheet">

    <link href="/resources/css/main.css" rel="stylesheet">
	
	<script src="/resources/js/common.js"></script>

</head>

<body>
    <!-- Navigation -->
    <nav class="navbar navbar-toggleable-md navbar-inverse bg-inverse fixed-top">
        <div class="container">
            <a class="navbar-brand" href="./">WPI CSA</a>
            <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarResponsive" aria-controls="navbarResponsive" aria-expanded="false" aria-label="Toggle navigation">
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

        <!-- Jumbotron Header
        <header class="jumbotron my-4">
            <center><h1 class="display-3">Survival Guide</h1></center>
            <center><p class="lead">Coming soon</p></center>
        </header>-->
        
        <div class="row top-buffer ">
	    		<div class="col-lg-6 col-sm-12">
	    			<div class="sg-height">
				${menuList}
				</div>
			</div>
			<div class="col-lg-6 clm-sm-12">
				<p class="lead feed-desc">Something</p>
			</div>
	    </div>
        
    </div>
    
    
    
    <!-- /.container -->

    <c:if test="${user == null}">
    <%@include file="subview/formLogin.jsp" %>
    	</c:if>
    
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
    <script src="/resources/js/formLogin.js"></script>
    </c:if>

</body>

</html>