package za.co.willemvisser.wpvhomecontroller.scheduler.job;

import java.io.IOException;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import za.co.willemvisser.wpvhomecontroller.util.HttpUtil;

public class XbeeRemoteIrrigationCommandJob implements Job {

	static Logger log = Logger.getLogger(XbeeRemoteIrrigationCommandJob.class.getName());
	
	//private static final String PUMP_ON_URL = "http://192.168.1.201/xon/3/D/7";
	//private static final String PUMP_OFF_URL = "http://192.168.1.201/xoff/3/D/7";
	
	private static final String PUMP_ON_URL = "http://localhost:8080/WPVHomeController_Scheduler_WebApp/xbee/xon/3/D/7";
	private static final String PUMP_OFF_URL = "http://localhost:8080/WPVHomeController_Scheduler_WebApp/xbee/xoff/3/D/7";
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		JobDataMap data = context.getJobDetail().getJobDataMap(); 
		String ip = data.getString("hostIPAddress");		//e.g. 192.168.1.201
		String command = data.getString("command");			//e.g. xon or xoff
		String boardID = data.getString("boardId");
		String outputId = data.getString("ouputId");		//Either P or D
		String pin = data.getString("pin");					//0 to 9
		
		StringBuffer commandUrl = new StringBuffer();
		commandUrl.append("http://").append(ip).append(":8080/WPVHomeController_Scheduler_WebApp/xbee/").append(command).append("/").append(boardID);
		commandUrl.append("/").append(outputId).append("/").append(pin);
		
		log.debug("Executing: " + commandUrl);
			
		
		HttpClient httpClient = HttpClientBuilder.create().build();
		
		try {
			//executeHttpGetRequest(httpClient, commandUrl.toString());
			HttpResponse httpResponse = HttpUtil.INSTANCE.doHttpGet( commandUrl.toString() );
			StringBuffer responseBuffer = HttpUtil.INSTANCE.getResponseContent(httpResponse);
			if (!responseBuffer.toString().equals("OK")) {
				
				if (command.equals("xon")) {
					log.error("Could not turn ON valve - that means we should not switch on the pump!");
					log.error("TODO - SMS / email Willem ASAP");
					return;
				} else {
					log.error("Could not turn OFF valve - that means we should actually not continue with the next valve");
					log.error("TODO - SMS / email Willem ASAP");
					try {
						turnOffPump(httpClient);
					} catch (Exception ee) {
						log.error("We could not turn OFF the PUMP ... TODO SMS / email Willem ASAP");
					}
					return;
				}
				
			}
		} catch (Exception e) {
			if (command.equals("xon")) {
				log.error("Could not turn ON valve - that means we should not switch on the pump!");
				log.error("TODO - SMS / email Willem ASAP");
				return;
			} else {
				log.error("Could not turn OFF valve - that means we should actually not continue with the next valve");
				log.error("TODO - SMS / email Willem ASAP");
				try {
					turnOffPump(httpClient);
				} catch (Exception ee) {
					log.error("We could not turn OFF the PUMP ... TODO SMS / email Willem ASAP");
				}
				return;
			}
			
		}
		
        if (command.equals("xon")) {
        	try {
				turnOnPump(httpClient);
			} catch (Exception ee) {
				log.error("We could not turn ON the PUMP ... TODO SMS / email Willem ASAP");
			}
        } else {
        	try {
				turnOffPump(httpClient);
			} catch (Exception ee) {
				log.error("We could not turn OFF the PUMP ... TODO SMS / email Willem ASAP");
			}
        }
                
	}
	
	private void turnOnPump(HttpClient httpClient) throws ClientProtocolException, IOException {
		log.debug("Turning on pump...");
		executeHttpGetRequest(httpClient, PUMP_ON_URL);
	}
	
	private void turnOffPump(HttpClient httpClient) throws ClientProtocolException, IOException {
		log.debug("Turning off pump...");
		executeHttpGetRequest(httpClient, PUMP_OFF_URL);
	}
	
	private void executeHttpGetRequest(HttpClient httpclient, String url) throws ClientProtocolException, IOException {
		log.debug("Executing request: " + url);
		HttpGet httpget = new HttpGet(url);
        httpget.getParams().setParameter("http.socket.timeout", new Integer(5000));

        HttpResponse response = httpclient.execute(httpget);
        HttpEntity entity = response.getEntity();;		
			
		log.debug("Response Status: " + response.getStatusLine());
			
	    if (entity != null) {
	        EntityUtils.consume(entity);
	    }
	    
	    //TODO - check response for starting with "ERROR" and if so throw exception
		
	}
	
	

}
