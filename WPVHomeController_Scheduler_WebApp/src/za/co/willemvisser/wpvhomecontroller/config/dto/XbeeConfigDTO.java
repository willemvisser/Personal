package za.co.willemvisser.wpvhomecontroller.config.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.log4j.Logger;

import za.co.willemvisser.wpvhomecontroller.xbee.XbeeController;

import com.rapplogic.xbee.api.zigbee.ZNetRxIoSampleResponse;

@XmlRootElement(name = "xbee")
@XmlAccessorType (XmlAccessType.FIELD)
public class XbeeConfigDTO implements Serializable {

	private static final long serialVersionUID = -4429292811268844403L;
	
	static Logger log = Logger.getLogger(XbeeConfigDTO.class.getName());
	
	private String name;
	private String address;
	private String id;
	
	@XmlTransient
	private ZNetRxIoSampleResponse latestPortReadings;
	
	@XmlTransient
	private HashMap<Integer, Integer> rxResponseMap; 
		
	@XmlTransient
	private Date lastSync = null;
	
	@XmlElement(name = "device")
	@XmlElementWrapper(name="devices")
	private ArrayList<XbeeConfigDeviceDTO> deviceList; 
		
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public ArrayList<XbeeConfigDeviceDTO> getDeviceList() {
		if (deviceList == null) {
			deviceList = new ArrayList<XbeeConfigDeviceDTO>();
		}
		return deviceList;
	}
	public void setDeviceList(ArrayList<XbeeConfigDeviceDTO> deviceList) {
		this.deviceList = deviceList;
	}
	public ZNetRxIoSampleResponse getLatestPortReadings() {
		return latestPortReadings;
	}
	public void setLatestPortReadings(ZNetRxIoSampleResponse latestPortReadings) {
		this.latestPortReadings = latestPortReadings;
		this.lastSync = new Date();
		
		for (XbeeConfigDeviceDTO xbeeConfigDeviceDTO : getDeviceList()) {
			int devicePort = Integer.parseInt(xbeeConfigDeviceDTO.getLogicalPort());
			//log.debug("devicePort for Digital Setting: " + devicePort);
			if (xbeeConfigDeviceDTO.isDigital()) {	
				//log.debug("latestPortReadings.isDigitalOn(devicePort):  " + (latestPortReadings.isDigitalOn(devicePort) )); 
				if (latestPortReadings.isDigitalOn(devicePort) != null) {
					//Start with check if this device is infrared
					if (xbeeConfigDeviceDTO.getType().equals(XbeeConfigDeviceDTO.TYPE_INFRARED)) {
						if (xbeeConfigDeviceDTO.isEnabled() && !latestPortReadings.isDigitalOn(devicePort) ) {
							log.debug("Turning off Infrared for: " + xbeeConfigDeviceDTO.getName() );
						} else if (!xbeeConfigDeviceDTO.isEnabled() && latestPortReadings.isDigitalOn(devicePort) ) {
							log.debug("Turning on Infrared for: " + xbeeConfigDeviceDTO.getName() );
						}
						
						xbeeConfigDeviceDTO.setEnabled( latestPortReadings.isDigitalOn(devicePort) );
						
					} else if (xbeeConfigDeviceDTO.getType().equals(XbeeConfigDeviceDTO.TYPE_TOGGLESWITCH)) {
						
						if (!xbeeConfigDeviceDTO.isEnabled() && !latestPortReadings.isDigitalOn(devicePort)) {
							//OK button pressed, we should toggle the state now of the linked device
							log.debug("toggleswitch Pressed: " + xbeeConfigDeviceDTO.getId() + ", Port=" + xbeeConfigDeviceDTO.getPort() + 
									", Address=" + xbeeConfigDeviceDTO.getPortAddress());
							if (xbeeConfigDeviceDTO.getLinkedDeviceId() != null) {
								XbeeConfigDeviceDTO linkedXbeeDeviceDTP = XbeeController.INSTANCE.getDeviceWithID(xbeeConfigDeviceDTO.getLinkedDeviceId());								
								if (linkedXbeeDeviceDTP.isEnabled()) {
									log.info("Linked device is on, so turning it off");
									XbeeController.INSTANCE.switchXbeeDevice(linkedXbeeDeviceDTP, false);
								} else {
									log.info("Linked device is off, so turning it on");
									XbeeController.INSTANCE.switchXbeeDevice(linkedXbeeDeviceDTP, true);
								}
							} else {
								log.error("Button pressed, but no linked device found for device: " + xbeeConfigDeviceDTO.getId());
							}
							
						}
																		
						xbeeConfigDeviceDTO.setEnabled( !latestPortReadings.isDigitalOn(devicePort) );												
						
					} else {
						xbeeConfigDeviceDTO.setEnabled( latestPortReadings.isDigitalOn(devicePort) );
					}
					
					
				
				} else {
					log.debug(xbeeConfigDeviceDTO.getName() + " 10:" + latestPortReadings.isD10On() + 
							" 11: " + latestPortReadings.isD11On() +
							" 12: " + latestPortReadings.isD12On());					
				}
			} else {
				xbeeConfigDeviceDTO.setAnalogValue(latestPortReadings.getAnalog(devicePort) );
			}
		}
	}
	
	public void setRxResponseEntry(int[] data) {
		if (rxResponseMap == null) {
			rxResponseMap = new HashMap<Integer, Integer>();
			rxResponseMap.put(Integer.valueOf(data[0]), Integer.valueOf(data[1]) );
		} else {
			//TODO - we want to build in something to ignore the false 0 readings from Water meter
			//TODO - for now, since this is the only Xbee we have in the ring, going to hardcode it (yes I know - bad idea!)
			if (data[1] != 0) {
				rxResponseMap.put(Integer.valueOf(data[0]), Integer.valueOf(data[1]) );
			} else {
				log.info("TODO - hardcoded ignore 0 value - not setting!");
			}
		}
		
		
		
		StringBuffer dataBuffer = new StringBuffer();
		for (int i=0; i<data.length; i++) {
			if (i != 0) {
				dataBuffer.append(",");
			}
			dataBuffer.append(data[i]);			
		}
		log.debug("setRxResponseEntry (" + this.getId() + " - " + this.getName() + ") Len(" + data.length + "): " + dataBuffer);
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Date getLastSync() {
		return lastSync;
	}
	public void setLastSync(Date lastSync) {
		this.lastSync = lastSync;
	}
	
	public HashMap<Integer, Integer> getRxResponseMap() {
		if (rxResponseMap == null) {
			rxResponseMap = new HashMap<Integer, Integer>();
		}
		return rxResponseMap;
	}
	public void setRxResponseMap(HashMap<Integer, Integer> rxResponseMap) {
		this.rxResponseMap = rxResponseMap;
	}
}
