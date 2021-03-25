package alik.itms.QosHandle.gatherVlan.thread;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import alik.itms.QosHandle.common.Global;
import alik.itms.QosHandle.common.ThreadMonitor;
import alik.itms.QosHandle.gatherVlan.dao.GatherVlanDAO;

/**
 * 定时任务
 * @author jiafh
 *
 */
public class GatherVlanExpiryDateTimer extends TimerTask{
	
	private Logger loger = LoggerFactory.getLogger(GatherVlanExpiryDateTimer.class);
	
	private GatherVlanDAO gatherVlanDAO = new GatherVlanDAO();

	public static void main(String[] args)
	{
		List<HashMap<String,String>> al = new ArrayList<HashMap<String,String>>();
		HashMap<String,String> hm1 = new HashMap<String,String>();
		HashMap<String,String> hm2 = new HashMap<String,String>();
		hm1.put("1", "1");
		hm2.put("2", "2");
		al.add(hm1);
		al.add(hm2);
		al = al.subList(0, 3);
		System.out.println();
	}
	@Override
	public void run() {
		long currTime = 0;
		
		// 当前时间
		currTime = System.currentTimeMillis()/1000;
		
		long queue = new ThreadMonitor(Global.GATHERVLAN_THREADPOOL, 5).getqueueCount();
		if(queue > Global.GATHERVLAN_POOLSIZE){
			loger.warn("[{}]当前线程池排队较多：" + queue + ", 本次不执行", currTime);
		}
		else{
			loger.warn("[{}]当前线程池排队：" + queue + ", 采集开始", currTime);
			loger.error("[{}]当前线程池排队：" + queue + ", 采集开始", currTime);
		}
		
		// 判断是否设置有效期		
		if(0 != Global.GATHERVLAN_EXPIRYDATE){
			// 失效时间
			long addTime = currTime - Global.GATHERVLAN_EXPIRYDATE * 24 * 60 * 60;
			int deleteCount = gatherVlanDAO.deleteOldData(addTime, Global.GATHERVLAN_TABLENAME);
			loger.warn("GatherVlanExpiryDateTimer == >删除{}{}条失效数据。",Global.GATHERVLAN_TABLENAME,deleteCount);
			
			// 失效时间
			addTime = currTime - Global.GATHERTELNETFTP_EXPIRYDATE * 24 * 60 * 60;
			deleteCount = gatherVlanDAO.deleteOldData(addTime, Global.GATHERTELNETFTP_TABLENAME);
			loger.warn("GatherVlanExpiryDateTimer == >删除{}{}条失效数据。",Global.GATHERTELNETFTP_TABLENAME,deleteCount);
		}
		
		//currTime = System.currentTimeMillis()/1000;
		Global.ftpList = gatherVlanDAO.getDevice4Gather(currTime - 60 * 10 , Global.GATHERTELNETFTP_TABLENAME);
		if(Global.ftpList.size() > Global.GATHER_NUM){
			Global.ftpList = Global.ftpList.subList(0, Global.GATHER_NUM);
		}
		loger.warn("[{}]时刻查询采集Telnet/Ftp数量：{}", currTime, Global.ftpList.size());
		
		Global.vlanList = gatherVlanDAO.getDevice4Gather(currTime - 60 * 10 , Global.GATHERVLAN_TABLENAME);
		if(Global.vlanList.size() > Global.GATHER_NUM){
			Global.vlanList = Global.vlanList.subList(0, Global.GATHER_NUM);
		}
		loger.warn("[{}]时刻查询采集VlanMulticast数量：{}", currTime, Global.ftpList.size());
		
		Global.ftpMap.clear();
		Global.vlanMap.clear();
		Global.gatherMap.clear();
		
		for(int i = 0; i<Global.vlanList.size(); i++ ){
			Global.gatherMap.put(Global.vlanList.get(i).get("device_id"), "");
			Global.vlanMap.put(Global.vlanList.get(i).get("device_id"), "");
		}
		
		for(int i = 0; i<Global.ftpList.size(); i++ ){
			Global.gatherMap.put(Global.ftpList.get(i).get("device_id"), "");
			Global.ftpMap.put(Global.ftpList.get(i).get("device_id"), "");
		}
		
		for (Entry<String, String> entry : Global.gatherMap.entrySet()){
			Global.GATHERVLAN_THREADPOOL.execute(new GatherThread(entry.getKey()));
		}
	}
}
