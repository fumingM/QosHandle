package alik.itms.QosHandle.gatherVlan.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.linkage.commons.db.DBOperation;
import com.linkage.commons.db.PrepareSQL;
import com.linkage.commons.util.StringUtil;

public class GatherVlanDAO {
	
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
	public ArrayList<HashMap<String, String>> getGatherVLanData(String deviceId,String tableName){
		PrepareSQL psql = new PrepareSQL("select * from " + tableName + " where device_id = ?");
		psql.setString(1, deviceId);
		return DBOperation.getRecords(psql.getSQL());
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
	 * 全网业务新增数据库 telnet
	 * @param deviceId
	 * @param tableName
	 * @param status
	 * @param time
	 * @return
	 */
	public int addTelnetAllData(String deviceId,String tableName,Map<String,Object> vLanNodeMap,long time){
		PrepareSQL psql = new PrepareSQL("insert into " + tableName + " (device_id,status,gathertime,addtime,TelnetEnable,FtpEnable,result_desc) values(?,?,?,?,?,?,?)");
		psql.setString(1, deviceId);
		psql.setInt(2, StringUtil.getIntegerValue(vLanNodeMap.get("resutlCode")));
		psql.setLong(3, time);
		psql.setLong(4, time);
		psql.setString(5, StringUtil.getStringValue(vLanNodeMap.get("TelnetEnable")));
		psql.setString(6, StringUtil.getStringValue(vLanNodeMap.get("FtpEnable")));
		psql.setString(7, StringUtil.getStringValue(vLanNodeMap.get("desc")));
		return DBOperation.executeUpdate(psql.getSQL());
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
