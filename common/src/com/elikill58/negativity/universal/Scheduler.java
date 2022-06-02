package com.elikill58.negativity.universal;

import java.time.Duration;
import java.util.function.Consumer;

import org.checkerframework.checker.nullness.qual.Nullable;

public interface Scheduler {
	
	void runRepeating(Consumer<ScheduledTask> task, int delayTicks, int intervalTicks);
	
	/**
	 * Run the given task each given ticks, after waiting a delay
	 * 
	 * @param task task to run
	 * @param delayTicks delay before starting task
	 * @param intervalTicks ticks between each task runned
	 * @return the running task
	 */
	ScheduledTask runRepeating(Runnable task, int delayTicks, int intervalTicks);
	
	default ScheduledTask runRepeating(Runnable task, int intervalTicks) {
		return runRepeating(task, intervalTicks, null);
	}
	
	ScheduledTask runRepeating(Runnable task, int intervalTicks, @Nullable String name);
	
	/**
	 * Run task after waiting given ticks
	 * 
	 * @param task task to run
	 * @param delayTicks ticks before running task
	 * @return the running task
	 */
	ScheduledTask runDelayed(Runnable task, int delayTicks);
	
	ScheduledTask runRepeatingAsync(Runnable task, Duration delay, Duration interval, @Nullable String name);
	
	static Scheduler getInstance() {
		return Adapter.getAdapter().getScheduler();
	}
}
