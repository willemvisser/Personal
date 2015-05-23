<!--A Design by W3layouts
Author: W3layout
Author URL: http://w3layouts.com
License: Creative Commons Attribution 3.0 Unported
License URL: http://creativecommons.org/licenses/by/3.0/
-->
<!DOCTYPE HTML>
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
<title>WPV Home Controller | Lights</title>
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
	
		if (request.getParameter("action") != null && request.getParameter("action").equals("on")) {
			HttpUtil.INSTANCE.doHttpGet("http://192.168.1.202:8080/WPVHomeController_Scheduler_WebApp/xbee/xon/" + request.getParameter("id") + "/"  + request.getParameter("portAddress") + "/"  + request.getParameter("port"));
		} else if (request.getParameter("action") != null && request.getParameter("action").equals("off")) {
			HttpUtil.INSTANCE.doHttpGet("http://192.168.1.202:8080/WPVHomeController_Scheduler_WebApp/xbee/xoff/" + request.getParameter("id") + "/"  + request.getParameter("portAddress") + "/"  + request.getParameter("port"));
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
								<li class="active"><a href="lights.jsp"><i><img src="images/mail.png" alt="" /></i>Lights</a></li>
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
	    
	    		
	    		 
	  		</div> <!-- column_left -->
	  		
            <div class="column_middle">
              <div class="column_middle_grid1">
		         <div class="column_right_grid calender"  style="margin-top:0px">
                      <div class="cal1"> </div>
				 </div>
		         
		       </div>
		         	           		         	           		       
    	    </div>	<!-- column middle -->
    	    
    	    
    	    
            <div class="column_right">            	
				
				<div class="menu_box">
		    		 	 <h3>Outside</h3>
		    		 	   <div class="menu_box_list">
					      		<ul>
					      		<%
					      	
					      	HashMap<XBeeAddress64, XbeeConfigDTO> xbeeMap = XbeeController.INSTANCE.getXbeeDeviceMap();
				      		for (XbeeConfigDTO xbeeConfigDTO : xbeeMap.values()) {
				      			%>
			      				<div class="sublistmenu_green">
			      					<h4><%=xbeeConfigDTO.getAddress()%> -> <%=xbeeConfigDTO.getName()%></h4>
			      				</div>
				      			<% 
				      			for (XbeeConfigDeviceDTO xbeeConfigDeviceDTO : xbeeConfigDTO.getDeviceList()) {
				      				if (xbeeConfigDeviceDTO.getType().equals("Light") ) {
				      				%>
				      				<div class="sublistmenu_blue">				      					 
				      					
				      					<%				      					
				      					if (xbeeConfigDeviceDTO.isEnabled()) {
				      						%>
								      		<div class="simplemenu_green"><h4><a href="lights.jsp?id=<%=xbeeConfigDTO.getId()%>&portAddress=<%=xbeeConfigDeviceDTO.getPortAddress()%>&port=<%=xbeeConfigDeviceDTO.getPort()%>&action=off"><%=xbeeConfigDeviceDTO.getName()%></a></h4></div>	
								      	<% 
								      	} else {
								      	%>
								      		<div class="simplemenu_black"><h4><a href="lights.jsp?id=<%=xbeeConfigDTO.getId()%>&portAddress=<%=xbeeConfigDeviceDTO.getPortAddress()%>&port=<%=xbeeConfigDeviceDTO.getPort()%>&action=on"><%=xbeeConfigDeviceDTO.getName()%></a></h4></div>								      		
								      	<%	
				      					}
				      					%>
				      					
				      					</h4>
				      				</div>
					      				
					      			<%
				      				}
				      			}
				      		}
					      	%>
					      	
					      	<!-- 
					      			<div class="simplemenu_black"><h4><a href="lights.jsp?id=pathlight&action=off">Path Lights [Off]</a></h4></div>
					      			<div class="simplemenu_green"><h4><a href="lights.jsp?id=pathlight&action=on">Path Lights [On]</a></h4></div>
					      	 -->
					    		</ul>
					      </div>
		    	</div>
				   
				  
				   
             </div>
    	<div class="clear"></div>
 	 </div>
   </div>
   
  		 <div class="copy-right">
				<p>© 2015 Willem Visser, Version 1.41</p>
	 	 </div>   
</body>
</html>

