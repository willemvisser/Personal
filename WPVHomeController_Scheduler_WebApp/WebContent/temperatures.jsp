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
<title>WPV Home Controller | Temperatures</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
<link href="css/style.css" rel="stylesheet" type="text/css" media="all"/>
<link href="css/nav.css" rel="stylesheet" type="text/css" media="all"/>
<link href='http://fonts.googleapis.com/css?family=Carrois+Gothic+SC' rel='stylesheet' type='text/css'>

<link rel="stylesheet" href="css/wxug_core.css">		  
<link rel="stylesheet" href="css/wxug_omnibus.css">

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
		
		setInterval('updateDeviceTempDiv()', 1000); // refresh div after 1 secs		
		setInterval('updateWeatherDiv()', 300000); // refresh div after 5 mins
		
		function updateWeatherDiv() {			
			$.get('./inc/weatherDiv.jsp', function(data) {
			  $('#divWeather').html(data);
			});
		}
						
		
		function updateDeviceTempDiv() {			
			$.get('./inc/deviceTempDiv.jsp', function(data) {
			  $('#divDeviceTemp').html(data);
			});
		}
		
		updateWeatherDiv();		
		updateDeviceTempDiv();
		
		
		
	</script>
     
	<div class="wrap">	 
	      <div class="header">
	      	  <div class="header_top">
					  <div class="menu">
						  <a class="toggleMenu" href="#"><img src="images/nav.png" alt="" /></a>
							<ul class="nav">
								<li><a href="index.jsp"><i><img src="images/settings.png" alt="" /></i>Home</a></li>
								<li><a href="irrigation.jsp"><i><img src="images/user.png" alt="" /></i>Irrigation</a></li>
								<li class="active"><a href="temperatures.jsp"><i><img src="images/mail.png" alt="" /></i>Temperatures</a></li>
								<!-- 
								<li><a href="lights.jsp"><i><img src="images/mail.png" alt="" /></i>Lights</a></li>
								 -->
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
	    	 
	    	<div class="column_left" id="divWeather">	
	    	 	    	          	    	    				    		 
			</div>
	
	
	    
	    		
	    		 
	  		 <!--  </div> column_left -->
	  		
            <div class="column_middle">
            
		         
		         <div id="divDeviceTemp">            	
												   				  
				   
             	 </div>	
             	              	 
             	 
             	 
		         
		    		         	          
		         	           
		       
    	    </div>
    	    
           
    	<div class="clear"></div>
 	 </div>
   </div>
   
  		 <div class="copy-right">
				<p>� 2016 Willem Visser, Version 1.50</p>
	 	 </div>   
</body>
</html>

