<!--A Design by W3layouts
Author: W3layout
Author URL: http://w3layouts.com
License: Creative Commons Attribution 3.0 Unported
License URL: http://creativecommons.org/licenses/by/3.0/
-->
<!DOCTYPE HTML>
<%@page import="za.co.willemvisser.wpvhomecontroller.config.dto.XbeeConfigDeviceDTO"%>
<%@page import="za.co.willemvisser.wpvhomecontroller.config.dto.XbeeConfigDTO"%>
<%@page import="za.co.willemvisser.wpvhomecontroller.xbee.dto.XbeeDTO"%>
<%@page import="com.rapplogic.xbee.api.XBeeAddress64"%>
<%@page import="java.util.HashMap"%>
<%@page import="za.co.willemvisser.wpvhomecontroller.xbee.XbeeController"%>
<%@page import="za.co.willemvisser.wpvhomecontroller.weather.dto.ForecastDayDTO"%>
<%@page import="za.co.willemvisser.wpvhomecontroller.weather.WeatherService"%>
<%@page import="za.co.willemvisser.wpvhomecontroller.weather.dto.Forecast10DayDTO"%>
<html>
<head>
<title>WPV Home Controller | Home</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
<link href="css/style.css" rel="stylesheet" type="text/css" media="all"/>
<link href="css/nav.css" rel="stylesheet" type="text/css" media="all"/>
<link href='http://fonts.googleapis.com/css?family=Carrois+Gothic+SC' rel='stylesheet' type='text/css'>

<link rel="stylesheet" href="css/wxug_core.css">		  
<link rel="stylesheet" href="css/wxug_omnibus.css">
<link rel="stylesheet" href="css/percentage_style.css">

<script type="text/javascript" src="js/jquery.js"></script>
<script type="text/javascript" src="js/login.js"></script>
<script type="text/javascript" src="js/Chart.js"></script>
 <script type="text/javascript" src="js/jquery.easing.js"></script>
 <script type="text/javascript" src="js/jquery.ulslide.js"></script>
 <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script>
 <!----Calender -------->
  <link rel="stylesheet" href="css/clndr.css" type="text/css" />
  <script src="js/underscore-min.js"></script>
  <script src= "js/moment-2.2.1.js"></script>
  <script src="js/clndr.js"></script>
  <script src="js/site.js"></script>  
  <!----End Calender -------->
</head>
<body>			  

	
    <!-- Menu -->
	<div class="wrap">	 
	      <div class="header">
	      	  <div class="header_top">
					  <div class="menu">
						  <a class="toggleMenu" href="#"><img src="images/nav.png" alt="" /></a>
							<ul class="nav">
								<li><a href="index.jsp"><i><img src="images/settings.png" alt="" /></i>Home</a></li>
								<li><a href="irrigation.jsp"><i><img src="images/user.png" alt="" /></i>Irrigation</a></li>
								<li class="active"><a href="cameras.jsp"><i><img src="images/views.png" alt="" /></i>Cameras</a></li>
								<li><a href="temperatures.jsp"><i><img src="images/mail.png" alt="" /></i>Temperatures</a></li>
								<!-- 
								<li><a href="lights.jsp"><i><img src="images/mail.png" alt="" /></i>Lights</a></li>
								 -->
								<li><a href="settings.jsp"><i><img src="images/settings.png" alt="" /></i>Settings</a></li>
							
						    </ul>
							<script type="text/javascript" src="js/responsive-nav.js"></script>
				        </div>	
					  
		 		      <div class="clear"></div>				 
				   </div>
			</div>	  					     
	</div>		
	
	<div class="main">
	<%
	if (request.getParameter("cam") != null) {
	%>		  	    
		<a href="cameras.jsp"><img src="http://192.168.1.203:808<%=request.getParameter("cam")%>" border="0" width="100%"/></a>		
	<%
	} else {
	%>	    	    
		<a href="cameras.jsp?cam=2"><img src="http://192.168.1.203:8082" border="0" width="40%"/></a>
		<a href="cameras.jsp?cam=3"><img src="http://192.168.1.203:8083" border="0" width="40%"></a>
		<a href="cameras.jsp?cam=1"><img src="http://192.168.1.203:8081" border="0" width="40%"></a>
		<a href="cameras.jsp?cam=4"><img src="http://192.168.1.203:8084" border="0" width="40%"></a> 	   	  
   <%
	}
   %>
   </div>
   <br/>
  		 <div class="copy-right" style="float: right;">
				<p>© 2016 Willem Visser, Version 1.50</p>
	 	 </div>   
</body>
</html>

