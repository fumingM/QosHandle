package alik.itms.QosHandle.batchSet.dao;

import java.util.ArrayList;
import java.util.HashMap;

import com.linkage.commons.db.DBOperation;
import com.linkage.commons.db.PrepareSQL;

public class BatchSetDAO {
	
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
	 * 查询设备是否已经设置过
	 * @param deviceId
	 * @param tableName
	 * @return
	 */
	public ArrayList<HashMap<String, String>> getSetData(String deviceId,String tableName){
		PrepareSQL psql = new PrepareSQL("select a.*,b.ParamPath,b.ParamValue,b.ParamType,b.set_strategy from " + tableName + " a,tab_batchconfig_task b where a.task_id=b.task_id and a.device_id = ?");
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
	public int updateAllData(String deviceId,String tableName,int status,long time){
		PrepareSQL psql = new PrepareSQL("update " + tableName + " set status=?,settime=?  where device_id = ?");
		psql.setInt(1, status);
		psql.setLong(2, time);
		psql.setString(3, deviceId);
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
	public int addAllData(String deviceId,String tableName,int status,long time){
		PrepareSQL psql = new PrepareSQL("insert into " + tableName + " (device_id,status,settime,addtime) values(?,?,?,?)");
		psql.setString(1, deviceId);
		psql.setInt(2, status);
		psql.setLong(3, time);
		psql.setLong(4, time);
		return DBOperation.executeUpdate(psql.getSQL());
	}
}
