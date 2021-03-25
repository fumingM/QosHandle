package alik.itms.QosHandle.superPasswd.thread;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.linkage.commons.util.MathUtil;
import com.linkage.commons.xml.XML;
import alik.itms.QosHandle.common.ACSCorba;
import alik.itms.QosHandle.common.APPUtil;
import alik.itms.QosHandle.common.GetDeviceOnLineStatus;
import alik.itms.QosHandle.common.Global;
import alik.itms.QosHandle.obj.ParameValueOBJ;
import alik.itms.QosHandle.superPasswd.dao.SuperPasswdDAO;

/**
 * 设置超级密码节点值逻辑
 * @author jiafh
 *
 */
public class SuperPasswdThread implements Runnable{
	
	private Logger loger = LoggerFactory.getLogger(SuperPasswdThread.class);
	
	private SuperPasswdDAO superPasswdDAO = new SuperPasswdDAO();
	
	// 监听到的具体消息
	private String message;
	
	private String telecomPasswd;
	
	public SuperPasswdThread(String message){
		this.message = message;
	}

	@Override
	public void run() {
		
		// 解析xml
		XML xml = new XML(message,"String");
		String deviceId = ((Element) xml.getElement("devList").getChildren("dev").get(0)).getChildText("devId");
		ArrayList<HashMap<String, String>> datalist = superPasswdDAO.getSetData(deviceId, Global.SUPERPASSWD_TABLENAME);
		if(1 == Global.SUPERPASSWD_ISMAKE ){
			loger.debug("定制任务。。。");
			String eventList = ((Element) xml.getElement("devList").getChildren("dev").get(0)).getChildText("eventList");
			superPasswdMake(datalist, deviceId, eventList);		
		}else{
			loger.debug("全网任务。。。");
			superPasswdAll(datalist, deviceId);
		}
	}
	
	/**
	 * 定制任务情况
	 * @param datalist
	 */
	private void superPasswdMake(ArrayList<HashMap<String, String>> datalist,String deviceId,String eventList){
		if(null != datalist && !datalist.isEmpty() && null != datalist.get(0)){
			eventList = null == eventList ? "" : eventList;
			String[] eventArr = eventList.split(",");
			Arrays.sort(eventArr);
			boolean isSet = false;
			if(0 == Global.SUPERPASSWD_FAILENABLE){
				for(HashMap<String, String> dataMap : datalist){
					if("0".equals(dataMap.get("status")) && 
							("0".equals(dataMap.get("set_strategy")) || Arrays.binarySearch(eventArr, eventList.contains(dataMap.get("set_strategy"))) >= 0 )){
						isSet = true;
					}
				}
			}else{
				String[] failResetEventArr = Global.SUPERPASSWD_FAILRESETLIST.split(",");
				Arrays.sort(failResetEventArr);
				for(HashMap<String, String> dataMap : datalist){
					if(("0".equals(dataMap.get("status")) || Arrays.binarySearch(failResetEventArr, dataMap.get("status")) >= 0) &&
							("0".equals(dataMap.get("set_strategy")) || Arrays.binarySearch(eventArr, eventList.contains(dataMap.get("set_strategy"))) >= 0)){
						isSet = true;
					}
				}
			}

			// 设置节点
			if(isSet){
				int result = superPasswdNode(deviceId);
				superPasswdDAO.updateAllData(deviceId, Global.SUPERPASSWD_TABLENAME, result, System.currentTimeMillis()/1000);
				
				// 成功时更新数据库超级密码
				if(1 == result){
					superPasswdDAO.updatePwdByDeviceId(telecomPasswd, deviceId);
				}
			}else{
				loger.warn("定制任务[{}]此设备在有效期内已成功设置过节点值或者没开通失败重设功能或者不符合定制策略",deviceId);
				APPUtil.messageTran(message, "superPasswd",deviceId);
			}
			
			
		}else{
			loger.debug("定制任务不存在[{}]此设备",deviceId);
			APPUtil.messageTran(message, "superPasswd",deviceId);
		}
	}
	
	/**
	 * 全网任务情况
	 * @param datalist
	 */
	private void superPasswdAll(ArrayList<HashMap<String, String>> datalist,String deviceId){
		if(null != datalist && !datalist.isEmpty() && null != datalist.get(0)){
			boolean isSet = false;
			if(1 == Global.SUPERPASSWD_FAILENABLE){

				String[] failResetEventArr = Global.SUPERPASSWD_FAILRESETLIST.split(",");
				Arrays.sort(failResetEventArr);
				for(HashMap<String, String> dataMap : datalist){
					if(Arrays.binarySearch(failResetEventArr, dataMap.get("status")) >= 0){
						isSet = true;
					}
				}
			}
			// 设置节点
			if(isSet){
				int result = superPasswdNode(deviceId);
				superPasswdDAO.updateAllData(deviceId, Global.SUPERPASSWD_TABLENAME, result, System.currentTimeMillis()/1000);
				// 成功时更新数据库超级密码
				if(1 == result){
					superPasswdDAO.updatePwdByDeviceId(telecomPasswd, deviceId);
				}
			}else{
				loger.warn("全网任务[{}]此设备在有效期内已成功设置过节点值或者没开通失败重设功能",deviceId);
				APPUtil.messageTran(message, "superPasswd",deviceId);
			}
			
		}else{
			// 设置节点
			int result = superPasswdNode(deviceId);
			superPasswdDAO.addAllData(deviceId, Global.SUPERPASSWD_TABLENAME, result, System.currentTimeMillis()/1000);
			// 成功时更新数据库超级密码
			if(1 == result){
				superPasswdDAO.updatePwdByDeviceId(telecomPasswd, deviceId);
			}
		}
		
	}
	
	private int superPasswdNode(String deviceId){
		
		GetDeviceOnLineStatus getStatus = new GetDeviceOnLineStatus();
		ACSCorba acsCorba = new ACSCorba();
		int flag = getStatus.testDeviceOnLineStatus(deviceId, acsCorba);
		if (-6 == flag) {
			loger.warn("设备正在被操作，无法获取节点值，device_id={}", deviceId);
			return flag;
		}else if (1 == flag){
			loger.warn("[SuperPasswdThread][{}]设备在线，可以进行操作", deviceId);
			ParameValueOBJ pvObj = new ParameValueOBJ();
			pvObj.setName("InternetGatewayDevice.DeviceInfo.X_CT-COM_TeleComAccount.Password");
			pvObj.setType("1");
			telecomPasswd = "telecomadmin" + MathUtil.getRandom();
			pvObj.setValue(telecomPasswd);
			int result = acsCorba.setValue(deviceId, pvObj);
			loger.warn("设备设置结果为，device_id={},result={}", deviceId,result);
			return result;
		}else{
			loger.warn("设备离线，device_id={},flag={}", deviceId,flag);
			return flag;
		}
	}
}
