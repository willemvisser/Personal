package za.co.willemvisser.wpvhomecontroller.scheduler.job;

import java.util.ArrayList;
import java.util.Collection;

import org.quartz.InterruptableJob;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.UnableToInterruptJobException;
import org.apache.log4j.Logger;

import za.co.willemvisser.wpvhomecontroller.config.ConfigController;
import za.co.willemvisser.wpvhomecontroller.config.dto.GeneralPropertyDTO;
import za.co.willemvisser.wpvhomecontroller.util.HttpUtil;
import za.co.willemvisser.wpvhomecontroller.util.NumberUtil;
import za.co.willemvisser.wpvhomecontroller.util.WaterTankManager;

import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClientBuilder;
import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.MetricDatum;
import com.amazonaws.services.cloudwatch.model.PutMetricDataRequest;
import com.amazonaws.services.cloudwatch.model.PutMetricDataResult;
import com.amazonaws.services.cloudwatch.model.StandardUnit;	

public class WaterTankPostMetricsJob implements InterruptableJob {

	static Logger log = Logger.getLogger(WaterTankPostMetricsJob.class.getName());
	
	
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
					
		try {
			AmazonCloudWatch cw = AmazonCloudWatchClientBuilder.defaultClient();
			
			
						
			double currentDepth = WaterTankManager.INSTANCE.getWaterTankDepthInCM();
			double percentageFull = WaterTankManager.INSTANCE.getWaterTankDepthPercentage();
													
			Collection<Dimension> dimensions = new ArrayList<Dimension>();
			
			Dimension dimensionHostName = new Dimension()
			    .withName("Host Name")
			    .withValue(ConfigController.INSTANCE.getHostName());
			dimensions.add(dimensionHostName);
			
			GeneralPropertyDTO propDTO = ConfigController.INSTANCE.getGeneralProperty("ServerType");
			if (propDTO != null) {
				Dimension dimensionServerType = new Dimension()
			    	.withName("Server Type")
			    	.withValue(propDTO.getValue());
				dimensions.add(dimensionServerType);
			}
			
			MetricDatum datum = new MetricDatum()
			    .withMetricName("Water Tank Depth")
			    .withUnit(StandardUnit.None)
			    .withValue(currentDepth)
			    .withDimensions(dimensions);
			
			MetricDatum datum2 = new MetricDatum()
				    .withMetricName("Water Tank Percentage Full")
				    .withUnit(StandardUnit.None)
				    .withValue(percentageFull)
				    .withDimensions(dimensions);

			PutMetricDataRequest request = new PutMetricDataRequest()
			    .withNamespace("HomeAutomation")
			    .withMetricData(datum)
			    .withMetricData(datum2);

			cw.putMetricData(request);			
			
			log.debug("Tank metrics posted to CloudWatch");
			
		} catch (Exception e) {
			log.error("Water Tank Post metrics error (posting to AWS: " + e);			
		}
        
	}

	@Override
	public void interrupt() throws UnableToInterruptJobException {
		//No need to do anything		
	}

}
