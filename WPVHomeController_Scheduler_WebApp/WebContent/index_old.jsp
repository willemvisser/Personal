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
		<title>WPVHomeController_SchedulerWebApp 0.1</title>
	</head>
	<body>
	
		<%
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
				
				HashMap<String, String> params = new HashMap<String, String>();
				params.put("command", "xon");
				params.put("boardId", "1");
				params.put("ouputId", "D");
				params.put("pin", request.getParameter("valve"));
				newJobDto.setParams(params);
				
				WPVHomeControllerScheduler.INSTANCE.addJob(newJobDto, WPVHomeControllerScheduler.INSTANCE.getGeneralJobPropertiesMap());
				
				//Adding the stop event
				newJobDto.setName("OnceOff_QuickEvent_Off_" + (cal.getTime().getTime()) );
				params.put("command", "xoff");
				//out.println("<br/>Min: " + Integer.valueOf(request.getParameter("time")) + "<br/>");
				
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
	
	
		<h2>Weather <a href="weather.jsp">(Full Weather)</a></h2>
		<br/>
		
		<table>
		<tr>		
		<%
			Forecast10DayDTO forecast10DayDTO = WeatherService.INSTANCE.get10DayForecast();
			for (int i=0; i<6; i++) {
				ForecastDayDTO forecast = forecast10DayDTO.getForecasts().get(i);
				out.print("<td>");
				out.print(forecast.getTitle());
				out.print("</br>");
				out.print("<img src='");
				out.print(forecast.getIconUrl());
				out.print("'/>");
				out.print("</br>");
				out.print(forecast.getFcText());
				out.println("</td>");
			}
		%>
		</tr>
		</table>
		<br/>
		<h2>Irrigation (<a href="irrigation.jsp">Irrigation</a>)</h2>
		
		
		
		<p align="right">Version 1.0</p>
	</body>
</html>