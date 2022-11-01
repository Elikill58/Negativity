package com.elikill58.negativity.sponge7;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.spongepowered.api.Platform.Type;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandManager;
import org.spongepowered.api.command.CommandMapping;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.EventManager;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartingServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.network.ChannelBinding.RawDataChannel;
import org.spongepowered.api.network.ChannelBuf;
import org.spongepowered.api.network.ChannelRegistrar;
import org.spongepowered.api.network.PlayerConnection;
import org.spongepowered.api.network.RawDataListener;
import org.spongepowered.api.network.RemoteConnection;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.channel.MessageReceiver;
import org.spongepowered.api.text.format.TextColors;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.events.channel.GameChannelNegativityMessageEvent;
import com.elikill58.negativity.api.yaml.Configuration;
import com.elikill58.negativity.sponge7.impl.entity.SpongeEntityManager;
import com.elikill58.negativity.sponge7.impl.entity.SpongePlayer;
import com.elikill58.negativity.sponge7.listeners.BlockListeners;
import com.elikill58.negativity.sponge7.listeners.CommandsExecutorManager;
import com.elikill58.negativity.sponge7.listeners.CommandsListeners;
import com.elikill58.negativity.sponge7.listeners.EntityListeners;
import com.elikill58.negativity.sponge7.listeners.InventoryListeners;
import com.elikill58.negativity.sponge7.listeners.PacketListeners;
import com.elikill58.negativity.sponge7.listeners.PlayersListeners;
import com.elikill58.negativity.sponge7.utils.Utils;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.Stats;
import com.elikill58.negativity.universal.account.NegativityAccountManager;
import com.elikill58.negativity.universal.ban.BanManager;
import com.elikill58.negativity.universal.detections.Cheat;
import com.elikill58.negativity.universal.detections.Cheat.CheatHover;
import com.elikill58.negativity.universal.pluginMessages.AlertMessage;
import com.elikill58.negativity.universal.pluginMessages.NegativityMessagesManager;
import com.elikill58.negativity.universal.pluginMessages.ReportMessage;
import com.elikill58.negativity.universal.storage.account.NegativityAccountStorage;
import com.elikill58.negativity.universal.warn.WarnManager;
import com.google.inject.Inject;

@Plugin(id = "negativity")
public class SpongeNegativity {

	public static SpongeNegativity INSTANCE;

	@Inject
	private PluginContainer plugin;
	@Inject
	@ConfigDir(sharedRoot = false)
	private Path configDir;
	public static RawDataChannel channel = null, fmlChannel = null, bungeecordChannel = null;

	private final Map<String, CommandMapping> reloadableCommands = new HashMap<>();

	public PluginContainer getContainer() {
		return plugin;
	}

	@Listener
	public void onPreInit(GamePreInitializationEvent event) {
		INSTANCE = this;

		new File(configDir.toFile().getAbsolutePath() + File.separator + "user" + File.separator + "proof").mkdirs();

		Adapter.setAdapter(new SpongeAdapter(this));
		Negativity.loadNegativity();

		EventManager eventManager = Sponge.getEventManager();
		eventManager.registerListeners(this, new BlockListeners());
		eventManager.registerListeners(this, new EntityListeners());
		eventManager.registerListeners(this, new InventoryListeners());
		eventManager.registerListeners(this, new PlayersListeners());
		eventManager.registerListeners(this, new PacketListeners());
		eventManager.registerListeners(this, new CommandsListeners());

		NegativityAccountStorage.setDefaultStorage("file");

		plugin.getLogger().info("Negativity v" + plugin.getVersion().get() + " loaded.");
	}

	@Listener
	public void onGameStop(GameStoppingServerEvent e) {
		Negativity.closeNegativity();
	}

	@Listener
	public void onGameStart(GameStartingServerEvent e) {
		try {
			Class.forName("net.minecraftforge.fml.common.network.handshake.NetworkDispatcher");
			SpongeForgeSupport.isOnSpongeForge = true;
		} catch (ClassNotFoundException e1) {
			SpongeForgeSupport.isOnSpongeForge = false;
		}

		loadCommands(false);

		ChannelRegistrar channelRegistrar = Sponge.getChannelRegistrar();
		channel = channelRegistrar.createRawChannel(this, NegativityMessagesManager.CHANNEL_ID);
		channel.addListener(new ProxyCompanionListener());
		if (channelRegistrar.isChannelAvailable("FML|HS")) {
			fmlChannel = channelRegistrar.getOrCreateRaw(this, "FML|HS");
			fmlChannel.addListener(new FmlRawDataListener());
		}
		bungeecordChannel = channelRegistrar.getOrCreateRaw(this, "BungeeCord");

		Stats.sendStartupStats(Sponge.getServer().getBoundAddress().map(InetSocketAddress::getPort).orElse(-1));
	}

	public void reloadCommands() {
		loadCommands(true);
	}

	private void loadCommands(boolean reload) {
		CommandManager cmd = Sponge.getCommandManager();

		if (!reload) {
			cmd.register(this, new CommandsExecutorManager("negativity"), "negativity", "neg", "n");
		}

		reloadCommand("mod", cmd, () -> new CommandsExecutorManager("nmod"), "nmod", "mod");
		reloadCommand("kick", cmd, () -> new CommandsExecutorManager("nkick"), "nkick", "kick");
		reloadCommand("lang", cmd, () -> new CommandsExecutorManager("nlang"), "nlang", "lang");
		reloadCommand("report", cmd, () -> new CommandsExecutorManager("nreport"), "nreport", "report", "repot");
		reloadCommand("ban", cmd, () -> new CommandsExecutorManager("nban"), "nban", "negban", "ban");
		reloadCommand("unban", cmd, () -> new CommandsExecutorManager("nunban"), "nunban", "negunban", "unban");
		reloadCommand("chat.clear", cmd, () -> new CommandsExecutorManager("nclearchat"), "nclearchat", "clearchat");
		reloadCommand("chat.lock", cmd, () -> new CommandsExecutorManager("nlockchat"), "nlockchat", "lockchat");
		reloadCommand("warn", cmd, () -> new CommandsExecutorManager("nwarn"), "nwarn", "warn");
	}

	private void reloadCommand(String configKey, CommandManager manager, Supplier<CommandCallable> command,
			String... aliases) {
		Configuration conf = Adapter.getAdapter().getConfig();
		if(configKey.endsWith("ban"))
			conf = BanManager.getBanConfig();
		if(configKey.endsWith("warn"))
			conf = WarnManager.getWarnConfig();
		reloadCommand(configKey, conf.getBoolean("commands." + configKey),
				manager, command, aliases);
	}

	private void reloadCommand(String mappingKey, boolean enabled, CommandManager manager,
			Supplier<CommandCallable> command, String... aliases) {
		if (enabled) {
			if (!reloadableCommands.containsKey(mappingKey)) {
				manager.register(this, command.get(), aliases)
						.ifPresent(mapping -> reloadableCommands.put(mappingKey, mapping));
			}
		} else {
			CommandMapping mapping = reloadableCommands.remove(mappingKey);
			if (mapping != null) {
				manager.removeMapping(mapping);
			}
		}
	}

	@Listener
	public void onGameReload(GameReloadEvent event) {
		Adapter.getAdapter().reload();
	}

	@Listener
	public void onLeave(ClientConnectionEvent.Disconnect e, @First Player p) {
		Task.builder().delayTicks(5).execute(() -> {
			NegativityPlayer.removeFromCache(p.getUniqueId());
			NegativityAccountManager accountManager = Adapter.getAdapter().getAccountManager();
			UUID playerId = p.getUniqueId();
			accountManager.save(playerId);
			accountManager.dispose(playerId);
		}).submit(this);
	}

	public static SpongeNegativity getInstance() {
		return INSTANCE;
	}

	public static Text createAlertText(Player suspect, Cheat cheat, String hoverProof, int ping, int pendingAlertsCount,
			String messageKey, int reliability, MessageReceiver receiver) {
		return Text
				.builder(Messages.getStringMessage(receiver, messageKey, "%name%", suspect.getName(), "%cheat%",
						cheat.getName(), "%reliability%", String.valueOf(reliability), "%nb%",
						String.valueOf(pendingAlertsCount)))
				.onClick(TextActions.runCommand("/negativity " + suspect.getName()))
				.onHover(TextActions.showText(Text.of(
						Messages.getStringMessage(receiver, "negativity.alert_hover", "%reliability%",
								String.valueOf(reliability), "%ping%", String.valueOf(ping)),
						TextColors.RESET, (hoverProof.isEmpty() ? "" : "\n\n" + hoverProof))))
				.build();
	}

	public Path getDataFolder() {
		return configDir;
	}

	public Logger getLogger() {
		return plugin.getLogger();
	}

	public static void sendAlertMessage(Player p, String cheatName, int reliability, int ping, CheatHover hover,
			int alertsCount) {
		channel.sendTo(p, (payload) -> {
			try {
				AlertMessage message = new AlertMessage(p.getUniqueId(), cheatName, reliability, ping, hover,
						alertsCount);
				payload.writeBytes(NegativityMessagesManager.writeMessage(message));
			} catch (IOException e) {
				SpongeNegativity.getInstance().getLogger().error("Could not send alert message to the proxy.", e);
			}
		});
	}

	public static void sendReportMessage(Player p, String reportMsg, String nameReported) {
		channel.sendTo(p, (payload) -> {
			try {
				ReportMessage message = new ReportMessage(nameReported, reportMsg, p.getName());
				payload.writeBytes(NegativityMessagesManager.writeMessage(message));
			} catch (IOException e) {
				SpongeNegativity.getInstance().getLogger().error("Could not send report message to the proxy.", e);
			}
		});
	}

	public static void sendPluginMessage(byte[] rawMessage) {
		Player player = Utils.getFirstOnlinePlayer();
		if (player != null) {
			channel.sendTo(player, payload -> payload.writeBytes(rawMessage));
		} else {
			getInstance().getLogger()
					.error("Could not send plugin message to proxy because there are no player online.");
		}
	}

	private static class FmlRawDataListener implements RawDataListener {

		@Override
		public void handlePayload(ChannelBuf channelBuf, RemoteConnection connection, Type side) {
			if (!(connection instanceof PlayerConnection)) {
				return;
			}

			Player player = ((PlayerConnection) connection).getPlayer();
			byte[] rawData = channelBuf.readBytes(channelBuf.available());
			HashMap<String, String> playerMods = NegativityPlayer.getNegativityPlayer(player.getUniqueId(),
					() -> new SpongePlayer(player)).mods;
			playerMods.clear();
			playerMods.putAll(Utils.getModsNameVersionFromMessage(new String(rawData, StandardCharsets.UTF_8)));
		}
	}

	private static class ProxyCompanionListener implements RawDataListener {

		@Override
		public void handlePayload(ChannelBuf data, RemoteConnection connection, Type side) {
			byte[] rawData = data.readBytes(data.available());
			Player player = ((PlayerConnection) connection).getPlayer();
			com.elikill58.negativity.api.events.EventManager
					.callEvent(new GameChannelNegativityMessageEvent(SpongeEntityManager.getPlayer(player), rawData));
		}
	}
}
