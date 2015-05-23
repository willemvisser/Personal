package za.co.willemvisser.wpvhomecontroller.xbee.ws.rest;

import java.util.HashMap;

import javax.ws.rs.GET;  
import javax.ws.rs.Path;  
import javax.ws.rs.PathParam;  
import javax.ws.rs.Produces;  

import org.apache.log4j.Logger;

import com.rapplogic.xbee.api.XBeeAddress64;

import za.co.willemvisser.wpvhomecontroller.config.dto.XbeeConfigDTO;
import za.co.willemvisser.wpvhomecontroller.config.dto.XbeeConfigDeviceDTO;
import za.co.willemvisser.wpvhomecontroller.xbee.XbeeController;


@Path("/xdevice")
public class XDeviceController {
	
	static Logger log = Logger.getLogger(XDeviceController.class.getName());
	
	
	
	/**
	 * Given a device ID, find the Xbee device, and depending on 
	 * 	existing status, take some logical action
	 * @param deviceId
	 * @return OK or ERROR message
	 */
	@GET  
    @Path("/{deviceId}")  
    @Produces("text/plain")  
    public String takeAction(@PathParam("deviceId") String deviceId){          		
			
		log.info("XDeviceController taking action on device: " + deviceId);

		try {
			XbeeConfigDeviceDTO xbeeConfigDeviceDTO = XbeeController.INSTANCE.getDeviceWithID(deviceId);
			if (xbeeConfigDeviceDTO != null) {
				return takeAction(xbeeConfigDeviceDTO);
			} else {
				return "ERROR: Device not found";
			}
			
		} catch (Exception e) {
			log.error("xdevice error: " + e.toString() );
			return "ERROR: " + e.getMessage();
		}					
      
    } 
	
	
	/**
	 * @param xbeeConfigDeviceDTO
	 * @return OK or ERROR message
	 */
	private String takeAction(XbeeConfigDeviceDTO xbeeConfigDeviceDTO) {
		if (xbeeConfigDeviceDTO.getType().equals( XbeeConfigDeviceDTO.TYPE_LIGHT) 
				|| xbeeConfigDeviceDTO.getType().equals( XbeeConfigDeviceDTO.TYPE_PUMP)
				) {
				//|| xbeeConfigDeviceDTO.getType().equals( XbeeConfigDeviceDTO.TYPE_IRRIGATION)) {
			return takeSwitchAction(xbeeConfigDeviceDTO);
		} else {
			return "ERROR: Unknown Type";
		}
	}
	
	/**
	 * @param xbeeConfigDeviceDTO
	 * @return OK or ERROR message
	 */
	private String takeSwitchAction(XbeeConfigDeviceDTO xbeeConfigDeviceDTO) {
		if (!xbeeConfigDeviceDTO.isDigital()) {
			return "ERROR: A switch has to be set as digital";
		} else if (xbeeConfigDeviceDTO.isEnabled()) {
			return XbeeController.INSTANCE.swithXbeeDevice(XbeeController.INSTANCE.getXbeeWithDeviceID(xbeeConfigDeviceDTO.getId()).getId(), 
					xbeeConfigDeviceDTO.getPortAddress(), xbeeConfigDeviceDTO.getPort(), false);
		} else {
			return XbeeController.INSTANCE.swithXbeeDevice(XbeeController.INSTANCE.getXbeeWithDeviceID(xbeeConfigDeviceDTO.getId()).getId(), 
					xbeeConfigDeviceDTO.getPortAddress(), xbeeConfigDeviceDTO.getPort(), true);
		} 
	}
	
}
