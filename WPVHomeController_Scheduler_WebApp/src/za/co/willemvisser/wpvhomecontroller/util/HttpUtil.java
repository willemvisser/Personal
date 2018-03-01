package za.co.willemvisser.wpvhomecontroller.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
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
	
	public HttpResponse doHttpsGet(String url) throws IOException, GeneralSecurityException {
		TrustStrategy acceptingTrustStrategy = (cert, authType) -> true;
	    SSLSocketFactory sf = new SSLSocketFactory(
	      acceptingTrustStrategy, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
	    SchemeRegistry registry = new SchemeRegistry();
	    registry.register(new Scheme("https", 8443, sf));
	    ClientConnectionManager ccm = new PoolingClientConnectionManager(registry);
	 
	    DefaultHttpClient httpClient = new DefaultHttpClient(ccm);
	 
	    HttpGet getMethod = new HttpGet(url);
	     
	    return httpClient.execute(getMethod);
	}
	
	/**
	 * @param url
	 * @param params
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public HttpResponse doHttpResponse(String url, List<NameValuePair> params) 
			  throws ClientProtocolException, IOException {
			    CloseableHttpClient client = HttpClients.createDefault();
			    HttpPost httpPost = new HttpPost(url);
			 
			    httpPost.setEntity(new UrlEncodedFormEntity(params));
			    /*
			    List<NameValuePair> params = new ArrayList<NameValuePair>();
			    params.add(new BasicNameValuePair("username", "John"));
			    params.add(new BasicNameValuePair("password", "pass"));
			    httpPost.setEntity(new UrlEncodedFormEntity(params));
			 	*/
			    
			    CloseableHttpResponse response = client.execute(httpPost);			    
			    client.close();
			    
			    return response;
	}
	
	/**
	 * Parse the HttpResponse and return a StringBuffer containing the response as text
	 * @param httpResponse
	 * @return
	 * @throws IOException
	 */
	public StringBuffer getResponseContent(HttpResponse httpResponse) throws IOException {		
		
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
