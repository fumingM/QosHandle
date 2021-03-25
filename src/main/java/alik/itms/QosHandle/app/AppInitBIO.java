package alik.itms.QosHandle.app;

import java.io.IOException;
import java.util.Map;

import org.apache.log4j.xml.DOMConfigurator;
import org.logicalcobwebs.proxool.configuration.JAXPConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import alik.itms.QosHandle.common.Global;
import alik.itms.QosHandle.obj.Heartbeat;

import com.ailk.tr069.acsalive.thread.AcsAliveMessageDealThread;
import com.ailk.tr069.devrpc.thread.AcsDevRpcThread;
import com.linkage.commons.heartbeat.client.HeartBeatClient;
import com.linkage.commons.jms.MQConfigParser;
import com.linkage.commons.jms.obj.MQConfig;
import com.linkage.commons.util.FileUtil;
import com.linkage.commons.util.StringUtil;
import com.linkage.commons.xml.XML;
import com.linkage.commons.xml.XML2Bean;



/**
 * 此类放置一些公共的处理逻辑
 * 
 * 单个功能初始化配置文件、处理逻辑不要放在此类中
 * 
 * 启动类不要放在 此类和此包路径下
 * 
 * @author jiafh
 *
 */
public class AppInitBIO {
	
	/** log */
	private Logger logger = LoggerFactory.getLogger(AppInitBIO.class);
	
	/**
	 * 初始化
	 */
	public void init(){
		logger.warn("AppInit=>init()");
		this.initLog();
		boolean res = this.initDB();
		logger.error("数据库加载结果："+res);
		this.initApp();
		this.initClient();
	}
	
	/**
	 * 初始化日志文件
	 * 
	 * @return true || false
	 */
	private boolean initLog() {
		logger.warn("AppInit=>initLog()");
		try {
			DOMConfigurator.configureAndWatch(Global.G_ConfPath + "log4j.xml");
		} catch (RuntimeException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 初始化数据库
	 * @return true || false
	 */
	private boolean initDB() {
		logger.warn("AppInit=>initDB()");
		try {
			JAXPConfigurator.configure(Global.G_ConfPath + "proxool.xml",false);
		} catch (Exception e) {
			logger.error("Exception:\n{}", e);
			return false;
		}
		return true;
	}

	/**
	 * 初始化常量
	 * @return true || false
	 */
	private boolean initApp() {
		logger.warn("AppInit=>initApp()");
		try {
			XML xml = new XML(Global.G_ConfPath + "config.xml");
			
			Global.G_Alias = xml.getStringValue("Alias");
			
			// 系统名称
			Global.G_SERVERNAME = xml.getStringValue("ServerName");
			
			// 系统版本
			Global.G_VERSION = xml.getStringValue("Version");
			
			// 区域
			Global.G_INSTAREA = xml.getStringValue("InstArea");
			
			Global.G_BATCHSETENABLE = xml.getIntValue("batchSetEnable");
			
			Global.G_BATCHGATHERVLAN = xml.getIntValue("gatherVlanEnable");
			
			Global.G_BATCHGATHEROSS2 = xml.getIntValue("gatherOSS2Enable");
			
			Global.G_SUPERPASSWDENABLE = xml.getIntValue("superPasswdEnable");
			
			// 初始化MQPool.xml配置文件
			Global.MQ_POOl_MAP = MQConfigParser.getMQConfig(Global.G_ConfPath + "MQPool.xml", "itms");
			if (Global.MQ_POOl_MAP != null) {
				for (Map.Entry<String, MQConfig> entry : Global.MQ_POOl_MAP.entrySet()) {
					Global.ClIENT_ID = entry.getValue().getClientId();
					Global.SYSTEM_NAME = entry.getValue().getSystemName();
					break;
				}
			}
			
			// 初始化ACS
			AcsAliveMessageDealThread acsAliveMessageDealThread = AcsAliveMessageDealThread.getInstance(
					Global.getPrefixName(Global.SYSTEM_NAME)+ Global.SYSTEM_ACS, Global.MQ_POOl_MAP);
			if (acsAliveMessageDealThread != null) {
				acsAliveMessageDealThread.start();
			}

			AcsDevRpcThread acsDevRpcThread = AcsDevRpcThread.getInstance(Global.getPrefixName(
					Global.SYSTEM_NAME) + Global.SYSTEM_ACS, Global.MQ_POOl_MAP);
			if (acsDevRpcThread != null) {
				acsDevRpcThread.start();
			}
			
		} catch (Exception e) {
			logger.error("加载配置文件失败{}！", e);
			return false;
		}
		return true;
	}
	
	/**
	 * 向Qos发送心跳
	 */
	private void initClient()
	{
		logger.debug("AppInit=>initClient()");
		try {
			XML2Bean bean = new XML2Bean(FileUtil.readFile(Global.G_ConfPath + "heartbeat.xml"));
			
			Heartbeat heartbeat = (Heartbeat) bean.getBean("Heartbeat", Heartbeat.class);
			if(1 == heartbeat.getEnab()){
				if(StringUtil.IsEmpty(heartbeat.getIp()) || StringUtil.IsEmpty(heartbeat.getPort())){
					logger.warn("向Qos发送心跳的IP和端口不能为空");
					return;
				}
				
				String[] ipArr = null;
				String[] portArr = null;
				if(!StringUtil.IsEmpty(heartbeat.getIp())){
					ipArr = heartbeat.getIp().split(",");
				}
				if(!StringUtil.IsEmpty(heartbeat.getPort())){
					portArr = heartbeat.getPort().split(",");
				}
				
				if(null == ipArr || null == portArr || ipArr.length != portArr.length || 
						StringUtil.IsEmpty(heartbeat.getIp()) || StringUtil.IsEmpty(heartbeat.getPort())){
					logger.error("发送心跳配置文件配置错误，请重新配置");
					return;
				}
				
				// 向QoS发送心跳
				for(int i = 0; i < ipArr.length; i++){
					HeartBeatClient heartBeatClient = new HeartBeatClient();
					heartBeatClient.setIp(ipArr[i]);
					heartBeatClient.setPort(StringUtil.getIntegerValue(portArr[i]));
					heartBeatClient.setBeatFrequency(heartbeat.getCycle());
					heartBeatClient.setNodeName(heartbeat.getNodeName());
					heartBeatClient.start();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
