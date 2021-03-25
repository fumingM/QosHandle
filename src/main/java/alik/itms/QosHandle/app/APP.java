package alik.itms.QosHandle.app;

import java.util.List;

import alik.itms.QosHandle.batchSet.BatchSetApp;
import alik.itms.QosHandle.common.Global;
import alik.itms.QosHandle.gatherOSS2.GatherOSS2App;
import alik.itms.QosHandle.gatherVlan.GatherVlanApp;

import com.linkage.commons.jms.obj.ServMessageConfig;
import com.linkage.commons.xml.XML;

/**
 * 模块启动类
 * @author jiafh
 *
 */
public class APP {
	
	public static void main(String[] args) {
		
		// 初始化公共配置项
		try {
			
			Global.G_HomePath = System.getProperty("user.dir") + "/";
			Global.G_ConfPath = Global.G_HomePath + "../conf/";
			
			AppInitBIO appInitBIO = new AppInitBIO();
			appInitBIO.init();
			
			// 初始化批量配置固定节点功能配置项
			if(1 == Global.G_BATCHSETENABLE){
				new BatchSetApp().batchSetAppStart();
			}
			
			// 批量采集组播VLAN节点
			if(1 == Global.G_BATCHGATHERVLAN){
				new GatherVlanApp().gatherVlanAppStart();
			}
			
			// 批量采集组播VLAN节点
			if(1 == Global.G_BATCHGATHEROSS2){
				new GatherOSS2App().gatherOSS2AppStart();
			}
			
			// 批量修改设备超级密码
			/*if(1 == Global.G_SUPERPASSWDENABLE){
				new SuperPasswdApp().superPasswdAppStart();
			}*/
			
			// 初始化监听线程
			XML xml = new XML(Global.G_ConfPath + "config.xml");
			//采集模块部署时不配置servMessageConfig相关参数即可 （代码不必注释，BatchSet还需要使用）
			List<ServMessageConfig> servMessageConfigList = MessageUtil.getServMessageConfig(xml,Global.MQ_POOl_MAP);
			//List<ServMessageConfig> servMessageConfigList = MessageUtil.getServMessageConfig(new XML("E:\\AsiaWork\\workspace\\ailk-itms-QosHandle\\conf\\config.xml"),Global.MQ_POOl_MAP);
			if(null != servMessageConfigList){
				if(servMessageConfigList.isEmpty()){
					System.out.println("isEmpty");
				}
				else{
					System.out.println("not isEmpty");
				}
			}
			MessageUtil.mqListenerList(servMessageConfigList, Global.MQ_POOl_MAP);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
}
