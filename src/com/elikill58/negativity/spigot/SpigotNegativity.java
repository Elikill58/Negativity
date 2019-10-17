package com.elikill58.negativity.spigot;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
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
import com.elikill58.negativity.spigot.packets.PacketListenerAPI;
import com.elikill58.negativity.spigot.packets.PacketManager;
import com.elikill58.negativity.spigot.support.EssentialsSupport;
import com.elikill58.negativity.spigot.support.WorldGuardAPI;
import com.elikill58.negativity.spigot.timers.ActualizeClickTimer;
import com.elikill58.negativity.spigot.timers.ActualizeInvTimer;
import com.elikill58.negativity.spigot.timers.TimerAnalyzePacket;
import com.elikill58.negativity.spigot.timers.TimerSpawnFakePlayer;
import com.elikill58.negativity.spigot.timers.TimerTimeBetweenAlert;
import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.Database;
import com.elikill58.negativity.universal.ItemUseBypass;
import com.elikill58.negativity.universal.ItemUseBypass.WhenBypass;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.Stats;
import com.elikill58.negativity.universal.Stats.StatsType;
import com.elikill58.negativity.universal.SuspectManager;
import com.elikill58.negativity.universal.TranslatedMessages;
import com.elikill58.negativity.universal.Version;
import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.adapter.SpigotAdapter;
import com.elikill58.negativity.universal.ban.Ban;
import com.elikill58.negativity.universal.ban.support.AdvancedBanSupport;
import com.elikill58.negativity.universal.ban.support.EssentialsBanSupport;
import com.elikill58.negativity.universal.ban.support.MaxBansSupport;
import com.elikill58.negativity.universal.permissions.Perm;
import com.elikill58.negativity.universal.utils.UniversalUtils;

@SuppressWarnings("deprecation")
public class SpigotNegativity extends JavaPlugin {

	private static SpigotNegativity INSTANCE;
	public static boolean isOnBungeecord = false, log = false, log_console = false, hasBypass = true, essentialsSupport = false,
			worldGuardSupport = false;
	public static Material MATERIAL_CLOSE = Material.REDSTONE;
	private BukkitRunnable clickTimer = null, invTimer = null, packetTimer = null, runSpawnFakePlayer = null, timeTimeBetweenAlert = null;
	public static List<PlayerCheatAlertEvent> alerts = new ArrayList<>();
	private static final HashMap<Player, HashMap<Cheat, Long>> TIME_LAST_CHEAT_ALERT = new HashMap<>();
	
	@Override
	public void onEnable() {
		INSTANCE = this;
		if (Adapter.getAdapter() == null)
			Adapter.setAdapter(new SpigotAdapter(this, getConfig()));
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
		String channelNameNegativity = "", channelNameFml = "";
		if (Version.getVersion().isNewerOrEquals(Version.V1_13)) {
			channelNameNegativity = "custom:negativity";
			channelNameFml = "test:fml";
		} else {
			channelNameNegativity = "negativity";
			channelNameFml = "FML|HS";
		}
		if (!messenger.getOutgoingChannels().contains(channelNameNegativity))
			messenger.registerOutgoingPluginChannel(this, channelNameNegativity);
		if (!messenger.getIncomingChannels().contains(channelNameNegativity))
			messenger.registerIncomingPluginChannel(this, channelNameNegativity, channelEvents);
		if (!messenger.getOutgoingChannels().contains(channelNameFml))
			messenger.registerOutgoingPluginChannel(this, channelNameFml);
		if (!messenger.getIncomingChannels().contains(channelNameFml))
			messenger.registerIncomingPluginChannel(this, channelNameFml, channelEvents);
		
		for (Player p : Utils.getOnlinePlayers()) {
			PacketListenerAPI.addPlayer(p);
			manageAutoVerif(p);
			Utils.sendUpdateMessageIfNeed(p);
		}
		(clickTimer = new ActualizeClickTimer()).runTaskTimer(this, 20, 20);
		(invTimer = new ActualizeInvTimer()).runTaskTimerAsynchronously(this, 5, 5);
		(packetTimer = new TimerAnalyzePacket()).runTaskTimer(this, 20, 20);
		(runSpawnFakePlayer = new TimerSpawnFakePlayer()).runTaskTimer(this, 20, 20 * 60 * 20);
		(timeTimeBetweenAlert = new TimerTimeBetweenAlert()).runTaskTimer(this, 20, 20);

		for (Cheat c : Cheat.values()) {
			if (c.isActive() && c.hasListener()) {
				pm.registerEvents((Listener) c, this);
			}
		}

		loadCommand();

		if (getConfig().get("items") != null) {
			ConfigurationSection cs = getConfig().getConfigurationSection("items");
			for (String s : cs.getKeys(false))
				new ItemUseBypass(s, cs.getString(s + ".cheats"), cs.getString(s + ".when"));
		}
		if (UniversalUtils.hasInternet() && !UniversalUtils.isLatestVersion(getDescription().getVersion())) {
			getLogger().info("New version available (" + UniversalUtils.getLatestVersion().orElse("unknow")
					+ "). Download it here: https://www.spigotmc.org/resources/48399/");
		}
		getServer().getScheduler().runTaskAsynchronously(this, new Runnable() {
			@Override
			public void run() {
				Stats.loadStats();
				Stats.updateStats(StatsType.ONLINE, 1);
				Stats.updateStats(StatsType.PORT, Bukkit.getServer().getPort());
			}
		});
		ada.loadLang();

		if (Bukkit.getPluginManager().getPlugin("Essentials") != null) {
			essentialsSupport = true;
			if(ada.getStringInConfig("ban.other_plugin.plugin_used").equalsIgnoreCase("essentials"))
				Ban.addBanPlugin(new EssentialsBanSupport());
		}
		if (Bukkit.getPluginManager().getPlugin("WorldGuard") != null) {
			worldGuardSupport = true;
			WorldGuardAPI.init();
		}

		if (essentialsSupport || worldGuardSupport) {
			String s = (essentialsSupport
					? (worldGuardSupport ? "Essentials and WorldGuard plugins are detected."
							: "Essentials plugin detected.")
					: "WorldGuard plugin detected.");
			getLogger().info(s + " Loading support ..." + (ada.getStringInConfig("ban.other_plugin.plugin_used").equalsIgnoreCase("essentials") && essentialsSupport ? " (Essentials also used as Ban plugin)" : ""));
		}

		if (Bukkit.getPluginManager().getPlugin("MaxBans") != null && ada.getStringInConfig("ban.other_plugin.plugin_used").equalsIgnoreCase("MaxBans")) {
			Ban.addBanPlugin(new MaxBansSupport());
			getLogger().info("Ban plugin MaxBans found ! Loading support ...");
		}

		if (Bukkit.getPluginManager().getPlugin("AdvancedBan") != null && ada.getStringInConfig("ban.other_plugin.plugin_used").equalsIgnoreCase("AdvancedBan")) {
			Ban.addBanPlugin(new AdvancedBanSupport());
			getLogger().info("Ban plugin AdvancedBan found ! Loading support ...");
		}
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
		if (!TranslatedMessages.activeTranslation)
			unRegisterBukkitCommand(langCmd);
		else
			langCmd.setExecutor(new LangCommand());

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
			SpigotNegativityPlayer.getNegativityPlayer(p).destroy(false);
			PacketListenerAPI.removePlayer(p);
		}
		Database.close();
		Stats.updateStats(StatsType.ONLINE, 0);
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
		return alertMod(type, p, c, reliability, proof, "");
	}

	public static boolean alertMod(ReportType type, Player p, Cheat c, int reliability, String proof,
			String hover_proof) {
		if(!c.isActive())
			return false;
		SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(p);
		if (!np.already_blink && c.equals(Cheat.fromString("BLINK").get())) {
			np.already_blink = true;
			return false;
		}
		if (np.isInFight && c.isBlockedInFight())
			return false;
		if (c.equals(Cheat.forKey("FLY").get()) && p.hasPermission("essentials.fly"))
			return false;
		if (essentialsSupport && EssentialsSupport.checkEssentialsPrecondition(p))
			return false;
		if (p.getItemInHand() != null)
			if (ItemUseBypass.ITEM_BYPASS.containsKey(p.getItemInHand().getType()))
				if (ItemUseBypass.ITEM_BYPASS.get(p.getItemInHand().getType()).getWhen().equals(WhenBypass.ALWAYS))
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
		np.addWarn(c, reliability);
		logProof(np, type, p, c, reliability, proof, ping);
		PlayerCheatAlertEvent alert = new PlayerCheatAlertEvent(p, c, reliability,
				c.getReliabilityAlert() < reliability, ping, proof, hover_proof);
		Bukkit.getPluginManager().callEvent(alert);
		if (alert.isCancelled() || !alert.isAlert())
			return false;
		if (c.allowKick() && c.getAlertToKick() <= np.getWarn(c)) {
			PlayerCheatKickEvent kick = new PlayerCheatKickEvent(p, c, reliability);
			Bukkit.getPluginManager().callEvent(kick);
			if (!kick.isCancelled())
				p.kickPlayer(Messages.getMessage(p, "kick.kicked", "%cheat%", c.getName(), "%reason%", c.getName(), "%playername%", p.getName(), "%cheat%", c.getName()));
		}
		Ban.manageBan(c, np, reliability);
		if (Ban.isBanned(np.getAccount()))
			return false;
		int timeBetweenTwoAlert = Adapter.getAdapter().getIntegerInConfig("time_between_alert");
		if(timeBetweenTwoAlert != -1) {
			HashMap<Cheat, Long> time_alert = (TIME_LAST_CHEAT_ALERT.containsKey(p) ? TIME_LAST_CHEAT_ALERT.get(p) : new HashMap<>());
			if(time_alert.containsKey(c)) {
				if(((currentTimeMilli - time_alert.get(c)) < timeBetweenTwoAlert)) {
					np.ALERT_NOT_SHOWED.put(c, np.ALERT_NOT_SHOWED.containsKey(c) ? np.ALERT_NOT_SHOWED.get(c) + 1 : 1);
					return true;
				}
			}
				//else reliability = 100;
			time_alert.put(c, currentTimeMilli);
			TIME_LAST_CHEAT_ALERT.put(p, time_alert);
		}
		
		if (isOnBungeecord)
			sendMessage(p, c.getName(), String.valueOf(reliability), String.valueOf(ping), hover_proof);
		else {
			if (log_console)
				INSTANCE.getLogger()
						.info("New " + type.getName() + " for " + p.getName() + " (UUID: " + p.getUniqueId().toString()
								+ ") (ping: " + ping + ") : suspected of cheating (" + c.getName() + ") Reliability: "
								+ reliability);
			boolean hasPermPeople = false;
			for (Player pl : Utils.getOnlinePlayers())
				if (Perm.hasPerm(SpigotNegativityPlayer.getNegativityPlayer(pl), "showAlert")) {
					if(np.ALERT_NOT_SHOWED.containsKey(c) && np.ALERT_NOT_SHOWED.get(c) > 0) {
						new ClickableText().addRunnableHoverEvent(
								Messages.getMessage(pl, "negativity.alert_multiple", "%name%", p.getName(), "%cheat%", c.getName(),
										"%reliability%", String.valueOf(100), "%nb%", String.valueOf(np.ALERT_NOT_SHOWED.get(c))),
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
			if(!hasPermPeople)
				alerts.add(alert);
			np.ALERT_NOT_SHOWED.remove(c);
		}
		return true;
	}

	private static void sendMessage(Player p, String cheatName, String reliability, String ping, String hover) {
		sendReportMessage(p, p.getName() + "/**/" + cheatName + "/**/" + reliability + "/**/" + ping + "/**/" + hover);
	}

	public static void sendReportMessage(Player p, String reportMsg) {
		try (ByteArrayOutputStream ba = new ByteArrayOutputStream(); DataOutputStream out = new DataOutputStream(ba)) {
			out.writeUTF(reportMsg);
			p.sendPluginMessage(SpigotNegativity.getInstance(), "negativity", ba.toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void logProof(SpigotNegativityPlayer np, ReportType type, Player p, Cheat c, int reliability,
			String proof, int ping) {
		np.logProof(new Timestamp(System.currentTimeMillis()) + ": (" + ping + "ms) " + reliability + "% " + c.getKey()
				+ " > " + proof);
	}

	public static void manageAutoVerif(Player p) {
		SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(p);
		if (Cheat.ALL.isActive()) {
			np.startAllAnalyze();
			return;
		}
		boolean needPacket = false;
		for (Cheat c : Cheat.values())
			if (c.isActive() || Cheat.ALL.isActive()) {
				if (c.isAutoVerif() || Cheat.ALL.isAutoVerif()) {
					np.startAnalyze(c);
					if (c.needPacket() || Cheat.ALL.needPacket())
						needPacket = true;
				}
			}
		if (needPacket)
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
