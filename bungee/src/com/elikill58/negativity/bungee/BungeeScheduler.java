package com.elikill58.negativity.bungee;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.elikill58.negativity.universal.ScheduledTask;
import com.elikill58.negativity.universal.Scheduler;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.TaskScheduler;

public class BungeeScheduler implements Scheduler {

	private Plugin plugin;
	private TaskScheduler scheduler;

	public BungeeScheduler(Plugin plugin) {
		this.plugin = plugin;
		this.scheduler = plugin.getProxy().getScheduler();
	}

	@Override
	public void runRepeating(Consumer<ScheduledTask> task, int delayTicks, int intervalTicks) {
		throw new UnsupportedOperationException("Scheduler runRepeating is not support on bungeecord yet.");
	}

	@Override
	public ScheduledTask runRepeating(Runnable task, int delayTicks, int intervalTicks) {
		return new BungeeRunnableWrapper(
				scheduler.schedule(plugin, task, toMs(delayTicks), toMs(intervalTicks), TimeUnit.MILLISECONDS));
	}

	@Override
	public ScheduledTask runRepeating(Runnable task, int intervalTicks, @Nullable String name) {
		long ms = toMs(intervalTicks);
		return new BungeeRunnableWrapper(scheduler.schedule(plugin, task, ms, ms, TimeUnit.MILLISECONDS));
	}

	@Override
	public ScheduledTask runDelayed(Runnable task, int delayTicks) {
		return new BungeeRunnableWrapper(scheduler.schedule(plugin, task, toMs(delayTicks), TimeUnit.MILLISECONDS));
	}

	@Override
	public ScheduledTask runRepeatingAsync(Runnable task, Duration delay, Duration interval, @Nullable String name) {
		return new BungeeRunnableWrapper(scheduler.schedule(plugin, task, delay.toMillis(), interval.toMillis(), TimeUnit.MILLISECONDS));
	}

	private long toMs(int ticks) {
		return (ticks * 1000) / 20;
	}

	private static class BungeeRunnableWrapper implements ScheduledTask {

		private final net.md_5.bungee.api.scheduler.ScheduledTask task;

		private BungeeRunnableWrapper(net.md_5.bungee.api.scheduler.ScheduledTask task) {
			this.task = task;
		}

		@Override
		public void cancel() {
			this.task.cancel();
		}
	}
}
