package alik.itms.QosHandle.batchSet.bio;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import alik.itms.QosHandle.batchSet.thread.BatchSetServThread;
import alik.itms.QosHandle.common.APPUtil;
import alik.itms.QosHandle.common.Global;

import com.linkage.commons.jms.MQMessageHandle;
import com.linkage.commons.jms.obj.MQConfig;
import com.linkage.commons.jms.obj.SendMessage;
import com.linkage.commons.util.StringUtil;
import com.linkage.commons.xml.XML;

/**
 * 批量设置固定节点功能
 * @author jiafh
 *
 */
public class BatchSetMessageHandle extends MQMessageHandle{

	private Logger loger = LoggerFactory.getLogger(BatchSetMessageHandle.class);
	
	public BatchSetMessageHandle(List<SendMessage> sendMessageList,Map<String, MQConfig> mqConfigMap) {
		super(sendMessageList, mqConfigMap);
	}
	
	@Override
	public void handTopicMessage(String topic, String message) {

		loger.debug("处理{}消息：{}",topic,message);
		
		// 解析消息
		XML xml = new XML(message,"String");
		if(Global.BATCHSET_EVENTENABLE == 1){
			String eventList = ((Element) xml.getElement("devList").getChildren("dev").get(0)).getChildText("eventList");
			String deviceId = ((Element) xml.getElement("devList").getChildren("dev").get(0)).getChildText("devId");
			if(!StringUtil.IsEmpty(eventList)){
				String[] eventArr = eventList.split(",");
				Arrays.sort(eventArr);
				
				String batchEventList = Global.BATCHSET_EVENTLIST == null ? "" : Global.BATCHSET_EVENTLIST;
				String[] batchEventArr = batchEventList.split(",");
				boolean isSet = false;
				for(String batchEvent : batchEventArr){
					if(Arrays.binarySearch(eventArr, batchEvent) >= 0){
						isSet = true;
						break;
					}
				}
				
				// 判断是否需要处理事件
				if(isSet){
					loger.warn("eventList[{}]存在需要设置的事件",eventList);
					Global.BATCHSET_THREADPOOL.execute(new BatchSetServThread(message));
				}else{
					loger.warn("eventList[{}]不存在需要设置的事件",eventList);
					APPUtil.messageTran(message,Global.G_Alias,deviceId);
				}		
			}else{
				loger.warn("eventList[{}]不存在需要设置的事件",eventList);
				APPUtil.messageTran(message,Global.G_Alias,deviceId);
			}	
		}else{
			Global.BATCHSET_THREADPOOL.execute(new BatchSetServThread(message));
		}
	}
}
