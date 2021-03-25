package alik.itms.QosHandle.superPasswd;

import java.util.Timer;
import alik.itms.QosHandle.superPasswd.bio.SuperPasswdBIO;
import alik.itms.QosHandle.superPasswd.thread.SuperPasswdExpiryDateTimer;

public class SuperPasswdApp {
	
	public void superPasswdAppStart(){
		
		// 初始化功能配置项
		SuperPasswdBIO superPasswdBIO = new SuperPasswdBIO();
		superPasswdBIO.initApp();
		
		// 十分钟执行一次删除过期数据操作
		new Timer().schedule(new SuperPasswdExpiryDateTimer(), 10 * 1000L,600 * 1000L);
	}
}
