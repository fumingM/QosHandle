package alik.itms.QosHandle.superPasswd.bio;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import alik.itms.QosHandle.common.APPUtil;
import alik.itms.QosHandle.common.Global;
import alik.itms.QosHandle.superPasswd.thread.SuperPasswdThread;
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
public class SuperPasswdMessageHandle extends MQMessageHandle{

	private Logger loger = LoggerFactory.getLogger(SuperPasswdMessageHandle.class);
	
	public SuperPasswdMessageHandle(List<SendMessage> sendMessageList,Map<String, MQConfig> mqConfigMap) {
		super(sendMessageList, mqConfigMap);
	}
	
	@Override
	public void handTopicMessage(String topic, String message) {

		loger.debug("处理{}消息：{}",topic,message);
		
		// 解析消息
		XML xml = new XML(message,"String");
		if(Global.SUPERPASSWD_EVENTENABLE == 1){
			String eventList = ((Element) xml.getElement("devList").getChildren("dev").get(0)).getChildText("eventList");
			String deviceId = ((Element) xml.getElement("devList").getChildren("dev").get(0)).getChildText("devId");
			if(!StringUtil.IsEmpty(eventList)){
				String[] eventArr = eventList.split(",");
				Arrays.sort(eventArr);
				
				String batchEventList = Global.SUPERPASSWD_EVENTLIST == null ? "" : Global.SUPERPASSWD_EVENTLIST;
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
					Global.SUPERPASSWD_THREADPOOL.execute(new SuperPasswdThread(message));
				}else{
					loger.warn("eventList[{}]不存在需要设置的事件",eventList);
					APPUtil.messageTran(message,"superPasswd",deviceId);
				}		
			}else{
				loger.warn("eventList[{}]不存在需要设置的事件",eventList);
				APPUtil.messageTran(message,"superPasswd",deviceId);
			}	
		}else{
			Global.SUPERPASSWD_THREADPOOL.execute(new SuperPasswdThread(message));
		}
	}
}
