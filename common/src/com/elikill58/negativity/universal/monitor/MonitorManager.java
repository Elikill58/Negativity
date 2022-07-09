package com.elikill58.negativity.universal.monitor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.elikill58.negativity.universal.monitor.cpu.CpuMonitorManager;

public abstract class MonitorManager {

	private static List<MonitorManager> monitors = new ArrayList<>();
	public static List<MonitorManager> getMonitors() {
		return monitors;
	}
	
	public static void load() {
		monitors.forEach(MonitorManager::disable);
		monitors = new ArrayList<>(Arrays.asList(new CpuMonitorManager()));
		monitors.forEach(MonitorManager::enable);
	}
	
	/**
	 * Enable actual monitor
	 */
	public abstract void enable();
	
	/**
	 * Get the name of what the monitor is checking
	 * 
	 * @return the name
	 */
	public abstract String getName();

	/**
	 * Disable actual monitor.
	 */
	public abstract void disable();

}
