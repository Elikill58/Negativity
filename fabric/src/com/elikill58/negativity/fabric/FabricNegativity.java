package com.elikill58.negativity.fabric;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.channel.GameChannelNegativityMessageEvent;
import com.elikill58.negativity.fabric.impl.entity.FabricEntityManager;
import com.elikill58.negativity.fabric.impl.entity.FabricPlayer;
import com.elikill58.negativity.fabric.listeners.CommandsExecutorManager;
import com.elikill58.negativity.fabric.listeners.PlayersListeners;
import com.elikill58.negativity.fabric.packets.NegativityPacketManager;
import com.elikill58.negativity.fabric.utils.Utils;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.Stats;
import com.elikill58.negativity.universal.account.NegativityAccount;
import com.elikill58.negativity.universal.account.NegativityAccountManager;
import com.elikill58.negativity.universal.ban.Ban;
import com.elikill58.negativity.universal.ban.BanManager;
import com.elikill58.negativity.universal.detections.Cheat.CheatHover;
import com.elikill58.negativity.universal.pluginMessages.AlertMessage;
import com.elikill58.negativity.universal.pluginMessages.NegativityMessagesManager;
import com.elikill58.negativity.universal.pluginMessages.ReportMessage;
import com.elikill58.negativity.universal.storage.account.NegativityAccountStorage;
import com.elikill58.negativity.universal.utils.UniversalUtils;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.PlayChannelHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
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
	public static Identifier negativityChannel = new Identifier(NegativityMessagesManager.CHANNEL_ID),
			fmlChannel = new Identifier("fml:hs"), bungeecordChannel = new Identifier("bungeecord");

	@Override
	public void onInitializeServer() {
		INSTANCE = this;

		this.configDir = Path.of("config", "Negativity");
		configDir.toFile().mkdirs();
		new File(configDir.toFile().getAbsolutePath() + File.separator + "user" + File.separator + "proof").mkdirs();

		Adapter.setAdapter(new FabricAdapter(this, LOGGER));
		BanManager.init();

		ServerLifecycleEvents.SERVER_STARTING.register(this::onGameStart);
		ServerLifecycleEvents.SERVER_STOPPING.register(this::onGameStop);
		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> loadCommands(dispatcher));

		PlayersListeners.register();
		ServerPlayConnectionEvents.DISCONNECT.register(this::onLeave);
		ServerPlayConnectionEvents.INIT.register(this::onAuth);

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
		Negativity.loadNegativity();
		packetManager = new NegativityPacketManager(this);

		ServerPlayNetworking.registerGlobalReceiver(fmlChannel, new FmlRawDataListener());
		ServerPlayNetworking.registerGlobalReceiver(negativityChannel, new ProxyCompanionListener());

		Stats.sendStartupStats(srv.getServerPort());
	}

	private void loadCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
		registerCommand("negativity", dispatcher, "neg", "n");
		reloadCommand("nmod", dispatcher, "nmod", "mod");
		reloadCommand("kick", dispatcher, "nkick", "kick");
		reloadCommand("lang", dispatcher, "nlang", "lang");
		reloadCommand("report", dispatcher, "nreport", "report", "repot");
		reloadCommand("ban", dispatcher, "nban", "negban", "ban");
		reloadCommand("unban", dispatcher, "nunban", "negunban", "unban");
		reloadCommand("chat.clear", dispatcher, "nclearchat", "clearchat");
		reloadCommand("chat.lock", dispatcher, "nlockchat", "lockchat");
	}

	private void reloadCommand(String configKey, CommandDispatcher<ServerCommandSource> dispatcher, String cmd,
			String... alias) {
		if ((configKey.endsWith("ban") ? BanManager.getBanConfig() : Adapter.getAdapter().getConfig())
				.getBoolean("commands." + configKey)) {
			registerCommand(cmd, dispatcher, alias);
		}
	}

	private void registerCommand(String cmd, CommandDispatcher<ServerCommandSource> dispatcher, String... alias) {
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

	public void onAuth(ServerPlayNetworkHandler e, MinecraftServer srv) {
		UUID playerId = e.getPlayer().getUuid();
		NegativityAccount account = NegativityAccount.get(playerId);
		Ban activeBan = BanManager.getActiveBan(playerId);
		if (activeBan != null) {
			String kickMsgKey;
			String formattedExpiration;
			if (activeBan.isDefinitive()) {
				kickMsgKey = "ban.kick_def";
				formattedExpiration = "definitively";
			} else {
				kickMsgKey = "ban.kick_time";
				LocalDateTime expirationDateTime = LocalDateTime
						.ofInstant(Instant.ofEpochMilli(activeBan.getExpirationTime()), ZoneId.systemDefault());
				formattedExpiration = UniversalUtils.GENERIC_DATE_TIME_FORMATTER.format(expirationDateTime);
			}
			e.disconnect(Messages.getMessage(account, kickMsgKey, "%reason%", activeBan.getReason(), "%time%",
					formattedExpiration, "%by%", activeBan.getBannedBy()));
			Adapter.getAdapter().getAccountManager().dispose(account.getPlayerId());
		}
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

	public static List<ServerPlayerEntity> getOnlinePlayers() {
		PlayerManager playerManager = getInstance().server.getPlayerManager();
		if (playerManager != null) {
			return playerManager.getPlayerList();
		}
		return Collections.emptyList();
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
