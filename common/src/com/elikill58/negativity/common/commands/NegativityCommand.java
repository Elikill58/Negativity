package com.elikill58.negativity.common.commands;

import static com.elikill58.negativity.universal.verif.VerificationManager.CONSOLE;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;
import java.util.UUID;
import java.util.stream.Collectors;

import com.elikill58.negativity.api.GameMode;
import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.colors.ChatColor;
import com.elikill58.negativity.api.commands.CommandListeners;
import com.elikill58.negativity.api.commands.CommandSender;
import com.elikill58.negativity.api.commands.TabListeners;
import com.elikill58.negativity.api.entity.OfflinePlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.inventory.AbstractInventory.NegativityInventory;
import com.elikill58.negativity.api.inventory.InventoryManager;
import com.elikill58.negativity.api.protocols.Check;
import com.elikill58.negativity.api.yaml.Configuration;
import com.elikill58.negativity.api.yaml.YamlConfiguration;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Messages;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.ProxyCompanionManager;
import com.elikill58.negativity.universal.Scheduler;
import com.elikill58.negativity.universal.TranslatedMessages;
import com.elikill58.negativity.universal.account.NegativityAccount;
import com.elikill58.negativity.universal.ban.BanManager;
import com.elikill58.negativity.universal.ban.OldBansDbMigrator;
import com.elikill58.negativity.universal.detections.Cheat;
import com.elikill58.negativity.universal.detections.Cheat.CheatCategory;
import com.elikill58.negativity.universal.detections.Special;
import com.elikill58.negativity.universal.detections.keys.CheatKeys;
import com.elikill58.negativity.universal.detections.keys.SpecialKeys;
import com.elikill58.negativity.universal.monitor.MonitorManager;
import com.elikill58.negativity.universal.monitor.MonitorType;
import com.elikill58.negativity.universal.permissions.Perm;
import com.elikill58.negativity.universal.playerModifications.PlayerModifications;
import com.elikill58.negativity.universal.playerModifications.PlayerModificationsManager;
import com.elikill58.negativity.universal.pluginMessages.NegativityMessagesManager;
import com.elikill58.negativity.universal.pluginMessages.PlayerVersionMessage;
import com.elikill58.negativity.universal.pluginMessages.ShowAlertStatusMessage;
import com.elikill58.negativity.universal.report.ReportType;
import com.elikill58.negativity.universal.translation.MessagesUpdater;
import com.elikill58.negativity.universal.utils.UniversalUtils;
import com.elikill58.negativity.universal.verif.VerificationManager;
import com.elikill58.negativity.universal.verif.Verificator;
import com.elikill58.negativity.universal.webhooks.Webhook;
import com.elikill58.negativity.universal.webhooks.WebhookManager;

public class NegativityCommand implements CommandListeners, TabListeners {

	@Override
	public boolean onCommand(CommandSender sender, String[] arg, String prefix) {
		if (arg.length == 0) {
			sendHelp(sender, 1);
			return true;
		}
		if (arg[0].equalsIgnoreCase("help")) {
			sendHelp(sender, (arg.length > 1 && UniversalUtils.isInteger(arg[1]) ? Integer.parseInt(arg[1]) : 1));
			return true;
		} else if(arg[0].equalsIgnoreCase("test")) {
			// send ask version request
			Player p = (Player) sender;
			p.sendPluginMessage(NegativityMessagesManager.CHANNEL_ID, new PlayerVersionMessage(p.getUniqueId(), null));
			return false;
		} else if (arg[0].equalsIgnoreCase("verif")) {
			if (sender instanceof Player && !Perm.hasPerm(sender, Perm.VERIF)) {
				Messages.sendMessage(sender, "not_permission");
				return false;
			}

			if (arg.length < 2) {
				Messages.sendMessage(sender, "not_forget_player");
				return true;
			}

			Player target = Adapter.getAdapter().getPlayer(arg[1]);
			if (target == null) {
				Messages.sendMessage(sender, "invalid_player", "%arg%", arg[1]);
				return false;
			}

			NegativityPlayer nTarget = NegativityPlayer.getNegativityPlayer(target);
			int time = UniversalUtils.getFirstInt(arg).orElse(VerificationManager.getTimeVerif() / 20);
			Set<Cheat> cheatsToVerify = new LinkedHashSet<>();
			boolean forceGiven = false;
			if (arg.length == 2 || (arg.length == 3 && UniversalUtils.isInteger(arg[2]))) {
				Messages.sendMessage(sender, "negativity.verif.start_all", "%name%", target.getName(), "%time%", time);
				cheatsToVerify.addAll(Cheat.CHEATS);
			} else {
				StringJoiner cheatNamesJoiner = new StringJoiner(", ");
				for (int i = 2; i < arg.length; i++) {
					Cheat cheat = Cheat.fromString(arg[i]);
					if (cheat != null) {
						cheatNamesJoiner.add(cheat.getName());
						cheatsToVerify.add(cheat);
					}
				}

				String cheatsList = cheatNamesJoiner.toString();
				if (cheatsList.isEmpty()) {
					Messages.sendMessage(sender, "negativity.verif.start_none");
					return false;
				} else {
					forceGiven = true;
					Messages.sendMessage(sender, "negativity.verif.start", "%name%", target.getName(), "%cheat%", cheatsList, "%time%", time);
				}
			}
			UUID askerUUID = (sender instanceof Player ? ((Player) sender).getUniqueId() : CONSOLE);
			Verificator verif = VerificationManager.create(askerUUID, target.getUniqueId(), new Verificator(nTarget, sender.getName(), cheatsToVerify, forceGiven));
			Scheduler.getInstance().runDelayed(() -> {
				//Verificator verif = VerificationManager.getVerificationsFrom(target.getUniqueId(), askerUUID).get();
				verif.generateMessage();
				verif.getMessages().forEach((s) -> sender.sendMessage(ChatColor.color("&a[&2Verif&a] " + s)));
				verif.save();
				VerificationManager.remove(askerUUID, target.getUniqueId());
			}, time * 20);
			return true;
		} else if (arg[0].equalsIgnoreCase("alert")) {
			if (!(sender instanceof Player)) {
				Messages.sendMessage(sender, "only_player");
				return true;
			}
			Player playerSender = (Player) sender;
			UUID uuid = playerSender.getUniqueId();
			NegativityAccount acc = NegativityAccount.get(uuid);
			boolean newVal = !acc.isShowAlert();
			acc.setShowAlert(newVal);
			if(ProxyCompanionManager.isIntegrationEnabled()) {
				playerSender.sendPluginMessage(NegativityMessagesManager.CHANNEL_ID, new ShowAlertStatusMessage(uuid, newVal)); // send message to bungee
			}
			Messages.sendMessage(playerSender, newVal ? "negativity.see_alert" : "negativity.see_no_longer_alert");
			Adapter.getAdapter().getAccountManager().save(acc.getPlayerId());
			return true;
		} else if (arg[0].equalsIgnoreCase("reload")) {
			if (sender instanceof Player && !Perm.hasPerm(NegativityPlayer.getNegativityPlayer((Player) sender), Perm.RELOAD)) {
				Messages.sendMessage(sender, "not_permission");
				return false;
			}
			Negativity.loadNegativity();
			Adapter.getAdapter().reload();
			Messages.sendMessage(sender, "negativity.reload_done");
			return true;
		} else if (arg[0].equalsIgnoreCase("mod")) {
			if (!(sender instanceof Player)) {
				Messages.sendMessage(sender, "only_player");
				return true;
			}
			Player p = (Player) sender;
			if (!Perm.hasPerm(NegativityPlayer.getNegativityPlayer(p), Perm.MOD)) {
				Messages.sendMessage(sender, "not_permission");
				return true;
			}
			InventoryManager.open(NegativityInventory.MOD, (Player) sender);
			return true;
		} else if (arg[0].equalsIgnoreCase("admin") || arg[0].toLowerCase(Locale.ROOT).contains("manage")) {
			if (arg.length >= 2 && arg[1].equalsIgnoreCase("updateMessages")) {
				if (sender instanceof Player && !Perm.hasPerm(NegativityPlayer.getNegativityPlayer((Player) sender), Perm.ADMIN)) {
					Messages.sendMessage(sender, "not_permission");
					return false;
				}

				MessagesUpdater.performUpdate("lang", (message, placeholders) -> Messages.sendMessage(sender, message, (Object[]) placeholders));
				return true;
			}

			if (!(sender instanceof Player)) {
				Messages.sendMessage(sender, "only_player");
				return true;
			}
			Player p = (Player) sender;
			if (!Perm.hasPerm(NegativityPlayer.getNegativityPlayer(p), Perm.ADMIN)) {
				Messages.sendMessage(sender, "not_permission");
				return true;
			}

			InventoryManager.open(NegativityInventory.ADMIN, p);
			return true;
		} else if (arg[0].equalsIgnoreCase("monitor")) {
			if (!Perm.hasPerm(sender, Perm.ADMIN)) {
				Messages.sendMessage(sender, "not_permission");
				return true;
			}
			if(arg.length == 1) { // show all possible monitor
				MonitorType.getMonitors().forEach(mm -> mm.getMonitor().showResult(sender));
			} else if(arg[1].equalsIgnoreCase("generate")) {
				File file = null;
				try {
					Adapter ada = Adapter.getAdapter();
					File folder = new File(ada.getDataFolder(), "monitors");
					if(!folder.exists()) {
						folder.mkdirs();
					}
					file = new File(folder, System.currentTimeMillis() + ".yml");
					file.createNewFile();
					Configuration config = YamlConfiguration.load(file);
					config.set("date", System.currentTimeMillis());
					config.set("by", sender.getName());
					config.set("server.platform", ada.getPlatformID().getName());
					config.set("server.version", ada.getServerVersion().getName());
					config.set("plugin.version", ada.getPluginVersion());
					config.set("plugin.all", ada.getAllPlugins());
					for(MonitorType<?> monitor : MonitorType.getMonitors()) {
						Configuration monitorConfig = config.createSection(monitor.getName().toLowerCase(Locale.ROOT).replace(" ", ""));
						monitor.getMonitor().getFullConfig().forEach(result -> {
							if(!result.isEmpty()) {
								result.save(monitorConfig);
							}
						});
					}
					config.save();
					sender.sendMessage(ChatColor.GREEN + "Summary saved at monitors/" + file.getName());
				} catch (Exception e) {
					sender.sendMessage(ChatColor.RED + "Failed to save monitor summary.");
					e.printStackTrace();
					if(file != null) // try to don't have empty file
						file.deleteOnExit();
				}
			} else {
				Optional<? extends MonitorManager> optManager = MonitorType.getMonitors().stream().filter(mm -> mm.getName().equalsIgnoreCase(arg[1])).map(MonitorType::getMonitor).findFirst();
				if(optManager.isPresent()) {
					optManager.get().showPerCheatResult(sender);
				} else {
					sender.sendMessage(ChatColor.RED + "Can't find " + arg[1] + " monitor. You can use: " + String.join(", ", MonitorType.getMonitors().stream().map(MonitorType::getName).collect(Collectors.toList())));
				}
			}
			return true;
		} else if (arg[0].equalsIgnoreCase("options")) {
			if (sender instanceof Player && !Perm.hasPerm(sender, Perm.ADMIN)) {
				Messages.sendMessage(sender, "not_permission");
				return false;
			}
			if(arg.length < 4) {
				sender.sendMessage(ChatColor.RED + "Unknow options. Please follow tab completion");
				return true;
			}
			
			if(arg[1].equalsIgnoreCase("cheat")) {
				Cheat cheat = Cheat.fromString(arg[2]);
				if(cheat == null == !arg[2].equalsIgnoreCase("all")) {
					sender.sendMessage(ChatColor.RED + "Failed to find given cheat.");
					return false;
				}
				for(Cheat c : cheat == null ? Cheat.values() : Arrays.asList(cheat)) {
					if(arg[3].equalsIgnoreCase("active")) {
						c.setActive(arg.length >= 5 ? UniversalUtils.getBoolean(arg[4]) : !c.isActive());
					} else if(arg[3].equalsIgnoreCase("kick")) {
						c.setAllowKick(arg.length >= 5 ? UniversalUtils.getBoolean(arg[4]) : !c.allowKick());
					} else if(arg[3].equalsIgnoreCase("set_back")) {
						c.setBack(arg.length >= 5 ? UniversalUtils.getBoolean(arg[4]) : !c.isSetBack());
					} else if(arg[3].equalsIgnoreCase("bedrock")) {
						c.setDisabledForBedrock(arg.length >= 5 ? UniversalUtils.getBoolean(arg[4]) : !c.isDisabledForBedrock());
					} else if(arg[3].equalsIgnoreCase("check")) {
						Check check = arg.length >= 5 ? c.getChecks().stream().filter(ch -> ch.name().equalsIgnoreCase(arg[4])).findFirst().orElse(null) : null;
						if(check == null) {
							sender.sendMessage(ChatColor.RED + "Can't find check.");
							return false;
						}
						c.setCheckActive(check, arg.length >= 6 ? UniversalUtils.getBoolean(arg[5]) : !c.checkActive(check));
					} else {
						sender.sendMessage(ChatColor.RED + "Unknow options");
						return true;
					}
					c.saveConfig();
				}
				if(cheat == null)
					sender.sendMessage(ChatColor.GREEN + "Option for all cheats edited.");
				else
					sender.sendMessage(ChatColor.GREEN + "Option for " + cheat.getName() + " edited.");
			} else if(arg[1].equalsIgnoreCase("special")) {
				Special special = Special.fromString(arg[2]);
				if(special == null == !arg[2].equalsIgnoreCase("all")) {
					sender.sendMessage(ChatColor.RED + "Failed to find given cheat.");
					return false;
				}
				for(Special s : special == null ? Special.values() : Arrays.asList(special)) {
					if(arg[3].equalsIgnoreCase("active")) {
						s.setActive(arg.length >= 5 ? UniversalUtils.getBoolean(arg[4]) : !s.isActive());
					} else if(arg[3].equalsIgnoreCase("bedrock")) {
						s.setDisabledForBedrock(arg.length >= 5 ? UniversalUtils.getBoolean(arg[4]) : !s.isDisabledForBedrock());
					} else {
						sender.sendMessage(ChatColor.RED + "Unknow options");
						return true;
					}
					s.saveConfig();
				}
				if(special == null)
					sender.sendMessage(ChatColor.GREEN + "Option for all specials edited.");
				else
					sender.sendMessage(ChatColor.GREEN + "Option for " + special.getName() + " edited.");
			} else if(arg[1].equalsIgnoreCase("global")) {
				if(arg[2].equalsIgnoreCase("lang")) {
					String lang = arg[3];
					Optional<String> optLang = TranslatedMessages.LANGS.stream().filter(l -> l.equalsIgnoreCase(lang)).findFirst();
					if(optLang.isPresent()) {
						Adapter.getAdapter().getConfig().set("Translation.default", lang);
						Adapter.getAdapter().getConfig().save();
						TranslatedMessages.DEFAULT_LANG = lang;
						TranslatedMessages.loadMessages();
						sender.sendMessage(ChatColor.GREEN + "Lang changed to " + lang);
					} else {
						sender.sendMessage(ChatColor.RED + "Unknow lang " + lang);
					}
				} else if(arg[2].equalsIgnoreCase("debug")) {
					boolean b = UniversalUtils.getBoolean(arg[3]);
					Adapter.getAdapter().getConfig().set("debug", b);
					Adapter.getAdapter().getConfig().save();
					sender.sendMessage(ChatColor.GREEN + "Debug mode " + (b ? "enabled" : "disabled"));
				} else {
					sender.sendMessage(ChatColor.RED + "Unknow options");
				}
			} else {
				sender.sendMessage(ChatColor.RED + "Unknow options");
			}
			return true;
		} else if (arg[0].equalsIgnoreCase("tp")) {
			if (!(sender instanceof Player)) {
				Messages.sendMessage(sender, "only_player");
				return true;
			}
			if(arg.length == 1) {
				Messages.sendMessage(sender, "not_forget_player");
				return true;
			}
			Player p = (Player) sender;
			Player target = Adapter.getAdapter().getPlayer(arg[1]);
			if (target == null) {
				if(arg.length == 2) { // not precise server
					Messages.sendMessage(sender, "invalid_player", "%arg%", arg[1]);
				} else {
					p.sendMessage(ChatColor.GREEN + "Teleporting to server " + arg[2] + " ...");
					p.sendToServer(arg[2]);
				}
				return false;
			} else {
				InventoryManager.open(NegativityInventory.GLOBAL_PLAYER, p, target);
			}
			return true;
		} else if (arg[0].equalsIgnoreCase("migrateoldbans") && !(sender instanceof Player)) {
			try {
				OldBansDbMigrator.performMigration();
			} catch (Exception e) {
				sender.sendMessage("An error occurred when performing migration: " + e.getMessage());
				e.printStackTrace();
			}
			return true;
		} else if (arg[0].equalsIgnoreCase("clear")) {
			if (!Perm.hasPerm(sender, Perm.MOD)) {
				Messages.sendMessage(sender, "not_permission");
				return true;
			}

			OfflinePlayer target = arg.length == 1 ? null : Adapter.getAdapter().getOfflinePlayer(arg[1]);
			if (target == null) {
				Messages.sendMessage(sender, "invalid_player", "%arg%", arg[1]);
				return false;
			}
			NegativityAccount account = NegativityAccount.get(target.getUniqueId());
			for (Cheat c : Cheat.values()) {
				account.setWarnCount(c, 0);
			}
			Adapter.getAdapter().getAccountManager().update(account);
			Messages.sendMessage(sender, "negativity.cleared", "%name%", target.getName());
			return true;
		} else if (arg[0].equalsIgnoreCase("webhook")) {
			if (!Perm.hasPerm(sender, Perm.ADMIN)) {
				Messages.sendMessage(sender, "not_permission");
				return true;
			}
			if(WebhookManager.isEnabled()) {
				List<Webhook> webhooks = WebhookManager.getWebhooks();
				if(webhooks.isEmpty()) {
					sender.sendMessage(ChatColor.YELLOW + "No webhook configurated.");
				} else {
					for(Webhook hook : webhooks) {
						
						if(hook.ping(sender.getName())) {
							sender.sendMessage(ChatColor.GREEN + hook.getWebhookName() + " well configurated.");
						} else {
							sender.sendMessage(ChatColor.RED + hook.getWebhookName() + " seems to don't work.");
						}
					}
				}
			} else {
				sender.sendMessage(ChatColor.YELLOW + "Webhooks are disabled.");
			}
			return true;
		} else if (arg[0].equalsIgnoreCase("debug")) {
			if (!(sender instanceof Player)) {
				Messages.sendMessage(sender, "only_player");
				return true;
			}
			Adapter ada = Adapter.getAdapter();

			Player p = (Player) sender;
			Player target = (arg.length == 1 ? p : ada.getPlayer(arg[1]));
			if (target == null) {
				Messages.sendMessage(sender, "invalid_player", "%arg%", arg[1]);
				return false;
			}
			String name = (target == p ? "You" : target.getName());
			
			NegativityPlayer np = NegativityPlayer.getNegativityPlayer(target);
			p.sendMessage(ChatColor.YELLOW + "--- Checking debug for bypass ---");
			p.sendMessage(ChatColor.GOLD + ada.getName() + ": " + ada.getVersion() + ". Negativity: " + ada.getPluginVersion());
			boolean hasBypass = false;
			if (np.isFreeze) {
				p.sendMessage(ChatColor.RED + name + " are currently freezed.");
				hasBypass = true;
			}
			if(!(target.getGameMode().equals(GameMode.SURVIVAL) || target.getGameMode().equals(GameMode.ADVENTURE))) {
				p.sendMessage(ChatColor.RED + "Lot of detection are disabled if you're not on survival/adventure.");
				hasBypass = true;
			}
			int ping = target.getPing();
			if(np.isInFight)
				hasBypass = true;
			Cheat c = arg.length > 2 ? Cheat.fromString(arg[2]) : Cheat.values().stream().filter(Cheat::isActive).findFirst().get();
			if(c != null) {
				p.sendMessage(ChatColor.GREEN + "Checking for cheat " + c.getName() + ".");
				if(!c.isActive()) {
					p.sendMessage(ChatColor.RED + "Cheat disabled.");
					hasBypass = true;
				}
				if (c.getCheatCategory().equals(CheatCategory.MOVEMENT)) {
					for (PlayerModifications modification : PlayerModificationsManager.getModifications()) {
						if (modification.shouldIgnoreMovementChecks(p)) {
							p.sendMessage(ChatColor.RED + modification.getDisplayname() + " movement bypass.");
							hasBypass = true;
						}
					}
				}
				if (c.getKey().equals(CheatKeys.FLY)) {
					for (PlayerModifications modification : PlayerModificationsManager.getModifications()) {
						if (modification.canFly(p)) {
							p.sendMessage(ChatColor.RED + modification.getDisplayname() + " fly bypass.");
							hasBypass = true;
						}
					}
				}
				if(ping > c.getMaxAlertPing()) {
					p.sendMessage(ChatColor.RED + "To high ping ! " + ChatColor.YELLOW + "(" + ping + " > " + c.getMaxAlertPing() + ")");
					hasBypass = true;
				}
				if(!np.hasDetectionActive(c)) {
					p.sendMessage(ChatColor.RED + "Detection of " + c.getName() + " not active: " + np.getWhyDetectionNotActive(c));
					hasBypass = true;
				}
			} else
				p.sendMessage(ChatColor.RED + "Unknow cheat " + arg[2] + ".");
			if((c != null && ping > c.getMaxAlertPing()) || (c == null && ping > 200))
				hasBypass = true;
			p.sendMessage(hasBypass ? ChatColor.RED + "Warn: " + name + " have bypass, so you cannot be detected." : ChatColor.GREEN + "Good news: " + name + " can be detected !");
			if(!hasBypass && c != null)
				Negativity.alertMod(ReportType.INFO, target, c, 100, "test", "");
			return true;
		}

		OfflinePlayer targetPlayer = Adapter.getAdapter().getOfflinePlayer(arg[0]);
		if (targetPlayer != null) {
			if (!(sender instanceof Player)) {
				Messages.sendMessage(sender, "only_player");
				return false;
			}

			Player playerSender = (Player) sender;
			if (!Perm.hasPerm(NegativityPlayer.getNegativityPlayer(playerSender), Perm.CHECK)) {
				Messages.sendMessage(sender, "not_permission");
				return false;
			}
			if(targetPlayer instanceof Player)
				InventoryManager.open(NegativityInventory.GLOBAL_PLAYER, playerSender, targetPlayer);
			else
				InventoryManager.open(NegativityInventory.GLOBAL_PLAYER_OFFLINE, playerSender, targetPlayer);
			return true;
		}

		sendHelp(sender, 1);
		return true;
	}
	
	private void sendHelp(CommandSender sender, int page) {
		int nbPerPage = 6;
		List<String> list = new ArrayList<>();
		// lets construct all lines
		if (Perm.hasPerm(sender, Perm.VERIF))
			list.addAll(Messages.getMessageList(sender, "negativity.verif.help"));
		if (Perm.hasPerm(sender, Perm.CHECK))
			list.addAll(Messages.getMessageList(sender, "negativity.help"));
		if (sender instanceof Player)
			list.addAll(Messages.getMessageList(sender, "negativity.alert.help"));
		if (Perm.hasPerm(sender, Perm.RELOAD))
			list.addAll(Messages.getMessageList(sender, "negativity.reload.help"));
		if (sender instanceof Player && Perm.hasPerm(sender, Perm.MOD))
			list.addAll(Messages.getMessageList(sender, "negativity.mod.help"));
		if (Perm.hasPerm(sender, Perm.MOD))
			list.addAll(Messages.getMessageList(sender, "negativity.clear.help"));
		if (sender instanceof Player && Perm.hasPerm(sender, Perm.ADMIN))
			list.addAll(Messages.getMessageList(sender, "negativity.admin.help"));
		if (Perm.hasPerm(sender, Perm.ADMIN) && WebhookManager.isEnabled())
			list.addAll(Messages.getMessageList(sender, "negativity.webhook.help"));
		if (sender instanceof Player)
			list.addAll(Messages.getMessageList(sender, "negativity.debug.help"));
		
		Configuration conf = Adapter.getAdapter().getConfig();
		if(conf.getBoolean("commands.report", true) && Perm.hasPerm(sender, Perm.REPORT))
			list.add(Messages.getMessage(sender, "report.help"));
		if(conf.getBoolean("commands.kick", true) && Perm.hasPerm(sender, Perm.MOD))
			list.add(Messages.getMessage(sender, "kick.help"));
		if(Perm.hasPerm(sender, Perm.LANG))
			list.add(Messages.getMessage(sender, "lang.help"));
		if(conf.getBoolean("commands.chat.clear", true) && Perm.hasPerm(sender, Perm.CHAT_CLEAR))
			list.add(Messages.getMessage(sender, "negativity.chat.clear.help"));
		if(conf.getBoolean("commands.chat.lock", true) && Perm.hasPerm(sender, Perm.CHAT_LOCK))
			list.add(Messages.getMessage(sender, "negativity.chat.lock.help"));
		
		Configuration banConfig = BanManager.getBanConfig();
		if(banConfig.getBoolean("commands.ban", false) && Perm.hasPerm(sender, Perm.BAN))
			list.addAll(Messages.getMessageList(sender, "ban.help"));
		if(banConfig.getBoolean("commands.unban", false) && Perm.hasPerm(sender, Perm.UNBAN))
			list.add(Messages.getMessage(sender, "unban.help"));
		
		int nbLine = list.size();
		int nbPage = 0;
		while(nbPage * nbPerPage < nbLine)
			nbPage++;
		// now let's find which lines have to be sent
		Messages.sendMessage(sender, "negativity.help.header", "%page%", page, "%max%", nbPage);
		for(int i = (page - 1) * nbPerPage; i < (page) * nbPerPage; i++) {
			if(nbLine > i)
				sender.sendMessage(list.get(i));
		}
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String[] arg, String prefix) {
		List<String> suggestions = new ArrayList<>();
		if (arg.length == 1) {
			// /negativity |
			for (com.elikill58.negativity.api.entity.Player p : Adapter.getAdapter().getOnlinePlayers()) {
				if (p.getName().toLowerCase(Locale.ROOT).startsWith(prefix.toLowerCase(Locale.ROOT)) || prefix.isEmpty()) {
					suggestions.add(p.getName());
				}
			}
			if ("verif".startsWith(prefix))
				suggestions.add("verif");
			if ("reload".startsWith(prefix))
				suggestions.add("reload");
			if ("alert".startsWith(prefix))
				suggestions.add("alert");
			if ("admin".startsWith(prefix) && Perm.hasPerm(sender, Perm.ADMIN))
				suggestions.add("admin");
			if ("monitor".startsWith(prefix) && Perm.hasPerm(sender, Perm.ADMIN))
				suggestions.add("monitor");
			if ("options".startsWith(prefix) && Perm.hasPerm(sender, Perm.ADMIN))
				suggestions.add("options");
			if ("debug".startsWith(prefix) || prefix.isEmpty())
				suggestions.add("debug");
			if ("webhook".startsWith(prefix) && WebhookManager.isEnabled())
				suggestions.add("webhook");
		} else {
			if (arg[0].equalsIgnoreCase("verif") || arg[0].equalsIgnoreCase("debug")) {
				// both command use tab arguments to works
				if (arg.length == 2) {
					// /negativity verif | OR /negativity debug |
					for (Player p : Adapter.getAdapter().getOnlinePlayers()) {
						if (prefix.isEmpty() || p.getName().toLowerCase(Locale.ROOT).startsWith(prefix.toLowerCase(Locale.ROOT))) {
							suggestions.add(p.getName());
						}
					}
				} else if (Adapter.getAdapter().getPlayer(arg[1]) != null) {
					// /negativity verif <target> |
					for (Cheat c : Cheat.values()) {
						if (prefix.isEmpty() || c.getCommandName().toLowerCase(Locale.ROOT).startsWith(prefix.toLowerCase(Locale.ROOT))) {
							suggestions.add(c.getCommandName());
						}
					}
				}
			} else if (arg[0].equalsIgnoreCase("admin") && arg.length == 2) {
				if (sender instanceof Player && Perm.hasPerm(sender, Perm.MANAGE_CHEAT)) {
					suggestions.add("updateMessages");
				}
			} else if (arg[0].equalsIgnoreCase("monitor") && Perm.hasPerm(sender, Perm.ADMIN)) {
				if(arg.length == 2) {
					for(MonitorType<?> type : MonitorType.getMonitors())
						if(prefix.isEmpty() || type.getName().toLowerCase(Locale.ROOT).startsWith(prefix))
							suggestions.add(type.getName());
					if (prefix.isEmpty() || "generate".startsWith(prefix))
						suggestions.add("generate");
				}
			} else if (arg[0].equalsIgnoreCase("options") && Perm.hasPerm(sender, Perm.ADMIN)) {
				List<String> possible;
				if(arg.length == 2) {
					possible = Arrays.asList("cheat", "special", "global");
				} else if(arg[1].equalsIgnoreCase("cheat")) {
					if(arg.length == 3) {
						possible = Arrays.asList(CheatKeys.values()).stream().map(CheatKeys::getLowerKey).collect(Collectors.toList());
						if (prefix.isEmpty() || "all".startsWith(prefix))
							suggestions.add("all");
					} else if(arg.length == 4) { // what should be changed
						possible = Arrays.asList("active", "kick", "check", "set_back", "bedrock");
					} else if(arg.length == 5) {
						if(arg[3].equalsIgnoreCase("check")) {
							Cheat c = Cheat.fromString(arg[2]);
							if(c == null)
								possible = Arrays.asList();
							else
								possible = c.getChecks().stream().map(Check::name).collect(Collectors.toList());
						} else
							possible = Arrays.asList("true", "false");
					} else {
						possible = null;
					}
				} else if(arg[1].equalsIgnoreCase("special")) {
					if(arg.length == 3) {
						possible = Arrays.asList(SpecialKeys.values()).stream().map(SpecialKeys::getLowerKey).collect(Collectors.toList());
						if (prefix.isEmpty() || "all".startsWith(prefix))
							suggestions.add("all");
					} else if(arg.length == 4) { // what should be changed
						possible = Arrays.asList("active", "bedrock");
					} else if(arg.length == 5) {
						possible = Arrays.asList("true", "false");
					} else {
						possible = null;
					}
				} else if(arg[1].equalsIgnoreCase("global")) {
					if(arg.length == 3) {
						possible = Arrays.asList("lang", "debug");
					} else if(arg.length == 4) {
						if(arg[2].equalsIgnoreCase("lang"))
							possible = TranslatedMessages.LANGS;
						else if(arg[2].equalsIgnoreCase("debug"))
							possible = Arrays.asList("true", "false");
						else
							possible = null;
					} else
						possible = null;
				} else
					possible = null;
				if(possible == null) // unknow cmd
					return suggestions;
				for (String s : possible) {
					if (prefix.isEmpty() || s.toLowerCase(Locale.ROOT).startsWith(prefix.toLowerCase(Locale.ROOT))) {
						suggestions.add(s);
					}
				}
			}
		}
		return suggestions;
	}
}
