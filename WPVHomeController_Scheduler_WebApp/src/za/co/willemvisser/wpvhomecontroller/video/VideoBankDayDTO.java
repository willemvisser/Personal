package za.co.willemvisser.wpvhomecontroller.video;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;


public class VideoBankDayDTO {
	
	static Logger log = Logger.getLogger(VideoBankDayDTO.class.getName());

	private String dayStr;
	private ArrayList<String> videoList;
	
	public VideoBankDayDTO(String dayStr) {
		this.dayStr = dayStr;
		videoList = new ArrayList<>();
	}
	
	public void addVideo(String videoPath) {
		videoList.add(videoPath);
	}
	
	public HashMap<String, ArrayList<String>> listVideosPerHour() {
		HashMap<String, ArrayList<String>> videosPerHourMap = new HashMap<String, ArrayList<String>>();
		
		for (String videoPath : videoList) {
			//First we need to pick out the hour to further sub divide
			int dashPos = videoPath.indexOf('-');
			String hourStr = videoPath.substring(dashPos+9, dashPos+11);		
			//log.info("Listing: " + videoPath + " > Hour: " + hourStr);
			
			ArrayList<String> videoList = videosPerHourMap.get(hourStr);
			if (videoList == null) {
				videoList = new ArrayList<String>();
				videosPerHourMap.put(hourStr, videoList);
			}
			videoList.add(videoPath);
		}
			
		return videosPerHourMap;
	}
	
}
