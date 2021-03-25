package alik.itms.QosHandle.batchSet.thread;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import alik.itms.QosHandle.batchSet.dao.BatchSetDAO;
import alik.itms.QosHandle.batchSet.obj.NodeObj;
import alik.itms.QosHandle.common.ACSCorba;
import alik.itms.QosHandle.common.APPUtil;
import alik.itms.QosHandle.common.GetDeviceOnLineStatus;
import alik.itms.QosHandle.common.Global;
import alik.itms.QosHandle.obj.ParameValueOBJ;

import com.linkage.commons.util.StringUtil;
import com.linkage.commons.xml.XML;

/**
 * 设置固定节点值逻辑
 * @author jiafh
 *
 */
public class BatchSetServThread implements Runnable{
	
	private Logger loger = LoggerFactory.getLogger(BatchSetServThread.class);
	
	private BatchSetDAO batchSetDAO = new BatchSetDAO();
	
	// 监听到的具体消息
	private String message;
	
	public BatchSetServThread(String message){
		this.message = message;
	}
	List<NodeObj> params = new ArrayList<NodeObj>();

	@Override
	public void run() {
		
		// 解析xml
		XML xml = new XML(message,"String");
		String deviceId = ((Element) xml.getElement("devList").getChildren("dev").get(0)).getChildText("devId");
		ArrayList<HashMap<String, String>> datalist = batchSetDAO.getSetData(deviceId, Global.BATCHSET_TABLENAME);
		if(null == datalist||datalist.size()<1){
			loger.warn("没有"+deviceId+"设备任务，结束");
			return;
		}
		String[] paths = StringUtil.getStringValue(datalist.get(0).get("parampath")).split(",");
		String[] values = StringUtil.getStringValue(datalist.get(0).get("paramvalue")).split(",");
		String[] types = StringUtil.getStringValue(datalist.get(0).get("paramtype")).split(",");
		params = new ArrayList<NodeObj>();
		for(int i=0;i<paths.length;i++){
			NodeObj nodeObj = new NodeObj();
			nodeObj.setName(paths[i]);
			nodeObj.setValue(values[i]);
			nodeObj.setType(types[i]);
			params.add(nodeObj);
		}
		
		if(1 == Global.BATCHSET_ISMAKE ){
			loger.warn("[{}]定制任务",deviceId);
			String eventList = ((Element) xml.getElement("devList").getChildren("dev").get(0)).getChildText("eventList");
			batchSetMake(datalist, deviceId, eventList);		
		}else{
			loger.warn("[{}]全网任务",deviceId);
			batchSetAll(datalist, deviceId);
		}
	}
	
	
	private boolean checkSame(String[] aa,String[] bb){
	   for(String a:aa){
		   for(String b:bb){
			   if(a.equals(b)){
				   return true;
			   }
		   }
	   }
	   return false;
	}
	/**
	 * 定制任务情况
	 * @param datalist
	 */
	private void batchSetMake(ArrayList<HashMap<String, String>> datalist,String deviceId,String eventList){
		if(null != datalist && !datalist.isEmpty() && null != datalist.get(0)){
			eventList = null == eventList ? "" : eventList;
			String[] eventArr = eventList.split(",");
			Arrays.sort(eventArr);
			boolean isSet = false;
			if(0 == Global.BATCHSET_FAILENABLE){
				for(HashMap<String, String> dataMap : datalist){
					if("0".equals(dataMap.get("status")) && 
							("0".equals(dataMap.get("set_strategy")) || checkSame(eventArr, dataMap.get("set_strategy").split(",")))){
						//Arrays.binarySearch(eventArr, eventList.contains(dataMap.get("set_strategy"))) >= 0
						isSet = true;
					}
				}
			}else{
				String[] failResetEventArr = Global.BATCHSET_FAILRESETLIST.split(",");
				Arrays.sort(failResetEventArr);
				for(HashMap<String, String> dataMap : datalist){
					if(("0".equals(dataMap.get("status")) || Arrays.binarySearch(failResetEventArr, dataMap.get("status")) >= 0) &&
							("0".equals(dataMap.get("set_strategy")) || checkSame(eventArr, dataMap.get("set_strategy").split(",")))){
						isSet = true;
					}
				}
			}

			// 设置节点
			if(isSet){
				int result = batchSetNode(deviceId);
				batchSetDAO.updateAllData(deviceId, Global.BATCHSET_TABLENAME, result, System.currentTimeMillis()/1000);	
			}else{
				loger.warn("定制任务[{}]此设备在有效期内已成功设置过节点值或者没开通失败重设功能或者不符合定制策略",deviceId);
				APPUtil.messageTran(message, Global.G_Alias, deviceId);
			}
			
			
		}else{
			loger.debug("定制任务不存在[{}]此设备",deviceId);
			APPUtil.messageTran(message, Global.G_Alias, deviceId);
		}
	}
	
	/**
	 * 全网任务情况
	 * @param datalist
	 */
	private void batchSetAll(ArrayList<HashMap<String, String>> datalist,String deviceId){
		if(null != datalist && !datalist.isEmpty() && null != datalist.get(0)){
			boolean isSet = false;
			if(1 == Global.BATCHSET_FAILENABLE){

				String[] failResetEventArr = Global.BATCHSET_FAILRESETLIST.split(",");
				Arrays.sort(failResetEventArr);
				for(HashMap<String, String> dataMap : datalist){
					if("0".equals(dataMap.get("status")) || Arrays.binarySearch(failResetEventArr, dataMap.get("status")) >= 0){
						isSet = true;
					}
				}
			}
			// 设置节点
			if(isSet){
				int result = batchSetNode(deviceId);
				batchSetDAO.updateAllData(deviceId, Global.BATCHSET_TABLENAME, result, System.currentTimeMillis()/1000);				
			}else{
				loger.warn("全网任务[{}]此设备在有效期内已成功设置过节点值或者没开通失败重设功能",deviceId);
				APPUtil.messageTran(message, Global.G_Alias, deviceId);
			}
			
		}else{
			// 设置节点
			int result = batchSetNode(deviceId);
			batchSetDAO.addAllData(deviceId, Global.BATCHSET_TABLENAME, result, System.currentTimeMillis()/1000);
		}
		
	}
	
	private int batchSetNode(String deviceId){
		
		GetDeviceOnLineStatus getStatus = new GetDeviceOnLineStatus();
		ACSCorba acsCorba = new ACSCorba();
		int flag = getStatus.testDeviceOnLineStatus(deviceId, acsCorba);
		if (-6 == flag) {
			loger.warn("设备正在被操作，无法获取节点值，device_id={}", deviceId);
			return flag;
		}else if (1 == flag){
			loger.warn("[BatchSetServThread][{}]设备在线，可以进行操作", deviceId);
			if(null != params && !params.isEmpty() && null != params.get(0)){
				
				ArrayList<ParameValueOBJ> pvObjList= new ArrayList<ParameValueOBJ>();				
				for(NodeObj nodeObj : params){
					ParameValueOBJ pvObj = new ParameValueOBJ();
					pvObj.setName(nodeObj.getName());
					pvObj.setType(nodeObj.getType());
					pvObj.setValue(nodeObj.getValue());
					pvObjList.add(pvObj);
				}
				int result = acsCorba.setValue(deviceId, pvObjList);
				loger.warn("设备设置结果为，device_id={},result={}", deviceId,result);
				if(result==0||result==1){
					result = 1;
				}
				return result;
			}
			loger.warn("没有配置具体节点，设置失败，device_id={}", deviceId);
			return -999;
		}else{
			loger.warn("设备离线，device_id={},flag={}", deviceId,flag);
			return flag;
		}
	}
}
