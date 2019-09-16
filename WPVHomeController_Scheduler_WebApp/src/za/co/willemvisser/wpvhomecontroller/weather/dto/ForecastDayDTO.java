package za.co.willemvisser.wpvhomecontroller.weather.dto;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author willemv
 *
 *  OpenWeatherOrg Day Forecast
 */
public class ForecastDayDTO {

	private String description;	
	private String descriptionExtended;
	private String iconName;
	private double temp;
	private double tempMin;
	private double tempMax;
	private double windSpeed;
	private int windDegrees;
	private int cloudCover;
	private String sunrise;
	private String sunset;
	
	public ForecastDayDTO() {
		super();				
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescriptionExtended() {
		return descriptionExtended;
	}

	public void setDescriptionExtended(String descriptionExtended) {
		this.descriptionExtended = descriptionExtended;
	}

	public double getTemp() {
		return temp;
	}

	public void setTemp(double temp) {
		this.temp = temp;
	}

	public double getTempMin() {
		return tempMin;
	}

	public void setTempMin(double tempMin) {
		this.tempMin = tempMin;
	}

	public double getTempMax() {
		return tempMax;
	}

	public void setTempMax(double tempMax) {
		this.tempMax = tempMax;
	}

	public double getWindSpeed() {
		return windSpeed;
	}

	public void setWindSpeed(double windSpeed) {
		this.windSpeed = windSpeed;
	}

	public int getWindDegrees() {
		return windDegrees;
	}

	public void setWindDegrees(int windDegrees) {
		this.windDegrees = windDegrees;
	}

	public int getCloudCover() {
		return cloudCover;
	}

	public void setCloudCover(int cloudCover) {
		this.cloudCover = cloudCover;
	}

	public String getSunrise() {
		return sunrise;
	}

	public void setSunrise(String sunrise) {
		this.sunrise = sunrise;
	}

	public String getSunset() {
		return sunset;
	}

	public void setSunset(String sunset) {
		this.sunset = sunset;
	}

	public String getIconName() {
		return iconName;
	}

	public void setIconName(String iconName) {
		this.iconName = iconName;
	}
	
	public String toJSONString() throws JSONException {
		JSONObject forecastJSON = new JSONObject();
		forecastJSON.put("description", getDescription());
		forecastJSON.put("descriptionExtended", getDescriptionExtended());
		forecastJSON.put("iconName", getIconName());
		forecastJSON.put("temp", getTemp());
		forecastJSON.put("tempMin", getTempMin());
		forecastJSON.put("tempMax", getTempMax());
		forecastJSON.put("windSpeed", getWindSpeed());
		forecastJSON.put("windDegress", getWindDegrees());
		forecastJSON.put("cloudCover", getCloudCover());
		forecastJSON.put("sunrise", getSunrise());
		forecastJSON.put("sunset", getSunset());
		return forecastJSON.toString();
	}
}
