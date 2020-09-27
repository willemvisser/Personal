package za.co.willemvisser.wpvhomecontroller.util;

import java.io.IOException;

import org.apache.log4j.Logger;

import za.co.willemvisser.wpvhomecontroller.config.ConfigController;


public enum WaterTankFillUtil {

	INSTANCE;
	
	// http://192.168.1.202:8080/WPVHomeController_Scheduler_WebApp/xbee/xdevice/IRR_OUTSIDE
	// IRR_PUMP
	
	static Logger log = Logger.getLogger(WaterTankFillUtil.class.getName());
	
	private boolean pumping = false;
	
	private static final int maxFillInPercentage = 10;  //This is the maximum percentage we want to fill in one job
	
	
	private double minFillDepthPercentage = 80;				//This is the depth we want to fill to
	private double requestedFillDepth = 9999;		 
													
	private double currentDepth = 0;
	
	private int DEFAULT_FILL_TIME_IN_SECS = 80;		
	
	/**
	 * @param toWhatHeightInCm
	 * @param maxNoCycles	The maximum number of cycles we will pump in one job - safeguard to not drain the wellpoint
	 * @param fillTimeInSecs The number of seconds to keep on pumping
	 */
	public void startPumping(int toWhatHeightInCm, int maxNoCycles, int fillTimeInSecs) {
		//TODO - when we start - we should check if all sensors are alive.
		//TODO - if after x attempts the level hasn't changed - we should stop as it means probably something is broken - 
		//			either the pipe or sensor
		//TOOD - we should add a timestamp to depth reading
		
		pumping = true;
		this.requestedFillDepth = toWhatHeightInCm;
		
		int noCycles = 0;
		int pumpingTime = fillTimeInSecs == -1 ? DEFAULT_FILL_TIME_IN_SECS * 1000 : fillTimeInSecs * 1000;
		
		
		/*
		 * Before we even start - we are going to check the depth
		 */
		
		try {
			updateCurrentDepth();
			
			TelegramUtil.INSTANCE.sendMessage("Water Tank Fill Job ... Start with pumpingTime=" + pumpingTime + 
					" ... Current Depth: " + currentDepth);
			
			/*
			if (WeatherService.INSTANCE.isItRainingToday() || WeatherService.INSTANCE.isItRainingTomorrow()) {
				if (currentDepth <= 85) {
					log.info("It is raining today/tomorrow, and depth <= 85 so not filling the tank (or stopping).");
					TelegramUtil.INSTANCE.sendMessage("It is raining today/tomorrow, and depth <= 85 so not filling the tank (or stopping).");
					return;
				} else {
					log.info("It is raining tomorrow, but depth > 85, so we need to fill the tank");
				}
			} else {
				log.info("Not raining today or tomorrow");
			}
			*/
			
			if (this.requestedFillDepth == -1) {
				this.requestedFillDepth = currentDepth - maxFillInPercentage;
			}
			
			if (currentDepth >= minFillDepthPercentage) {
				log.info("We are not going to add any water:" + currentDepth + " <= " + minFillDepthPercentage + " (min depth)" );
				TelegramUtil.INSTANCE.sendMessage("Water Tank Fill ... End ... We are already at required minimum depth: " + minFillDepthPercentage);
				return;
			} else if (currentDepth >= requestedFillDepth) {
				log.info("We are not going to add any water:" +  currentDepth + " <= " + requestedFillDepth + " (requested depth): True!" );
				TelegramUtil.INSTANCE.sendMessage("Water Tank Fill ... End ... We are already at requested depth: " + requestedFillDepth);
				return;
			}
			
		} catch (Exception e) {
			log.warn("Could not determine the depth of the tank, so we are not going to pump anything");		
			TelegramUtil.INSTANCE.sendMessage("Water Tank Fill ... Not Started ... Unable to determine tank depth - nothing was pumped.");
			return;
		}
		
		
		
				
		while (pumping) {
			noCycles++;
			
			try {
				updateCurrentDepth();
				
				if (this.requestedFillDepth == -1) {
					this.requestedFillDepth = currentDepth - maxFillInPercentage;
				}
				
			} catch (Exception e) {
				log.warn("Could not determine the depth of the tank, exiting");
				TelegramUtil.INSTANCE.sendMessage("Water Tank Fill ... End ... Unable to determine tank depth - stopping at depth: " + currentDepth + " after " + noCycles + " cycles.");
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
					TelegramUtil.INSTANCE.sendMessage("Water Tank Fill ... End ... Unable to switch on pump - stopping at depth: " + currentDepth + " after " + noCycles + " cycles.");
					break;
				}
				
				log.info("Pumped switched on OK");
				
				Thread.sleep(150);
			} catch (IOException io) {
				log.warn("Could not switch on irrigation pump for WaterTank, exiting");
				TelegramUtil.INSTANCE.sendMessage("Water Tank Fill ... End ... Unable to switch on pump - stopping at depth: " + currentDepth + " after " + noCycles + " cycles.");
				pumping = false;
				break;
			} catch (InterruptedException ie) {
				//DO nothing
			}
			
			
			try {
				
				Thread.sleep(pumpingTime);
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
					TelegramUtil.INSTANCE.sendMessage("Water Tank Fill ... End ... Unable to switch off pump - stopping at depth: " + currentDepth + " after " + noCycles + " cycles.");
					switchOffPumpNow();
					break;
				} else {
					log.info("Pump switched off OK");
				}
				
				
			} catch (IOException io) {
				log.warn("Could not switch off irrigation pump for WaterTank, exiting");
				TelegramUtil.INSTANCE.sendMessage("Water Tank Fill ... End ... Unable to switch off pump - stopping at depth: " + currentDepth + " after " + noCycles + " cycles.");
				pumping = false;
				break;
			}
			
			if (currentDepth <= minFillDepthPercentage) {
				log.info( currentDepth + " <= " + minFillDepthPercentage + " (min depth): True!" );
				pumping = false;
				log.info("Stopping");
				log.info("Stopping, we are now on depth: " + currentDepth);
				TelegramUtil.INSTANCE.sendMessage("Water Tank Fill ... End ... stopping at depth: " + currentDepth + " after " + noCycles + " cycles, we have reached minimum fill depth: " + minFillDepthPercentage);
			} else if (currentDepth <= requestedFillDepth) {
				log.info( currentDepth + " <= " + requestedFillDepth + " (requested depth): True!" );
				pumping = false;				
				log.info("We are now on depth: " + currentDepth);
				TelegramUtil.INSTANCE.sendMessage("Water Tank Fill ... End ... stopping at depth: " + currentDepth + " after " + noCycles + " cycles, we have reached requested fill depth: " + requestedFillDepth);
			} else if (noCycles >= maxNoCycles) {
				log.info( "We have reached the max no of cycles for the job: " + maxNoCycles );
				pumping = false;
				log.info("We are now on depth: " + currentDepth);
				TelegramUtil.INSTANCE.sendMessage("Water Tank Fill ... End ... stopping at depth: " + currentDepth + " after " + noCycles + " cycles (Max cycles reached)");
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
	
	/**
	 * Retrieves the current tank depth
	 * @throws IOException
	 * @throws NumberFormatException
	 */
	private void updateCurrentDepth() throws IOException, NumberFormatException {
		
		
		currentDepth = WaterTankManager.INSTANCE.getWaterTankDepthPercentage();
										
		log.info("Tank Depth: " + currentDepth);
		
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
