package com.elikill58.negativity.velocity;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.elikill58.negativity.universal.ScheduledTask;
import com.elikill58.negativity.universal.Scheduler;

public class VelocityScheduler implements Scheduler {

	private final VelocityNegativity plugin;
	private final com.velocitypowered.api.scheduler.Scheduler scheduler;

	public VelocityScheduler(VelocityNegativity plugin) {
		this.plugin = plugin;
		this.scheduler = plugin.getServer().getScheduler();
	}

	@Override
	public void runRepeating(Consumer<ScheduledTask> task, int delayTicks, int intervalTicks) {
		throw new UnsupportedOperationException("Scheduler runRepeating is not support on velocity yet.");
	}

	@Override
	public ScheduledTask runRepeating(Runnable task, int delayTicks, int intervalTicks) {
		return new VelocityRunnableWrapper(
				scheduler.buildTask(plugin, task).delay(toMs(delayTicks), TimeUnit.MILLISECONDS)
						.repeat(toMs(intervalTicks), TimeUnit.MILLISECONDS).schedule());
	}

	@Override
	public ScheduledTask runRepeating(Runnable task, int intervalTicks, @Nullable String name) {
		return new VelocityRunnableWrapper(
				scheduler.buildTask(plugin, task).repeat(toMs(intervalTicks), TimeUnit.MILLISECONDS).schedule());
	}

	@Override
	public ScheduledTask runDelayed(Runnable task, int delayTicks) {
		return new VelocityRunnableWrapper(
				scheduler.buildTask(plugin, task).delay(toMs(delayTicks), TimeUnit.MILLISECONDS).schedule());
	}

	@Override
	public ScheduledTask runRepeatingAsync(Runnable task, Duration delay, Duration interval, @Nullable String name) {
		return new VelocityRunnableWrapper(
				scheduler.buildTask(plugin, task)
						.delay(delay.toMillis(), TimeUnit.MILLISECONDS)
						.repeat(interval.toMillis(), TimeUnit.MILLISECONDS)
						.schedule());
	}
	
	private long toMs(int ticks) {
		return (ticks * 1000) / 20;
	}

	private static class VelocityRunnableWrapper implements ScheduledTask {

		private final com.velocitypowered.api.scheduler.ScheduledTask task;

		private VelocityRunnableWrapper(com.velocitypowered.api.scheduler.ScheduledTask task) {
			this.task = task;
		}

		@Override
		public void cancel() {
			this.task.cancel();
		}
	}
}
