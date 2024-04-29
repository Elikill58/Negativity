package com.elikill58.negativity.minestom;

import java.time.Duration;
import java.util.function.Consumer;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.elikill58.negativity.universal.ScheduledTask;
import com.elikill58.negativity.universal.Scheduler;

import net.minestom.server.MinecraftServer;
import net.minestom.server.timer.ExecutionType;
import net.minestom.server.timer.SchedulerManager;
import net.minestom.server.timer.Task;
import net.minestom.server.timer.TaskSchedule;

public class MinestomScheduler implements Scheduler {
	
	private final SchedulerManager sh = MinecraftServer.getSchedulerManager();
	
	@Override
	public void run(Runnable task) {
		sh.scheduleNextTick(task);
	}
	
	@Override
	public void runRepeating(Consumer<ScheduledTask> task, int delayTicks, int intervalTicks) {
		MinestomNegativity.getInstance().getLogger().error("MinestomScheduler#runRepeating isn't implemented yet.");
	}
	
	@Override
	public ScheduledTask runRepeating(Runnable task, int delayTicks, int intervalTicks) {
		return new TaskWrapper(sh.scheduleTask(task, TaskSchedule.tick(delayTicks), TaskSchedule.tick(intervalTicks), ExecutionType.SYNC));
	}
	
	@Override
	public ScheduledTask runRepeating(Runnable task, int intervalTicks, @Nullable String name) {
		return new TaskWrapper(sh.scheduleTask(task, TaskSchedule.tick(intervalTicks), TaskSchedule.tick(intervalTicks), ExecutionType.SYNC));
	}
	
	@Override
	public ScheduledTask runRepeatingAsync(Runnable task, Duration delay, Duration interval, @Nullable String name) {
		return new TaskWrapper(sh.scheduleTask(task, TaskSchedule.duration(delay), TaskSchedule.duration(interval), ExecutionType.ASYNC));
	}
	
	@Override
	public ScheduledTask runDelayed(Runnable task, int delayTicks) {
		return new TaskWrapper(sh.scheduleTask(task, TaskSchedule.tick(delayTicks), TaskSchedule.stop(), ExecutionType.ASYNC));
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
