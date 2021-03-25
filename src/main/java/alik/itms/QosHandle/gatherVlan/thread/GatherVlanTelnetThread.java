package alik.itms.QosHandle.gatherVlan.thread;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import alik.itms.QosHandle.common.ACSCorba;
import alik.itms.QosHandle.common.GetDeviceOnLineStatus;
import alik.itms.QosHandle.common.Global;
import alik.itms.QosHandle.gatherVlan.dao.GatherVlanDAO;
import alik.itms.QosHandle.obj.ParameValueOBJ;

/**
 * 设置固定节点值逻辑
 * @author jiafh
 *
 */
public class GatherVlanTelnetThread implements Runnable{
	
	private Logger loger = LoggerFactory.getLogger(GatherVlanTelnetThread.class);
	
	private GatherVlanDAO gatherVlanDAO = new GatherVlanDAO();
	
	// 监听到的具体消息
	private String deviceId;
	
	public GatherVlanTelnetThread(String deviceId){
		this.deviceId = deviceId;
	}

	@Override
	public void run() {
		loger.debug("全网任务。。。");
		gatherTelnetAll(deviceId);
	}
	
	
	
	/**
	 * 全网任务情况
	 * @param datalist
	 */
	private void gatherTelnetAll(String deviceId){
		// 设置节点
		Map<String,Object> telnetNodeMap = gatherTelnetNode(deviceId);
		gatherVlanDAO.addTelnetAllData(deviceId, Global.GATHERTELNETFTP_TABLENAME, telnetNodeMap, System.currentTimeMillis()/1000);
	}
	
	
	
	private Map<String,Object> gatherTelnetNode(String deviceId){
		
		Map<String,Object> gatherResultMap = new HashMap<String, Object>();
		gatherResultMap.put("resutlCode", -999);
		gatherResultMap.put("desc", "采集失败，未知错误");
		
		GetDeviceOnLineStatus getStatus = new GetDeviceOnLineStatus();
		ACSCorba acsCorba = new ACSCorba();
		int flag = getStatus.testDeviceOnLineStatus(deviceId, acsCorba);
		flag = -6;
		if (-6 == flag) {
			loger.warn("设备正在被操作，无法获取节点值，device_id={}", deviceId);
			gatherResultMap.put("resutlCode", flag);
			gatherResultMap.put("desc", "设备正在被操作，无法获取节点值");
		}else if (1 == flag){
			loger.warn("[GatherVlanThread][{}]设备在线，可以进行操作", deviceId);
			String[] setPath = new String[2];
			setPath[0] = "InternetGatewayDevice.DeviceInfo.X_CT-COM_ServiceManage.TelnetEnable";
			setPath[1] = "InternetGatewayDevice.DeviceInfo.X_CT-COM_ServiceManage.FtpEnable";
				
			
			loger.warn("[{}]start to gather telnet、ftp path",deviceId);
			
			ArrayList<ParameValueOBJ> parameValueOBJList = acsCorba.getValue(deviceId, setPath);
			if(null != parameValueOBJList && !parameValueOBJList.isEmpty() && null != parameValueOBJList.get(0) && 2 == parameValueOBJList.size()){
				loger.warn(parameValueOBJList.get(0).getName());
				loger.warn(parameValueOBJList.get(1).getName());
				parameValueOBJList.get(0).getValue();
				parameValueOBJList.get(1).getValue();
				gatherResultMap.put("resutlCode", 1);
				gatherResultMap.put("desc", "采集成功");
				gatherResultMap.put(parameValueOBJList.get(0).getName().substring(parameValueOBJList.get(0).getName().lastIndexOf(".")+1), parameValueOBJList.get(0).getValue());
				gatherResultMap.put(parameValueOBJList.get(1).getName().substring(parameValueOBJList.get(1).getName().lastIndexOf(".")+1), parameValueOBJList.get(1).getValue());
				loger.warn("[GatherVlanThread] [{}]采集成功，TelnetEnable：[{}],FtpEnable:[{}]",
						deviceId, gatherResultMap.get("TelnetEnable"), gatherResultMap.get("FtpEnable"));
			}
		}else{
			loger.warn("设备离线，device_id={},flag={}", deviceId,flag);
			gatherResultMap.put("resutlCode", flag);
			gatherResultMap.put("desc", "设备不在线");
		}
		return gatherResultMap;
	}
}
