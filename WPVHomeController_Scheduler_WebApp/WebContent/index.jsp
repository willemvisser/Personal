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
<head>
<title>WPV Home Controller | Home</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
<link href="css/style.css" rel="stylesheet" type="text/css" media="all"/>
<link href="css/nav.css" rel="stylesheet" type="text/css" media="all"/>
<link href='http://fonts.googleapis.com/css?family=Carrois+Gothic+SC' rel='stylesheet' type='text/css'>
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

	<script type="text/javascript">
		//setInterval('updateDeviceStatusDiv()', 1000); // refresh div after 5 secs
		setInterval('updateDeviceActiveDiv()', 1000); // refresh div after 1 secs
		setInterval('updateDeviceTempDiv()', 1000); // refresh div after 1 secs
	   
		function updateDeviceStatusDiv() {			
			$.get('./inc/deviceStatusDiv.jsp', function(data) {
			  $('#divDeviceStatus').html(data);
			});
		}
		
		function updateDeviceActiveDiv() {			
			$.get('./inc/deviceActiveDiv.jsp', function(data) {
			  $('#divDeviceActive').html(data);
			});
		}
		
		function updateDeviceTempDiv() {			
			$.get('./inc/deviceTempDiv.jsp', function(data) {
			  $('#divDeviceTemp').html(data);
			});
		}
		
		//updateDeviceStatusDiv();
		updateDeviceActiveDiv();
		updateDeviceTempDiv();
		
	</script>
     
	    <div class="wrap">	 
	      <div class="header">
	      	  <div class="header_top">
					  <div class="menu">
						  <a class="toggleMenu" href="#"><img src="images/nav.png" alt="" /></a>
							<ul class="nav">
								<li class="active"><a href="index.jsp"><i><img src="images/settings.png" alt="" /></i>Home</a></li>
								<li><a href="irrigation.jsp"><i><img src="images/user.png" alt="" /></i>Irrigation</a></li>
								<li><a href="lights.jsp"><i><img src="images/mail.png" alt="" /></i>Lights</a></li>
								<li><a href="settings.jsp"><i><img src="images/settings.png" alt="" /></i>Settings</a></li>
							<div class="clear"></div>
						    </ul>
							<script type="text/javascript" src="js/responsive-nav.js"></script>
				        </div>	
					  
		 		      <div class="clear"></div>				 
				   </div>
			</div>	  					     
	</div>
	
	  <div class="main">  
	    <div class="wrap">  		 
	    	<div class="column_left">	          
	    
	    		<div class="weather" style="margin-top:0px">
		        	<h3><i><img src="images/location.png" alt="" /> </i> Melkbosstrand</h3>
		            <!-- 
		            <div class="today_temp">
		            	<div class="temp">
							<figure>Fri 29/06<span>24<em>o</em></span></figure>
						</div>
						<img src="images/sun.png" alt="" />
					</div>
					 -->
					 
					<div class="temp_list">
				    	<ul>
				    	<%
							Forecast10DayDTO forecast10DayDTO = WeatherService.INSTANCE.get10DayForecast();
				    		if (forecast10DayDTO != null) {
							for (int i=0; i<12; i++) {
								ForecastDayDTO forecast = forecast10DayDTO.getForecasts().get(i);
								if (!forecast.getTitle().endsWith("Night")) {
							%>								
								<li><a href="#"><span class="day_name"><%=forecast.getTitle() %></span>&nbsp; <!-- 29/06  --> 
					  			<!-- <label class="digits">25<em>o</em>  --> <p><%=forecast.getFcText() %></p></label><div class="clear"></div></a>
					  			
					  			</li>
							<%	
								}
							}
				    		}
						%>
						<!-- 
						  		    <li><a href="#"><span class="day_name">Sat</span>&nbsp; 29/06 
						  			<label class="digits">25<em>o</em> <i><img src="images/sun_icon1.png" alt="" /></i></label><div class="clear"></div></a></li>
						  			<li class="active"><a href="#"><span class="day_name">Sun</span>&nbsp; 30/06 
						  			<label class="digits">22<em>o</em> <i><img src="images/clouds.png" alt="" /></i></label><div class="clear"></div></a></li>
						  			<li><a href="#"><span class="day_name">Mon</span>&nbsp; 01/07 
						  			<label class="digits">24<em>o</em> <i><img src="images/clouds.png" alt="" /></i></label><div class="clear"></div></a></li>
						  			<li><a href="#"><span class="day_name">Tue</span>&nbsp; 02/07 
						  			<label class="digits">26<em>o</em> <i><img src="images/sun_icon1.png" alt="" /></i></label><div class="clear"></div></a></li>
						  			<li><a href="#"><span class="day_name">Wed</span>&nbsp; 03/07 
						  			<label class="digits">27<em>o</em> <i><img src="images/sun_icon2.png" alt="" /></i></label><div class="clear"></div></a></li>
						  			<li><a href="#"><span class="day_name">Thu</span>&nbsp; 04/07
						  			<label class="digits">29<em>o</em> <i><img src="images/sun_icon2.png" alt="" /></i></label><div class="clear"></div></a></li>
						   -->
				    	</ul>
				      </div>
		          </div>
	    		 
	  		</div> <!-- column_left -->
	  		
            <div class="column_middle">
             <!-- 
              <div class="column_middle_grid1">
		      -->
		         <!--  
		         <div class="column_right_grid calender"  style="margin-top:0px">
                      <div class="cal1"> </div>
				 </div>
		         -->
		         
		         <div id="divDeviceTemp">            	
												   				  
				   
             	</div>	
		         
		     <!--     
		       </div>
			 -->		         	          
		         	           
		       
    	    </div>
    	    
            <div class="column_right" id="divDeviceActive">            	
												   				  
				   
             </div>
    	<div class="clear"></div>
 	 </div>
   </div>
   
  		 <div class="copy-right">
				<p>© 2015 Willem Visser, Version 1.41</p>
	 	 </div>   
</body>
</html>

