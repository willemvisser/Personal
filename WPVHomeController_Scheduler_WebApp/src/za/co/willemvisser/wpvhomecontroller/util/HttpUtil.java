package za.co.willemvisser.wpvhomecontroller.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;

public enum HttpUtil {
	
	INSTANCE;
	
	static Logger log = Logger.getLogger(HttpUtil.class.getName());
	
	
	/**
	 * Given a url, do a HTTP GET request and return the response
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public HttpResponse doHttpGet(String url) throws IOException {
		
		HttpClient httpClient = HttpClients.custom().setUserAgent("Mozilla/5.0 (Linux; Android 4.0.4; Galaxy Nexus Build/IMM76B) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.133 Mobile Safari/535.19").build();	    		
        HttpGet httpget = new HttpGet( url );        
        HttpResponse response = httpClient.execute(httpget);
				
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
