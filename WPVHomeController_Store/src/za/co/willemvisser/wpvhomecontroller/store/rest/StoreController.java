package za.co.willemvisser.wpvhomecontroller.store.rest;

import java.text.SimpleDateFormat;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.apache.log4j.Logger;


@Path("/store")
public class StoreController {

	static Logger log = Logger.getLogger(StoreController.class.getName());
	
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
	
	@GET  
	@Path("/{deviceId}/{dateStr}/{temp}")  
    @Produces("text/plain")  
    public String storeTemperatureMeasurement(@PathParam("deviceId") String deviceId, @PathParam("dateStr") String dateStr,
    					@PathParam("temp") String temp){
		
		log.info("storeTemperatureMeasurement: deviceID=" + deviceId + ", DateStr=" + dateStr + ", temp=" + temp);
		
		return "OK";
	}
	
	
}
