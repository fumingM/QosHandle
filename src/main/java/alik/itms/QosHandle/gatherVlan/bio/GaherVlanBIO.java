package alik.itms.QosHandle.gatherVlan.bio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import alik.itms.QosHandle.common.Global;

import com.linkage.commons.thread.ThreadPoolCommon;
import com.linkage.commons.xml.XML;

public class GaherVlanBIO {
	
	// log
	private static final Logger logger = LoggerFactory.getLogger(GaherVlanBIO.class);

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
			Global.GATHERVLAN_ISMAKE = xml.getIntValue("gatherVlan.isMake");
			
			// 有效期
			Global.GATHERVLAN_EXPIRYDATE = xml.getIntValue("gatherVlan.expiryDate");
			
			// 触发事件开关
			Global.GATHERVLAN_EVENTENABLE = xml.getIntValue("gatherVlan.eventEnable");
			
			// 触发事件
			Global.GATHERVLAN_EVENTLIST = xml.getStringValue("gatherVlan.eventList");
			
			// 失败重新触发开关
			Global.GATHERVLAN_FAILENABLE = xml.getIntValue("gatherVlan.failRestEnable");
			
			// 失败重新触发情况
			Global.GATHERVLAN_FAILRESETLIST = xml.getStringValue("gatherVlan.failResetList");
			
			//采集表明
			Global.GATHERVLAN_TABLENAME = xml.getStringValue("gatherVlan.tableName");
			
			// 线程池大小
			Global.GATHERVLAN_POOLSIZE = xml.getIntValue("gatherVlan.poolSize");
			Global.GATHERVLAN_THREADPOOL = ThreadPoolCommon.getFixedThreadPool(Global.GATHERVLAN_POOLSIZE);
						
			// 是否为定制
			Global.GATHERTELNETFTP_ISMAKE = xml.getIntValue("gatherTelnetftp.isMake");
			
			// 有效期
			Global.GATHERTELNETFTP_EXPIRYDATE = xml.getIntValue("gatherTelnetftp.expiryDate");
			
			// 触发事件开关
			Global.GATHERTELNETFTP_EVENTENABLE = xml.getIntValue("gatherTelnetftp.eventEnable");
			
			// 触发事件
			Global.GATHERTELNETFTP_EVENTLIST = xml.getStringValue("gatherTelnetftp.eventList");
			
			// 失败重新触发开关
			Global.GATHERTELNETFTP_FAILENABLE = xml.getIntValue("gatherTelnetftp.failRestEnable");
			
			// 失败重新触发情况
			Global.GATHERTELNETFTP_FAILRESETLIST = xml.getStringValue("gatherTelnetftp.failResetList");
					
			//采集表名
			Global.GATHERTELNETFTP_TABLENAME = xml.getStringValue("gatherTelnetftp.tableName");
			
			// 线程池大小
			Global.GATHERTELNETFTP_POOLSIZE = xml.getIntValue("gatherTelnetftp.poolSize");
			Global.GATHERTELNETFTP_THREADPOOL = ThreadPoolCommon.getFixedThreadPool(Global.GATHERTELNETFTP_POOLSIZE);
			
			Global.GATHER_NUM = xml.getIntValue("gatherVlan.gatherNum");
			
		} catch (Exception e) {
			logger.error("GaherVlanBIO ==> 加载配置文件失败{}！", e);
			return false;
		}
		return true;
	}
}
