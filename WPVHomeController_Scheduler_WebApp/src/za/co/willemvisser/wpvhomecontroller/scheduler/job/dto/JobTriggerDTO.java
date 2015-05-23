package za.co.willemvisser.wpvhomecontroller.scheduler.job.dto;

import org.quartz.JobDataMap;
import org.quartz.Trigger;

public class JobTriggerDTO {

	private String jobName;
	private String groupName;	
	private Trigger trigger;
	private JobDataMap jobDataMap;
	
	/**
	 * @param jobName
	 * @param groupName
	 */
	public JobTriggerDTO(String jobName, String groupName, Trigger trigger, JobDataMap jobDataMap) {
		super();
		this.jobName = jobName;
		this.groupName = groupName;
		this.trigger = trigger;
		this.jobDataMap = jobDataMap;
	}
	
	/**
	 * @param jobName
	 * @param groupName
	 * @param triggers
	 */
	/*
	public JobTriggerDTO(String jobName, String groupName, List<Trigger> triggers) {
		super();
		this.jobName = jobName;
		this.groupName = groupName;
		this.triggers = triggers;
	}
	*/
	
	/*
	public void addTrigger(Trigger trigger) {
		triggers.add(trigger);
	}
	*/

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public Trigger getTrigger() {
		return trigger;
	}

	public void setTrigger(Trigger trigger) {
		this.trigger = trigger;
	}

	public JobDataMap getJobDataMap() {
		return jobDataMap;
	}

	public void setJobDataMap(JobDataMap jobDataMap) {
		this.jobDataMap = jobDataMap;
	}

	/*
	public List<Trigger> getTriggers() {
		return triggers;
	}
	*/
	
}
