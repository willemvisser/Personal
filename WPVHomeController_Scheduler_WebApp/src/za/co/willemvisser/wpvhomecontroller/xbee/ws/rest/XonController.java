package za.co.willemvisser.wpvhomecontroller.xbee.ws.rest;

import javax.ws.rs.GET;  
import javax.ws.rs.Path;  
import javax.ws.rs.PathParam;  
import javax.ws.rs.Produces;  

import org.apache.log4j.Logger;

import za.co.willemvisser.wpvhomecontroller.xbee.XbeeController;

import com.rapplogic.xbee.api.RemoteAtRequest;
import com.rapplogic.xbee.api.RemoteAtResponse;
import com.rapplogic.xbee.api.XBeeAddress64;
import com.rapplogic.xbee.api.XBeeTimeoutException;

@Path("/xon")
public class XonController {
	
	static Logger log = Logger.getLogger(XonController.class.getName());

	@GET  
    @Path("/version")  
    @Produces("text/plain")  
    public String hello(){  
        return "Version:  xxx";      
    }
	
	//WV QUeryString Example:  http://192.168.1.202/xon/1/D/7
    //  Where params: 1) Command  (e.g. xon, xoff, on etc)
    //                2) Device (If Xon / Xoff - equals device MAC index)
    //                3) Parameter1 (e.g. P, D etc)
    //                4) Parameter2 (e.g. 1, 9 etc)
	
	
	@GET  
    @Path("/{device}/{param1}/{param2}")  
    @Produces("text/plain")  
    public String showMsg(@PathParam("device") String device, @PathParam("param1") String param1, @PathParam("param2") String param2){  
        		
		
		log.info("xon command:  " + device + " - param1: " + param1 + " - param2: " + param2);


		try {
			RemoteAtResponse response = XbeeController.INSTANCE.remoteAtRequest(device, param1 + param2, new int[] {5});
			
			if (response.isOk()) {
		        // success
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
