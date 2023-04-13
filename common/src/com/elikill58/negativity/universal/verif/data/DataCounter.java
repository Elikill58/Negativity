package com.elikill58.negativity.universal.verif.data;

public abstract class DataCounter<T extends Number> {

	protected int amount;
	protected T total, min, max;
	
	public T getTotal() {
		return total;
	}
	
	/**
	 * Get the counter of the data
	 * 
	 * @return number of recorded data
	 */
	public int getSize() {
		return amount;
	}
	
	/**
	 * Add a new data
	 * 
	 * @param value the recored value
	 */
	public void add(T value) {
		amount++;
		if(total == null) {
			total = value;
			min = value;
			max = value;
		} else {
			internalAdd(value);
			internalManageMinMax(value);
		}
	}
	
	protected abstract void internalAdd(T value);
	
	protected abstract void internalManageMinMax(T value);
	
	/**
	 * Get the minimum of registered data
	 * 
	 * @return min
	 */
	public T getMin() {
		return min;
	}

	/**
	 * Get the maximum of registered data
	 * 
	 * @return max
	 */
	public T getMax() {
		return max;
	}
	
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
		return amount > 0;
	}
}
