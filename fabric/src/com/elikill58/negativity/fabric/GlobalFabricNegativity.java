package com.elikill58.negativity.fabric;

import java.util.concurrent.Callable;
import java.util.function.Function;

import com.elikill58.negativity.api.commands.CommandSender;
import com.elikill58.negativity.api.entity.Player;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class GlobalFabricNegativity {

	private static Callable<Integer> getTicks;
	private static Function<ServerCommandSource, CommandSender> getExecutorFunction;
	private static Function<ServerPlayerEntity, Player> getPlayerFunction;
	
	@Deprecated
	public static void load(Callable<Integer> getTicks, Function<ServerCommandSource, CommandSender> getExecutorFunction) {
		GlobalFabricNegativity.getTicks = getTicks;
		GlobalFabricNegativity.getExecutorFunction = getExecutorFunction;
	}
	
	public static void load(Callable<Integer> getTicks, Function<ServerCommandSource, CommandSender> getExecutorFunction, Function<ServerPlayerEntity, Player> getPlayerFunction) {
		GlobalFabricNegativity.getTicks = getTicks;
		GlobalFabricNegativity.getExecutorFunction = getExecutorFunction;
		GlobalFabricNegativity.getPlayerFunction = getPlayerFunction;
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
	
	public static Player getPlayer(ServerPlayerEntity source) {
		return getPlayerFunction.apply(source);
	}
}
