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
import za.co.willemvisser.wpvhomecontroller.util.TelegramUtil;
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
		
		XbeeController.INSTANCE.shutdown();
	}

	@Override
	public void contextInitialized(ServletContextEvent context) {
		System.out.println("Starting up...");
										
		String ipAddress = "localhost";
				
		
		try {			
		    
			ConfigController.INSTANCE.init("54.68.136.170", context.getServletContext());
			
			WPVHomeControllerScheduler.INSTANCE.startScheduler(ipAddress);
									
			TelegramUtil.INSTANCE.setApiToken( ConfigController.INSTANCE.getGeneralProperty("Telegram_API_Token").getValue() );
			TelegramUtil.INSTANCE.setChatID(ConfigController.INSTANCE.getGeneralProperty("Telegram_Chat_ID").getValue());
			
			
			/* See if we need to enable XBEE flows */
			try { 
				GeneralPropertyDTO propDTO = ConfigController.INSTANCE.getGeneralProperty(ConfigController.PROPERTY_XBEE_ENABLED);
				
				if (propDTO != null && propDTO.getValue().equalsIgnoreCase("true")) {
									
					String xbeePortName = ConfigController.INSTANCE.getGeneralProperty(ConfigController.PROPERTY_XBEE_PORTNAME).getValue();
					int xbeeBaudRate = Integer.parseInt(ConfigController.INSTANCE.getGeneralProperty(ConfigController.PROPERTY_XBEE_BAUDRATE).getValue());
					
					XbeeController.INSTANCE.init(xbeePortName, xbeeBaudRate, ConfigController.INSTANCE.getXbeeConfigsDTO());
				} else {
					System.out.println("Xbee Disabled as per GeneralProperties");
				}
			} catch (Exception e) {
				System.out.println("WARNING *** - Could not start up Xbee: " + e.getMessage());
				e.printStackTrace();
			}			
												
		} catch (Exception e) {
			e.printStackTrace();
		}		
		
		
		
		
	}
	
	private Object loadXmlFromResource(Class classType, ServletContext context, String fileName) throws JAXBException {	
		
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
