package za.co.willemvisser.wpvhomecontroller.mqtt;

import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import za.co.willemvisser.wpvhomecontroller.config.ConfigController;
import za.co.willemvisser.wpvhomecontroller.util.HttpUtil;
import za.co.willemvisser.wpvhomecontroller.util.NumberUtil;

public class MQTTListener implements Runnable, MqttCallback {

	private int qos             = 0;
	private String broker       = "tcp://192.168.1.200:1883";
	private String clientId     = "wpvserver";
	
	static Logger log = Logger.getLogger(MQTTListener.class.getName());	
	private MqttClient client;
	private boolean isRunning = false;
	
	private MemoryPersistence persistence = new MemoryPersistence();
	private Thread myThread = null;
	
	public static final String TOPIC_CMD_TANK1_DEPTH = "wpvserver/cmd/tank1_depth";
	public static final String TOPIC_STAT_TANK1_DEPTH = "wpvserver/stat/tank1_depth";
	
	public MQTTListener() {
		super();
		myThread = new Thread(this);
		myThread.start();
	}
	
	
	@Override
	public void run() {
		isRunning = true;
		try {
			connectAndSubscribeToServer();
			
			while (isRunning) {
				//Do Nothing
				
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
			client.disconnect();
		} catch (Exception e) {
			log.error("Could not disconnect: " + e);
		}
		isRunning = false;
	}
	
	private void connectAndSubscribeToServer() throws MqttException {
		client = new MqttClient(broker, clientId, persistence);
        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(true);
        connOpts.setKeepAliveInterval(15);
        log.info("Connecting to broker: "+broker);
        client.connect(connOpts);
        log.info("Connected");    
        client.setCallback(this);        
        client.subscribe(TOPIC_CMD_TANK1_DEPTH, qos);
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
		
		log.info("Received on topic '" + topic + "': " + mqttMessage.toString());
		if (topic.equals(TOPIC_CMD_TANK1_DEPTH)) {
			postCurrentTank1Depth();
		} else {
			log.error("Unknown message!: " + topic + " -> " + mqttMessage);
		}
		log.info("messageArrived Done (DELME)");
	}
	
	private void postCurrentTank1Depth() {
		try {
			log.info("Posting Current Tank Depth...");
			StringBuffer response = HttpUtil.INSTANCE.getResponseContent(HttpUtil.INSTANCE.doHttpGet(
					ConfigController.INSTANCE.getGeneralProperty(ConfigController.PROPERTY_TANK_LEVEL_HTTP_URL).getValue()));
						
			log.info("Tank Depth Response: " + response);
			
			double currentDepth = 0;
			try {
				double tankDepthInCm = Double.parseDouble(response.toString()); 								
				currentDepth = NumberUtil.round( ((198.0 - tankDepthInCm + 13.2) / 198.0 * 100), 2);  
				
			} catch (Exception ee) {
				currentDepth = -111;
				log.error("Could not retrieve current tank depth, posting a value of -111");
				log.error(ee);				
			}
			
			log.info("Current Depth: " + currentDepth);
			
//			MqttMessage message = new MqttMessage((currentDepth+"").getBytes());
//			log.info("MQTT Message: " + message.toString());
//            message.setQos(qos);
            //client.publish(TOPIC_STAT_TANK1_DEPTH, message);
			log.info("Sending MQTT message ...");
            client.publish(TOPIC_STAT_TANK1_DEPTH, (currentDepth+"").getBytes(), qos, false);
            log.info("Tank1 STAT MQTT Message published");
            
		} catch (Exception e) {
			log.error("Could not post tank depth to MQTT: " + e);
		}
	}

}
