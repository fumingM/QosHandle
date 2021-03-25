package alik.itms.QosHandle.gatherOSS2;

import alik.itms.QosHandle.common.Global;
import alik.itms.QosHandle.common.ThreadMonitor;
import alik.itms.QosHandle.gatherOSS2.bio.GaherOSS2BIO;

public class GatherOSS2App {
	
	public void gatherOSS2AppStart(){
		
		// 初始化功能配置项
		GaherOSS2BIO gaherOSS2BIO = new GaherOSS2BIO();
		gaherOSS2BIO.initApp();
		
		//启动线程监控
		new ThreadMonitor(Global.GATHEROSS2_THREADPOOL, 5).start();

		// 十分钟执行一次删除过期数据操作
		/*new Timer().schedule(new GatherVlanExpiryDateTimer(), 30 * 1000L,600 * 1000L);*/
	}
}
