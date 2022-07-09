package com.elikill58.negativity.universal.monitor;

import java.util.ArrayList;
import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;

public abstract class MonitorMeasurement<T extends MonitorMeasurement<T>> implements Comparable<T> {

	/**
	 * Get header of result. Used for raw & cleaned result.<br>
	 * Should include description of the next result
	 * 
	 * @return the footer, or empty list
	 */
	public @NonNull List<String> getHeaderResult() {
		return new ArrayList<>();
	}
	
	/**
	 * Get all result lines, even if it doesn't concerned Negativity plugin.
	 * 
	 * @return all results lines
	 */
	public abstract @NonNull List<String> getRawResult();
	
	/**
	 * Get cleaned lines. It will return only which one is concerned by Negativity plugin
	 * 
	 * @return cleaned results lines
	 */
	public abstract @NonNull List<String> getCleanedResult();

	/**
	 * Get footer of result. Used for raw & cleaned result.<br>
	 * Should include conclusion and things like that.
	 * 
	 * @return the footer, or empty list
	 */
	public @NonNull List<String> getFooterResult() {
		return new ArrayList<>();
	}
}
