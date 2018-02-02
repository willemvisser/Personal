package za.co.willemvisser.wpvhomecontroller.util;

import java.io.IOException;

import org.apache.log4j.Logger;

public enum WaterTankFillUtil {

	INSTANCE;
	
	// http://192.168.1.202:8080/WPVHomeController_Scheduler_WebApp/xbee/xdevice/IRR_OUTSIDE
	// IRR_PUMP
	
	static Logger log = Logger.getLogger(WaterTankFillUtil.class.getName());
	
	private boolean pumping = false;
	
	private static final int maxFillInCms = 10;  //This is the maximum number of centimeters we want to fill in one job
	
	
	private int minFillDepth = 40;				//This is the depth we want to fill to - estimate 15cm is 100%, we don't want more than 40cm (?) to avoid too much damp in sensor
	private int requestedFillDepth = 9999;		 
													
	private int currentDepth = 0;
	
	
	
	/**
	 * @param toWhatHeightInCm
	 * @param maxNoCycles	The maximum number of cycles we will pump in one job - safeguard to not drain the wellpoint
	 */
	public void startPumping(int toWhatHeightInCm, int maxNoCycles) {
		//TODO - when we start - we should check if all sensors are alive.
		
		pumping = true;
		this.requestedFillDepth = toWhatHeightInCm;
		
		int noCycles = 0;
		
				
		while (pumping) {
			noCycles++;
			
			try {
				StringBuffer response = HttpUtil.INSTANCE.getResponseContent(HttpUtil.INSTANCE.doHttpGet("http://192.168.1.202:8080/WPVHomeController_Scheduler_WebApp/xbee/device/getmapvalue/TANK_LEVEL"));
				log.info("Tank Depth: " + response);
				currentDepth = Integer.parseInt(response.toString());
				
				if (this.requestedFillDepth == -1) {
					this.requestedFillDepth = currentDepth - maxFillInCms;
				}
				
			} catch (Exception e) {
				log.warn("Could not determine the depth of the tank, exiting");
				pumping = false;
				break;
			}
						
			try {
				log.debug("Attempting to switch on pump ...");
				StringBuffer response = HttpUtil.INSTANCE.getResponseContent(HttpUtil.INSTANCE.doHttpGet("http://192.168.1.202:8080/WPVHomeController_Scheduler_WebApp/xbee/xon/3/D/7"));
				//log.info("Response: " + response);
				if (!response.toString().equals("OK")) {
					pumping = false;
					log.warn("Could not switch on irrigation pump for WaterTank, exiting");
					break;
				}
				
				log.info("Pumped switched on OK");
				
				Thread.sleep(150);
			} catch (IOException io) {
				log.warn("Could not switch on irrigation pump for WaterTank, exiting");
				pumping = false;
				break;
			} catch (InterruptedException ie) {
				//DO nothing
			}
			
			
			try {
				Thread.sleep(80000);
			} catch (Exception e) {
				log.warn("Thread sleep got interruped");
				//Do nothing - it is OK if we get interrupted
			}
			
			
			try {
				log.debug("Attempting to switch off pump ...");
				StringBuffer response = HttpUtil.INSTANCE.getResponseContent(HttpUtil.INSTANCE.doHttpGet("http://192.168.1.202:8080/WPVHomeController_Scheduler_WebApp/xbee/xdevice/IRR_PUMP"));
				//log.info("Response: " + response);
				if (!response.toString().equals("OK")) {
					pumping = false;
					log.warn("Could not switch off irrigation pump for WaterTank, exiting");
					switchOffPumpNow();
					break;
				} else {
					log.info("Pump switched off OK");
				}
				
				
			} catch (IOException io) {
				log.warn("Could not switch off irrigation pump for WaterTank, exiting");
				pumping = false;
				break;
			}
			
			if (currentDepth <= minFillDepth) {
				log.info( currentDepth + " <= " + minFillDepth + " (min depth): True!" );
				pumping = false;
				log.info("Stopping");
				log.info("We are now on depth: " + currentDepth);
			} else if (currentDepth <= requestedFillDepth) {
				log.info( currentDepth + " <= " + requestedFillDepth + " (requested depth): True!" );
				pumping = false;				
				log.info("We are now on depth: " + currentDepth);
			} else if (noCycles >= maxNoCycles) {
				log.info( "We have reached the max no of cycles for the job: " + maxNoCycles );
				pumping = false;
				log.info("We are now on depth: " + currentDepth);
			} else {
				log.info( currentDepth + " <= " + requestedFillDepth + ": False!" );								
				
				try {
					//Thread.sleep(310000);
					Thread.sleep(60000);
				} catch (Exception e) {
					log.warn("Thread sleep got interruped");
					//Do nothing - it is OK if we get interrupted
				}
				
				
			}
						
		}
		
		switchOffPumpNow();
	}
	
	private void switchOffPumpNow() {
		// We should use the XoffController here
		log.info("Breaking out ... switching off pump ...");
		boolean switchedOff = false;
		while (!switchedOff) {
			try {
				Thread.sleep(2000);
				StringBuffer response = HttpUtil.INSTANCE.getResponseContent(HttpUtil.INSTANCE.doHttpGet("http://192.168.1.202:8080/WPVHomeController_Scheduler_WebApp/xbee/xoff/3/D/7"));
				log.info("Response: " + response);
				if (response.toString().equals("OK")) {
					switchedOff = true;
				}
			} catch (Exception e) {
				log.error("Really bad - ", e);
			}
		}
	}
	
	public void stopPumping() {
		pumping = false;
	}
	
	/**
	 * @return if we are currently pumping
	 */
	public boolean isPumping() {
		return pumping;
	}
	
}
