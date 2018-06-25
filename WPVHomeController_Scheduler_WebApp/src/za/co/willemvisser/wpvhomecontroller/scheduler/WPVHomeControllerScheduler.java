package za.co.willemvisser.wpvhomecontroller.scheduler;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.apache.http.HttpResponse;
import org.apache.log4j.Logger;
import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;

import static org.quartz.JobBuilder.*;   
import static org.quartz.TriggerBuilder.*;     
import static org.quartz.CronScheduleBuilder.*;   
import za.co.willemvisser.wpvhomecontroller.config.ConfigController;
import za.co.willemvisser.wpvhomecontroller.scheduler.job.dto.JobDTO;
import za.co.willemvisser.wpvhomecontroller.scheduler.job.dto.JobParamDTO;
import za.co.willemvisser.wpvhomecontroller.scheduler.job.dto.JobTriggerDTO;
import za.co.willemvisser.wpvhomecontroller.util.HttpUtil;
import za.co.willemvisser.wpvhomecontroller.util.S3Util;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


/*
 * Irrigation:
 *		D1: Outside trees
 * 		D3: Middle weak
 * 		D2: 
 * 		D6: Main grass - house side
 * 		D4: Main grass - top large 
 * 		D7: Main grass - boundary wall
 */

public enum WPVHomeControllerScheduler {

	INSTANCE;
	
	static Logger log = Logger.getLogger(WPVHomeControllerScheduler.class.getName());
	private Scheduler scheduler;
	private HashMap<String, String> generalJobPropertiesMap;
	
	private HashMap<String, JobKey> jobMap;
	
	private boolean isStarted = false;
	
	public static final String HTTP_PREFIX = "http://";
	public static final String GROUP_NAME_IRRIGATION = "Irrigation";
		
	private static final String S3_KEY_SCHEDULE = "Schedule.xml";
	
	private WPVHomeControllerScheduler() {}
	
	
	
	/**
	 * @param hostIPAddress
	 * @throws SchedulerException
	 */
	public synchronized void startScheduler(String hostIPAddress) throws SchedulerException {
		if (!isStarted) {
			log.debug("Starting scheduler ...");			
			
			jobMap = new HashMap<String, JobKey>();
			
			generalJobPropertiesMap = new HashMap<String, String>();
			generalJobPropertiesMap.put("hostIPAddress", hostIPAddress);
			
			Properties props = new Properties();
			props.setProperty("org.quartz.scheduler.skipUpdateCheck","true");
			props.setProperty("org.quartz.jobStore.class", "org.quartz.simpl.RAMJobStore");
			props.setProperty("org.quartz.threadPool.class", "org.quartz.simpl.SimpleThreadPool");
			props.setProperty("org.quartz.threadPool.threadCount", "4");
			
			SchedulerFactory schdFact = new StdSchedulerFactory(props);
			
	        //scheduler = StdSchedulerFactory.getDefaultScheduler();
			scheduler = schdFact.getScheduler();
			
			scheduler.start();
			
			try {
	        	processXmlConfig( getJobXmlConfigFromS3() );          	
	        } catch (Exception e) {
	        	log.error("Could not process xml: " + e.toString());
	        	throw new SchedulerException(e);
	        }
			isStarted = true;
		}
	}	
	
	public void reloadSchedule() {		
		try {			
			scheduler.clear();
        	processXmlConfig( getJobXmlConfigFromS3() );          	
        } catch (Exception e) {
        	log.error("Could not process xml: " + e.toString());        	
        }
	}
	
	/**
	 * @param newJobScheduleXmlStr to XML to write to S3
	 */
	public void writeJobScheduletoS3(String newJobScheduleXmlStr) {
		//TODO - we should validate at least this is valid XML - and probably that this parses correctly before we save.
		S3Util.INSTANCE.writeStringToBucket(S3Util.BUCKET_WPVHOMESCHEDULER, S3_KEY_SCHEDULE, newJobScheduleXmlStr);
	}
	
	public LinkedList<String> getCurrentlyExecutingJobs() {
		LinkedList<String> jobList = new LinkedList<String>();
		try {
			List<JobExecutionContext> currentlyExecutingJobList= scheduler.getCurrentlyExecutingJobs();
			for (JobExecutionContext jobExecutionContext : currentlyExecutingJobList) {
				jobList.add( jobExecutionContext.getJobDetail().getDescription() );
			}
		} catch (Exception e) {
			log.error("Could not list currently executing jobs: " + e.toString());
		}
		return jobList;
	}
	
	/**
	 * @throws SchedulerException
	 */
	public List<JobTriggerDTO> listJobTriggers() throws SchedulerException {
		return listJobTriggers(null);
	}
	
	public List<JobTriggerDTO> listJobTriggers(String groupNameFilter) throws SchedulerException {
		              
        //TODO - retrieve XML Config from remote location again?
 
        LinkedList<JobTriggerDTO> jobTriggerDTOList = new LinkedList<JobTriggerDTO>();
        for (String groupName : scheduler.getJobGroupNames()) {
        	
        	if (groupNameFilter != null && !groupNameFilter.equals(groupName)) {
        		break;
        	}
        	 
            for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
        
            	String jobName = jobKey.getName();
            	String jobGroup = jobKey.getGroup();
            	JobDataMap jobDataMap = scheduler.getJobDetail(jobKey).getJobDataMap();
        
	       	  	//get job's trigger
	       	  	List<Trigger> triggers = (List<Trigger>) scheduler.getTriggersOfJob(jobKey);
	       	  	
	       	  	for (Trigger trigger : triggers) {
	       	  		jobTriggerDTOList.add(new JobTriggerDTO(jobName, groupName, trigger, jobDataMap) );
	       	  	}
	        
	       }
        
        }
        
        Collections.sort(jobTriggerDTOList, new Comparator<JobTriggerDTO>() {
            public int compare(JobTriggerDTO m1, JobTriggerDTO m2) {
                return m1.getTrigger().getNextFireTime().compareTo(m2.getTrigger().getNextFireTime());
            }
        });
           
        return jobTriggerDTOList;
 
	}
	
	
	/**
	 * @throws SchedulerException
	 * @throws InterruptedException 
	 */
	public void stopScheduler() throws SchedulerException, InterruptedException {
		scheduler.shutdown(true);
		Thread.sleep(1000);
	}
	
	public String getJobXmlConfigFromS3() {
		return S3Util.INSTANCE.getBucketAsString(S3Util.BUCKET_WPVHOMESCHEDULER, S3_KEY_SCHEDULE);
	}
	
	protected void processXmlConfig(String jobXmlConfigStr) 
			throws ParserConfigurationException, SAXException, IOException, XPathExpressionException, 
			DOMException, ParseException, CloneNotSupportedException {
		log.debug("processXmlConfig");		
		
        DocumentBuilderFactory dbFactory 
           = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;

        dBuilder = dbFactory.newDocumentBuilder();
        
        InputSource is = new InputSource(
        		new StringReader( jobXmlConfigStr ));

        Document doc = dBuilder.parse(is);
        doc.getDocumentElement().normalize();

        XPath xPath =  XPathFactory.newInstance().newXPath();

        String expression = "/jobsconfig/job";	        
        NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(doc, XPathConstants.NODESET);
        for (int i = 0; i < nodeList.getLength(); i++) {
        	Node nNode = nodeList.item(i);
        	Element eElement = (Element) nNode;
        	JobDTO jobDTO = new JobDTO(xPath, eElement);
        	addJob(jobDTO, generalJobPropertiesMap);
        	        	
        	//TODO - why limit this to these groups, why not all??  
        	//			I think this should just check if there is a duration > 0
        	
        	if (jobDTO.getGroupName().equals( JobDTO.GROUPNAME_IRRIGATION ) || 
        			jobDTO.getGroupName().equals( JobDTO.GROUPNAME_POOLPUMP)) {
        		
        		//TODO - find out how to do cloning??
        		JobDTO jobDTOEnd = new JobDTO(
        					jobDTO.getClassName(),
        					jobDTO.getName(),
        					jobDTO.getGroupName(),
        					jobDTO.getDurationMin(),
        					jobDTO.getStartTime(),
        					jobDTO.getCronExpression(),
        					jobDTO.getParams()
        				);
        		
        		int spaceIndex1 = jobDTOEnd.getCronExpression().indexOf(" ");
        		int spaceIndex2 = jobDTOEnd.getCronExpression().indexOf(" ", spaceIndex1+1);
        		int spaceIndex3 = jobDTOEnd.getCronExpression().indexOf(" ", spaceIndex2+1);
        		
        		int mins = Integer.parseInt(jobDTOEnd.getCronExpression().substring(spaceIndex1+1, spaceIndex2));
        		int hours = Integer.parseInt(jobDTOEnd.getCronExpression().substring(spaceIndex2+1, spaceIndex3));
        		
        		Calendar cal = new GregorianCalendar();
        		cal.set(Calendar.HOUR_OF_DAY, hours);
        		cal.set(Calendar.MINUTE, mins);
        		cal.add(Calendar.MINUTE, jobDTOEnd.getDurationMin());        		
        		
        		StringBuffer newCronBuffer = new StringBuffer();
        		newCronBuffer.append(jobDTOEnd.getCronExpression().substring(0, spaceIndex1+1));
        		newCronBuffer.append(cal.get(Calendar.MINUTE));
        		newCronBuffer.append(" ");
        		newCronBuffer.append(cal.get(Calendar.HOUR_OF_DAY));
        		newCronBuffer.append( jobDTOEnd.getCronExpression().substring(spaceIndex3) );        		        		
        		
        		jobDTOEnd.setCronExpression(newCronBuffer.toString());
        		jobDTOEnd.getParams().add(new JobParamDTO("command", "xoff"));
        		
        		addJob(jobDTOEnd, generalJobPropertiesMap);
        	}
        }
	}
	
	
	public void addJob(JobDTO jobDTO, HashMap<String, String> jobPropertiesMap) {
		try {			
			Class reportClass = Class.forName(jobDTO.getClassName());
			JobDetail job1 = newJob(reportClass)   
				    //.withIdentity(jobDTO.getName(), jobDTO.getGroupName())
					.withIdentity(jobDTO.getCronExpression(), jobDTO.getGroupName())
				    .build(); 			
			
			job1.getJobDataMap().putAll(jobPropertiesMap);  
			for (JobParamDTO jobParamDTO : jobDTO.getParams()) {
				job1.getJobDataMap().put(jobParamDTO.getName(), jobParamDTO.getValue());
			}			
			job1.getJobDataMap().put("job.name", jobDTO.getName() );
			
			
			CronTrigger trigger1 = newTrigger()    
				    //.withIdentity("trigger_" + jobDTO.getName(), jobDTO.getGroupName())
					.withIdentity("trigger_" + jobDTO.getCronExpression(), jobDTO.getGroupName())
				    .startAt(jobDTO.getStartTime())   
				    .withSchedule(cronSchedule( jobDTO.getCronExpression() ) )   
				    .build();
			
			
			jobMap.put(jobDTO.getName(), job1.getKey());
			
			scheduler.scheduleJob(job1, trigger1);
			
			log.debug("Adding job: " + jobDTO.getName() + " with cron: " + jobDTO.getCronExpression() + " and startime: " + jobDTO.getStartTime());
			
		} catch (ClassNotFoundException cnfe) {
			log.error("Could not add job with name " + jobDTO.getName() + " : " + cnfe.toString());
		} catch (SchedulerException se) {
			log.error("Could not add job with name " + jobDTO.getName() + " : " + se.toString());
		} catch (Exception ge) {
			log.error("Unexpected: " + ge.toString());
		}
	}



	public HashMap<String, String> getGeneralJobPropertiesMap() {
		return generalJobPropertiesMap;
	}
	
	
	
	public List<JobTriggerDTO> cancelGroupTriggersForToday(String groupNameToCancel) throws SchedulerException {
		log.info("cancelGroupTriggersForToday: " + groupNameToCancel);
		Date today = new Date();
		
		Calendar newTriggerStartTimeCal = GregorianCalendar.getInstance();
		newTriggerStartTimeCal.setTime(today);					
		newTriggerStartTimeCal.add(Calendar.DATE, 1);
		newTriggerStartTimeCal.set(Calendar.HOUR_OF_DAY, 0);
		newTriggerStartTimeCal.set(Calendar.MINUTE, 0);
		newTriggerStartTimeCal.set(Calendar.SECOND, 0);
		newTriggerStartTimeCal.set(Calendar.MILLISECOND, 0);
		
		for (String groupName : scheduler.getJobGroupNames()) {
       	 
            for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupNameToCancel))) {        
            	
            	String jobName = jobKey.getName();
            	String jobGroup = jobKey.getGroup();
            	
            	log.debug("JobName: " + jobName + " group: " + jobGroup);
            	if (!jobGroup.equals(groupNameToCancel)) {
            		log.debug("Groupname for this job does not match the group to cancel");
            		break;
            	}
            	
            	JobDataMap jobDataMap = scheduler.getJobDetail(jobKey).getJobDataMap();
        
	       	  	//get job's trigger
	       	  	List<Trigger> triggers = (List<Trigger>) scheduler.getTriggersOfJob(jobKey);
	       	  	
	       	  	for (Trigger trigger : triggers) {
	       	  		Date triggerStartTime = trigger.getNextFireTime();
	       	  		
		       	  	if (triggerStartTime.getDate() != today.getDate() || triggerStartTime.getYear() != today.getYear() ||
		       	  			triggerStartTime.getMonth() != today.getMonth()) {
		       	  		break;
		       	  	}
		       	  	
	
					CronTrigger cTrigger = (CronTrigger)trigger;
					
					log.info("Right, Job: " + jobName + " with trigger start: " + cTrigger.getStartTime() + " now " + newTriggerStartTimeCal.getTime());
	       	  		
//					Trigger trigger1 = (Trigger) ((Object) trigger).clone();
//					
					CronTrigger trigger1 = newTrigger()    
						    //.withIdentity("trigger_" + jobDTO.getName(), jobDTO.getGroupName())
							.withIdentity(jobName, groupName)
						    .startAt(newTriggerStartTimeCal.getTime())   
						    .withSchedule(cronSchedule( cTrigger.getCronExpression() ) )   
						    .build();
					
					scheduler.rescheduleJob(trigger.getKey(), trigger1);
	       	  	}

	       	  	//Date nextFireTime = triggers.get(0).getNextFireTime(); 
	        
	       		//System.out.println("[jobName] : " + jobName + " [groupName] : "
	       		//	+ jobGroup + " - " + nextFireTime);
	        
	       }
        
        }
		
		
		return listJobTriggers();
	}
	
	public List<JobTriggerDTO> cancelGroupTriggersForTomorrow(String groupNameToCancel) throws SchedulerException {
		log.info("cancelGroupTriggersForTomorrow: " + groupNameToCancel);
		Date today = new Date();
		
		Calendar tomorrowCal = GregorianCalendar.getInstance();
		tomorrowCal.setTime(today);					
		tomorrowCal.add(Calendar.DATE, 1);
		tomorrowCal.set(Calendar.HOUR_OF_DAY, 0);
		tomorrowCal.set(Calendar.MINUTE, 0);
		tomorrowCal.set(Calendar.SECOND, 0);
		tomorrowCal.set(Calendar.MILLISECOND, 0);
		Date tomorrow = tomorrowCal.getTime();
		
		Calendar newTriggerStartTimeCal = GregorianCalendar.getInstance();
		newTriggerStartTimeCal.setTime(today);					
		newTriggerStartTimeCal.add(Calendar.DATE, 2);
		newTriggerStartTimeCal.set(Calendar.HOUR_OF_DAY, 0);
		newTriggerStartTimeCal.set(Calendar.MINUTE, 0);
		newTriggerStartTimeCal.set(Calendar.SECOND, 0);
		newTriggerStartTimeCal.set(Calendar.MILLISECOND, 0);
		
		for (String groupName : scheduler.getJobGroupNames()) {
       	 
            for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupNameToCancel))) {        
            	
            	String jobName = jobKey.getName();
            	String jobGroup = jobKey.getGroup();
            	
            	log.debug("JobName: " + jobName + " group: " + jobGroup);
            	if (!jobGroup.equals(groupNameToCancel)) {
            		log.debug("Groupname for this job does not match the group to cancel");
            		break;
            	}
            	
            	JobDataMap jobDataMap = scheduler.getJobDetail(jobKey).getJobDataMap();
        
	       	  	//get job's trigger
	       	  	List<Trigger> triggers = (List<Trigger>) scheduler.getTriggersOfJob(jobKey);
	       	  	
	       	  	for (Trigger trigger : triggers) {
	       	  		Date triggerStartTime = trigger.getNextFireTime();
	       	  		
		       	  	if (triggerStartTime.getDate() != tomorrow.getDate() || triggerStartTime.getYear() != tomorrow.getYear() ||
		       	  			triggerStartTime.getMonth() != tomorrow.getMonth()) {
		       	  		break;
		       	  	}
		       	  	
	
					CronTrigger cTrigger = (CronTrigger)trigger;
					
					log.info("Right, Job: " + jobName + " with trigger start: " + cTrigger.getStartTime() + " now " + newTriggerStartTimeCal.getTime());
	       	  		
//					Trigger trigger1 = (Trigger) ((Object) trigger).clone();
//					
					CronTrigger trigger1 = newTrigger()    
						    //.withIdentity("trigger_" + jobDTO.getName(), jobDTO.getGroupName())
							.withIdentity(jobName, groupName)
						    .startAt(newTriggerStartTimeCal.getTime())   
						    .withSchedule(cronSchedule( cTrigger.getCronExpression() ) )   
						    .build();
					
					scheduler.rescheduleJob(trigger.getKey(), trigger1);
	       	  	}

	       	  	//Date nextFireTime = triggers.get(0).getNextFireTime(); 
	        
	       		//System.out.println("[jobName] : " + jobName + " [groupName] : "
	       		//	+ jobGroup + " - " + nextFireTime);
	        
	       }
        
        }
		
		
		return listJobTriggers();
	}


}
