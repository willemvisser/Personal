package za.co.willemvisser.wpvhomecontroller.util;

import org.apache.log4j.Logger;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public enum MQTTUtil {
	
	INSTANCE;
	
	static Logger log = Logger.getLogger(MQTTUtil.class.getName());
	
	private int qos             = 2;
	private String broker       = "tcp://192.168.1.200:1883";
	private String clientId     = "wpvserver";

	public static final String TOPIC_TANK1_DEPTH = "wpvserver/tank1_depth";
	
	public void sendMessage(String topic, String content) {
		        
        MemoryPersistence persistence = new MemoryPersistence();

        try {
            MqttClient sampleClient = new MqttClient(broker, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            log.debug("Connecting to broker: "+broker);
            sampleClient.connect(connOpts);
            log.debug("Connected");
            log.debug("Publishing message: "+content);
            MqttMessage message = new MqttMessage(content.getBytes());
            message.setQos(qos);
            sampleClient.publish(topic, message);
            log.debug("Message published");
            sampleClient.disconnect();
            log.debug("Disconnected");
            
        } catch(MqttException me) {
            log.error("reason "+me.getReasonCode());
            log.error("msg "+me.getMessage());
            log.error("loc "+me.getLocalizedMessage());
            log.error("cause "+me.getCause());
            log.error("excep "+me);
            log.error(me);
        }
		
	}

}
