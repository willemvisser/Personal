package za.co.willemvisser.wpvhomecontroller.scheduler.web.context;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.apache.log4j.Logger;
import za.co.willemvisser.wpvhomecontroller.listener.WaterTankListener;

public class WaterTankServletListener implements ServletContextListener {

	private WaterTankListener myListener = null;
	static Logger log = Logger.getLogger(WaterTankServletListener.class.getName());
	
	@Override
	public void contextInitialized(ServletContextEvent arg0) {
									
		try {			
			myListener = new WaterTankListener();			
        } catch (Exception e) {
        	log.error("Could not start WaterTankListener thread: " + e);
        }
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		try {
			
			myListener.stop();
			
        } catch (Exception e) {
        	log.error("Could not stop WaterTankListener thread: " + e);
        }

	}

	

}
