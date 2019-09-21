package za.co.willemvisser.wpvhomecontroller.listener;

import org.apache.log4j.Logger;

public class WaterTankListener implements Runnable {

	private Thread myThread = null;
	private boolean isRunning = false;
	
	static Logger log = Logger.getLogger(WaterTankListener.class.getName());	
	
	public WaterTankListener() {
		super();
		myThread = new Thread(this);
		myThread.start();
	}
	
	@Override
	public void run() {
		while (isRunning) {
			try {
				
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

}
