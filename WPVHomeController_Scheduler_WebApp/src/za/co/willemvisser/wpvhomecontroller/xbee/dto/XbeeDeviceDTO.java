package za.co.willemvisser.wpvhomecontroller.xbee.dto;

import java.io.Serializable;

/**
 * @author willemv
 *
 *	This represents a "device" attached to a xbee IO port - can be analog or digital
 */
public class XbeeDeviceDTO implements Serializable {
	
	private static final long serialVersionUID = -1100388804552989662L;
	
	private String id;
	private String name;
	private int port;		
	private boolean digital;
	private String type;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public boolean isDigital() {
		return digital;
	}
	public void setDigital(boolean digital) {
		this.digital = digital;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}		
	
	@Override
	public String toString() {
		return getType() + ", " + getId() + ", " + getName() + ", " + getPort();
	}

}
