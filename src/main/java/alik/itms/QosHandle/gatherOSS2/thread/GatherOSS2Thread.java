package alik.itms.QosHandle.gatherOSS2.thread;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import alik.itms.QosHandle.common.ACSCorba;
import alik.itms.QosHandle.common.GetDeviceOnLineStatus;
import alik.itms.QosHandle.common.Global;
import alik.itms.QosHandle.gatherOSS2.bio.GatherOSS2MessageHandle;
import alik.itms.QosHandle.gatherOSS2.dao.GatherOSS2DAO;

import com.linkage.commons.util.DateTimeUtil;
import com.linkage.commons.util.StringUtil;
import com.linkage.commons.xml.XML;

/**
 * 设置固定节点值逻辑
 * @author jiafh
 *
 */
public class GatherOSS2Thread implements Runnable{
	
	private Logger loger = LoggerFactory.getLogger(GatherOSS2Thread.class);
	
	private GatherOSS2DAO gatherOss2DAO = new GatherOSS2DAO();
	
	private HashMap<String,String> pathAll = new HashMap<String,String>();
	
	private HashMap<String,String> pathBase = new HashMap<String,String>();
	
	private ArrayList<String> pathWan = new ArrayList<String>();
	
	private ArrayList<String> pathLan = new ArrayList<String>();
	
	private String WANAccessType = "GPON";
	
	private String TXPower = "InternetGatewayDevice.WANDevice.1.X_CU_WANGPONInterfaceConfig.OpticalTransceiver.TXPower";
	
	private String RXPower = "InternetGatewayDevice.WANDevice.1.X_CU_WANGPONInterfaceConfig.OpticalTransceiver.RXPower";
	
	private String sn = "";
	
	private String oui = "";
	
	private String result = "";
	// 监听到的具体消息
	private String message;
	
	public GatherOSS2Thread(String message){
		this.message = message;
	}

	@Override
	public void run() {
		String deviceId = "";
		try{
			// 解析xml
			XML xml = new XML(message,"String");
			deviceId = ((Element) xml.getElement("devList").getChildren("dev").get(0)).getChildText("devId");
			
			GatherOSS2MessageHandle.chkCount(deviceId, "add");
			
			//查询设备在7天内是否已经采集过
			long gathertime = gatherOss2DAO.getGatherOSS2Data(deviceId, Global.GATHEROSS2_TABLENAME);
			long currtime = new DateTimeUtil().getLongTime();
			loger.warn("currtime="+currtime+" ,gathertime="+gathertime+", Global.GATHEROSS2_EXPIRYDATE="+Global.GATHEROSS2_EXPIRYDATE);
			if( ((currtime - gathertime) < Global.GATHEROSS2_EXPIRYDATE * 86400) && gathertime!=0){
				loger.warn("[{}]在{}天内已经采集过, 跳过", deviceId, Global.GATHEROSS2_EXPIRYDATE);
				GatherOSS2MessageHandle.messageTran(message,"gatherOSS2",deviceId);
			}
			else{
				boolean dbresult = gatherOss2DAO.deleteAll(deviceId);
				loger.warn("[{}]清空所有采集表，result={}", deviceId, dbresult);
				
				//预读
				dealPath(deviceId);
				
				if("0".equals(result) && (null != pathAll && !pathAll.isEmpty())){
					loger.warn("[{}]预读成功, 准备采集", deviceId);
					gatherAll(deviceId, currtime);
				}
				else{
					loger.warn("[{}]预读失败, 结束", deviceId);
				}
				
				int dbres = gatherOss2DAO.insertRecord(deviceId, result);
				loger.warn("[{}]插入record表完成，result={}", deviceId,dbres);
			}
		}
		catch (Exception e) {
			result = "-1";
			loger.warn("[{}]出现异常", deviceId);
			e.printStackTrace();
		}
		finally{
			//count-1
			GatherOSS2MessageHandle.chkCount(deviceId, "del");
		}
	}
	
	/**
	 * 处理预读、整合path
	 * @param datalist
	 * @param deviceId
	 * @return result 1成功 -2不在线 -1采集故障
	 */
	private void dealPath(String deviceId){
		pathBase.put("InternetGatewayDevice.DeviceInfo.ManufacturerOUI", "");
		pathBase.put("InternetGatewayDevice.DeviceInfo.SerialNumber", "");
		pathBase.put("InternetGatewayDevice.DeviceInfo.ModelName", "");
		pathBase.put("InternetGatewayDevice.X_CU_UserInfo.UserName", "");
		pathBase.put("InternetGatewayDevice.Services.X_CU_User.1.NumberOfSubuser", "");
		pathBase.put("InternetGatewayDevice.DeviceInfo.Description", "");
		pathBase.put("InternetGatewayDevice.DeviceInfo.ProductClass", "");
		pathBase.put("InternetGatewayDevice.DeviceInfo.Manufacturer", "");
		pathBase.put("InternetGatewayDevice.DeviceInfo.HardwareVersion", "");
		pathBase.put("InternetGatewayDevice.DeviceInfo.SoftwareVersion", "");
		pathBase.put("InternetGatewayDevice.WANDevice.1.WANCommonInterfaceConfig.WANAccessType", "");
		pathBase.put("InternetGatewayDevice.WANDeviceNumberOfEntries", "");
		pathBase.put("InternetGatewayDevice.LANDeviceNumberOfEntries", "");
		pathBase.put("InternetGatewayDevice.X_CU_POTSDeviceNumber", "");
		pathBase.put("InternetGatewayDevice.LANDevice.1.X_CU_WLANEnable", "");
		pathBase.put("InternetGatewayDevice.DeviceInfo.X_CU_IPProtocolVersion", "");
		pathBase.put("InternetGatewayDevice.LANDevice.1.WLANConfiguration.1.X_CU_Band", "");
		//pathBase.put("InternetGatewayDevice.X_CU_Function.HttpSpeedTest.TestURL", "");
		//pathBase.put("InternetGatewayDevice.X_CU_Function.RMS_SpeedTest.testURL", "");
		//pathBase.put("InternetGatewayDevice.DeviceInfo.X_CU_OS", "");
		
		pathBase.put("InternetGatewayDevice.DeviceInfo.UpTime", "");
		
		//pathBase.put("InternetGatewayDevice.WANDevice.1.X_CU_WANGPONInterfaceConfig.OpticalTransceiver.TXPower", "");
		//pathBase.put("InternetGatewayDevice.WANDevice.1.X_CU_WANGPONInterfaceConfig.OpticalTransceiver.RXPower", "");
		pathAll.putAll(pathBase);
		
		GetDeviceOnLineStatus getStatus = new GetDeviceOnLineStatus();
		ACSCorba acsCorba = new ACSCorba();
		int flag = getStatus.testDeviceOnLineStatus(deviceId, acsCorba);
		if (-6 == flag) {
			loger.warn("设备正在被操作，无法获取节点值，device_id={}", deviceId);
			result = "-6";
			return;
		}
		else if (1 == flag){
			loger.warn("[{}]设备在线，准备进行WANDevice相关节点预读", deviceId);
			
			ArrayList<String> wanConnPathsList = null;
			// 默认“InternetGatewayDevice.WANDevice.”下只有实例“1”
			wanConnPathsList = acsCorba.getParamNamesPath(deviceId, "InternetGatewayDevice.WANDevice.1.WANConnectionDevice.", 0);
			if (wanConnPathsList == null || wanConnPathsList.size() == 0 || wanConnPathsList.isEmpty())
			{
				loger.warn("[{}]WANDevice相关节点预读失败",deviceId);
				result = "-1";
				return ;
			}
				
			loger.warn("[{}]WANDevice相关节点预读", deviceId);
			for (int i = 0; i < wanConnPathsList.size(); i++)
			{
				String namepath = wanConnPathsList.get(i);
				if (namepath.endsWith(".X_CU_ServiceList")  || namepath.endsWith(".ConnectionType")  ||namepath.endsWith(".X_CU_IPMode")  ||
						namepath.endsWith(".X_CU_IPv6IPAddressOrigin")  ||namepath.endsWith(".WANIPConnectionNumberOfEntries")  ||namepath.endsWith(".EthernetBytesSent")  ||
						namepath.endsWith(".EthernetBytesReceived")  ||namepath.endsWith(".EthernetPacketsSent")  ||namepath.endsWith(".EthernetPacketsReceived")  ||namepath.endsWith(".WANPPPConnectionNumberOfEntries") ){
					pathWan.add(namepath);
					pathAll.put(namepath,"");
					//loger.info("[{}]WANDevice相关节点:{}",deviceId,namepath);
					continue;
				}
			}
			
			loger.warn("[{}]LANDevice相关节点预读", deviceId);
			wanConnPathsList = acsCorba.getParamNamesPath(deviceId, "InternetGatewayDevice.LANDevice.", 0);
			if (wanConnPathsList == null || wanConnPathsList.size() == 0 || wanConnPathsList.isEmpty())
			{
				loger.warn("[{}]LANDevice相关节点预读失败",deviceId);
				result = "-1";
				return ;
			}
			
			for (int i = 0; i < wanConnPathsList.size(); i++)
			{
				String namepath = wanConnPathsList.get(i);
				if(namepath.indexOf(".LANEthernetInterfaceConfig.") >=0){
					if (namepath.endsWith(".Status")  || namepath.endsWith(".MACAddress")  ||namepath.endsWith(".MaxBitRate")  ||
							namepath.endsWith(".X_CU_AdaptRate")  ||namepath.endsWith(".DuplexMode")  ||namepath.endsWith(".Stats.BytesSent")  ||
							namepath.endsWith(".Stats.BytesReceived")  ||namepath.endsWith(".Stats.PacketsSent")  ||namepath.endsWith(".Stats.PacketsReceived") ){
						pathLan.add(namepath);
						pathAll.put(namepath,"");
						//loger.warn("[{}]LANDevice相关节点:{}",deviceId,namepath);
						//loger.warn("[{}]pathAll size :{}",deviceId,pathAll.size());
						continue;
					}
				}
				else if(namepath.endsWith(".LANHostConfigManagement.DHCPServerEnable")){
					pathLan.add(namepath);
					pathAll.put(namepath,"");
					//loger.warn("[{}]LANDevice相关节点:{}",deviceId,namepath);
					//loger.warn("[{}]pathAll size :{}",deviceId,pathAll.size());
					continue;
				}
				else if(namepath.indexOf(".Hosts.Host.") >=0){
					if (namepath.endsWith(".InterfaceType")|| namepath.endsWith(".HostNumberOfEntries")||namepath.endsWith(".X_CU_Hosttype")||
							namepath.endsWith(".IPAddress")|| namepath.endsWith(".MACAddress")){
						pathLan.add(namepath);
						pathAll.put(namepath,"");
						//loger.warn("[{}]LANDevice相关节点:{}",deviceId,namepath);
						//loger.warn("[{}]pathAll size :{}",deviceId,pathAll.size());
						continue;
					}
				}
				else if(namepath.indexOf(".WLANConfiguration.") >=0){
					if(namepath.indexOf(".AssociatedDevice.") >=0){
						if(namepath.endsWith(".AssociatedDeviceMACAddress") ||namepath.endsWith(".AssociatedDeviceIPAddress")){
							pathLan.add(namepath);
							pathAll.put(namepath,"");
							//loger.warn("[{}]LANDevice相关节点:{}",deviceId,namepath);
							//loger.warn("[{}]pathAll size :{}",deviceId,pathAll.size());
							continue;
						}
					}
					else{
						if (namepath.endsWith(".X_CU_Dual_bandsupport")  || namepath.endsWith(".Status")  ||namepath.endsWith(".X_CU_Band")  ||
								namepath.endsWith(".Channel")  ||namepath.endsWith(".Standard") ||namepath.endsWith(".MaxBitRate")  ||
								namepath.endsWith(".WPAAuthenticationMode")  ||namepath.endsWith(".WMMSupported") ||namepath.endsWith(".WMMEnable")  ||
								namepath.endsWith(".TotalBytesSent")  ||namepath.endsWith(".TotalBytesReceived") ||namepath.endsWith(".TotalPacketsSent") 
								||namepath.endsWith(".TotalPacketsReceived") ||namepath.endsWith(".TransmitPower") ){
							pathLan.add(namepath);
							pathAll.put(namepath,"");
							//loger.warn("[{}]LANDevice相关节点:{}",deviceId,namepath);
							//loger.warn("[{}]pathAll size :{}",deviceId,pathAll.size());
							continue;
						}
					}
				}
			}
			result = "0";
			return;
		}
		else{
			loger.warn("设备离线，device_id={},flag={}", deviceId,flag);
			result = "-2";
			return;
		}
		
	}
	
	public static void main(String[] args)
	{
		String a = "InternetGatewayDevice.LANDevice.1.WLANConfiguration.4.TransmitPower";
		System.out.println(a.endsWith(".TransmitPower"));
		
	}
	
	/**
	 * 采集
	 * @param deviceId
	 */
	private void gatherAll(String deviceId, long gathertime){
		ACSCorba acsCorba = new ACSCorba();
		
		String[] paramNameArr = new String[pathAll.size()];
		int arri = 0;

		loger.warn("paramNameArr.size="+paramNameArr.length);
		for(Map.Entry<String, String> entry : pathAll.entrySet()){
			paramNameArr[arri] = entry.getKey();
			arri = arri + 1;
		}
		
		Map<String, String> paramValueMap = new HashMap<String, String>();
		for (int k = 0; k < (paramNameArr.length / 20) + 1; k++)
		{
			loger.warn("[{}]开始第{}次采集", deviceId, (k+1));
			String[] paramNametemp = new String[paramNameArr.length - (k * 20) > 20 ? 20
					: paramNameArr.length - (k * 20)];
			for (int m = 0; m < paramNametemp.length; m++)
			{
				paramNametemp[m] = paramNameArr[k * 20 + m];
			}
			Map<String, String> maptemp = acsCorba.getParaValueMap(deviceId,
					paramNametemp);
			if (maptemp != null && !maptemp.isEmpty())
			{
				paramValueMap.putAll(maptemp);
			}
		}
		if (paramValueMap.isEmpty())
		{
			loger.warn("[{}]获取参数值失败", deviceId);
			result = "-1";
			return ;
		}
		loger.warn("[{}]采集全部完成", deviceId);
		
		//采集HttpSpeedTest和RMS_SpeedTest，这两个不是叶子节点，而且可能不存在。所以进行单独采集
		Map<String, String> maptemp = acsCorba.getParaValueMap(deviceId,
				new String[]{"InternetGatewayDevice.X_CU_Function.HttpSpeedTest.TestURL"});
		if (maptemp != null && !maptemp.isEmpty()){
			pathBase.put("InternetGatewayDevice.X_CU_Function.HttpSpeedTest", "1");
			paramValueMap.put("InternetGatewayDevice.X_CU_Function.HttpSpeedTest", "1");
		}
		else{
			pathBase.put("InternetGatewayDevice.X_CU_Function.HttpSpeedTest", "0");
			paramValueMap.put("InternetGatewayDevice.X_CU_Function.HttpSpeedTest", "0");
		}
		
		maptemp = acsCorba.getParaValueMap(deviceId,
				new String[]{"InternetGatewayDevice.X_CU_Function.RMS_SpeedTest.testURL"});
		if (maptemp != null && !maptemp.isEmpty()){
			pathBase.put("InternetGatewayDevice.X_CU_Function.RMS_SpeedTest", "1");
			paramValueMap.put("InternetGatewayDevice.X_CU_Function.RMS_SpeedTest", "1");
		}
		else{
			pathBase.put("InternetGatewayDevice.X_CU_Function.RMS_SpeedTest", "0");
			paramValueMap.put("InternetGatewayDevice.X_CU_Function.RMS_SpeedTest", "0");
		}
		
		loger.warn("[{}]HttpSpeedTest[{}]、RMS_SpeedTest[{}]采集完成", deviceId,
				paramValueMap.get("InternetGatewayDevice.X_CU_Function.HttpSpeedTest"),
				paramValueMap.get("InternetGatewayDevice.X_CU_Function.RMS_SpeedTest"));
		
		maptemp = acsCorba.getParaValueMap(deviceId,
				new String[]{"InternetGatewayDevice.DeviceInfo.X_CU_OS"});
		if (maptemp != null && !maptemp.isEmpty()){
			pathBase.putAll(maptemp);
			paramValueMap.putAll(maptemp);
		}
		else{
			pathBase.put("InternetGatewayDevice.DeviceInfo.X_CU_OS", "");
			paramValueMap.put("InternetGatewayDevice.DeviceInfo.X_CU_OS", "");
		}
		loger.warn("[{}]X_CU_OS[{}]采集完成", deviceId, StringUtil.getStringValue(paramValueMap, "InternetGatewayDevice.DeviceInfo.X_CU_OS", ""));
		
		//光功率采集（不确定是GPON还是EPON，所以需要单独采集）
		WANAccessType = StringUtil.getStringValue(paramValueMap, "InternetGatewayDevice.WANDevice.1.WANCommonInterfaceConfig.WANAccessType", "GPON");
		if(WANAccessType.indexOf("GPON")==-1){
			TXPower = TXPower.replace("GPON", "EPON");
			RXPower = RXPower.replace("GPON", "EPON");
		}
		maptemp = acsCorba.getParaValueMap(deviceId,
				new String[]{TXPower, RXPower});
		if (maptemp != null && !maptemp.isEmpty()){
			pathBase.put(TXPower, StringUtil.getStringValue(paramValueMap, TXPower, ""));
			pathBase.put(RXPower, StringUtil.getStringValue(paramValueMap, RXPower, ""));
			paramValueMap.putAll(maptemp);
			loger.warn("[{}]采集光功率{}成功", deviceId, WANAccessType);
		}
		else{
			loger.warn("[{}]采集光功率{}失败", deviceId, WANAccessType);
		}
		
		for (Map.Entry<String, String> entry : paramValueMap.entrySet()){
			loger.info("[{}]{}={} ", new Object[] { deviceId, entry.getKey(),entry.getValue() });
			sn = paramValueMap.get("InternetGatewayDevice.DeviceInfo.SerialNumber");
			oui = paramValueMap.get("InternetGatewayDevice.DeviceInfo.ManufacturerOUI");
		}
		
		//处理基础信息
		int dbres = gatherOss2DAO.inserBase(deviceId, pathBase, gathertime, paramValueMap);
		loger.warn("[{}]插入base表完成，result={}", deviceId,dbres);
		
		dbres = gatherOss2DAO.inserLan(deviceId,oui,sn, gathertime, paramValueMap);
		loger.warn("[{}]插入lan表完成，result={}", deviceId,dbres);
		
		dbres = gatherOss2DAO.inserLanconf(deviceId,oui,sn, gathertime, paramValueMap);
		loger.warn("[{}]插入lanconf表完成，result={}", deviceId,dbres);
		
		dbres = gatherOss2DAO.inserLanhost(deviceId,oui,sn, gathertime, paramValueMap);
		loger.warn("[{}]插入lanhost表完成，result={}", deviceId,dbres);
		
		dbres = gatherOss2DAO.inserWanip(deviceId,oui,sn, gathertime, paramValueMap);
		loger.warn("[{}]插入wanip表完成，result={}", deviceId,dbres);
		
		dbres = gatherOss2DAO.inserWanppp(deviceId,oui,sn, gathertime, paramValueMap);
		loger.warn("[{}]插入wanppp表完成，result={}", deviceId,dbres);
		
		dbres = gatherOss2DAO.inserWlan(deviceId,oui,sn, gathertime, paramValueMap);
		loger.warn("[{}]插入wlan表完成，result={}", deviceId,dbres);
		
		dbres = gatherOss2DAO.inserWlanhost(deviceId,oui,sn, gathertime, paramValueMap);
		loger.warn("[{}]插入wlanhost表完成，result={}", deviceId,dbres);
		
		result = "1";
			
		return ;
	}
}
