package alik.itms.QosHandle.batchSet;

import java.util.Timer;
import alik.itms.QosHandle.batchSet.bio.BatchSetBIO;
import alik.itms.QosHandle.batchSet.thread.BatchSetExpiryDateTimer;

/**
 * 批量设置固定节点启动类
 * @author jiafh
 *
 */
public class BatchSetApp {
	
	public void batchSetAppStart(){
		
		// 初始化功能配置项
		BatchSetBIO batchSetBIO = new BatchSetBIO();
		batchSetBIO.initApp();
		
		// 十分钟执行一次删除过期数据操作
		new Timer().schedule(new BatchSetExpiryDateTimer(), 10 * 1000L,600 * 1000L);
	}
}
