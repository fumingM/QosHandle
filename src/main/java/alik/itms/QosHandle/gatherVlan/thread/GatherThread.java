package alik.itms.QosHandle.gatherVlan.thread;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import alik.itms.QosHandle.common.ACSCorba;
import alik.itms.QosHandle.common.GetDeviceOnLineStatus;
import alik.itms.QosHandle.common.Global;
import alik.itms.QosHandle.gatherVlan.dao.GatherVlanDAO;
import alik.itms.QosHandle.obj.ParameValueOBJ;

import com.linkage.commons.util.StringUtil;

/**
 * 设置固定节点值逻辑
 * 
 *
 **/

public class GatherThread implements Runnable{
	
	private Logger loger = LoggerFactory.getLogger(GatherThread.class);
	
	private GatherVlanDAO gatherVlanDAO = new GatherVlanDAO();
	
	// 监听到的具体消息
	private String deviceId;
	
	public GatherThread(String deviceId){
		this.deviceId = deviceId;
	}

	@Override
	public void run() {
		if(Global.ftpMap.containsKey(deviceId)){
			gatherTelnetAll(deviceId);
		}
		
		if(Global.vlanMap.containsKey(deviceId)){
			gatherVlanAll(deviceId);
		}
	}
	
	
	/**
	 * 全网任务情况
	 * @param datalist
	 */
	private void gatherVlanAll(String deviceId){
		// 设置节点
		Map<String,Object> vLanNodeMap = gatherVLanNode(deviceId);
		gatherVlanDAO.addAllData(deviceId, Global.GATHERVLAN_TABLENAME, vLanNodeMap, System.currentTimeMillis()/1000);
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
	
	
	
	
	private Map<String,Object> gatherVLanNode(String deviceId){
		
		Map<String,Object> gatherResultMap = new HashMap<String, Object>();
		gatherResultMap.put("resutlCode", -999);
		gatherResultMap.put("desc", "采集失败，未知错误");
		
		GetDeviceOnLineStatus getStatus = new GetDeviceOnLineStatus();
		ACSCorba acsCorba = new ACSCorba();
		int flag = getStatus.testDeviceOnLineStatus(deviceId, acsCorba);
		if (-6 == flag) {
			loger.warn("设备正在被操作，无法获取节点值，device_id={}", deviceId);
			gatherResultMap.put("resutlCode", flag);
			gatherResultMap.put("desc", "设备正在被操作，无法获取节点值");
		}else if (1 == flag){
			loger.warn("[GatherThread][{}]设备在线，可以进行操作", deviceId);
			String[] setPath = new String[2];
			setPath[0] = "InternetGatewayDevice.Services.X_CT-COM_IPTV.SnoopingEnable";
			
			String wanConnPath = "InternetGatewayDevice.WANDevice.1.WANConnectionDevice.";
			String wanServiceList = ".X_CT-COM_ServiceList";
			String wanPPPConnection = ".WANPPPConnection.";
			String wanIPConnection = ".WANIPConnection.";
			String vlanIdPath = "";
			String setMulticastVlanPath = "";
			
			String accessType = gatherVlanDAO.getAccessType(deviceId);
			// 以下获取的方式是NX专用
			if (null == accessType)
			{
				String[] gatherPath = new String[]{"InternetGatewayDevice.WANDevice.1.WANCommonInterfaceConfig.WANAccessType"};
				ArrayList<ParameValueOBJ> objLlist = acsCorba.getValue(deviceId, gatherPath);
				
				if(objLlist!=null && objLlist.size()!=0){
					for(ParameValueOBJ pvobj : objLlist){
						if(pvobj.getName().endsWith("WANAccessType")){
							accessType = pvobj.getValue();
						}
					}
				}
				loger.warn("[{}]采集到的，accessType为：[{}]", deviceId,accessType);
			}
			
			if("EPON".equals(accessType)){
				vlanIdPath = ".X_CT-COM_WANEponLinkConfig.VLANIDMark";
			}else if("GPON".equals(accessType)){
				vlanIdPath = ".X_CT-COM_WANGponLinkConfig.VLANIDMark";
			}else{
				loger.warn("accessType既不是EPON也不是GPON");
				gatherResultMap.put("resutlCode", -7);
				gatherResultMap.put("desc", "accessType既不是EPON也不是GPON");
				return gatherResultMap;
			}
			
			
			//不支持快速采集，获取全部路径
			ArrayList<String> wanConnPathsList = new ArrayList<String>();
			
			try{
				loger.warn("[{}]先尝试一次采集全部路径", deviceId);
				wanConnPathsList = acsCorba.getParamNamesPath(deviceId, wanConnPath, 0);
				loger.warn("wanConnPathsList.size:{}",wanConnPathsList.size());
				if (wanConnPathsList == null || wanConnPathsList.size() == 0
						|| wanConnPathsList.isEmpty()) {
					return null;
				}
				else{
					ArrayList<String> paramNameList = new ArrayList<String>();
					for (int i = 0; i < wanConnPathsList.size(); i++) {
						String namepath = wanConnPathsList.get(i);
						if (namepath.indexOf(".X_CT-COM_ServiceList") >= 0) {
							paramNameList.add(namepath);
						}
					}
					wanConnPathsList = new ArrayList<String>();
					wanConnPathsList.addAll(paramNameList);
				}
				
				if(wanConnPathsList.size()==0){
					loger.warn("[GatherThread] [{}]无X_CT-COM_ServiceList节点：", deviceId);
				}
				
				String[] paramNametemp = new String[wanConnPathsList.size()];
				for(int i=0;i<wanConnPathsList.size();i++){
					paramNametemp[i] = wanConnPathsList.get(i);
				}
				
				Map<String, String> paramValueMap = acsCorba.getParaValueMap(deviceId,
						paramNametemp);
				
				if (paramValueMap.isEmpty()) {
					loger.warn("[GatherThread] [{}]获取ServiceList失败",
							deviceId);
				}
				for (Map.Entry<String, String> entry : paramValueMap.entrySet()) {
					loger.debug("[{}]{}={} ",
							new Object[]{deviceId, entry.getKey(), entry.getValue()});
					//InternetGatewayDevice.WANDevice.1.WANConnectionDevice.2.WANPPPConnection.3.X_CT-COM_ServiceList
					if (entry.getValue().indexOf("OTHER") >= 0 || entry.getValue().indexOf("IPTV") >= 0) {
						String a = entry.getKey().split("\\.")[4];
						String b = entry.getKey().split("\\.")[6];
						int index = entry.getKey().indexOf(b+".X_CT-COM_ServiceList");
						String servListPathJ = entry.getKey().substring(0, index);
						
						// 采集 X_CT-COM_MulticastVlan
						setMulticastVlanPath = servListPathJ + b + ".X_CT-COM_MulticastVlan";
						setPath[1] = setMulticastVlanPath;
						loger.warn("[{}]获取OTHER成功, setMulticastVlanPath={}", deviceId, setMulticastVlanPath);
					}
				}
			}
			catch (Exception e) {
				loger.warn("[{}]一次采集全部路径失败，开始逐层采集", deviceId);
				// 默认“InternetGatewayDevice.WANDevice.”下只有实例“1”
				wanConnPathsList = acsCorba.getParamNamesPath(deviceId, wanConnPath, 0);
				if (wanConnPathsList == null || wanConnPathsList.size() == 0 || wanConnPathsList.isEmpty())
				{
					loger.warn("[{}] [{}]获取WANConnectionDevice下所有节点路径失败，逐层获取",deviceId);
					wanConnPathsList = new ArrayList<String>();
					List<String> jList = acsCorba.getIList(deviceId, wanConnPath);
					if (null == jList || jList.size() == 0 || jList.isEmpty())
					{
						loger.warn("[GatherThread] [{}]获取" + wanConnPath + "下实例号失败，返回", deviceId);
						gatherResultMap.put("resutlCode", -8);
						gatherResultMap.put("desc", "获取" + wanConnPath + "下实例号失败");
						return gatherResultMap;
					}else{
						for (String j : jList){
							// 获取session，
							List<String> kPPPList = acsCorba.getIList(deviceId, wanConnPath + j + wanPPPConnection);
							if (null == kPPPList || kPPPList.size() == 0 || kPPPList.isEmpty())
							{
								loger.warn("[GatherThread] [{}]获取" + wanConnPath
										+ wanConnPath + j + wanPPPConnection + "下实例号失败", deviceId);
								kPPPList = acsCorba.getIList(deviceId, wanConnPath + j
										+ wanIPConnection);
								if (null == kPPPList || kPPPList.size() == 0 || kPPPList.isEmpty())
								{
									loger.warn("[GatherThread] [{}]获取" + wanConnPath
											+ wanConnPath + j + wanIPConnection + "下实例号失败", deviceId);
									gatherResultMap.put("resutlCode", -9);
									gatherResultMap.put("desc", "获取" + wanConnPath + wanConnPath + j + wanIPConnection + "下实例号失败");
									return gatherResultMap;
								}else{
									for (String kppp : kPPPList)
									{
										wanConnPathsList.add(wanConnPath + j + wanIPConnection + kppp
												+ wanServiceList);
									}
								}
							}
							else
							{
								for (String kppp : kPPPList)
								{
									wanConnPathsList.add(wanConnPath + j + wanPPPConnection + kppp
											+ wanServiceList);
								}
							}
						}
					}
				}
					
				// serviceList节点
				ArrayList<String> serviceListList = new ArrayList<String>();
				// 所有需要采集的节点
				ArrayList<String> paramNameList = new ArrayList<String>();
				for (int i = 0; i < wanConnPathsList.size(); i++)
				{
					String namepath = wanConnPathsList.get(i);
					if (namepath.indexOf(vlanIdPath) >= 0 || namepath.indexOf(wanServiceList) >= 0)
					{
						serviceListList.add(namepath);
						paramNameList.add(namepath);
						continue;
					}
				}
				if (serviceListList.size() == 0 || serviceListList.isEmpty())
				{
					loger.warn("[GatherThread] [{}]不存在WANIP下的X_CT-COM_ServiceList节点，返回", deviceId);
					gatherResultMap.put("resutlCode", -10);
					gatherResultMap.put("desc", "不存在WANIP下的X_CT-COM_ServiceList节点");
					return gatherResultMap;
				}else{
					String[] paramNameArr = new String[paramNameList.size()];
					int arri = 0;
					for (String paramName : paramNameList)
					{
						paramNameArr[arri] = paramName;
						arri = arri + 1;
					}
					Map<String, String> paramValueMap = new HashMap<String, String>();
					for (int k = 0; k < (paramNameArr.length / 20) + 1; k++)
					{
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
						loger.warn("[GatherThread] [{}]获取ServiceList失败", deviceId);
						gatherResultMap.put("resutlCode", -11);
						gatherResultMap.put("desc", "获取ServiceList失败");
						return gatherResultMap;
					}
					
					for (Map.Entry<String, String> entry : paramValueMap.entrySet())
					{
						loger.debug("[{}]{}={} ", new Object[] { deviceId, entry.getKey(),entry.getValue() });
						String paramName = entry.getKey();
						String j = paramName.substring(wanConnPath.length(), paramName.indexOf(".",wanConnPath.length()));
						if (paramName.indexOf(wanServiceList) >= 0)
						{
							String k = paramName.substring(paramName.indexOf(wanServiceList) - 1,
									paramName.indexOf(wanServiceList));
							if (!StringUtil.IsEmpty(entry.getValue()) && entry.getValue().equalsIgnoreCase("other")){
								//X_CT-COM_ServiceList的值为INTERNET的时候，此节点路径即为要删除的路径
								setMulticastVlanPath = wanConnPath + j + wanPPPConnection + k + ".X_CT-COM_MulticastVlan";
								setPath[1] = setMulticastVlanPath;
							}
						}
					}
				}
			}
			
			if(setMulticastVlanPath != null && !"".equals(setMulticastVlanPath)){
				loger.warn("[{}]start to gather path[{}]",deviceId,setMulticastVlanPath);
				
				ArrayList<ParameValueOBJ> parameValueOBJList = acsCorba.getValue(deviceId, setPath);
				if(null != parameValueOBJList && !parameValueOBJList.isEmpty() && null != parameValueOBJList.get(0) && 2 == parameValueOBJList.size()){
					parameValueOBJList.get(0).getValue();
					parameValueOBJList.get(1).getValue();
					gatherResultMap.put("resutlCode", 1);
					gatherResultMap.put("desc", "采集成功");
					gatherResultMap.put(parameValueOBJList.get(0).getName().substring(parameValueOBJList.get(0).getName().lastIndexOf(".")+1), parameValueOBJList.get(0).getValue());
					gatherResultMap.put(parameValueOBJList.get(1).getName().substring(parameValueOBJList.get(1).getName().lastIndexOf(".")+1), parameValueOBJList.get(1).getValue());
					loger.warn("[GatherThread] [{}]采集成功，snoopingEnable：[{}],multicastVlan:[{}]",
							deviceId,gatherResultMap.get("SnoopingEnable"), gatherResultMap.get("X_CT-COM_MulticastVlan"));
				}

			}
		}else{
			loger.warn("设备离线，device_id={},flag={}", deviceId,flag);
			gatherResultMap.put("resutlCode", flag);
			gatherResultMap.put("desc", "设备不在线");
		}
		return gatherResultMap;
	}
	
	
	private Map<String,Object> gatherTelnetNode(String deviceId){
		
		Map<String,Object> gatherResultMap = new HashMap<String, Object>();
		gatherResultMap.put("resutlCode", -999);
		gatherResultMap.put("desc", "采集失败，未知错误");
		
		GetDeviceOnLineStatus getStatus = new GetDeviceOnLineStatus();
		ACSCorba acsCorba = new ACSCorba();
		int flag = getStatus.testDeviceOnLineStatus(deviceId, acsCorba);
		if (-6 == flag) {
			loger.warn("设备正在被操作，无法获取节点值，device_id={}", deviceId);
			gatherResultMap.put("resutlCode", flag);
			gatherResultMap.put("desc", "设备正在被操作，无法获取节点值");
		}else if (1 == flag){
			loger.warn("[GatherThread][{}]设备在线，可以进行操作", deviceId);
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
				loger.warn("[GatherThread] [{}]采集成功，TelnetEnable：[{}],FtpEnable:[{}]",
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
