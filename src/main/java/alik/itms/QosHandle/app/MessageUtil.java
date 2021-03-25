package alik.itms.QosHandle.app;


import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.linkage.commons.jms.MQMessageHandle;
import com.linkage.commons.jms.MQMessageListener;
import com.linkage.commons.jms.MQPublisher;
import com.linkage.commons.jms.obj.ListenerMessage;
import com.linkage.commons.jms.obj.MQConfig;
import com.linkage.commons.jms.obj.SendMessage;
import com.linkage.commons.jms.obj.ServMessageConfig;
import com.linkage.commons.xml.XML;

public class MessageUtil {
	
	// log
	private static final Logger logger = LoggerFactory.getLogger(MessageUtil.class);
		
	/**
	 * 入参为config.xml配置文件XML对象
	 * 返回值为servMessageConfig实体类的集合
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws NoSuchMethodException 
	 * @throws SecurityException 
	*/
	@SuppressWarnings("unchecked")
	public static List<ServMessageConfig> getServMessageConfig(XML xml,Map<String, MQConfig> mqConfigMap)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, 
			InvocationTargetException, SecurityException, NoSuchMethodException{
		
		logger.warn("开始解析消息配置文件。。。");
		List<ServMessageConfig> servMessageConfigList = new ArrayList<ServMessageConfig>();
		ServMessageConfig servMessageConfig = new ServMessageConfig();
		List<SendMessage> sendMessageList = new ArrayList<SendMessage>();
		
		// 监听实体
		ListenerMessage listenerMessage = new ListenerMessage();
		listenerMessage.setKey(xml.getStringValue("messageConifg.servMessageConfig.listenerMessage.key"));
		listenerMessage.setTopic(xml.getStringValue("messageConifg.servMessageConfig.listenerMessage.topic"));
		logger.warn("handleClass:"+xml.getStringValue("messageConifg.servMessageConfig.listenerMessage.handleClass"));
		Class<MQMessageHandle> mqMessageHandleClass = (Class<MQMessageHandle>)Class.forName(xml.getStringValue("messageConifg.servMessageConfig.listenerMessage.handleClass"));
		Constructor<MQMessageHandle> mqMessageHandleConstructorClass = mqMessageHandleClass.getConstructor(List.class,Map.class);
		listenerMessage.setMqMessageHandle(mqMessageHandleConstructorClass.newInstance(sendMessageList,mqConfigMap));
		servMessageConfig.setListenerMessage(listenerMessage);
		
		servMessageConfigList.add(servMessageConfig);
		logger.warn("servMessageConfigList长度为[{}]",servMessageConfigList.size());
		return servMessageConfigList;
	}
	
	/**
	 * 启动所有需要监听的线程
	 * @param servMessageConfig
	 */
	public static void mqListenerList(List<ServMessageConfig> servMessageConfigList,Map<String, MQConfig> mqConfigMap){
		
		if(null != servMessageConfigList && !servMessageConfigList.isEmpty()){
			for(ServMessageConfig servMessageConfig : servMessageConfigList){
				
				ListenerMessage listenerMessage = servMessageConfig.getListenerMessage();
				
				logger.warn("mqListenerList:"+listenerMessage.getKey()+","+listenerMessage.getTopic());
				if (null != mqConfigMap){
					if ("activeMQ".equals(mqConfigMap.get(listenerMessage.getKey()).getType())){
						
						// 监听MQ信息
						logger.warn("MQ 监听dev.informbatchSupGather消息");
					}
					else if("kafka".equals(mqConfigMap.get(listenerMessage.getKey()).getType())){
						logger.warn("KAFKA 监听dev.informbatchSupGather消息");
					}
				}
				// 启动监听
				new MQMessageListener(mqConfigMap, listenerMessage.getKey(),
						listenerMessage.getTopic(), listenerMessage.getMqMessageHandle()).start();
			}
		}	
	}
	
	/**
	 * 转发消息
	 * @param sendMessageList
	 * @param mqConfigMap
	 * @param message
	 */
	public static void mqSendMessage(List<SendMessage> sendMessageList,Map<String, MQConfig> mqConfigMap,String message){
		
		// 读取MQPool.xml配置项
		if(null != sendMessageList && !sendMessageList.isEmpty()){
			for(SendMessage sendMessage : sendMessageList){
				MQPublisher mqPublisher = new MQPublisher(sendMessage.getKey(), mqConfigMap);
				List<String> sendTopicList = sendMessage.getTopic();
				if(null != sendTopicList && !sendTopicList.isEmpty()){
					for(String sendTopic : sendTopicList){
						mqPublisher.publishMQ(sendTopic, message);
					}
				}
			}	
		}
	}

}

