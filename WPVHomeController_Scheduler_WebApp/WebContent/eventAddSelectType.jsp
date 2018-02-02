<%@page import="java.util.LinkedList"%>
<%@page import="za.co.willemvisser.wpvhomecontroller.scheduler.job.dto.JobParamDTO"%>
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
		<title>WPV Home Controller | Irrigation</title>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
		<link href="css/style.css" rel="stylesheet" type="text/css" media="all"/>
		<link href="css/nav.css" rel="stylesheet" type="text/css" media="all"/>
		 
		<link href="css/wpvstyle.css" rel="stylesheet" type="text/css" media="all"/>
				 
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
		  <script src="js/wpvpushbuttons.js"></script>
		  
		  <!----End Calender -------->
	</head>
	<body>
	
		<%
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		String action = request.getParameter("action");
		if (action != null && action.equals("irrigation_quickaddevent")) {
			out.println("<p><i>New irrigation event added!</i></p>");
			JobDTO newJobDto = new JobDTO();
			newJobDto.setClassName("za.co.willemvisser.wpvhomecontroller.scheduler.job.XbeeRemoteIrrigationCommandJob");
			newJobDto.setGroupName("Irrigation");
			Calendar cal = GregorianCalendar.getInstance();
			Date now = new Date();
			newJobDto.setName("OnceOff_QuickEvent_On_" + (cal.getTime().getTime()) );				
			newJobDto.setStartTime( cal.getTime() );
			
			cal.add(Calendar.SECOND, 30);
			StringBuffer strBuff = new StringBuffer();
			strBuff.append(cal.get(Calendar.SECOND));
			strBuff.append(" ");
			strBuff.append(cal.get(Calendar.MINUTE));
			strBuff.append(" ");
			strBuff.append(cal.get(Calendar.HOUR_OF_DAY));
			strBuff.append(" ");
			strBuff.append(cal.get(Calendar.DAY_OF_MONTH));
			strBuff.append(" ");
			strBuff.append(cal.get(Calendar.MONTH)+1);
			strBuff.append(" ? ");
			strBuff.append(cal.get(Calendar.YEAR));								
			
			newJobDto.setCronExpression(strBuff.toString());
			
			List<JobParamDTO> params = new LinkedList<JobParamDTO>();
			params.add(new JobParamDTO("command", "xon"));
			params.add(new JobParamDTO("boardId", "1"));
			params.add(new JobParamDTO("ouputId", "D"));
			params.add(new JobParamDTO("pin", request.getParameter("valve")));
			newJobDto.setParams(params);
			
			
			WPVHomeControllerScheduler.INSTANCE.addJob(newJobDto, WPVHomeControllerScheduler.INSTANCE.getGeneralJobPropertiesMap());
			
			//Adding the stop event
			newJobDto.setName("OnceOff_QuickEvent_Off_" + (cal.getTime().getTime()) );
			params = new LinkedList<JobParamDTO>();
			params.add(new JobParamDTO("command", "xoff"));
			params.add(new JobParamDTO("boardId", "1"));
			params.add(new JobParamDTO("ouputId", "D"));
			params.add(new JobParamDTO("pin", request.getParameter("valve")));
			newJobDto.setParams(params);			
			
			cal.add(Calendar.MINUTE,  Integer.valueOf(request.getParameter("time")) );
			strBuff = new StringBuffer();
			strBuff.append(cal.get(Calendar.SECOND));
			strBuff.append(" ");
			strBuff.append(cal.get(Calendar.MINUTE));
			strBuff.append(" ");
			strBuff.append(cal.get(Calendar.HOUR_OF_DAY));
			strBuff.append(" ");
			strBuff.append(cal.get(Calendar.DAY_OF_MONTH));
			strBuff.append(" ");
			strBuff.append(cal.get(Calendar.MONTH)+1);
			strBuff.append(" ? ");
			strBuff.append(cal.get(Calendar.YEAR));												
			newJobDto.setCronExpression(strBuff.toString());
			
			
			WPVHomeControllerScheduler.INSTANCE.addJob(newJobDto, WPVHomeControllerScheduler.INSTANCE.getGeneralJobPropertiesMap());
		}
		%>
	

		
		
		<div class="wrap">	 
	    	<div class="header">
	      		<div class="header_top">
					  <div class="menu">
						  <a class="toggleMenu" href="#"><img src="images/nav.png" alt="" /></a>
							<ul class="nav">
								<li><a class="original" href="index.jsp"><i><img src="images/settings.png" alt="" /></i>Home</a></li>
								<li class="active"><a href="irrigation.jsp"><i><img src="images/user.png" alt="" /></i>Events</a></li>
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
		    		 <div class="menu_box">
		    		 	 <h3>Actions</h3>
		    		 	   <div class="menu_box_list">
					      		<ul>
							  		<li><a href="#" class="account_settings"><span>Next Events</span></a></li>
							  		<li class="active"><a href="irrigationAddEvent.jsp" class="messages"><span>Add Event</span><div class="clear"></div></a></li>
							  		<li><a href="#" class="invites"><span>Stop Current Event</span><div class="clear"></div></a></li>
							  		<li><a href="#" class="events"><span>Clear Today</span><div class="clear"></div></a></li>							  		
							  		<li><a href="#" class="statistics"><span>Statistics</span></a></li>						  	
					    		</ul>
					      </div>
		    		 </div>
		    	</div>
			</div>  <!-- wrap -->
		
		
			<div class="column_middle"  style="width:52%">
              	<div class="column_middle_grid1">
	    				      
	    			<div class="column_right_grid sign-in">
				 	<div class="sign_in">					    					    
					    
					    <form action="eventAdd.jsp" method="POST">					    	
					 	    					 	    														
							
									
							<div class="button-group-navigation">							
							  <li><a id="btnZone4" class="btnZone" href="#" value="XbeeRemoteCommand">Direct Switch</a></li>
							  <li><a id="btnZone6" class="btnZone" href="#" value="WaterTankFillJob">Water Tank Fill Job</a></li>							  
							</div>		
							
							<input type="hidden" name="action" value="irrigation_quickaddevent"/>
							
							<br/><br/>							
			
					 		<input type="submit" class="my-button" value="Next">
					 	</form>					       				   
          		       </div>          		 	  
				   </div>
	    		
	    		 
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