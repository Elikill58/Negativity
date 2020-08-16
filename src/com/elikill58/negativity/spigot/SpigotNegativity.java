package com.elikill58.negativity.spigot;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringJoiner;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.scheduler.BukkitTask;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.timers.ActualizeInvTimer;
import com.elikill58.negativity.api.timers.AnalyzePacketTimer;
import com.elikill58.negativity.api.timers.ClickManagerTimer;
import com.elikill58.negativity.api.timers.PendingAlertsTimer;
import com.elikill58.negativity.api.timers.SpawnFakePlayerTimer;
import com.elikill58.negativity.spigot.events.ChannelEvents;
import com.elikill58.negativity.spigot.events.FightManager;
import com.elikill58.negativity.spigot.events.PlayersEvents;
import com.elikill58.negativity.spigot.events.ServerCrasherEvents;
import com.elikill58.negativity.spigot.impl.entity.SpigotPlayer;
import com.elikill58.negativity.spigot.listeners.BlockListeners;
import com.elikill58.negativity.spigot.listeners.CommandsListeners;
import com.elikill58.negativity.spigot.listeners.EntityListeners;
import com.elikill58.negativity.spigot.listeners.InventoryListeners;
import com.elikill58.negativity.spigot.listeners.PlayersListeners;
import com.elikill58.negativity.spigot.packets.NegativityPacketManager;
import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.Cheat.CheatHover;
import com.elikill58.negativity.universal.Database;
import com.elikill58.negativity.universal.ItemUseBypass;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.ProxyCompanionManager;
import com.elikill58.negativity.universal.Stats;
import com.elikill58.negativity.universal.Stats.StatsType;
import com.elikill58.negativity.universal.Version;
import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.adapter.SpigotAdapter;
import com.elikill58.negativity.universal.ban.BanManager;
import com.elikill58.negativity.universal.ban.processor.ForwardToProxyBanProcessor;
import com.elikill58.negativity.universal.ban.support.AdvancedBanProcessor;
import com.elikill58.negativity.universal.ban.support.BukkitBanProcessor;
import com.elikill58.negativity.universal.ban.support.LiteBansProcessor;
import com.elikill58.negativity.universal.ban.support.MaxBansProcessor;
import com.elikill58.negativity.universal.dataStorage.NegativityAccountStorage;
import com.elikill58.negativity.universal.dataStorage.file.SpigotFileNegativityAccountStorage;
import com.elikill58.negativity.universal.permissions.Perm;
import com.elikill58.negativity.universal.pluginMessages.AlertMessage;
import com.elikill58.negativity.universal.pluginMessages.NegativityMessagesManager;
import com.elikill58.negativity.universal.pluginMessages.ProxyPingMessage;
import com.elikill58.negativity.universal.pluginMessages.ReportMessage;
import com.elikill58.negativity.universal.utils.ReflectionUtils;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class SpigotNegativity extends JavaPlugin {

	private static SpigotNegativity INSTANCE;
	public static boolean log = false, log_console = false, hasBypass = false, isCraftBukkit = false, essentialsSupport = false,
			worldGuardSupport = false, gadgetMenuSupport = false, viaVersionSupport = false, protocolSupportSupport = false;
	private BukkitTask invTimer = null, timeTimeBetweenAlert = null, packetTimer = null, runSpawnFakePlayer = null;
	public static String CHANNEL_NAME_FML = "";
	private static int timeBetweenAlert = -1;
	private NegativityPacketManager packetManager;
	
	@Override
	public void onEnable() {
		INSTANCE = this;
		if (Adapter.getAdapter() == null)
			Adapter.setAdapter(new SpigotAdapter(this));
		Version v = Version.getVersion(Utils.VERSION);
		if (v.equals(Version.HIGHER))
			getLogger().warning("Unknow server version " + Utils.VERSION + " ! Some problems can appears.");
		else
			getLogger().info("Detected server version: " + v.name().toLowerCase() + " (" + Utils.VERSION + ")");
		
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
		getLogger().info("This plugin is free, but you can support me : https://www.patreon.com/elikill58 <3");
		UniversalUtils.init();
		FakePlayer.loadClass();
		ProxyCompanionManager.updateForceDisabled(getConfig().getBoolean("disableProxyIntegration"));

		new Metrics(this)
				.addCustomChart(new Metrics.SimplePie("custom_permission", () -> String.valueOf(Database.hasCustom)));

		PluginManager pm = Bukkit.getPluginManager();
		pm.registerEvents(new PlayersEvents(this), this);
		pm.registerEvents(new FightManager(), this);
		pm.registerEvents(new ServerCrasherEvents(this), this);
		pm.registerEvents(new PlayersListeners(), this);
		pm.registerEvents(new InventoryListeners(), this);
		pm.registerEvents(new BlockListeners(), this);
		pm.registerEvents(new EntityListeners(), this);

		Messenger messenger = getServer().getMessenger();
		ChannelEvents channelEvents = new ChannelEvents();
		if (v.isNewerOrEquals(Version.V1_13)) {
			CHANNEL_NAME_FML = "negativity:fml";
		} else {
			CHANNEL_NAME_FML = "FML|HS";
		}
		loadChannelInOut(messenger, NegativityMessagesManager.CHANNEL_ID, channelEvents);
		loadChannelInOut(messenger, CHANNEL_NAME_FML, channelEvents);
		
		for (Player p : Utils.getOnlinePlayers())
			manageAutoVerif(p);

		loadCommand();

		if (getConfig().get("items") != null) {
			ConfigurationSection cs = getConfig().getConfigurationSection("items");
			for (String s : cs.getKeys(false))
				new ItemUseBypass(s, cs.getString(s + ".cheats"), cs.getString(s + ".when"));
		}
		if (!UniversalUtils.isLatestVersion(getDescription().getVersion())) {
			getLogger().info("New version available (" + UniversalUtils.getLatestVersion().orElse("unknow")
					+ "). Download it here: https://www.spigotmc.org/resources/48399/");
		}
		getServer().getScheduler().runTaskAsynchronously(this, new Runnable() {
			@Override
			public void run() {
				Stats.loadStats();
				Stats.updateStats(StatsType.ONLINE, 1 + "");
				Stats.updateStats(StatsType.PORT, Bukkit.getServer().getPort() + "");
			}
		});

		NegativityAccountStorage.register("file", new SpigotFileNegativityAccountStorage(new File(getDataFolder(), "user")));
		NegativityAccountStorage.setDefaultStorage("file");
		StringJoiner supportedPluginName = new StringJoiner(", ");
		BanManager.registerProcessor("bukkit", new BukkitBanProcessor());
		BanManager.registerProcessor(ForwardToProxyBanProcessor.PROCESSOR_ID, new ForwardToProxyBanProcessor(SpigotNegativity::sendPluginMessage));
		if (Bukkit.getPluginManager().getPlugin("Essentials") != null) {
			essentialsSupport = true;
			supportedPluginName.add("Essentials");
		}
		if (Bukkit.getPluginManager().getPlugin("WorldGuard") != null) {
			worldGuardSupport = true;
			supportedPluginName.add("WorldGuard");
		}
		if (Bukkit.getPluginManager().getPlugin("GadgetsMenu") != null) {
			gadgetMenuSupport = true;
			supportedPluginName.add("GadgetsMenu");
		}

		if (Bukkit.getPluginManager().getPlugin("MaxBans") != null) {
			BanManager.registerProcessor("maxbans", new MaxBansProcessor());
			supportedPluginName.add("MaxBans");
		}

		if (Bukkit.getPluginManager().getPlugin("AdvancedBan") != null) {
			BanManager.registerProcessor("advancedban", new AdvancedBanProcessor());
			supportedPluginName.add("AdvancedBan");
		}

		if (Bukkit.getPluginManager().getPlugin("LiteBans") != null) {
			BanManager.registerProcessor("litebans", new LiteBansProcessor());
			supportedPluginName.add("LiteBans");
		}
		
		if (Bukkit.getPluginManager().getPlugin("ViaVersion") != null) {
			viaVersionSupport = true;
			supportedPluginName.add("ViaVersion");
		}
		
		if (Bukkit.getPluginManager().getPlugin("ProtocolSupport") != null) {
			protocolSupportSupport = true;
			supportedPluginName.add("ProtocolSupport");
		}
		
		if (Bukkit.getPluginManager().getPlugin("floodgate-bukkit") != null) {
			Negativity.floodGateSupport = true;
			supportedPluginName.add("FloodGate");
		}
		
		Perm.registerChecker(Perm.PLATFORM_CHECKER, new BukkitPermissionChecker());

		if (supportedPluginName.length() > 0) {
			getLogger().info("Loaded support for " + supportedPluginName.toString() + ".");
		}

		getServer().getScheduler().runTaskTimer(this, new ClickManagerTimer(), 20, 20);
		invTimer = getServer().getScheduler().runTaskTimer(this, new ActualizeInvTimer(), 5, 5);
		packetTimer = getServer().getScheduler().runTaskTimer(this, new AnalyzePacketTimer(), 20, 20);
		runSpawnFakePlayer = getServer().getScheduler().runTaskTimer(this, new SpawnFakePlayerTimer(), 20, 20 * 60 * 10);
		timeBetweenAlert = getConfig().getInt("time_between_alert");
		if(timeBetweenAlert != -1) {
			int timeTick = (timeBetweenAlert / 1000) * 20;
			if(timeTimeBetweenAlert != null)
				timeTimeBetweenAlert.cancel();
			timeTimeBetweenAlert = getServer().getScheduler().runTaskTimer(this, new PendingAlertsTimer(), timeTick, timeTick);
		}
	}
	
	private void loadChannelInOut(Messenger messenger, String channel, ChannelEvents event) {
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

		PluginCommand banCmd = getCommand("nban");
		if (!commandSection.getBoolean("ban", true))
			unRegisterBukkitCommand(banCmd);
		else {
			banCmd.setAliases(Arrays.asList("negban"));
			banCmd.setExecutor(command);
			banCmd.setTabCompleter(command);
		}

		PluginCommand unbanCmd = getCommand("nunban");
		if (!commandSection.getBoolean("unban", true))
			unRegisterBukkitCommand(unbanCmd);
		else {
			unbanCmd.setAliases(Arrays.asList("negunban"));
			unbanCmd.setExecutor(command);
			unbanCmd.setTabCompleter(command);
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
	}

	@Override
	public void onDisable() {
		for (Player p : Utils.getOnlinePlayers()) {
			NegativityPlayer.removeFromCache(p.getUniqueId());
		}
		Database.close();
		Stats.updateStats(StatsType.ONLINE, 0 + "");
		invTimer.cancel();
		packetTimer.cancel();
		runSpawnFakePlayer.cancel();
		timeTimeBetweenAlert.cancel();
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

	public static void manageAutoVerif(Player p) {
		NegativityPlayer np = NegativityPlayer.getNegativityPlayer(new SpigotPlayer(p));
		np.ACTIVE_CHEAT.clear();
		boolean needPacket = false;
		for (Cheat c : Cheat.values())
			if (c.isActive()) {
				np.startAnalyze(c);
				if (c.needPacket())
					needPacket = true;
			}
		if (needPacket && !NegativityPlayer.INJECTED.contains(p.getUniqueId()))
			NegativityPlayer.INJECTED.add(p.getUniqueId());
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
			HashMap<?, ?> knownCommands = (HashMap<?, ?>) getKnownCommands((SimpleCommandMap) result);
			if (knownCommands.containsKey(cmd.getName()))
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
