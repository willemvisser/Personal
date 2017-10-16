package za.co.willemvisser.wpvhomecontroller.ws.rest;

import javax.ws.rs.GET;  
import javax.ws.rs.Path;  
import javax.ws.rs.PathParam;  
import javax.ws.rs.Produces;  

import org.apache.log4j.Logger;

import za.co.willemvisser.wpvhomecontroller.config.dto.XbeeConfigDTO;
import za.co.willemvisser.wpvhomecontroller.config.dto.XbeeConfigDeviceDTO;
import za.co.willemvisser.wpvhomecontroller.xbee.XbeeController;


@Path("/device")
public class DeviceController {

	static Logger log = Logger.getLogger(DeviceController.class.getName());
	
	@GET
	@Path("/{deviceId}/{port}/{portAddress}")  
	@Produces("text/plain")  
    public String getDeviceStatus(@PathParam("deviceId") String deviceId,  @PathParam("portAddress") String portAddress, 
    		@PathParam("port") String port) {
		
		//TODO - we first need to determine what device type this is.  Perhaps we prefix the device ID with the type to identify it??
		try {
			XbeeConfigDeviceDTO deviceDTO = XbeeController.INSTANCE.getDeviceWithID(deviceId);
			XbeeConfigDTO xbeeConfigDTO = XbeeController.INSTANCE.getXbeeWithDeviceID(deviceId);
			return String.valueOf( xbeeConfigDTO.getRxResponseMap().get(Integer.parseInt(deviceDTO.getPortAddress())) );						
		} catch (Exception e) {
			return "ERR";
		}
	}
}
