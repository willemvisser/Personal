package za.co.willemvisser.wpvhomecontroller.scheduler.job;

import java.io.IOException;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

public class XbeeRemoteCommandJob implements Job {

	static Logger log = Logger.getLogger(XbeeRemoteCommandJob.class.getName());
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		JobDataMap data = context.getJobDetail().getJobDataMap(); 
		String ip = data.getString("hostIPAddress");		//e.g. 192.168.1.201
		String command = data.getString("command");			//e.g. xon or xoff
		String boardID = data.getString("boardId");
		String outputId = data.getString("ouputId");		//Either P or D
		String pin = data.getString("pin");					//0 to 9
		
		StringBuffer commandUrl = new StringBuffer();
		commandUrl.append("http://").append(ip).append("/").append(command).append("/").append(boardID);
		commandUrl.append("/").append(outputId).append("/").append(pin);
		
		log.debug("Executing: " + commandUrl);
		
		//httpget.getParams().setParameter("http.socket.timeout", new Integer(5000));
		//HttpGet myGet = new HttpGet("http://foo.com/someservlet?param1=foo&param2=bar");
		
		HttpClient httpclient = HttpClientBuilder.create().build();

        HttpGet httpget = new HttpGet(commandUrl.toString());

        HttpResponse response;
        HttpEntity entity;
		try {
			response = httpclient.execute(httpget);
			entity = response.getEntity();
			
			log.debug("XbeeRemoteCommandJob Response Status: " + response.getStatusLine());
			
	        if (entity != null) {
	        	EntityUtils.consume(entity);
	        }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        

        
	}

}
