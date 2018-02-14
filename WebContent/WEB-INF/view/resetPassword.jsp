<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import = "java.util.ResourceBundle" %>
<% ResourceBundle resource = ResourceBundle.getBundle("dataSource");
  String BScss=resource.getString("BScss");String BSjs=resource.getString("BSjs");String JQjs=resource.getString("JQjs"); String rootDir=resource.getString("rootDir"); %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="en">

<head>

    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <meta name="description" content="">
    <meta name="author" content="">

    <title>WPI CSA</title>
    
	<link href="<%=BScss %>" rel="stylesheet">
	<script src="<%=JQjs %>"></script>
    
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css" rel="stylesheet">
	
	<script src="/resources/js/common.js?v=2"></script>
	
    <link href="/resources/css/main.css" rel="stylesheet">

</head>

<body>
    <!-- Navigation -->
    <nav class="navbar navbar-expand-md navbar-dark bg-dark fixed-top">
        <div class="container">
            <a class="navbar-brand" href="../">WPI CSA</a>
        </div>
    </nav>

    <!-- Page Content -->
    <div class="container">
    
    	<c:choose>
		<c:when test="${changePwd}">
		<div class="card mt-3 mb-1" style="max-width:500px;margin:0 auto;">
		    <div class="card-header">
		        Reset password
		    </div>
		    <div class="card-body">
				<form>
				    <div class="form-group">
				        <label for="recipient-name" class="col-form-label">Password:</label>
				        <input type="password" class="form-control" id="newPassword" placeholder="At least 6 characters with letters and numbers">
				    </div>
				    <div class="form-group">
				        <label for="message-text" class="col-form-label">Reenter password:</label>
				        <input type="password" class="form-control" id="confirmPassword" placeholder="Enter your password again">
				    </div>
				    <input type="hidden" id="veriToken" value="${veriToken}">
				</form>
				<button id="resetPwdBtn" class="btn btn-primary">Submit</button>
		    </div>
		</div>
	    
		</c:when>
		<c:otherwise>
	    <header class="jumbotron my-4">
            <center><h1 class="display-4">Something goes wrong</h1></center>
            <br><br><br>
            <center><p class="lead">${msg}</p></center>
            <br>
            <center><a class="btn btn-primary" href="<%=rootDir %>" role="button">Take me back to main page</a></center>
        </header>
		</c:otherwise>
		</c:choose>

        
        
    </div>
    <!-- /.container -->

    
    <%@include file="subview/popup.jsp" %><!-- This must be placed below all other modals -->

    <%@include file="subview/footer.jsp" %>

	<script src="<%=BSjs %>"></script>
	
	<script>
	$("#resetPwdBtn").click(function(){
	    var accessToken = getAccessToken();
	    var passwordRegex = /([0-9].*[a-zA-Z])|([a-zA-Z].*[0-9])/;
	    var password = $("#newPassword").val().trim();
		var confirm = $("#confirmPassword").val().trim();
	    
		if (password != confirm) {
			showErrorPopup('The two passwords you entered are not the same');
		} else if (password.length < 6) {
			showErrorPopup('Password needs to have more than 6 characters');
		} else if (!passwordRegex.test(password)) {
			showErrorPopup('Password needs to have at least one letter and one number');
		} else {
			startBtnLoading('#resetPwdBtn');
		    
		    $.ajax({
		        type: "POST",
		        url: "../../update_password",
		        data: JSON.stringify({accessToken : accessToken, newPwd : password, veriToken : $('#veriToken').val()}),
		        contentType: "application/json",
		        dataType: "json",
		        success: function(data){
		        	stopBtnLoading('#resetPwdBtn');
					if (data['error'] == "" ) {
						showPopup('Done', 'Your password change is done. You will be redirected in 5 seconds.');
						window.setTimeout(function(){
							window.location.href = "<%=rootDir %>";
						}, 5000);

					} else {
						showErrorPopup(data['error']);
					}
		        },
		        failure: function(errMsg) {
		        	stopBtnLoading('#resetPwdBtn');
		        	showErrorPopup('Unknown error occured. Please contact support');
		        }
		    });
		}
	    
	}); 
	</script>

</body>

</html>