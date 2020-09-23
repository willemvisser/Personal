package za.co.willemvisser.wpvhomecontroller.ws.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import org.apache.log4j.Logger;
import za.co.willemvisser.wpvhomecontroller.property.PropertyManager;
import za.co.willemvisser.wpvhomecontroller.util.DateUtil;

@Path("/property")
public class GeneralPropertyController {
	
	static Logger log = Logger.getLogger(GeneralPropertyController.class.getName());

	@GET
	@Path("/set/{key}/{value}")  
	@Produces("text/plain")  
    public String setPropertyValue(@PathParam("key") String key, @PathParam("value") String value) {
				
		try {
			PropertyManager.INSTANCE.setProperty(key, value);			
			return "OK";
		} catch (Exception e) {
			log.error("setPropertyValue: " + e.toString() );
			return "ERR";
		}
	}
	
	
	@GET
	@Path("/get/{key}")  
	@Produces("text/plain")  
    public String getPropertyValue(@PathParam("key") String key) {								
		
		try {	
			return PropertyManager.INSTANCE.getProperty(key).getValue();
														
		} catch (Exception e) {
			log.error("getPropertyValue error: " + e.toString() );
			return "ERR";
		}
	}
	
	@GET
	@Path("/getlastupdated/{key}")  
	@Produces("text/plain")  
    public String getLastUpdatedValue(@PathParam("key") String key) {								
		
		try {	
			return DateUtil.INSTANCE.convertDateTimeToString(PropertyManager.INSTANCE.getProperty(key).getLastUpdated());
														
		} catch (Exception e) {
			log.error("getLastUpdatedValue error: " + e.toString() );
			return "ERR";
		}
	}
	
}
