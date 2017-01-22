package za.co.willemvisser.wpvhomecontroller.xbee;

import java.util.ArrayList;
import java.util.Date;

import org.apache.log4j.Logger;

import za.co.willemvisser.wpvhomecontroller.config.dto.XbeeConfigDTO;
import za.co.willemvisser.wpvhomecontroller.config.dto.XbeeConfigDeviceDTO;
import za.co.willemvisser.wpvhomecontroller.xbee.dto.XbeeDTO;

import com.rapplogic.xbee.api.AtCommandResponse;
import com.rapplogic.xbee.api.PacketListener;
import com.rapplogic.xbee.api.RemoteAtResponse;
import com.rapplogic.xbee.api.XBeeResponse;
import com.rapplogic.xbee.api.zigbee.ZNetNodeIdentificationResponse;
import com.rapplogic.xbee.api.zigbee.ZNetRxIoSampleResponse;
import com.rapplogic.xbee.api.zigbee.ZNetRxResponse;
import com.rapplogic.xbee.util.ByteUtils;

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
				
				XbeeConfigDTO xbeeConfigDTO = XbeeController.INSTANCE.getXbeeDeviceMap().get(ioResponse.getRemoteAddress64());
				if (xbeeConfigDTO != null) {					
					xbeeConfigDTO.setLatestPortReadings(ioResponse);
				} else {
					log.info("New XBee found: " + ioResponse.getRemoteAddress64());
					XbeeConfigDTO newXbeeConfigDTO = new XbeeConfigDTO();
					newXbeeConfigDTO.setName("Unknown XBee: " + ioResponse.getRemoteAddress64() );					
					newXbeeConfigDTO.setAddress(ioResponse.getRemoteAddress64().getAddress().toString());
					newXbeeConfigDTO.setDeviceList(new ArrayList<XbeeConfigDeviceDTO>());
					newXbeeConfigDTO.setLatestPortReadings(ioResponse);					
					XbeeController.INSTANCE.getXbeeDeviceMap().put(ioResponse.getRemoteAddress64(), 
							newXbeeConfigDTO);
				}
			} else if (response instanceof RemoteAtResponse) {
				RemoteAtResponse remoteResponse = (RemoteAtResponse) response;
				log.info("Remote Response OK: " + remoteResponse.isOk() + ", " + remoteResponse.toString() ); 
			} else if (response instanceof AtCommandResponse) {
				AtCommandResponse atResponse = (AtCommandResponse) response;				
				if (atResponse.getCommand().equals("ND") && atResponse.getValue() != null && atResponse.getValue().length > 0) {
					//ZBNodeDiscover nd = ZBNodeDiscover.parse((AtCommandResponse)response);
					//log.info("Node Discover is " + nd);					
					log.info("Node discovered: " + atResponse.getValue() + " -> " + atResponse.toString() );
				} else if (atResponse.getCommand().equals("NT")){ 										
					// default is 6 seconds
					int nodeDiscoveryTimeout = ByteUtils.convertMultiByteToInt(atResponse.getValue()) * 100;			
					log.info("Node discovery timeout is " + nodeDiscoveryTimeout + " milliseconds");
				} else {
					log.error("Unknown Command Response: getCommand=" + atResponse.getCommand() + 
							" - String: " + atResponse.toString() );
				}							
			} else if (response instanceof ZNetRxResponse) {				
				ZNetRxResponse rxResponse = (ZNetRxResponse)response;				
				if (rxResponse.getData() != null) {
					//log.info("ZNetRxResponse Size: " + rxResponse.getData().length);
					if (rxResponse.getData().length == 2) {
						//TODO - we should move this out to a separate process handler
						
						XbeeConfigDTO xbeeConfigDTO = XbeeController.INSTANCE.getXbeeDeviceMap().get(rxResponse.getRemoteAddress64());
						if (xbeeConfigDTO != null) {					
							xbeeConfigDTO.setRxResponseEntry(rxResponse.getData());
						} else {
							log.info("New XBee found: " + rxResponse.getRemoteAddress64());
							XbeeConfigDTO newXbeeConfigDTO = new XbeeConfigDTO();
							newXbeeConfigDTO.setName("Unknown XBee: " + rxResponse.getRemoteAddress64() );					
							newXbeeConfigDTO.setAddress(rxResponse.getRemoteAddress64().getAddress().toString());
							newXbeeConfigDTO.setDeviceList(new ArrayList<XbeeConfigDeviceDTO>());
							newXbeeConfigDTO.setRxResponseEntry(rxResponse.getData());					
							XbeeController.INSTANCE.getXbeeDeviceMap().put(rxResponse.getRemoteAddress64(), 
									newXbeeConfigDTO);
						}
						
						xbeeConfigDTO.setLastSync(new Date());
						
						log.debug("ZNetRxResponse (Data): " + rxResponse.getData()[0] + " - " + rxResponse.getData()[1] );
						
					} else {
						log.error("ZNetRxResponse - unknown packet size (" + rxResponse.getData().length + ") - expected 2: " + rxResponse.toString());
					}
					
					
				} else {
					log.error("ZNetRxResponse - empty/null packet data - expected 2: " + rxResponse.toString());
				}
			} else {
				log.info("TODO: Unknown Response: API_ID=" + response.getApiId().toString() + " -> " + response.toString() + " type=" + response.getClass());
			}
			
			//log.debug("D6: (" + ioResponse.isD6On() + ")  D7: (" + (ioResponse.isD7On()) + ")  D7:  (" + ioResponse.isDigitalOn(7) + ")");
			
		} catch (Exception e) {
			log.error("Could not do a ZNetRxIoSampleResponse response: " + e.toString(), e );
		}

	}

}
