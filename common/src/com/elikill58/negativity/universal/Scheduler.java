package com.elikill58.negativity.universal;

import java.util.function.Consumer;

import org.checkerframework.checker.nullness.qual.Nullable;

public interface Scheduler {
	
	void runRepeating(Consumer<ScheduledTask> task, int delayTicks, int intervalTicks);
	
	ScheduledTask runRepeating(Runnable task, int delayTicks, int intervalTicks);
	
	default ScheduledTask runRepeating(Runnable task, int intervalTicks) {
		return runRepeating(task, intervalTicks, null);
	}
	
	ScheduledTask runRepeating(Runnable task, int intervalTicks, @Nullable String name);
	
	ScheduledTask runDelayed(Runnable task, int delayTicks);
	
	static Scheduler getInstance() {
		return Adapter.getAdapter().getScheduler();
	}
}
