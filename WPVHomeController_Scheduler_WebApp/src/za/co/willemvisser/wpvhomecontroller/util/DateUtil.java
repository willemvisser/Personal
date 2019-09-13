package za.co.willemvisser.wpvhomecontroller.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public enum DateUtil {
	
	INSTANCE;
	
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	/**
	 * @param date
	 * @return The date formatted as string yyyy-MM-dd hh:mm:ss
	 */
	public String convertDateTimeToString(Date date) {
		return sdf.format(date);
	}
	
		
	/**
	 * @param dateStr
	 * @return day in format MM/dd
	 * @throws ParseException
	 */
	public String getDayInShortFormat(String dateStr) throws ParseException {
		Date theDate = sdf.parse(dateStr);
		return theDate.getMonth() + "/" + theDate.getDate();
	}
	
	public String getHourInShortFormat(String dateStr) throws ParseException {
		Date theDate = sdf.parse(dateStr);
		if (theDate.getHours() < 12) {
			return theDate.getHours() + " AM";
		} else {
			return theDate.getHours() + " PM";
		}		
	}
}
