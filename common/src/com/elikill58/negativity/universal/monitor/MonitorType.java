package com.elikill58.negativity.universal.monitor;

import java.util.Arrays;
import java.util.List;

import com.elikill58.negativity.universal.monitor.cpu.CpuMonitor;

public class MonitorType<T extends MonitorManager> {

	public static List<MonitorType<? extends MonitorManager>> getMonitors() {
		return Arrays.asList(CPU);
	}
	
	public static final MonitorType<CpuMonitor> CPU = new MonitorType<>(new CpuMonitor());
	
	private final T monitor;
	
	private MonitorType(T monitor) {
		this.monitor = monitor;
	}
	
	public String getName() {
		return monitor.getName();
	}
	
	public T getMonitor() {
		return monitor;
	}
}
