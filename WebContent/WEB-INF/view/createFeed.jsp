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

    <title>WPI CSA - New Article</title>
	
    <link href="<%=BScss %>" rel="stylesheet">
    <script src="<%=JQjs %>"></script>
    
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/froala-editor/2.5.1/css/froala_editor.pkgd.min.css" rel="stylesheet" type="text/css" />
	<link href="https://cdnjs.cloudflare.com/ajax/libs/froala-editor/2.5.1/css/froala_style.min.css" rel="stylesheet" type="text/css" />

    <link href="/resources/css/main.css?v=2" rel="stylesheet">
	
	<script src="/resources/js/common.js?v=2"></script>
	<script src="/resources/js/imagePicker.js?v=2"></script>
	<script src="/resources/js/editor.js?v=2"></script>
	<script src="/resources/js/froala.js"></script>
	<!-- <script src="https://cdn.rawgit.com/DerekFangming/WPI_CSA_Service/609cd534/WebContent/WEB-INF/view/js/froala.js"></script> -->

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
                    <li class="nav-item active">
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

       <div class="card mt-3">
			<div class="card-header" style="height:49px;">
				<p class="lead" style="margin-top:-13px"><font size="6">Cover&emsp;</font>This will be the cover image and title of your article</p>
			</div>
			<div class="row">
				<div class="col-lg-6 col-sm-12">
					<div id="img-picker" class="aspect-fill border-right" ondragover="allowDrop(event)"></div>
				</div>
				<div class="col-lg-6 clm-sm-12">
					<div class="card-body">
						<c:choose>
						<c:when test="${hasAvatar == null}">
					    <div class="alert alert-warning" role="alert" id ="alertMsg">
							You do not have an avatar and it will show as the default panda. We strongly recommend you to add an avatar from mobile end before posting articles.
						</div>
						<input type="hidden" id="hasAvatar" value="false">
						</c:when>
						<c:otherwise>
					    <div class="alert alert-success" role="alert" id ="alertMsg">
					    	Don't forget to check format to make sure your article look good in all platforms.
					    </div>
					    <input type="hidden" id="hasAvatar" value="true">
						</c:otherwise>
						</c:choose>
						<div class="input-group mb-3">
						    <div class="input-group-prepend">
						        <button class="btn btn-outline-secondary dropdown-toggle" id="currentType" type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">Blog</button>
						        <div class="dropdown-menu">
						            <a class="dropdown-item" href="#" onclick="selectType(1);">Blog</a>
						            <a class="dropdown-item" href="#" onclick="selectType(2);">Trade</a>
						            <c:if test="${user.roleId <= 2}">
						            <a class="dropdown-item" href="#" onclick="selectType(3);">Event</a>
			    						</c:if>
						        </div>
						    </div>
						    <input type="text" id="title" placeholder="Enter title and select article type" class="form-control" aria-label="Text input with dropdown button">
						</div>
						
						<c:if test="${user.roleId <= 2}">
						<div class="input-group mb-3" id="eventInput" style="display: none">
						    <div class="input-group-prepend">
						        <button class="btn btn-outline-secondary" id="eventInputBtn" type="button">Add an event</button>
						    </div>
						    <input type="text" class="form-control" placeholder="No event added yet" id="eventTitlePreview" disabled>
						</div>
						</c:if>
						
						<div class="mt-3"></div>
						<button type="button" id="submitBtn" class="btn btn-success pull-right">Submit</button>
						<button type="button" id="checkFormatBtn" class="btn btn-secondary">Check formatting</button>
						<button type="button" id="instructionBtn" class="btn btn-secondary">Instruction</button>
						
					</div>
				</div>
			</div>
		</div>
        
        <div class="card mt-3">
			<div class="card-header" style="height:49px;">
				<p class="lead" style="margin-top:-13px"><font size="6">Content&emsp;</font>This is the content of your article</p>
			</div>
			<div style="min-height:300px">
				<textarea></textarea>
			</div>
		</div>

		

    </div>
    <!-- /.container -->

	<c:if test="${user.roleId <= 2}">
	<!-- event input modal -->
	<div class="modal fade" id="eventInputModal" role="dialog">
	    <div class="modal-dialog modal-dialog-centered">
	
	        <!-- Modal content-->
	        <div class="modal-content">
	            <div class="modal-header">
	                <h4 class="modal-title">New event<small><small class="text-muted">&emsp;All fields are required.</small></small></h4>
	                <button type="button" class="close" data-dismiss="modal">&times;</button>
	            </div>
	            <div class="modal-body">
		            	<form>
					    <div class="form-group">
					        <label for="recipient-name" class="col-form-label">Title:</label>
					        <input type="text" class="form-control" id="eventTitle">
					    </div>
					    <div class="form-group">
					        <label for="recipient-name" class="col-form-label">Description:</label>
					        <input type="text" class="form-control" id="eventDesc">
					    </div>
					    <div class="form-group">
					        <label for="message-text" class="col-form-label">Start Time:</label>
					        <input type="datetime-local" class="form-control" id="eventSTime">
					    </div>
					    <div class="form-group">
					        <label for="message-text" class="col-form-label">End Time:</label>
					        <input type="datetime-local" class="form-control" id="eventETime">
					    </div>
					    <div class="form-group">
					        <label for="message-text" class="col-form-label">Location:</label>
					        <input type="text" class="form-control" id="eventLocation">
					    </div>
					    <div class="form-group">
					    		<label for="message-text" class="col-form-label">Event type:</label>
					    		<div class="input-group">
							    <div class="input-group-prepend">
							        <button class="btn btn-secondary" type="button" id="ticketToggleBtn">Sell ticket</button>
							        <button class="btn btn-outline-secondary" type="button" id="calToggleBtn">Calendar only</button>
							    </div>
							    <input type="text" id="ticketStatusLbl" class="form-control" placeholder="Enter following fields" disabled>
							</div>
						</div>
						<div id="ticketDetailSec">
							<div class="form-group">
						    		<label for="message-text" class="col-form-label">Ticket price:<small class="text-warning">&emsp;Cannot modify once created.</small></label>
						    		<div class="input-group">
								    <div class="input-group-prepend">
								        <button class="btn btn-secondary" type="button" id="freeTicketToggleBtn">Free</button>
								        <button class="btn btn-outline-secondary" type="button" id="paidTicketToggleBtn">Sell ticket</button>
								        <span class="input-group-text">$</span>
								    </div>
								    <input type="text" id="ticketFeeInput" onkeypress="validateInputNumber(event)" onfocusout="this.value = formalizeFeeAmount(this.value)" class="form-control" placeholder="0.00" disabled>
								</div>
							</div>
							<div class="form-group">
						    		<label for="message-text" class="col-form-label">Ticket balance:</label>
						    		<div class="input-group">
								    <div class="input-group-prepend">
								        <button class="btn btn-secondary" type="button" id="sellNowToggleBtn">Sell now</button>
								        <button class="btn btn-outline-secondary" type="button" id="sellLaterToggleBtn">Sell later</button>
								        <span class="input-group-text">#</span>
								    </div>
								    <input type="text" id="ticketBalInput" onkeypress="return event.charCode >= 48 && event.charCode <= 57" class="form-control" placeholder="Ticket balance count">
								</div>
							</div>
							<div class="form-group">
						        <label for="message-text" class="col-form-label">Ticket design:</label>
						        <div>
						        		<div style="float: left;">
										<button class="btn btn-secondary" type="button" onclick="chooseFile(1);" id="ticketBgSBtn">Add background</button><br>
								        <button class="btn btn-secondary mt-3" type="button" onclick="chooseFile(2);" id="ticketThumBtn">Add thumbnail</button>
								        <div style="display:none;">
									        	<input id="ticketImgInput" type="file" accept="image/jpeg, image/jpg, image/png"/>
									        <input id="ticketImgOption" value="1">
										</div>
									</div>
									<div class="ticket-bg-container" >
										<img id="ticketBGImage" class="aspect-fill" width="300px" height="450px" style="position: absolute;"/>
										<div class="ticket_blur"></div>
										<div class="ticket-thum-container border">
										<img id="ticketThumnImage" class="aspect-fill" width="150px" height="150px"/>
										</div>
									</div>
						        </div>
								
						    </div>
					    </div>
					    
					</form>
				</div>
				<div class="modal-footer">
					<button onclick="saveEvent();" type="button" class="btn btn-primary">Save</button>
					<button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
				</div>
			</div>
	    </div>
	</div>
	<!-- /.event input modal -->
	</c:if>
    
    <%@include file="subview/popup.jsp" %><!-- This must be placed below all other modals -->

    <%@include file="subview/footer.jsp" %>
    
    <script src="/resources/js/createFeed.js?v=2"></script>

	<script src="<%=BSjs %>"></script>

</body>

</html>