package com.elikill58.negativity.sponge7;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.scheduler.Task;

import com.elikill58.negativity.universal.ScheduledTask;
import com.elikill58.negativity.universal.Scheduler;

public class SpongeScheduler implements Scheduler {
	
	private final Object plugin;
	
	public SpongeScheduler(Object plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public void runRepeating(Consumer<ScheduledTask> task, int delayTicks, int intervalTicks) {
		Task.builder().execute(spongeTask -> task.accept(new TaskWrapper(spongeTask))).delayTicks(delayTicks).intervalTicks(intervalTicks).submit(this.plugin);
	}
	
	@Override
	public ScheduledTask runRepeating(Runnable task, int delayTicks, int intervalTicks) {
		return new TaskWrapper(Task.builder().execute(task).delayTicks(delayTicks).intervalTicks(intervalTicks).submit(this.plugin));
	}
	
	@Override
	public ScheduledTask runRepeating(Runnable task, int intervalTicks, @Nullable String name) {
		Task.Builder builder = Task.builder().execute(task).intervalTicks(intervalTicks);
		if (name != null) {
			builder.name(name);
		}
		return new TaskWrapper(builder.submit(this.plugin));
	}
	
	@Override
	public ScheduledTask runDelayed(Runnable task, int delayTicks) {
		return new TaskWrapper(Task.builder().execute(task).delayTicks(delayTicks).submit(this.plugin));
	}
	
	@Override
	public ScheduledTask runRepeatingAsync(Runnable task, Duration delay, Duration interval, @Nullable String name) {
		Task.Builder builder = Task.builder().execute(task).async()
				.delay(delay.toMillis(), TimeUnit.MILLISECONDS)
				.interval(interval.toMillis(), TimeUnit.MILLISECONDS);
		if (name != null) {
			builder.name(name);
		}
		return new TaskWrapper(builder.submit(this.plugin));
	}
	
	private static class TaskWrapper implements ScheduledTask {
		
		private final Task task;
		
		private TaskWrapper(Task task) {
			this.task = task;
		}
		
		@Override
		public void cancel() {
			this.task.cancel();
		}
	}
}
