<html>
	<head>
		<title>Docter's panel</title>
		<link href="https://cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/4.0.0/css/bootstrap.min.css" rel="stylesheet">
		<script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
		<script src="https://cdnjs.cloudflare.com/ajax/libs/Chart.js/2.4.0/Chart.min.js"></script>
		<link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css" rel="stylesheet">
	</head>
	<body>
		<nav class="navbar navbar-expand-md navbar-dark bg-dark fixed-top">
			<div class="container">
				<a class="navbar-brand" href="./">Docter's panel</a>
				
			</div>
		</nav>
		<!-- Page Content -->
		<div class="container">
			<!-- Padding for nav bar -->
			<div class="row my-4" style="height:30px"></div>
			
			<!-- Fake login div -->
			<div id="loginDiv" class="row" style="max-width:350px;margin:0 auto;display:none;">
				<div class="input-group" style="margin-top:120px;">
					<div class="input-group-prepend">
						<span class="input-group-text" id="basic-addon1">Username</span>
					</div>
					<input type="text" class="form-control" placeholder="Enter your username">
				</div>
				<div class="input-group my-3">
					<div class="input-group-prepend">
						<span class="input-group-text">Password&nbsp;</span>
					</div>
					<input type="password" class="form-control" placeholder="Enter your password">
				</div>
				<button id="loginBtn" type="button" class="btn btn-primary btn-block">Login</button>
			</div>
			
			<!-- Data div -->
			<div id="dataDiv" class="row" >
				
				<div class="col-4">
					<table class="table table-striped">
						<thead>
							<tr>
								<th scope="col">View</th>
								<th scope="col">Time</th>
								<th scope="col">ECG</th>
								<th scope="col">PPG</th>
								<th scope="col">Temp</th>
								<th scope="col">SPO2</th>
							</tr>
						</thead>
						<tbody>
							<tr>
								<td><button type="button" class="btn btn-primary"><i class="fa fa-sign-in"></i></button></td>
								<td>2018/1/2 23:55:00</td>
								<td>12</td>
								<td>45</td>
								<td>80</td>
								<td>345</td>
							</tr>
							<tr>
								<td><button type="button" class="btn btn-primary"><i class="fa fa-sign-in"></i></button></td>
								<td>2018/1/2 23:55:00</td>
								<td>12</td>
								<td>45</td>
								<td>80</td>
								<td>345</td>
							</tr>
							<tr>
								<td><button type="button" class="btn btn-primary"><i class="fa fa-sign-in"></i></button></td>
								<td>2018/1/2 23:55:00</td>
								<td>12</td>
								<td>45</td>
								<td>80</td>
								<td>345</td>
							</tr>
						</tbody>
					</table>
				</div>
				<div class="col-8">
					<canvas id="myChart" class="ml-2"></canvas>
				</div>
				
			</div>
		</div>
		<script>
			$('#loginBtn').click(function(){
				setTimeout(function() {
					$('#loginDiv').fadeOut('slow', function() {
						$('#dataDiv').fadeIn();
					});
				}, 500);
			});
			
			$( document ).ready(function() {
				var ctx = document.getElementById('myChart').getContext('2d');
				var chart = new Chart(ctx, {
					// The type of chart we want to create
					type: 'line',
				
					// The data for our dataset
					data: {
						labels: ["January", "February", "March", "April", "May", "June", "July"],
						datasets: [{
							label: "My First dataset",
							backgroundColor: 'rgb(255, 99, 132)',
							borderColor: 'rgb(255, 99, 132)',
							data: [0, 10, 5, 2, 20, 30, 45],
						}]
					},
				
					// Configuration options go here
					options: {}
				});
			});
		</script>
		<script src="https://cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/4.0.0/js/bootstrap.min.js"></script>
	</body>
</html>