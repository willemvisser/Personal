package za.co.willemvisser.wpvhomecontroller.xbee;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import za.co.willemvisser.wpvhomecontroller.config.dto.XbeeConfigDTO;
import za.co.willemvisser.wpvhomecontroller.config.dto.XbeeConfigDeviceDTO;
import za.co.willemvisser.wpvhomecontroller.xbee.dto.XbeeDTO;

import com.rapplogic.xbee.api.PacketListener;
import com.rapplogic.xbee.api.XBeeResponse;
import com.rapplogic.xbee.api.zigbee.ZNetNodeIdentificationResponse;
import com.rapplogic.xbee.api.zigbee.ZNetRxIoSampleResponse;

public class XbeeControllerPacketHandler implements PacketListener {

	static Logger log = Logger.getLogger(XbeeControllerPacketHandler.class.getName());
	
	@Override
	public void processResponse(XBeeResponse response) {
		
		//log.debug("API ID: " + response.getApiId());
		
		//if (response.getApiId().equals())
		try {
			if (response instanceof ZNetNodeIdentificationResponse) {
				ZNetNodeIdentificationResponse idResponse = (ZNetNodeIdentificationResponse)response;
				log.info("New Xbee Identification, Node ID: " + idResponse.getNodeIdentifier() + ", Address: " + 
							idResponse.getRemoteAddress64() + ", Type: " + idResponse.getDeviceType().toString());
				
			} else if (response instanceof ZNetRxIoSampleResponse) {
												
				ZNetRxIoSampleResponse ioResponse = (ZNetRxIoSampleResponse)response;
				
				log.debug("Address: " + ioResponse.getRemoteAddress64() + " - " + ioResponse.getRemoteAddress64().getAddress());
				log.debug("Analog Sample 0: " + ioResponse.getAnalog0() );				
				
				//XbeeController.INSTANCE.getXbeeIOOnOffMap().put("Irrigation Pump", Boolean.valueOf(ioResponse.isD7On()));
				
				XbeeConfigDTO xbeeConfigDTO = XbeeController.INSTANCE.getXbeeDeviceMap().get(ioResponse.getRemoteAddress64());
				if (xbeeConfigDTO != null) {
					log.debug("Awesome, found Xbee entry for: " + xbeeConfigDTO.getName() + " - " + ioResponse.getRemoteAddress64());
					xbeeConfigDTO.setLatestPortReadings(ioResponse);
				} else {
					log.info("New XBee found: " + ioResponse.getRemoteAddress64());
					XbeeConfigDTO newXbeeConfigDTO = new XbeeConfigDTO();
					newXbeeConfigDTO.setName("Unknown XBee");					
					newXbeeConfigDTO.setAddress(ioResponse.getRemoteAddress64().getAddress().toString());
					newXbeeConfigDTO.setDeviceList(new ArrayList<XbeeConfigDeviceDTO>());
					newXbeeConfigDTO.setLatestPortReadings(ioResponse);					
					XbeeController.INSTANCE.getXbeeDeviceMap().put(ioResponse.getRemoteAddress64(), 
							newXbeeConfigDTO);
				}
			}
			
			//log.debug("D6: (" + ioResponse.isD6On() + ")  D7: (" + (ioResponse.isD7On()) + ")  D7:  (" + ioResponse.isDigitalOn(7) + ")");
			
		} catch (Exception e) {
			log.error("Could not do a ZNetRxIoSampleResponse response: " + e.toString(), e );
		}

	}

}
