package za.co.willemvisser.wpvhomecontroller.property;

import java.util.HashMap;

import org.apache.log4j.Logger;

public enum PropertyManager {

	INSTANCE;
	
	static Logger log = Logger.getLogger(PropertyManager.class.getName());
	
	private HashMap<String, PropertyDTO> keyMap = new HashMap<String, PropertyDTO>();
	
	/**
	 * @param key
	 * @param value
	 */
	public void setProperty(String key, String value) {
		keyMap.put(key, new PropertyDTO(key, value));
	}
	
	/**
	 * @param key
	 * @return
	 */
	public PropertyDTO getProperty(String key) {
		return keyMap.get(key);
	}
}
