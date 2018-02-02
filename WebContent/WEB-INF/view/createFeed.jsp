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

    <link href="/resources/css/main.css?v=1" rel="stylesheet">
	
	<script src="/resources/js/common.js?v=1"></script>
	<script src="resources/js/imagePicker.js?v=1"></script>
	<script src="resources/js/editor.js"></script>
	
	<script>
	$(function() {
	    $('textarea').froalaEditor({
	    	height: 300,
			toolbarButtons: ['bold', 'italic', 'underline', '|', 'color', 'paragraphFormat', 'align', '|', 'insertImage', 'insertTable', '|', 'undo', 'redo', 'spellChecker', 'selectAll', 'clearFormatting', '|', 'print', 'fullscreen', 'html'],
			toolbarButtonsMD: ['bold', 'italic', 'underline', '|', 'color', 'paragraphFormat', 'align', '|', 'insertImage', 'insertTable', '|', 'undo', 'redo', 'spellChecker', 'selectAll', 'clearFormatting', '|', 'print', 'fullscreen', 'html'],
			toolbarButtonsSM: ['bold', 'italic', 'underline', '|', 'color', 'paragraphFormat', 'align', '-', 'insertImage', 'insertTable', '|', 'undo', 'redo', 'spellChecker', 'selectAll', 'clearFormatting', '|', 'print', 'fullscreen', 'html'],
			toolbarButtonsXS: ['bold', 'italic', 'underline', '|', 'color', 'paragraphFormat', 'align', '-', 'insertImage', 'insertTable', '|', 'undo', 'redo', 'spellChecker', 'selectAll', 'clearFormatting', '|', 'print', 'fullscreen', 'html'],
			paragraphFormat: {
				H1: 'Heading 1',
				H2: 'Heading 2',
				H3: 'Heading 3',
				H4: 'Heading 4',
				N: 'Paragraph'
			}, 
			imageInsertButtons: ['imageBack', '|', 'imageUpload', 'imageByURL'],
			imageEditButtons: ['imageReplace', 'imageSize', 'imageRemove'],
			tableEditButtons: ['tableRows', 'tableRemove'],
			tableInsertMaxSize: 1,
			colorsBackground: ['#FFFFFF', '#FFFFFF', '#FFFFFF', '#FFFFFF', '#FFFFFF'],
			quickInsertButtons: ['image']
		});
	});
	</script>
	<script>
    
	$(document).ready(function() {
		$('#img-picker').imagePicker({name: 'images'});
	})
	</script>

	

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
                        <a class="nav-link" href="#">Life</a>
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

       <div class="card top-buffer">
			<div class="card-header" style="height:49px;">
				<p class="lead" style="margin-top:-13px"><font size="6">Cover&emsp;</font>This will be the cover image and title of your article</p>
			</div>
			<div class="row">
				<div class="col-lg-6 col-sm-12">
					<div class="life-img-container">
						<div id="img-picker" class="right-line aspect-fill"></div>
					</div>
				</div>
				<div class="col-lg-6 clm-sm-12">
					<div class="card-body">
						<div class="alert alert-warning" role="alert">
							You do not have an avatar and it will show as the default panda. We strongly recommend you to add an avatar from mobile end before posting articles.
						</div>
						<div class="input-group">
						    <div class="input-group-prepend">
						        <button class="btn btn-outline-secondary dropdown-toggle" type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
						        	Blog
						        </button>
						        <div class="dropdown-menu">
						            <a class="dropdown-item" href="#">Blog</a>
						            <a class="dropdown-item" href="#">Trade</a>
						            <a class="dropdown-item" href="#">Event</a>
						        </div>
						    </div>
						    <input type="text" placeholder="Enter title and select article type" class="form-control" aria-label="Text input with dropdown button">
						</div>
					</div>
				</div>
			</div>
		</div>
        
        <div class="card top-buffer">
			<div class="card-header" style="height:49px;">
				<p class="lead" style="margin-top:-13px"><font size="6">Content&emsp;</font>This is the content of your article</p>
			</div>
			<div style="min-height:300px">
				<textarea></textarea>
			</div>
		</div>

		

    </div>
    <!-- /.container -->


    
    <%@include file="subview/popup.jsp" %><!-- This must be placed below all other modals -->

    <%@include file="subview/footer.jsp" %>

	<script src="<%=BSjs %>"></script>

</body>

</html>