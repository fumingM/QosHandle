package alik.itms.QosHandle.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.linkage.commons.thread.ThreadPoolCommon;



/**
 * 
 * @author fanjm (Ailk No.)
 * @version 1.0
 * @since 2019年4月9日
 * @category ailk.itms.preprocess.util
 * @copyright Ailk NBS-Network Mgt. RD Dept.
 *
 */
public class ThreadMonitor extends Thread
{
	/** log */
	private static final Logger log = LoggerFactory
			.getLogger(ThreadMonitor.class);
	
	ThreadPoolCommon threadPoolCommon = null;
	int monitorSleepTime = 0;
	
	public ThreadMonitor(ThreadPoolCommon threadPoolCommon, int monitorSleepTime)
	{
		super();
		this.threadPoolCommon = threadPoolCommon;
		this.monitorSleepTime = monitorSleepTime;
	}

	@Override
	public void run(){
		while (true){
			if (null != threadPoolCommon && monitorSleepTime>0){
					log.error("ThreadPool:" + getThreadPoolMonitorInfo() + "\n");
			}
			try{
				Thread.sleep(monitorSleepTime * 1000L);
			}
			catch (InterruptedException e){
				log.error("{}", e.getMessage());
				Thread.currentThread().interrupt();
			}
		}
		 
	}
	
	/**
	 * 
	 * @param threadPoolCommon
	 * @param type 队列类型，1 接收dev.inform线程，  2处理业务线程， 3 连接acs线程
	 * @return
	 */
	public String getThreadPoolMonitorInfo() {
		if (null == threadPoolCommon) {
			return null;
		}
		int activeCount = threadPoolCommon.getActiveCount();
		long completedTaskCount = threadPoolCommon.getCompletedTaskCount();
		long taskCount = threadPoolCommon.getTaskCount();
		long maximumPoolSize = threadPoolCommon.getMaximumPoolSize();
		long queueCount = taskCount - completedTaskCount - activeCount;
		
		StringBuffer buffer = new StringBuffer();
		buffer.append("POOL-INFO:")
			  .append("Active=").append(activeCount).append(";")
			  .append("Queue=").append(queueCount).append(";")
			  .append("Completed=").append(completedTaskCount).append(";")
			  .append("AllTask=").append(taskCount).append(";")
			  .append("PoolSize=").append(maximumPoolSize).append("");
		
		return buffer.toString();
	}

	public long getqueueCount() {
		int activeCount = threadPoolCommon.getActiveCount();
		long completedTaskCount = threadPoolCommon.getCompletedTaskCount();
		long taskCount = threadPoolCommon.getTaskCount();
		long queueCount = taskCount - completedTaskCount - activeCount;
		return queueCount;
	}
	
	public ThreadPoolCommon getThreadPoolCommon()
	{
		return threadPoolCommon;
	}

	
	public void setThreadPoolCommon(ThreadPoolCommon threadPoolCommon)
	{
		this.threadPoolCommon = threadPoolCommon;
	}

	
	public int getMonitorSleepTime()
	{
		return monitorSleepTime;
	}

	
	public void setMonitorSleepTime(int monitorSleepTime)
	{
		this.monitorSleepTime = monitorSleepTime;
	}
}
