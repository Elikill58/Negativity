package com.elikill58.negativity.universal.monitor.cpu;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.elikill58.negativity.api.colors.ChatColor;
import com.elikill58.negativity.universal.detections.keys.IDetectionKey;
import com.elikill58.negativity.universal.monitor.MonitorMeasure;
import com.elikill58.negativity.universal.verif.data.LongDataCounter;

public class CpuMeasure {

	protected static long totalTime = 0;

	protected final ConcurrentHashMap<String, LongDataCounter> datas = new ConcurrentHashMap<>();
	protected final LongDataCounter globalData = new LongDataCounter();
	protected final String measureName;
	protected final long timeBegin = System.nanoTime();

	public CpuMeasure(String measureName) {
		this.measureName = measureName;
	}

	public CpuMeasure(IDetectionKey<?> key) {
		this.measureName = key.getLowerKey();
	}

	public @Nullable String printData(String name, LongDataCounter data) {
		double l = ((double) data.getTotal() / ((System.nanoTime() - timeBegin) / 1000)) * 100;
		if (l <= 0.01)
			return null;
		return ChatColor.GREEN + (name.isEmpty() ? "" : name + " ") + ChatColor.YELLOW + data.getAverage() + "μs " + ChatColor.GRAY + "(Min/Max: " + data.getMin() + "/" + data.getMax()
				+ ") " + ChatColor.AQUA + (l <= 0.01 ? ">0%" : String.format("%.2f", l) + "%");
	}

	public LongDataCounter getGlobalData() {
		return globalData;
	}

	public ConcurrentHashMap<String, LongDataCounter> getDatas() {
		return datas;
	}

	public void add(String o, long time) {
		totalTime += time;
		globalData.add(time);
		datas.computeIfAbsent(o, a -> new LongDataCounter()).add(time);
	}

	public String getName() {
		return measureName;
	}

	public List<String> getResult() {
		List<String> result = new ArrayList<>();
		String printed = printData(getName(), globalData);
		if (printed != null)
			result.add(printed);
		return result;
	}

	public List<String> getResultPer() {
		List<String> result = new ArrayList<>();
		datas.forEach((check, data) -> {
			if (data.has()) {
				String printed = printData(check, data);
				if (printed != null)
					result.add(printed);
			}
		});
		return result;
	}

	public List<MonitorMeasure> getResults(String key) {
		List<MonitorMeasure> result = new ArrayList<>();
		datas.forEach((check, data) -> {
			if (data.has()) {
				result.add(new MonitorMeasure(key + check, data.getAverage(), data.getMin(), data.getMax(), ((double) data.getTotal() / ((System.nanoTime() - timeBegin) / 1000)) * 100));
			}
		});
		result.removeIf(MonitorMeasure::isVeryLow);
		return result;
	}
}
