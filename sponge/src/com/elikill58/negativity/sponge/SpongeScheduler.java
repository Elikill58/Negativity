package com.elikill58.negativity.sponge;

import java.time.Duration;
import java.util.function.Consumer;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.util.Ticks;
import org.spongepowered.plugin.PluginContainer;

import com.elikill58.negativity.universal.ScheduledTask;
import com.elikill58.negativity.universal.Scheduler;

public class SpongeScheduler implements Scheduler {
	
	private final PluginContainer plugin;
	
	public SpongeScheduler(PluginContainer plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public void runRepeating(Consumer<ScheduledTask> task, int delayTicks, int intervalTicks) {
		Sponge.server().scheduler().submit(Task.builder()
			.plugin(this.plugin)
			.execute(spongeTask -> task.accept(new SpongeTaskWrapper(spongeTask)))
			.delay(Ticks.of(delayTicks))
			.interval(Ticks.of(intervalTicks))
			.build());
	}
	
	@Override
	public ScheduledTask runRepeating(Runnable task, int delayTicks, int intervalTicks) {
		return new SpongeTaskWrapper(Sponge.server().scheduler().submit(Task.builder()
			.plugin(this.plugin)
			.execute(task)
			.delay(Ticks.of(delayTicks))
			.interval(Ticks.of(intervalTicks))
			.build()));
	}
	
	@Override
	public ScheduledTask runRepeating(Runnable task, int intervalTicks, @Nullable String name) {
		Task.Builder builder = Task.builder()
			.plugin(this.plugin)
			.execute(task)
			.interval(Ticks.of(intervalTicks));
		return new SpongeTaskWrapper(Sponge.server().scheduler().submit(builder.build()));
	}
	
	@Override
	public ScheduledTask runDelayed(Runnable task, int delayTicks) {
		return new SpongeTaskWrapper(Sponge.server().scheduler().submit(Task.builder()
			.plugin(this.plugin)
			.execute(task)
			.delay(Ticks.of(delayTicks)).build()));
	}

	@Override
	public ScheduledTask runRepeatingAsync(Runnable task, Duration delay, Duration interval, @Nullable String name) {
		Task spongeTask = Task.builder()
				.plugin(this.plugin)
				.execute(task)
				.delay(delay)
				.interval(interval)
				.build();
		return new SpongeTaskWrapper(Sponge.server().scheduler().submit(spongeTask, name));
	}
	
	private static class SpongeTaskWrapper implements ScheduledTask {
		
		private final org.spongepowered.api.scheduler.ScheduledTask task;
		
		private SpongeTaskWrapper(org.spongepowered.api.scheduler.ScheduledTask task) {
			this.task = task;
		}
		
		@Override
		public void cancel() {
			this.task.cancel();
		}
	}
}
