package com.elikill58.negativity.fabric;

import java.util.concurrent.Callable;
import java.util.function.Function;

import com.elikill58.negativity.api.commands.CommandSender;

/**
 * Shared entry points between Negativity and the version-specific NegativityFabric mods.
 * <p>
 * This class must not reference any Minecraft type in its signatures: it is shipped once in
 * Negativity and loaded on every Fabric runtime (intermediary names up to 1.21.11, Mojang names
 * from 26.1). NegativityFabric 26.x reaches {@link #load} reflectively, and {@code Class#getMethod}
 * resolves the types of every declared method, so a Minecraft type anywhere here would fail with
 * {@link NoClassDefFoundError} on the other namespace.
 */
public class GlobalFabricNegativity {

	private static Callable<Integer> getTicks;
	private static Function<Object, CommandSender> getExecutorFunction;

	/**
	 * @param <S> the command source type of the running Minecraft version
	 */
	@SuppressWarnings("unchecked")
	public static <S> void load(Callable<Integer> getTicks, Function<S, CommandSender> getExecutorFunction) {
		GlobalFabricNegativity.getTicks = getTicks;
		GlobalFabricNegativity.getExecutorFunction = source -> getExecutorFunction.apply((S) source);
	}

	public static int getTicks() {
		try {
			return getTicks.call();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * @param source the Minecraft command source (kept as {@link Object} to stay mapping-independent)
	 */
	public static CommandSender getExecutor(Object source) {
		return getExecutorFunction.apply(source);
	}
}
