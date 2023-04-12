package com.elikill58.negativity.universal.monitor.cpu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.checkerframework.checker.nullness.qual.NonNull;

import com.elikill58.negativity.api.colors.ChatColor;
import com.elikill58.negativity.universal.detections.Special;
import com.elikill58.negativity.universal.detections.keys.IDetectionKey;
import com.elikill58.negativity.universal.monitor.MonitorManager;
import com.elikill58.negativity.universal.monitor.MonitorMeasure;
import com.elikill58.negativity.universal.monitor.cpu.function.CheckCpuMeasure;
import com.elikill58.negativity.universal.monitor.cpu.function.CpuMeasure;
import com.elikill58.negativity.universal.monitor.cpu.function.EventCpuMeasure;
import com.elikill58.negativity.universal.monitor.cpu.function.SpecialCpuMeasure;

public class CpuMonitor extends MonitorManager {

	private ConcurrentHashMap<IDetectionKey<?>, CheckCpuMeasure> detectionMeasures = new ConcurrentHashMap<>();
	private ConcurrentHashMap<Special, SpecialCpuMeasure> specialMeasures = new ConcurrentHashMap<>();
	private ConcurrentHashMap<Class<?>, EventCpuMeasure> eventMeasures = new ConcurrentHashMap<>();
	
	public CpuMonitor() {
		super("CPU");
	}

	public ConcurrentHashMap<IDetectionKey<?>, CheckCpuMeasure> getDetectionMeasures() {
		return detectionMeasures;
	}
	
	public ConcurrentHashMap<Special, SpecialCpuMeasure> getSpecialMeasures() {
		return specialMeasures;
	}

	public ConcurrentHashMap<Class<?>, EventCpuMeasure> getEventMeasures() {
		return eventMeasures;
	}
	
	public CheckCpuMeasure getMeasureForDetection(IDetectionKey<?> key) {
		return detectionMeasures.computeIfAbsent(key, CheckCpuMeasure::new);
	}
	
	public CpuMeasure<?> getMeasureFor(Object obj) {
		if(obj instanceof Special)
			return specialMeasures.computeIfAbsent((Special) obj, SpecialCpuMeasure::new);
		return eventMeasures.computeIfAbsent(obj.getClass(), EventCpuMeasure::new);
	}
	
	@Override
	public @NonNull String getDescription() {
		return ChatColor.YELLOW + "Result of monitor for " + getName() + " (Unit: microseconds):";
	}
	
	@Override
	public List<String> getResult() {
		List<String> result = new ArrayList<>();
		getDetectionMeasures().values().stream().map(CpuMeasure::getResult).forEach(result::addAll);
		getSpecialMeasures().values().stream().map(CpuMeasure::getResult).forEach(result::addAll);
		getEventMeasures().values().stream().map(CpuMeasure::getResult).forEach(result::addAll);
		return result;
	}
	
	@Override
	public boolean canParsePerCheat() {
		return true;
	}

	@Override
	public HashMap<IDetectionKey<?>, List<String>> getResultPerCheat() {
		HashMap<IDetectionKey<?>, List<String>> results = new HashMap<>();
		getDetectionMeasures().forEach((key, measure) -> results.put(key, measure.getResultPer()));
		getSpecialMeasures().forEach((key, measure) -> results.put(key.getKey(), measure.getResultPer()));
		return results;
	}
	
	@Override
	public List<MonitorMeasure> getFullConfig() {
		List<MonitorMeasure> result = new ArrayList<>();
		getDetectionMeasures().forEach((key, cpu) -> result.addAll(cpu.getResults("detection.")));
		getSpecialMeasures().forEach((key, cpu) -> result.addAll(cpu.getResults("special.")));
		getEventMeasures().forEach((key, cpu) -> result.addAll(cpu.getResults("event.")));
		return result;
	}
}
