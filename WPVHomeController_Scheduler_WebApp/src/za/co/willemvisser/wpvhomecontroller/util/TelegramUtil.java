package za.co.willemvisser.wpvhomecontroller.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;



public enum TelegramUtil {

	INSTANCE;
	
	static Logger log = Logger.getLogger(TelegramUtil.class.getName());
	
	private static final String CHAT_ID = "515240914";
	private static final String HTTPS_URL = "https://api.telegram.org/bot567399121:AAEU3fVmPbnOAV9iN9mR6xQ87kKsgYv-cWQ/sendMessage";
	
	public void sendMessage(String message) {
		try {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("chat_id", CHAT_ID));
			params.add(new BasicNameValuePair("text", message));
				
			HttpResponse response = HttpUtil.INSTANCE.doHttpResponse(HTTPS_URL, params);
			log.info("Telegram Post Response: " + HttpUtil.INSTANCE.getResponseContent(response) );
		} catch (Exception e) {
			log.error("Could not send Telegram message", e);
		}
	}
	
}
