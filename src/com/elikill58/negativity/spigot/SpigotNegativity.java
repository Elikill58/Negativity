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
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.scheduler.BukkitRunnable;

import com.elikill58.negativity.spigot.commands.BanCommand;
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
import com.elikill58.negativity.spigot.timers.ActualizeClickTimer;
import com.elikill58.negativity.spigot.timers.ActualizeInvTimer;
import com.elikill58.negativity.spigot.timers.TimerAnalyzePacket;
import com.elikill58.negativity.spigot.timers.TimerSpawnFakePlayer;
import com.elikill58.negativity.spigot.utils.ReportType;
import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.Database;
import com.elikill58.negativity.universal.ItemUseBypass;
import com.elikill58.negativity.universal.ItemUseBypass.WhenBypass;
import com.elikill58.negativity.universal.Stats;
import com.elikill58.negativity.universal.Stats.StatsType;
import com.elikill58.negativity.universal.SuspectManager;
import com.elikill58.negativity.universal.TranslatedMessages;
import com.elikill58.negativity.universal.UniversalUtils;
import com.elikill58.negativity.universal.Version;
import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.adapter.SpigotAdapter;
import com.elikill58.negativity.universal.ban.Ban;
import com.elikill58.negativity.universal.permissions.Perm;

@SuppressWarnings("deprecation")
public class SpigotNegativity extends JavaPlugin {

	private static SpigotNegativity INSTANCE;
	public static boolean isOnBungeecord = false, log = false, hasBypass = true, essentialsSupport = false;
	public static Material MATERIAL_CLOSE = Material.REDSTONE;
	private BukkitRunnable clickTimer = null, invTimer = null, packetTimer = null, runSpawnFakePlayer = null;
	public static List<PlayerCheatAlertEvent> alerts = new ArrayList<>();

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
		File confDir = new File(getDataFolder().getAbsolutePath() + File.separator + "config.yml");
		new File(getDataFolder().getAbsolutePath() + File.separator + "user").mkdirs();
		if (!confDir.exists()) {
			getLogger().info("------ Negativity Information ------");
			getLogger().info("");
			getLogger().info("English:");
			getLogger().info(" > Thanks for downloading Negativity :)");
			getLogger().info("I'm trying to make the better anti-cheat has possible.");
			getLogger().info(
					"If there is any false positive, problem or if you have a suggestion you can contact me via:");
			getLogger().info(
					"Discord: @Elikill58#0743, mail: arpetzouille@gmail.com, and Elikill58 in all other web site like Twitter, Spigotmc ...");
			getLogger().info("");
			getLogger().info("French:");
			getLogger().info(" > Merci d'avoir téléchargé Negativity :)");
			getLogger().info("J'essaie de faire le meilleur anti-cheat possible.");
			getLogger().info(
					"S'il y a des faux positifs, des problémes ou si vous avez des suggestions, vous pouvez me contacter via:");
			getLogger().info(
					"Discord: @Elikill58#0743, mail: arpetzouille@gmail.com, et Elikill58 sur tout les autres site comme Twitter, Spigotmc ...");
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
		hasBypass = ada.getBooleanInConfig("Permissions.bypass.active");

		new Metrics(this).addCustomChart(new Metrics.SimplePie("custom_permission", () -> String.valueOf(Database.hasCustom)));

		PluginManager pm = Bukkit.getPluginManager();
		pm.registerEvents(new PlayersEvents(), this);
		pm.registerEvents(new InventoryEvents(), this);
		pm.registerEvents(new FightManager(), this);

		Messenger messenger = getServer().getMessenger();
		ChannelEvents channelEvents = new ChannelEvents();
		if (Version.getVersion().isNewerOrEquals(Version.V1_13)) {
			String channelNameNegativity = "custom:negativity";
			if (!messenger.getOutgoingChannels().contains(channelNameNegativity))
				messenger.registerOutgoingPluginChannel(this, channelNameNegativity);
			if (!messenger.getIncomingChannels().contains(channelNameNegativity))
				messenger.registerIncomingPluginChannel(this, channelNameNegativity, channelEvents);
			if (!messenger.getOutgoingChannels().contains("test:fml"))
				messenger.registerOutgoingPluginChannel(this, "test:fml");
			if (!messenger.getIncomingChannels().contains("test:fml"))
				messenger.registerIncomingPluginChannel(this, "test:fml", channelEvents);
		} else {
			String channelNameNegativity = "negativity";
			if (!messenger.getOutgoingChannels().contains(channelNameNegativity))
				messenger.registerOutgoingPluginChannel(this, channelNameNegativity);
			if (!messenger.getIncomingChannels().contains(channelNameNegativity))
				messenger.registerIncomingPluginChannel(this, channelNameNegativity, channelEvents);
			if (!messenger.getOutgoingChannels().contains("FML|HS"))
				messenger.registerOutgoingPluginChannel(this, "FML|HS");
			if (!messenger.getIncomingChannels().contains("FML|HS"))
				messenger.registerIncomingPluginChannel(this, "FML|HS", channelEvents);
		}
		for (Player p : Utils.getOnlinePlayers()) {
			PacketListenerAPI.addPlayer(p);
			manageAutoVerif(p);
			/*
			 * for (Player pl : Utils.getOnlinePlayers()) pl.showPlayer(p);
			 */
			Utils.sendUpdateMessageIfNeed(p);
		}
		(clickTimer = new ActualizeClickTimer()).runTaskTimer(this, 20, 20);
		(invTimer = new ActualizeInvTimer()).runTaskTimerAsynchronously(this, 5, 5);
		(packetTimer = new TimerAnalyzePacket()).runTaskTimer(this, 20, 20);
		(runSpawnFakePlayer = new TimerSpawnFakePlayer()).runTaskTimer(this, 20, 20 * 60 * 20);
		
		for(Cheat c : Cheat.CHEATS) {
			if(c.isActive()) {
				if(c.hasListener()) {
					pm.registerEvents((Listener) c, this);
				}
				c.run();
			}
		}
		
		loadCommand();

		if (getConfig().get("items") != null) {
			ConfigurationSection cs = getConfig().getConfigurationSection("items");
			for (String s : cs.getKeys(false))
				new ItemUseBypass(s, cs.getString(s + ".cheats"), cs.getString(s + ".when"));
		}
		if (UniversalUtils.hasInternet()
				&& !UniversalUtils.isLatestVersion(getDescription().getVersion())) {
			getLogger().info("New version available (" + UniversalUtils.getLatestVersion().orElse("unknow")
					+ "). Download it here: https://www.spigotmc.org/resources/48399/");
		}
		getServer().getScheduler().runTaskAsynchronously(this, new Runnable() {
			@Override
			public void run() {
				Stats.updateStats(StatsType.ONLINE, 1);
				Stats.updateStats(StatsType.PORT, Bukkit.getServer().getPort());
			}
		});
		ada.loadLang();
		
		Plugin essentialsPlugin = Bukkit.getPluginManager().getPlugin("Essentials");
	    if (essentialsPlugin != null)
	    	essentialsSupport = true;
	}
	
	private void loadCommand() {
		PluginCommand negativity = getCommand("negativity");
		NegativityCommand negativityCmd = new NegativityCommand();
		negativity.setExecutor(negativityCmd);
		negativity.setTabCompleter(negativityCmd);

		PluginCommand reportCmd = getCommand("report");
		if (!getConfig().getBoolean("report_command"))
			unRegisterBukkitCommand(reportCmd);
		else
			reportCmd.setExecutor(new ReportCommand());

		PluginCommand banCmd = getCommand("nban");
		List<String> banAlias = new ArrayList<String>();
		banAlias.add("negban");
		banCmd.setAliases(banAlias);
		if (!getConfig().getBoolean("ban_command"))
			unRegisterBukkitCommand(banCmd);
		else
			banCmd.setExecutor(new BanCommand());

		PluginCommand unbanCmd = getCommand("nunban");
		List<String> unbanAlias = new ArrayList<String>();
		unbanAlias.add("negunban");
		unbanCmd.setAliases(unbanAlias);
		if (!getConfig().getBoolean("unban_command"))
			unRegisterBukkitCommand(unbanCmd);
		else
			unbanCmd.setExecutor(new UnbanCommand());

		PluginCommand langCmd = getCommand("lang");
		if (!TranslatedMessages.activeTranslation)
			unRegisterBukkitCommand(langCmd);
		else
			langCmd.setExecutor(new LangCommand());

		PluginCommand suspectCmd = getCommand("suspect");
		if (!SuspectManager.ENABLED)
			unRegisterBukkitCommand(suspectCmd);
		else
			suspectCmd.setExecutor(new SuspectCommand());

		getCommand("mod").setExecutor(new ModCommand());
	}

	@Override
	public void onDisable() {
		for (Player p : Utils.getOnlinePlayers())
			PacketListenerAPI.removePlayer(p);
		Database.close();
		Stats.updateStats(StatsType.ONLINE, 0);
		invTimer.cancel();
		clickTimer.cancel();
		packetTimer.cancel();
		runSpawnFakePlayer.cancel();
	}

	public static SpigotNegativity getInstance() {
		return INSTANCE;
	}

	public static boolean alertMod(ReportType type, Player p, Cheat c, int reliability, String proof) {
		return alertMod(type, p, c, reliability, proof, "");
	}

	public static boolean alertMod(ReportType type, Player p, Cheat c, int reliability, String proof, String hover_proof) {
		SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(p);
		if (c.equals(Cheat.fromString("BLINK").get()) && !np.already_blink) {
			np.already_blink = true;
			return false;
		}
		if (np.isInFight && c.isBlockedInFight())
			return false;
		if (essentialsSupport && EssentialsSupport.checkEssentialsPrecondition(p))
				return false;
		if (p.getItemInHand() != null)
			if (ItemUseBypass.ITEM_BYPASS.containsKey(p.getItemInHand().getType()))
				if (ItemUseBypass.ITEM_BYPASS.get(p.getItemInHand().getType()).getWhen().equals(WhenBypass.ALWAYS))
					return false;
		int ping = Utils.getPing(p);
		if (np.TIME_INVINCIBILITY > System.currentTimeMillis() || reliability < 30 || ping > c.getMaxAlertPing()
				|| ((double) ((Damageable) p).getHealth()) == 0.0D
				|| getInstance().getConfig().getInt("tps_alert_stop") > Utils.getLastTPS() || ping < 0 || np.isFreeze)
			return false;
		Bukkit.getPluginManager().callEvent(new PlayerCheatEvent(p, c, reliability));
		if (hasBypass && Perm.hasPerm(SpigotNegativityPlayer.getNegativityPlayer(p),
				"Permissions.bypass." + c.getKey().toLowerCase())) {
			PlayerCheatBypassEvent bypassEvent = new PlayerCheatBypassEvent(p, c, reliability);
			Bukkit.getPluginManager().callEvent(bypassEvent);
			if (!bypassEvent.isCancelled())
				return false;
		}
		logProof(type, p, c, reliability, proof, ping);
		PlayerCheatAlertEvent alert = new PlayerCheatAlertEvent(p, c, reliability,
				c.getReliabilityAlert() < reliability, ping, proof, hover_proof);
		Bukkit.getPluginManager().callEvent(alert);
		if (alert.isCancelled() || !alert.isAlert())
			return false;
		if (c.allowKick() && c.getAlertToKick() <= np.getWarn(c)) {
			PlayerCheatKickEvent kick = new PlayerCheatKickEvent(p, c, reliability);
			Bukkit.getPluginManager().callEvent(kick);
			if (!kick.isCancelled())
				p.kickPlayer(Messages.getMessage(p, "kick", "%cheat%", c.getName()));
		}
		Stats.updateStats(StatsType.CHEATS, p.getName() + ": " + c.getKey() + " (Reliability: " + reliability + ") Ping: "
				+ ping + " Type: " + type.getName());
		if(Ban.isBanned(np))
			return false;
		Ban.manageBan(c, np, reliability);
		if (isOnBungeecord)
			sendMessage(p, c.getName(), String.valueOf(reliability), String.valueOf(ping), hover_proof);
		else {
			if (log)
				INSTANCE.getLogger()
						.info("New " + type.getName() + " for " + p.getName() + " (UUID: " + p.getUniqueId().toString()
								+ ") (ping: " + ping + ") : suspected of cheating (" + c.getName() + ") Reliability: "
								+ reliability);
			for (Player pl : Utils.getOnlinePlayers())
				if (Perm.hasPerm(SpigotNegativityPlayer.getNegativityPlayer(pl), "showAlert"))
					new ClickableText().addRunnableHoverEvent(
							Messages.getMessage(pl, "negativity.alert", "%name%", p.getName(), "%cheat%", c.getName(),
									"%reliability%", String.valueOf(reliability)),
							Messages.getMessage(pl, "negativity.alert_hover", "%reliability%",
									String.valueOf(reliability), "%ping%", String.valueOf(ping))
									+ (hover_proof.equalsIgnoreCase("") ? "" : "\n" + hover_proof),
							"/negativity " + p.getName()).sendToPlayer(pl);
		}
		return true;
	}

	private static void sendMessage(Player p, String cheatName, String reliability, String ping, String hover) {
		try (ByteArrayOutputStream ba = new ByteArrayOutputStream(); DataOutputStream out = new DataOutputStream(ba)) {
			out.writeUTF(p.getName() + "/**/" + cheatName + "/**/" + reliability + "/**/" + ping + "/**/" + hover);
			p.sendPluginMessage(SpigotNegativity.getInstance(), "Negativity", ba.toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void sendReportMessage(Player p, String reportMsg) {
		try (ByteArrayOutputStream ba = new ByteArrayOutputStream(); DataOutputStream out = new DataOutputStream(ba)) {
			out.writeUTF(reportMsg);
			p.sendPluginMessage(SpigotNegativity.getInstance(), "Negativity", ba.toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void logProof(ReportType type, Player p, Cheat c, int reliability, String proof, int ping) {
		Timestamp stamp = new Timestamp(System.currentTimeMillis());
		SpigotNegativityPlayer.getNegativityPlayer(p).logProof(stamp,
				stamp + ": (" + ping + "ms) " + reliability + "% " + c.getKey() + " > " + proof);
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
		Class<?> clazz = object.getClass();
		Field objectField = clazz.getDeclaredField(field);
		objectField.setAccessible(true);
		Object result = objectField.get(object);
		objectField.setAccessible(false);
		return result;
	}

	private Object getKnownCommands(Object object) {
		try {
			Class<?> clazz = object.getClass();
			Field objectField = clazz.getDeclaredField("knownCommands");
			objectField.setAccessible(true);
			Object result = objectField.get(object);
			objectField.setAccessible(false);
			return result;
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
			SimpleCommandMap commandMap = (SimpleCommandMap) result;
			Object map = getKnownCommands(commandMap);
			HashMap<?, ?> knownCommands = (HashMap<?, ?>) map;
			if(knownCommands.containsKey(cmd.getName()))
				knownCommands.remove(cmd.getName());
			for (String alias : cmd.getAliases())
				if (knownCommands.containsKey(alias) && knownCommands.get(alias).toString().contains(this.getName()))
					knownCommands.remove(alias);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
