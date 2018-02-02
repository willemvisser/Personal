package za.co.willemvisser.wpvhomecontroller.scheduler.web.context;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.ArrayList;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.http.HttpResponse;

import za.co.willemvisser.wpvhomecontroller.config.ConfigController;
import za.co.willemvisser.wpvhomecontroller.config.dto.GeneralPropertiesDTO;
import za.co.willemvisser.wpvhomecontroller.config.dto.GeneralPropertyDTO;
import za.co.willemvisser.wpvhomecontroller.config.dto.XbeeConfigDTO;
import za.co.willemvisser.wpvhomecontroller.config.dto.XbeeConfigDeviceDTO;
import za.co.willemvisser.wpvhomecontroller.config.dto.XbeeConfigsDTO;
import za.co.willemvisser.wpvhomecontroller.scheduler.WPVHomeControllerScheduler;
import za.co.willemvisser.wpvhomecontroller.util.HttpUtil;
import za.co.willemvisser.wpvhomecontroller.util.WaterTankFillUtil;
import za.co.willemvisser.wpvhomecontroller.xbee.XbeeController;
import za.co.willemvisser.wpvhomecontroller.xbee.dto.XbeeDeviceDTO;

public class SchedulerServletListener implements ServletContextListener {

	private static final String configXmlWarFilePath = "/WEB-INF/defaultconfig/IrrigationTimes.xml";
	
	private static final String GENERALPROPS_XML_LOCAL_PATH = "/WEB-INF/defaultconfig/GeneralProperties.xml";
	private static final String XML_LOCAL_PATH_XBEECONFIG = "/WEB-INF/defaultconfig/XbeeConfig.xml";
	
	private static final String XML_LOCAL_PATH = "/WEB-INF/defaultconfig/";
	private static final String XML_FILENAME_XBEECONFIG = "XbeeConfig.xml";
	
	private static final String HTTP_PREFIX = "http://";
	
	
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {		
		System.out.println("Shutting down!!!");
		try {
			
			if (WaterTankFillUtil.INSTANCE.isPumping()) {				
				WaterTankFillUtil.INSTANCE.stopPumping();
			}
			WPVHomeControllerScheduler.INSTANCE.stopScheduler();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		/* Shutting down Xbee Controller */
		XbeeController.INSTANCE.shutdown();
	}

	@Override
	public void contextInitialized(ServletContextEvent context) {
		System.out.println("Starting up...");
							
			
		//TODO - get this from config
		//String ipAddress = "192.168.1.201";
		String ipAddress = "localhost";
		
		InputStream configXmlInputStream = context.getServletContext().getResourceAsStream(configXmlWarFilePath);
		
		//
		
		try {			
		    
			WPVHomeControllerScheduler.INSTANCE.startScheduler(ipAddress);
			
			ConfigController.INSTANCE.init("54.68.136.170", context.getServletContext());
						
			XbeeController.INSTANCE.init("/dev/ttyUSB0", 9600, ConfigController.INSTANCE.getXbeeConfigsDTO());
			
		} catch (Exception e) {
			e.printStackTrace();
		}		
		
		
	}
	
	private Object loadXmlFromResource(Class classType, ServletContext context, String fileName) throws JAXBException {
	//private Object loadXmlFromResource(Class classType, InputStream is) throws JAXBException {
		
		JAXBContext jaxbContext = JAXBContext.newInstance(classType);
		Unmarshaller um = jaxbContext.createUnmarshaller();
		
		/* Attempt to download from remote server */
		try {
			String remoteUrl = HTTP_PREFIX + 
					"54.68.136.170/config/" + 
					fileName;
			
			HttpResponse response = HttpUtil.INSTANCE.doHttpGet(remoteUrl);
			if (response.getStatusLine().getStatusCode() == 200) {
				
				return um.unmarshal( response.getEntity().getContent() );
			
			} else {
				throw new IOException("Could not load Remote XML file: " + remoteUrl);
			}
			
			
		} catch (Exception e) {
			System.err.println("Failed to download remote file: " + e);
		}
		
		/* Remote retrieve failed - download locally */
		InputStream configGeneralPropsXmlInputStream = context.getResourceAsStream(XML_LOCAL_PATH + fileName);				
		return um.unmarshal(configGeneralPropsXmlInputStream);			
	}

}
