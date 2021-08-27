package com.elikill58.negativity.common.commands;

import static com.elikill58.negativity.universal.verif.VerificationManager.CONSOLE;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.StringJoiner;
import java.util.UUID;

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
import com.elikill58.negativity.api.utils.Utils;
import com.elikill58.negativity.api.yaml.config.Configuration;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.Cheat.CheatCategory;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.Messages;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.Scheduler;
import com.elikill58.negativity.universal.account.NegativityAccount;
import com.elikill58.negativity.universal.ban.BanManager;
import com.elikill58.negativity.universal.ban.OldBansDbMigrator;
import com.elikill58.negativity.universal.bypass.BypassManager;
import com.elikill58.negativity.universal.permissions.Perm;
import com.elikill58.negativity.universal.playerModifications.PlayerModifications;
import com.elikill58.negativity.universal.playerModifications.PlayerModificationsManager;
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
		if (arg.length == 0 || arg[0].equalsIgnoreCase("help")) {
			sendHelp(sender);
			return true;
		}

		if (arg[0].equalsIgnoreCase("verif")) {
			if (sender instanceof Player && !Perm.hasPerm(NegativityPlayer.getNegativityPlayer((Player) sender), Perm.VERIF)) {
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
			if (arg.length == 2 || (arg.length == 3 && UniversalUtils.isInteger(arg[2]))) {
				nTarget.startAllAnalyze();
				Messages.sendMessage(sender, "negativity.verif.start_all", "%name%", target.getName(), "%time%", time);
				cheatsToVerify.addAll(Cheat.CHEATS);
			} else {
				StringJoiner cheatNamesJoiner = new StringJoiner(", ");
				for (int i = 2; i < arg.length; i++) {
					Cheat cheat = Cheat.fromString(arg[i]);
					if (cheat != null) {
						nTarget.startAnalyze(cheat);
						cheatNamesJoiner.add(cheat.getName());
						cheatsToVerify.add(cheat);
					}
				}

				String cheatsList = cheatNamesJoiner.toString();
				if (cheatsList.isEmpty()) {
					Messages.sendMessage(sender, "negativity.verif.start_none");
					return false;
				} else {
					Messages.sendMessage(sender, "negativity.verif.start", "%name%", target.getName(), "%cheat%", cheatsList, "%time%", time);
				}
			}
			UUID askerUUID = (sender instanceof Player ? ((Player) sender).getUniqueId() : CONSOLE);
			VerificationManager.create(askerUUID, target.getUniqueId(), new Verificator(nTarget, sender.getName(), cheatsToVerify));
			Scheduler.getInstance().runDelayed(() -> {
				Verificator verif = VerificationManager.getVerificationsFrom(target.getUniqueId(), askerUUID).get();
				verif.generateMessage();
				verif.getMessages().forEach((s) -> sender.sendMessage(Utils.coloredMessage("&a[&2Verif&a] " + s)));
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
			NegativityPlayer np = NegativityPlayer.getCached(playerSender.getUniqueId());
			np.disableShowingAlert = !np.disableShowingAlert;
			Messages.sendMessage(playerSender, np.disableShowingAlert ? "negativity.see_no_longer_alert" : "negativity.see_alert");
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
			InventoryManager.getInventory(NegativityInventory.MOD).ifPresent((inv) -> inv.openInventory((Player) sender));
			return true;
		} else if (arg[0].equalsIgnoreCase("admin") || arg[0].toLowerCase(Locale.ROOT).contains("manage")) {
			if (arg.length >= 2 && arg[1].equalsIgnoreCase("updateMessages")) {
				if (sender instanceof Player && !Perm.hasPerm(NegativityPlayer.getNegativityPlayer((Player) sender), Perm.MANAGE_CHEAT)) {
					Messages.sendMessage(sender, "not_permission");
					return true;
				}

				MessagesUpdater.performUpdate("lang", (message, placeholders) -> Messages.sendMessage(sender, message, (Object[]) placeholders));
				return true;
			}

			if (!(sender instanceof Player)) {
				Messages.sendMessage(sender, "only_player");
				return true;
			}
			Player p = (Player) sender;
			if (!Perm.hasPerm(NegativityPlayer.getNegativityPlayer(p), Perm.MANAGE_CHEAT)) {
				Messages.sendMessage(sender, "not_permission");
				return true;
			}

			InventoryManager.open(NegativityInventory.ADMIN, p);
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

			Player target = arg.length == 1 ? null : Adapter.getAdapter().getPlayer(arg[1]);
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
			p.sendMessage(ChatColor.YELLOW + "--- Checking debug for bypass | no alert ---");
			p.sendMessage(ChatColor.GOLD + ada.getName() + ": " + ada.getVersion() + ". Negativity: " + ada.getPluginVersion());
			long time = System.currentTimeMillis();
			boolean hasBypass = false;
			Cheat c = Cheat.values().stream().filter(Cheat::isActive).findFirst().get();
			if (np.TIME_INVINCIBILITY > time) {
				p.sendMessage(ChatColor.RED + "Invincibility (stay " + (np.TIME_INVINCIBILITY - time) + "ms)");
				hasBypass = true;
			}
			if (np.isFreeze) {
				p.sendMessage(ChatColor.RED + name + " are currently freezed.");
				hasBypass = true;
			}
			double tps = ada.getLastTPS();
			if(ada.getConfig().getDouble("tps_alert_stop") > tps) {
				p.sendMessage(ChatColor.RED + "Too low TPS : " + tps);
				hasBypass = true;
			}
			if(!(target.getGameMode().equals(GameMode.SURVIVAL) || target.getGameMode().equals(GameMode.ADVENTURE))) {
				p.sendMessage(ChatColor.RED + "Lot of detection are disabled if you're not on survival/adventure.");
				hasBypass = true;
			}
			int ping = target.getPing();
			if(np.isInFight)
				hasBypass = true;
			if(arg.length > 2) {
				c = Cheat.fromString(arg[2]);
				if(c != null) {
					p.sendMessage(ChatColor.GREEN + "Checking for cheat " + c.getName() + ".");
					if(!c.isActive()) {
						p.sendMessage(ChatColor.RED + "Cheat disabled.");
						hasBypass = true;
					}
					if(!np.already_blink && c.getKey().equals(CheatKeys.BLINK)) {
						p.sendMessage(ChatColor.RED + "Bypass for blink.");
						hasBypass = true;
					}
					if(BypassManager.hasBypass(p, c)) {
						p.sendMessage(ChatColor.RED + "You have a bypass actually");
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
					if(np.isInFight && c.isBlockedInFight()) {
						p.sendMessage(ChatColor.RED + "Bypass because in fight.");
						hasBypass = true;
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
			} else
				p.sendMessage(ChatColor.YELLOW + (np.isInFight ? "In fight, " : "") + "Ping: " + ping + "ms (by default, at 200ms you bypass it)");
			if((c != null && ping > c.getMaxAlertPing()) || ping > 200)
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
				InventoryManager.open(NegativityInventory.CHECK_MENU, playerSender, targetPlayer);
			else
				InventoryManager.open(NegativityInventory.CHECK_MENU_OFFLINE, playerSender, targetPlayer);
			return true;
		}

		sendHelp(sender);
		return true;
	}
	
	private void sendHelp(CommandSender sender) {
		if (Perm.hasPerm(sender, Perm.VERIF))
			Messages.sendMessageList(sender, "negativity.verif.help");
		if (Perm.hasPerm(sender, Perm.CHECK))
			Messages.sendMessageList(sender, "negativity.help");
		if (sender instanceof Player)
			Messages.sendMessageList(sender, "negativity.alert.help");
		if (Perm.hasPerm(sender, Perm.RELOAD))
			Messages.sendMessageList(sender, "negativity.reload.help");
		if (sender instanceof Player && Perm.hasPerm(sender, Perm.MOD))
			Messages.sendMessageList(sender, "negativity.mod.help");
		if (Perm.hasPerm(sender, Perm.MOD))
			Messages.sendMessageList(sender, "negativity.clear.help");
		if (sender instanceof Player && Perm.hasPerm(sender, Perm.MANAGE_CHEAT))
			Messages.sendMessageList(sender, "negativity.admin.help");
		if (Perm.hasPerm(sender, Perm.ADMIN) && WebhookManager.isEnabled())
			Messages.sendMessageList(sender, "negativity.webhook.help");
		if (sender instanceof Player)
			Messages.sendMessageList(sender, "negativity.debug.help");
		Configuration conf = Adapter.getAdapter().getConfig();
		if(conf.getBoolean("commands.report") && Perm.hasPerm(sender, Perm.REPORT))
			Messages.sendMessage(sender, "report.help");
		if(conf.getBoolean("commands.kick") && Perm.hasPerm(sender, Perm.MOD))
			Messages.sendMessage(sender, "kick.help");
		if(Perm.hasPerm(sender, Perm.LANG))
			Messages.sendMessage(sender, "lang.help");
		Configuration banConfig = BanManager.getBanConfig();
		if(banConfig.getBoolean("commands.ban") && Perm.hasPerm(sender, Perm.BAN))
			Messages.sendMessageList(sender, "ban.help");
		if(banConfig.getBoolean("commands.unban") && Perm.hasPerm(sender, Perm.UNBAN))
			Messages.sendMessage(sender, "unban.help");
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
			if ("admin".startsWith(prefix) && (sender instanceof Player) && Perm.hasPerm(NegativityPlayer.getCached(((Player) sender).getUniqueId()), Perm.MANAGE_CHEAT))
				suggestions.add("admin");
			if ("debug".startsWith(prefix))
				suggestions.add("debug");
		} else {
			if (arg[0].equalsIgnoreCase("verif") || arg[0].equalsIgnoreCase("debug")) {
				// both command use tab arguments to works
				if (arg.length == 2) {
					// /negativity verif | OR /negativity debug |
					for (Player p : Adapter.getAdapter().getOnlinePlayers()) {
						if (p.getName().toLowerCase(Locale.ROOT).startsWith(prefix.toLowerCase(Locale.ROOT)) || prefix.isEmpty()) {
							suggestions.add(p.getName());
						}
					}
				} else if (Adapter.getAdapter().getPlayer(arg[1]) != null) {
					// /negativity verif <target> |
					for (Cheat c : Cheat.values()) {
						if (c.getName().toLowerCase(Locale.ROOT).startsWith(prefix.toLowerCase(Locale.ROOT)) || prefix.isEmpty()) {
							suggestions.add(c.getName());
						}
					}
				}
			} else if (arg[0].equalsIgnoreCase("admin") && arg.length == 2) {
				if (sender instanceof Player && Perm.hasPerm(NegativityPlayer.getCached(((Player) sender).getUniqueId()), Perm.MANAGE_CHEAT)) {
					suggestions.add("updateMessages");
				}
			}
		}
		return suggestions;
	}
}
