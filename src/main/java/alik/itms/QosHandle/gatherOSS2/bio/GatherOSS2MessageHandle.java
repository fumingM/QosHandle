package alik.itms.QosHandle.gatherOSS2.bio;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import alik.itms.QosHandle.common.Global;
import alik.itms.QosHandle.gatherOSS2.thread.GatherOSS2Thread;

import com.linkage.commons.jms.MQMessageHandle;
import com.linkage.commons.jms.MQPublisher;
import com.linkage.commons.jms.obj.MQConfig;
import com.linkage.commons.jms.obj.SendMessage;
import com.linkage.commons.util.StringUtil;
import com.linkage.commons.xml.XML;

/**
 * 批量采集组播OSS2节点
 * @author jiafh
 *
 */
public class GatherOSS2MessageHandle extends MQMessageHandle{

	private static Logger loger = LoggerFactory.getLogger(GatherOSS2MessageHandle.class);
	
	private static int count = 0;
	private static HashSet<String> devSet = new HashSet<String>();
	
	public GatherOSS2MessageHandle(List<SendMessage> sendMessageList,Map<String, MQConfig> mqConfigMap) {
		super(sendMessageList, mqConfigMap);
	}
	
	@Override
	public void handTopicMessage(String topic, String message) {

		loger.warn("处理{}消息：{}",topic,message);
		
		// 解析消息
		XML xml = new XML(message,"String");
		
		String eventList = ((Element) xml.getElement("devList").getChildren("dev").get(0)).getChildText("eventList");
		String deviceId = ((Element) xml.getElement("devList").getChildren("dev").get(0)).getChildText("devId");
		if(!StringUtil.IsEmpty(eventList)){
			//上报事件
			String[] eventArr = eventList.split(",");
			Arrays.sort(eventArr);
			
			//触发的eventlist
			String batchEventList = Global.GATHEROSS2_EVENTLIST == null ? "" : Global.GATHEROSS2_EVENTLIST;
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
				if("232031".equals(deviceId)){
					Global.GATHEROSS2_THREADPOOL.execute(new GatherOSS2Thread(message));
				}
				else{
					int currcount = chkCount(deviceId, "get");
					if(currcount == -1){
						loger.warn("[{}]在队列中已存在，结束",deviceId);
					}
					else if(currcount < Global.GATHER_NUM){
						loger.warn("[{}]当前count={},准备处理", deviceId, currcount);
						Global.GATHEROSS2_THREADPOOL.execute(new GatherOSS2Thread(message));
					}
					else{
						loger.warn("[{}]当前count={},跳过", deviceId, currcount);
					}
				}
			}else{
				loger.warn("eventList[{}]不存在需要采集的事件",eventList);
				messageTran(message,"gatherOSS2",deviceId);
			}		
		}else{
			loger.warn("eventList[{}]不存在需要采集的事件",eventList);
			messageTran(message,"gatherOSS2",deviceId);
		}	
	}
	
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
		loger.warn("设备ID[{}]没有[{}]的处理任务，转发给[{}]模块", deviceId, mqConfig, tranTopic);
		
		MQPublisher mqPublisher = null;
		if(Global.MQPUBLISHERMAP.isEmpty() || null == Global.MQPUBLISHERMAP.get(tranTopic)){
			mqPublisher = new MQPublisher("dev.inform" + tranTopic, mqConfig, Global.MQ_POOl_MAP);
			Global.MQPUBLISHERMAP.put(tranTopic, mqPublisher);
		}else{
			mqPublisher = Global.MQPUBLISHERMAP.get(tranTopic);
		}
		
		mqPublisher.publishString(message);
	}
	
	public static void main(String[] args)
	{
		devSet.add("348560");
		System.out.println(devSet.size());
		System.out.println(devSet.contains("348560"));
		for(String set:devSet){
			System.out.println(set);
		}
	}
	public synchronized static int chkCount(String deviceId, String opera){
		if("add".equals(opera)){
			devSet.add(deviceId);
			count ++;
			loger.warn("[{}]加入队列, 当前数量: {}",deviceId, count);
			return count;
		}
		else if("get".equals(opera)){
			if(devSet.contains(deviceId)){
				return -1;
			}
			return count;
		}
		else if("del".equals(opera)){
			devSet.remove(deviceId);
			count --;
			loger.warn("[{}]从队列清除, 当前数量: {}",deviceId, count);
			return count;
		}
		else{
			System.out.println("对count操作异常：opera="+opera);
			return count;
		}
	}
}
