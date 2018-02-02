package za.co.willemvisser.wpvhomecontroller.scheduler.job;

import org.quartz.InterruptableJob;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.UnableToInterruptJobException;
import org.apache.log4j.Logger;

import za.co.willemvisser.wpvhomecontroller.util.WaterTankFillUtil;

public class WaterTankFillJob implements InterruptableJob {

	static Logger log = Logger.getLogger(WaterTankFillJob.class.getName());
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		JobDataMap data = context.getJobDetail().getJobDataMap(); 
		String fillDepthStr = data.getString("fillDepth");		//  (this is in CM, when to stop pumping, e.g. 50cm from sensor)
		
		if (fillDepthStr == null) {
			throw new JobExecutionException("No 'fillDepth' specified.");
		}
		
		String maxNoCyclesStr = data.getString("maxNoCycles");
		if (maxNoCyclesStr == null) {
			throw new JobExecutionException("No 'maxNoCycles' specified");
		}
		
		try {
			int fillDepth = Integer.parseInt(fillDepthStr);
			int maxNoCycles = Integer.parseInt(maxNoCyclesStr);
			WaterTankFillUtil.INSTANCE.startPumping(fillDepth, maxNoCycles);
		} catch (Exception e) {
			throw new JobExecutionException("Could not retrieve fill depth from the specified value: " + fillDepthStr);
		}
        
	}

	@Override
	public void interrupt() throws UnableToInterruptJobException {
		WaterTankFillUtil.INSTANCE.stopPumping();		
	}

}
