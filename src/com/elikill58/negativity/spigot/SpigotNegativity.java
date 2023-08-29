package com.elikill58.negativity.spigot;

import static com.elikill58.negativity.universal.verif.VerificationManager.hasVerifications;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.StringJoiner;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
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
import com.elikill58.negativity.spigot.events.ElytraEvents;
import com.elikill58.negativity.spigot.events.FightManager;
import com.elikill58.negativity.spigot.events.PlayersEvents;
import com.elikill58.negativity.spigot.events.ServerCrasherEvents;
import com.elikill58.negativity.spigot.inventories.AbstractInventory;
import com.elikill58.negativity.spigot.listeners.PlayerCheatAlertEvent;
import com.elikill58.negativity.spigot.listeners.PlayerCheatBypassEvent;
import com.elikill58.negativity.spigot.listeners.PlayerCheatEvent;
import com.elikill58.negativity.spigot.listeners.PlayerCheatKickEvent;
import com.elikill58.negativity.spigot.listeners.ShowAlertPermissionEvent;
import com.elikill58.negativity.spigot.packets.NegativityPacketManager;
import com.elikill58.negativity.spigot.support.EssentialsSupport;
import com.elikill58.negativity.spigot.support.FloodGateSupportManager;
import com.elikill58.negativity.spigot.timers.ActualizeInvTimer;
import com.elikill58.negativity.spigot.timers.TimerAnalyzePacket;
import com.elikill58.negativity.spigot.timers.TimerTimeBetweenAlert;
import com.elikill58.negativity.spigot.utils.ItemUtils;
import com.elikill58.negativity.spigot.utils.PacketUtils;
import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.spigot.webhooks.WebhookManager;
import com.elikill58.negativity.spigot.webhooks.messages.AlertWebhookMessage;
import com.elikill58.negativity.spigot.webhooks.messages.WebhookMessage.WebhookMessageType;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.Cheat.CheatHover;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.Database;
import com.elikill58.negativity.universal.ItemUseBypass;
import com.elikill58.negativity.universal.ItemUseBypass.WhenBypass;
import com.elikill58.negativity.universal.ProxyCompanionManager;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.Stats;
import com.elikill58.negativity.universal.Version;
import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.adapter.SpigotAdapter;
import com.elikill58.negativity.universal.ban.BanManager;
import com.elikill58.negativity.universal.ban.BanUtils;
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
import com.elikill58.negativity.universal.verif.VerificationManager;

public class SpigotNegativity extends JavaPlugin {

	private static SpigotNegativity INSTANCE;
	public static boolean log = false, log_console = false, hasBypass = false, reloading = false, essentialsSupport = false,
			worldGuardSupport = false, gadgetMenuSupport = false, viaVersionSupport = false, protocolSupportSupport = false, isCraftBukkit = false, isMagma = false;
	public static double tps_alert_stop = 19.0;
	private BukkitRunnable invTimer = null, packetTimer = null, runSpawnFakePlayer = null, timeTimeBetweenAlert = null;
	public static String CHANNEL_NAME_FML = "";
	public static String CHANNEL_NAME_BRAND = "";
	private static int timeBetweenAlert = -1;
	private NegativityPacketManager packetManager;
	
	@Override
	public void onEnable() {
		INSTANCE = this;
		reloading = true;
		if (Adapter.getAdapter() == null)
			Adapter.setAdapter(new SpigotAdapter(this));
		Version v = Version.getVersion(Utils.VERSION);
		if (v.equals(Version.V1_7)) {
			getLogger().severe("The 1.7 version of Minecraft is not supported. The 1.13 version is the latest one to do it.");
			setEnabled(false);
			return;
		}
		if (v.equals(Version.HIGHER))
			getLogger().warning("Unknow server version " + Utils.VERSION + " ! Some problems can appears.");
		else
			getLogger().info("Detected server version: " + v.name().toLowerCase(Locale.ROOT) + " (" + Utils.VERSION + ")");
		
		try {
			Class.forName("org.spigotmc.SpigotConfig");
			isCraftBukkit = false;
		} catch (ClassNotFoundException e) {
			isCraftBukkit = true;
		}
		
		packetManager = new NegativityPacketManager(this);
		new File(getDataFolder().getAbsolutePath() + File.separator + "user" + File.separator + "proof").mkdirs();
		if (!new File(getDataFolder().getAbsolutePath() + File.separator + "config.yml").exists()) {
			getLogger().info("------ Negativity Information ------");
			getLogger().info("");
			getLogger().info(" > Thanks for downloading Negativity :)");
			getLogger().info("I'm trying to make the best anti-cheat as possible.");
			getLogger().severe("WARN: This plugin has no longer support since 01/09/2023.");
			getLogger().info("");
			getLogger().info("------ Negativity Information ------");
			getConfig().options().copyDefaults();
			saveDefaultConfig();
		} else {
			getLogger().warning("This plugin has no longer support since 01/09/2023. Only the premium version is now updated.");
			getLogger().warning("You can find it at: https://www.spigotmc.org/resources/86874.");
		}
		getLogger().info("This plugin is free, but you can buy the premium version : https://www.spigotmc.org/resources/86874 <3");
		UniversalUtils.init();
		Cheat.loadCheat();
		ProxyCompanionManager.updateForceDisabled(getConfig().getBoolean("disableProxyIntegration"));
		setupValue();

		new Metrics(this)
				.addCustomChart(new Metrics.SimplePie("custom_permission", () -> String.valueOf(Database.hasCustom)));

		Messenger messenger = getServer().getMessenger();
		ChannelEvents channelEvents = new ChannelEvents();
		if (v.isNewerOrEquals(Version.V1_13)) {
			CHANNEL_NAME_FML = "fml:hs";
			CHANNEL_NAME_BRAND = "minecraft:brand";
		} else {
			CHANNEL_NAME_FML = "FML|HS";
			CHANNEL_NAME_BRAND = "MC|Brand";
		}
		loadChannelInOut(messenger, NegativityMessagesManager.CHANNEL_ID, channelEvents);
		loadChannelInOut(messenger, CHANNEL_NAME_FML, channelEvents);
		loadChannelInOut(messenger, CHANNEL_NAME_BRAND, channelEvents);

		PluginManager pm = Bukkit.getPluginManager();
		pm.registerEvents(new PlayersEvents(this), this);
		pm.registerEvents(new FightManager(), this);
		pm.registerEvents(new ServerCrasherEvents(this), this);
		if(v.isNewerOrEquals(Version.V1_9))
			pm.registerEvents(new ElytraEvents(), this);
		
		for (Player p : Utils.getOnlinePlayers())
			manageAutoVerif(p);

		(invTimer = new ActualizeInvTimer()).runTaskTimer(this, 5, 5);
		(packetTimer = new TimerAnalyzePacket()).runTaskTimer(this, 20, 20);

		for (Cheat c : Cheat.values())
			if (c.isActive() && c.hasListener())
				pm.registerEvents((Listener) c, this);

		loadCommand();

		ItemUseBypass.load();
		WebhookManager.init();
		Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
			if (!UniversalUtils.isLatestVersion(getDescription().getVersion())) {
				getLogger().info("New version available (" + UniversalUtils.getLatestVersion().orElse("unknow")
						+ "). Download it here: https://www.spigotmc.org/resources/48399/");
			}
		});
		getServer().getScheduler().runTaskAsynchronously(this, () -> {
			Stats.loadStats();
		});
		if(getConfig().getBoolean("stats", true))
			getServer().getScheduler().runTaskTimerAsynchronously(this, Stats::update, 20 * 60 * 5, 20 * 60 * 5);
		AbstractInventory.init(this);

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
			FloodGateSupportManager.hasSupport = true;
			supportedPluginName.add("FloodGate");
		}
		
		if (Bukkit.getPluginManager().getPlugin("floodgate") != null) {
			FloodGateSupportManager.hasSupport = true;
			FloodGateSupportManager.isV2 = true;
			supportedPluginName.add("FloodGate");
		}
		
		Perm.registerChecker(Perm.PLATFORM_CHECKER, new BukkitPermissionChecker());

		if (supportedPluginName.length() > 0) {
			getLogger().info("Loaded support for " + supportedPluginName.toString() + ".");
		}
		
		getServer().getScheduler().runTaskLater(this, () -> {
			reloading = false;
		}, 3 * 20);
		 
		try {
			 Class.forName("org.magmafoundation.magma.configuration.MagmaConfig");
			 isMagma = true;
			 getLogger().info("Magma platform detected.");
		} catch (Exception e) {} // ignore
		
		try {
			// finding field name
			// 1.16 = h
			// 1.15 = f
			// 1.14 = f
			// 1.13 = d
			// 1.12 = h
			// 1.10 = h
			// 1.9 = h
			// 1.8 = h
			// 1.7 = g
			String fieldNameLastTimeTps;
			if(v.isNewerOrEquals(Version.V1_19)) {
				fieldNameLastTimeTps = "k";
			} else if(v.equals(Version.V1_18)) {
				if(PacketUtils.VERSION.equalsIgnoreCase("v1_18_R2"))
					fieldNameLastTimeTps = "o";
				else
					fieldNameLastTimeTps = "p";
			} else if(v.equals(Version.V1_17))
				fieldNameLastTimeTps = "n";
			else if(v.equals(Version.V1_13))
				fieldNameLastTimeTps = "d";
			else if(v.equals(Version.V1_7))
				fieldNameLastTimeTps = "g";
			else if(v.equals(Version.V1_14) || v.equals(Version.V1_15))
				fieldNameLastTimeTps = "f";
			else
				fieldNameLastTimeTps = isMagma ? "field_71311_j" : "h";
			Class<?> mcServerClass = PacketUtils.getNmsClass("MinecraftServer", "server.");
			Object mcServer = mcServerClass.getMethod("getServer").invoke(mcServerClass);
			Field fieldLastTimeTps = mcServerClass.getDeclaredField(fieldNameLastTimeTps);
			fieldLastTimeTps.setAccessible(true);
			getServer().getScheduler().runTaskTimerAsynchronously(this, () -> {
				try {
					Object lastTime = fieldLastTimeTps.get(mcServer);
					double i = ((double) Utils.sumTps((long[]) lastTime)) * 1.0E-6D;
					if(UniversalUtils.TPS_DROP && i < 50) { // if disabled and need to be enabled
						UniversalUtils.TPS_DROP = false;
					} else if(!UniversalUtils.TPS_DROP && i > 50) { // if disabled but need to be
						UniversalUtils.TPS_DROP = true;
						Adapter.getAdapter().debug("Disabling detection because of TPS lagspike: " + i);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}, 1, 1);
		} catch (Exception e) {
			e.printStackTrace();
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
		
		ConfigurationSection commandSection = getConfig().getConfigurationSection("commands");
		if(commandSection == null) {
			getLogger().severe("Cannot find 'commands' section in config. Please, see default config here:");
			getLogger().severe("https://github.com/Elikill58/Negativity/blob/master/config.yml");
			getLogger().severe("Or reset your own config.");
			return;
		}

		PluginCommand reportCmd = getCommand("nreport");
		if (commandSection != null && !commandSection.getBoolean("report", true))
			unRegisterBukkitCommand(reportCmd);
		else {
			reportCmd.setAliases(Arrays.asList("report", "negreport"));
			reportCmd.setExecutor(new ReportCommand());
			reportCmd.setTabCompleter(new ReportCommand());
		}

		PluginCommand banCmd = getCommand("nban");
		if (getConfig().getBoolean("ban.ban-command-enabled", false))
			unRegisterBukkitCommand(banCmd);
		else {
			banCmd.setAliases(Arrays.asList("ban", "negban"));
			banCmd.setExecutor(new BanCommand());
			banCmd.setTabCompleter(new BanCommand());
		}

		PluginCommand unbanCmd = getCommand("nunban");
		if (getConfig().getBoolean("ban.unban-command-enabled", false))
			unRegisterBukkitCommand(unbanCmd);
		else {
			unbanCmd.setAliases(Arrays.asList("unban", "negunban"));
			unbanCmd.setExecutor(new UnbanCommand());
			unbanCmd.setTabCompleter(new UnbanCommand());
		}

		PluginCommand kickCmd = getCommand("nkick");
		if (commandSection != null && !commandSection.getBoolean("kick", true))
			unRegisterBukkitCommand(kickCmd);
		else {
			kickCmd.setAliases(Arrays.asList("kick", "negkick"));
			kickCmd.setExecutor(new KickCommand());
			kickCmd.setTabCompleter(new KickCommand());
		}

		PluginCommand langCmd = getCommand("nlang");
		if (commandSection != null && !commandSection.getBoolean("lang", true))
			unRegisterBukkitCommand(langCmd);
		else {
			LangCommand langExecutor = new LangCommand();
			langCmd.setAliases(Arrays.asList("lang", "neglang"));
			langCmd.setExecutor(langExecutor);
			langCmd.setTabCompleter(langExecutor);
		}

		PluginCommand modCmd = getCommand("nmod");
		if (commandSection != null && !commandSection.getBoolean("mod", true))
			unRegisterBukkitCommand(modCmd);
		else {
			modCmd.setAliases(Arrays.asList("mod"));
			modCmd.setExecutor(new ModCommand());
		}
	}

	@Override
	public void onDisable() {
		for (Player p : Utils.getOnlinePlayers()) {
			SpigotNegativityPlayer.removeFromCache(p.getUniqueId());
		}
		Database.close();
		invTimer.cancel();
		packetTimer.cancel();
		if(runSpawnFakePlayer != null)
			runSpawnFakePlayer.cancel();
		if(timeTimeBetweenAlert != null)
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
		return alertMod(type, p, c, reliability, proof, (CheatHover) null, 1);
	}

	/**
	 * @deprecated Use {@link #alertMod(ReportType, Player, Cheat, int, String, CheatHover)} instead
	 */
	@Deprecated
	public static boolean alertMod(ReportType type, Player p, Cheat c, int reliability, String proof, String hover_proof) {
		return alertMod(type, p, c, reliability, proof, new CheatHover.Literal(hover_proof), 1);
	}
	
	/**
	 * @deprecated Use {@link #alertMod(ReportType, Player, Cheat, int, String, CheatHover, int)} instead
	 */
	@Deprecated
	public static boolean alertMod(ReportType type, Player p, Cheat c, int reliability, String proof, String hover_proof, String stats_send) {
		return alertMod(type, p, c, reliability, proof, new CheatHover.Literal(hover_proof), 1);
	}

	/**
	 * @deprecated Use {@link #alertMod(ReportType, Player, Cheat, int, String, CheatHover, int)} instead
	 */
	@Deprecated
	public static boolean alertMod(ReportType type, Player p, Cheat c, int reliability, String proof,
			String hover_proof, int amount) {
		hover_proof = Utils.coloredMessage(hover_proof);
		return alertMod(type, p, c, reliability, proof, new CheatHover.Literal(hover_proof), amount);
	}

	public static boolean alertMod(ReportType type, Player p, Cheat c, int reliability, String proof,
			CheatHover hover) {
		return alertMod(type, p, c, reliability, proof, hover, 1);
	}

	public static boolean alertMod(ReportType type, Player p, Cheat c, int reliability, String proof,
			CheatHover hover, int amount) {
		if(reloading || !c.isActive() || reliability < 55)
			return false;
		SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(p);
		if (c.equals(Cheat.forKey(CheatKeys.FLY)) && p.hasPermission("essentials.fly") && essentialsSupport && EssentialsSupport.checkEssentialsPrecondition(p))
			return false;
		if(VerificationManager.isDisablingAlertOnVerif() && hasVerifications(p.getUniqueId()))
			return false;
		
		int ping = np.ping;
		long currentTimeMilli = System.currentTimeMillis();
		if (np.TIME_INVINCIBILITY > currentTimeMilli || ping > c.getMaxAlertPing()
				|| np.getLife() == 0.0D || np.isFreeze
				|| getInstance().getConfig().getDouble("tps_alert_stop", 19.0) > Utils.getLastTPS() || ping < 0)
			return false;
		
		ItemStack itemInHand = Utils.getItemInHand(p);
		Material blockBelow = p.getLocation().clone().subtract(0, 1, 0).getBlock().getType();
		// Why a boolean to check if the block is loaded ? It's to prevent bug which can appear with getTargetBlock method
		boolean hasLoadTargetVisual = false;
		Block targetVisual = null;
		for(Entry<String, ItemUseBypass> itemUseBypass : ItemUseBypass.ITEM_BYPASS.entrySet()) {
			String id = itemUseBypass.getKey();
			ItemUseBypass itemBypass = itemUseBypass.getValue();
			if(!itemBypass.isForThisCheat(c))
				continue;
			if(itemBypass.getWhen().equals(WhenBypass.ALWAYS)) {
				if(ItemUtils.isItemBypass(itemBypass, itemInHand)) {
					return false;
				}
			} else if(itemBypass.getWhen().equals(WhenBypass.BELOW)) {
				if(blockBelow.name().equalsIgnoreCase(id)) {
					return false;
				}
			} else if(itemBypass.getWhen().equals(WhenBypass.LOOKING)) {
				if(!hasLoadTargetVisual) {
					targetVisual = Utils.getTargetBlock(p, 7);
					hasLoadTargetVisual = true;
				}
				if(targetVisual != null && targetVisual.getType().name().equalsIgnoreCase(id)) {
					return false;
				}
			} else if(itemBypass.getWhen().equals(WhenBypass.WEARING)) {
				for(ItemStack armor : p.getInventory().getArmorContents()) {
					if(ItemUtils.isItemBypass(itemBypass, armor))
						return false;
				}
			}
		}
		
		callSyncEvent(new PlayerCheatEvent(p, c, reliability));
		if (hasBypass && (Perm.hasPerm(SpigotNegativityPlayer.getNegativityPlayer(p), "bypass." + c.getKey().toLowerCase(Locale.ROOT))
				|| Perm.hasPerm(SpigotNegativityPlayer.getNegativityPlayer(p), Perm.BYPASS_ALL))) {
			PlayerCheatBypassEvent bypassEvent = new PlayerCheatBypassEvent(p, c, reliability);
			callSyncEvent(bypassEvent);
			if (!bypassEvent.isCancelled())
				return false;
		}
		PlayerCheatAlertEvent alert = new PlayerCheatAlertEvent(type, p, c, reliability,
				c.getReliabilityAlert() < reliability, ping, proof, hover, amount);
		if(type == ReportType.INFO) { // if it's a debug alert, ignore it
			sendAlertMessage(np, alert);
			return false;
		} else
			WebhookManager.addToQueue(new AlertWebhookMessage(WebhookMessageType.ALERT, p, "Negativity", System.currentTimeMillis(), alert.getNbAlert() == 0 ? 1 : alert.getNbAlert(), alert.getReliability(), alert.getCheat()));
		callSyncEvent(alert);
		if (alert.isCancelled() || !alert.isAlert())
			return false;
		int oldWarn = np.addWarn(c, reliability, amount);
		logProof(np, type, p, c, reliability, proof, ping);
		if(BanManager.isBanned(np.getUUID())) {
			return false;
		}

		if (BanManager.autoBan && BanUtils.banIfNeeded(np, c, reliability) != null) {
			return false;
		}
		Stats.updateCheat(c, amount);
		if (c.allowKick() && ((long) (oldWarn / c.getAlertToKick())) < ((long) (np.getWarn(c) / c.getAlertToKick()))) { // if reach new alert state
			PlayerCheatKickEvent kick = new PlayerCheatKickEvent(p, c, reliability);
			callSyncEvent(kick);
			if (!kick.isCancelled())
				p.kickPlayer(Messages.getMessage(p, "kick.neg_kick", "%cheat%", c.getName(), "%reason%", np.getReason(c), "%playername%", p.getName()));
		}
		manageAlertCommand(np, type, p, c, reliability);
		if(timeBetweenAlert != -1) {
			List<PlayerCheatAlertEvent> tempList = np.ALERT_NOT_SHOWED.containsKey(c) ? np.ALERT_NOT_SHOWED.get(c) : new ArrayList<>();
			tempList.add(alert);
			np.ALERT_NOT_SHOWED.put(c, tempList);
			return true;
		}

		sendAlertMessage(np, alert);
		return true;
	}

	private static void manageAlertCommand(SpigotNegativityPlayer np, ReportType type, Player p, Cheat c, int reliability) {
		ConfigurationSection conf = getInstance().getConfig().getConfigurationSection("alert.command");
		if(conf == null || !conf.getBoolean("active") || conf.getInt("reliability_need") > reliability)
			return;
		int cooldown = conf.getInt("cooldown", 0);
		if(cooldown > 0) {
			if(np.lastAlertCommandRan > System.currentTimeMillis())
				return; // has cooldown
			np.lastAlertCommandRan = System.currentTimeMillis() + cooldown;
		}
		for(String s : conf.getStringList("run")) {
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), UniversalUtils.replacePlaceholders(s, "%version%", np.getPlayerVersion().getName(), "%name%",
					p.getName(), "%uuid%", p.getUniqueId().toString(), "%cheat_key%", c.getKey().toLowerCase(Locale.ROOT), "%world%", p.getWorld().getName(), "%cheat_name%",
					c.getName(), "%reliability%", reliability, "%report_type%", type.name(), "%warn%", np.getWarn(c), "%ping%", Utils.getPing(p), "%tps%", String.format("%.2f", Utils.getLastTPS())));
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
		if(alert.getNbAlertConsole() > 0 && log_console && !alert.getReportType().equals(ReportType.INFO)) {
			Location location = p.getLocation();
			String sLoc = "[" + location.getWorld().getName() + ": " + location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ() + "]";
			INSTANCE.getLogger().info("New " + alert.getReportType().getName() + " for " + p.getName() + " (" + ping + " ms, UUID: "
						+ p.getUniqueId().toString() + ") seem to use " + c.getName() + " "
						+ (alert.getNbAlertConsole() > 1 ? alert.getNbAlertConsole() + " times " : "") + "Reliability: " + reliability + " " + sLoc);
		}
		CheatHover hoverMsg = alert.getHover();
		if (ProxyCompanionManager.isIntegrationEnabled()) {
			sendAlertMessage(p, c.getName(), reliability, ping, hoverMsg, alert.getNbAlert());
			np.ALERT_NOT_SHOWED.remove(c);
		} else {
			boolean hasPermPeople = false;
			Object[] placeholders = new Object[] { "%name%", p.getName(), "%cheat%", c.getName(), "%ping%", ping,
				"%reliability%", reliability, "%nb%", alert.getNbAlert(), "%tps%", Utils.getLastTPS() };
			for (Player pl : Utils.getOnlinePlayers()) {
				SpigotNegativityPlayer npMod = SpigotNegativityPlayer.getNegativityPlayer(pl);
				boolean basicPerm = Perm.hasPerm(npMod, Perm.SHOW_ALERT);
				ShowAlertPermissionEvent permissionEvent = new ShowAlertPermissionEvent(p, np, basicPerm);
				Bukkit.getPluginManager().callEvent(permissionEvent);
				if (permissionEvent.isCancelled() || !npMod.isShowAlert())
					continue;
				if (permissionEvent.hasBasicPerm()) {
					new ClickableText().addRunnableHoverEvent(
							Messages.getMessage(pl, alert.getAlertMessageKey(), placeholders),
							Messages.getMessage(pl, "negativity.alert_hover", placeholders)
									+ ChatColor.RESET + (hoverMsg == null ? "" : "\n\n" + hoverMsg.compile(npMod)),
								"/negativity " + p.getName()).sendToPlayer(pl);
					hasPermPeople = true;
				}
			}
			if(hasPermPeople && !alert.getReportType().equals(ReportType.INFO)) {
				np.ALERT_NOT_SHOWED.remove(c);
			}
		}
	}

	private static void sendAlertMessage(Player p, String cheatName, int reliability, int ping, CheatHover hover, int alertsCount) {
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
		if(log && type != ReportType.INFO)
			np.logProof(new Timestamp(System.currentTimeMillis()) + ": (" + ping + "ms) " + reliability + "% " + c.getKey()
				+ " > " + proof + ". Player version: " + np.getPlayerVersion().name() + ". TPS: " + Arrays.toString(Utils.getTPS()));
	}

	public static void manageAutoVerif(Player p) {
		boolean needPacket = false;
		for (Cheat c : Cheat.values())
			if (c.isActive()) {
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
		tps_alert_stop = config.getDouble("tps_alert_stop", 19);
		
		timeBetweenAlert = config.getInt("time_between_alert");
		if(timeBetweenAlert != -1) {
			int timeTick = (timeBetweenAlert / 1000) * 20;
			if(pl.timeTimeBetweenAlert != null)
				pl.timeTimeBetweenAlert.cancel();
			(pl.timeTimeBetweenAlert = new TimerTimeBetweenAlert()).runTaskTimer(pl, timeTick, timeTick);
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
	
	public static void callSyncEvent(Event e) {
		if(Bukkit.isPrimaryThread())
			Bukkit.getPluginManager().callEvent(e);
		else
			Bukkit.getScheduler().runTask(getInstance(), () -> Bukkit.getPluginManager().callEvent(e));
	}
}
