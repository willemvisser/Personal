package za.co.willemvisser.wpvhomecontroller.weather.dto;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Element;

import za.co.willemvisser.wpvhomecontroller.util.DateUtil;

public class Forecast3HourDTO {

	private String hour;
	private String day;		//Date, e.g. "2019-07-20"
	
	

	private String icon;
	private String iconUrl;
	private String summaryMain;
	private String summaryDescription;
	private String high;	
	private String low;
	private String windSpeed;
	private String windDirection;
	private String rain3H;
	private String cloudCover;		//Percentage		

	public Forecast3HourDTO() {
		super();
	}		
	
	public Forecast3HourDTO(XPath xPath, Element eElement) throws DOMException, ParseException, XPathExpressionException {
	
	}
	
	/**
	 * @return a statis "No Data" DTO
	 */
	public static Forecast3HourDTO buildNoDataAvailableDTO() {
		Forecast3HourDTO dto = new Forecast3HourDTO();
		
		dto.setIcon("n.a.");
		dto.setIconUrl("n.a.");
		
		return dto;
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
	
	public String getDay() {
		return day;
	}
	
	/**
	 * @param dt_text 2019-07-19 03:00:00
	 * @throws ParseException 
	 */
	public void setDayAndHour(String dt_text) throws ParseException {		
		this.day = DateUtil.INSTANCE.getDayInShortFormat(dt_text);
		this.hour = DateUtil.INSTANCE.getHourInShortFormat(dt_text);
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
	
	public String getHour() {
		return hour;
	}

	public void setHour(String hour) {
		this.hour = hour;
	}

	public String getSummaryMain() {
		return summaryMain;
	}

	public void setSummaryMain(String summaryMain) {
		this.summaryMain = summaryMain;
	}

	public String getSummaryDescription() {
		return summaryDescription;
	}

	public void setSummaryDescription(String summaryDescription) {
		this.summaryDescription = summaryDescription;
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

	public String getRain3H() {
		return rain3H;
	}

	public void setRain3H(String rain3h) {
		rain3H = rain3h;
	}

	public String getCloudCover() {
		return cloudCover;
	}

	public void setCloudCover(String cloudCover) {
		this.cloudCover = cloudCover;
	}
	
	/**
	 * @return JSON view of the DTO (and yes - I should go find a utility to do this)
	 */
	public StringBuffer toJSON() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("{");
		buffer.append("\"day\":");	buffer.append(getDay());
		buffer.append("\"hour\":");	buffer.append(getHour());
		buffer.append("\"cloudCover\":");	buffer.append(getCloudCover());
		buffer.append("}");
		
		return buffer;
	}
	
}
