<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="en">

<head>

    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <meta name="description" content="">
    <meta name="author" content="">

    <title>WPI CSA - Life</title>

    <!--<link href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0-beta.2/css/bootstrap.min.css" rel="stylesheet">-->
    <link href="/resources/css/local/bootstrap.min.css" rel="stylesheet">

    <link href="/resources/css/main.css" rel="stylesheet">
	
    <!--<script src="https://code.jquery.com/jquery-3.2.1.min.js"></script>-->
	<script src="/resources/css/local/jquery-3.2.1.min.js"></script>
	
	<!--<script>
	$(document).ready(function(){
	  $("#signinButton").click(function(){
		  $("#signinIcon").attr("class","fa fa-circle-o-notch fa-spin");
	  });
	});
	</script>-->

</head>

<body>

    <!-- Navigation -->
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark fixed-top">
        <div class="container">
            <a class="navbar-brand" href="#">WPI CSA</a>
            <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarResponsive" aria-controls="navbarResponsive" aria-expanded="false" aria-label="Toggle navigation">
          <span class="navbar-toggler-icon"></span>
        </button>
            <div class="collapse navbar-collapse" id="navbarResponsive">
                <ul class="navbar-nav ml-auto">
                    <li class="nav-item active">
                        <a class="nav-link" href="#">Life</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="sg.html">Survival Guide</a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="#signup" data-toggle="modal" data-target=".bs-modal-sm">Login/Register</a>
                    </li>
                </ul>
            </div>
        </div>
    </nav>

    <!-- Page Content -->
    <div class="container">

        <!-- Jumbotron Header -->
        <header class="jumbotron my-4">
            <h1 class="display-3">A Warm Welcome!</h1>
            <p class="lead">This site is created as web version of WPI CSA mobile apps. It has limited features for now comparing with mobile apps.</p>
			<p class="lead">If you see any errors & bugs, don't forget to report to CSA!</p>
        </header>
		
		<div class="card top-buffer">
			<div class="card-header">
				<h1><a href="https://www.google.com">Some title up here</a></h1>
			</div>
			<div class="row">
				<div class="col">
					<div class="feed-image">
						<img class="tall" src="http://placehold.it/400x800" />
					</div>
				</div>
				<div class="col">
					<p class="lead top-buffer">This site is created as web version of WPI CSA mobile apps. It has limited features for now comparing with mobile apps.</p>
				</div>
			</div>
		</div>
		
		
		<div class="card top-buffer">
			<div class="card-header">
				<h1>Some title up here</h1>
			</div>
			<div class="row">
				<div class="col">
					<div class="feed-image">
						<img class="tall" src="http://placehold.it/800x400" />
					</div>
				</div>
				<div class="col">
					<p class="lead">This site is created as web version of WPI CSA mobile apps. It has limited features for now comparing with mobile apps.</p>
				</div>
			</div>
		</div>
		
		<div class="card top-buffer">
			<div class="row">
				<div class="col">
					<h1>Some title up here</h1>
				</div>
			</div>
			<div class="row">
				<div class="col">
					<img class = "center-block" src="http://placehold.it/500x300"  />
				</div>
				<div class="col">
					<p class="lead">This site is created as web version of WPI CSA mobile apps. It has limited features for now comparing with mobile apps.</p>
				</div>
			</div>
		</div>

        <!-- Page Features -->
        
        <!-- /.row -->

    </div>
    <!-- /.container -->

    <!-- Modal -->
    <div class="modal fade bs-modal-sm" id="myModal" tabindex="-1" role="dialog" aria-labelledby="mySmallModalLabel" aria-hidden="true">
        <div class="modal-dialog modal-md">
            <div class="modal-content">
                <br>
                <ul id="myTab" class="nav nav-tabs">
                    <li class="nav-item"><a class="nav-link active" href="#signin" data-toggle="tab">Sign In</a></li>
                    <li class="nav-item"><a class="nav-link" href="#signup" data-toggle="tab">Register</a></li>
                    <li class="nav-item"><a class="nav-link" href="#why" data-toggle="tab">Why?</a></li>
                </ul>
                <div class="modal-body">
                    <div id="myTabContent" class="tab-content">
                        <div class="tab-pane fade" id="why">
                            <p>You need to log in to get free tickets for events hosted by CSA, edit Survival Guide, etc. Note that you need to register using your @wpi.edu email in order to have such privilege.</p>
                            <p><br> Please contact <a href="mailto:csa@wpi.edu">csa@wpi.edu</a> for any questions.</p>
                        </div>
                        <div class="tab-pane fade show active" id="signin">
                            <form class="form-horizontal" action="/web_login" method="post">
                                <fieldset>
                                    <!-- Sign In Form -->
                                    <!-- Text input-->
                                    <div class="control-group">
                                        <label class="control-label" for="userid">Username:</label>
                                        <div class="controls">
                                            <input required="" id="username" name="username" type="text" class="form-control" placeholder="Your username \ email" class="input-medium" required="">
                                        </div>
                                    </div>

                                    <!-- Password input-->
                                    <div class="control-group">
                                        <label class="control-label" for="passwordinput">Password:</label>
                                        <div class="controls">
                                            <input required="" id="password" name="password" class="form-control" type="password" placeholder="Your password" class="input-medium">
                                        </div>
                                    </div>

                                    <!-- Multiple Checkboxes (inline) -->
                                    <div class="control-group">
                                        <label class="control-label" for="rememberme"></label>
                                        <div class="controls">
                                            <label class="checkbox inline" for="rememberme-0">
											<input type="checkbox" name="remember" id="remember" value="remember">
											Remember me
											</label>
                                        </div>
                                    </div>

                                    <!-- Button -->
                                    <div class="control-group">
                                        <label class="control-label" for="signin"></label>
                                        <div class="controls">
                                            <button id="signinButton" name="signin" class="btn btn-success">Sign In</button>
                                        </div>
                                    </div>
                                </fieldset>
                            </form>
                        </div>
                        <div class="tab-pane fade" id="signup">
                            <form class="form-horizontal">
                                <fieldset>
                                    <!-- Sign Up Form -->
                                    <!-- Text input-->
                                    <div class="control-group">
                                        <label class="control-label" for="Email">Username:</label>
                                        <div class="controls">
                                            <input id="Email" name="Email" class="form-control" type="text" placeholder="Your email. Preferably your @wpi.edu email" class="input-large" required="">
                                        </div>
                                    </div>

                                    <!-- Text input-->
                                    <div class="control-group">
                                        <label class="control-label" for="userid">Name:</label>
                                        <div class="controls">
                                            <input id="userid" name="userid" class="form-control" type="text" placeholder="Your name" class="input-large" required="">
                                        </div>
                                    </div>

                                    <!-- Password input-->
                                    <div class="control-group">
                                        <label class="control-label" for="password">Password:</label>
                                        <div class="controls">
                                            <input id="password" name="password" class="form-control" type="password" placeholder="At least 6 characters with letters and numbers" class="input-large" required="">
                                          
                                        </div>
                                    </div>

                                    <!-- Text input-->
                                    <div class="control-group">
                                        <label class="control-label" for="reenterpassword">Re-Enter Password:</label>
                                        <div class="controls">
                                            <input id="reenterpassword" class="form-control" name="reenterpassword" type="password" placeholder="Enter your password again" class="input-large" required="">
                                        </div>
                                    </div>

                                    <!-- Button -->
                                    <div class="control-group">
                                        <label class="control-label" for="confirmsignup"></label>
                                        <div class="controls">
                                            <button id="confirmsignup" name="confirmsignup" class="btn btn-success">Sign Up</button>
                                        </div>
                                    </div>
                                </fieldset>
                            </form>
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <center>
                        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                    </center>
                </div>
            </div>
        </div>
    </div>

    <!-- Footer -->
    <footer class="py-5 bg-dark top-buffer">
        <div class="container">
            <p class="m-0 text-center text-white">Copyright &copy; Your Website 2017</p>
        </div>
        <!-- /.container -->
    </footer>

    <!--<script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0-beta.2/js/bootstrap.bundle.min.js"></script>-->
    <script src="/resources/css/local//bootstrap.bundle.min.js"></script>

</body>

</html>