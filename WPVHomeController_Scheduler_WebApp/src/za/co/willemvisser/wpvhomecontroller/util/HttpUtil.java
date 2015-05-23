package za.co.willemvisser.wpvhomecontroller.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;

public enum HttpUtil {
	
	INSTANCE;
	
	static Logger log = Logger.getLogger(HttpUtil.class.getName());
	
	
	public HttpResponse doHttpGet(String url) throws IOException {
		HttpClient httpclient = HttpClientBuilder.create().build();

        HttpGet httpget = new HttpGet( url );

        HttpResponse response;        
		
		response = httpclient.execute(httpget);
		
		
		log.debug("doHttpGet (" + url + ") Response Status: " + response.getStatusLine());		        

        return response;
	}
	
	public StringBuffer getResponseContent(HttpResponse httpResponse) throws IOException {
		//HttpEntity entity = httpResponse.getEntity();
		
//		if (entity != null) {
//        	EntityUtils.consume(entity);
//        }
		
		BufferedReader rd = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
		StringBuffer buffer = new StringBuffer();
		String body = "";
		while ((body = rd.readLine()) != null) 
		{
		    buffer.append(body);
		}
		return buffer;
	}	
	
}
