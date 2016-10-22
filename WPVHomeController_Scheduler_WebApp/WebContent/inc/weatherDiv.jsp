<%@page import="za.co.willemvisser.wpvhomecontroller.weather.WeatherService"%>
<div id="location"></div>
			
			<div class="row">
				<div class="small-12 columns">
		
				</div>
		
				<div class="small-3 columns">
					<div id="curIcon"><img src="//icons.wxug.com/i/c/v4/<%=WeatherService.INSTANCE.getForecastForToday().getIcon() %>.svg" alt="Partly Cloudy" class="wx-data" data-station="" data-variable="icon_url" /></div>
					<!-- 
					<div id="curCond" class="wx-data" data-station="" data-variable="condition"><span class="wx-value">Partly Cloudy</span></div>
					 -->
				</div>
		
				<div class="small-6 columns">
					<div id="curTemp" style="color: #feae3c;">
						<span class="wx-data" data-station="" data-variable="temperature">
							<span class="wx-value"><%=WeatherService.INSTANCE.getOutsideTemperatureAsString() %></span>
							<span class="wx-unit">&deg;C</span>
						</span>
					</div>
		
					<div id="curFeel">
						<span class="wx-label" style="color: #fff;">House temp</span>
						<span style="color: #feae3c;">
							<span class="wx-data" data-station="" data-variable="feelslike">
								<span class="wx-value"><%=WeatherService.INSTANCE.getHouseTemperatureAsString() %></span>
								<span class="wx-unit">&deg;C</span>
							</span>
						</span>
					</div>
				</div>	
				
											
					
		</div>
	
	
	<div id="wx-quickie" class="wx-data" data-station="" data-variable="weather_quickie">
		<p id="precip-inline"></p>
		<p class="wx-value">
		</p>
	</div>
	
	<div class="row collapse" id="todaySummary">
		<div class="small-6 columns">						
			<span style="color: #feae3c;" class="today" title="Today's weather summary">Today</span>			
			<div>High <span style="color: #feae3c;"><%=WeatherService.INSTANCE.getForecastForToday().getHigh()%> &deg;C</span><span class="split">|</span> Low <span style="color: #feae3c;"><%=WeatherService.INSTANCE.getForecastForToday().getLow()%> &deg;C</span></div>
			<div><%=WeatherService.INSTANCE.getForecastForToday().getPrecipitationChance()%>% Chance of Precip.</div>
			<div>Precip. <%=WeatherService.INSTANCE.getForecastForToday().getPrecipitation()%> mm</div>
		</div>
		
		<div class="small-6 columns">			
			<span style="color: #feae3c;" class="today" title="Tomorrow's weather summary">Tomorrow</span>			
			<div>High <span style="color: #feae3c;"><%=WeatherService.INSTANCE.getForecastForTomorrow().getHigh()%> &deg;C</span><span class="split">|</span> Low <span style="color: #feae3c;"><%=WeatherService.INSTANCE.getForecastForTomorrow().getLow()%> &deg;C</span></div>
			<div><%=WeatherService.INSTANCE.getForecastForTomorrow().getPrecipitationChance()%>% Chance of Precip.</div>
			<div>Precip. <%=WeatherService.INSTANCE.getForecastForTomorrow().getPrecipitation()%> mm</div>
		</div>
		
	</div>
		
				

		<!-- TIME --> 		
		<div id="location">
			<br/>		
			<div class="local-time"> <span style="color: #11a8ab;"><i class="fi-clock"></i> <%=WeatherService.INSTANCE.getLastUpdatedString()%></span></div>					
		
		<!--
		</div>
		
		 -->
		</div>