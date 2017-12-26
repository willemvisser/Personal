package za.co.willemvisser.wpvhomecontroller.util;

import java.net.URLEncoder;
import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.log4j.Logger;

import za.co.willemvisser.wpvhomecontroller.config.ConfigController;
import za.co.willemvisser.wpvhomecontroller.config.dto.GeneralPropertyDTO;

public enum StoreUtil {

	INSTANCE;
	
	static Logger log = Logger.getLogger(StoreUtil.class.getName());
	
	public void logEvent(String deviceId, String command, Boolean success, String description, Date timestamp) {
		log.debug("logEvent: " + description);
		
		//TODO - This caused up to 60s delay in commands.  We should be adding these events to a queue / bus - and then
		//		 	have an asynch thread reading from it.
		
		/*
		
		GeneralPropertyDTO propStoreEventURL = ConfigController.INSTANCE.getGeneralProperty(GeneralPropertyDTO.PROP_STORE_EVENT_URL);
		if (propStoreEventURL == null) {
			log.error("Could not find the Store HTTP URl in the general properties file under key: " + GeneralPropertyDTO.PROP_STORE_EVENT_URL);
			//TODO - add to event??
			return;
		}				
		
		try {
			StringBuffer requestBuffer = new StringBuffer(propStoreEventURL.getValue());
			requestBuffer.append(deviceId);
			requestBuffer.append("/");
			requestBuffer.append(command);
			requestBuffer.append("/");
			requestBuffer.append(success);
			requestBuffer.append("/");
			requestBuffer.append(URLEncoder.encode(description, "UTF-8").replaceAll("\\+", "%20"));
			//TODO - timestamp in case the post get's delayed?
			
			HttpResponse response = HttpUtil.INSTANCE.doHttpGet(requestBuffer.toString());
			StringBuffer responseStr = HttpUtil.INSTANCE.getResponseContent(response);
			if (responseStr.toString().equals("OK")) {
				log.info("Posted successfully.");
			} else {
				log.error("Failure: " + responseStr);
				//TODO - we should add this to the log and retry later
			}
		} catch (Exception e) {
			log.error("Could not post event: " + e);
			
			//TODO - we should store this event and try again later
		}
		
		*/
	}
}
