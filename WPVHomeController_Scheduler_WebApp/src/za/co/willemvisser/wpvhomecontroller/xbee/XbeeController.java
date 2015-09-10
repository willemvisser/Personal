package za.co.willemvisser.wpvhomecontroller.xbee;

import java.util.Date;
import java.util.HashMap;

import org.apache.log4j.Logger;

import za.co.willemvisser.wpvhomecontroller.config.ConfigController;
import za.co.willemvisser.wpvhomecontroller.config.dto.GeneralPropertyDTO;
import za.co.willemvisser.wpvhomecontroller.config.dto.XbeeConfigDTO;
import za.co.willemvisser.wpvhomecontroller.config.dto.XbeeConfigDeviceDTO;
import za.co.willemvisser.wpvhomecontroller.config.dto.XbeeConfigsDTO;
import za.co.willemvisser.wpvhomecontroller.util.StoreUtil;
import za.co.willemvisser.wpvhomecontroller.xbee.dto.XbeeDTO;

import com.rapplogic.xbee.api.RemoteAtRequest;
import com.rapplogic.xbee.api.RemoteAtResponse;
import com.rapplogic.xbee.api.XBee;
import com.rapplogic.xbee.api.XBeeAddress64;
import com.rapplogic.xbee.api.XBeeException;
import com.rapplogic.xbee.api.XBeeTimeoutException;


public enum XbeeController {

	INSTANCE;
	
	static Logger log = Logger.getLogger(XbeeController.class.getName());
	
	private XBee xbee = null;
	
	//public static final XBeeAddress64 ADDRESS_irrigationController = 	new XBeeAddress64("00 13 A2 00 40 A8 C0 3C");
	
	public static final XBeeAddress64 ADDRESS_spare = 	new XBeeAddress64("00 13 A2 00 40 A2 65 60");
	public static final XBeeAddress64 ADDRESS_irrigationPump = 			new XBeeAddress64("00 13 A2 00 40 B2 5C 6E");
	public static final XBeeAddress64 ADDRESS_irrigationController = 				new XBeeAddress64("00 13 A2 00 40 A8 C0 3C");
	public static final XBeeAddress64 ADDRESS_poolPump = 				new XBeeAddress64("00 13 A2 00 40 A8 BB 54");
	
	
	private HashMap<String, XBeeAddress64> xbeeAddressMapCache = null;
	private HashMap<XBeeAddress64, XbeeConfigDTO> xbeeDeviceMap = null;
	
	private XbeeController() {}
	
	/**
	 * @param port  eg /dev/tty.usbserial-A6005v5M
	 * @param baudRate eg 9600
	 * @throws XBeeException 
	 */
	public void init(String port, int baudRate, XbeeConfigsDTO xbeeConfigsDTO) throws XBeeException {
		xbee = new XBee();
		
		boolean xbeeEnabled = Boolean.parseBoolean(ConfigController.INSTANCE.getGeneralProperty(GeneralPropertyDTO.PROP_XBEE_ENABLED).getValue());
		if (xbeeEnabled) {
			xbee.open(port, baudRate);
			xbee.addPacketListener(new XbeeControllerPacketHandler());
		}
		
		loadXbeeConfig(xbeeConfigsDTO);
	}
	
	/**
	 * @param xbeeConfigsDTO
	 */
	public void loadXbeeConfig(XbeeConfigsDTO xbeeConfigsDTO) {
		xbeeAddressMapCache = new HashMap<String, XBeeAddress64>();		
		for (XbeeConfigDTO xbeeConfigDTO : xbeeConfigsDTO.getXbeeList()) {
			xbeeAddressMapCache.put(xbeeConfigDTO.getId(), new XBeeAddress64(xbeeConfigDTO.getAddress()));
		}
				
		xbeeDeviceMap = new HashMap<XBeeAddress64, XbeeConfigDTO>();		
		for (XbeeConfigDTO xbeeConfigDTO : xbeeConfigsDTO.getXbeeList()) {
			xbeeDeviceMap.put(new XBeeAddress64(xbeeConfigDTO.getAddress()), xbeeConfigDTO);
		}
	}
	
	public void shutdown() {
		xbee.close();
	}
	
	/**
	 * @param xbeeAddressID
	 * @param command
	 * @param value
	 * @return
	 * @throws XBeeTimeoutException
	 * @throws XBeeException
	 */
	public RemoteAtResponse remoteAtRequest(String xbeeAddressID, String command, int[] value) throws XBeeTimeoutException, XBeeException {
		log.debug("Sending Xbee remote AT Request: " + command + value);
		
		// replace with SH + SL of your end device
		
		//XBeeAddress64 address = new XBeeAddress64("00 13 A2 00 40 A2 65 60");
		//XBeeAddress64 address = XBeeAddress64.ZNET_COORDINATOR;
		
		XBeeAddress64 xbeeAddress = xbeeAddressMapCache.get(xbeeAddressID);
		if (xbeeAddress == null) {
			throw new XBeeException("Xbee with ID '" + xbeeAddressID + "' not found.");
		}
		
		
		
		return remoteAtRequest(xbeeAddress, command, value);								
				
	}
	
	/**
	 * @param xbeeAddress
	 * @param command
	 * @param value
	 * @return
	 * @throws XBeeTimeoutException
	 * @throws XBeeException
	 */
	public RemoteAtResponse remoteAtRequest(XBeeAddress64 xbeeAddress, String command, int[] value) throws XBeeTimeoutException, XBeeException {		
		
		log.debug("XbeeAddress: " + xbeeAddress + " command: " + command + " value: " + value);
		
		
		RemoteAtRequest request = new RemoteAtRequest(xbeeAddress, command, value);			
		XbeeConfigDTO xbeeConfigDTO = xbeeDeviceMap.get(xbeeAddress);
			
		try {
			RemoteAtResponse response = (RemoteAtResponse) xbee.sendSynchronous(request, 5000);		
			
			if (response.isOk()) {		 
				StoreUtil.INSTANCE.logEvent(xbeeConfigDTO.getId(), command, Boolean.TRUE, "Turning " + (value[0] == 5 ? "on" : "off") + " port " + command + " for Xbee: " + xbeeConfigDTO.getName(), new Date());
				return response;
			} else {				
				log.error("Xbee Remote response error: " + response.getStatus());
				throw new XBeeException("Could not get remote Xbee response: " + response.getStatus());
			}		
		} catch (XBeeTimeoutException timeoutException) {
			StoreUtil.INSTANCE.logEvent(xbeeConfigDTO.getId(), command, Boolean.FALSE, "Turning " + (value[0] == 5 ? "on" : "off") + " port " + command + " for Xbee: " + xbeeConfigDTO.getName(), new Date());
			throw timeoutException;			
		} catch (XBeeException xbeeException) {
			StoreUtil.INSTANCE.logEvent(xbeeConfigDTO.getId(), command, Boolean.FALSE, "Turning " + (value[0] == 5 ? "on" : "off") + " port " + command + " for Xbee: " + xbeeConfigDTO.getName(), new Date());
			throw xbeeException;
		} catch (Exception e) {
			StoreUtil.INSTANCE.logEvent(xbeeConfigDTO.getId(), command, Boolean.FALSE, "Turning " + (value[0] == 5 ? "on" : "off") + " port " + command + " for Xbee: " + xbeeConfigDTO.getName(), new Date());
			throw e;
		}
	}
		
	
	public HashMap<XBeeAddress64, XbeeConfigDTO> getXbeeDeviceMap() {
		return xbeeDeviceMap;
	}
	
	/**
	 * @param deviceID
	 * @return XbeeConfigDeviceDTO with given ID
	 */
	public XbeeConfigDeviceDTO getDeviceWithID(String deviceID) {
		HashMap<XBeeAddress64, XbeeConfigDTO> xbeeMap = XbeeController.INSTANCE.getXbeeDeviceMap();
		for (XbeeConfigDTO xbeeConfigDTO : xbeeMap.values()) {
			for (XbeeConfigDeviceDTO xbeeConfigDeviceDTO : xbeeConfigDTO.getDeviceList()) {
				if (xbeeConfigDeviceDTO.getId().equals(deviceID)) {
					return xbeeConfigDeviceDTO;
				}
			}
		}
		return null;
	}
	
	/**
	 * @param deviceID
	 * @return
	 */
	public XbeeConfigDTO getXbeeWithDeviceID(String deviceID) {
		HashMap<XBeeAddress64, XbeeConfigDTO> xbeeMap = XbeeController.INSTANCE.getXbeeDeviceMap();
		for (XbeeConfigDTO xbeeConfigDTO : xbeeMap.values()) {
			for (XbeeConfigDeviceDTO xbeeConfigDeviceDTO : xbeeConfigDTO.getDeviceList()) {
				if (xbeeConfigDeviceDTO.getId().equals(deviceID)) {
					return xbeeConfigDTO;
				}
			}
		}
		return null;
	}
		
	/**
	 * Turn on or off Xbee port
	 * @param xbeeAddress	(1, 2, etc mapped to correct AddressID)
	 * @param portName  (e.g. D or P)
	 * @param port		(e.g. 4)
	 * @param on		(true for on, false for off)
	 * @return
	 */
	public String swithXbeeDevice(String xbeeAddressID, String portName, String port, boolean on) {
		try {
			log.info("swithXbeeDevice: " + xbeeAddressID + ", " + portName + ", " + port + ", " + on);
			RemoteAtResponse response = XbeeController.INSTANCE.remoteAtRequest(xbeeAddressID, portName + port, on ? new int[] {5} : new int[] {4});
			
			if (response.isOk()) {		 
				log.debug("COOL - success!!");
				StoreUtil.INSTANCE.logEvent(xbeeAddressID, portName, Boolean.TRUE, "Turning " + (on ? "on" : "off") + " port " + port + " for Xbee with Address: " + xbeeAddressID, new Date());
				return "OK";
			} else {
				log.error("NOT ok: " + response.getStatus());
				StoreUtil.INSTANCE.logEvent(xbeeAddressID, portName, Boolean.FALSE, "Turning " + (on ? "on" : "off") + " port " + port + " for Xbee with Address: " + xbeeAddressID, new Date());
				return "ERROR: " + response.getStatus();
			}
			
		} catch (Exception e) {
			log.error("xon error: " + e.toString() );
			return "ERROR: " + e.getMessage();
		}
	}
	
	/**
	 * @param xbeeConfigDeviceDTO
	 * @param on
	 * @return OK or ERROR: 
	 */
	public String switchXbeeDevice(XbeeConfigDeviceDTO xbeeConfigDeviceDTO, boolean on) {
		return swithXbeeDevice(getXbeeWithDeviceID(xbeeConfigDeviceDTO.getId()).getId(), 
				xbeeConfigDeviceDTO.getPortAddress(), xbeeConfigDeviceDTO.getPort(), on );
	}
	
}
