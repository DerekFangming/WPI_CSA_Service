<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import = "java.util.ResourceBundle" %>
<% ResourceBundle resource = ResourceBundle.getBundle("dataSource");
  String BScss=resource.getString("BScss");String BSjs=resource.getString("BSjs");String JQjs=resource.getString("JQjs");
  String DTcss=resource.getString("DTcss");String DTJjs=resource.getString("DTJjs");String DTBjs=resource.getString("DTBjs"); %>
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
    
    <link href="<%=DTcss %>" rel="stylesheet">

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
                    <li class="nav-item active">
                        <a class="nav-link" href="./user">User</a>
                    </li>
                    <li class="nav-item dropdown">
					    <a class="nav-link dropdown-toggle" href="#" id="navbarDropdownMenuLink" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
					    	${currentUser.name}
				        </a>
				        <div class="dropdown-menu" aria-labelledby="navbarDropdownMenuLink">
						    <a class="dropdown-item" href="../../"><i class="fa fa-chevron-circle-left"></i>&nbsp;Back to main site</a>
						    <a class="dropdown-item" href="../../logout"><i class="fa fa-sign-out"></i>&nbsp;Log out</a>
						</div>
					</li>
                </ul>
            </div>
        </div>
    </nav>

    <!-- Page Content -->
    <div class="container">
    
    	<div class="row">
	    	<div class="col-lg-8 col-sm-12 top-buffer">
	    		<table id="userTable" class="table table-striped table-bordered table-hover" cellspacing="0" width="100%">
				    <thead>
				        <tr>
				        	<th style="display:none;">ID</th>
				            <th>Name</th>
				            <th>Email</th>
				            <th>Confirmed</th>
				            <th>Role</th>
				            <th style="display:none;">Joined</th>
				        </tr>
				    </thead>
				    <tbody>
				        <c:forEach items="${userList}" var="user">
				            <tr>
				            	<th style="display:none;">${user.id}</th>
				                <th>${user.name}</th>
				                <th>${user.username}</th>
				                <c:choose>
								<c:when test="${user.emailConfirmed}">
							    <th><i class="fa fa-check" style="color:green"></i><p style="display:none;">1</p></th>
								</c:when>
								<c:otherwise>
							    <th><i class="fa fa-times" style="color:red"></i><p style="display:none;">0</p></th>
								</c:otherwise>
								</c:choose>
								
								<c:choose>
								<c:when test="${user.roleId == 1}">
							    <th><p style="display:none;">1</p>System Admin</th>
								</c:when>
								<c:when test="${user.roleId == 2}">
							    <th>Site Admin</th>
								</c:when>
								<c:otherwise>
							    <th>User</th>
								</c:otherwise>
								</c:choose>
								
								<th style="display:none;">${user.createdAt}</th>
				            </tr>
				        </c:forEach>
				    </tbody>
				</table>
			</div>
			<div class="col-lg-4 col-sm-12 top-buffer">
	    		<div class="card">
				    <h5 class="card-header">User details</h5>
				    <div class="card-body">
				    	<div class="admin-user-img-container">
				    		<img class="aspect-fill" src="https://ae01.alicdn.com/kf/HTB1vzC9MVXXXXaPXVXXq6xXFXXXl/13-1-11-7CM-Adorable-Cartoon-Panda-Head-Vinyl-Car-Stickers-Cute-Tail-Car-Styling-Decal.jpg_640x640.jpg">
				    	</div>
				        <h5 class="card-title"><i class="fa fa-user"></i> <span id="userDispName">Name</span></h5>
				        <p class="card-text"><i class="fa fa-envelope"></i> <span id="userUsername">Email</span></p>
				        <p class="card-text"><i class="fa fa-calendar"></i> <span id="userCreated">Member since</span></p>
				        <input type="hidden" id="selectedUserId" value="0">
				        <input type="hidden" id="selectedUserRoleId" value="0">
				        <button id="resendEmailConfirmBtn" class="btn btn-primary" data-toggle="tooltip-email">Re-send confirm account email</button>
				        <br><br>
				        <button id="sendResetPasswordBtn" class="btn btn-primary" data-toggle="tooltip-pwd">Send password reset email</button>
				        
				        <div class="top-buffer card-body-bottom-line"></div>
				        <input type="hidden" id="currentUserID" value="${currentUser.id}">
				        <input type="hidden" id="currentUserRoleID" value="${currentUser.roleId}">
				        <div class="dropdown top-buffer">
							<button class="btn btn-primary dropdown-toggle" type="button" id="currentRole" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
								User Role
							</button>
						    <div class="dropdown-menu" aria-labelledby="currentRole">
						    		<c:if test="${currentUser.roleId == 1}">
		    						<a class="dropdown-item" href="#" onclick="selectRole(1);">System Admin</a>
		    						</c:if>
						        <a class="dropdown-item" href="#" onclick="selectRole(2);">Site Admin</a>
						        <a class="dropdown-item" href="#" onclick="selectRole(3);">User</a>
						    </div>
						    <button id="setUserRoleBtn" class="btn btn-primary" style="margin-left:20px" data-toggle="tooltip-role">Save user role</button>
						</div>
						
						
						
				    </div>
				</div>
			</div>
			
    	</div>    	

    </div>
    <!-- /.container -->
    
    <%@include file="subview/popup.jsp" %><!-- This must be placed below all other modals -->

    <%@include file="subview/footer.jsp" %>

    <script src="<%=BSjs %>"></script>
	
	<script>
        $(function () {
            $('[data-toggle="tooltip-email"]').tooltip({
			    placement: "bottom",
			    title: "Send an email to the selected user to confirm email address."
			});
            $('[data-toggle="tooltip-pwd"]').tooltip({
			    placement: "bottom",
			    title: "Send an email to the selected user to reset password. Note that user has to confirm email first before resetting password."
			});
            $('[data-toggle="tooltip-role"]').tooltip({
			    placement: "bottom",
			    title: "Update the role of the selected user. Admins will have access to this portal. An email will be sent to the user if role changed."
			});
        });
    </script>
    
    <script src="/resources/js/adminUser.js?v=1"></script>
    
    
    <script src="<%=DTJjs %>"></script>
    <script src="<%=DTBjs %>"></script>

</body>

</html>