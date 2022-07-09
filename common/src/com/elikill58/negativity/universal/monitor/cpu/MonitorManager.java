package com.elikill58.negativity.universal.monitor.cpu;

import java.util.Timer;

import com.elikill58.negativity.universal.Adapter;

public class MonitorManager {

	private static MonitorTask monitorTask;
	public static MonitorTask getMonitorTask() {
		return monitorTask;
	}
	
	public static void load() {
		if(monitorTask != null)
			monitorTask.cancel();
		Timer timer = new Timer("Negativity-Monitor");
        monitorTask = new MonitorTask(Adapter.getAdapter().getLogger(), Thread.currentThread().getId());
        timer.scheduleAtFixedRate(monitorTask, MonitorTask.SAMPLE_DELAY, MonitorTask.SAMPLE_INTERVAL);
	}
}
