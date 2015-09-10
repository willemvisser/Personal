package za.co.willemvisser.wpvhomecontroller.config;

import java.io.IOException;
import java.io.InputStream;
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

public enum ConfigController {

	INSTANCE;
	
	private static final String XML_LOCAL_SERVLET_PATH = "/WEB-INF/defaultconfig/";	
	private static final String HTTP_PREFIX = "http://";
	
	private static final String XML_FILENAME_XBEECONFIG = "XbeeConfig.xml";
	private static final String XML_FILENAME_GENERALPROPS = "GeneralProperties.xml";
	
	public static final String XML_FILENAME_SCHEDULE = "Schedule.xml";
	
	private String remoteConfigHostAddress;
	private XbeeConfigsDTO xbeeConfigsDTO;
	private GeneralPropertiesDTO generalPropertiesDTO;
	
	static Logger log = Logger.getLogger(ConfigController.class.getName());
	
	public void init(String remoteConfigHostAddress, ServletContext context) throws Exception {
		this.remoteConfigHostAddress = remoteConfigHostAddress;
		
		this.xbeeConfigsDTO = (XbeeConfigsDTO)loadXmlFromResource(XbeeConfigsDTO.class, context, XML_FILENAME_XBEECONFIG);
		this.generalPropertiesDTO = (GeneralPropertiesDTO)loadXmlFromResource(GeneralPropertiesDTO.class, context, 
				getHostName() + XML_FILENAME_GENERALPROPS);
				

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
			log.info("Hostname: " + hostname);
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
		for (GeneralPropertyDTO generalPropertyDTO : this.generalPropertiesDTO.getGeneralPropertyList()) {
			if (generalPropertyDTO.getKey().equals(propertyKey)) {
				return generalPropertyDTO;
			}
		}
		return null;
	}
		
	
	private Object loadXmlFromResource(Class classType, ServletContext context, String fileName) throws Exception {
		log.info("Loading config: " + fileName);
		JAXBContext jaxbContext = JAXBContext.newInstance(classType);
		Unmarshaller um = jaxbContext.createUnmarshaller();
		
		try {
			return loadXmlFromRemoteResource(um, fileName);			
		} catch (Exception e) {
			log.error("Could not load resource from remote location", e);
			
			log.info("Loading resource from local path");
			return loadXmlFromLocalResource(um, context, fileName);
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
		String remoteUrl = HTTP_PREFIX + remoteConfigHostAddress + 
				"/config/" + 
				fileName;
				
		HttpResponse response = HttpUtil.INSTANCE.doHttpGet(remoteUrl);
		if (response.getStatusLine().getStatusCode() == 200) {				
			return um.unmarshal( response.getEntity().getContent() );				
		} else {
			throw new IOException("Could not load Remote XML file: " + remoteUrl);
		}								
							
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
