package com.elikill58.negativity.universal.monitor.cpu;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import com.elikill58.negativity.api.colors.ChatColor;
import com.elikill58.negativity.universal.detections.keys.CheatKeys;

/**
 * Based on the project https://github.com/sk89q/WarmRoast by sk89q
 */
public class CpuMonitorTask extends TimerTask {

	public static final long SAMPLE_INTERVAL = 100L;
	public static final long SAMPLE_DELAY = TimeUnit.SECONDS.toMillis(1) / 2;
	private static final int MAX_DEPTH = 25;

	private final ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
	private final long threadId;

	private CpuMeasurement rootNode;
	private int samples;

	public CpuMonitorTask(long threadId) {
		this.threadId = threadId;
	}

	public synchronized CpuMeasurement getRootNode() {
		return rootNode;
	}

	public synchronized int getSamples() {
		return samples;
	}

	@Override
	public void run() {
		ThreadInfo threadInfo = threadMXBean.getThreadInfo(threadId, MAX_DEPTH);
		StackTraceElement[] stackTrace = threadInfo.getStackTrace();
		if (stackTrace.length > 0) {
			StackTraceElement rootElement = stackTrace[stackTrace.length - 1];
			synchronized (this) {
				samples++;

				if (rootNode == null) {
					String rootClass = rootElement.getClassName();
					String rootMethod = rootElement.getMethodName();

					String id = rootClass + '.' + rootMethod;
					rootNode = new CpuMeasurement(id, rootClass, rootMethod, this);
				}

				rootNode.onMeasurement(stackTrace, 0, SAMPLE_INTERVAL);
			}
		}
	}

	@Override
	public String toString() {
		return "MonitorTask{threadId=" + threadId + "}";
	}
	
	public List<String> getHeaderResult() {
		ThreadInfo threadInfo = threadMXBean.getThreadInfo(threadId, MAX_DEPTH);

		List<String> result = new ArrayList<>();

		synchronized (this) {
			result.add(ChatColor.GRAY + threadInfo.getThreadName() + " " + rootNode.getTotalTime() + "ms");
		}

		return result;
	}

	public List<String> getCleanedResult() {
		List<String> result = new ArrayList<>();
		synchronized (this) {
			rootNode.writeCleanedString(result, 1);
		}
		return result;
	}
	
	public List<String> getRawResult() {
		List<String> result = new ArrayList<>();
		synchronized (this) {
			rootNode.writeRawString(result, 1);
		}
		return result;
	}

	public HashMap<CheatKeys, List<String>> getResultPerCheat() {
		HashMap<CheatKeys, List<String>> map = new HashMap<>();
		synchronized (this) {
			rootNode.writeResultPerCheat(map);
		}
		return map;
	}
}