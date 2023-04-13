package com.elikill58.negativity.universal.monitor.cpu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import com.elikill58.negativity.api.colors.ChatColor;
import com.elikill58.negativity.universal.detections.Special;
import com.elikill58.negativity.universal.detections.keys.IDetectionKey;
import com.elikill58.negativity.universal.monitor.MonitorManager;
import com.elikill58.negativity.universal.monitor.MonitorMeasure;

public class CpuMonitor extends MonitorManager {

	private ConcurrentHashMap<IDetectionKey<?>, CpuMeasure> detectionMeasures = new ConcurrentHashMap<>();
	private ConcurrentHashMap<String, CpuMeasure> measures = new ConcurrentHashMap<>();
	
	public CpuMonitor() {
		super("CPU");
	}
	
	public ConcurrentHashMap<IDetectionKey<?>, CpuMeasure> getDetectionMeasures() {
		return detectionMeasures;
	}
	
	public CpuMeasure getMeasureForDetection(IDetectionKey<?> key) {
		return detectionMeasures.computeIfAbsent(key, CpuMeasure::new);
	}
	
	public ConcurrentHashMap<String, CpuMeasure> getMeasures() {
		return measures;
	}
	
	public @Nullable CpuMeasure getMeasure(Object o) {
		if(!isEnabled())
			return null;
		return getMeasure((String) (o instanceof Special ? ((Special) o).getKey().getLowerKey() : o.getClass().getSimpleName()));
	}
	
	public @Nullable CpuMeasure getMeasure(String s) {
		if(!isEnabled())
			return null;
		return measures.computeIfAbsent(s, CpuMeasure::new);
	}
	
	@Override
	public @NonNull String getDescription() {
		return ChatColor.YELLOW + "Result of monitor for " + getName() + " (Unit: microseconds):";
	}
	
	@Override
	public List<String> getResult() {
		List<String> result = new ArrayList<>();
		getMeasures().values().stream().map(CpuMeasure::getResult).forEach(result::addAll);
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
		return results;
	}
	
	@Override
	public List<MonitorMeasure> getFullConfig() {
		List<MonitorMeasure> result = new ArrayList<>();
		getDetectionMeasures().forEach((key, cpu) -> result.addAll(cpu.getResults("detection.")));
		getMeasures().forEach((key, cpu) -> {
			result.addAll(cpu.getResults(key.contains(".") ? "special." : "event."));
		});
		return result;
	}
}
