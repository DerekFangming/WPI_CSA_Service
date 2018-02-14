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
            <a class="navbar-brand" href="./">WPI CSA</a>
        </div>
    </nav>

    <!-- Page Content -->
    <div class="container">
		<div class="card mt-4" style="max-width:500px;margin:0 auto;">
		    <div class="card-header">
		        Report Problem
		    </div>
		    <div class="card-body">
				<form>
				    <div class="form-group">
				        <label for="recipient-name" class="col-form-label">Email:</label>
				        <input type="text" class="form-control" id="email" placeholder="Endter your email address">
				    </div>
				    <div class="form-group">
				        <label for="message-text" class="col-form-label">Message:</label>
				        <textarea class="form-control" style="min-height:300px;" placeholder="What can we help you?"></textarea>
				    </div>
				    <input type="hidden" id="veriToken" value="${veriToken}">
				</form>
				<button id="submitBtn" class="btn btn-primary">Submit</button>
		    </div>
		</div>
	    
		
        
        
    </div>
    <!-- /.container -->

    
    <%@include file="subview/popup.jsp" %><!-- This must be placed below all other modals -->

    <%@include file="subview/footer.jsp" %>

	<script src="<%=BSjs %>"></script>
	
	<script>
	$("#submitBtn").click(function(){
		showPopup('Done', 'We got your report and will get back to you ASAP. You will be redirected in 5 seconds.');
		window.setTimeout(function(){
			window.location.href = "<%=rootDir %>";
		}, 5000);

	    
	}); 
	</script>

</body>

</html>