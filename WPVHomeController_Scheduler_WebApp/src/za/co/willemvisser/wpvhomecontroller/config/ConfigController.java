package za.co.willemvisser.wpvhomecontroller.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.InetAddress;

import javax.servlet.ServletContext;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.http.HttpResponse;
import org.apache.log4j.Logger;

import za.co.willemvisser.wpvhomecontroller.config.dto.GeneralPropertiesDTO;
import za.co.willemvisser.wpvhomecontroller.config.dto.GeneralPropertyDTO;
import za.co.willemvisser.wpvhomecontroller.config.dto.XbeeConfigsDTO;
import za.co.willemvisser.wpvhomecontroller.util.HttpUtil;
import za.co.willemvisser.wpvhomecontroller.util.S3Util;

public enum ConfigController {

	INSTANCE;
	
	private static final String XML_LOCAL_SERVLET_PATH = "/WEB-INF/defaultconfig/";	
	private static final String HTTP_PREFIX = "http://";
	
	private static final String XML_FILENAME_XBEECONFIG = "XbeeConfig.xml";
	private static final String XML_FILENAME_GENERALPROPS = "GeneralProperties.xml";
	
	public static final String XML_FILENAME_SCHEDULE = "Schedule.xml";
	
	public static final String PROPERTY_OUTSIDE_TEMPID = "OutsideTempID";
	public static final String PROPERTY_INSIDE_TEMPID = "InsideTempID";
	public static final String PROPERTY_WATERTANKLEVEL_ID = "WaterTankLevelID";
	public static final String PROPERTY_TANK_LEVEL_HTTP_URL = "TankLevelHttpURL";
	public static final String PROPERTY_TANK_LEVEL_LASTUPDATED_HTTP_URL = "TankLevelLastUpdatedHttpURL";
	public static final String PROPERTY_XBEE_PORTNAME = "XbeePortName";
	public static final String PROPERTY_XBEE_BAUDRATE = "XbeeBaudRate";
	public static final String PROPERTY_XBEE_ENABLED = "XbeeEnabled";
	
	private String remoteConfigHostAddress;
	private XbeeConfigsDTO xbeeConfigsDTO;
	private GeneralPropertiesDTO generalPropertiesDTO;
	
	static Logger log = Logger.getLogger(ConfigController.class.getName());
	
	public void init(String remoteConfigHostAddress, ServletContext context) throws Exception {
		
		try {
			log.info("ConfigController init");
			
			this.remoteConfigHostAddress = remoteConfigHostAddress;
			
			log.info("Loading XML config: " + XML_FILENAME_GENERALPROPS);
			this.generalPropertiesDTO = (GeneralPropertiesDTO)loadXmlFromResource(GeneralPropertiesDTO.class, context, 
					XML_FILENAME_GENERALPROPS);
			System.out.println("ConfigController init done");
			log.info("this.generalPropertiesDTO initialized: " + this.generalPropertiesDTO);
			
			this.xbeeConfigsDTO = (XbeeConfigsDTO)loadXmlFromResource(XbeeConfigsDTO.class, context, XML_FILENAME_XBEECONFIG);
			System.out.println("ConfigController xbee load init done");
		} catch (Exception e) {
			log.error("Could not init ConfigController: " + e.toString() );
		}

	}
	
	/**
	 * Refresh the cached version of the XbeeConfig array from the remote source  
	 */
	public void reloadRemoteXbeeConfig() {				
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(XbeeConfigsDTO.class);
			Unmarshaller um = jaxbContext.createUnmarshaller();
			Object obj = loadXmlFromRemoteResource(um, XML_FILENAME_XBEECONFIG);
			if (obj != null) {
				this.xbeeConfigsDTO = (XbeeConfigsDTO)obj;
			}
		} catch (Exception e) {
			log.error("Could not load resource from remote location", e);						
		}
				
	}
	
	public String getHostName() {
		StringBuffer hostNameBuffer = new StringBuffer();
		try {
			String hostname = InetAddress.getLocalHost().getHostName();			
			hostNameBuffer.append(hostname);
			hostNameBuffer.append("_");
		} catch (Exception e) {
			log.error("Could not get hostname", e);
		}	
		return hostNameBuffer.toString();
	}
	
	/**
	 * @param propertyKey
	 * @return GeneralPropertyDTO with given key
	 */
	public GeneralPropertyDTO getGeneralProperty(String propertyKey) {
		log.info("Retrieving General Property: " + propertyKey);
		log.info("GeneralProperties != null: " + (this.generalPropertiesDTO != null) );
		if (this.generalPropertiesDTO != null) {
			log.info("getGeneralPropertyList != null: " + (this.generalPropertiesDTO.getGeneralPropertyList() != null) );
		}
		for (GeneralPropertyDTO generalPropertyDTO : this.generalPropertiesDTO.getGeneralPropertyList()) {
			if (generalPropertyDTO != null && generalPropertyDTO.getKey().equals(propertyKey)) {
				return generalPropertyDTO;
			}
		}
		log.error("Error - could not find a general property with key name: " + propertyKey);
		return null;
	}
		
	
	private Object loadXmlFromResource(Class classType, ServletContext context, String fileName) throws Exception {
		
		
		try {
			log.info("loadXmlFromResource: " + fileName);
			JAXBContext jaxbContext = JAXBContext.newInstance(classType);
			log.info("jaxbContext newInstance");
			Unmarshaller um = jaxbContext.createUnmarshaller();
			
			log.info("About to loadXmlFromRemoteResource...");
			return loadXmlFromRemoteResource(um, fileName);			
		} catch (Exception e) {
			log.error("Could not load resource from remote location", e);
			
			log.info("Loading resource from local path");
			return null;
		} catch (Throwable t) {
			log.error("Could not load class: " + t.getMessage());
			return null;
		}
	}
	
	/**
	 * @param um
	 * @param context
	 * @param fileName
	 * @return Object retrieved from remote HTTP server
	 * @throws JAXBException
	 * @throws IOException
	 */
	private Object loadXmlFromRemoteResource(Unmarshaller um, String fileName) throws JAXBException, IOException {										
		/* Attempt to download from remote server */
		/*
		String remoteUrl = HTTP_PREFIX + remoteConfigHostAddress + 
				"/config/" + 
				fileName;
			
		
		HttpResponse response = HttpUtil.INSTANCE.doHttpGet(remoteUrl);
		if (response.getStatusLine().getStatusCode() == 200) {		
			
			
			try { 
				S3Util.INSTANCE.writeStringToBucket(S3Util.BUCKET_WPVHOMESCHEDULER, fileName,  HttpUtil.INSTANCE.getResponseContent(response).toString()  );
			} catch (Exception e) {
				log.error(e);
			}
		
					
			return um.unmarshal( response.getEntity().getContent() );
			
		} else {
			throw new IOException("Could not load Remote XML file: " + remoteUrl);
		}
		*/
		return um.unmarshal( new StringReader( S3Util.INSTANCE.getBucketAsString(S3Util.BUCKET_WPVHOMESCHEDULER, fileName) ) );
							
	}
	
	/**
	 * @param um
	 * @param context
	 * @param fileName
	 * @return Object retrieved from local disc in WAR path
	 * @throws Exception
	 */
	private Object loadXmlFromLocalResource(Unmarshaller um, ServletContext context, String fileName) throws Exception {
		/* Remote retrieve failed - download locally */
		InputStream configGeneralPropsXmlInputStream = context.getResourceAsStream(XML_LOCAL_SERVLET_PATH + fileName);				
		return um.unmarshal(configGeneralPropsXmlInputStream);		
	}


	public XbeeConfigsDTO getXbeeConfigsDTO() {
		return xbeeConfigsDTO;
	}


	public void setXbeeConfigsDTO(XbeeConfigsDTO xbeeConfigsDTO) {
		this.xbeeConfigsDTO = xbeeConfigsDTO;
	}


	public GeneralPropertiesDTO getGeneralPropertiesDTO() {
		return generalPropertiesDTO;
	}


	public void setGeneralPropertiesDTO(GeneralPropertiesDTO generalPropertiesDTO) {
		this.generalPropertiesDTO = generalPropertiesDTO;
	}
	
}
