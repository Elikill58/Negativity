package com.elikill58.negativity.spigot;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.StringJoiner;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.configuration.ConfigurationSection;
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
import com.elikill58.negativity.spigot.commands.SuspectCommand;
import com.elikill58.negativity.spigot.commands.UnbanCommand;
import com.elikill58.negativity.spigot.events.ChannelEvents;
import com.elikill58.negativity.spigot.events.FightManager;
import com.elikill58.negativity.spigot.events.InventoryEvents;
import com.elikill58.negativity.spigot.events.PlayersEvents;
import com.elikill58.negativity.spigot.listeners.PlayerCheatAlertEvent;
import com.elikill58.negativity.spigot.listeners.PlayerCheatBypassEvent;
import com.elikill58.negativity.spigot.listeners.PlayerCheatEvent;
import com.elikill58.negativity.spigot.listeners.PlayerCheatKickEvent;
import com.elikill58.negativity.spigot.listeners.ShowAlertPermissionEvent;
import com.elikill58.negativity.spigot.packets.PacketListenerAPI;
import com.elikill58.negativity.spigot.packets.PacketManager;
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
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.Stats;
import com.elikill58.negativity.universal.Stats.StatsType;
import com.elikill58.negativity.universal.SuspectManager;
import com.elikill58.negativity.universal.Version;
import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.adapter.SpigotAdapter;
import com.elikill58.negativity.universal.ban.BanManager;
import com.elikill58.negativity.universal.ban.BanUtils;
import com.elikill58.negativity.universal.ban.support.AdvancedBanProcessor;
import com.elikill58.negativity.universal.ban.support.BukkitBanProcessor;
import com.elikill58.negativity.universal.ban.support.MaxBansProcessor;
import com.elikill58.negativity.universal.permissions.Perm;
import com.elikill58.negativity.universal.pluginMessages.AlertMessage;
import com.elikill58.negativity.universal.pluginMessages.NegativityMessagesManager;
import com.elikill58.negativity.universal.pluginMessages.ReportMessage;
import com.elikill58.negativity.universal.utils.UniversalUtils;

@SuppressWarnings("deprecation")
public class SpigotNegativity extends JavaPlugin {

	private static SpigotNegativity INSTANCE;
	public static boolean isOnBungeecord = false, log = false, log_console = false, hasBypass = false, essentialsSupport = false,
			worldGuardSupport = false, gadgetMenuSupport = false;
	public static Material MATERIAL_CLOSE = Material.REDSTONE;
	private BukkitRunnable clickTimer = null, invTimer = null, packetTimer = null, runSpawnFakePlayer = null, timeTimeBetweenAlert = null;
	public static List<PlayerCheatAlertEvent> alerts = new ArrayList<>();
	private static final HashMap<Player, HashMap<Cheat, Long>> TIME_LAST_CHEAT_ALERT = new HashMap<>();
	public static String CHANNEL_NAME_FML = "";
	
	@Override
	public void onEnable() {
		INSTANCE = this;
		if (Adapter.getAdapter() == null)
			Adapter.setAdapter(new SpigotAdapter(this));
		Adapter ada = Adapter.getAdapter();
		Version v = Version.getVersion();
		if (v.equals(Version.HIGHER))
			getLogger().warning("Unknow server version ! Some problems can appears.");
		else
			getLogger().info("Detected server version: " + v.name().toLowerCase());
		try {
			MATERIAL_CLOSE = (Material) Material.class.getField("BARRIER").get(Material.class);
		} catch (IllegalArgumentException | IllegalAccessException | SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			MATERIAL_CLOSE = Material.REDSTONE;
		}
		PacketManager.run(this);
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
		isOnBungeecord = ada.getBooleanInConfig("hasBungeecord");
		log = ada.getBooleanInConfig("log_alerts");
		log_console = ada.getBooleanInConfig("log_alerts_in_console");
		hasBypass = ada.getBooleanInConfig("Permissions.bypass.active");

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
		
		for (Player p : Utils.getOnlinePlayers()) {
			PacketListenerAPI.addPlayer(p);
			manageAutoVerif(p);
		}
		(clickTimer = new ActualizeClickTimer()).runTaskTimer(this, 20, 20);
		(invTimer = new ActualizeInvTimer()).runTaskTimerAsynchronously(this, 5, 5);
		(packetTimer = new TimerAnalyzePacket()).runTaskTimer(this, 20, 20);
		(runSpawnFakePlayer = new TimerSpawnFakePlayer()).runTaskTimer(this, 20, 20 * 60 * 10);
		(timeTimeBetweenAlert = new TimerTimeBetweenAlert()).runTaskTimer(this, 20, 20);

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

		StringJoiner supportedPluginName = new StringJoiner(", ");
		BanManager.registerProcessor("bukkit", new BukkitBanProcessor());
		if (Bukkit.getPluginManager().getPlugin("Essentials") != null) {
			essentialsSupport = true;
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

		PluginCommand suspectCmd = getCommand("suspect");
		if (!SuspectManager.ENABLED)
			unRegisterBukkitCommand(suspectCmd);
		else {
			suspectCmd.setExecutor(new SuspectCommand());
			suspectCmd.setTabCompleter(new SuspectCommand());
		}

		getCommand("mod").setExecutor(new ModCommand());
	}

	@Override
	public void onDisable() {
		for (Player p : Utils.getOnlinePlayers()) {
			SpigotNegativityPlayer.removeFromCache(p.getUniqueId());
			PacketListenerAPI.removePlayer(p);
		}
		Database.close();
		Stats.updateStats(StatsType.ONLINE, 0 + "");
		invTimer.cancel();
		clickTimer.cancel();
		packetTimer.cancel();
		runSpawnFakePlayer.cancel();
		timeTimeBetweenAlert.cancel();
	}

	public static SpigotNegativity getInstance() {
		return INSTANCE;
	}
	
	public static boolean alertMod(ReportType type, Player p, Cheat c, int reliability, String proof) {
		return alertMod(type, p, c, reliability, proof, "", "");
	}
	
	public static boolean alertMod(ReportType type, Player p, Cheat c, int reliability, String proof, String hover_proof) {
		return alertMod(type, p, c, reliability, proof, hover_proof, "");
	}

	public static boolean alertMod(ReportType type, Player p, Cheat c, int reliability, String proof,
			String hover_proof, String stats_send) {
		if(!c.isActive())
			return false;
		if(reliability < 55)
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
		if (p.getItemInHand() != null)
			if (ItemUseBypass.ITEM_BYPASS.containsKey(p.getItemInHand().getType().name()))
				if (ItemUseBypass.ITEM_BYPASS.get(p.getItemInHand().getType().name()).getWhen().equals(WhenBypass.ALWAYS))
					return false;
		Block target = Utils.getTargetBlock(p, 5);
		if(target != null && !target.getType().equals(Material.AIR))
			if (ItemUseBypass.ITEM_BYPASS.containsKey(target.getType().name()))
				if (ItemUseBypass.ITEM_BYPASS.get(target.getType().name()).getWhen().equals(WhenBypass.LOOKING))
					return false;
		
		int ping = Utils.getPing(p);
		long currentTimeMilli = System.currentTimeMillis();
		if (np.TIME_INVINCIBILITY > currentTimeMilli || reliability < 30 || ping > c.getMaxAlertPing()
				|| ((double) ((Damageable) p).getHealth()) == 0.0D
				|| getInstance().getConfig().getInt("tps_alert_stop") > Utils.getLastTPS() || ping < 0 || np.isFreeze)
			return false;
		Bukkit.getPluginManager().callEvent(new PlayerCheatEvent(p, c, reliability));
		if (hasBypass && Perm.hasPerm(SpigotNegativityPlayer.getNegativityPlayer(p),
				"bypass." + c.getKey().toLowerCase())) {
			PlayerCheatBypassEvent bypassEvent = new PlayerCheatBypassEvent(p, c, reliability);
			Bukkit.getPluginManager().callEvent(bypassEvent);
			if (!bypassEvent.isCancelled())
				return false;
		}
		PlayerCheatAlertEvent alert = new PlayerCheatAlertEvent(type, p, c, reliability,
				c.getReliabilityAlert() < reliability, ping, proof, hover_proof, stats_send);
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
			Stats.updateStats(StatsType.CHEAT, c.getKey(), reliability + "", stats_send);
			return false;
		}

		if (BanUtils.banIfNeeded(np, c, reliability) != null) {
			Stats.updateStats(StatsType.CHEAT, c.getKey(), reliability + "", stats_send);
			return false;
		}
		int timeBetweenTwoAlert = Adapter.getAdapter().getIntegerInConfig("time_between_alert");
		if(timeBetweenTwoAlert != -1) {
			HashMap<Cheat, Long> time_alert = (TIME_LAST_CHEAT_ALERT.containsKey(p) ? TIME_LAST_CHEAT_ALERT.get(p) : new HashMap<>());
			if(time_alert.containsKey(c)) {
				if(((currentTimeMilli - time_alert.get(c)) < timeBetweenTwoAlert)) {
					List<PlayerCheatAlertEvent> tempList = np.ALERT_NOT_SHOWED.containsKey(c) ? np.ALERT_NOT_SHOWED.get(c) : new ArrayList<>();
					tempList.add(alert);
					np.ALERT_NOT_SHOWED.put(c, tempList);
					return true;
				}
			}
			time_alert.put(c, currentTimeMilli);
			TIME_LAST_CHEAT_ALERT.put(p, time_alert);
		}

		sendAlertMessage(type, np, p, c, ping, reliability, hover_proof, alert, 1, stats_send);
		np.ALERT_NOT_SHOWED.remove(c);
		return true;
	}

	public static void sendAlertMessage(ReportType type, SpigotNegativityPlayer np, Player p, Cheat c, int ping, int reliability,
										String hover_proof, PlayerCheatAlertEvent alert, int alertsCount, String stats_send) {
		Stats.updateStats(StatsType.CHEAT, c.getKey(), reliability + "", stats_send);
		if (log_console)
			INSTANCE.getLogger()
					.info("New " + type.getName() + " for " + p.getName() + " (UUID: " + p.getUniqueId().toString()
							+ ") (ping: " + ping + ") : suspected of cheating (" + c.getName() + ") Reliability: "
							+ reliability);
		if (isOnBungeecord) {
			sendAlertMessage(p, c.getName(), reliability, ping, hover_proof, alertsCount);
		} else {
			boolean hasPermPeople = false;
			for (Player pl : Utils.getOnlinePlayers()) {
				boolean basicPerm = Perm.hasPerm(SpigotNegativityPlayer.getNegativityPlayer(pl), "showAlert");
				ShowAlertPermissionEvent permissionEvent = new ShowAlertPermissionEvent(p, np, basicPerm);
				Bukkit.getPluginManager().callEvent(permissionEvent);
				if (permissionEvent.isCancelled())
					continue;
				if (permissionEvent.hasBasicPerm()) {
					if(np.ALERT_NOT_SHOWED.containsKey(c) && np.ALERT_NOT_SHOWED.get(c).size() > 1) {
						new ClickableText().addRunnableHoverEvent(
								Messages.getMessage(pl, "negativity.alert_multiple", "%name%", p.getName(), "%cheat%", c.getName(),
										"%reliability%", String.valueOf(100), "%nb%", String.valueOf(np.ALERT_NOT_SHOWED.get(c).size())),
								Messages.getMessage(pl, "negativity.alert_hover", "%reliability%",
										String.valueOf(100), "%ping%", String.valueOf(ping))
										+ (hover_proof.equalsIgnoreCase("") ? "" : "\n" + hover_proof),
								"/negativity " + p.getName()).sendToPlayer(pl);
					} else {
						new ClickableText().addRunnableHoverEvent(
								Messages.getMessage(pl, "negativity.alert", "%name%", p.getName(), "%cheat%", c.getName(),
										"%reliability%", String.valueOf(reliability)),
								Messages.getMessage(pl, "negativity.alert_hover", "%reliability%",
										String.valueOf(reliability), "%ping%", String.valueOf(ping))
										+ (hover_proof.equalsIgnoreCase("") ? "" : "\n" + hover_proof),
								"/negativity " + p.getName()).sendToPlayer(pl);
					}
					hasPermPeople = true;
				}
			}
			if(!hasPermPeople)
				alerts.add(alert);
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

	private static void logProof(SpigotNegativityPlayer np, ReportType type, Player p, Cheat c, int reliability,
			String proof, int ping) {
		if(log)
			np.logProof(new Timestamp(System.currentTimeMillis()) + ": (" + ping + "ms) " + reliability + "% " + c.getKey()
				+ " > " + proof + ". TPS: " + Arrays.toString(Utils.getTPS()));
	}

	public static void manageAutoVerif(Player p) {
		SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(p);
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

	private Object getPrivateField(Object object, String field)
			throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		Field objectField = object.getClass().getDeclaredField(field);
		objectField.setAccessible(true);
		return objectField.get(object);
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
			Object result = getPrivateField(this.getServer().getPluginManager(), "commandMap");
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
}
