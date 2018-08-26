package za.co.willemvisser.wpvhomecontroller.scheduler.web.context;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;

import za.co.willemvisser.wpvhomecontroller.mqtt.MQTTListener;

public class MQTTServletListener implements ServletContextListener {

	private MQTTListener myListener = null;
	static Logger log = Logger.getLogger(MQTTServletListener.class.getName());
	
	@Override
	public void contextInitialized(ServletContextEvent arg0) {
									
		try {			
			myListener = new MQTTListener();			
        } catch (Exception e) {
        	log.error("Could not stop thread: " + e);
        }
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		try {
			
			myListener.stop();
			
        } catch (Exception e) {
        	log.error("Could not stop thread: " + e);
        }

	}

	

}
