package za.co.willemvisser.wpvhomecontroller.mqtt;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONArray;
import org.json.JSONObject;

import za.co.willemvisser.wpvhomecontroller.config.ConfigController;
import za.co.willemvisser.wpvhomecontroller.util.DateUtil;
import za.co.willemvisser.wpvhomecontroller.util.HttpUtil;
import za.co.willemvisser.wpvhomecontroller.util.NumberUtil;
import za.co.willemvisser.wpvhomecontroller.util.WaterTankManager;
import za.co.willemvisser.wpvhomecontroller.weather.OpenWeatherService;

public class MQTTListener implements Runnable, MqttCallback {

	private int qos             = 0;
	private String broker       = "tcp://localhost:1883";
	private String clientId     = "wpvserver";
	
	static Logger log = Logger.getLogger(MQTTListener.class.getName());	
	private MqttClient client;
	private boolean isRunning = false;
	
	private MemoryPersistence persistence = new MemoryPersistence();
	private Thread myThread = null;
	
	public static final String TOPIC_CMD_TANK1_DEPTH = "wpvserver/cmd/tank1_depth";
	public static final String TOPIC_STAT_TANK1_DEPTH = "wpvserver/stat/tank1_depth";
	
	public static final String TOPIC_CMD_WEATHER_TODAY = "wpvserver/cmd/weather_today";
	public static final String TOPIC_STAT_WEATHER_TODAY = "wpvserver/stat/weather_today";
	
	public static final String TOPIC_STAT_BOREHOLEPUMP = "stat/sonoff_boreholepump/STATUS";
	public static final String TOPIC_CMD_BOREHOLEPUMP_STATUS = "cmnd/sonoff_boreholepump/status";
	
	public static final String TOPIC_CMD_BOREHOLEPUMP_POWER = "cmnd/sonoff_boreholepump/power";
	
	private Date lastRequestedUpdateForBoreholePumpStatus;
	private static final int secondsToWaitBetweenBoreholePumpStatusUpdates = 30;
	
	public MQTTListener() {
		super();
		myThread = new Thread(this);
		myThread.start();
	}
	
	
	@Override
	public void run() {
		isRunning = true;
		lastRequestedUpdateForBoreholePumpStatus = new Date();
		try {
			connectAndSubscribeToServer();
			
			while (isRunning) {
				try {
					myThread.sleep(250);
					myThread.yield();	
					
					retrieveLatestStatusForWaterTankPump();
				} catch (Exception e) {
					//Do Nothing
				}				
			}
		} catch (Exception e) {
			log.error("Could not start MQTTListener thread: " + e);
		}
	}		
	
	/**
	 *  Stop the thread 
	 */
	public void stop() {
		try {
			if (client != null) {
				client.disconnect();
			}
		} catch (Exception e) {
			log.error("Could not disconnect: " + e);
		}
		isRunning = false;
	}
	
	private void connectAndSubscribeToServer() throws MqttException {
		client = new MqttClient(broker, clientId, persistence);
		client.setTimeToWait(500);
        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(true);
        connOpts.setKeepAliveInterval(15);
        log.info("Connecting to broker: "+broker);
        client.connect(connOpts);
        log.info("Connected");    
        client.setCallback(this);        
        client.subscribe(TOPIC_CMD_TANK1_DEPTH, qos);
        client.subscribe(TOPIC_CMD_WEATHER_TODAY, qos);
        client.subscribe(TOPIC_STAT_BOREHOLEPUMP, qos);
	}

	@Override
	public void connectionLost(Throwable arg0) {
		log.error("Connection lost");
	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
		
		log.debug("Received on topic '" + topic + "': " + mqttMessage.toString());
		if (topic.equals(TOPIC_CMD_TANK1_DEPTH)) {
			postCurrentTank1Depth();
		} else if (topic.startsWith(TOPIC_CMD_WEATHER_TODAY)) {			
			log.debug("mqqtMessage: " + mqttMessage.toString());
			postTodaysWeather(mqttMessage.toString());
		} else if (topic.startsWith(TOPIC_STAT_BOREHOLEPUMP)) {
			log.debug("Received Borehole Pump Stat event...");
			processWaterTankPumpSTATEvent(mqttMessage.toString());
		} else {
			log.error("Unknown message!: " + topic + " -> " + mqttMessage);
		}
		
	}
	
	private void retrieveLatestStatusForWaterTankPump() {
		try {
			
			Calendar calAWhileAgo = new GregorianCalendar();
			calAWhileAgo.add(Calendar.SECOND, -secondsToWaitBetweenBoreholePumpStatusUpdates);
						
			if (lastRequestedUpdateForBoreholePumpStatus.before(calAWhileAgo.getTime())) {				
				client.publish(TOPIC_CMD_BOREHOLEPUMP_STATUS, new MqttMessage(("").getBytes()));
				lastRequestedUpdateForBoreholePumpStatus = new Date();
			}		
		} catch (Exception e) {
			log.error("Could not post to MQTT to retrieve latest status for Tank Pump Sonoff: " + e);
			//We know we could get "Client is not connected anymore" exception - we should handle that
			try {
				myThread.sleep(2500);
				client.reconnect();
				log.info("MQTT Reconnect OK");
			} catch (Exception ee) {
				log.error("We could not reconnect to MQTT");
			}
		}
	}
	
	
	/**
	 * Posts the current main tank depth to MQTT 
	 */
	private void postCurrentTank1Depth() {
		try {
//			log.debug("Posting Current Tank Depth...");
//			StringBuffer response = HttpUtil.INSTANCE.getResponseContent(HttpUtil.INSTANCE.doHttpGet(
//					ConfigController.INSTANCE.getGeneralProperty(ConfigController.PROPERTY_TANK_LEVEL_HTTP_URL).getValue()));

			double currentDepth = WaterTankManager.INSTANCE.getWaterTankDepthPercentage();
//			log.info("Tank Depth Response: " + currentDepth);
//			
//			double currentDepth = 0;
//			try {
//				double tankDepthInCm = Double.parseDouble(response.toString()); 								
//				currentDepth = NumberUtil.round( ((198.0 - tankDepthInCm + 13.2) / 198.0 * 100), 2);  
//				
//			} catch (Exception ee) {
//				currentDepth = -111;
//				log.error("Could not retrieve current tank depth, posting a value of -111");
//				log.error(ee);				
//			}
			
			log.info("Current Depth: " + currentDepth);
			
//			StringBuffer responseForLastUpdated = HttpUtil.INSTANCE.getResponseContent(HttpUtil.INSTANCE.doHttpGet(
//					ConfigController.INSTANCE.getGeneralProperty(ConfigController.PROPERTY_TANK_LEVEL_LASTUPDATED_HTTP_URL).getValue()));
//			log.debug("Tank Level Last Updated: " + responseForLastUpdated);
			
			
			String responseForLastUpdated = DateUtil.INSTANCE.convertDateTimeToString( WaterTankManager.INSTANCE.getWaterTankLastUpdated() );

			log.debug("Sending MQTT message ...");
            client.publish(TOPIC_STAT_TANK1_DEPTH, new MqttMessage((currentDepth + "|" + responseForLastUpdated).getBytes()));            
            
		} catch (Exception e) {
			log.error("Could not post tank depth to MQTT: " + e);
		}
	}
	
	/**
	 *  Posting today's weather to MQTT 
	 */
	private void postTodaysWeather(String stationID) {
		try {
			log.debug("Posting Today's Weather to MQTT ...");
									
            client.publish(TOPIC_STAT_WEATHER_TODAY, new MqttMessage((OpenWeatherService.INSTANCE.getCurrentForecast(stationID).toJSONString()).getBytes()));
            log.debug("Today's Weather STAT MQTT Message published");
            
		} catch (Exception e) {
			log.error("Could not today's weather to MQTT: " + e);
		}
	}
	
	/**
	 *  {"Status":{"Module":1,"FriendlyName":["Borehole Pump"],"Topic":"sonoff_boreholepump","ButtonTopic":"0","Power":0,"PowerOnState":3,
	 *  "LedState":1,"SaveData":1,"SaveState":1,"ButtonRetain":0,"PowerRetain":0}} 
	 */
	private void processWaterTankPumpSTATEvent(String mqttMessage) {		
		
		try {
			JSONObject jsonObj = new JSONObject(mqttMessage);			
			JSONObject jsonStatus = jsonObj.getJSONObject("Status");			
			int power = jsonStatus.getInt("Power");			
            
			boolean shouldWeStopPumping = WaterTankManager.INSTANCE.updatePowerStatusAndReturnSignalToStop(power);
			if (shouldWeStopPumping) {
				client.publish(TOPIC_CMD_BOREHOLEPUMP_POWER, new MqttMessage(("off").getBytes()));
			}
			
            
		} catch (Exception e) {
			log.error("Could not process WaterTankPump STAT mqtt: " + e);
		}
	}

}
