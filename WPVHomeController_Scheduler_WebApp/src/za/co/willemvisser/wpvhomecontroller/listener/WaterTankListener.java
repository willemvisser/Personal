package za.co.willemvisser.wpvhomecontroller.listener;

import java.util.Date;

import org.apache.log4j.Logger;

public class WaterTankListener implements Runnable {

	private Thread myThread = null;
	private boolean isRunning = false;
	private Date lastCheckTimeStamp = new Date();
	
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
				
				if (shouldWeCheckNow()) {
					log.info("WaterTankListener - Checking if we should fill up the tank now ...");
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
	private boolean shouldWeCheckNow() {
		Date now = new Date();
		long milliSecondsPassed = now.getTime() - lastCheckTimeStamp.getTime();		
		log.info("MillPassed: " + milliSecondsPassed + ", Mins: " + (milliSecondsPassed/1000/60));
		if (milliSecondsPassed/1000/60 > 1) {
			lastCheckTimeStamp = now;
			return true;
		} else {			
			return false;
		}
		
	}

}
