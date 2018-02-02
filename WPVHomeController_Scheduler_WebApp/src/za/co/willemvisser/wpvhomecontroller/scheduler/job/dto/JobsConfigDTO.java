package za.co.willemvisser.wpvhomecontroller.scheduler.job.dto;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.log4j.Logger;

@XmlRootElement(name = "jobsconfig")
@XmlAccessorType (XmlAccessType.FIELD)
public class JobsConfigDTO {

	static Logger log = Logger.getLogger(JobsConfigDTO.class.getName());
	
	@XmlElement(name = "job")
	private ArrayList<JobDTO> jobList;

	public ArrayList<JobDTO> getJobList() {
		return jobList;
	}

	public void setJobList(ArrayList<JobDTO> jobList) {
		this.jobList = jobList;
	}
	
}
