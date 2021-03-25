package alik.itms.QosHandle.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import alik.itms.QosHandle.batchSet.obj.NodeObj;

import com.linkage.commons.jms.MQPublisher;
import com.linkage.commons.jms.obj.MQConfig;
import com.linkage.commons.thread.ThreadPoolCommon;

/**
 * 公共类
 * @author jiafh
 *
 */
public class Global {
	public static String G_Alias = null;
	// 系统路径
	public static String G_HomePath = null;
	public static String G_ConfPath = null;
	
	// 系统名称
	public static String G_SERVERNAME = null;
	// 版本
	public static String G_VERSION = null;
	
	// 区域
	public static String G_INSTAREA = null;
	
	// 是否使用批量配置节点功能 0：不使用，1：使用
	public static int G_BATCHSETENABLE = 0;
	
	// 是否使用批量采集VLAN节点功能0：不使用，1：使用
	public static int G_BATCHGATHERVLAN = 0;
	
	// 是否使用采集OSS2节点功能0：不使用，1：使用
	public static int G_BATCHGATHEROSS2 = 0;
		
	// 是否使用批量修改系统超级密码功能0：不使用，1：使用
	public static int G_SUPERPASSWDENABLE = 0;
	
	public static Map<String, MQConfig> MQ_POOl_MAP = new HashMap<String, MQConfig>();

	// 调用ACS的Corba使用
	public static String SYSTEM_NAME = "ITMS";
	
	// 调用Corba模块标识
	public static String ClIENT_ID = null;
	
	public static Map<String,MQPublisher> MQPUBLISHERMAP = new HashMap<String, MQPublisher>();
	
	public static int Priority_Hig = 1;
	
	public static String SYSTEM_ITMS_PREFIX = "ITMS_";
	public static String SYSTEM_BBMS_PREFIX = "BBMS_";
	public static String SYSTEM_STB_PREFIX = "STB_";
	public static String SYSTEM_ITMS = "ITMS";
	public static String SYSTEM_BBMS = "BBMS";
	public static String SYSTEM_STBMS = "STBMS";
	
	public static String SYSTEM_ACS = "ACS";
	/** 命令类型：0：检测连接、1：普通命令（获取参数、设置参数）、2：诊断命令、3：文件下发（软件升级） */
	public static int RpcTest_Type = 0;
	public static int RpcCmd_Type = 1;
	
	// 获取前缀名称
	public static String getPrefixName(String systemName)
	{
		if (SYSTEM_ITMS.equals(systemName))
		{
			return SYSTEM_ITMS_PREFIX;
		}
		else if (SYSTEM_BBMS.equals(systemName))
		{
			return SYSTEM_BBMS_PREFIX;
		}
		else if (SYSTEM_STBMS.equals(systemName))
		{
			return SYSTEM_STB_PREFIX;
		}
		else
		{
			return "";
		}
	}
	
	////////////////////批量设置固定节点配置项/////////////////////////////////////
	public static int BATCHSET_ISMAKE = 0;
	
	public static int BATCHSET_EXPIRYDATE = 7;
	
	public static int BATCHSET_EVENTENABLE = 0;
	
	public static String BATCHSET_EVENTLIST = "1";
	
	public static int BATCHSET_FAILENABLE = 0;
	
	public static String BATCHSET_FAILRESETLIST = "-1,-2";
	
	public static int BATCHSET_POOLSIZE = 100;
	
	public static List<NodeObj> BATCHSET_NODELIST = new ArrayList<NodeObj>(); 
	
	public static String BATCHSET_TABLENAME = "tab_batch_result_telnet";
	
	public static ThreadPoolCommon BATCHSET_THREADPOOL = null;
	
	////////////////////批量采集VLAN节点/////////////////////////////////////////////
	public static int GATHERVLAN_ISMAKE = 0;
	
	public static int GATHERVLAN_EXPIRYDATE = 7;
	
	public static int GATHERVLAN_EVENTENABLE = 0;
	
	public static String GATHERVLAN_EVENTLIST = "1";
	
	public static int GATHERVLAN_FAILENABLE = 0;
	
	public static String GATHERVLAN_FAILRESETLIST = "-1,-2";
	
	public static int GATHERVLAN_POOLSIZE = 100;
	
	public static ThreadPoolCommon GATHERVLAN_THREADPOOL = null;
	
	public static String GATHERVLAN_TABLENAME = "tab_result_multicast_vlan";
	
	////////////////////批量采集VLAN节点/////////////////////////////////////////////
	public static int GATHERTELNETFTP_ISMAKE = 0;

	public static int GATHERTELNETFTP_EXPIRYDATE = 7;

	public static int GATHERTELNETFTP_EVENTENABLE = 0;

	public static String GATHERTELNETFTP_EVENTLIST = "1";

	public static int GATHERTELNETFTP_FAILENABLE = 0;

	public static String GATHERTELNETFTP_FAILRESETLIST = "-1,-2";

	public static int GATHERTELNETFTP_POOLSIZE = 100;

	public static ThreadPoolCommon GATHERTELNETFTP_THREADPOOL = null;

	public static String GATHERTELNETFTP_TABLENAME = "tab_result_telnetftp";
	
	////////////////////修改超级密码////////////////////////////////////////////////
	public static int SUPERPASSWD_ISMAKE = 0;
	
	public static int SUPERPASSWD_EXPIRYDATE = 7;
	
	public static int SUPERPASSWD_EVENTENABLE = 0;
	
	public static String SUPERPASSWD_EVENTLIST = "1";
	
	public static int SUPERPASSWD_FAILENABLE = 0;
	
	public static String SUPERPASSWD_FAILRESETLIST = "-1,-2";
	
	public static int SUPERPASSWD_POOLSIZE = 100;
	
	public static ThreadPoolCommon SUPERPASSWD_THREADPOOL = null;
	
	public static String SUPERPASSWD_TABLENAME = "tab_result_super_passwd";
	
	public static int GATHER_NUM = 1000;
	
	////////////////////OSS2采集/////////////////////////////////////////////
	public static int GATHEROSS2_ISMAKE = 0;
	
	public static int GATHEROSS2_EXPIRYDATE = 7;
	
	public static int GATHEROSS2_EVENTENABLE = 0;
	
	public static String GATHEROSS2_EVENTLIST = "1";
	
	public static int GATHEROSS2_FAILENABLE = 0;
	
	public static String GATHEROSS2_FAILRESETLIST = "-1,-2";
	
	public static int GATHEROSS2_POOLSIZE = 100;
	
	public static ThreadPoolCommon GATHEROSS2_THREADPOOL = null;
	
	public static String GATHEROSS2_TABLENAME = "tab_gatherOSS2_record";
	
	
	public static List<HashMap<String, String>> ftpList = new ArrayList<HashMap<String, String>>();
	
	public static List<HashMap<String, String>> vlanList = new ArrayList<HashMap<String, String>>();
	
	public static HashMap<String, String> ftpMap = new HashMap<String, String>();
	
	public static HashMap<String, String> vlanMap = new HashMap<String, String>();
	
	public static HashMap<String, String> gatherMap = new HashMap<String, String>();
}
