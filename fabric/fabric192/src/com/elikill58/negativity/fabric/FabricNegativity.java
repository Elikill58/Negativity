package com.elikill58.negativity.fabric;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.channel.GameChannelNegativityMessageEvent;
import com.elikill58.negativity.api.yaml.Configuration;
import com.elikill58.negativity.fabric.commands.CommandsExecutorManager;
import com.elikill58.negativity.fabric.impl.entity.FabricEntityManager;
import com.elikill58.negativity.fabric.impl.entity.FabricPlayer;
import com.elikill58.negativity.fabric.impl.location.FabricWorld;
import com.elikill58.negativity.fabric.listeners.PlayersListeners;
import com.elikill58.negativity.fabric.packets.NegativityPacketManager;
import com.elikill58.negativity.fabric.utils.Utils;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.Stats;
import com.elikill58.negativity.universal.account.NegativityAccountManager;
import com.elikill58.negativity.universal.ban.BanManager;
import com.elikill58.negativity.universal.detections.Cheat.CheatHover;
import com.elikill58.negativity.universal.pluginMessages.AlertMessage;
import com.elikill58.negativity.universal.pluginMessages.NegativityMessagesManager;
import com.elikill58.negativity.universal.pluginMessages.ReportMessage;
import com.elikill58.negativity.universal.storage.account.NegativityAccountStorage;
import com.elikill58.negativity.universal.warn.WarnManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.PlayChannelHandler;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class FabricNegativity implements DedicatedServerModInitializer {

	public static FabricNegativity INSTANCE;
	private static final Logger LOGGER = LoggerFactory.getLogger("negativity");

	private Path configDir;
	private MinecraftServer server;
	private NegativityPacketManager packetManager;
	private CommandDispatcher<ServerCommandSource> dispatcher;
	private boolean commandLoaded = false;
	public static Identifier negativityChannel = new Identifier(NegativityMessagesManager.CHANNEL_ID),
			fmlChannel = new Identifier("fml:hs"), bungeecordChannel = new Identifier("bungeecord");

	@Override
	public void onInitializeServer() {
		INSTANCE = this;

		this.configDir = Path.of("config", "Negativity");
		configDir.toFile().mkdirs();
		new File(configDir.toFile().getAbsolutePath() + File.separator + "user" + File.separator + "proof").mkdirs();

		Adapter.setAdapter(new FabricAdapter(this, LOGGER));

		ServerLifecycleEvents.SERVER_STARTING.register(this::onGameStart);
		ServerLifecycleEvents.SERVER_STOPPING.register(this::onGameStop);
		CommandRegistrationCallback.EVENT.register(this::loadCommands);

		PlayersListeners.register();
		ServerPlayConnectionEvents.DISCONNECT.register(this::onLeave);

		NegativityAccountStorage.setDefaultStorage("file");

		// LOGGER.info("Negativity v" + plugin.getVersion().get() + " loaded.");
	}

	public void onGameStop(MinecraftServer srv) {
		if (FabricAdapter.getAdapter().getScheduler() instanceof FabricScheduler scheduler) {
			Adapter.getAdapter().getLogger().info("Shutting down scheduler");
			try {
				scheduler.shutdown();
			} catch (Exception e) {
				Adapter.getAdapter().getLogger().error("Error occurred when shutting down scheduler: " + e.getMessage());
				e.printStackTrace();
			}
		}
		Negativity.closeNegativity();
	}

	public void onGameStart(MinecraftServer srv) {
		this.server = srv;
		GlobalFabricNegativity.load(srv, FabricEntityManager::getExecutor, FabricWorld::new);
		Negativity.loadNegativity();
		packetManager = new NegativityPacketManager(this);

		ServerPlayNetworking.registerGlobalReceiver(fmlChannel, new FmlRawDataListener());
		ServerPlayNetworking.registerGlobalReceiver(negativityChannel, new ProxyCompanionListener());

		if(dispatcher != null && !commandLoaded) {
			loadCommandsFinal();
		}
		
		Stats.sendStartupStats(srv.getServerPort());
	}

	private void loadCommands(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registry, CommandManager.RegistrationEnvironment env) {
		this.dispatcher = dispatcher;
		if(server == null) // not loaded yet
			return;
		loadCommandsFinal();
	}
	
	private void loadCommandsFinal() {
		registerCommand("negativity", "neg", "n");
		reloadCommand("nmod", "nmod", "mod");
		reloadCommand("kick", "nkick", "kick");
		reloadCommand("lang", "nlang", "lang");
		reloadCommand("report", "nreport", "report", "repot");
		reloadCommand("ban", "nban", "negban", "ban");
		reloadCommand("unban", "nunban", "negunban", "unban");
		reloadCommand("chat.clear", "nclearchat", "clearchat");
		reloadCommand("chat.lock", "nlockchat", "lockchat");
		reloadCommand("warn", "nwarn", "warn");
		commandLoaded = true;
	}

	private void reloadCommand(String configKey, String cmd,
			String... alias) {
		Configuration conf = Adapter.getAdapter().getConfig();
		if(configKey.endsWith("ban"))
			conf = BanManager.getBanConfig();
		if(configKey.endsWith("warn"))
			conf = WarnManager.getWarnConfig();
		if (conf.getBoolean("commands." + configKey)) {
			registerCommand(cmd, alias);
		}
	}

	private void registerCommand(String cmd, String... alias) {
		CommandsExecutorManager executor = new CommandsExecutorManager(cmd);
		LiteralCommandNode<ServerCommandSource> node = dispatcher
				.register(CommandManager.literal(cmd)
					.executes(executor)
					.then(CommandManager.argument("args", StringArgumentType.greedyString()).suggests(executor).executes(executor)));

		for (String sub : alias)
			dispatcher.register(CommandManager.literal(sub).redirect(node));
	}

	public MinecraftServer getServer() {
		return server;
	}

	public void onLeave(ServerPlayNetworkHandler e, MinecraftServer srv) {
		Adapter.getAdapter().getScheduler().runDelayed(() -> {
			UUID playerId = e.getPlayer().getUuid();
			NegativityPlayer.removeFromCache(playerId);
			NegativityAccountManager accountManager = Adapter.getAdapter().getAccountManager();
			accountManager.save(playerId);
			accountManager.dispose(playerId);
		}, 5);
	}

	public static FabricNegativity getInstance() {
		return INSTANCE;
	}

	public Path getDataFolder() {
		return configDir;
	}

	public NegativityPacketManager getPacketManager() {
		return packetManager;
	}

	public static void sendAlertMessage(Player p, String cheatName, int reliability, int ping, CheatHover hover,
			int alertsCount) {
		try {
			PacketByteBuf buf = PacketByteBufs.create();
			buf.writeBytes(NegativityMessagesManager
					.writeMessage(new AlertMessage(p.getUniqueId(), cheatName, reliability, ping, hover, alertsCount)));
			ServerPlayNetworking.send((ServerPlayerEntity) p.getDefault(), negativityChannel, buf);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void sendReportMessage(Player p, String reportMsg, String nameReported) {
		try {
			PacketByteBuf buf = PacketByteBufs.create();
			buf.writeBytes(
					NegativityMessagesManager.writeMessage(new ReportMessage(nameReported, reportMsg, p.getName())));
			ServerPlayNetworking.send((ServerPlayerEntity) p.getDefault(), negativityChannel, buf);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void sendPluginMessage(byte[] rawMessage) {
		ServerPlayerEntity player = Utils.getFirstOnlinePlayer();
		if (player != null) {
			PacketByteBuf buf = PacketByteBufs.create();
			buf.writeBytes(rawMessage);
			ServerPlayNetworking.send(player, negativityChannel, buf);
		} else {
			Adapter.getAdapter().getLogger()
					.error("Could not send plugin message to proxy because there are no player online.");
		}
	}

	private static class FmlRawDataListener implements PlayChannelHandler {

		@Override
		public void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler,
				PacketByteBuf buf, PacketSender responseSender) {
			byte[] rawData = buf.readBytes(buf.capacity()).array();
			HashMap<String, String> playerMods = NegativityPlayer.getNegativityPlayer(player.getUuid(),
					() -> new FabricPlayer(player)).mods;
			playerMods.clear();
			playerMods.putAll(Utils.getModsNameVersionFromMessage(new String(rawData, StandardCharsets.UTF_8)));
		}
	}

	private static class ProxyCompanionListener implements PlayChannelHandler {

		@Override
		public void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler,
				PacketByteBuf buf, PacketSender responseSender) {
			byte[] rawData = buf.readBytes(buf.capacity()).array();
			com.elikill58.negativity.api.events.EventManager
					.callEvent(new GameChannelNegativityMessageEvent(FabricEntityManager.getPlayer(player), rawData));
		}
	}
}
