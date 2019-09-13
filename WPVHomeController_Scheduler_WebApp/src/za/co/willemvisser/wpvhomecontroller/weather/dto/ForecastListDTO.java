package za.co.willemvisser.wpvhomecontroller.weather.dto;

import java.util.LinkedList;
import java.util.List;

public class ForecastListDTO {

	//http://api.wunderground.com/api/58f4b5045e2ce60c/forecast10day/q/locid:SFWP0402.xml
	private List<Forecast3HourDTO> forecasts = null;
	private String city;
		
	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public ForecastListDTO() {
		super();
		forecasts = new LinkedList<Forecast3HourDTO>();
	}
	
	public void addForecast(Forecast3HourDTO forecastDayDTO) {
		forecasts.add(forecastDayDTO);
	}

	public List<Forecast3HourDTO> getForecasts() {
		return forecasts;
	}

	public void setForecasts(List<Forecast3HourDTO> forecasts) {
		this.forecasts = forecasts;
	}
	
		
}
