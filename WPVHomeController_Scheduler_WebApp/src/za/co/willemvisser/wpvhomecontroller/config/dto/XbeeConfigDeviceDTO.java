package za.co.willemvisser.wpvhomecontroller.config.dto;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement(name = "device")
@XmlAccessorType (XmlAccessType.FIELD)
public class XbeeConfigDeviceDTO implements Serializable {

	private static final long serialVersionUID = 2024817628229583327L;
	
	public static final String TYPE_IRRIGATION = "Irrigation";
	public static final String TYPE_PUMP = "Pump";
	public static final String TYPE_LIGHT = "Light";
	public static final String TYPE_TEMPERATURE = "Temperature";
	public static final String TYPE_INFRARED = "Infrared";
	public static final String TYPE_TOGGLESWITCH = "ToggleSwitch";
	
	private String id;
	private String name;
	private String portAddress;
	private String port;
	private String logicalPort;
	private String type;
	private boolean digital;
	private String linkedDeviceId;
	
	@XmlTransient
	private boolean enabled;
	
	@XmlTransient
	private Integer analogValue;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public boolean isEnabled() {		
		return enabled;	
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	public boolean isDigital() {
		return digital;
	}
	public void setDigital(boolean digital) {
		this.digital = digital;
	}
	public Integer getAnalogValue() {
		return analogValue;
	}
	public void setAnalogValue(Integer analogValue) {
		this.analogValue = analogValue;
	}
	public String getPortAddress() {
		return portAddress;
	}
	public void setPortAddress(String portAddress) {
		this.portAddress = portAddress;
	}
	public String getLogicalPort() {
		return logicalPort;
	}
	public void setLogicalPort(String logicalPort) {
		this.logicalPort = logicalPort;
	}
	public String getLinkedDeviceId() {
		return linkedDeviceId;
	}
	public void setLinkedDeviceId(String linkedDeviceId) {
		this.linkedDeviceId = linkedDeviceId;
	}

}
