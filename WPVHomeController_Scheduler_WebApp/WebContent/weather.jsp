<%@page import="za.co.willemvisser.wpvhomecontroller.weather.dto.ForecastDayDTO"%>
<%@page import="za.co.willemvisser.wpvhomecontroller.weather.dto.Forecast10DayDTO"%>
<%@page import="za.co.willemvisser.wpvhomecontroller.weather.WeatherService"%>
<html>
	<head>
		<title>WPVHomeController_SchedulerWebApp 0.1</title>
	</head>
	<body>
		<h2>Full Weather</h2>
		<table>
		<tr>		
		<%
			Forecast10DayDTO forecast10DayDTO = WeatherService.INSTANCE.get10DayForecast();
			for (int i=0; i<8; i++) {
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
		<a href="index.jsp">Home</a>
	</body>
</html>