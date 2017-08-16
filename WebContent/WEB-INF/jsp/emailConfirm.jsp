<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        <meta>
        <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
        <title>_(:3」 ∠)_</title>
        <meta name="description" content="">
        <meta name="viewport" content="width=device-width">

        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
        <link rel="stylesheet" href="/resources/css/emailConfirm.css">

		
		<script>
			var colors = ["#34495e", "#2980b9", "#8e44ad", "#d35400", "#c0392b"];
			var ind = 0;
			
			function myFunction() {
				var a = {
					backgroundColor: colors[(ind++) % 5]
				};
			
				$("body").animate(a, 525);
				setTimeout( myFunction, 2000 );
			}
			
			function close_window() {
				window.open(location, '_self', '');
				close();
			}
		</script>
    </head>
    <body onload="myFunction()">


    <div class="container">
        <section>
		<div  class="story-container story"></div>

        <div class="row">
            <div class="col-xs-12 col-sm-8 col-sm-offset-2">
                    <p class="center50text amatic">Thank you for registering at fmning.com domain.</p>
            </div>
        </div>
		<div  class="story-container story"></div>
		<div class="row">
            <div class="col-xs-12 col-sm-8 col-sm-offset-2">
                    <p class="center35text amatic">${msg}</p>
            </div>
        </div>
		<div  class="story-container story"></div>
		<div class="center35text"><a href="#" onclick="close_window();return false;"><img src="/resources/img/exit.png" class="story-icon-3"></a></div>
		<div  class="story-container story"></div>

        </section>

    </div> 
		<script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/1.10.1/jquery.min.js"></script>
        <script src="/resources/js/emailConfirm.min.js"></script>

    </body>
</html>
