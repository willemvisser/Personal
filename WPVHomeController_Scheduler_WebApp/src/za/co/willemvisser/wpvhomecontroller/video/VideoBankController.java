package za.co.willemvisser.wpvhomecontroller.video;

import java.io.File;
import java.util.HashMap;

import org.apache.log4j.Logger;


public enum VideoBankController {

	INSTANCE;
	
	static final String videoPath = "/Users/willemv/git/Personal/WPVHomeController_Scheduler_WebApp/WebContent/video";
	
	static Logger log = Logger.getLogger(VideoBankController.class.getName());
	
	private HashMap<String, VideoBankDayDTO> videoMap;
	
	public void init() {
		videoMap = new HashMap<String, VideoBankDayDTO>();
	}
	
	public void refresh() {
		processDirectory(videoPath);		
	}
	
	private void processDirectory(String pathToDir) {
		log.info("Processing dir: " + pathToDir);
		File dirListPath = new File(pathToDir);		
		
		String[] dirList = dirListPath.list();
		for (String path : dirList) {
			File f = new File(pathToDir + File.separator + path);
			if (f.isDirectory()) {
				processDirectory(f.getAbsolutePath());
			} else {				
				if (path.endsWith(".swf")) {
					//535-20170607 113825.avi
					addFile(path);
				}		
			}
		}
	}
	
	private void addFile(String path) {
		log.info("processing: " + path);
		int dashPos = path.indexOf('-');
		String dateStr = path.substring(dashPos+1, dashPos+9);
		
		VideoBankDayDTO dto = videoMap.get(dateStr);
		if (dto == null) {
			dto = new VideoBankDayDTO(dateStr);
			videoMap.put(dateStr, dto);
		}
		
		dto.addVideo(path);
	}
	
	public HashMap<String, VideoBankDayDTO> getVideoMap() {
		return videoMap;
	}
	
	public VideoBankDayDTO getVideoBankDayDTO(String dateStr) {
		return videoMap.get(dateStr);
	}

	
}
