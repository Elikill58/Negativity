package com.elikill58.negativity.universal.monitor.cpu;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.logger.LoggerAdapter;

/**
 * Based on the project https://github.com/sk89q/WarmRoast by sk89q
 */
public class CpuMonitorTask extends TimerTask {

	public static final long SAMPLE_INTERVAL = 100L;
	public static final long SAMPLE_DELAY = TimeUnit.SECONDS.toMillis(1) / 2;
	private static final int MAX_DEPTH = 25;

	private final ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
	private final LoggerAdapter logger;
	private final long threadId;

	private CpuMeasurement rootNode;
	private int samples;

	public CpuMonitorTask(LoggerAdapter logger, long threadId) {
		this.logger = logger;
		this.threadId = threadId;
	}

	public synchronized CpuMeasurement getRootSample() {
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
					rootNode = new CpuMeasurement(id, rootClass, rootMethod);
				}

				rootNode.onMeasurement(stackTrace, 0, SAMPLE_INTERVAL);
			}
		}
	}

	@Deprecated
	public String paste() {
		try {
			File folder = new File(Adapter.getAdapter().getDataFolder(), "lag");
			folder.mkdirs();
			File file = new File(folder, UUID.randomUUID().toString() + ".txt");
			file.createNewFile();
			BufferedWriter handle = Files.newBufferedWriter(file.toPath(), StandardOpenOption.APPEND);
			handle.write("CLEANED");
			handle.write(getCleanedResult().toString());
			handle.write("ALL");
			handle.write(getRawResult().toString());
			handle.close();
		} catch (IOException ex) {
			logger.printError("Failed to save monitoring data", ex);
		}

		return null;
	}
	
	@Override
	public String toString() {
		return "MonitorTask{threadId=" + threadId + "}";
	}

	public List<String> getCleanedResult() {
		ThreadInfo threadInfo = threadMXBean.getThreadInfo(threadId, MAX_DEPTH);

		List<String> result = new ArrayList<>();

		synchronized (this) {
			result.add(threadInfo.getThreadName() + " " + rootNode.getTotalTime() + "ms");

			rootNode.writeCleanedString(result, 1);
		}

		return result;
	}
	
	public List<String> getRawResult() {
		ThreadInfo threadInfo = threadMXBean.getThreadInfo(threadId, MAX_DEPTH);

		List<String> result = new ArrayList<>();

		synchronized (this) {
			result.add(threadInfo.getThreadName() + " " + rootNode.getTotalTime() + "ms");

			rootNode.writeRawString(result, 1);
		}

		return result;
	}
}