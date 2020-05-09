package com.elikill58.negativity.spigot;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringJoiner;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.scheduler.BukkitRunnable;

import com.elikill58.negativity.spigot.commands.BanCommand;
import com.elikill58.negativity.spigot.commands.KickCommand;
import com.elikill58.negativity.spigot.commands.LangCommand;
import com.elikill58.negativity.spigot.commands.ModCommand;
import com.elikill58.negativity.spigot.commands.NegativityCommand;
import com.elikill58.negativity.spigot.commands.ReportCommand;
import com.elikill58.negativity.spigot.commands.UnbanCommand;
import com.elikill58.negativity.spigot.events.ChannelEvents;
import com.elikill58.negativity.spigot.events.FightManager;
import com.elikill58.negativity.spigot.events.InventoryEvents;
import com.elikill58.negativity.spigot.events.PlayersEvents;
import com.elikill58.negativity.spigot.inventories.AbstractInventory;
import com.elikill58.negativity.spigot.listeners.PlayerCheatAlertEvent;
import com.elikill58.negativity.spigot.listeners.PlayerCheatBypassEvent;
import com.elikill58.negativity.spigot.listeners.PlayerCheatEvent;
import com.elikill58.negativity.spigot.listeners.PlayerCheatKickEvent;
import com.elikill58.negativity.spigot.listeners.ShowAlertPermissionEvent;
import com.elikill58.negativity.spigot.packets.NegativityPacketManager;
import com.elikill58.negativity.spigot.support.EssentialsSupport;
import com.elikill58.negativity.spigot.support.GadgetMenuSupport;
import com.elikill58.negativity.spigot.timers.ActualizeClickTimer;
import com.elikill58.negativity.spigot.timers.ActualizeInvTimer;
import com.elikill58.negativity.spigot.timers.TimerAnalyzePacket;
import com.elikill58.negativity.spigot.timers.TimerSpawnFakePlayer;
import com.elikill58.negativity.spigot.timers.TimerTimeBetweenAlert;
import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.Cheat.CheatCategory;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.Database;
import com.elikill58.negativity.universal.ItemUseBypass;
import com.elikill58.negativity.universal.ItemUseBypass.WhenBypass;
import com.elikill58.negativity.universal.ProxyCompanionManager;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.Stats;
import com.elikill58.negativity.universal.Stats.StatsType;
import com.elikill58.negativity.universal.Version;
import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.adapter.SpigotAdapter;
import com.elikill58.negativity.universal.ban.BanManager;
import com.elikill58.negativity.universal.ban.BanUtils;
import com.elikill58.negativity.universal.ban.processor.ForwardToProxyBanProcessor;
import com.elikill58.negativity.universal.ban.support.AdvancedBanProcessor;
import com.elikill58.negativity.universal.ban.support.BukkitBanProcessor;
import com.elikill58.negativity.universal.ban.support.MaxBansProcessor;
import com.elikill58.negativity.universal.permissions.Perm;
import com.elikill58.negativity.universal.pluginMessages.AlertMessage;
import com.elikill58.negativity.universal.pluginMessages.NegativityMessagesManager;
import com.elikill58.negativity.universal.pluginMessages.ProxyPingMessage;
import com.elikill58.negativity.universal.pluginMessages.ReportMessage;
import com.elikill58.negativity.universal.utils.ReflectionUtils;
import com.elikill58.negativity.universal.utils.UniversalUtils;

@SuppressWarnings("deprecation")
public class SpigotNegativity extends JavaPlugin {

	private static SpigotNegativity INSTANCE;
	public static boolean log = false, log_console = false, hasBypass = false, essentialsSupport = false,
			worldGuardSupport = false, gadgetMenuSupport = false, viaVersionSupport = false, protocolSupportSupport = false;
	public static final Material MATERIAL_CLOSE = Utils.getMaterialWith1_15_Compatibility("BARRIER", "REDSTONE");
	private BukkitRunnable clickTimer = null, invTimer = null, packetTimer = null, runSpawnFakePlayer = null, timeTimeBetweenAlert = null;
	public static final HashMap<Player, HashMap<Cheat, Long>> TIME_LAST_CHEAT_ALERT = new HashMap<>();
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
			getLogger().warning("Unknow server version ! Some problems can appears.");
		else
			getLogger().info("Detected server version: " + v.name().toLowerCase());
		
		packetManager = new NegativityPacketManager(this);
		new File(getDataFolder().getAbsolutePath() + File.separator + "user").mkdirs();
		if (!new File(getDataFolder().getAbsolutePath() + File.separator + "config.yml").exists()) {
			getLogger().info("------ Negativity Information ------");
			getLogger().info("");
			getLogger().info(" > Thanks for downloading Negativity :)");
			getLogger().info("I'm trying to make the better anti-cheat has possible.");
			getLogger().info(
					"If there is any false positive, problem or if you have a suggestion you can contact me via:");
			getLogger().info(
					"Discord: @Elikill58#0743, mail: arpetzouille@gmail.com, and Elikill58 in all other web site like Twitter, Spigotmc ...");
			getLogger().info("");
			getLogger().info("------ Negativity Information ------");
			getConfig().options().copyDefaults();
			saveDefaultConfig();
		}
		UniversalUtils.init();
		Cheat.loadCheat();
		FakePlayer.loadClass();
		ProxyCompanionManager.updateForceDisabled(getConfig().getBoolean("disableProxyIntegration"));
		setupValue();

		new Metrics(this)
				.addCustomChart(new Metrics.SimplePie("custom_permission", () -> String.valueOf(Database.hasCustom)));

		PluginManager pm = Bukkit.getPluginManager();
		pm.registerEvents(new PlayersEvents(), this);
		pm.registerEvents(new InventoryEvents(), this);
		pm.registerEvents(new FightManager(), this);

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
		
		(clickTimer = new ActualizeClickTimer()).runTaskTimer(this, 20, 20);
		(invTimer = new ActualizeInvTimer()).runTaskTimerAsynchronously(this, 5, 5);
		(packetTimer = new TimerAnalyzePacket()).runTaskTimer(this, 20, 20);
		(runSpawnFakePlayer = new TimerSpawnFakePlayer()).runTaskTimer(this, 20, 20 * 60 * 10);

		for (Cheat c : Cheat.values())
			if (c.isActive() && c.hasListener())
				pm.registerEvents((Listener) c, this);
		
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
		AbstractInventory.init(this);

		StringJoiner supportedPluginName = new StringJoiner(", ");
		BanManager.registerProcessor("bukkit", new BukkitBanProcessor());
		BanManager.registerProcessor(ForwardToProxyBanProcessor.PROCESSOR_ID, new ForwardToProxyBanProcessor(rawMessage -> {
			Player player = Utils.getFirstOnlinePlayer();
			if (player != null) {
				player.sendPluginMessage(this, NegativityMessagesManager.CHANNEL_ID, rawMessage);
			} else {
				getLogger().severe("Could not send ban revocation request to proxy because there are no player online.");
			}
		}));
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
		
		if (Bukkit.getPluginManager().getPlugin("ViaVersion") != null) {
			viaVersionSupport = true;
			supportedPluginName.add("ViaVersion");
		}
		
		if (Bukkit.getPluginManager().getPlugin("ProtocolSupport") != null) {
			protocolSupportSupport = true;
			supportedPluginName.add("ProtocolSupport");
		}
		
		Perm.registerChecker(Perm.PLATFORM_CHECKER, new BukkitPermissionChecker());

		if (supportedPluginName.length() > 0) {
			getLogger().info("Loaded support for " + supportedPluginName.toString() + ".");
		}
	}
	
	private void loadChannelInOut(Messenger messenger, String channel, ChannelEvents event) {
		if (!messenger.getOutgoingChannels().contains(channel))
			messenger.registerOutgoingPluginChannel(this, channel);
		if (!messenger.getIncomingChannels().contains(channel))
			messenger.registerIncomingPluginChannel(this, channel, event);
	}

	private void loadCommand() {
		PluginCommand negativity = getCommand("negativity");
		NegativityCommand negativityCmd = new NegativityCommand();
		negativity.setExecutor(negativityCmd);
		negativity.setTabCompleter(negativityCmd);

		PluginCommand reportCmd = getCommand("report");
		if (!getConfig().getBoolean("report_command"))
			unRegisterBukkitCommand(reportCmd);
		else {
			reportCmd.setExecutor(new ReportCommand());
			reportCmd.setTabCompleter(new ReportCommand());
		}

		PluginCommand banCmd = getCommand("nban");
		if (!getConfig().getBoolean("ban_command"))
			unRegisterBukkitCommand(banCmd);
		else {
			List<String> banAlias = new ArrayList<String>();
			banAlias.add("negban");
			banCmd.setAliases(banAlias);
			banCmd.setExecutor(new BanCommand());
			banCmd.setTabCompleter(new BanCommand());
		}

		PluginCommand unbanCmd = getCommand("nunban");
		if (!getConfig().getBoolean("unban_command"))
			unRegisterBukkitCommand(unbanCmd);
		else {
			List<String> unbanAlias = new ArrayList<String>();
			unbanAlias.add("negunban");
			unbanCmd.setAliases(unbanAlias);
			unbanCmd.setExecutor(new UnbanCommand());
			unbanCmd.setTabCompleter(new UnbanCommand());
		}

		PluginCommand kickCmd = getCommand("nkick");
		if (!getConfig().getBoolean("kick_command"))
			unRegisterBukkitCommand(kickCmd);
		else {
			List<String> kickAlias = new ArrayList<String>();
			kickAlias.add("negkick");
			kickCmd.setAliases(kickAlias);
			kickCmd.setExecutor(new KickCommand());
			kickCmd.setTabCompleter(new KickCommand());
		}

		PluginCommand langCmd = getCommand("lang");
		LangCommand langExecutor = new LangCommand();
		langCmd.setExecutor(langExecutor);
		langCmd.setTabCompleter(langExecutor);

		getCommand("mod").setExecutor(new ModCommand());
	}

	@Override
	public void onDisable() {
		for (Player p : Utils.getOnlinePlayers()) {
			SpigotNegativityPlayer.removeFromCache(p.getUniqueId());
		}
		Database.close();
		Stats.updateStats(StatsType.ONLINE, 0 + "");
		invTimer.cancel();
		clickTimer.cancel();
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
	
	public static boolean alertMod(ReportType type, Player p, Cheat c, int reliability, String proof) {
		return alertMod(type, p, c, reliability, proof, "", 1);
	}
	
	public static boolean alertMod(ReportType type, Player p, Cheat c, int reliability, String proof, String hover_proof) {
		return alertMod(type, p, c, reliability, proof, hover_proof, 1);
	}
	
	@Deprecated // old method, please never use it
	public static boolean alertMod(ReportType type, Player p, Cheat c, int reliability, String proof, String hover_proof, String stats_send) {
		return alertMod(type, p, c, reliability, proof, hover_proof, 1);
	}

	public static boolean alertMod(ReportType type, Player p, Cheat c, int reliability, String proof,
			String hover_proof, int amount) {
		if(!c.isActive() || reliability < 55)
			return false;
		SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(p);
		if (!np.already_blink && c.equals(Cheat.forKey(CheatKeys.BLINK))) {
			np.already_blink = true;
			return false;
		}
		if (np.isInFight && c.isBlockedInFight())
			return false;
		if (c.equals(Cheat.forKey(CheatKeys.FLY)) && p.hasPermission("essentials.fly") && essentialsSupport && EssentialsSupport.checkEssentialsPrecondition(p))
			return false;
		if(c.getCheatCategory().equals(CheatCategory.MOVEMENT) && gadgetMenuSupport &&  GadgetMenuSupport.checkGadgetsMenuPreconditions(p))
			return false;
		
		int ping = Utils.getPing(p);
		long currentTimeMilli = System.currentTimeMillis();
		if (np.TIME_INVINCIBILITY > currentTimeMilli || ping > c.getMaxAlertPing()
				|| ((double) ((Damageable) p).getHealth()) == 0.0D
				|| getInstance().getConfig().getInt("tps_alert_stop") > Utils.getLastTPS() || ping < 0 || np.isFreeze)
			return false;
		
		if (p.getItemInHand() != null)
			if (ItemUseBypass.ITEM_BYPASS.containsKey(p.getItemInHand().getType().name()))
				if (ItemUseBypass.ITEM_BYPASS.get(p.getItemInHand().getType().name()).getWhen().equals(WhenBypass.ALWAYS))
					return false;
		Block target = Utils.getTargetBlock(p, 5);
		if(target != null && !target.getType().equals(Material.AIR))
			if (ItemUseBypass.ITEM_BYPASS.containsKey(target.getType().name()))
				if (ItemUseBypass.ITEM_BYPASS.get(target.getType().name()).getWhen().equals(WhenBypass.LOOKING))
					return false;
		
		Bukkit.getPluginManager().callEvent(new PlayerCheatEvent(p, c, reliability));
		if (hasBypass && (Perm.hasPerm(SpigotNegativityPlayer.getNegativityPlayer(p), "bypass." + c.getKey().toLowerCase())
				|| Perm.hasPerm(SpigotNegativityPlayer.getNegativityPlayer(p), "bypass.all"))) {
			PlayerCheatBypassEvent bypassEvent = new PlayerCheatBypassEvent(p, c, reliability);
			Bukkit.getPluginManager().callEvent(bypassEvent);
			if (!bypassEvent.isCancelled())
				return false;
		}
		PlayerCheatAlertEvent alert = new PlayerCheatAlertEvent(type, p, c, reliability,
				c.getReliabilityAlert() < reliability, ping, proof, hover_proof, amount);
		Bukkit.getPluginManager().callEvent(alert);
		if (alert.isCancelled() || !alert.isAlert())
			return false;
		np.addWarn(c, reliability);
		logProof(np, type, p, c, reliability, proof, ping);
		if (c.allowKick() && c.getAlertToKick() <= np.getWarn(c)) {
			PlayerCheatKickEvent kick = new PlayerCheatKickEvent(p, c, reliability);
			Bukkit.getPluginManager().callEvent(kick);
			if (!kick.isCancelled())
				p.kickPlayer(Messages.getMessage(p, "kick.kicked", "%cheat%", c.getName(), "%reason%", c.getName(), "%playername%", p.getName(), "%cheat%", c.getName()));
		}
		if(BanManager.isBanned(np.getUUID())) {
			Stats.updateStats(StatsType.CHEAT, c.getKey(), reliability + "");
			return false;
		}

		if (BanUtils.banIfNeeded(np, c, reliability) != null) {
			Stats.updateStats(StatsType.CHEAT, c.getKey(), reliability + "");
			return false;
		}
		manageAlertCommand(type, p, c, reliability);
		if(timeBetweenAlert != -1) {
			List<PlayerCheatAlertEvent> tempList = np.ALERT_NOT_SHOWED.containsKey(c) ? np.ALERT_NOT_SHOWED.get(c) : new ArrayList<>();
			tempList.add(alert);
			np.ALERT_NOT_SHOWED.put(c, tempList);
			return true;
		}

		sendAlertMessage(np, alert);
		return true;
	}

	private static void manageAlertCommand(ReportType type, Player p, Cheat c, int reliability) {
		FileConfiguration conf = getInstance().getConfig();
		if(!conf.getBoolean("alert.command.active") || conf.getInt("alert.command.reliability_need") > reliability)
			return;
		for(String s : conf.getStringList("alert.command.run")) {
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), UniversalUtils.replacePlaceholders(s, "%name%",
					p.getName(), "%uuid%", p.getUniqueId().toString(), "%cheat_key%", c.getKey(), "%cheat_name%",
					c.getName(), "%reliability%", reliability, "%report_type%", type.name()));
		}
	}

	public static void sendAlertMessage(SpigotNegativityPlayer np, PlayerCheatAlertEvent alert) {
		Cheat c = alert.getCheat();
		int reliability = alert.getReliability();
		if(reliability == 0) {// alert already sent
			np.ALERT_NOT_SHOWED.remove(c);
			return;
		}
		Player p = alert.getPlayer();
		int ping = alert.getPing();
		if(alert.getNbAlertConsole() > 0 && log_console) {
				INSTANCE.getLogger().info("New " + alert.getReportType().getName() + " for " + p.getName()
						+ " (UUID: " + p.getUniqueId().toString() + ") (ping: " + ping + ") : suspected of cheating ("
						+ c.getName() + ") " + (alert.getNbAlertConsole() > 1 ? alert.getNbAlertConsole() + " times " : "") + "Reliability: " + reliability);
		}
		if (ProxyCompanionManager.isIntegrationEnabled()) {
			sendAlertMessage(p, c.getName(), reliability, ping, alert.getHoverProof(), alert.getNbAlert());
			np.ALERT_NOT_SHOWED.remove(c);
		} else {
			String hover_proof = alert.getHoverProof();
			boolean hasPermPeople = false;
			for (Player pl : Utils.getOnlinePlayers()) {
				SpigotNegativityPlayer npMod = SpigotNegativityPlayer.getNegativityPlayer(pl);
				boolean basicPerm = Perm.hasPerm(npMod, Perm.SHOW_ALERT);
				ShowAlertPermissionEvent permissionEvent = new ShowAlertPermissionEvent(p, np, basicPerm);
				Bukkit.getPluginManager().callEvent(permissionEvent);
				if (permissionEvent.isCancelled() || npMod.disableShowingAlert)
					continue;
				if (permissionEvent.hasBasicPerm()) {
					new ClickableText().addRunnableHoverEvent(
							Messages.getMessage(pl, alert.getAlertMessageKey(), "%name%", p.getName(), "%cheat%", c.getName(),
									"%reliability%", String.valueOf(reliability), "%nb%", String.valueOf(alert.getNbAlert())),
							Messages.getMessage(pl, "negativity.alert_hover", "%reliability%",
									String.valueOf(reliability), "%ping%", String.valueOf(ping))
									+ (hover_proof.equalsIgnoreCase("") ? "" : "\n" + hover_proof),
							"/negativity " + p.getName()).sendToPlayer(pl);
					hasPermPeople = true;
				}
			}
			if(hasPermPeople) {
				np.ALERT_NOT_SHOWED.remove(c);
				Stats.updateStats(StatsType.CHEAT, c.getKey(), reliability + "");
			}
		}
	}

	private static void sendAlertMessage(Player p, String cheatName, int reliability, int ping, String hover, int alertsCount) {
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

	private static void logProof(SpigotNegativityPlayer np, ReportType type, Player p, Cheat c, int reliability,
			String proof, int ping) {
		if(log)
			np.logProof(new Timestamp(System.currentTimeMillis()) + ": (" + ping + "ms) " + reliability + "% " + c.getKey()
				+ " > " + proof + ". " + (viaVersionSupport ? "Player version: " + np.getPlayerVersion().name() + " " : "") + "TPS: " + Arrays.toString(Utils.getTPS()));
	}

	public static void manageAutoVerif(Player p) {
		SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(p);
		np.ACTIVE_CHEAT.clear();
		boolean needPacket = false;
		for (Cheat c : Cheat.values())
			if (c.isActive() && c.isAutoVerif()) {
				np.startAnalyze(c);
				if (c.needPacket())
					needPacket = true;
			}
		if (needPacket && !SpigotNegativityPlayer.INJECTED.contains(p.getUniqueId()))
			SpigotNegativityPlayer.INJECTED.add(p.getUniqueId());
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

	public static void setupValue() {
		SpigotNegativity pl = getInstance();
		FileConfiguration config = pl.getConfig();
		log = config.getBoolean("log_alerts");
		log_console = config.getBoolean("log_alerts_in_console");
		hasBypass = config.getBoolean("Permissions.bypass.active");
		
		timeBetweenAlert = config.getInt("time_between_alert");
		if(timeBetweenAlert != -1) {
			int timeTick = (timeBetweenAlert / 1000) * 20;
			if(pl.timeTimeBetweenAlert != null)
				pl.timeTimeBetweenAlert.cancel();
			(pl.timeTimeBetweenAlert = new TimerTimeBetweenAlert()).runTaskTimer(pl, timeTick, timeTick);
		}
	}
}
