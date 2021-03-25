package alik.itms.QosHandle.superPasswd.bio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import alik.itms.QosHandle.common.Global;
import com.linkage.commons.thread.ThreadPoolCommon;
import com.linkage.commons.xml.XML;

public class SuperPasswdBIO {
	
	// log
	private static final Logger logger = LoggerFactory.getLogger(SuperPasswdBIO.class);

	/**
	 * 初始化常量
	 * @return true || false
	 */
	@SuppressWarnings("deprecation")
	public boolean initApp() {
		logger.warn("SuperPasswBIO=>initApp()");
		try {
			XML xml = new XML(Global.G_ConfPath + "config.xml");
			
			// 是否为定制
			Global.SUPERPASSWD_ISMAKE = xml.getIntValue("superPasswd.isMake");
			
			// 有效期
			Global.SUPERPASSWD_EXPIRYDATE = xml.getIntValue("superPasswd.expiryDate");
			
			// 触发事件开关
			Global.SUPERPASSWD_EVENTENABLE = xml.getIntValue("superPasswd.eventEnable");
			
			// 触发事件
			Global.SUPERPASSWD_EVENTLIST = xml.getStringValue("superPasswd.eventList");
			
			// 失败重新触发开关
			Global.SUPERPASSWD_FAILENABLE = xml.getIntValue("superPasswd.failRestEnable");
			
			// 失败重新触发情况
			Global.SUPERPASSWD_FAILRESETLIST = xml.getStringValue("superPasswd.failResetList");
			
			Global.SUPERPASSWD_TABLENAME = xml.getStringValue("superPasswd.tableName");
			
			// 线程池大小
			Global.SUPERPASSWD_POOLSIZE = xml.getIntValue("superPasswd.poolSize");
			Global.SUPERPASSWD_THREADPOOL = ThreadPoolCommon.getFixedThreadPool(Global.SUPERPASSWD_POOLSIZE);
			
		} catch (Exception e) {
			logger.error("GaherVlanBIO ==> 加载配置文件失败{}！", e);
			return false;
		}
		return true;
	}
}
