<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
  <title>Email confirmation</title>
  <link href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css" rel="stylesheet">
  <link href="/resources/css/agency.css" rel="stylesheet">
  <link href="https://maxcdn.bootstrapcdn.com/font-awesome/4.7.0/css/font-awesome.min.css" rel="stylesheet" type="text/css">
  <link href="https://fonts.googleapis.com/css?family=Montserrat:400,700" rel="stylesheet" type="text/css">
  <link href="https://fonts.googleapis.com/css?family=Kaushan+Script" rel="stylesheet" type="text/css">
  <link href="https://fonts.googleapis.com/css?family=Droid+Serif:400,700,400italic,700italic" rel="stylesheet" type="text/css">
  <link href="https://fonts.googleapis.com/css?family=Roboto+Slab:400,100,300,700" rel="stylesheet" type="text/css">
  <!--[if lt IE 9]> 
    <script src="https://oss.maxcdn.com/libs/html5shiv/3.7.0/html5shiv.js"></script> 
    <script src="https://oss.maxcdn.com/libs/respond.js/1.4.2/respond.min.js"></script> 
  <![endif]-->

  <script type="text/javascript">
    function close_window() {
    	window.open(location, '_self', '');
      close();
    }
  </script>

</head>

<body id="page-top" class="index">
  <nav class="navbar navbar-default navbar-fixed-top">
    <div class="container">
      <div class="navbar-header page-scroll">
        <a class="navbar-brand page-scroll" href="#page-top">Fmning.com</a>
      </div>
      <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
        <ul class="nav navbar-nav navbar-right"> </ul>
      </div>
    </div>
  </nav>
  <header>
    <div class="container">
      <div class="intro-text">
        <div class="intro-lead-in">Thank you for registering at fmning.com domain</div>
        <div class="intro-lead-in ${msgFont}">${msg}</div>
        ${extraMsg}

        <a href="#" onclick="close_window();return false;" class="page-scroll btn btn-xl">close</a>
      </div>
    </div>
  </header>
  <footer>
    <div class="container">
      <div class="row">
        <div class="col-md-4"> <span class="copyright">Copyright &copy; fmning.com 2017</span> </div>
        <div class="col-md-4">
          <ul class="list-inline social-buttons">
            <li><a href="#"><i class="fa fa-twitter"></i></a> </li>
            <li><a href="#"><i class="fa fa-facebook"></i></a> </li>
            <li><a href="#"><i class="fa fa-linkedin"></i></a> </li>
          </ul>
        </div>
        <div class="col-md-4">
          <ul class="list-inline quicklinks">
            <li><a href="http://fmning.com/privacy_policy.html" target="_blank">Privacy Policy</a> </li>
            <li><a href="http://fmning.com/term_of_use.html" target="_blank">Terms of Use</a> </li>
          </ul>
        </div>
      </div>
    </div>
  </footer>
  <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/1.11.1/jquery.min.js"></script>
  <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
  <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery-easing/1.3/jquery.easing.min.js"></script>
  <script src="https://cdnjs.cloudflare.com/ajax/libs/classie/1.0.1/classie.js"></script>
  <script src="https://cdnjs.cloudflare.com/ajax/libs/animated-header/0.0.1/js/cbpAnimatedHeader.min.js"></script>
  <script src="/resources/js/agency.js"></script>
</body>
</html>