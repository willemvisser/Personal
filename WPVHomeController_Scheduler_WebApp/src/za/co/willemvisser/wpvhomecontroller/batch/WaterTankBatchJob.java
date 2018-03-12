package za.co.willemvisser.wpvhomecontroller.batch;

import za.co.willemvisser.wpvhomecontroller.util.WaterTankFillUtil;

public class WaterTankBatchJob {

	public static void main(String[] args) {
		WaterTankFillUtil.INSTANCE.startPumping(40, 2, 320);
		try {
			Thread.sleep(20000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}

}
