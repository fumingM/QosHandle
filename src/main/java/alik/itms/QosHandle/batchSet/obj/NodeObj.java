package alik.itms.QosHandle.batchSet.obj;

/**
 * 设置节点的对象
 * @author Administrator
 *
 */
public class NodeObj {
	
	// 节点路径
	private String name;
	
	// 节点值
	private String value;
	
	private String type;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	
	public String getType()
	{
		return type;
	}

	
	public void setType(String type)
	{
		this.type = type;
	}
}
