package za.co.willemvisser.wpvhomecontroller.weather.dto;

import java.util.LinkedList;
import java.util.List;

public class Forecast10DayDTO {

	//http://api.wunderground.com/api/58f4b5045e2ce60c/forecast10day/q/locid:SFWP0402.xml
	private List<ForecastDayDTO> forecasts = null;
		
	public Forecast10DayDTO() {
		super();
		forecasts = new LinkedList<ForecastDayDTO>();
	}
	
	public void addForecast(ForecastDayDTO forecastDayDTO) {
		forecasts.add(forecastDayDTO);
	}

	public List<ForecastDayDTO> getForecasts() {
		return forecasts;
	}

	public void setForecasts(List<ForecastDayDTO> forecasts) {
		this.forecasts = forecasts;
	}
	
		
}
