package alik.itms.QosHandle.gatherVlan.bio;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import alik.itms.QosHandle.common.APPUtil;
import alik.itms.QosHandle.common.Global;
import alik.itms.QosHandle.gatherVlan.thread.GatherVlanThread;

import com.linkage.commons.jms.MQMessageHandle;
import com.linkage.commons.jms.obj.MQConfig;
import com.linkage.commons.jms.obj.SendMessage;
import com.linkage.commons.util.StringUtil;
import com.linkage.commons.xml.XML;

/**
 * 批量采集组播VLAN节点
 * @author jiafh
 *
 */
public class GatherVlanMessageHandle extends MQMessageHandle{

	private Logger loger = LoggerFactory.getLogger(GatherVlanMessageHandle.class);
	
	public GatherVlanMessageHandle(List<SendMessage> sendMessageList,Map<String, MQConfig> mqConfigMap) {
		super(sendMessageList, mqConfigMap);
	}
	
	@Override
	public void handTopicMessage(String topic, String message) {

		loger.debug("处理{}消息：{}",topic,message);
		
		// 解析消息
		XML xml = new XML(message,"String");
		if(Global.GATHERVLAN_EVENTENABLE == 1){
			String eventList = ((Element) xml.getElement("devList").getChildren("dev").get(0)).getChildText("eventList");
			String deviceId = ((Element) xml.getElement("devList").getChildren("dev").get(0)).getChildText("devId");
			if(!StringUtil.IsEmpty(eventList)){
				//上报事件
				String[] eventArr = eventList.split(",");
				Arrays.sort(eventArr);
				
				//触发的eventlist
				String batchEventList = Global.GATHERVLAN_EVENTLIST == null ? "" : Global.GATHERVLAN_EVENTLIST;
				String[] batchEventArr = batchEventList.split(",");
				boolean isSet = false;
				for(String batchEvent : batchEventArr){
					if(Arrays.binarySearch(eventArr, batchEvent) >= 0){
						isSet = true;
						break;
					}
				}
				
				// 判断若上报事件在触发事件列表中
				if(isSet){
					Global.GATHERVLAN_THREADPOOL.execute(new GatherVlanThread(message));
				}else{
					loger.warn("eventList[{}]不存在需要采集的事件",eventList);
					APPUtil.messageTran(message,"gatherVlan",deviceId);
				}		
			}else{
				loger.warn("eventList[{}]不存在需要采集的事件",eventList);
				APPUtil.messageTran(message,"gatherVlan",deviceId);
			}	
		}else{
			Global.GATHERVLAN_THREADPOOL.execute(new GatherVlanThread(message));
		}
	}
}
