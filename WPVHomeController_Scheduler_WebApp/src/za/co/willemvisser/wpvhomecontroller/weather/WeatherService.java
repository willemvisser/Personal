package za.co.willemvisser.wpvhomecontroller.weather;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.log4j.Logger;

import za.co.willemvisser.wpvhomecontroller.config.ConfigController;
import za.co.willemvisser.wpvhomecontroller.config.dto.XbeeConfigDeviceDTO;
import za.co.willemvisser.wpvhomecontroller.weather.dto.Forecast10DayDTO;
import za.co.willemvisser.wpvhomecontroller.weather.dto.ForecastDayDTO;
import za.co.willemvisser.wpvhomecontroller.xbee.XbeeController;

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
	private final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm' on 'MMM dd', 'yyyy");
	
	private WeatherService() {}
	
	private static final String forecast10DayUrl = "http://api.wunderground.com/api/58f4b5045e2ce60c/forecast10day/q/locid:SFWP0402.json";
	
	// JSON Node names
    private static final String TAG_FORECAST = "forecast";
    private static final String TAG_TXTFORECAST = "txt_forecast";
    private static final String TAG_FORECASTDAYS = "forecastday";
    private static final String TAG_SIMPLEFORECAST = "simpleforecast";
		    
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
	                JSONArray  simpleForecastArray = jsonObj.getJSONObject(TAG_FORECAST).getJSONObject(TAG_SIMPLEFORECAST).getJSONArray(TAG_FORECASTDAYS);
	                
	                //System.out.println("******\r\nResponse:\r\n\r\n" + responseStr);
	                for (int i=0; i<10; i++) {
	                	try {	                		
	                		String title = forecastArray.getJSONObject(i).getString("title");
		                	
		                	ForecastDayDTO forecastDayDTO = new ForecastDayDTO();	                	
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

    /**
     * @return Today's forecast
     */
    public synchronized ForecastDayDTO getForecastForToday() {
    	return get10DayForecast().getForecasts().get(0);
    }
    
    /**
     * @return Tomorrow's forecast
     */
    public synchronized ForecastDayDTO getForecastForTomorrow() {
    	return get10DayForecast().getForecasts().get(1);
    }
    
    public String getLastUpdatedString() {
    	return sdf.format(lastUpdated10DayForecast);
    }
    
    /**
     * @return the Outside temperature as a String
     */
    public String getOutsideTemperatureAsString() {
    	try {
    		String deviceId = ConfigController.INSTANCE.getGeneralProperty(ConfigController.PROPERTY_OUTSIDE_TEMPID).getValue();    		
    		XbeeConfigDeviceDTO deviceDTO = XbeeController.INSTANCE.getDeviceWithID(deviceId);    		
    		return Double.toString( deviceDTO.getAnalogValue().intValue()/10.0 );    	    	
    	} catch (Exception e) {
    		return "n.a.";
    	}
    }
    
    /**
     * @return the House (Inside) temperature as a String
     */
    public String getHouseTemperatureAsString() {
    	try {
    		String deviceId = ConfigController.INSTANCE.getGeneralProperty(ConfigController.PROPERTY_INSIDE_TEMPID).getValue();    		
    		XbeeConfigDeviceDTO deviceDTO = XbeeController.INSTANCE.getDeviceWithID(deviceId);    		
    		return Double.toString( deviceDTO.getAnalogValue().intValue()/10.0 );    	    	
    	} catch (Exception e) {
    		return "n.a.";
    	}
    }
}
