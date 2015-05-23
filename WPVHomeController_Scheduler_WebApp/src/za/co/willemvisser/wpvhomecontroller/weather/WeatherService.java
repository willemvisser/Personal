package za.co.willemvisser.wpvhomecontroller.weather;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.log4j.Logger;

import za.co.willemvisser.wpvhomecontroller.weather.dto.Forecast10DayDTO;
import za.co.willemvisser.wpvhomecontroller.weather.dto.ForecastDayDTO;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

public enum WeatherService {

	INSTANCE;
	
	static Logger log = Logger.getLogger(WeatherService.class.getName());
	
	private Date lastUpdated10DayForecast = null;
	private Forecast10DayDTO cachedForecast10DayDTO = null;
	
	private WeatherService() {}
	
	private static final String forecast10DayUrl = "http://api.wunderground.com/api/58f4b5045e2ce60c/forecast10day/q/locid:SFWP0402.json";
	
	// JSON Node names
    private static final String TAG_FORECAST = "forecast";
    private static final String TAG_TXTFORECAST = "txt_forecast";
    private static final String TAG_FORECASTDAYS = "forecastday";
	
	public synchronized Forecast10DayDTO get10DayForecast() {
		
		Calendar calAnHourAgo = new GregorianCalendar();
		calAnHourAgo.add(Calendar.HOUR_OF_DAY, -1);
		
		if (lastUpdated10DayForecast == null 
				|| cachedForecast10DayDTO == null
				|| lastUpdated10DayForecast.before(calAnHourAgo.getTime()) ) {
		
			log.info("Refreshing weather data...");
			
			Forecast10DayDTO forecast10DayDTO = new Forecast10DayDTO();
			
			HttpClient httpclient = new DefaultHttpClient();
	        HttpResponse response;
	        String responseString = null;
	        try {
	        	//System.out.println("URI: " + uri[0] );
	        	
	        	
	        	
	            response = httpclient.execute(new HttpGet(forecast10DayUrl));
	            StatusLine statusLine = response.getStatusLine();
	            if(statusLine.getStatusCode() == HttpStatus.SC_OK){
	                ByteArrayOutputStream out = new ByteArrayOutputStream();
	                response.getEntity().writeTo(out);
	                out.close();
	                responseString = out.toString();
	                log.debug("Weather Call response: " + responseString);
	                
	                JSONObject jsonObj = new JSONObject(responseString);
	                JSONArray  forecastArray = jsonObj.getJSONObject(TAG_FORECAST).getJSONObject(TAG_TXTFORECAST).getJSONArray(TAG_FORECASTDAYS);
	                
	                //System.out.println("******\r\nResponse:\r\n\r\n" + responseStr);
	                for (int i=0; i<forecastArray.length(); i++) {
	                	try {
		                	String title = forecastArray.getJSONObject(i).getString("title");
		                	
		                	ForecastDayDTO forecastDayDTO = new ForecastDayDTO();	                	
		                	forecastDayDTO.setTitle(title);
		                	forecastDayDTO.setIconUrl( forecastArray.getJSONObject(i).getString("icon_url") ) ;
		                	forecastDayDTO.setFcText( forecastArray.getJSONObject(i).getString("fcttext_metric") );
		                	
	//	                	URL url = new URL(weatherDayForecastDTO.getWeatherImageUrl());
	//	                	weatherDayForecastDTO.setWeatherBitmap( BitmapFactory.decodeStream(url.openConnection().getInputStream()) );
	//            	    	
	//	                	weatherOutlookForecastDTO.addNewForecast(weatherDayForecastDTO);
		                	forecast10DayDTO.addForecast(forecastDayDTO);
	                	} catch (Exception e) {
	        	    		log.error("Could not process forecast: " + e.toString());
	        	    	}
	                }
	            }
	            
	            lastUpdated10DayForecast = new Date();
	            cachedForecast10DayDTO = forecast10DayDTO;
	        } catch (Exception e) {
	        	log.error("Cannot get forecast: " + e.toString() );
	        }
	        
		} 
		
		return cachedForecast10DayDTO;
		        
        
	}
}
