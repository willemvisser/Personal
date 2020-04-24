package za.co.willemvisser.wpvhomecontroller.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import org.apache.log4j.Logger;



public enum TelegramUtil {

	INSTANCE;
	
	static Logger log = Logger.getLogger(TelegramUtil.class.getName());
		
	private static final String HTTPS_URL = "https://api.telegram.org/bot%s/sendMessage?chat_id=%s&text=%s";
	
	private String apiToken;
	private String chatID;		

	public String getApiToken() {
		return apiToken;
	}

	public void setApiToken(String apiToken) {
		this.apiToken = apiToken;
	}

	public String getChatID() {
		return chatID;
	}

	public void setChatID(String chatID) {
		this.chatID = chatID;
	}
	
	public void sendMessage(String message) {
		
		try {
			//String urlString = "https://api.telegram.org/bot%s/sendMessage?chat_id=%s&text=%s";
			String urlString = String.format(HTTPS_URL, getApiToken(), getChatID(), message);
			
			URL url = new URL(urlString);
			URLConnection conn = url.openConnection();
	
			StringBuilder sb = new StringBuilder();
			InputStream is = new BufferedInputStream(conn.getInputStream());
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String inputLine = "";
			while ((inputLine = br.readLine()) != null) {
			    sb.append(inputLine);
			}
			String response = sb.toString();
			
			log.info(response);
		} catch (Exception e) {
			log.error("Could not send Telegram message", e);
		}
		
	}
	
}
