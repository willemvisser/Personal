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