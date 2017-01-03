<!--A Design by W3layouts
Author: W3layout
Author URL: http://w3layouts.com
License: Creative Commons Attribution 3.0 Unported
License URL: http://creativecommons.org/licenses/by/3.0/
-->
<!DOCTYPE HTML>
<%@page import="za.co.willemvisser.wpvhomecontroller.config.dto.GeneralPropertyDTO"%>
<%@page import="java.util.ArrayList"%>
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
	
	
	     
	    <div class="wrap">	 
	      <div class="header">
	      	  <div class="header_top">
					  <div class="menu">
						  <a class="toggleMenu" href="#"><img src="images/nav.png" alt="" /></a>
							<ul class="nav">
								<li><a href="index.jsp"><i><img src="images/settings.png" alt="" /></i>Home</a></li>
								<li><a href="irrigation.jsp"><i><img src="images/user.png" alt="" /></i>Irrigation</a></li>
								<!-- 
								<li><a href="lights.jsp"><i><img src="images/mail.png" alt="" /></i>Lights</a></li>
								 -->
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
	    
	  		
            
              		<table>
              			<thead>
              				<tr>
              					<th>
              					Key
              					</th>
              					<th>
              					Value
              					</th>
              					<th>
              					Description
              					</th>
              				</tr>
              			</thead>
              			<tbody>
              				<%
              				ArrayList<GeneralPropertyDTO> propList = ConfigController.INSTANCE.getGeneralPropertiesDTO().generalPropertyList;
              				for (GeneralPropertyDTO dto : propList) {
              					out.println("<tr>");
              					out.print("<td><input size=\"30\" type=\"text\" name=\"key\" value=\"" + dto.getKey() + "\">"); 	out.println("</td>");
              					out.print("<td><input size=\"50\" type=\"text\" name=\"value\" value=\"" + dto.getValue() + "\">"); 	out.println("</td>");
              					out.print("<td><input size=\"120\" type=\"text\" name=\"description\" value=\"" + dto.getDescription() + "\">"); 	out.println("</td>");
              					out.println("<td><input type=\"submit\"></td>");
              					out.println("</tr>");
              				}
              				%>
              			</tbody>
              		</table>
		         	           		         	           		       
    	    
    	    
    	    
             
             
    	
 	 
   </div>
   
   <br/>
  		 <div class="copy-right">
				<p>© 2015 Willem Visser, Version 1.41</p>
	 	 </div>   
</body>
</html>

