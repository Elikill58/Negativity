package com.elikill58.negativity.spigot;

import java.time.Duration;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.checkerframework.checker.nullness.qual.Nullable;

import com.elikill58.negativity.universal.ScheduledTask;
import com.elikill58.negativity.universal.Scheduler;

public class SpigotScheduler implements Scheduler {
	
	private Plugin plugin;
	
	public SpigotScheduler(Plugin plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public void runRepeating(Consumer<ScheduledTask> task, int delayTicks, int intervalTicks) {
		new BukkitRunnable() {
			private final ScheduledTask wrapper = new BukkitRunnableWrapper(this);
			@Override
			public void run() {
				task.accept(this.wrapper);
			}
		}.runTaskTimer(this.plugin, delayTicks, intervalTicks);
	}
	
	@Override
	public ScheduledTask runRepeating(Runnable task, int delayTicks, int intervalTicks) {
		return new TaskWrapper(Bukkit.getScheduler().runTaskTimer(this.plugin, task, delayTicks, intervalTicks));
	}
	
	@Override
	public ScheduledTask runRepeating(Runnable task, int intervalTicks, @Nullable String name) {
		return new TaskWrapper(Bukkit.getScheduler().runTaskTimer(this.plugin, task, 0, intervalTicks));
	}
	
	@Override
	public ScheduledTask runDelayed(Runnable task, int delayTicks) {
		return new TaskWrapper(Bukkit.getScheduler().runTaskLater(this.plugin, task, delayTicks));
	}
	
	@Override
	public ScheduledTask runRepeatingAsync(Runnable task, Duration delay, Duration interval, @Nullable String name) {
		long delayTicks = (delay.toMillis() * 20) / 1000;
		long intervalTicks = (interval.toMillis() * 20) / 1000;
		return new TaskWrapper(Bukkit.getScheduler().runTaskTimerAsynchronously(this.plugin, task, delayTicks, intervalTicks));
	}
	
	private static class TaskWrapper implements ScheduledTask {
		
		private final BukkitTask task;
		
		private TaskWrapper(BukkitTask task) {
			this.task = task;
		}
		
		@Override
		public void cancel() {
			this.task.cancel();
		}
	}
	
	private static class BukkitRunnableWrapper implements ScheduledTask {
		
		private final BukkitRunnable task;
		
		private BukkitRunnableWrapper(BukkitRunnable task) {
			this.task = task;
		}
		
		@Override
		public void cancel() {
			this.task.cancel();
		}
	}
}
