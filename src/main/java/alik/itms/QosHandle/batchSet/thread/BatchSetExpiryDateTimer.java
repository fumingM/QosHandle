package alik.itms.QosHandle.batchSet.thread;

import java.util.TimerTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import alik.itms.QosHandle.batchSet.dao.BatchSetDAO;
import alik.itms.QosHandle.common.Global;

/**
 * 定时任务
 * @author jiafh
 *
 */
public class BatchSetExpiryDateTimer extends TimerTask{
	
	private Logger loger = LoggerFactory.getLogger(BatchSetExpiryDateTimer.class);
	
	private BatchSetDAO batchSetDAO = new BatchSetDAO();

	@Override
	public void run() {
		
		// 判断是否设置有效期
		if(0 != Global.BATCHSET_EXPIRYDATE){
			
			// 当前时间
			long currTime = System.currentTimeMillis()/1000;
			
			// 失效时间
			long addTime = currTime - Global.BATCHSET_EXPIRYDATE * 24 * 60 * 60;
			
			int deleteCount = batchSetDAO.deleteOldData(addTime, Global.BATCHSET_TABLENAME);
			loger.warn("删除{}条失效数据。",deleteCount);
		}
	}
}
