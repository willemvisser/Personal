package za.co.willemvisser.wpvhomecontroller.ws.rest;

import javax.ws.rs.GET;  
import javax.ws.rs.Path;  
import javax.ws.rs.PathParam;  
import javax.ws.rs.Produces;  

import org.apache.log4j.Logger;

import za.co.willemvisser.wpvhomecontroller.config.dto.XbeeConfigDTO;
import za.co.willemvisser.wpvhomecontroller.xbee.XbeeController;


@Path("/device")
public class DeviceController {

	static Logger log = Logger.getLogger(DeviceController.class.getName());
	
	@GET
	@Path("/{deviceId}/{mapIndex}")  
	@Produces("text/plain")  
    public String getDeviceMapValue(@PathParam("deviceId") String deviceId,  @PathParam("mapIndex") Integer mapIndex) {		
		//TODO - we first need to determine what device type this is.  Perhaps we prefix the device ID with the type to identify it??
		try {			
			XbeeConfigDTO xbeeConfigDTO = XbeeController.INSTANCE.getXbeeWithDeviceID(deviceId);
			return String.valueOf( xbeeConfigDTO.getRxResponseMap().get(mapIndex.intValue()) );						
		} catch (Exception e) {
			return "ERR";
		}
	}
	
	@GET
	@Path("/{deviceId}/{mapIndex}/{mapValue}")  
	@Produces("text/plain")  
    public String setDeviceMapValue(@PathParam("deviceId") String deviceId,  @PathParam("mapIndex") Integer mapIndex, 
    		@PathParam("mapValue") Integer mapValue) {
		
		//TODO - we first need to determine what device type this is.  Perhaps we prefix the device ID with the type to identify it??
		try {
			//XbeeConfigDeviceDTO deviceDTO = XbeeController.INSTANCE.getDeviceWithID(deviceId);
			XbeeConfigDTO xbeeConfigDTO = XbeeController.INSTANCE.getXbeeWithDeviceID(deviceId);
			xbeeConfigDTO.getRxResponseMap().put(mapIndex, mapValue);			
			return "OK";
		} catch (Exception e) {
			return "ERR";
		}
	}
}
