package za.co.willemvisser.wpvhomecontroller.ws.rest;

import java.util.Date;

import javax.ws.rs.GET;  
import javax.ws.rs.Path;  
import javax.ws.rs.PathParam;  
import javax.ws.rs.Produces;  

import org.apache.log4j.Logger;

import za.co.willemvisser.wpvhomecontroller.config.dto.XbeeConfigDTO;
import za.co.willemvisser.wpvhomecontroller.xbee.XbeeController;



@Path("/board")
public class BoardController {

	static Logger log = Logger.getLogger(BoardController.class.getName());
	
	@GET
	@Path("/heartbeat/{boardId}")  
	@Produces("text/plain")  
    public String setDeviceHeartBeat(@PathParam("boardId") String boardId) {
		
		//TODO - we first need to determine what device type this is.  Perhaps we prefix the device ID with the type to identify it??
		log.debug("BoardController.setDeviceHeartBeat: " + boardId);
		try {						
			XbeeConfigDTO xbeeConfigDTO = XbeeController.INSTANCE.getBoardWithID(boardId);
			xbeeConfigDTO.setLastSync(new Date());					
			return "OK";					
		} catch (Exception e) {
			log.error("setDeviceHeartBeat: " + e.toString());
			return "ERR";
		}
	}
	
	@GET
	@Path("/getmapvalue/{boardId}/{mapIndex}")  
	@Produces("text/plain")  
    public String getBoardMapValue(@PathParam("boardId") String boardId,  @PathParam("mapIndex") Integer mapIndex) {		
		//TODO - we first need to determine what device type this is.  Perhaps we prefix the device ID with the type to identify it??
		try {	
			log.info("Board getmapvalue: " + boardId + ":" + mapIndex);
			XbeeConfigDTO xbeeConfigDTO = XbeeController.INSTANCE.getBoardWithID(boardId);
			return String.valueOf( xbeeConfigDTO.getRxResponseMap().get(mapIndex.intValue()) );						
		} catch (Exception e) {
			log.error("getDeviceMapValue: " + e.toString() );
			return "ERR";
		}
	}
	
	@GET
	@Path("/setmapvalue/{boardId}/{mapIndex}/{mapValue}")  
	@Produces("text/plain")  
    public String setBoardMapValue(@PathParam("boardId") String boardId,  @PathParam("mapIndex") Integer mapIndex, 
    		@PathParam("mapValue") Integer mapValue) {
		
		//TODO - we first need to determine what device type this is.  Perhaps we prefix the device ID with the type to identify it??
		try {
			log.info("Board setmapvalue: " + boardId + ":" + mapIndex + ":" + mapValue);
			XbeeConfigDTO xbeeConfigDTO = XbeeController.INSTANCE.getBoardWithID(boardId);
			xbeeConfigDTO.getRxResponseMap().put(mapIndex, mapValue);			
			return "OK";
		} catch (Exception e) {
			log.error("setDeviceMapValue: " + e.toString() );
			return "ERR";
		}
	}
}
