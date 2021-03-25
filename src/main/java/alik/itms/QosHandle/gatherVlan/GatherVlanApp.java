package alik.itms.QosHandle.gatherVlan;

import java.util.Timer;

import alik.itms.QosHandle.common.Global;
import alik.itms.QosHandle.common.ThreadMonitor;
import alik.itms.QosHandle.gatherVlan.bio.GaherVlanBIO;
import alik.itms.QosHandle.gatherVlan.thread.GatherVlanExpiryDateTimer;

public class GatherVlanApp {
	
	public void gatherVlanAppStart(){
		
		// 初始化功能配置项
		GaherVlanBIO gaherVlanBIO = new GaherVlanBIO();
		gaherVlanBIO.initApp();
		
		//启动线程监控
		new ThreadMonitor(Global.GATHERVLAN_THREADPOOL, 5).start();

		// 十分钟执行一次删除过期数据操作
		new Timer().schedule(new GatherVlanExpiryDateTimer(), 30 * 1000L,600 * 1000L);
	}
}
