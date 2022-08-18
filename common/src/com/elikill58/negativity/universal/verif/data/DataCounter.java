package com.elikill58.negativity.universal.verif.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class DataCounter<T> {

	protected final List<T> list = Collections.synchronizedList(new ArrayList<>());
	
	public abstract T getTotal();
	
	/**
	 * Get all data
	 * 
	 * @return all data
	 */
	public List<T> getList(){
		return list;
	}
	
	/**
	 * Get the counter of the data
	 * 
	 * @return number of recorded data
	 */
	public int getSize() {
		return list.size();
	}
	
	/**
	 * Add a new data
	 * 
	 * @param value the recored value
	 */
	public void add(T value) {
		list.add(value);
	}

	/**
	 * Get the minimum of registered data
	 * 
	 * @return min
	 */
	public abstract T getMin();

	/**
	 * Get the maximum of registered data
	 * 
	 * @return max
	 */
	public abstract T getMax();
	
	/**
	 * Get the average of the data
	 * 
	 * @return the data average
	 */
	public abstract T getAverage();

	/**
	 * Check if has something
	 * 
	 * @return true if at least one value have been recorded
	 */
	public boolean has() {
		return !list.isEmpty();
	}
}
