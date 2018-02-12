<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.GregorianCalendar"%>
<%@page import="java.util.Calendar"%>
<%@page import="java.util.Date"%>
<%@page import="za.co.willemvisser.wpvhomecontroller.scheduler.job.dto.JobDTO"%>
<%@page import="za.co.willemvisser.wpvhomecontroller.scheduler.WPVHomeControllerScheduler"%>
<%@page import="za.co.willemvisser.wpvhomecontroller.scheduler.job.dto.JobTriggerDTO"%>
<%@page import="java.util.List"%>
<%@page import="za.co.willemvisser.wpvhomecontroller.weather.dto.ForecastDayDTO"%>
<%@page import="za.co.willemvisser.wpvhomecontroller.weather.dto.Forecast10DayDTO"%>
<%@page import="za.co.willemvisser.wpvhomecontroller.weather.WeatherService"%>
<html>
	<head>
		<title>WPV Home Controller | Events</title>
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
	
		<%
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");			
		%>	
		
		<jsp:include page="inc/menuDiv.jsp">
			<jsp:param name="active" value="Events" />
		</jsp:include>
		
		<div class="main">  
		
	    	<div class="wrap">	    			
				<div class="column_left">	          
		    		 <jsp:include page="inc/menuEventsDiv.jsp">
						<jsp:param name="active" value="Today" />
					 </jsp:include>
		    	</div>
			</div>  <!-- wrap -->
		
		
			<div class="column_middle"  style="width:52%">
              <div class="column_middle_grid1">
	    				      
	    
	    		<div class="weather" style="margin-top:0px">
		        	<h3><i><img src="images/location.png" alt="" /> </i> Today's Scheduled Irrigation Times</h3>
		            
					
					<div class="temp_list">
				    	<ul>
				    	
				    	<%
						List<JobTriggerDTO> jobTriggerList = WPVHomeControllerScheduler.INSTANCE.listJobTriggers();
						for (int i=0; i<jobTriggerList.size(); i++) {
							JobTriggerDTO jobTriggerDTO = jobTriggerList.get(i);							
							%>
							<li><a href="#"><span class="day_name"><%=jobTriggerDTO.getJobDataMap().getString("job.name")%> </span>
							<!-- 
							<span class="day_name"> | <%=jobTriggerDTO.getJobDataMap().getString("command")%></span>
							<span class="day_name"> | <%=jobTriggerDTO.getJobDataMap().getString("ouputId")%><%=jobTriggerDTO.getJobDataMap().getString("pin")%></span>
							 --> 
				  			<label class="digits"><%=sdf.format(jobTriggerDTO.getTrigger().getNextFireTime()) %>
				  			
				  			<i>
				  			<%
				  				if (jobTriggerDTO.getJobDataMap().getString("command") != null) {
					  				if (jobTriggerDTO.getJobDataMap().getString("command").equals("xon")) {
					  					out.print("<img src=\"images/start-icon.png\" alt=\"\" />");	
					  				} else if (jobTriggerDTO.getJobDataMap().getString("command").equals("xoff")) {
					  					out.print("<img src=\"images/stop-red-icon.png\" alt=\"\" />");
					  				}
				  				} else {
				  					out.print("<img src=\"images/start-icon.png\" alt=\"\" />");
				  				}
				  			%>				  			
				  			</i></label>				  			
				  			<div class="clear"></div></a></li>
							<% 
							
						}
						%>
				    	
						  		    
				    	</ul>
				      </div>	<!-- temp_list -->
		          </div>	<!-- weather -->
	    		 
	  		</div> <!-- column_middle_grid1 -->
	  		</div> <!-- column_middle -->
	
 

		</div> <!-- main -->
		
		<!-- 
		<div class="copy-right">
				<p>© 2015 Willem Visser, Version 1.0</p>
	 	 </div> 
		 -->
		 
	</body>
</html>