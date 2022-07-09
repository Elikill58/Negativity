package com.elikill58.negativity.universal.monitor.cpu;

import java.util.Timer;

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
	public void disable() {
		if(cpuMonitorTask != null)
			cpuMonitorTask.cancel();
		if(cpuMonitorTimer != null)
			cpuMonitorTimer.cancel();
	}
}
