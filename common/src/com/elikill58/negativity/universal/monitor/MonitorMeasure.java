package com.elikill58.negativity.universal.monitor;

import com.elikill58.negativity.api.yaml.Configuration;

public class MonitorMeasure {

	private final String key;
	private final long duration, min, max;
	private final double percent;
	
	public MonitorMeasure(String key, long duration, long min, long max, double percent) {
		this.key = key;
		this.duration = duration;
		this.min = min;
		this.max = max;
		this.percent = percent;
	}
	
	/**
	 * Get the key of result
	 * 
	 * @return the key
	 */
	public String getKey() {
		return key;
	}
	
	/**
	 * Get the duration of the monitor result
	 * 
	 * @return duration of result
	 */
	public long getDuration() {
		return duration;
	}
	
	/**
	 * Get the max duration of result
	 * 
	 * @return max result
	 */
	public long getMax() {
		return max;
	}
	
	/**
	 * Get the min duration of result
	 * 
	 * @return min result
	 */
	public long getMin() {
		return min;
	}
	
	/**
	 * Get percent of used compared to total
	 * 
	 * @return percent of total
	 */
	public double getPercent() {
		return percent;
	}
	
	/**
	 * Check if this result is empty
	 * 
	 * @return true if is empty
	 */
	public boolean isEmpty() {
		return duration == 0;
	}
	
	/**
	 * Check if this measure is very low. Can be used to don't show empty values
	 * 
	 * @return true if very low
	 */
	public boolean isVeryLow() {
		return getDuration() <= 3 || String.format("%.2f", getPercent()).equalsIgnoreCase("0,00");
	}
	
	public void save(Configuration config) {
		config.set(key + ".result", toString());
		config.set(key + ".duration", getDuration());
		config.set(key + ".min", getMin());
		config.set(key + ".max", getMax());
		config.set(key + ".percent", getPercent());
	}
	
	@Override
	public String toString() {
		return getDuration() + "μs (Min/Max: " + getMin() + "/" + getMax() + ") " + String.format("%.2f", getPercent()) + "%";
	}
}
