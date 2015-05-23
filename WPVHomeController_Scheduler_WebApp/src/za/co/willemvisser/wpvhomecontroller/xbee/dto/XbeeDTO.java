package za.co.willemvisser.wpvhomecontroller.xbee.dto;

import java.io.Serializable;
import java.util.LinkedList;

import com.rapplogic.xbee.api.XBeeAddress64;
import com.rapplogic.xbee.api.zigbee.ZNetRxIoSampleResponse;

public class XbeeDTO implements Serializable {
	
	private static final long serialVersionUID = -7187858949534472234L;
	
	private String name;
	private XBeeAddress64 xbeeAddress64;
	private LinkedList<XbeeDeviceDTO> deviceList;
	
	private ZNetRxIoSampleResponse latestPortReadings;
	
	public XbeeDTO(String name, XBeeAddress64 xbeeAddress64) {
		super();
		this.name = name;
		this.xbeeAddress64 = xbeeAddress64;
		deviceList = new LinkedList<XbeeDeviceDTO>();
		latestPortReadings = null;
	}
	
	public void addDevice(XbeeDeviceDTO xbeeDeviceDTO) {
		deviceList.add(xbeeDeviceDTO);
	}
	
	
	public XBeeAddress64 getXbeeAddress64() {
		return xbeeAddress64;
	}
	public void setXbeeAddress64(XBeeAddress64 xbeeAddress64) {
		this.xbeeAddress64 = xbeeAddress64;
	}
	public LinkedList<XbeeDeviceDTO> getDeviceList() {
		return deviceList;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public ZNetRxIoSampleResponse getLatestPortReadings() {
		return latestPortReadings;
	}

	public void setLatestPortReadings(ZNetRxIoSampleResponse latestPortReadings) {
		this.latestPortReadings = latestPortReadings;
	}

	public void setDeviceList(LinkedList<XbeeDeviceDTO> deviceList) {
		this.deviceList = deviceList;
	}

}
