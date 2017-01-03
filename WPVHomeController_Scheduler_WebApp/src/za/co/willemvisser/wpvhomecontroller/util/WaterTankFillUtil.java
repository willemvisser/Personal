package za.co.willemvisser.wpvhomecontroller.util;

import java.io.IOException;

import org.apache.log4j.Logger;

public enum WaterTankFillUtil {

	INSTANCE;
	
	// http://192.168.1.202:8080/WPVHomeController_Scheduler_WebApp/xbee/xdevice/IRR_OUTSIDE
	// IRR_PUMP
	
	static Logger log = Logger.getLogger(WaterTankFillUtil.class.getName());
	
	private boolean pumping = false;
	
	private static final int maxLitres = 800;
	private static final int litrePerFill = 20;
	
	private int litresAdded = 0;
	
	public void startPumping() {
		pumping = true;
		int noCycles = 0;
		while (pumping) {
			noCycles++;
			litresAdded += litrePerFill;
			
			try {
				StringBuffer response = HttpUtil.INSTANCE.getResponseContent(HttpUtil.INSTANCE.doHttpGet("http://192.168.1.202:8080/WPVHomeController_Scheduler_WebApp/xbee/xon/3/D/7"));
				//log.info("Response: " + response);
				if (!response.toString().equals("OK")) {
					pumping = false;
					log.warn("Could not switch on irrigation pump for WaterTank, exiting");
					break;
				}
				
				Thread.sleep(150);
			} catch (IOException io) {
				log.warn("Could not switch on irrigation pump for WaterTank, exiting");
				pumping = false;
				break;
			} catch (InterruptedException ie) {
				//DO nothing
			}
			
			
			try {
				StringBuffer response = HttpUtil.INSTANCE.getResponseContent(HttpUtil.INSTANCE.doHttpGet("http://192.168.1.202:8080/WPVHomeController_Scheduler_WebApp/xbee/xdevice/IRR_OUTSIDE"));
				//log.info("Response: " + response);
				if (!response.toString().equals("OK")) {
					pumping = false;
					log.warn("Could not switch on irrigation valve for WaterTank, exiting");
					switchOffPumpNow();
					break;
				}
			} catch (IOException io) {
				log.warn("Could not switch on irrigation valve for WaterTank, exiting");
				pumping = false;
				break;
			}
			
			
			
			try {
				Thread.sleep(45000);
			} catch (Exception e) {
				log.warn("Thread sleep got interruped");
				//Do nothing - it is OK if we get interrupted
			}
			
			
			try {
				StringBuffer response = HttpUtil.INSTANCE.getResponseContent(HttpUtil.INSTANCE.doHttpGet("http://192.168.1.202:8080/WPVHomeController_Scheduler_WebApp/xbee/xdevice/IRR_OUTSIDE"));
				//log.info("Response: " + response);
				if (!response.toString().equals("OK")) {
					pumping = false;
					log.warn("Could not switch off irrigation valve for WaterTank, exiting");
					switchOffPumpNow();
				} 
				Thread.sleep(150);
			} catch (IOException io) {
				log.warn("Could not switch on irrigation valve for WaterTank, exiting");
				pumping = false;			
			} catch (InterruptedException ie) {
				//DO nothing
			}
			
			
			try {
				StringBuffer response = HttpUtil.INSTANCE.getResponseContent(HttpUtil.INSTANCE.doHttpGet("http://192.168.1.202:8080/WPVHomeController_Scheduler_WebApp/xbee/xdevice/IRR_PUMP"));
				//log.info("Response: " + response);
				if (!response.toString().equals("OK")) {
					pumping = false;
					log.warn("Could not switch off irrigation pump for WaterTank, exiting");
					switchOffPumpNow();
					break;
				}
				
				
			} catch (IOException io) {
				log.warn("Could not switch off irrigation pump for WaterTank, exiting");
				pumping = false;
				break;
			}
			
			
			if (litresAdded >= maxLitres) {
				pumping = false;
				log.info("Stopping - we've pumped '" + litresAdded + "' litres!!");
			} else {
				
				log.info("Litres Added: " + litresAdded + "l with " + noCycles + " cycles.");
				
				try {
					Thread.sleep(240000);
				} catch (Exception e) {
					log.warn("Thread sleep got interruped");
					//Do nothing - it is OK if we get interrupted
				}
				
				//Every 200l (yes we can do a remainder here rather dumbass) - sleep extra 200s
				if (litresAdded == 180 || litresAdded == 360 || litresAdded == 540 || litresAdded == 720) {					
					try {
						Thread.sleep(200000);
					} catch (Exception e) {
						log.warn("Thread sleep got interruped");
						//Do nothing - it is OK if we get interrupted
					}
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
	
}
