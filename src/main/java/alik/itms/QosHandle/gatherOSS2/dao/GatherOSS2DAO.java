package alik.itms.QosHandle.gatherOSS2.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.linkage.commons.db.DBOperation;
import com.linkage.commons.db.PrepareSQL;
import com.linkage.commons.util.DateTimeUtil;
import com.linkage.commons.util.StringUtil;

public class GatherOSS2DAO {
	
	private Logger loger = LoggerFactory.getLogger(GatherOSS2DAO.class);
	
	/**
	 * 删除定制任务的过期任务
	 * @param addTime
	 * @return
	 */
	public int deleteOldData(long addTime,String tableName){
		
		PrepareSQL psql = new PrepareSQL("delete from " + tableName + " where addtime < ?");
		psql.setLong(1, addTime);
		return DBOperation.executeUpdate(psql.getSQL());
	}
	
	/**
	 * 查询设备是否已经采集过
	 * @param deviceId
	 * @param tableName
	 * @return
	 */
	public Long getGatherOSS2Data(String deviceId,String tableName){
		PrepareSQL psql = new PrepareSQL("select * from " + tableName + " where device_id = ? and result='1'");
		psql.setString(1, deviceId);
		Map<String, String> map = DBOperation.getRecord(psql.getSQL());
		return StringUtil.getLongValue(StringUtil.getStringValue(map, "gather_time", "0"));
	}
	
	/**
	 * 更新数据库表
	 * @param deviceId
	 * @param tableName
	 * @param status
	 * @param time
	 * @return
	 */
	public int updateAllData(String deviceId,String tableName,Map<String,Object> vLanNodeMap,long time){
		PrepareSQL psql = new PrepareSQL("update " + tableName + " set status=?,gathertime=?,snooping_enable=?,multicast_vlan=?,result_desc=?  where device_id = ?");
		psql.setInt(1, StringUtil.getIntegerValue(vLanNodeMap.get("resutlCode")));
		psql.setLong(2, time);
		psql.setInt(3, StringUtil.getIntegerValue(vLanNodeMap.get("SnoopingEnable")));
		psql.setString(4, StringUtil.getStringValue(vLanNodeMap.get("X_CT-COM_MulticastVlan")));
		psql.setString(5, StringUtil.getStringValue(vLanNodeMap.get("result_desc")));
		psql.setString(6, deviceId);
		return DBOperation.executeUpdate(psql.getSQL());
	}
	
	/**
	 * 更新数据库表(Telnet、ftp)
	 * @param deviceId
	 * @param tableName
	 * @param status
	 * @param time
	 * @return
	 */
	public int updateTelnetAllData(String deviceId,String tableName,Map<String,Object> vLanNodeMap,long time){
		PrepareSQL psql = new PrepareSQL("update " + tableName + " set status=?,gathertime=?,TelnetEnable=?,FtpEnable=?,result_desc=?  where device_id = ?");
		psql.setInt(1, StringUtil.getIntegerValue(vLanNodeMap.get("resutlCode")));
		psql.setLong(2, time);
		psql.setString(3, StringUtil.getStringValue(vLanNodeMap.get("TelnetEnable")));
		psql.setString(4, StringUtil.getStringValue(vLanNodeMap.get("FtpEnable")));
		psql.setString(5, StringUtil.getStringValue(vLanNodeMap.get("result_desc")));
		psql.setString(6, deviceId);
		return DBOperation.executeUpdate(psql.getSQL());
	}
	
	/**
	 * 全网业务新增数据库
	 * @param deviceId
	 * @param tableName
	 * @param status
	 * @param time
	 * @return
	 */
	public int addAllData(String deviceId,String tableName,Map<String,Object> vLanNodeMap,long time){
		PrepareSQL psql = new PrepareSQL("insert into " + tableName + " (device_id,status,gathertime,addtime,snooping_enable,multicast_vlan,result_desc) values(?,?,?,?,?,?,?)");
		psql.setString(1, deviceId);
		psql.setInt(2, StringUtil.getIntegerValue(vLanNodeMap.get("resutlCode")));
		psql.setLong(3, time);
		psql.setLong(4, time);
		psql.setInt(5, StringUtil.getIntegerValue(vLanNodeMap.get("SnoopingEnable")));
		psql.setString(6, StringUtil.getStringValue(vLanNodeMap.get("X_CT-COM_MulticastVlan")));
		psql.setString(7, StringUtil.getStringValue(vLanNodeMap.get("desc")));
		return DBOperation.executeUpdate(psql.getSQL());
	}
	
	
	/**
	 * 清空所有表
	 * @param deviceId
	 * @param tableName
	 * @param vLanNodeMap
	 * @param time
	 * @return
	 */
	public boolean deleteAll(String deviceId){
		ArrayList<String> list = new ArrayList<String>();
		StringBuilder sql0 = new StringBuilder();
		StringBuilder sql1 = new StringBuilder();
		StringBuilder sql2 = new StringBuilder();
		StringBuilder sql3 = new StringBuilder();
		StringBuilder sql4 = new StringBuilder();
		StringBuilder sql5 = new StringBuilder();
		StringBuilder sql6 = new StringBuilder();
		StringBuilder sql7 = new StringBuilder();
		StringBuilder sql8 = new StringBuilder();
		sql0.append("delete from tab_gatherOSS2_record where device_id='").append(deviceId).append("'");
		sql1.append("delete from tab_gatherOSS2_base where device_id='").append(deviceId).append("'");
		sql2.append("delete from tab_gatherOSS2_lan where device_id='").append(deviceId).append("'");
		sql3.append("delete from tab_gatherOSS2_lanconf where device_id='").append(deviceId).append("'");
		sql4.append("delete from tab_gatherOSS2_lanhost where device_id='").append(deviceId).append("'");
		sql5.append("delete from tab_gatherOSS2_wanip where device_id='").append(deviceId).append("'");
		sql6.append("delete from tab_gatherOSS2_wanppp where device_id='").append(deviceId).append("'");
		sql7.append("delete from tab_gatherOSS2_wlan where device_id='").append(deviceId).append("'");
		sql8.append("delete from tab_gatherOSS2_wlanhost where device_id='").append(deviceId).append("'");
		
		list.add(sql0.toString().replaceAll("'null'", "null"));
		list.add(sql1.toString().replaceAll("'null'", "null"));
		list.add(sql2.toString().replaceAll("'null'", "null"));
		list.add(sql3.toString().replaceAll("'null'", "null"));
		list.add(sql4.toString().replaceAll("'null'", "null"));
		list.add(sql5.toString().replaceAll("'null'", "null"));
		list.add(sql6.toString().replaceAll("'null'", "null"));
		list.add(sql7.toString().replaceAll("'null'", "null"));
		list.add(sql8.toString().replaceAll("'null'", "null"));
		for(String sql:list){
			loger.info(sql);
		}
		int result = DBOperation.executeUpdate(list);
		list = null;
		if (1 == result)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	/**
	 * 最后插入记录表
	 * @param deviceId
	 * @return
	 */
	public int insertRecord(String deviceId, String result){
		PrepareSQL psql = new PrepareSQL("insert into tab_gatherOSS2_record(device_id,gather_time,result) " +
				"values (?,?,?)");
		psql.setString(1, deviceId);
		psql.setLong(2, new DateTimeUtil().getLongTime());
		psql.setString(3, result);
		
		return DBOperation.executeUpdate(psql.getSQL());
	}
	
	
	
	/**
	 * 记录base表
	 * @param deviceId
	 * @return
	 */
	public int inserBase(String deviceId, HashMap<String, String> pathBase,
			long gathertime, Map<String, String> paramValueMap){
		
		StringBuffer sf1 = new StringBuffer("insert into tab_gatherOSS2_base (device_id,gather_time");
		StringBuffer sf2 = new StringBuffer(") values(?,?");
		for (Map.Entry<String, String> entry : pathBase.entrySet())
		{
			String paramName = entry.getKey();
			sf1.append(",").append(paramName.substring(paramName.lastIndexOf(".") + 1));
			sf2.append(",?");
		}
		PrepareSQL psql = new PrepareSQL(sf1.toString() + sf2.toString() + ")");
		int index = 1;
		psql.setString(index++, deviceId);
		psql.setLong(index++, gathertime);
		for (Map.Entry<String, String> entry : pathBase.entrySet())
		{
			String paramName = entry.getKey();
			//遍历pathBase，将paramValueMap结果放到pathAll中
			psql.setString(index++, StringUtil.getStringValue(paramValueMap, paramName, ""));
		}
		
		return DBOperation.executeUpdate(psql.getSQL());
	}
	
	
	public static void main(String[] args)
	{
		String currKey = "InternetGatewayDevice.LANDevice.1.WLANConfiguration.1.PreSharedKey.2.AssociatedDeviceMACAddress";
		GatherOSS2DAO a = new GatherOSS2DAO();
		System.out.println(a.geti(currKey, ".AssociatedDevice."));
	}
	
	public String geti(String currKey, String str){
		int left = currKey.indexOf(str) + str.length();
		if(currKey.indexOf(str)==-1){
			loger.warn("geti异常,currKey={},str={}",currKey,str);
			return "";
		}
		int right = currKey.indexOf(".",left);
		if(right!=-1){
			return currKey.substring(left, right);
		}
		else{
			return currKey.substring(left);
		}
	}
	
	/**
	 * 记录LAN信息
	 * @param deviceId
	 * @return
	 */
	public int inserLan(String deviceId, String oui,String sn, long gathertime, Map<String, String> paramValueMap){
		
		ArrayList<String> list = new ArrayList<String>();
		ArrayList<String> keyList = new ArrayList<String>();
		
		for (Map.Entry<String, String> entry : paramValueMap.entrySet())
		{
			String namepath = entry.getKey();
			if(namepath.indexOf(".LANEthernetInterfaceConfig.") >=0){
				if (namepath.indexOf(".Enable") >= 0 || namepath.indexOf(".Status") >= 0 || namepath.indexOf(".MACAddress") >= 0 ||namepath.indexOf(".MaxBitRate") >= 0 ||
						namepath.indexOf(".X_CU_AdaptRate") >= 0 ||namepath.indexOf(".DuplexMode") >= 0 ||namepath.indexOf(".Stats.BytesSent") >= 0 ||
						namepath.indexOf(".Stats.BytesReceived") >= 0 ||namepath.indexOf(".Stats.PacketsSent") >= 0 ||namepath.indexOf(".Stats.PacketsReceived") >= 0){
					boolean exist = false;
					for(String currKey : keyList){
						if(namepath.contains(currKey)){ //当前namepath的InternetGatewayDevice.LANDevice.{i}.LANEthernetInterfaceConfig.{i}已经出现过
							exist = true;
							break;
						}
					}
					if(exist) continue;
					else{
						String currKey = namepath.substring(0, namepath.lastIndexOf("."));
						if(currKey.length() - "InternetGatewayDevice.LANDevice.1.LANEthernetInterfaceConfig.1".length()>1) continue;
						keyList.add(currKey);
					}
					continue;
				}
			}
		}
		
		//遍历所有key
		for(String currKey : keyList){
			loger.warn("lancurrKey="+ currKey);
			PrepareSQL psql = new PrepareSQL("insert into tab_gatherOSS2_lan (device_id,ManufacturerOUI,SerialNumber," +
					"Enable,Status,MACAddress,MaxBitRate,X_CU_AdaptRate,DuplexMode,BytesSent,BytesReceived,PacketsSent,PacketsReceived,LANDevicei,LANEthIntConfigi,gather_time) " +
					"values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			psql.setString(1, deviceId);
			psql.setString(2, oui);
			psql.setString(3, sn);
			psql.setString(4, StringUtil.getStringValue(paramValueMap, currKey+".Enable", ""));
			psql.setString(5, StringUtil.getStringValue(paramValueMap, currKey+".Status", ""));
			psql.setString(6, StringUtil.getStringValue(paramValueMap, currKey+".MACAddress", ""));
			psql.setString(7, StringUtil.getStringValue(paramValueMap, currKey+".MaxBitRate", ""));
			psql.setString(8, StringUtil.getStringValue(paramValueMap, currKey+".X_CU_AdaptRate", ""));
			psql.setString(9, StringUtil.getStringValue(paramValueMap, currKey+".DuplexMode", ""));
			psql.setString(10, StringUtil.getStringValue(paramValueMap, currKey+".Stats.BytesSent", ""));
			psql.setString(11, StringUtil.getStringValue(paramValueMap, currKey+".Stats.BytesReceived", ""));
			psql.setString(12, StringUtil.getStringValue(paramValueMap, currKey+".Stats.PacketsSent", ""));
			psql.setString(13, StringUtil.getStringValue(paramValueMap, currKey+".Stats.PacketsReceived", ""));
			psql.setString(14, geti(currKey, ".LANDevice."));
			psql.setString(15, geti(currKey, ".LANEthernetInterfaceConfig."));
			psql.setLong(16, gathertime);
			
			list.add(psql.getSQL());
		}
		
		return DBOperation.executeUpdate(list);
	}
	
	
	
	/**
	 * 记录LAN配置信息
	 * @param deviceId
	 * @return
	 */
	public int inserLanconf(String deviceId, String oui,String sn, long gathertime, Map<String, String> paramValueMap){
		
		ArrayList<String> list = new ArrayList<String>();
		ArrayList<String> keyList = new ArrayList<String>();
		
		for (Map.Entry<String, String> entry : paramValueMap.entrySet())
		{
			String namepath = entry.getKey();
			if(namepath.indexOf(".LANHostConfigManagement.DHCPServerEnable") >=0){
				boolean exist = false;
				for(String currKey : keyList){
					if(namepath.contains(currKey)){ //当前namepath的InternetGatewayDevice.LANDevice.{i}.LANEthernetInterfaceConfig.{i}已经出现过
						exist = true;
						break;
					}
				}
				if(exist) continue;
				else{
					String currKey = namepath.substring(0, namepath.lastIndexOf("."));
					keyList.add(currKey);
				}
				continue;
			}
		}
		
		//遍历所有key
		for(String currKey : keyList){
			loger.warn("lanconfcurrKey="+ currKey);
			PrepareSQL psql = new PrepareSQL("insert into tab_gatherOSS2_lanconf (device_id,ManufacturerOUI,SerialNumber,DHCPServerEnable,LANDevicei,gather_time) " +
					"values (?,?,?,?,?,?)");
			psql.setString(1, deviceId);
			psql.setString(2, oui);
			psql.setString(3, sn);
			psql.setString(4, StringUtil.getStringValue(paramValueMap, currKey+".DHCPServerEnable", ""));
			psql.setString(5, geti(currKey, ".LANDevice."));
			psql.setLong(6, gathertime);
			list.add(psql.getSQL());
		}
		
		return DBOperation.executeUpdate(list);
	}
	
	
	/**
	 * 记录LANhost信息
	 * @param deviceId
	 * @return
	 */
	public int inserLanhost(String deviceId, String oui,String sn, long gathertime, Map<String, String> paramValueMap){
		
		ArrayList<String> list = new ArrayList<String>();
		ArrayList<String> keyList = new ArrayList<String>();
		
		for (Map.Entry<String, String> entry : paramValueMap.entrySet())
		{
			String namepath = entry.getKey();
			if(namepath.indexOf(".Hosts.Host.") >=0){
				if (namepath.indexOf(".HostName") >= 0 || namepath.indexOf(".VendorClassID") >= 0 ||namepath.indexOf(".Layer2Interface") >= 0 ||namepath.indexOf(".Active") >= 0 ||
						namepath.indexOf(".InterfaceType") >= 0 ||namepath.indexOf(".X_CU_Hosttype") >= 0 ||
						namepath.indexOf(".IPAddress") >= 0 ||namepath.indexOf(".MACAddress") >= 0){
					boolean exist = false;
					for(String currKey : keyList){
						if(namepath.contains(currKey)){ //当前namepath的InternetGatewayDevice.LANDevice.{i}.LANEthernetInterfaceConfig.{i}已经出现过
							exist = true;
							break;
						}
					}
					if(exist) continue;
					else{
						String currKey = namepath.substring(0, namepath.lastIndexOf("."));
						keyList.add(currKey);
					}
					continue;
				}
			}
		}
		
		//遍历所有key
		for(String currKey : keyList){
			loger.warn("lanhostcurrKey="+ currKey);
			PrepareSQL psql = new PrepareSQL("insert into tab_gatherOSS2_lanhost (device_id,ManufacturerOUI,SerialNumber," +
					"InterfaceType,HostNumberOfEntries,X_CU_Hosttype,IPAddress,MACAddress,LANDevicei,Hosti,gather_time,HostName,VendorClassID,Layer2Interface,Active) " +
					"values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			psql.setString(1, deviceId);
			psql.setString(2, oui);
			psql.setString(3, sn);
			psql.setString(4, StringUtil.getStringValue(paramValueMap, currKey+".InterfaceType", ""));
			psql.setString(5, StringUtil.getStringValue(paramValueMap, "InternetGatewayDevice.LANDevice.1.Hosts.HostNumberOfEntries", ""));
			psql.setString(6, StringUtil.getStringValue(paramValueMap, currKey+".X_CU_Hosttype", ""));
			psql.setString(7, StringUtil.getStringValue(paramValueMap, currKey+".IPAddress", ""));
			psql.setString(8, StringUtil.getStringValue(paramValueMap, currKey+".MACAddress", ""));
			psql.setString(9, geti(currKey, ".LANDevice."));
			psql.setString(10, geti(currKey, ".Hosts.Host."));
			psql.setLong(11, gathertime);
			
			psql.setString(12, StringUtil.getStringValue(paramValueMap, currKey+".HostName", ""));
			psql.setString(13, StringUtil.getStringValue(paramValueMap, currKey+".VendorClassID", ""));
			psql.setString(14, StringUtil.getStringValue(paramValueMap, currKey+".Layer2Interface", ""));
			psql.setString(15, StringUtil.getStringValue(paramValueMap, currKey+".Active", ""));
			
			list.add(psql.getSQL());
		}
		
		return DBOperation.executeUpdate(list);
	}
	
	
	
	/**
	 * 记录wan ip信息
	 * @param deviceId
	 * @return
	 */
	public int inserWanip(String deviceId, String oui,String sn, long gathertime, Map<String, String> paramValueMap){
		
		ArrayList<String> list = new ArrayList<String>();
		ArrayList<String> keyList = new ArrayList<String>();
		
		for (Map.Entry<String, String> entry : paramValueMap.entrySet())
		{
			String namepath = entry.getKey();
			if(namepath.indexOf("InternetGatewayDevice.WANDevice.1.WANConnectionDevice") >=0 && namepath.indexOf(".WANIPConnection.") >=0){
				if (namepath.indexOf("X_CU_ServiceList") >= 0 || namepath.indexOf("ConnectionType") >= 0 ||namepath.indexOf("X_CU_IPMode") >= 0 ||
						namepath.indexOf("X_CU_IPv6IPAddressOrigin") >= 0 ||namepath.indexOf("WANIPConnectionNumberOfEntries") >= 0 ||namepath.indexOf("EthernetBytesSent") >= 0 ||
						namepath.indexOf("EthernetBytesReceived") >= 0 ||namepath.indexOf("EthernetPacketsSent") >= 0 ||namepath.indexOf("EthernetPacketsReceived") >= 0 ||namepath.indexOf("WANPPPConnectionNumberOfEntries") >= 0){
					boolean exist = false;
					for(String currKey : keyList){
						if(namepath.contains(currKey)){ //当前namepath的InternetGatewayDevice.LANDevice.{i}.LANEthernetInterfaceConfig.{i}已经出现过
							exist = true;
							break;
						}
					}
					if(exist) continue;
					else{
						String currKey = namepath.substring(0, namepath.lastIndexOf("."));
						if(currKey.length() - "InternetGatewayDevice.WANDevice.1.WANConnectionDevice.1.WANIPConnection.1".length()>1) continue;
						if(-1 == currKey.indexOf(".WANIPConnection.")) continue;
						keyList.add(currKey);
					}
					continue;
				}
			}
		}
		
		//遍历所有key
		for(String currKey : keyList){
			loger.warn("wanipcurrKey="+ currKey);
			PrepareSQL psql = new PrepareSQL("insert into tab_gatherOSS2_wanip (device_id,ManufacturerOUI,SerialNumber," +
					"X_CU_ServiceList,ConnectionType,X_CU_IPMode,X_CU_IPv6IPAddressOrigin,WANIPConnectionNumberOfEntries," +
					"EthernetBytesSent,EthernetBytesReceived,EthernetPacketsSent,EthernetPacketsReceived,WANDevicei,WANConnDevi,WANIPConnectioni,gather_time) " +
					"values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			psql.setString(1, deviceId);
			psql.setString(2, oui);
			psql.setString(3, sn);
			psql.setString(4, StringUtil.getStringValue(paramValueMap, currKey+".X_CU_ServiceList", ""));
			psql.setString(5, StringUtil.getStringValue(paramValueMap, currKey+".ConnectionType", ""));
			psql.setString(6, StringUtil.getStringValue(paramValueMap, currKey+".X_CU_IPMode", ""));
			psql.setString(7, StringUtil.getStringValue(paramValueMap, currKey+".X_CU_IPv6IPAddressOrigin", ""));
			psql.setString(8, StringUtil.getStringValue(paramValueMap, currKey.substring(0, currKey.indexOf(".WANIPConnection."))+".WANIPConnectionNumberOfEntries", ""));
			psql.setString(9, StringUtil.getStringValue(paramValueMap, currKey+".Stats.EthernetBytesSent", ""));
			psql.setString(10, StringUtil.getStringValue(paramValueMap, currKey+".Stats.EthernetBytesReceived", ""));
			psql.setString(11, StringUtil.getStringValue(paramValueMap, currKey+".Stats.EthernetPacketsSent", ""));
			psql.setString(12, StringUtil.getStringValue(paramValueMap, currKey+".Stats.EthernetPacketsReceived", ""));
			psql.setString(13, geti(currKey, ".WANDevice."));
			psql.setString(14, geti(currKey, ".WANConnectionDevice."));
			psql.setString(15, geti(currKey, ".WANIPConnection."));
			psql.setLong(16, gathertime);
			list.add(psql.getSQL());
		}
		
		return DBOperation.executeUpdate(list);
	}
	
	
	/**
	 * 记录wan ppp信息
	 * @param deviceId
	 * @return
	 */
	public int inserWanppp(String deviceId, String oui,String sn, long gathertime, Map<String, String> paramValueMap){
		
		ArrayList<String> list = new ArrayList<String>();
		ArrayList<String> keyList = new ArrayList<String>();
		
		for (Map.Entry<String, String> entry : paramValueMap.entrySet())
		{
			String namepath = entry.getKey();
			if(namepath.indexOf("InternetGatewayDevice.WANDevice.1.WANConnectionDevice") >=0 && namepath.indexOf(".WANPPPConnection.") >=0){
				if (namepath.indexOf("X_CU_ServiceList") >= 0 || namepath.indexOf("ConnectionType") >= 0 ||namepath.indexOf("X_CU_IPMode") >= 0 ||
						namepath.indexOf("X_CU_IPv6IPAddressOrigin") >= 0 ||namepath.indexOf("WANIPConnectionNumberOfEntries") >= 0 ||namepath.indexOf("EthernetBytesSent") >= 0 ||
						namepath.indexOf("EthernetBytesReceived") >= 0 ||namepath.indexOf("EthernetPacketsSent") >= 0 ||namepath.indexOf("EthernetPacketsReceived") >= 0 ||namepath.indexOf("WANPPPConnectionNumberOfEntries") >= 0){
					boolean exist = false;
					for(String currKey : keyList){
						if(namepath.contains(currKey)){ //当前namepath的InternetGatewayDevice.LANDevice.{i}.LANEthernetInterfaceConfig.{i}已经出现过
							exist = true;
							break;
						}
					}
					if(exist) continue;
					else{
						String currKey = namepath.substring(0, namepath.lastIndexOf("."));
						if(currKey.length() - "InternetGatewayDevice.WANDevice.1.WANConnectionDevice.1.WANPPPConnection.1".length()>1) continue;
						if(-1 == currKey.indexOf(".WANPPPConnection.")) continue;
						keyList.add(currKey);
					}
					continue;
				}
			}
		}
		
		//遍历所有key
		for(String currKey : keyList){
			loger.warn("wanpppcurrKey="+ currKey);
			PrepareSQL psql = new PrepareSQL("insert into tab_gatherOSS2_wanppp (device_id,ManufacturerOUI,SerialNumber," +
					"X_CU_ServiceList,ConnectionType,X_CU_IPMode,X_CU_IPv6IPAddressOrigin,WANPPPConnectionNumberOfEntries," +
					"EthernetBytesSent,EthernetBytesReceived,EthernetPacketsSent,EthernetPacketsReceived,WANDevicei,WANConnDevi,WANPPPConnectioni,gather_time) " +
					"values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			psql.setString(1, deviceId);
			psql.setString(2, oui);
			psql.setString(3, sn);
			psql.setString(4, StringUtil.getStringValue(paramValueMap, currKey+".X_CU_ServiceList", ""));
			psql.setString(5, StringUtil.getStringValue(paramValueMap, currKey+".ConnectionType", ""));
			psql.setString(6, StringUtil.getStringValue(paramValueMap, currKey+".X_CU_IPMode", ""));
			psql.setString(7, StringUtil.getStringValue(paramValueMap, currKey+".X_CU_IPv6IPAddressOrigin", ""));
			psql.setString(8, StringUtil.getStringValue(paramValueMap, currKey.substring(0, currKey.indexOf(".WANPPPConnection."))+".WANPPPConnectionNumberOfEntries", ""));
			psql.setString(9, StringUtil.getStringValue(paramValueMap, currKey+".Stats.EthernetBytesSent", ""));
			psql.setString(10, StringUtil.getStringValue(paramValueMap, currKey+".Stats.EthernetBytesReceived", ""));
			psql.setString(11, StringUtil.getStringValue(paramValueMap, currKey+".Stats.EthernetPacketsSent", ""));
			psql.setString(12, StringUtil.getStringValue(paramValueMap, currKey+".Stats.EthernetPacketsReceived", ""));
			psql.setString(13, geti(currKey, ".WANDevice."));
			psql.setString(14, geti(currKey, ".WANConnectionDevice."));
			psql.setString(15, geti(currKey, ".WANPPPConnection."));
			psql.setLong(16, gathertime);
			list.add(psql.getSQL());
		}
		
		return DBOperation.executeUpdate(list);
	}
	
	
	
	/**
	 * 记录Wlan信息
	 * @param deviceId
	 * @return
	 */
	public int inserWlan(String deviceId, String oui,String sn, long gathertime, Map<String, String> paramValueMap){
		
		ArrayList<String> list = new ArrayList<String>();
		ArrayList<String> keyList = new ArrayList<String>();
		
		for (Map.Entry<String, String> entry : paramValueMap.entrySet())
		{
			String namepath = entry.getKey();
			if(namepath.indexOf(".WLANConfiguration.") >=0){
				if (namepath.indexOf(".X_CU_Dual_bandsupport") >= 0 || namepath.indexOf(".Status") >= 0 ||namepath.indexOf(".X_CU_Band") >= 0 ||
						namepath.indexOf(".Channel") >= 0 ||namepath.indexOf(".Standard") >= 0||namepath.indexOf(".MaxBitRate") >= 0 ||
						namepath.indexOf(".WPAAuthenticationMode") >= 0 ||namepath.indexOf(".WMMSupported") >= 0||namepath.indexOf(".WMMEnable") >= 0 ||
						namepath.indexOf(".TotalBytesSent") >= 0 ||namepath.indexOf(".TotalBytesReceived") >= 0||namepath.indexOf(".TotalPacketsSent") >= 0
						||namepath.indexOf(".TotalPacketsReceived") >= 0||namepath.indexOf(".TransmitPower") >= 0){
					boolean exist = false;
					for(String currKey : keyList){
						if(namepath.contains(currKey)){ //当前namepath的InternetGatewayDevice.LANDevice.{i}.LANEthernetInterfaceConfig.{i}已经出现过
							exist = true;
							break;
						}
					}
					if(exist) continue;
					else{
						String currKey = namepath.substring(0, namepath.lastIndexOf("."));
						keyList.add(currKey);
					}
					continue;
				}
			}
		}
		
		//遍历所有key
		for(String currKey : keyList){
			loger.warn("wlancurrKey="+ currKey);
			PrepareSQL psql = new PrepareSQL("insert into tab_gatherOSS2_wlan (device_id,ManufacturerOUI,SerialNumber," +
					"X_CU_Dual_bandsupport,Status,X_CU_Band,Channel,Standard,MaxBitRate,WPAAuthenticationMode,WMMSupported," +
					"WMMEnable,TotalBytesSent,TotalBytesReceived,TotalPacketsSent,TotalPacketsReceived,TransmitPower,LANDevicei,WLANConfigurationi,gather_time) " +
					"values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			psql.setString(1, deviceId);
			psql.setString(2, oui);
			psql.setString(3, sn);
			psql.setString(4, StringUtil.getStringValue(paramValueMap, currKey+".X_CU_Dual_bandsupport", ""));
			psql.setString(5, StringUtil.getStringValue(paramValueMap, currKey+".Status", ""));
			psql.setString(6, StringUtil.getStringValue(paramValueMap, currKey+".X_CU_Band", ""));
			psql.setString(7, StringUtil.getStringValue(paramValueMap, currKey+".Channel", ""));
			psql.setString(8, StringUtil.getStringValue(paramValueMap, currKey+".Standard", ""));
			psql.setString(9, StringUtil.getStringValue(paramValueMap, currKey+".MaxBitRate", ""));
			psql.setString(10, StringUtil.getStringValue(paramValueMap, currKey+".WPAAuthenticationMode", ""));
			psql.setString(11, StringUtil.getStringValue(paramValueMap, currKey+".WMMSupported", ""));
			psql.setString(12, StringUtil.getStringValue(paramValueMap, currKey+".WMMEnable", ""));
			psql.setString(13, StringUtil.getStringValue(paramValueMap, currKey+".TotalBytesSent", ""));
			psql.setString(14, StringUtil.getStringValue(paramValueMap, currKey+".TotalBytesReceived", ""));
			psql.setString(15, StringUtil.getStringValue(paramValueMap, currKey+".TotalPacketsSent", ""));
			psql.setString(16, StringUtil.getStringValue(paramValueMap, currKey+".TotalPacketsReceived", ""));
			psql.setString(17, StringUtil.getStringValue(paramValueMap, currKey+".TransmitPower", ""));
			psql.setString(18, geti(currKey, ".LANDevice."));
			psql.setString(19, geti(currKey, ".WLANConfiguration."));
			psql.setLong(20, gathertime);
			list.add(psql.getSQL());
		}
		
		return DBOperation.executeUpdate(list);
	}
	
	
	
	/**
	 * 记录Wlanhost信息
	 * @param deviceId
	 * @return
	 */
	public int inserWlanhost(String deviceId, String oui,String sn, long gathertime, Map<String, String> paramValueMap){
		
		ArrayList<String> list = new ArrayList<String>();
		ArrayList<String> keyList = new ArrayList<String>();
		
		for (Map.Entry<String, String> entry : paramValueMap.entrySet())
		{
			String namepath = entry.getKey();
			if(namepath.indexOf(".WLANConfiguration.") >=0 && namepath.indexOf(".AssociatedDevice.") >=0){
				if (namepath.indexOf(".AssociatedDeviceMACAddress") >= 0||namepath.indexOf(".AssociatedDeviceIPAddress") >= 0){
					boolean exist = false;
					for(String currKey : keyList){
						if(namepath.contains(currKey)){ //当前namepath的InternetGatewayDevice.LANDevice.{i}.LANEthernetInterfaceConfig.{i}已经出现过
							exist = true;
							break;
						}
					}
					if(exist) continue;
					else{
						String currKey = namepath.substring(0, namepath.lastIndexOf("."));
						keyList.add(currKey);
					}
					continue;
				}
			}
		}
		
		//遍历所有key
		for(String currKey : keyList){
			loger.warn("wlanhostcurrKey="+ currKey);
			PrepareSQL psql = new PrepareSQL("insert into tab_gatherOSS2_wlanhost (device_id,ManufacturerOUI,SerialNumber," +
					"AssociatedDeviceMACAddress,AssociatedDeviceIPAddress,LANDevicei,WLANConfigurationi,AssociatedDevicei,gather_time) " +
					"values (?,?,?,?,?,?,?,?,?)");
			psql.setString(1, deviceId);
			psql.setString(2, oui);
			psql.setString(3, sn);
			psql.setString(4, StringUtil.getStringValue(paramValueMap, currKey+".AssociatedDeviceMACAddress", ""));
			psql.setString(5, StringUtil.getStringValue(paramValueMap, currKey+".AssociatedDeviceIPAddress", ""));
			psql.setString(6, geti(currKey, ".LANDevice."));
			psql.setString(7, geti(currKey, ".WLANConfiguration."));
			psql.setString(8, geti(currKey, ".AssociatedDevice."));
			psql.setLong(9, gathertime);
			list.add(psql.getSQL());
		}
		
		return DBOperation.executeUpdate(list);
	}
	
	
	
	/**
	 * 获得上行方式 
	 * @param deviceId
	 * @return
	 */
	public  String getAccessType(String deviceId)
	{
		StringBuilder sql = new StringBuilder();
		sql.append("select access_type from gw_wan where device_id='").append(deviceId)
				.append("' and wan_id=1");
		Map<String, String> accessTypeMap = DBOperation.getRecord(sql.toString());
		if (null == accessTypeMap || null == accessTypeMap.get("access_type"))
		{
			return null;
		}
		else
		{
			return accessTypeMap.get("access_type");
		}
	}
	
	
	/**
	 * 查询最近上线的设备id集合
	 * @param time
	 * @param tableName
	 * @return
	 */
	public ArrayList<HashMap<String, String>> getDevice4Gather(long time, String tableName){
		PrepareSQL psql = new PrepareSQL("select device_id from gw_devicestatus where not exists (select * from "+tableName+" where device_id = gw_devicestatus.device_id) and last_time > ? and online_status=1 order by last_time desc");
		psql.setLong(1, time);
		return DBOperation.getRecords(psql.getSQL());
	}
}
