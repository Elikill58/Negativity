package com.elikill58.negativity.spigot;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.logging.Level;

import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.scheduler.BukkitTask;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.yaml.config.Configuration;
import com.elikill58.negativity.common.timers.ActualizeInvTimer;
import com.elikill58.negativity.common.timers.AnalyzePacketTimer;
import com.elikill58.negativity.common.timers.ClickManagerTimer;
import com.elikill58.negativity.common.timers.PendingAlertsTimer;
import com.elikill58.negativity.common.timers.SpawnFakePlayerTimer;
import com.elikill58.negativity.spigot.impl.entity.SpigotFakePlayer;
import com.elikill58.negativity.spigot.impl.entity.SpigotPlayer;
import com.elikill58.negativity.spigot.listeners.BlockListeners;
import com.elikill58.negativity.spigot.listeners.ChannelListeners;
import com.elikill58.negativity.spigot.listeners.CommandsListeners;
import com.elikill58.negativity.spigot.listeners.ElytraListeners;
import com.elikill58.negativity.spigot.listeners.EntityListeners;
import com.elikill58.negativity.spigot.listeners.FightManager;
import com.elikill58.negativity.spigot.listeners.InventoryListeners;
import com.elikill58.negativity.spigot.listeners.PlayersListeners;
import com.elikill58.negativity.spigot.nms.SpigotVersionAdapter;
import com.elikill58.negativity.spigot.packets.NegativityPacketManager;
import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Cheat.CheatHover;
import com.elikill58.negativity.universal.Database;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.ProxyCompanionManager;
import com.elikill58.negativity.universal.Stats;
import com.elikill58.negativity.universal.Stats.StatsType;
import com.elikill58.negativity.universal.Version;
import com.elikill58.negativity.universal.ban.BanManager;
import com.elikill58.negativity.universal.dataStorage.NegativityAccountStorage;
import com.elikill58.negativity.universal.pluginMessages.AlertMessage;
import com.elikill58.negativity.universal.pluginMessages.NegativityMessagesManager;
import com.elikill58.negativity.universal.pluginMessages.ProxyPingMessage;
import com.elikill58.negativity.universal.pluginMessages.ReportMessage;
import com.elikill58.negativity.universal.utils.ReflectionUtils;

public class SpigotNegativity extends JavaPlugin {

	private static SpigotNegativity INSTANCE;
	public static boolean hasBypass = false, isCraftBukkit = false;
	private BukkitTask clickTimer = null, invTimer = null, pendingAlertsTimer = null, packetTimer = null, runSpawnFakePlayer = null;
	public static String CHANNEL_NAME_FML = "";
	private NegativityPacketManager packetManager;
	
	@Override
	public void onEnable() {
		INSTANCE = this;
		if (Adapter.getAdapter() == null)
			Adapter.setAdapter(new SpigotAdapter(this));
		Version v = Version.getVersion(Utils.VERSION);
		if (v.equals(Version.HIGHER))
			getLogger().warning("Unknow server version " + Utils.VERSION + " ! Some problems can appears.");
		else {
			SpigotVersionAdapter.getVersionAdapter();
			getLogger().info("Detected server version: " + v.name().toLowerCase(Locale.ROOT) + " (" + Utils.VERSION + ")");
		}
		
		packetManager = new NegativityPacketManager(this);
		new File(getDataFolder().getAbsolutePath() + File.separator + "user" + File.separator + "proof").mkdirs();
		if (!new File(getDataFolder().getAbsolutePath() + File.separator + "config.yml").exists()) {
			getLogger().info("------ Negativity Information ------");
			getLogger().info("");
			getLogger().info(" > Thanks for downloading Negativity :)");
			getLogger().info("I'm trying to make the better anti-cheat has possible.");
			getLogger().info("If you get error/false positive, or just have suggestion, you can contact me via:");
			getLogger().info("Discord: @Elikill58#0743, @Elikill58 on twitter or in all other web site like Spigotmc ...");
			getLogger().info("");
			getLogger().info("------ Negativity Information ------");
			getConfig().options().copyDefaults();
			saveDefaultConfig();
		}
		try {
			Class.forName("org.spigotmc.SpigotConfig");
			isCraftBukkit = false;
		} catch (ClassNotFoundException e) {
			isCraftBukkit = true;
		}
		Negativity.loadNegativity();
		SpigotFakePlayer.loadClass();

		try {
			Class.forName("com.google.gson.JsonObject");
			new Metrics(this, 1743)
					.addCustomChart(new Metrics.SimplePie("custom_permission", () -> String.valueOf(Database.hasCustom)));
		} catch (ClassNotFoundException e) {
			// on 1.7, there isn't any gson feature
		}

		PluginManager pm = Bukkit.getPluginManager();
		pm.registerEvents(new PlayersListeners(), this);
		pm.registerEvents(new FightManager(), this);
		pm.registerEvents(new InventoryListeners(), this);
		pm.registerEvents(new BlockListeners(), this);
		pm.registerEvents(new EntityListeners(), this);
		if(v.isNewerOrEquals(Version.V1_9))
			pm.registerEvents(new ElytraListeners(), this);

		Messenger messenger = getServer().getMessenger();
		ChannelListeners channelListeners = new ChannelListeners();
		if (v.isNewerOrEquals(Version.V1_13)) {
			CHANNEL_NAME_FML = "negativity:fml";
		} else {
			CHANNEL_NAME_FML = "FML|HS";
		}
		loadChannelInOut(messenger, NegativityMessagesManager.CHANNEL_ID, channelListeners);
		loadChannelInOut(messenger, CHANNEL_NAME_FML, channelListeners);
		
		for (Player p : Utils.getOnlinePlayers())
			NegativityPlayer.getNegativityPlayer(p.getUniqueId(), () -> new SpigotPlayer(p)).manageAutoVerif();

		loadCommand();
		
		Stats.sendStartupStats(Bukkit.getServer().getPort());
		
		NegativityAccountStorage.setDefaultStorage("file");

		clickTimer = getServer().getScheduler().runTaskTimer(this, new ClickManagerTimer(), 20, 20);
		invTimer = getServer().getScheduler().runTaskTimer(this, new ActualizeInvTimer(), 5, 5);
		packetTimer = getServer().getScheduler().runTaskTimer(this, new AnalyzePacketTimer(), 20, 20);
		runSpawnFakePlayer = getServer().getScheduler().runTaskTimer(this, new SpawnFakePlayerTimer(), 20, 20 * 60 * 10);
		if(Negativity.timeBetweenAlert != -1) {
			int timeTick = Negativity.timeBetweenAlert / 50;
			pendingAlertsTimer = getServer().getScheduler().runTaskTimer(this, new PendingAlertsTimer(), 20, timeTick);
		}
		trySendProxyPing();
		
		getServer().getScheduler().runTaskTimerAsynchronously(this, () -> {
			try {
				double i = SpigotVersionAdapter.getVersionAdapter().getAverageTps() * 1.0E-6D;
				if (Negativity.tpsDrop && i < 50) { // if disabled and need to be enabled
					Negativity.tpsDrop = false;
				} else if (!Negativity.tpsDrop && i > 50) { // if disabled but need to be
					Negativity.tpsDrop = true;
					Adapter.getAdapter().debug("Disabling detection because of TPS lagspike: " + i);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}, 1, 1);
	}
	
	private void loadChannelInOut(Messenger messenger, String channel, ChannelListeners event) {
		if (!messenger.getOutgoingChannels().contains(channel))
			messenger.registerOutgoingPluginChannel(this, channel);
		if (!messenger.getIncomingChannels().contains(channel))
			messenger.registerIncomingPluginChannel(this, channel, event);
	}

	private void loadCommand() {
		CommandsListeners command = new CommandsListeners();
		ConfigurationSection commandSection = getConfig().getConfigurationSection("commands");
		PluginCommand negativity = getCommand("negativity");
		negativity.setExecutor(command);
		negativity.setTabCompleter(command);

		PluginCommand reportCmd = getCommand("nreport");
		if (!commandSection.getBoolean("report", true))
			unRegisterBukkitCommand(reportCmd);
		else {
			reportCmd.setExecutor(command);
			reportCmd.setTabCompleter(command);
		}

		PluginCommand kickCmd = getCommand("nkick");
		if (!commandSection.getBoolean("kick", true))
			unRegisterBukkitCommand(kickCmd);
		else {
			kickCmd.setAliases(Arrays.asList("negkick"));
			kickCmd.setExecutor(command);
			kickCmd.setTabCompleter(command);
		}

		PluginCommand langCmd = getCommand("nlang");
		if (!commandSection.getBoolean("lang", true))
			unRegisterBukkitCommand(langCmd);
		else {
			langCmd.setExecutor(command);
			langCmd.setTabCompleter(command);
		}

		PluginCommand modCmd = getCommand("nmod");
		if (!commandSection.getBoolean("mod", true))
			unRegisterBukkitCommand(modCmd);
		else
			modCmd.setExecutor(command);
		
		
		
		Configuration banConfig = BanManager.getBanConfig().getSection("commands");
		PluginCommand banCmd = getCommand("nban");
		if (!banConfig.getBoolean("ban", true))
			unRegisterBukkitCommand(banCmd);
		else {
			banCmd.setAliases(Arrays.asList("negban"));
			banCmd.setExecutor(command);
			banCmd.setTabCompleter(command);
		}

		PluginCommand unbanCmd = getCommand("nunban");
		if (!banConfig.getBoolean("unban", true))
			unRegisterBukkitCommand(unbanCmd);
		else {
			unbanCmd.setAliases(Arrays.asList("negunban"));
			unbanCmd.setExecutor(command);
			unbanCmd.setTabCompleter(command);
		}
	}

	@Override
	public void onDisable() {
		for (Player p : Utils.getOnlinePlayers()) {
			NegativityPlayer.removeFromCache(p.getUniqueId());
		}
		Database.close();
		Stats.updateStats(StatsType.ONLINE, 0 + "");
		clickTimer.cancel();
		invTimer.cancel();
		packetTimer.cancel();
		runSpawnFakePlayer.cancel();
		if (pendingAlertsTimer != null) {
			pendingAlertsTimer.cancel();
		}
		packetManager.getPacketManager().clear();
	}
	
	public NegativityPacketManager getPacketManager() {
		return packetManager;
	}

	public static SpigotNegativity getInstance() {
		return INSTANCE;
	}

	public static void sendAlertMessage(Player p, String cheatName, int reliability, int ping, CheatHover hover, int alertsCount) {
		try {
			AlertMessage alertMessage = new AlertMessage(p.getName(), cheatName, reliability, ping, hover, alertsCount);
			p.sendPluginMessage(SpigotNegativity.getInstance(), NegativityMessagesManager.CHANNEL_ID, NegativityMessagesManager.writeMessage(alertMessage));
		} catch (IOException e) {
			SpigotNegativity.getInstance().getLogger().severe("Could not send alert message to the proxy.");
			e.printStackTrace();
		}
	}

	public static void sendReportMessage(Player reporter, String reason, String reported) {
		try {
			ReportMessage reportMessage = new ReportMessage(reported, reason, reporter.getName());
			reporter.sendPluginMessage(SpigotNegativity.getInstance(), NegativityMessagesManager.CHANNEL_ID, NegativityMessagesManager.writeMessage(reportMessage));
		} catch (IOException e) {
			SpigotNegativity.getInstance().getLogger().severe("Could not send report message to the proxy.");
			e.printStackTrace();
		}
	}

	public static void sendProxyPing(Player player) {
		ProxyCompanionManager.searchedCompanion = true;
		try {
			byte[] pingMessage = NegativityMessagesManager.writeMessage(new ProxyPingMessage(NegativityMessagesManager.PROTOCOL_VERSION));
			player.sendPluginMessage(SpigotNegativity.getInstance(), NegativityMessagesManager.CHANNEL_ID, pingMessage);
		} catch (IOException ex) {
			SpigotNegativity.getInstance().getLogger().log(Level.SEVERE, "Could not write ProxyPingMessage.", ex);
		}
	}

	public static void trySendProxyPing() {
		Iterator<? extends Player> onlinePlayers = Utils.getOnlinePlayers().iterator();
		if (onlinePlayers.hasNext()) {
			sendProxyPing(onlinePlayers.next());
		}
	}

	private Object getKnownCommands(Object object) {
		try {
			Field objectField = object.getClass().getDeclaredField("knownCommands");
			objectField.setAccessible(true);
			return objectField.get(object);
		} catch (NoSuchFieldException e) {
			Class<?> clazz = object.getClass();
			try {
				return clazz.getMethod("getKnownCommands").invoke(object);
			} catch (Exception e1) {
				e1.printStackTrace();
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public void unRegisterBukkitCommand(PluginCommand cmd) {
		try {
			Object result = ReflectionUtils.getPrivateField(this.getServer().getPluginManager(), "commandMap");
			HashMap<?, ?> knownCommands = (HashMap<?, ?>) getKnownCommands(result);
			knownCommands.remove(cmd.getName());
			for (String alias : cmd.getAliases())
				if (knownCommands.containsKey(alias) && knownCommands.get(alias).toString().contains(this.getName()))
					knownCommands.remove(alias);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void sendPluginMessage(byte[] rawMessage) {
		Player player = Utils.getFirstOnlinePlayer();
		if (player != null) {
			player.sendPluginMessage(getInstance(), NegativityMessagesManager.CHANNEL_ID, rawMessage);
		} else {
			getInstance().getLogger().severe("Could not send plugin message to proxy because there are no player online.");
		}
	}
}
