package za.co.willemvisser.wpvhomecontroller.weather;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import za.co.willemvisser.wpvhomecontroller.util.HttpUtil;
import za.co.willemvisser.wpvhomecontroller.weather.dto.Forecast3HourDTO;
import za.co.willemvisser.wpvhomecontroller.weather.dto.ForecastDayDTO;
import za.co.willemvisser.wpvhomecontroller.weather.dto.ForecastListDTO;

public enum OpenWeatherService {

	INSTANCE;
	
	private static final String WEATHERSERVICE_5DAY_URL = "http://api.openweathermap.org/data/2.5/forecast?id=3370352&APPID=4e2b821b3ef36699807f3e75163d5fd3&units=metric";
	private static final String WEATHERSERVICE_TODAY_URL = "http://api.openweathermap.org/data/2.5/weather?APPID=4e2b821b3ef36699807f3e75163d5fd3&units=metric&id=";
	
	static Logger log = Logger.getLogger(OpenWeatherService.class.getName());
	
	private static final SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm");
	
	//private Date lastUpdatedTodaysForecast = null;
	private Date lastUpdated5DayForecast = null;
	private ForecastListDTO cachedForecastListDTO = null;
	//private ForecastDayDTO cachedForecastDayDTO = null;
	
	private HashMap<String, Date> lastUpdatedMap = new HashMap<String, Date>();
	private HashMap<String, ForecastDayDTO> forecastTodayMap = new HashMap<String, ForecastDayDTO>();
	
	private OpenWeatherService() {}
	
	/**
	 * @param unixTimestamp
	 * @return a String with the human formatted time string
	 */
	private String convertUnixTimeToString(long unixTimestamp) {
		long javaTimestamp = unixTimestamp * 1000L;
		Date date = new Date(javaTimestamp);
		return timeFormatter.format(date);
	}
	
	
	/**
	 * @return the current weather conditions
	 */
	public synchronized ForecastDayDTO getCurrentForecast(String stationID) {
		
		Calendar calAWhileAgo = new GregorianCalendar();
		calAWhileAgo.add(Calendar.MINUTE, -15);
		
		String stationCacheID = "totday_" + stationID;
		
		if (lastUpdatedMap.get(stationCacheID) == null
				|| forecastTodayMap.get(stationCacheID) == null
				|| lastUpdatedMap.get(stationCacheID).before(calAWhileAgo.getTime())) {
		
//		if (lastUpdatedTodaysForecast == null 
//				|| cachedForecastDayDTO == null
//				|| lastUpdatedTodaysForecast.before(calAWhileAgo.getTime()) ) {
//		
			log.info("Refreshing daily weather data from OpenWeatherService ...");
			
			ForecastDayDTO forecastDayDTO = new ForecastDayDTO();			
			
			HttpClient httpclient = new DefaultHttpClient();
	        HttpResponse response;
	        String responseString = null;
	        try {
	        	//System.out.println("URI: " + uri[0] );
	        		        		        	
	            response = httpclient.execute(new HttpGet(WEATHERSERVICE_TODAY_URL+stationID));
	            StatusLine statusLine = response.getStatusLine();
	            if(statusLine.getStatusCode() == HttpStatus.SC_OK){
	                ByteArrayOutputStream out = new ByteArrayOutputStream();
	                response.getEntity().writeTo(out);
	                out.close();
	                responseString = out.toString();
	                log.info("Weather Today response: " + responseString);
	                
	                JSONObject jsonObj = new JSONObject(responseString);	
	                
	                forecastDayDTO.setStationID(String.valueOf(jsonObj.getInt("id")));
	                forecastDayDTO.setStationName(jsonObj.getString("name"));
	                
	                JSONArray jsonArrayWeather = jsonObj.getJSONArray("weather");	                
	                forecastDayDTO.setDescription(jsonArrayWeather.getJSONObject(0).getString("main"));
	                forecastDayDTO.setDescriptionExtended(jsonArrayWeather.getJSONObject(0).getString("description"));
	                forecastDayDTO.setIconName(jsonArrayWeather.getJSONObject(0).getString("icon"));
	                
	                JSONObject jsonObjectMain = jsonObj.getJSONObject("main");
	                forecastDayDTO.setTemp(jsonObjectMain.getDouble("temp"));
	                forecastDayDTO.setTempMin(jsonObjectMain.getDouble("temp_min"));
	                forecastDayDTO.setTempMax(jsonObjectMain.getDouble("temp_max"));
	                	                	                
	                JSONObject jsonObjectWind = jsonObj.getJSONObject("wind");
	                forecastDayDTO.setWindSpeed(jsonObjectWind.getDouble("speed"));
	                forecastDayDTO.setWindDegrees(jsonObjectWind.getInt("deg"));
	                
	                JSONObject jsonObjectCloud = jsonObj.getJSONObject("clouds");
	                forecastDayDTO.setCloudCover(jsonObjectCloud.getInt("all"));
	                
	                JSONObject jsonObjectSys = jsonObj.getJSONObject("sys");
	                forecastDayDTO.setSunrise( convertUnixTimeToString(jsonObjectSys.getLong("sunrise")));
	                forecastDayDTO.setSunset( convertUnixTimeToString(jsonObjectSys.getLong("sunset")) );	                	               
	               	                	                
	                forecastTodayMap.put(stationCacheID, forecastDayDTO);
	        		lastUpdatedMap.put(stationCacheID, new Date());
	                
	                //lastUpdatedTodaysForecast = new Date();
	                //cachedForecastDayDTO = forecastDayDTO;
	                
	                log.info("Today's Weather: " + forecastTodayMap.get(stationCacheID).toJSONString());
	                
	                
		            		            
	            } else {
	            	//StringBuffer strBuffer = HttpUtil.INSTANCE.getResponseContent( HttpUtil.INSTANCE.doHttpGet( forecast10DayUrl ) );
	            	log.warn("Could not retrieve weather info (nothing in response to process)");
	            }
	            
	            
	        } catch (Exception e) {
	        	log.error("Cannot get daily forecast from OpenWeatherOrg: " + e.toString() );
	        } finally {
	        	//try { httpclient.
	        }
	        
		} 
		
		return forecastTodayMap.get(stationCacheID);
		
	}
	
	/**
	 * 
	 * @return ForecastListDTO with 5 days worth of 3 hourly data
	 */
	public synchronized ForecastListDTO get5DayForecast() {
		
		Calendar calAnHourAgo = new GregorianCalendar();
		calAnHourAgo.add(Calendar.HOUR_OF_DAY, -1);
		
		if (lastUpdated5DayForecast == null 
				|| cachedForecastListDTO == null
				|| lastUpdated5DayForecast.before(calAnHourAgo.getTime()) ) {
		
			log.info("Refreshing weather data from OpenWeatherService ...");
			
			ForecastListDTO forecastListDTO = new ForecastListDTO();
			if (cachedForecastListDTO == null) {
				cachedForecastListDTO = forecastListDTO;		//If the cache is null, set it to the empty list at this point
			}
			
			HttpClient httpclient = new DefaultHttpClient();
	        HttpResponse response;
	        String responseString = null;
	        try {
	        	//System.out.println("URI: " + uri[0] );
	        		        		        	
	            response = httpclient.execute(new HttpGet(WEATHERSERVICE_5DAY_URL));
	            StatusLine statusLine = response.getStatusLine();
	            if(statusLine.getStatusCode() == HttpStatus.SC_OK){
	                ByteArrayOutputStream out = new ByteArrayOutputStream();
	                response.getEntity().writeTo(out);
	                out.close();
	                responseString = out.toString();
	                log.info("Weather Call response: " + responseString);
	                
	                JSONObject jsonObj = new JSONObject(responseString);
	                //JSONArray  forecastArray = jsonObj.getJSONObject(TAG_FORECAST).getJSONObject(TAG_TXTFORECAST).getJSONArray(TAG_FORECASTDAYS);
	                //JSONArray  simpleForecastArray = jsonObj.getJSONObject(TAG_FORECAST).getJSONObject(TAG_SIMPLEFORECAST).getJSONArray(TAG_FORECASTDAYS);
	                
	                JSONObject jsonArrayCity = jsonObj.getJSONObject("city");	                
	                forecastListDTO.setCity(jsonArrayCity.getString("name"));
	                System.out.println("City Name: " + forecastListDTO.getCity());
	                
	                JSONArray forecastArray = jsonObj.getJSONArray("list");
	                for (int i=0; i<forecastArray.length(); i++) {
	                	JSONObject obj = forecastArray.getJSONObject(i);
	                	
	                	Forecast3HourDTO dto = new Forecast3HourDTO();
	                	dto.setDayAndHour(obj.getString("dt_text"));
	                }
	                /*
	                
	                for (int i=0; i<10; i++) {
	                	try {	                		
	                		String title = forecastArray.getJSONObject(i).getString("title");
		                	
		                	Forecast3HourDTO forecastDayDTO = new Forecast3HourDTO();	                	
		                	forecastDayDTO.setTitle(title);
		                	forecastDayDTO.setIcon( forecastArray.getJSONObject(i).getString("icon") ) ;
		                	forecastDayDTO.setIconUrl( forecastArray.getJSONObject(i).getString("icon_url") ) ;
		                	forecastDayDTO.setFcText( forecastArray.getJSONObject(i).getString("fcttext_metric") );
		                	forecastDayDTO.setHigh( simpleForecastArray.getJSONObject(i).getJSONObject("high").getString("celsius") );
		                	forecastDayDTO.setLow( simpleForecastArray.getJSONObject(i).getJSONObject("low").getString("celsius") );		                	
		                	forecastDayDTO.setPrecipitation( Integer.toString( simpleForecastArray.getJSONObject(i).getJSONObject("qpf_allday").getInt("mm") ) );
		                	forecastDayDTO.setPrecipitationChance( forecastArray.getJSONObject(i).getString("pop") );
		                	forecastDayDTO.setWindDirection( simpleForecastArray.getJSONObject(i).getJSONObject("maxwind").getString("dir") );
		                	forecastDayDTO.setWindSpeed( Double.toString( simpleForecastArray.getJSONObject(i).getJSONObject("maxwind").getDouble("kph") ));
		                	
		                	forecast10DayDTO.addForecast(forecastDayDTO);
	                	} catch (Exception e) {
	        	    		log.error("Could not process forecast: " + e.toString());
	        	    		
	        	    		StringBuffer strBuffer = HttpUtil.INSTANCE.getResponseContent( HttpUtil.INSTANCE.doHttpGet( forecast10DayUrl ) );
	        	    	}
	        	    	
	                }
	                */
	                	                
	            	lastUpdated5DayForecast = new Date();
	            	cachedForecastListDTO = forecastListDTO;
		            		            
	            } else {
	            	//StringBuffer strBuffer = HttpUtil.INSTANCE.getResponseContent( HttpUtil.INSTANCE.doHttpGet( forecast10DayUrl ) );
	            	log.warn("Could not retrieve weather info (nothing in response to process)");
	            }
	            
	            
	        } catch (Exception e) {
	        	log.error("Cannot get forecast: " + e.toString() );
	        } 
	        
		} 
		
		return cachedForecastListDTO;
		        
        
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//String weatherURL = "http://api.openweathermap.org/data/2.5/forecast?id=3370352&APPID=4e2b821b3ef36699807f3e75163d5fd3&units=metric";
		OpenWeatherService.INSTANCE.getCurrentForecast("3370352");
	}

}
