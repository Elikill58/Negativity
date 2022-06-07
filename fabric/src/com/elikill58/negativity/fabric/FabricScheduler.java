package com.elikill58.negativity.fabric;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.ScheduledTask;
import com.elikill58.negativity.universal.Scheduler;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

public class FabricScheduler implements Scheduler {
	
	private final ScheduledExecutorService service;
	private final List<SyncTask> tickTasks = new ArrayList<>();
	private final List<SyncTask> expiredTickTasks = new ArrayList<>();
	private final ReentrantReadWriteLock tickTasksLock = new ReentrantReadWriteLock();
	
	public FabricScheduler() {
		ThreadFactory threadFactory = new ThreadFactoryBuilder()
				.setNameFormat("negativity-scheduler-pool-%d")
				.build();
		this.service = Executors.newScheduledThreadPool(1, threadFactory);
	}
	
	@Override
	public void runRepeating(Consumer<ScheduledTask> task, int delayTicks, int intervalTicks) {
		schedule(task, delayTicks, intervalTicks, null);
	}
	
	@Override
	public ScheduledTask runRepeating(Runnable task, int delayTicks, int intervalTicks) {
		return schedule(new RunnableWrapper(task), delayTicks, intervalTicks, null);
	}
	
	@Override
	public ScheduledTask runRepeating(Runnable task, int intervalTicks, @Nullable String name) {
		return schedule(new RunnableWrapper(task), intervalTicks, intervalTicks, name);
	}
	
	@Override
	public ScheduledTask runRepeatingAsync(Runnable task, Duration delay, Duration interval, @Nullable String name) {
		return new TaskWrapper(service.scheduleAtFixedRate(new SafeRunnable(task), delay.toMillis(), interval.toMillis(), TimeUnit.MILLISECONDS));
	}
	
	@Override
	public ScheduledTask runDelayed(Runnable task, int delayTicks) {
		return schedule(new RunnableWrapper(task), delayTicks, -1, null);
	}
	
	private ScheduledTask schedule(Consumer<ScheduledTask> task, int delayTicks, int intervalTicks, @Nullable String name) {
		try {
			this.tickTasksLock.writeLock().lock();
			SyncTask syncTask = new SyncTask(task, delayTicks, intervalTicks, FabricNegativity.getInstance().getServer().getTicks(), name);
			this.tickTasks.add(syncTask);
			return syncTask;
		} finally {
			this.tickTasksLock.writeLock().unlock();
		}
	}
	
	public void tick(int tick) {
		try {
			this.tickTasksLock.readLock().lock();
			for (SyncTask tickTask : this.tickTasks) {
				if (tickTask.tick(tick)) {
					this.expiredTickTasks.add(tickTask);
				}
			}
		} finally {
			this.tickTasksLock.readLock().unlock();
		}
		
		if (!this.expiredTickTasks.isEmpty()) {
			try {
				this.tickTasksLock.writeLock().lock();
				this.tickTasks.removeAll(this.expiredTickTasks);
				this.expiredTickTasks.clear();
			} finally {
				this.tickTasksLock.writeLock().unlock();
			}
		}
	}
	
	public void shutdown() {
		this.service.shutdown();
		try {
			if (!this.service.awaitTermination(5, TimeUnit.SECONDS)) {
				Adapter.getAdapter().getLogger().warn("Failed to shutdown scheduler in 5 seconds. Forcing shutdown.");
				List<Runnable> skippedTasks = this.service.shutdownNow();
				Adapter.getAdapter().getLogger().warn("Skipping tasks " + skippedTasks);
			}
		} catch (InterruptedException ignore) {
		}
		Adapter.getAdapter().getLogger().info("Shutdown complete");
	}
	
	private static class TaskWrapper implements ScheduledTask {
		
		private final ScheduledFuture<?> task;
		
		private TaskWrapper(ScheduledFuture<?> task) {
			this.task = task;
		}
		
		@Override
		public void cancel() {
			this.task.cancel(false);
		}

	}
	
	private static final class SyncTask implements ScheduledTask {
		
		private final Consumer<ScheduledTask> task;
		private final int tickDelay;
		private final int ticksInterval;
		private final int startingTick;
		private final @Nullable String name;
		
		private boolean cancelled = false;
		
		private SyncTask(Consumer<ScheduledTask> task, int tickDelay, int ticksInterval, int startingTick, @Nullable String name) {
			this.task = task;
			this.tickDelay = tickDelay;
			this.ticksInterval = ticksInterval;
			this.startingTick = startingTick;
			this.name = name;
		}
		
		public boolean tick(int tick) {
			if (this.cancelled) {
				return true;
			}
			
			int ticksSinceStart = tick - startingTick;
			if (tickDelay > 0 && ticksSinceStart < tickDelay) {
				return false;
			}
			
			if (ticksInterval <= 0) {
				this.task.accept(this);
				return true;
			}
			
			if (ticksSinceStart % ticksInterval == 0) {
				this.task.accept(this);
			}
			
			return false;
		}
		
		@Override
		public void cancel() {
			this.cancelled = true;
		}
		
		@Override
		public String toString() {
			return "SyncTask{" +
					"task=" + task +
					", tickDelay=" + tickDelay +
					", ticksInterval=" + ticksInterval +
					", startingTick=" + startingTick +
					", name='" + name + '\'' +
					", cancelled=" + cancelled +
					'}';
		}
	}
	
	private record RunnableWrapper(Runnable task) implements Consumer<ScheduledTask> {
		
		@Override
		public void accept(ScheduledTask scheduledTask) {
			this.task.run();
		}
	}
	
	private record SafeRunnable(Runnable task) implements Runnable {
		
		@Override
		public void run() {
			try {
				this.task.run();
			} catch (Throwable t) {
				Adapter.getAdapter().getLogger().error("[FabricScheduler] An uncaught error occurred in task " + this.task + ": " + t);
				t.printStackTrace();
			}
		}
	}
}
