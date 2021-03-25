package alik.itms.QosHandle.common;

import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.linkage.commons.jms.MQPublisher;
import com.linkage.commons.util.StringUtil;
import com.linkage.commons.xml.XML;


public class APPUtil {
	
	private static Logger log = LoggerFactory.getLogger(APPUtil.class);
	
	/**
	 * 转发消息
	 * @param message
	 */
	public static void messageTran(String message,String mqConfig,String deviceId){
		
		// 解析消息
		XML xml = new XML(message,"String");
		// 获取存活列表
		String tranTopic = "other";
		String aliveList = ((Element) xml.getElement("devList").getChildren("dev").get(0)).getChildText("cmAliveList");	
		if(!StringUtil.IsEmpty(aliveList)){
			String[] aliveArr = aliveList.split(",");
			for(int index=0;index<aliveArr.length;index++){
				if(mqConfig.equals(aliveArr[index]) && index != aliveArr.length-1){
					tranTopic = aliveArr[index + 1];
					break;
				}
			}	
		}
		log.warn("[{}]没有设备ID[{}]的处理任务，转发给[{}]模块",mqConfig,deviceId,tranTopic);
		
		MQPublisher mqPublisher = null;
		if(Global.MQPUBLISHERMAP.isEmpty() || null == Global.MQPUBLISHERMAP.get(tranTopic)){
			mqPublisher = new MQPublisher("dev.inform" + tranTopic,tranTopic,Global.MQ_POOl_MAP);
			Global.MQPUBLISHERMAP.put(tranTopic, mqPublisher);
		}else{
			mqPublisher = Global.MQPUBLISHERMAP.get(tranTopic);
		}
		
		mqPublisher.publishString(message);
	}
}
