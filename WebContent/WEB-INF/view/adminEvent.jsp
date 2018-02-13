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

    <link href="/resources/css/main.css?v=2" rel="stylesheet">
	
	<script src="/resources/js/common.js?v=2"></script>
	
	

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
                    <li class="nav-item">
                        <a class="nav-link" href="./help">Help</a>
                    </li>
                    <li class="nav-item dropdown">
					    <a class="nav-link dropdown-toggle" href="#" id="navbarDropdownMenuLink" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
					    	${user.name}
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
        		
        		<c:forEach items="${eventList}" var="em">
	        <div class="col-lg-4 col-md-6 col-sm-12 mt-3">
	        		<div class="card card-event">
	        			<div class="card-img-top event-img-container">
					    <img class="aspect-fill" src="../../images/${em.coverImageId}.jpg">
				    </div>
				    <div class="card-body border-bottom">
				        <h5 id="titleLbl${em.event.id}" class="card-title">${em.event.title}</h5>
				        <p id="statusLbl${em.event.id}" class="card-text">Status: ${em.event.active ? "Selling" : "Sold out"}</p>
				        <p id="balanceLbl${em.event.id}" class="card-text">Ticket:  Sold ${em.registedUserCount}, remaining ${em.event.ticketBalance}</p>
				    </div>
					<div class="card-body border-bottom text-center">
					    	<button onclick="openEvent(${em.event.id});" type="button" class="btn btn-primary" data-toggle="tooltip-edit">
							<i class="fa fa-pencil-square-o"></i>&nbsp;Edit
						</button>
				        <button onclick="openPartiList(${em.event.id});" type="button" class="btn btn-primary" data-toggle="tooltip-list">
							<i class="fa fa-list-ul"></i>&nbsp;View  Participants
						</button>
					    </div>
					    <div class="card-body border-bottom input-group">
				    		<input id="balanceIn${em.event.id}" onkeypress="return event.charCode >= 48 && event.charCode <= 57" class="form-control" aria-label="Small" aria-describedby="inputGroup-sizing-sm">
				    		<span class="input-group-btn">
					        <button id="balanceBtn${em.event.id}" onclick="setBalance(${em.event.id});" type="button" class="btn btn-primary" data-toggle="tooltip-remain">
								<i class="fa fa-ticket"></i>&nbsp;Set  Ticket  Balance
							</button>
							</span>
					    </div>
					    <div class="card-body border-bottom text-center">
				    		<c:choose>
						<c:when test="${em.event.active}">
					    		<button id="statusBtn${em.event.id}" onclick="toggleStatus(${em.event.id}, false);" type="button" class="btn btn-danger" data-toggle="tooltip-soldout">
								<i class="fa fa-lock"></i>&nbsp;Mark  as  Sold  Out
							</button>
						</c:when>
						<c:otherwise>
					    		<button id="statusBtn${em.event.id}" onclick="toggleStatus(${em.event.id}, true);" type="button" class="btn btn-success" data-toggle="tooltip-selling">
								<i class="fa fa-unlock"></i>&nbsp;Start selling tickets
							</button>
						</c:otherwise>
						</c:choose>
					    </div>
				    
				</div>
        		</div>
	        
	      	</c:forEach>

        	</div>
        	
        	<div style="height:110px"></div>
        

		

    </div>
    <!-- /.container -->

	<%@include file="subview/adminEventPopup.jsp" %>
    
    <%@include file="subview/popup.jsp" %><!-- This must be placed below all other modals -->

    <%@include file="subview/footer.jsp" %>

    <script src="<%=BSjs %>"></script>
	
	<script>
        $(function () {
            $('[data-toggle="tooltip-edit"]').tooltip({
			    placement: "bottom",
			    title: "Edit the title, location or time of the event."
			});
            $('[data-toggle="tooltip-list"]').tooltip({
			    placement: "bottom",
			    title: "View who has registered for this event."
			});
			$('[data-toggle="tooltip-remain"]').tooltip({
			    placement: "bottom",
			    title: "Set the remaining ticket count that can be sold electronically. If set to 0, the button will still be there but user will see message saying Ticket Sold Out when they try to click."
			});
			$('[data-toggle="tooltip-soldout"]').tooltip({
			    placement: "bottom",
			    title: "Mark the ticket as sold out. This will remove the get ticket button regardless of ticket remaining counts."
			});
			$('[data-toggle="tooltip-selling"]').tooltip({
			    placement: "bottom",
			    title: "Start selling ticket. Users will see the get ticket button under this event."
			});
        });
    </script>
    
    <script src="/resources/js/adminEvent.js?v=2"></script>
    
    
    <script src="<%=DTJjs %>"></script>
    <script src="<%=DTBjs %>"></script>

</body>

</html>