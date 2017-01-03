package za.co.willemvisser.wpvhomecontroller.weather.dto;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Element;

public class ForecastDayDTO {

	private int period;
	private String icon;
	private String iconUrl;
	private String title;
	private String fcText;
	private String high;
	
	private String low;
	private String windSpeed;
	private String windDirection;
	private String precipitation;
	private String precipitationChance;				

	public ForecastDayDTO() {
		super();
	}		
	
	public ForecastDayDTO(XPath xPath, Element eElement) throws DOMException, ParseException, XPathExpressionException {
	
	}
	
	/**
	 * @return a statis "No Data" DTO
	 */
	public static ForecastDayDTO buildNoDataAvailableDTO() {
		ForecastDayDTO dto = new ForecastDayDTO();
		dto.setFcText("n.a.");
		dto.setHigh("n.a.");
		dto.setIcon("n.a.");
		dto.setIconUrl("n.a.");
		dto.setLow("n.a");
		dto.setPeriod(0);
		dto.setPrecipitation("n.a");
		dto.setPrecipitationChance("n.a");
		dto.setTitle("n.a");
		dto.setWindDirection("n.a.");
		dto.setWindSpeed("n.a.");
		return dto;
	}
	
	
	public String getHigh() {
		return high;
	}

	public void setHigh(String high) {
		this.high = high;
	}

	public String getLow() {
		return low;
	}

	public void setLow(String low) {
		this.low = low;
	}

	public String getWindSpeed() {
		return windSpeed;
	}

	public void setWindSpeed(String windSpeed) {
		this.windSpeed = windSpeed;
	}

	public String getWindDirection() {
		return windDirection;
	}

	public void setWindDirection(String windDirection) {
		this.windDirection = windDirection;
	}

	public String getPrecipitation() {
		return precipitation;
	}

	public void setPrecipitation(String precipitation) {
		this.precipitation = precipitation;
	}
	
	public int getPeriod() {
		return period;
	}
	public void setPeriod(int period) {
		this.period = period;
	}
	public String getIcon() {
		
	    Date date = new Date();        
	    
	    Calendar calEvening = Calendar.getInstance();
	    calEvening.setTime(new Date());
	    calEvening.set(Calendar.HOUR_OF_DAY, 19);
	    calEvening.set(Calendar.MINUTE, 30);
	    calEvening.set(Calendar.SECOND, 0);
	    
	    Calendar calMorning = Calendar.getInstance();
	    calMorning.setTime(new Date());
	    calMorning.set(Calendar.HOUR_OF_DAY, 7);
	    calMorning.set(Calendar.MINUTE, 0);
	    calMorning.set(Calendar.SECOND, 0);
	    
	    try {
	    	if (date.after(calEvening.getTime()) || date.before(calMorning.getTime()) ){
	    		return "nt_" + icon;
	    	} else {
	    		return icon;
	    	}
	    } catch (Exception e) {
	    	return icon;
	    }
		
		
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public String getIconUrl() {				
	    return iconUrl;	  				
	}
	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getFcText() {
		return fcText;
	}
	public void setFcText(String fcText) {
		this.fcText = fcText;
	}
	
	public String getPrecipitationChance() {
		return precipitationChance;
	}

	public void setPrecipitationChance(String precipitationChance) {
		this.precipitationChance = precipitationChance;
	}
	
	
}
