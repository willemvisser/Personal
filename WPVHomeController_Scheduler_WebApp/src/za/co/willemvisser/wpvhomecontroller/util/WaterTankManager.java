package za.co.willemvisser.wpvhomecontroller.util;

import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.log4j.Logger;

import za.co.willemvisser.wpvhomecontroller.config.ConfigController;

public enum WaterTankManager {
	
	INSTANCE;
	
	static Logger log = Logger.getLogger(WaterTankManager.class.getName());
	private boolean pumping = false;	
	private static final int maxDepthInPercentage = 75;  //This is the maximum number of centimeters we want to fill in one job
	private static final int maxTimeInMinsWeCanPump = 10;
	
	private double pumpingStartDepthPercentage = 0;		//The depth at which we started pumping
	private double pumpingStopDepthPercentage = 0;		//The depth at which we stopped pumping
	private Date pumpingStartTime;						//The timestamp of when we started pumping
	private Date pumpingStopTime;						//The timestamp of when we stopped pumping
	private double waterTankDepthPercentageCache = -1;
	
	private static final SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm");
	
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

	private void setPumping(boolean pumping) {
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
	
	private void updateWaterTankDepthCache() {
		waterTankDepthPercentageCache = getWaterTankDepthPercentage();
	}
	
	/**
	 * Validate that we've received a stat event the last x mins, and in case
	 * 	we haven't, and we are pumping, we should send an Alert, and try and
	 *  hard stop pumping 
	 */
	public boolean shouldWeStopPumping() {
		updateWaterTankDepthCache();
		
		Calendar calAWhileAgo = new GregorianCalendar();
		calAWhileAgo.add(Calendar.MINUTE, -maxTimeInMinsWeCanPump);
		
		if (pumping && waterTankDepthPercentageCache >= maxDepthInPercentage) {
						
			StringBuffer buffer = new StringBuffer("Max Depth achieved, we should stop pumping.\r\n");
			buffer.append("StartDepth: "); buffer.append(pumpingStartDepthPercentage); buffer.append(" StopDepth: "); buffer.append(pumpingStopDepthPercentage);
			buffer.append("\r\n");
			buffer.append("Started At: "); buffer.append(timeFormatter.format(pumpingStartTime)); 
			
			log.info(buffer);
			TelegramUtil.INSTANCE.sendMessage(URLEncoder.encode(buffer.toString()));
							
			return true;  	//Return true to ask to stop pumping
		} else if (pumping && pumpingStartTime.before(calAWhileAgo.getTime())) {
			StringBuffer msgBuffer = new StringBuffer("Max Time we are allowed to pump in one go achieved, stopping.\r\n");			
			msgBuffer.append("Start Depth: ");
			msgBuffer.append(pumpingStartDepthPercentage);
			msgBuffer.append("\r\n");
			msgBuffer.append("Current depth: ");
			msgBuffer.append(waterTankDepthPercentageCache);
			msgBuffer.append("\r\n");
			
			log.info(msgBuffer);
			TelegramUtil.INSTANCE.sendMessage(URLEncoder.encode(msgBuffer.toString()));
			return true;  	//Return true to ask to stop pumping
		} else {
			return false;	//Return false to ask to continue pumping
		}
	}
	
	/**
	 * @param power  0 for false, 1 for true (pumping)
	 * @return  if true, stop pumping
	 */
	public boolean updatePowerStatusAndReturnSignalToStop(int power) {
		double waterTankDepthPercentage = getWaterTankDepthPercentage();
		
		
		if (!pumping && power == 1) {
			//We have started to pump - set all the variables
			pumping = true;
			pumpingStartTime = new Date();
			pumpingStartDepthPercentage = waterTankDepthPercentage;
		} else if (pumping && power == 0) {
			pumping = false;
			pumpingStopTime = new Date();
			pumpingStopDepthPercentage = waterTankDepthPercentage;
									
			StringBuffer buffer = new StringBuffer("Pumping completed.\r\n");
			buffer.append("StartDepth: "); buffer.append(pumpingStartDepthPercentage); buffer.append(" StopDepth: "); buffer.append(pumpingStopDepthPercentage);
			buffer.append("\r\n");
			buffer.append("Started At: "); buffer.append(timeFormatter.format(pumpingStartTime)); buffer.append(" Stopped At: "); buffer.append(timeFormatter.format(pumpingStopTime));
			
			log.info(buffer);
			TelegramUtil.INSTANCE.sendMessage(URLEncoder.encode(buffer.toString()));
						
		} 
		
		return shouldWeStopPumping();

	}
	
}
