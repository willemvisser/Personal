package za.co.willemvisser.wpvhomecontroller.util;

import java.util.Date;

import org.apache.log4j.Logger;

import za.co.willemvisser.wpvhomecontroller.config.ConfigController;

public enum WaterTankManager {
	
	INSTANCE;
	
	static Logger log = Logger.getLogger(WaterTankManager.class.getName());
	private boolean pumping = false;	
	private static final int maxFillInCms = 10;  //This is the maximum number of centimeters we want to fill in one job
	
	private double pumpingStartDepthPercentage = 0;		//The depth at which we started pumping
	private Date pumpingStartTime;						//The timestamp of when we started pumping
	
	/**
	 * @return the depth of the main house water tank in CM
	 */
	public double getWaterTankDepthPercentage() {
		double currentDepth = -111;
		try {
			StringBuffer response = HttpUtil.INSTANCE.getResponseContent(HttpUtil.INSTANCE.doHttpGet(
				ConfigController.INSTANCE.getGeneralProperty(ConfigController.PROPERTY_TANK_LEVEL_HTTP_URL).getValue()));
					
			log.debug("Tank Depth Response: " + response);					
		 
			double tankDepthInCm = Double.parseDouble(response.toString()); 								
			currentDepth = NumberUtil.round( ((198.0 - tankDepthInCm + 13.2) / 198.0 * 100), 2);  
			
		} catch (Exception ee) {			
			log.error("Could not retrieve current tank depth, posting a value of -111");
			log.error(ee);				
		}
		return currentDepth;
	}

	public boolean isPumping() {
		return pumping;
	}

	public void setPumping(boolean pumping) {
		this.pumping = pumping;
		
		if (pumping) {
			pumpingStartDepthPercentage = getWaterTankDepthPercentage();
			pumpingStartTime = new Date();
		}
	}

	public Date getPumpingStartTime() {
		return pumpingStartTime;
	}

	public double getPumpingStartDepthPercentage() {
		return pumpingStartDepthPercentage;
	}
	
	/*
	 * http://192.168.1.141/cm?cmnd=Power
	 * http://<ip>/cm?cmnd=Power%20TOGGLE
http://<ip>/cm?cmnd=Power%20On
http://<ip>/cm?cmnd=Power%20off
	 */
	
}
