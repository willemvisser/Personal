<!--A Design by W3layouts
Author: W3layout
Author URL: http://w3layouts.com
License: Creative Commons Attribution 3.0 Unported
License URL: http://creativecommons.org/licenses/by/3.0/
-->
<!DOCTYPE HTML>
<%@page import="za.co.willemvisser.wpvhomecontroller.config.ConfigController"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="za.co.willemvisser.wpvhomecontroller.config.dto.XbeeConfigDeviceDTO"%>
<%@page import="za.co.willemvisser.wpvhomecontroller.config.dto.XbeeConfigDTO"%>
<%@page import="com.rapplogic.xbee.api.XBeeAddress64"%>
<%@page import="za.co.willemvisser.wpvhomecontroller.xbee.dto.XbeeDTO"%>
<%@page import="za.co.willemvisser.wpvhomecontroller.util.HttpUtil"%>
<%@page import="java.util.HashMap"%>
<%@page import="za.co.willemvisser.wpvhomecontroller.xbee.XbeeController"%>
<%@page import="za.co.willemvisser.wpvhomecontroller.weather.dto.ForecastDayDTO"%>
<%@page import="za.co.willemvisser.wpvhomecontroller.weather.WeatherService"%>
<%@page import="za.co.willemvisser.wpvhomecontroller.weather.dto.Forecast10DayDTO"%>
<head>
<title>WPV Home Controller | Settings</title>
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

	<%		
		String action = request.getParameter("action");
		if (action != null && action.equals("reload")) {
			ConfigController.INSTANCE.reloadRemoteXbeeConfig();
			XbeeController.INSTANCE.loadXbeeConfig(ConfigController.INSTANCE.getXbeeConfigsDTO());	
		} else if (action != null && action.equals("discover")) {
			XbeeController.INSTANCE.discoverXbeeRing();
		}

	%>
	
	<script type="text/javascript">
		setInterval('updateXbeeStatusDiv()', 1000); // refresh div after 1 secs
		setInterval('updateDeviceStatusDiv()', 1000); // refresh div after 1 secs
	   
		function updateXbeeStatusDiv() {			
			$.get('./inc/xbeeStatusDiv.jsp', function(data) {
			  $('#divXbeeStatus').html(data);
			});
		}
		
		function updateDeviceStatusDiv() {			
			$.get('./inc/deviceStatusDiv.jsp', function(data) {
			  $('#divDeviceStatus').html(data);
			});
		}
		
		updateDeviceStatusDiv();
		updateXbeeStatusDiv();
	</script>
	     
	    <div class="wrap">	 
	      <div class="header">
	      	  <div class="header_top">
					  <div class="menu">
						  <a class="toggleMenu" href="#"><img src="images/nav.png" alt="" /></a>
							<ul class="nav">
								<li><a href="index.jsp"><i><img src="images/settings.png" alt="" /></i>Home</a></li>
								<li><a href="irrigation.jsp"><i><img src="images/user.png" alt="" /></i>Irrigation</a></li>
								<li><a href="lights.jsp"><i><img src="images/mail.png" alt="" /></i>Lights</a></li>
								<li class="active"><a href="settings.jsp"><i><img src="images/settings.png" alt="" /></i>Settings</a></li>
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
	    
	    		<div class="menu_box">
		    		 	 <h3>Actions</h3>
		    		 	   <div class="menu_box_list">
					      		<ul>
							  		<li class="active"><a href="#" class="account_settings"><span>All Devices</span></a></li>
							  		<li><a href="eventLog.jsp" class="messages"><span>Event Log</span><div class="clear"></div></a></li>
							  		<li><a href="settings.jsp?action=reload" class="statistics"><span>Reload Config</span><div class="clear"></div></a></li>
							  		<li><a href="settings.jsp?action=discover" class="statistics"><span>Discover Network</span><div class="clear"></div></a></li>					  	
					    		</ul>
					      </div>
		    	</div>
	    		 
	  		</div> <!-- column_left -->
	  		
            <div class="column_middle" id="divDeviceStatus">
              
		         	           		         	           		       
    	    </div>	<!-- column middle -->
    	    
    	    
    	    
            <div class="column_right" id="divXbeeStatus">            	
				
  				   
             </div>
             
             
    	<div class="clear"></div>
 	 </div>
   </div>
   
  		 <div class="copy-right">
				<p>© 2015 Willem Visser, Version 1.41</p>
	 	 </div>   
</body>
</html>

