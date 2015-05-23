package za.co.willemvisser.wpvhomecontroller.config.dto;

import java.io.Serializable;
import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "xbeeconfig")
@XmlAccessorType(XmlAccessType.FIELD)
public class XbeeConfigsDTO implements Serializable {
	
	private static final long serialVersionUID = -904384166364874907L;

	@XmlElement(name = "xbee")
	public ArrayList<XbeeConfigDTO> xbeeList;

	public ArrayList<XbeeConfigDTO> getXbeeList() {
		return xbeeList;
	}

	public void setXbeeList(ArrayList<XbeeConfigDTO> xbeeList) {
		this.xbeeList = xbeeList;
	}
	
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		for (XbeeConfigDTO xbeeConfigDTO : this.getXbeeList()) {
			buffer.append("Xbee Name: ");
				buffer.append(xbeeConfigDTO.getName() );
				buffer.append("\r\n");
				
			
			for (XbeeConfigDeviceDTO deviceDTO : xbeeConfigDTO.getDeviceList()) {
				buffer.append(" - ");
				buffer.append(deviceDTO.getName());
				buffer.append(" -> ");
				buffer.append(deviceDTO.getPort());
				buffer.append("\r\n");
			}
			
		}
		return buffer.toString();
	}
}
