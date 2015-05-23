package za.co.willemvisser.wpvhomecontroller.config.dto;

import java.io.Serializable;
import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * @author willemv
 *
 *	General Properties DTO
 */
@XmlRootElement(name = "generalproperties")
@XmlAccessorType(XmlAccessType.FIELD)
public class GeneralPropertiesDTO implements Serializable {
	
	private static final long serialVersionUID = 1284942450375358960L;
		
	@XmlElement(name = "generalproperty")
	public ArrayList<GeneralPropertyDTO> generalPropertyList;
			
	public ArrayList<GeneralPropertyDTO> getGeneralPropertyList() {
		return generalPropertyList;
	}

	public void setGeneralPropertyList(
			ArrayList<GeneralPropertyDTO> generalPropertyList) {
		this.generalPropertyList = generalPropertyList;
	}
	
}
