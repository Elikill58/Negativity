package com.elikill58.negativity.spigot.scheduler;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.checkerframework.checker.nullness.qual.Nullable;

import com.elikill58.negativity.api.entity.Entity;
import com.elikill58.negativity.universal.ScheduledTask;
import com.elikill58.negativity.universal.Scheduler;
import com.elikill58.negativity.universal.utils.ReflectionUtils;

public class FoliaScheduler implements Scheduler {

	private Plugin plugin;
	private Object globalRegionScheduler = null;
	private Object asyncScheduler = null;

	public FoliaScheduler(Plugin plugin) {
		this.plugin = plugin;
		this.globalRegionScheduler = ReflectionUtils.callStaticMethod(Bukkit.class, "getGlobalRegionScheduler");
		this.asyncScheduler = ReflectionUtils.callStaticMethod(Bukkit.class, "getAsyncScheduler");
	}

	@Override
	public void run(Runnable task) {
		try {
			Method method = globalRegionScheduler.getClass().getDeclaredMethod("run", Plugin.class, Consumer.class);
			new TaskWrapper(method.invoke(globalRegionScheduler, this.plugin, (Consumer<?>) (scheduledTask) -> task.run()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void runEntity(Entity entity, Runnable task) {
		try {
			Object scheduler = getEntityScheduler(entity);
			Method method = scheduler.getClass().getDeclaredMethod("run", Plugin.class, Consumer.class, Runnable.class);
			new TaskWrapper(method.invoke(scheduler, this.plugin, (Consumer<?>) (scheduledTask) -> task.run(), null));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void runRepeating(Consumer<ScheduledTask> task, int delayTicks, int intervalTicks) {
		try {
			Method method = globalRegionScheduler.getClass().getDeclaredMethod("runAtFixedRate", Plugin.class, Consumer.class, long.class, long.class);
			new TaskWrapper(method.invoke(globalRegionScheduler, this.plugin, (Consumer<?>) (scheduledTask) -> task.accept(new TaskWrapper(scheduledTask)), delayTicks, intervalTicks));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public ScheduledTask runRepeating(Runnable task, int delayTicks, int intervalTicks) {
		try {
			Method method = globalRegionScheduler.getClass().getDeclaredMethod("runAtFixedRate", Plugin.class, Consumer.class, long.class, long.class);
			return new TaskWrapper(method.invoke(globalRegionScheduler, this.plugin, (Consumer<?>) (scheduledTask) -> task.run(), delayTicks, intervalTicks));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public ScheduledTask runRepeating(Runnable task, int intervalTicks, @Nullable String name) {
		return runRepeating(task, 1, intervalTicks);
	}

	@Override
	public ScheduledTask runDelayed(Runnable task, int delayTicks) {
		try {
			Method method = globalRegionScheduler.getClass().getDeclaredMethod("runDelayed", Plugin.class, Consumer.class, long.class);
			return new TaskWrapper(method.invoke(globalRegionScheduler, this.plugin, (Consumer<?>) (scheduledTask) -> task.run(), delayTicks));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public ScheduledTask runEntityRepeating(Entity entity, Runnable task, int delayTicks, int intervalTicks) {
		try {
			Object scheduler = getEntityScheduler(entity);
			Method method = scheduler.getClass().getDeclaredMethod("runAtFixedRate", Plugin.class, Consumer.class, long.class, long.class);
			return new TaskWrapper(method.invoke(scheduler, this.plugin, (Consumer<?>) (scheduledTask) -> task.run(), delayTicks, intervalTicks));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public ScheduledTask runEntityDelayed(Entity entity, Runnable task, int delayTicks) {
		try {
			Object scheduler = getEntityScheduler(entity);
			Method method = scheduler.getClass().getDeclaredMethod("runDelayed", Plugin.class, Consumer.class, Runnable.class ,long.class);
			return new TaskWrapper(method.invoke(scheduler, this.plugin, (Consumer<?>) (scheduledTask) -> task.run(), null, delayTicks));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private Object getEntityScheduler(Entity entity) throws Exception {
		Object bukkitEntity = entity.getDefault();
		return org.bukkit.entity.Entity.class.getDeclaredMethod("getScheduler").invoke(bukkitEntity);
	}

	@Override
	public ScheduledTask runRepeatingAsync(Runnable task, Duration delay, Duration interval, @Nullable String name) {
		try {
			Method method = asyncScheduler.getClass().getDeclaredMethod("runAtFixedRate", Plugin.class, Consumer.class, long.class, long.class, TimeUnit.class);
			return new TaskWrapper(method.invoke(asyncScheduler, this.plugin, (Consumer<?>) (scheduledTask) -> task.run(), delay.toMillis(), interval.toMillis(), TimeUnit.MILLISECONDS));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static class TaskWrapper implements ScheduledTask {

		// type io.papermc.paper.threadedregions.scheduler.ScheduledTask
		private final Object scheduledTask;

		private TaskWrapper(Object scheduledTask) {
			this.scheduledTask = scheduledTask;
		}

		@Override
		public void cancel() {
			try {
				Method m = scheduledTask.getClass().getDeclaredMethod("cancel");
				m.setAccessible(true);
				m.invoke(scheduledTask);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
