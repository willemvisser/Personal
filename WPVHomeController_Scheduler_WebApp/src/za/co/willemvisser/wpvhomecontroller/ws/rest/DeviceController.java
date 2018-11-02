package za.co.willemvisser.wpvhomecontroller.ws.rest;

import javax.ws.rs.GET;  
import javax.ws.rs.Path;  
import javax.ws.rs.PathParam;  
import javax.ws.rs.Produces;  

import org.apache.log4j.Logger;


import za.co.willemvisser.wpvhomecontroller.config.dto.XbeeConfigDTO;
import za.co.willemvisser.wpvhomecontroller.config.dto.XbeeConfigDeviceDTO;
import za.co.willemvisser.wpvhomecontroller.util.DateUtil;
import za.co.willemvisser.wpvhomecontroller.xbee.XbeeController;


@Path("/device")
public class DeviceController {

	static Logger log = Logger.getLogger(DeviceController.class.getName());
	
	@GET
	@Path("/getmapvalue/{deviceId}")  
	@Produces("text/plain")  
    public String getDeviceMapValue(@PathParam("deviceId") String deviceId) {		
		//TODO - we first need to determine what device type this is.  Perhaps we prefix the device ID with the type to identify it??						
		
		try {	
			log.debug("Device getmapvalue: " + deviceId);
			XbeeConfigDTO xbeeConfigDTO = XbeeController.INSTANCE.getXbeeWithDeviceID(deviceId);
			XbeeConfigDeviceDTO xbeeConfigDeviceDTO = XbeeController.INSTANCE.getDeviceWithID(deviceId);			
			return String.valueOf( xbeeConfigDTO.getRxResponseMap().get(Integer.valueOf(xbeeConfigDeviceDTO.getPortAddress())));						
		} catch (Exception e) {
			log.error("getDeviceMapValue error: " + e.toString() );
			return "ERR";
		}
	}
	
	@GET
	@Path("/getmaplastupdated/{deviceId}")  
	@Produces("text/plain")  
    public String getDeviceMapLastUpdated(@PathParam("deviceId") String deviceId) {		
		//TODO - we first need to determine what device type this is.  Perhaps we prefix the device ID with the type to identify it??						
		
		try {	
			log.debug("Device getmapvalue: " + deviceId);
			XbeeConfigDTO xbeeConfigDTO = XbeeController.INSTANCE.getXbeeWithDeviceID(deviceId);	
			
			return DateUtil.INSTANCE.convertDateTimeToString(xbeeConfigDTO.getLastSync()); 
									
		} catch (Exception e) {
			log.error("getDeviceMapLastUpdated error: " + e.toString() );
			return "ERR";
		}
	}
	
	@GET
	@Path("/setmapvalue/{deviceId}/{mapValue}")  
	@Produces("text/plain")  
    public String setDeviceMapValue(@PathParam("deviceId") String deviceId, @PathParam("mapValue") Integer mapValue) {
		
		//TODO - we first need to determine what device type this is.  Perhaps we prefix the device ID with the type to identify it??
		try {
			log.debug("Device setmapvalue: " + deviceId + ":" + mapValue);
			XbeeConfigDTO xbeeConfigDTO = XbeeController.INSTANCE.getXbeeWithDeviceID(deviceId);
			XbeeConfigDeviceDTO xbeeConfigDeviceDTO = XbeeController.INSTANCE.getDeviceWithID(deviceId);
			
			xbeeConfigDTO.getRxResponseMap().put( Integer.valueOf(xbeeConfigDeviceDTO.getPortAddress()), mapValue);			
			return "OK";
		} catch (Exception e) {
			log.error("setDeviceMapValue: " + e.toString() );
			return "ERR";
		}
	}
	
	
}
