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

    <link href="/resources/css/main.css?v=1" rel="stylesheet">
	
	<script src="/resources/js/common.js?v=1"></script>
	
	

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
		
		</c:otherwise>
		</c:choose>
		

        	<div class="row">
		
		
        		
        		

        	</div>
        	

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