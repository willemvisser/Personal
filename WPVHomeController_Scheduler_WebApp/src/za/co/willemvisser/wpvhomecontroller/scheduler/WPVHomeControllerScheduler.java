package za.co.willemvisser.wpvhomecontroller.scheduler;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.http.client.utils.CloneUtils;
import org.apache.log4j.Logger;
import org.quartz.CronTrigger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.JobListener;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.TriggerListener;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;

import static org.quartz.JobBuilder.*;   
import static org.quartz.TriggerBuilder.*;   
import static org.quartz.SimpleScheduleBuilder.*;   
import static org.quartz.CronScheduleBuilder.*;   
import static org.quartz.CalendarIntervalScheduleBuilder.*;   
import static org.quartz.JobKey.*;   
import static org.quartz.TriggerKey.*;   
import static org.quartz.DateBuilder.*;   
import static org.quartz.impl.matchers.KeyMatcher.*;   
import static org.quartz.impl.matchers.GroupMatcher.*;   
import static org.quartz.impl.matchers.AndMatcher.*;   
import static org.quartz.impl.matchers.OrMatcher.*;   
import static org.quartz.impl.matchers.EverythingMatcher.*;
import za.co.willemvisser.wpvhomecontroller.scheduler.job.XbeeRemoteCommandJob;
import za.co.willemvisser.wpvhomecontroller.scheduler.job.dto.JobDTO;
import za.co.willemvisser.wpvhomecontroller.scheduler.job.dto.JobTriggerDTO;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

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
	
	public static final String GROUP_NAME_IRRIGATION = "Irrigation";
		
	
	private WPVHomeControllerScheduler() {}
	
	
	
	/**
	 * @param hostIPAddress
	 * @throws SchedulerException
	 */
	public synchronized void startScheduler(String hostIPAddress, InputStream configXmlInputStream) throws SchedulerException {
		if (!isStarted) {
			log.debug("Starting scheduler ...");			
			
			jobMap = new HashMap<String, JobKey>();
			
			generalJobPropertiesMap = new HashMap<String, String>();
			generalJobPropertiesMap.put("hostIPAddress", hostIPAddress);
			
			Properties props = new Properties();
			props.setProperty("org.quartz.scheduler.skipUpdateCheck","true");
			props.setProperty("org.quartz.jobStore.class", "org.quartz.simpl.RAMJobStore");
			props.setProperty("org.quartz.threadPool.class", "org.quartz.simpl.SimpleThreadPool");
			props.setProperty("org.quartz.threadPool.threadCount", "1");
			
			SchedulerFactory schdFact = new StdSchedulerFactory(props);
			
	        //scheduler = StdSchedulerFactory.getDefaultScheduler();
			scheduler = schdFact.getScheduler();
			
			scheduler.start();
			
			try {
	        	processXmlConfig(configXmlInputStream);          	
	        } catch (Exception e) {
	        	log.error("Could not process xml: " + e.toString());
	        	throw new SchedulerException(e);
	        }
			isStarted = true;
		}
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
		              
        //TODO - retrieve XML Config from remote location again?

        /* Print out next events */
        //scheduler.getListenerManager().getTriggerListeners();
        //scheduler.getCurrentlyExecutingJobs()
        
        log.debug("Listing Jobs...");
        
        LinkedList<JobTriggerDTO> jobTriggerDTOList = new LinkedList<JobTriggerDTO>();
        for (String groupName : scheduler.getJobGroupNames()) {
        	 
            for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
        
            	String jobName = jobKey.getName();
            	String jobGroup = jobKey.getGroup();
            	JobDataMap jobDataMap = scheduler.getJobDetail(jobKey).getJobDataMap();
        
	       	  	//get job's trigger
	       	  	List<Trigger> triggers = (List<Trigger>) scheduler.getTriggersOfJob(jobKey);
	       	  	
	       	  	for (Trigger trigger : triggers) {
	       	  		jobTriggerDTOList.add(new JobTriggerDTO(jobName, groupName, trigger, jobDataMap) );
	       	  	}
	       	  	//Date nextFireTime = triggers.get(0).getNextFireTime(); 
	        
	       		//System.out.println("[jobName] : " + jobName + " [groupName] : "
	       		//	+ jobGroup + " - " + nextFireTime);
	        
	       }
        
        }
        
        Collections.sort(jobTriggerDTOList, new Comparator<JobTriggerDTO>() {
            public int compare(JobTriggerDTO m1, JobTriggerDTO m2) {
                return m1.getTrigger().getNextFireTime().compareTo(m2.getTrigger().getNextFireTime());
            }
        });
        
        /*
        final int first = 0;
        Collections.sort(jobTriggerDTOList, new Comparator() {		
            public int compare (Object o1, Object o2){
                Date d1 = ((JobTriggerDTO)o1).getTrigger().getNextFireTime();
                Date d2 = ((JobTriggerDTO)o2).getTrigger().getNextFireTime();
                if(d1.before(d2)){
                	jobTriggerDTOList.set(1, (JobTriggerDTO)o1);
                	jobTriggerDTOList.set(2, (JobTriggerDTO)o2);
                }
                return first;
            }
        });
        */
        
        return jobTriggerDTOList;
       /*
        Set<String> jobsList=jobMap.keySet();
        Iterator<String> jobsIterator=jobsList.iterator();

        while (jobsIterator.hasNext()) {
        	String jobName = jobsIterator.next();
        	log.info("*** Job Name: " + jobName );
        	JobKey jobKey = jobMap.get(jobName);
        	
        	
        	List<Trigger> triggers = (List<Trigger>) scheduler.getTriggersOfJob(jobKey);
      	  	if (triggers.size() > 0) {
      	  		Date nextFireTime = triggers.get(0).getNextFireTime(); 
       
      	  		log.info("[jobName] : " + jobName + " [groupName] : "
      	  				+ " - " + nextFireTime);
      	  	} else {
      	  		log.info("[jobName]  " + jobName + " has no triggers");
      	  	}
       
      	          	
        }
        */
        
	}
	
	
	/**
	 * @throws SchedulerException
	 * @throws InterruptedException 
	 */
	public void stopScheduler() throws SchedulerException, InterruptedException {
		scheduler.shutdown(true);
		Thread.sleep(1000);
	}
	
	protected void processXmlConfig(InputStream configXmlInputStream) 
			throws ParserConfigurationException, SAXException, IOException, XPathExpressionException, 
			DOMException, ParseException, CloneNotSupportedException {
		log.debug("processXmlConfig");		
		
        DocumentBuilderFactory dbFactory 
           = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;

        dBuilder = dbFactory.newDocumentBuilder();

        Document doc = dBuilder.parse(configXmlInputStream);
        doc.getDocumentElement().normalize();

        XPath xPath =  XPathFactory.newInstance().newXPath();

        String expression = "/jobsconfig/job";	        
        NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(doc, XPathConstants.NODESET);
        for (int i = 0; i < nodeList.getLength(); i++) {
        	Node nNode = nodeList.item(i);
        	Element eElement = (Element) nNode;
        	JobDTO jobDTO = new JobDTO(xPath, eElement);
        	addJob(jobDTO, generalJobPropertiesMap);
        	        	
        	if (jobDTO.getGroupName().equals( JobDTO.GROUPNAME_IRRIGATION )) {
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
        		jobDTOEnd.getParams().put("command", "xoff");
        		
        		addJob(jobDTOEnd, generalJobPropertiesMap);
        	}
        }
	}
	
	
	public void addJob(JobDTO jobDTO, HashMap<String, String> jobPropertiesMap) {
		try {
			log.debug("Adding new job..." + jobDTO.toString());
			Class reportClass = Class.forName(jobDTO.getClassName());
			JobDetail job1 = newJob(reportClass)   
				    //.withIdentity(jobDTO.getName(), jobDTO.getGroupName())
					.withIdentity(jobDTO.getCronExpression(), jobDTO.getGroupName())
				    .build(); 			
			
			job1.getJobDataMap().putAll(jobPropertiesMap);  
			job1.getJobDataMap().putAll( jobDTO.getParams() );
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
		}
	}



	public HashMap<String, String> getGeneralJobPropertiesMap() {
		return generalJobPropertiesMap;
	}
	
	
	
	public List<JobTriggerDTO> cancelGroupTriggersForToday(String groupNameToCancel) throws SchedulerException {
		for (String groupName : scheduler.getJobGroupNames()) {
       	 
            for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupNameToCancel))) {        
            	
            	String jobName = jobKey.getName();
            	String jobGroup = jobKey.getGroup();
            	
            	
            	
            	
            	JobDataMap jobDataMap = scheduler.getJobDetail(jobKey).getJobDataMap();
        
	       	  	//get job's trigger
	       	  	List<Trigger> triggers = (List<Trigger>) scheduler.getTriggersOfJob(jobKey);
	       	  	
	       	  	for (Trigger trigger : triggers) {
	       	  		Date triggerStartTime = trigger.getStartTime();
	       	  		
		       	  	Calendar cal = GregorianCalendar.getInstance();
					cal.setTime(triggerStartTime);					
					cal.add(Calendar.DATE, 1);
					
					log.info("Right, Job: " + jobName + " with trigger start: " + triggerStartTime + " now " + cal.getTime());
	       	  		
					
					CronTrigger trigger1 = newTrigger()    
						    //.withIdentity("trigger_" + jobDTO.getName(), jobDTO.getGroupName())
							.withIdentity("trigger_" + jobDTO.getCronExpression(), jobDTO.getGroupName())
						    .startAt(cal.getTime())   
						    .withSchedule(cronSchedule( jobDTO.getCronExpression() ) )   
						    .build();
					
	       	  	}

	       	  	//Date nextFireTime = triggers.get(0).getNextFireTime(); 
	        
	       		//System.out.println("[jobName] : " + jobName + " [groupName] : "
	       		//	+ jobGroup + " - " + nextFireTime);
	        
	       }
        
        }
		
		
		return listJobTriggers();
	}


}
