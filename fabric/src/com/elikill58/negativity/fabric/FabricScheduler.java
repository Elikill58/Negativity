package com.elikill58.negativity.fabric;

import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.elikill58.negativity.universal.ScheduledTask;
import com.elikill58.negativity.universal.Scheduler;

public class FabricScheduler implements Scheduler {
	
    private final ScheduledExecutorService service;
    private HashMap<Long, TaskWrapper> tasksWrapper = new HashMap<>();
	
	public FabricScheduler(FabricNegativity plugin) {
		this.service = Executors.newScheduledThreadPool(1);
	}
	
	@Override
	public void runRepeating(Consumer<ScheduledTask> task, int delayTicks, int intervalTicks) {
		long id = new Random().nextLong();
		tasksWrapper.put(id, new TaskWrapper(service.scheduleAtFixedRate(() -> {
			TaskWrapper wrapper = tasksWrapper.get(id);
			task.accept(wrapper);
			if(wrapper.getTask().isCancelled() || wrapper.getTask().isDone())
				tasksWrapper.remove(id);
		}, Scheduler.tickToMilliseconds(delayTicks), Scheduler.tickToMilliseconds(intervalTicks), TimeUnit.SECONDS)));
	}
	
	@Override
	public ScheduledTask runRepeating(Runnable task, int delayTicks, int intervalTicks) {
		return new TaskWrapper(service.scheduleAtFixedRate(task, Scheduler.tickToMilliseconds(intervalTicks), Scheduler.tickToMilliseconds(intervalTicks), TimeUnit.SECONDS));
	}
	
	@Override
	public ScheduledTask runRepeating(Runnable task, int intervalTicks, @Nullable String name) {
		return new TaskWrapper(service.scheduleAtFixedRate(task, Scheduler.tickToMilliseconds(intervalTicks), Scheduler.tickToMilliseconds(intervalTicks), TimeUnit.SECONDS));
	}
	
	@Override
	public ScheduledTask runRepeatingAsync(Runnable task, int intervalTicks, @Nullable String name) {
		return new TaskWrapper(service.scheduleAtFixedRate(task, Scheduler.tickToMilliseconds(intervalTicks), Scheduler.tickToMilliseconds(intervalTicks), TimeUnit.SECONDS));
	}
	
	@Override
	public ScheduledTask runDelayed(Runnable task, int delayTicks) {
		return new TaskWrapper(service.schedule(task, Scheduler.tickToMilliseconds(delayTicks), TimeUnit.SECONDS));
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

		public ScheduledFuture<?> getTask() {
			return task;
		}
	}
}
