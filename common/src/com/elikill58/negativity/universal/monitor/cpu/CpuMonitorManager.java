package com.elikill58.negativity.universal.monitor.cpu;

import java.util.List;
import java.util.Timer;

import org.checkerframework.checker.nullness.qual.NonNull;

import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.monitor.MonitorManager;

public class CpuMonitorManager extends MonitorManager {
	
	private CpuMonitorTask cpuMonitorTask;
	private Timer cpuMonitorTimer;

	@Override
	public void enable() {
		if(cpuMonitorTask != null)
			cpuMonitorTask.cancel();
		if(cpuMonitorTimer != null)
			cpuMonitorTimer.cancel();
        cpuMonitorTask = new CpuMonitorTask(Adapter.getAdapter().getLogger(), Thread.currentThread().getId());
		cpuMonitorTimer = new Timer("Negativity-Monitor");
        cpuMonitorTimer.scheduleAtFixedRate(cpuMonitorTask, CpuMonitorTask.SAMPLE_DELAY, CpuMonitorTask.SAMPLE_INTERVAL);
	}
	
	@Override
	public String getName() {
		return "CPU";
	}
	
	public CpuMonitorTask getMonitorTask() {
		return cpuMonitorTask;
	}
	
	public Timer getMonitorTimer() {
		return cpuMonitorTimer;
	}

	@Override
	public @NonNull List<String> getHeaderResult() {
		return getMonitorTask().getHeaderResult();
	}
	
	@Override
	public @NonNull List<String> getCleanedResult() {
		return getMonitorTask().getCleanedResult();
	}

	@Override
	public @NonNull List<String> getRawResult() {
		return getMonitorTask().getRawResult();
	}

	@Override
	public void disable() {
		if(cpuMonitorTask != null)
			cpuMonitorTask.cancel();
		if(cpuMonitorTimer != null)
			cpuMonitorTimer.cancel();
	}
}
