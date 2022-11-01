package com.elikill58.negativity.fabric;

import java.util.concurrent.Callable;
import java.util.function.Function;

import com.elikill58.negativity.api.commands.CommandSender;

import net.minecraft.server.command.ServerCommandSource;

public class GlobalFabricNegativity {

	private static Callable<Integer> getTicks;
	private static Function<ServerCommandSource, CommandSender> getExecutorFunction;
	
	public static void load(Callable<Integer> getTicks, Function<ServerCommandSource, CommandSender> getExecutorFunction) {
		GlobalFabricNegativity.getTicks = getTicks;
		GlobalFabricNegativity.getExecutorFunction = getExecutorFunction;
	}
	
	public static int getTicks() {
		try {
			return getTicks.call();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	public static CommandSender getExecutor(ServerCommandSource source) {
		return getExecutorFunction.apply(source);
	}
}
