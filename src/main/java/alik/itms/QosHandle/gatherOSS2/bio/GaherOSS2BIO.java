package alik.itms.QosHandle.gatherOSS2.bio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import alik.itms.QosHandle.common.Global;

import com.linkage.commons.thread.ThreadPoolCommon;
import com.linkage.commons.xml.XML;

public class GaherOSS2BIO {
	
	// log
	private static final Logger logger = LoggerFactory.getLogger(GaherOSS2BIO.class);

	/**
	 * 初始化常量
	 * @return true || false
	 */
	@SuppressWarnings("deprecation")
	public boolean initApp() {
		logger.warn("GaherVlanBIO=>initApp()");
		try {
			XML xml = new XML(Global.G_ConfPath + "config.xml");
			
			// 是否为定制
			Global.GATHEROSS2_ISMAKE = xml.getIntValue("gatherOSS2.isMake");
			
			// 有效期
			Global.GATHEROSS2_EXPIRYDATE = xml.getIntValue("gatherOSS2.expiryDate");
			
			// 触发事件开关
			Global.GATHEROSS2_EVENTENABLE = xml.getIntValue("gatherOSS2.eventEnable");
			
			// 触发事件
			Global.GATHEROSS2_EVENTLIST = xml.getStringValue("gatherOSS2.eventList");
			
			// 失败重新触发开关
			Global.GATHEROSS2_FAILENABLE = xml.getIntValue("gatherOSS2.failRestEnable");
			
			// 失败重新触发情况
			Global.GATHEROSS2_FAILRESETLIST = xml.getStringValue("gatherOSS2.failResetList");
			
			//采集表明
			Global.GATHEROSS2_TABLENAME = xml.getStringValue("gatherOSS2.tableName");
			
			// 线程池大小
			Global.GATHEROSS2_POOLSIZE = xml.getIntValue("gatherOSS2.poolSize");
			Global.GATHEROSS2_THREADPOOL = ThreadPoolCommon.getFixedThreadPool(Global.GATHEROSS2_POOLSIZE);
						
			
			Global.GATHER_NUM = xml.getIntValue("gatherOSS2.gatherNum");
			
		} catch (Exception e) {
			logger.error("GaherOSS2BIO ==> 加载配置文件失败{}！", e);
			return false;
		}
		return true;
	}
}
