package za.co.willemvisser.wpvhomecontroller.xbee;

import java.util.HashMap;

import org.apache.log4j.Logger;

import za.co.willemvisser.wpvhomecontroller.config.ConfigController;
import za.co.willemvisser.wpvhomecontroller.config.dto.GeneralPropertyDTO;
import za.co.willemvisser.wpvhomecontroller.config.dto.XbeeConfigDTO;
import za.co.willemvisser.wpvhomecontroller.config.dto.XbeeConfigDeviceDTO;
import za.co.willemvisser.wpvhomecontroller.config.dto.XbeeConfigsDTO;
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
							
		xbeeAddressMapCache = new HashMap<String, XBeeAddress64>();		
		for (XbeeConfigDTO xbeeConfigDTO : xbeeConfigsDTO.getXbeeList()) {
			xbeeAddressMapCache.put(xbeeConfigDTO.getId(), new XBeeAddress64(xbeeConfigDTO.getAddress()));
		}
		
		//xbeeAddressMapCache.put("4", ADDRESS_poolPump);
		//xbeeAddressMapCache.put("3", ADDRESS_irrigationPump);
		//xbeeAddressMapCache.put("1", ADDRESS_irrigationController);
		
		xbeeDeviceMap = new HashMap<XBeeAddress64, XbeeConfigDTO>();
		//xbeeDeviceMap.put(ADDRESS_irrigationController, new XbeeDTO("Irrigation Controller", ADDRESS_irrigationController));
		//xbeeDeviceMap.put(ADDRESS_irrigationPump, new XbeeDTO("Irrigation Pump", ADDRESS_irrigationPump));
		//xbeeDeviceMap.put(ADDRESS_poolPump, new XbeeDTO("Pool Pump", ADDRESS_poolPump));
		
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
		
		log.debug("Xbee: " + xbeeAddressID);
		
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
		
		log.info("XbeeAddress: " + xbeeAddress + " command: " + command + " value: " + value);
		RemoteAtRequest request = new RemoteAtRequest(xbeeAddress, command, value);
		
		RemoteAtResponse response = (RemoteAtResponse) xbee.sendSynchronous(request, 5000);

		if (response.isOk()) {
			return response;
		} else {
			log.error("Xbee Remote response error: " + response.getStatus());
			throw new XBeeException("Could not get remote Xbee response: " + response.getStatus());
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
			RemoteAtResponse response = XbeeController.INSTANCE.remoteAtRequest(xbeeAddressID, portName + port, on ? new int[] {5} : new int[] {4});
			
			if (response.isOk()) {		 
				log.info("COOL - success!!");
				return "OK";
			} else {
				log.error("NOT ok: " + response.getStatus());
				return "ERROR: " + response.getStatus();
			}
			
		} catch (Exception e) {
			log.error("xon error: " + e.toString() );
			return "ERROR: " + e.getMessage();
		}
	}
	
	
}
