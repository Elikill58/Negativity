package com.elikill58.negativity.fabric;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import com.elikill58.negativity.api.commands.CommandSender;
import com.elikill58.negativity.api.location.World;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class GlobalFabricNegativity {

	private static MinecraftServer server;
	private static Function<ServerCommandSource, CommandSender> getExecutorFunction;
	private static Function<net.minecraft.world.World, World> worldCreator;
	
	public static void load(MinecraftServer server, Function<ServerCommandSource, CommandSender> getExecutorFunction, Function<net.minecraft.world.World, World> worldCreator) {
		GlobalFabricNegativity.server = server;
		GlobalFabricNegativity.getExecutorFunction = getExecutorFunction;
		GlobalFabricNegativity.worldCreator = worldCreator;
	}
	
	public static MinecraftServer getServer() {
		return server;
	}

	public static List<ServerPlayerEntity> getOnlinePlayers() {
		PlayerManager playerManager = server.getPlayerManager();
		if (playerManager != null) {
			return playerManager.getPlayerList();
		}
		return Collections.emptyList();
	}
	
	public static CommandSender getExecutor(ServerCommandSource source) {
		return getExecutorFunction.apply(source);
	}
	
	public static World createWorld(net.minecraft.world.World w) {
		return worldCreator.apply(w);
	}
}
