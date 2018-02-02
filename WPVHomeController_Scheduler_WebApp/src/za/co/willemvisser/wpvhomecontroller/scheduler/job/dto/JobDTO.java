package za.co.willemvisser.wpvhomecontroller.scheduler.job.dto;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.apache.log4j.Logger;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

@XmlRootElement(name = "job")
@XmlAccessorType (XmlAccessType.FIELD)
public class JobDTO {

	static Logger log = Logger.getLogger(JobDTO.class.getName());
	
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		
	private String id;
	private String className;
	private String name;
	private String groupName;
	private int durationMin;
	private Date startTime;
	private String cronStartExpression;
	private List<JobParamDTO> params;
	
	public static final String GROUPNAME_IRRIGATION = "Irrigation";
	public static final String GROUPNAME_POOLPUMP = "PoolPump";
	
	public JobDTO() {
		super();
	}
	
	public JobDTO(String className, String name, String groupName, int durationMin, Date startTime, String cronStartExpression, List<JobParamDTO> params) {
		super();
		this.id = UUID.randomUUID().toString();
		this.className = className;
		this.name = name;
		this.groupName = groupName;
		this.durationMin = durationMin;
		this.startTime = startTime;
		this.cronStartExpression = cronStartExpression;
		this.params = params;
	}
	
	public JobDTO(XPath xPath, Element eElement) throws DOMException, ParseException, XPathExpressionException {
		super();
		this.id = eElement.getElementsByTagName("id").item(0).getTextContent();
		this.className = eElement.getElementsByTagName("class").item(0).getTextContent();
		this.name = eElement.getElementsByTagName("name").item(0).getTextContent();
		this.groupName = eElement.getElementsByTagName("groupName").item(0).getTextContent();
		String startTimeStr = eElement.getElementsByTagName("startTime").item(0).getTextContent();
		if (startTimeStr != null && startTimeStr.length() > 0) {
			if (startTimeStr.equals("Now")) {
				this.startTime = new Date();
			} else {
				this.startTime = sdf.parse( startTimeStr );
			}
		} else {
			this.startTime = null;
		}
		this.cronStartExpression = eElement.getElementsByTagName("cronStartExpression").item(0).getTextContent() ;
		this.durationMin = Integer.parseInt(eElement.getElementsByTagName("durationMin").item(0).getTextContent() );
		this.params = new LinkedList<JobParamDTO>();						
		
		String expression = "params/param";	        
        NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(eElement, XPathConstants.NODESET);
		//NodeList nodeList = eElement.getElementsByTagName("params").item(0).getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
        	Node nNode = nodeList.item(i);
        	Element paramElement = (Element) nNode;
        	this.params.add(new JobParamDTO(paramElement.getElementsByTagName("name").item(0).getTextContent(), 
                			paramElement.getElementsByTagName("value").item(0).getTextContent()));
        	//this.params.put(paramElement.getElementsByTagName("name").item(0).getTextContent(), 
        	//		paramElement.getElementsByTagName("value").item(0).getTextContent());
        }
        log.debug("Job '" + this.name + "' Added with cron: " + this.cronStartExpression + " and params: " + this.params);
	}
	
	
	public String getClassName() {
		return className;
	}
	
	public void setClassName(String className) {
		this.className = className;
	}
	
	public Date getStartTime() {
		return startTime;
	}
	
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}		
	
	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getGroupName() {
		return groupName;
	}


	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}


	public List<JobParamDTO> getParams() {
		return params;
	}


	public void setParams(List<JobParamDTO> params) {
		this.params = params;
	}


	public String getCronExpression() {
		return cronStartExpression;
	}


	public void setCronExpression(String cronExpression) {
		this.cronStartExpression = cronExpression;
	}
	
	@Override
	public String toString() {
		return "Adding job: " + this.getName() + " with cron: " + 
					this.getCronExpression() + " and startime: " + 
					this.getStartTime();
	}

	public int getDurationMin() {
		return durationMin;
	}

	public void setDurationMin(int durationMin) {
		this.durationMin = durationMin;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	
	
}
