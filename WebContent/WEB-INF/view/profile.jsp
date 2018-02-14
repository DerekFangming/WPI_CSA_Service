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

    <title>WPI CSA - Profile</title>
	
    <link href="<%=BScss %>" rel="stylesheet">
    <script src="<%=JQjs %>"></script>
    
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css" rel="stylesheet">

    <link href="/resources/css/main.css?v=2" rel="stylesheet">
	<script src="/resources/js/imagePicker.js?v=2"></script>
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
                    <li class="nav-item">
                        <a class="nav-link" href="./sg">Survival Guide</a>
                    </li>
                    <%@include file="subview/navUserLoggedIn.jsp" %>
                </ul>
            </div>
        </div>
    </nav>

    <!-- Page Content -->
    <div class="container">
        
        <div class="row">
        		<div class="col-lg-4 col-md-6 col-sm-12 mt-3">
        			<div class="card">
				    <div class="card-header">
				        Account & profile
				    </div>
				    <div class="card-body">
				    	<label class="lead">Avatar:</label>
				    	<!--  <div id="img-picker" class="aspect-fill" ondragover="allowDrop(event)"></div>-->
				    	<c:choose>
					    <c:when test="${um.userAvatarId == 0}">
					    <div class="life-img-container border" id="userAvatar">
					    		<img class="aspect-fill" src="/resources/img/defaultAvatar.png">
					    </div>
					    </c:when>
					    <c:otherwise>
					    <div class="life-img-container border" id="userAvatar">
					    		<img class="aspect-fill" src="./images/${um.userAvatarId}.jpg">
					    </div>
					    </c:otherwise>
						</c:choose>
						
						<label class="lead mt-3">Account:&nbsp;
							<c:choose>
						    <c:when test="${user.emailConfirmed}">
						    	<span class="badge badge-success"><small>Confirmed</small></span>
						    </c:when>
						    <c:otherwise>
						    	<span class="badge badge-danger"><small>Not confirmed</small></span>
						    </c:otherwise>
							</c:choose>
							</label>
						<div class="input-group">
						    <div class="input-group-prepend"><span class="input-group-text profile-lable">Account</span></div>
							    <input type="text" class="form-control bg-white" placeholder="Your name" value="${user.username}" disabled>
							    <c:if test="${!user.emailConfirmed}">
							    <div class="input-group-append">
								    <button class="btn btn-outline-secondary" type="button" onclick="sendVerificationEmail(event, this)">Re-send</button>
								</div>
							    </c:if>
							    
							</div>
					    	
					    	<label class="lead mt-3">Profile details:</label>
					    	<div class="input-group">
						    <div class="input-group-prepend"><span class="input-group-text profile-lable">Name</span></div>
						    <input type="text" class="form-control bg-white" id="userName" placeholder="Your name" value="${um.userName}" maxlength="20" disabled>
						</div>
						<div class="input-group mt-2">
						    <div class="input-group-prepend"><span class="input-group-text profile-lable">Birthday</span></div>
						    <input type="date" class="form-control bg-white" id="userBirthday" placeholder="Fromat MM/DD/YY" value="${um.userBirthday}" disabled>
						</div>
						<div class="input-group mt-2">
						    <div class="input-group-prepend"><span class="input-group-text profile-lable">Class of</span></div>
						    <input type="text" class="form-control bg-white" id="userClassof" placeholder="Graduation year, like 2020" value="${um.userClassof}" onkeypress="validateInputNumber(event)" maxlength="4" disabled>
						</div>
						<div class="input-group mt-2">
						    <div class="input-group-prepend"><span class="input-group-text profile-lable">Major</span></div>
						    <input type="text" class="form-control bg-white" id="userMajor" placeholder="Abbreviation of your major" value="${um.userMajor}" maxlength="10" disabled>
						</div>
						
						<button class="btn btn-success mt-3" type="button" id="profileSaveBtn" style="display:none;">Save</button>
						<div class="mt-3" style="height:38px;float:left;"></div>
						<button class="btn btn-secondary float-right mt-3" type="button" id="profileCancelBtn" style="display:none;">Cancel</button>
						<button class="btn btn-outline-primary float-right mt-3" type="button" id="profileEditBtn">Edit Profile</button>
						
						<input type="hidden" id="userOrigAvatarId" value="${um.userAvatarId}">
						<input type="hidden" id="userOrigName" value="${um.userName}">
						<input type="hidden" id="userOrigBirthday" value="${um.userBirthday}">
						<input type="hidden" id="userOrigClassof" value="${um.userClassof}">
						<input type="hidden" id="userOrigMajor" value="${um.userMajor}">
				    </div>
				</div>
        		</div>
        		<div class="col-lg-8 col-md-6 col-sm-12 mt-3">
        			<div class="card">
				    <div class="card-header">
				        Posted articles
				    </div>
				    <div class="card-body">
				    		
				    </div>
				</div>
				<div class="card mt-3">
				    <div class="card-header">
				        Edited Survival Guide articles
				    </div>
				    <div class="card-body">
				    </div>
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
		

    </div>
    <!-- /.container -->


    <%@include file="subview/popup.jsp" %><!-- This must be placed below all other modals -->

    <%@include file="subview/footer.jsp" %>

    <script src="<%=BSjs %>"></script>
    
    <c:if test="${!user.emailConfirmed}">
    <script src="/resources/js/verifyEmail.js?v=2"></script>
    </c:if>
    <script src="/resources/js/profile.js?v=2"></script>
</body>
</html>