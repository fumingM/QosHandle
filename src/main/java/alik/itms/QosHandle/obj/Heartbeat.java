package alik.itms.QosHandle.obj;

/**
 * 心跳实体
 * @author jiafh
 *
 */
public class Heartbeat {

	// 是否使用心跳
	private int enab = -1;
	
	// IP
	private String ip = null;
	
	// 端口
	private String port = null;
	
	// 心跳周期
	private int cycle = 5;
	
	// 客户端名称
	private String nodeName;
	
	public int getEnab() {
		return enab;
	}

	public void setEnab(int enab) {
		this.enab = enab;
	}

	public int getCycle() {
		return cycle;
	}

	public void setCycle(int cycle) {
		this.cycle = cycle;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getPort()
	{
		return port;
	}

	public void setPort(String port)
	{
		this.port = port;
	}

	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}
}
