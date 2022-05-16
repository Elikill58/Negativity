package com.elikill58.negativity.spigot.commands;

import static com.elikill58.negativity.universal.verif.VerificationManager.CONSOLE;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.StringJoiner;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.elikill58.negativity.spigot.Messages;
import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.spigot.WorldRegionBypass;
import com.elikill58.negativity.spigot.inventories.AbstractInventory;
import com.elikill58.negativity.spigot.inventories.AbstractInventory.InventoryType;
import com.elikill58.negativity.spigot.support.EssentialsSupport;
import com.elikill58.negativity.spigot.support.GadgetMenuSupport;
import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.Cheat.CheatCategory;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.NegativityAccount;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.ban.OldBansDbMigrator;
import com.elikill58.negativity.universal.permissions.Perm;
import com.elikill58.negativity.universal.pluginMessages.NegativityMessagesManager;
import com.elikill58.negativity.universal.pluginMessages.NegativityPlayerUpdateMessage;
import com.elikill58.negativity.universal.translation.MessagesUpdater;
import com.elikill58.negativity.universal.utils.UniversalUtils;
import com.elikill58.negativity.universal.verif.VerificationManager;
import com.elikill58.negativity.universal.verif.Verificator;

public class NegativityCommand implements CommandExecutor, TabCompleter {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] arg) {
		if (arg.length == 0 || arg[0].equalsIgnoreCase("help")) {
			Messages.sendMessageList(sender, "negativity.verif.help");
			FileConfiguration conf = SpigotNegativity.getInstance().getConfig();
			if(conf.getBoolean("report_command"))
				Messages.sendMessage(sender, "report.report_usage");
			if(conf.getBoolean("ban_command"))
				Messages.sendMessageList(sender, "ban.help");
			if(conf.getBoolean("unban_command"))
				Messages.sendMessage(sender, "unban.help");
			if(conf.getBoolean("kick_command"))
				Messages.sendMessage(sender, "kick.help");
			if(conf.getBoolean("report_command"))
				Messages.sendMessage(sender, "report.report_usage");
			Messages.sendMessage(sender, "lang.help");
			return true;
		}

		if (arg[0].equalsIgnoreCase("verif")) {
			if (sender instanceof Player && !Perm.hasPerm(SpigotNegativityPlayer.getNegativityPlayer((Player) sender), Perm.VERIF)) {
				Messages.sendMessage(sender, "not_permission");
				return false;
			}

			if (arg.length < 2) {
				Messages.sendMessage(sender, "not_forget_player");
				return true;
			}

			Player target = Bukkit.getPlayer(arg[1]);
			if (target == null) {
				Messages.sendMessage(sender, "invalid_player", "%arg%", arg[1]);
				return false;
			}

			SpigotNegativityPlayer nTarget = SpigotNegativityPlayer.getNegativityPlayer(target);
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
			SpigotNegativity pl = SpigotNegativity.getInstance();
			Bukkit.getScheduler().runTaskLater(pl, () -> {
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
			SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(playerSender);
			boolean nextVal = !np.isShowAlert();
			if(arg.length > 1) {
				if(arg[1].equalsIgnoreCase("on"))
					nextVal = true;
				else if(arg[1].equalsIgnoreCase("off"))
					nextVal = false;
			}
			np.setShowAlert(nextVal);
			Messages.sendMessage(playerSender, np.isShowAlert() ? "negativity.see_alert" : "negativity.see_no_longer_alert");
			
			try {
				byte[] rawMessage = NegativityMessagesManager.writeMessage(new NegativityPlayerUpdateMessage(np));
				playerSender.sendPluginMessage(SpigotNegativity.getInstance(), NegativityMessagesManager.CHANNEL_ID, rawMessage);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return true;
		} else if (arg[0].equalsIgnoreCase("reload")) {
			if (sender instanceof Player && !Perm.hasPerm(SpigotNegativityPlayer.getNegativityPlayer((Player) sender), Perm.RELOAD)) {
				Messages.sendMessage(sender, "not_permission");
				return false;
			}
			Adapter.getAdapter().reload();
			Messages.sendMessage(sender, "negativity.reload_done");
			sender.sendMessage(ChatColor.YELLOW + "Warn: We are sorry but it's possible that this reload option don't refresh what you want. So, if anything change, we suggest you to make a complete reload/restart.");
			return true;
		} else if (arg[0].equalsIgnoreCase("mod")) {
			if (!(sender instanceof Player)) {
				Messages.sendMessage(sender, "only_player");
				return true;
			}
			Player p = (Player) sender;
			if (!Perm.hasPerm(SpigotNegativityPlayer.getNegativityPlayer(p), Perm.MOD)) {
				Messages.sendMessage(sender, "not_permission");
				return true;
			}
			AbstractInventory.getInventory(InventoryType.MOD).ifPresent((inv) -> inv.openInventory(p));
			return true;
		} else if (arg[0].equalsIgnoreCase("admin") || arg[0].toLowerCase(Locale.ROOT).contains("manage")) {
			if (arg.length >= 2 && arg[1].equalsIgnoreCase("updateMessages")) {
				if (sender instanceof Player && !Perm.hasPerm(SpigotNegativityPlayer.getNegativityPlayer((Player) sender), Perm.MANAGE_CHEAT)) {
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
			if (!Perm.hasPerm(SpigotNegativityPlayer.getNegativityPlayer(p), Perm.MANAGE_CHEAT)) {
				Messages.sendMessage(sender, "not_permission");
				return true;
			}

			AbstractInventory.open(InventoryType.ADMIN, p);
			return true;
		} else if (arg[0].equalsIgnoreCase("migrateoldbans") && sender instanceof ConsoleCommandSender) {
			try {
				OldBansDbMigrator.performMigration();
			} catch (Exception e) {
				sender.sendMessage("An error occurred when performing migration: " + e.getMessage());
				e.printStackTrace();
			}
			return true;
		} else if (arg[0].equalsIgnoreCase("clear")) {
			if (sender instanceof Player && !Perm.hasPerm(SpigotNegativityPlayer.getNegativityPlayer((Player) sender), Perm.MOD)) {
				Messages.sendMessage(sender, "not_permission");
				return true;
			}

			Player target = arg.length == 1 ? null : Bukkit.getPlayer(arg[1]);
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
		} else if (arg[0].equalsIgnoreCase("debug")) {
			if (!(sender instanceof Player)) {
				Messages.sendMessage(sender, "only_player");
				return true;
			}
			Player p = (Player) sender;
			Player target = (arg.length == 1 ? p : Bukkit.getPlayer(arg[1]));
			if (target == null) {
				Messages.sendMessage(sender, "invalid_player", "%arg%", arg[1]);
				return false;
			}
			String name = (target == p ? "You" : target.getName());
			
			SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(target);
			p.sendMessage(ChatColor.YELLOW + "--- Checking debug for bypass | no alert ---");
			Adapter ada = Adapter.getAdapter();
			p.sendMessage(ChatColor.GOLD + ada.getName() + ": " + ada.getVersion() + ". Negativity " + SpigotNegativity.getInstance().getDescription().getVersion());
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
			double tps = Utils.getLastTPS();
			if(ada.getConfig().getDouble("tps_alert_stop") > tps) {
				p.sendMessage(ChatColor.RED + "Too low TPS : " + tps);
				hasBypass = true;
			}
			if(!(target.getGameMode().equals(GameMode.SURVIVAL) || target.getGameMode().equals(GameMode.ADVENTURE))) {
				p.sendMessage(ChatColor.RED + "Lot of detection are disabled if you're not on survival/adventure.");
				hasBypass = true;
			}
			int ping = np.ping;
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
					if(WorldRegionBypass.hasBypass(c, target.getLocation())) {
						p.sendMessage(ChatColor.RED + name + " have a location bypass actually");
						hasBypass = true;
					}
					if (SpigotNegativity.gadgetMenuSupport && c.getCheatCategory().equals(CheatCategory.MOVEMENT) 
							&& GadgetMenuSupport.checkGadgetsMenuPreconditions(target)) {
						p.sendMessage(ChatColor.RED + name + " has GadgetMenu movement bypass.");
						hasBypass = true;
					}
					if (SpigotNegativity.essentialsSupport && c.getKey().equals(CheatKeys.FLY) && target.hasPermission("essentials.fly")
							&& EssentialsSupport.checkEssentialsPrecondition(target)) {
						p.sendMessage(ChatColor.RED + name + " has Essentials fly bypass.");
						hasBypass = true;
					}
					if(np.isInFight && c.isBlockedInFight()) {
						p.sendMessage(ChatColor.RED + "Bypass because your are in fight.");
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
			if((c != null && ping > c.getMaxAlertPing()) || (c == null && ping > 200))
				hasBypass = true;
			p.sendMessage(hasBypass ? ChatColor.RED + "Warn: " + name +" have bypass, so you cannot be detected." : ChatColor.GREEN + "Good news: " + name + " can be detected !");
			if(!hasBypass && c != null)
				SpigotNegativity.alertMod(ReportType.INFO, target, c, 100, "", new Cheat.CheatHover.Literal("This is just a debug alert"));
			return true;
		}
		
		if(sender instanceof Player && Perm.hasPerm(SpigotNegativityPlayer.getNegativityPlayer((Player) sender), Perm.CHECK)) {
			@SuppressWarnings("deprecation")
			OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(arg[0]);
			if (targetPlayer != null) {
				Player playerSender = (Player) sender;
				//Inv.CHECKING.put(playerSender, targetPlayer);
				if(targetPlayer instanceof Player)
					AbstractInventory.open(InventoryType.CHECK_MENU, playerSender, targetPlayer);
				else
					AbstractInventory.open(InventoryType.CHECK_MENU_OFFLINE, playerSender, targetPlayer);
				return true;
			}
		}

		Messages.sendMessageList(sender, "negativity.verif.help");
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] arg) {
		List<String> suggestions = new ArrayList<>();
		String prefix = arg[arg.length - 1].toLowerCase(Locale.ROOT);
		if (arg.length == 1) {
			// /negativity |
			for (Player p : Utils.getOnlinePlayers()) {
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
			if ("debug".startsWith(prefix))
				suggestions.add("debug");
			if ("admin".startsWith(prefix) && (sender instanceof Player) && Perm.hasPerm(SpigotNegativityPlayer.getNegativityPlayer((Player) sender), Perm.MANAGE_CHEAT))
				suggestions.add("admin");
		} else {
			if (arg[0].equalsIgnoreCase("verif") || arg[0].equalsIgnoreCase("debug")) {
				// both command use tab arguments to works
				if (arg.length == 2) {
					// /negativity verif | OR /negativity debug |
					for (Player p : Utils.getOnlinePlayers()) {
						if (p.getName().toLowerCase(Locale.ROOT).startsWith(prefix.toLowerCase(Locale.ROOT)) || prefix.isEmpty()) {
							suggestions.add(p.getName());
						}
					}
				} else if (Bukkit.getPlayer(arg[1]) != null) {
					// /negativity verif <target> |
					for (Cheat c : Cheat.values()) {
						if (c.getName().toLowerCase(Locale.ROOT).startsWith(prefix.toLowerCase(Locale.ROOT)) || prefix.isEmpty()) {
							suggestions.add(c.getName());
						}
					}
				}
			} else if (arg[0].equalsIgnoreCase("admin") && arg.length == 2) {
				if (sender instanceof Player && Perm.hasPerm(SpigotNegativityPlayer.getNegativityPlayer((Player) sender), Perm.MANAGE_CHEAT)) {
					suggestions.add("updateMessages");
				}
			}
		}
		return suggestions;
	}
}
