package za.co.willemvisser.wpvhomecontroller.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public enum DateUtil {
	
	INSTANCE;
	
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	
	/**
	 * @param date
	 * @return The date formatted as string yyyy-MM-dd hh:mm:ss
	 */
	public String convertDateTimeToString(Date date) {
		return sdf.format(date);
	}
}
