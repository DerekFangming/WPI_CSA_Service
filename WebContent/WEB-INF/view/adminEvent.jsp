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
	
    <c:choose>
	<c:when test="${prodMode}">
    <link href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css" rel="stylesheet">
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
    <nav class="navbar navbar-expand-md navbar-dark bg-primary fixed-top">
        <div class="container">
            <a class="navbar-brand" href="../../">WPI CSA Admin</a>
            <button class="navbar-toggler navbar-toggler-right" type="button" data-toggle="collapse" data-target="#navbarResponsive" aria-controls="navbarResponsive" aria-expanded="false" aria-label="Toggle navigation">
          		<span class="navbar-toggler-icon"></span>
        		</button>
            <div class="collapse navbar-collapse" id="navbarResponsive">
                <ul class="navbar-nav ml-auto">
                    <li class="nav-item active">
                        <a class="nav-link" href="#">Event</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="./user">User</a>
                    </li>
                    <li class="nav-item dropdown">
					    <a class="nav-link dropdown-toggle" href="#" id="navbarDropdownMenuLink" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
					    	${user.name}
				        </a>
				        <div class="dropdown-menu" aria-labelledby="navbarDropdownMenuLink">
						    <a class="dropdown-item" href="../../"><i class="fa fa-chevron-circle-left" style="color:green"></i>&nbsp;Back to main site</a>
						    <a class="dropdown-item" href="../../logout"><i class="fa fa-sign-out"></i>&nbsp;Log out</a>
						</div>
					</li>
                </ul>
            </div>
        </div>
    </nav>

    <!-- Page Content -->
    <div class="container">

        	<div class="row top-buffer">
        		<div class="col-md-3">
	        		<div class="card" style="width: 18rem;">
				    <img class="card-img-top" src="../../images/53.jpg" alt="Card image cap">
				    <div class="card-body card-body-bottom-line">
				        <h5 class="card-title">Dragon night 2018</h5>
				        <p class="card-text">Status: Selling</p>
				        <p class="card-text">Ticket told: 35</p>
				        <p class="card-text">Ticket remaining: 25</p>
				    </div>
				    <div class="card-body card-body-bottom-line text-center">
				        <button href="#" type="button" class="btn btn-primary" data-toggle="tooltip-list">
							<i class="fa fa-users"></i>&nbsp;Participant list
						</button>
				    </div>
				    <div class="card-body card-body-bottom-line input-group">
				    		<input type="text" class="form-control" aria-label="Small" aria-describedby="inputGroup-sizing-sm">
				    		<span class="input-group-btn">
				        <button href="#" type="button" class="btn btn-primary" data-toggle="tooltip-remain">
							<i class="fa fa-users"></i>&nbsp;Set remaining #
						</button>
						</span>
				    </div>
				    <ul class="list-group list-group-flush">
				        
				        <li class="list-group-item">Dapibus ac facilisis in</li>
				        <li class="list-group-item">Vestibulum at eros</li>
				    </ul>
				    
				</div>
        		</div>
        	</div>
        
        <!--<c:forEach items="${feedList}" var="feed">
        <div class="card top-buffer">
			<div class="card-header">
				<h1><a href="./feed?id=${feed.id}">${feed.title}</a></h1>
			</div>
			<div class="row">
				<div class="col-lg-6 col-sm-12">
					<div class="feed-image">
						<img class="img-fitwidth" src="./images/${feed.coverImageId}.jpg" />
					</div>
				</div>
				<div class="col-lg-6 clm-sm-12">
					<p class="lead feed-desc">${feed.body}</p>
				</div>
			</div>
		</div>
        
      	</c:forEach>-->
		

    </div>
    <!-- /.container -->

    
    <%@include file="subview/popup.jsp" %><!-- This must be placed below all other modals -->

    <%@include file="subview/footer.jsp" %>

    <c:choose>
	<c:when test="${prodMode}">
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.bundle.min.js"></script>
	</c:when>
	<c:otherwise>
    <script src="/resources/css/local/bootstrap.bundle.min.js"></script>
	</c:otherwise>
	</c:choose>
	
	<script>
        $(function () {
            $('[data-toggle="tooltip-list"]').tooltip({
			    placement: "bottom",
			    title: "View who has registerred for this event."
			});
			$('[data-toggle="tooltip-remain"]').tooltip({
			    placement: "bottom",
			    title: "Set the remaining ticket count that can be sold electronically. If set to 0, user will see message saying Ticket Sold Out when they try to buy ticket."
			});
        });
    </script>

</body>

</html>