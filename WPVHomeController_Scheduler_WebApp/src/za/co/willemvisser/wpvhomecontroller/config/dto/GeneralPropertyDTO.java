package za.co.willemvisser.wpvhomecontroller.config.dto;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "generalproperty")
@XmlAccessorType (XmlAccessType.FIELD)
public class GeneralPropertyDTO implements Serializable {

	private static final long serialVersionUID = -905480048215959623L;
	
	public static final String PROP_XBEE_ENABLED = "XbeeEnabled";
	
	private String key;
	private String value;
	private String description;
	
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
		
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
}
