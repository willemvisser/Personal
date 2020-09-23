package za.co.willemvisser.wpvhomecontroller.property;

import java.util.Date;

public class PropertyDTO {

	private String key;
	private String value;
	private Date lastUpdated;
	
	public PropertyDTO(String key, String value) {
		super();
		this.key = key;
		this.value = value;
		this.lastUpdated = new Date();
	}
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public Date getLastUpdated() {
		return lastUpdated;
	}
	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}
	
}
