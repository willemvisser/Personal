package za.co.willemvisser.wpvhomecontroller.weather.dto;

import java.text.ParseException;

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
	
	public ForecastDayDTO() {
		super();
	}
	
	public ForecastDayDTO(XPath xPath, Element eElement) throws DOMException, ParseException, XPathExpressionException {
	
	}
	public int getPeriod() {
		return period;
	}
	public void setPeriod(int period) {
		this.period = period;
	}
	public String getIcon() {
		return icon;
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
	
	
}
