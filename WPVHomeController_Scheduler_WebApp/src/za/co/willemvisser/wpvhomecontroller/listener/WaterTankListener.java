package za.co.willemvisser.wpvhomecontroller.listener;

import java.util.Date;

import org.apache.log4j.Logger;

import za.co.willemvisser.wpvhomecontroller.util.WaterTankManager;

public class WaterTankListener implements Runnable {

	private Thread myThread = null;
	private boolean isRunning = false;
	private Date lastCheckTimeStamp_tanktopup = new Date();
	private Date lastCheckTimeStamp_pumpturnoff = new Date();
	
	static Logger log = Logger.getLogger(WaterTankListener.class.getName());	
	
	public WaterTankListener() {
		super();
		isRunning = true;
		myThread = new Thread(this);
		myThread.start();
	}
	
	@Override
	public void run() {
		log.info("Starting WaterTankListener ...");
		while (isRunning) {
			try {
				
				if (shouldWeCheckNowForTankTopUp()) {
					log.debug("Checking if we should fill up the tank now ...");
					if (!WaterTankManager.INSTANCE.isPumping()) {
						log.debug("Not pumping - evaluating depth now: " + WaterTankManager.INSTANCE.getWaterTankDepthPercentage());
						Date now = new Date();
						long milliSecondsPassed = now.getTime() - WaterTankManager.INSTANCE.getWaterTankLastUpdated().getTime();								
						if (milliSecondsPassed/1000/60 > 3) {
							log.info("Not switching on pump as the Tank Depth data is stale (>3mins): " + (milliSecondsPassed/1000/60) + " mins");							
						} else {
							if (WaterTankManager.INSTANCE.getWaterTankDepthPercentage() < 80) {
								log.info("We need to top up the tank - switching on borehole pump now...");
								WaterTankManager.INSTANCE.startPumping();
							}
						}
												
					} else {
						log.info("Pump is already on - not taking any further action");
					}
				}
				
				if (shouldWeCheckNowForTurningOffPump()) {
					log.debug("Checking if we should turn off the pump ...");
					if (WaterTankManager.INSTANCE.shouldWeStopPumping()) {
						WaterTankManager.INSTANCE.stopPumping();
					}
				}
				
				
			} catch (Exception e) {
				log.error("WaterTankListener Error: " + e);
			}
			
			
			/* Pause */
			try {
				myThread.sleep(10000);		//TODO - maybe 30 seconds is fine?
				myThread.yield();					
			} catch (Exception e) {
				//Do Nothing
			}
		}

	}
	
	/**
	 *  Stop the thread 
	 */
	public void stop() {		
		isRunning = false;
	}
	
	/**
	 * This method checks if enough time has passed between tank refills for us to check again
	 *  and possible fill up again.  A good value here is an hour to give the borehole enough timee
	 *  to fill up again.
	 */
	private boolean shouldWeCheckNowForTankTopUp() {
		Date now = new Date();
		long milliSecondsPassed = now.getTime() - lastCheckTimeStamp_tanktopup.getTime();				
		if (milliSecondsPassed/1000/60 > 30) {
			lastCheckTimeStamp_tanktopup = now;
			return true;
		} else {			
			return false;
		}
		
	}
	
	/**
	 * This method checks if enough time has passed between tank refills for us to check again
	 *  and possible fill up again.  A good value here is an hour to give the borehole enough timee
	 *  to fill up again.
	 */
	private boolean shouldWeCheckNowForTurningOffPump() {
		Date now = new Date();
		long milliSecondsPassed = now.getTime() - lastCheckTimeStamp_pumpturnoff.getTime();				
		if (milliSecondsPassed/1000/60 > 1) {
			lastCheckTimeStamp_pumpturnoff = now;
			return true;
		} else {			
			return false;
		}
		
	}

}
