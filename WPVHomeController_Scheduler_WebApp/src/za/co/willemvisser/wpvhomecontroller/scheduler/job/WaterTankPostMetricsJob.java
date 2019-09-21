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

import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClientBuilder;
import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.MetricDatum;
import com.amazonaws.services.cloudwatch.model.PutMetricDataRequest;
import com.amazonaws.services.cloudwatch.model.PutMetricDataResult;
import com.amazonaws.services.cloudwatch.model.StandardUnit;	

public class WaterTankPostMetricsJob implements InterruptableJob {

	static Logger log = Logger.getLogger(WaterTankPostMetricsJob.class.getName());
	
	final AmazonCloudWatch cw =
		    AmazonCloudWatchClientBuilder.defaultClient();
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
					
		try {
			StringBuffer response = HttpUtil.INSTANCE.getResponseContent(HttpUtil.INSTANCE.doHttpGet(
					ConfigController.INSTANCE.getGeneralProperty(ConfigController.PROPERTY_TANK_LEVEL_HTTP_URL).getValue()));
						
			double currentDepth = 0;
			double percentageFull = 0;
			try {
				currentDepth = Double.parseDouble(response.toString());
				percentageFull = NumberUtil.round( ((198.0 - currentDepth + 13.2) / 198.0 * 100), 2);
			} catch (Exception ee) {
				log.error("Could not retrieve current tank depth, not posting any metrics");
				log.error(ee);
				return;
			}
						
				
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
			//throw new JobExecutionException("Could not post metrics to CloudWatch for Water Tank", e);
		}
        
	}

	@Override
	public void interrupt() throws UnableToInterruptJobException {
		//No need to do anything		
	}

}
