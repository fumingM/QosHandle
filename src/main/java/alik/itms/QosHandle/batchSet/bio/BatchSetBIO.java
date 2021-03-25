package alik.itms.QosHandle.batchSet.bio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import alik.itms.QosHandle.common.Global;

import com.linkage.commons.thread.ThreadPoolCommon;
import com.linkage.commons.xml.XML;

public class BatchSetBIO {
	
	// log
	private static final Logger logger = LoggerFactory.getLogger(BatchSetBIO.class);

	/**
	 * 初始化常量
	 * @return true || false
	 */
	@SuppressWarnings("deprecation")
	public boolean initApp() {
		logger.warn("BatchSetBIO=>initApp()");
		try {
			XML xml = new XML(Global.G_ConfPath + "config.xml");
			
			// 是否为定制
			Global.BATCHSET_ISMAKE = xml.getIntValue("batchSet.isMake");
			
			// 有效期
			Global.BATCHSET_EXPIRYDATE = xml.getIntValue("batchSet.expiryDate");
			
			// 触发事件开关
			Global.BATCHSET_EVENTENABLE = xml.getIntValue("batchSet.eventEnable");
			
			// 触发事件
			Global.BATCHSET_EVENTLIST = xml.getStringValue("batchSet.eventList");
			
			// 失败重新触发开关
			Global.BATCHSET_FAILENABLE = xml.getIntValue("batchSet.failRestEnable");
			
			// 失败重新触发情况
			Global.BATCHSET_FAILRESETLIST = xml.getStringValue("batchSet.failResetList");
			
			// 线程池大小
			Global.BATCHSET_POOLSIZE = xml.getIntValue("batchSet.poolSize");
			Global.BATCHSET_THREADPOOL = ThreadPoolCommon.getFixedThreadPool(Global.BATCHSET_POOLSIZE);
			
			Global.BATCHSET_TABLENAME = xml.getStringValue("batchSet.tableName");
			
			// 设置节点对象	
			/*List<Element> nodeList = xml.getElements("batchSet.nodeList.node");
			if(null != nodeList && !nodeList.isEmpty()){
				for(Element node : nodeList){
					NodeObj nodeObj = new NodeObj();
					nodeObj.setName(node.getChildTextNormalize("name"));
					nodeObj.setValue(node.getChildTextNormalize("value"));
					Global.BATCHSET_NODELIST.add(nodeObj);
				}
			}*/
		} catch (Exception e) {
			logger.error("BatchSetBIO ==> 加载配置文件失败{}！", e);
			return false;
		}
		return true;
	}
}
