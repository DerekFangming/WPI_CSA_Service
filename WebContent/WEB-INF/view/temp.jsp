<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<title>Doctor's panel</title>
		<link href="https://cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/4.0.0/css/bootstrap.min.css" rel="stylesheet">
		<script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
		<script src="https://cdnjs.cloudflare.com/ajax/libs/Chart.js/2.4.0/Chart.min.js"></script>
		<link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css" rel="stylesheet">
		<script src="/resources/js/common.js"></script>
	</head>
	<body>
		<nav class="navbar navbar-expand-md navbar-dark bg-dark fixed-top">
			<div class="container">
				<a class="navbar-brand" href="./mqp">Doctor's panel</a>
				
				<div class="float-right">
					<button id="downloadBtn" class="btn btn-outline-success float-right ml-4">Download</button>
					<button id="refreshBtn" class="btn btn-outline-success float-right">Refresh</button>
				</div>
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
				
				<div class="col-5">
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
						<tbody id="mainList">
							<c:forEach items="${list}" var="rec">
								<tr>
									<td><button type="button" class="btn btn-primary" onclick="loadGraph(${rec.id});"><i class="fa fa-sign-in"></i></button></td>
									<td><script>parseDate('${rec.createdAt}');</script></td>
									<td>${rec.ehr}</td>
									<td>${rec.phr}</td>
									<td>${rec.temp}</td>
									<td>${rec.spo2}</td>
								</tr>
							</c:forEach>
							
						</tbody>
					</table>
					<input type="hidden" id="currentId" value="${last}">
				</div>
				<div class="col-7">
					<div class="row ml-2">
						<canvas id="ecgChart" class="ml-2"></canvas>
					</div>
					<div class="row ml-2">
						<canvas id="rdChart" class="ml-2"></canvas>
					</div>
					<div class="row ml-2">
						<canvas id="irdChart" class="ml-2"></canvas>
					</div>
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
			
			$(document).ready(function() {
				loadGraph($('#currentId').val());
			});
			
			$('#refreshBtn').click(function(){
				$.ajax({
		    		type: "GET",
		    		url: "./get_all_ppgecg",
		            contentType: "application/json",
		            dataType: "json",
			    		success: function (data) {
			    			if (data['error'] != '') {
			    				alert(data['error']);
			    			} else {
			    				var arr = data['list'];
			    				var body = '';
			    				for(var i = 0; i < arr.length; i++) {
			    					var obj = arr[i];
			    					body += '<tr><td><button type="button" class="btn btn-primary" onclick="loadGraph(' + obj.id;
			    					body += ');"><i class="fa fa-sign-in"></i></button></td>';
			    					body += '<td>' + parseDateStr(obj.createdAt) + '</td>';
			    					body += '<td>' + obj.ehr + '</td>';
			    					body += '<td>' + obj.phr + '</td>';
			    					body += '<td>' + obj.temp + '</td>';
			    					body += '<td>' + obj.spo2 + '</td></tr>';
			    				}
			    				
			    				$('#mainList').html(body);
			    			}
			    		},
			    		error: function (jqXHR, textStatus, errorThrown) {
			    			alert('Unknown error occured.');
			    		}
			    	});
			});
			
			$('#downloadBtn').click(function(){
				window.location="./download_ppgecg?id=" + $('#currentId').val();
			});
			
			function loadGraph(id) {
				$('#currentId').val(id);
				$.ajax({
			    		type: "GET",
			    		url: "./get_graph_data",
			    		data: {"id": id},
		            contentType: "application/json",
		            dataType: "json",
			    		success: function (data) {
			    			if (data['error'] != '') {
			    				alert(data['error']);
			    			} else {
			    				var ecgXAxis = createXAxis(data['edList']);
			    				var ecgChartCan = document.getElementById('ecgChart').getContext('2d');
			    				var ecgChart = new Chart(ecgChartCan, {
			    					// The type of chart we want to create
			    					type: 'line',
			    				
			    					// The data for our dataset
			    					data: {
			    						labels: ecgXAxis,
			    						datasets: [{
			    							label: "ECG",
			    							//backgroundColor: 'rgb(255, 99, 132)',
			    							borderColor: 'rgb(255, 99, 132)',
			    							data: data['edList'],
			    						}]
			    					},
			    				
			    					// Configuration options go here
			    					options: {scales:{xAxes: [{display: false}]}}
			    				});
			    				
			    				var ppgXAxis = createXAxis(data['rdList']);
			    				var rdChartCan = document.getElementById('rdChart').getContext('2d');
			    				var rdChart = new Chart(rdChartCan, {
			    					// The type of chart we want to create
			    					type: 'line',
			    				
			    					// The data for our dataset
			    					data: {
			    						labels: ppgXAxis,
			    						datasets: [{
			    							label: "RD",
			    							//backgroundColor: 'rgb(255, 99, 132)',
			    							borderColor: 'rgb(255, 99, 132)',
			    							data: data['rdList'],
			    						}]
			    					},
			    				
			    					// Configuration options go here
			    					options: {scales:{xAxes: [{display: false}]}}
			    				});
			    				
			    				var irdChartCan = document.getElementById('irdChart').getContext('2d');
			    				var irdChart = new Chart(irdChartCan, {
			    					// The type of chart we want to create
			    					type: 'line',
			    				
			    					// The data for our dataset
			    					data: {
			    						labels: ppgXAxis,
			    						datasets: [{
			    							label: "IRD",
			    							//backgroundColor: 'rgb(255, 99, 132)',
			    							borderColor: 'rgb(255, 99, 132)',
			    							data: data['irdList'],
			    						}]
			    					},
			    				
			    					// Configuration options go here
			    					options: {scales:{xAxes: [{display: false}]}}
			    				});
			    			}
			    		},
			    		error: function (jqXHR, textStatus, errorThrown) {
			    			alert('Unknown error occured.');
			    		}
			    	});
			};
			
			function createXAxis(array) {
				var xAxis = [];
				for (var i = 0; i < array.length; i++) {
					xAxis.push(i + 1);
				}
				return xAxis;
			};
		</script>
		<script src="https://cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/4.0.0/js/bootstrap.min.js"></script>
	</body>
</html>