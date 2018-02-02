<!--A Design by W3layouts
Author: W3layout
Author URL: http://w3layouts.com
License: Creative Commons Attribution 3.0 Unported
License URL: http://creativecommons.org/licenses/by/3.0/
-->
<!DOCTYPE HTML>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Set"%>
<%@page import="za.co.willemvisser.wpvhomecontroller.video.VideoBankDayDTO"%>
<%@page import="za.co.willemvisser.wpvhomecontroller.video.VideoBankController"%>
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
<title>WPV Home Controller | Security Archive</title>
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
		<div class="column_left">	 
		<h3>File List:</h3>
		<%
			//TODO - move these to the servlet context init
			VideoBankController.INSTANCE.init();
			VideoBankController.INSTANCE.refresh();
			
			String paramDate = request.getParameter("date");
			String paramHour = request.getParameter("hour");
			
			if (paramDate == null) { 
				HashMap<String, VideoBankDayDTO> videoMap = VideoBankController.INSTANCE.getVideoMap();
			 	Set<String> dateSet = videoMap.keySet();
			 	for (String dateStr : dateSet) {
					out.print("<a href=\"securitycams.jsp?date=");
					out.print(dateStr);
					out.print("\">");
					out.print(dateStr);
					out.println("</a>");					
					out.println("<br/>");
			 	}
			} else if (paramHour == null) {
				out.println("<h2>");
				out.println(paramDate);
				out.println("</h2>");
				VideoBankDayDTO videoBankDayDTO = VideoBankController.INSTANCE.getVideoBankDayDTO(paramDate);
				HashMap<String, ArrayList<String>> videosPerHourMap = videoBankDayDTO.listVideosPerHour();
				Set<String> hourSet = videosPerHourMap.keySet();
				
				for (String hour : hourSet) {
					out.print("<a href=\"securitycams.jsp?date=");
					out.print(paramDate);
					out.print("&hour=");
					out.print(hour);
					out.print("\">");
					out.print(hour);
					out.println("</a>");					
					out.println("<br/>");
				}
			 } else {
				 out.println("<h2>");					
				 	out.print(paramDate);
					out.print(" - ");
					out.print(paramHour);
					out.println("</h2>");
					
					VideoBankDayDTO videoBankDayDTO = VideoBankController.INSTANCE.getVideoBankDayDTO(paramDate);
					HashMap<String, ArrayList<String>> videosPerHourMap = videoBankDayDTO.listVideosPerHour();
					ArrayList<String> videoList = videosPerHourMap.get(paramHour);
					
					for (String video : videoList) {
						out.print("<a href=\"securitycams.jsp?date=");
						out.print(paramDate);
						out.print("&hour=");
						out.print(paramHour);
						out.print("&video=");
						out.print(video);
						out.print("\">");
						out.print(video);
						out.println("</a>");					
						out.println("<br/>");
					} 
			 }
			
			
		%>
		</div>
		<div class="column_middle"  style="width:75%; margin-top:0px">
			<embed src="video/<%=request.getParameter("video") %>" width="1280" height="960">
		</div>
	</div>
   <br/>
  		 <div class="copy-right" style="float: right;">
				<p>© 2016 Willem Visser, Version 1.50</p>
	 	 </div>   
</body>
</html>