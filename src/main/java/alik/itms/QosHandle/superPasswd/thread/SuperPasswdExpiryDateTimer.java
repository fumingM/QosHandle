package alik.itms.QosHandle.superPasswd.thread;

import java.util.TimerTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import alik.itms.QosHandle.common.Global;
import alik.itms.QosHandle.superPasswd.dao.SuperPasswdDAO;

/**
 * 定时任务
 * @author jiafh
 *
 */
public class SuperPasswdExpiryDateTimer extends TimerTask{
	
	private Logger loger = LoggerFactory.getLogger(SuperPasswdExpiryDateTimer.class);
	
	private SuperPasswdDAO superPasswdDAO = new SuperPasswdDAO();

	@Override
	public void run() {
		
		// 判断是否设置有效期		
		if(0 != Global.SUPERPASSWD_EXPIRYDATE){
			
			// 当前时间
			long currTime = System.currentTimeMillis()/1000;
			
			// 失效时间
			long addTime = currTime - Global.SUPERPASSWD_EXPIRYDATE * 24 * 60 * 60;
			
			int deleteCount = superPasswdDAO.deleteOldData(addTime, Global.SUPERPASSWD_TABLENAME);
			loger.warn("SuperPasswdExpiryDateTimer == >删除{}条失效数据。",deleteCount);
		}
	}
}
