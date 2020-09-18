package com.elikill58.negativity.common.commands;

import static com.elikill58.negativity.universal.verif.VerificationManager.CONSOLE;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.StringJoiner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.Map.Entry;

import com.elikill58.negativity.api.GameMode;
import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.block.Block;
import com.elikill58.negativity.api.colors.ChatColor;
import com.elikill58.negativity.api.commands.CommandListeners;
import com.elikill58.negativity.api.commands.CommandSender;
import com.elikill58.negativity.api.commands.TabListeners;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.inventory.AbstractInventory.NegativityInventory;
import com.elikill58.negativity.api.item.ItemStack;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.inventory.InventoryManager;
import com.elikill58.negativity.api.utils.Utils;
import com.elikill58.negativity.api.yaml.config.Configuration;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.CheatKeys;
import com.elikill58.negativity.universal.Messages;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.ReportType;
import com.elikill58.negativity.universal.Cheat.CheatCategory;
import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.ban.OldBansDbMigrator;
import com.elikill58.negativity.universal.bypass.ItemUseBypass;
import com.elikill58.negativity.universal.bypass.WorldRegionBypass;
import com.elikill58.negativity.universal.bypass.ItemUseBypass.WhenBypass;
import com.elikill58.negativity.universal.permissions.Perm;
import com.elikill58.negativity.universal.support.EssentialsSupport;
import com.elikill58.negativity.universal.support.GadgetMenuSupport;
import com.elikill58.negativity.universal.translation.MessagesUpdater;
import com.elikill58.negativity.universal.utils.UniversalUtils;
import com.elikill58.negativity.universal.verif.VerificationManager;
import com.elikill58.negativity.universal.verif.Verificator;

public class NegativityCommand implements CommandListeners, TabListeners {

	@Override
	public boolean onCommand(CommandSender sender, String[] arg, String prefix) {
		if (arg.length == 0 || arg[0].equalsIgnoreCase("help")) {
			Messages.sendMessageList(sender, "negativity.verif.help");
			Configuration conf = Adapter.getAdapter().getConfig();
			if(conf.getBoolean("commands.report") && Perm.hasPerm(sender, Perm.REPORT))
				Messages.sendMessage(sender, "report.report_usage");
			if(conf.getBoolean("commands.ban") && Perm.hasPerm(sender, Perm.BAN))
				Messages.sendMessageList(sender, "ban.help");
			if(conf.getBoolean("commands.unban") && Perm.hasPerm(sender, Perm.UNBAN))
				Messages.sendMessage(sender, "unban.help");
			if(conf.getBoolean("commands.kick") && Perm.hasPerm(sender, Perm.MOD))
				Messages.sendMessage(sender, "kick.help");
			if(Perm.hasPerm(sender, Perm.LANG))
				Messages.sendMessage(sender, "lang.help");
			return true;
		}

		if (arg[0].equalsIgnoreCase("verif")) {
			if (sender instanceof Player && !Perm.hasPerm(NegativityPlayer.getCached(((Player) sender).getUniqueId()), Perm.VERIF)) {
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

			NegativityPlayer nTarget = NegativityPlayer.getCached(target.getUniqueId());
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
			new Timer("verif-wait-" + sender.getName()).schedule(new TimerTask() {
				@Override
				public void run() {
					Verificator verif = VerificationManager.getVerificationsFrom(target.getUniqueId(), askerUUID).get();
					verif.generateMessage();
					verif.getMessages().forEach((s) -> sender.sendMessage(Utils.coloredMessage("&a[&2Verif&a] " + s)));
					verif.save();
					VerificationManager.remove(askerUUID, target.getUniqueId());
				}
			}, time * 1000);
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
		} else if (arg[0].equalsIgnoreCase("admin") || arg[0].toLowerCase().contains("manage")) {
			if (arg.length >= 2 && arg[1].equalsIgnoreCase("updateMessages")) {
				if (sender instanceof Player && !Perm.hasPerm(NegativityPlayer.getCached(((Player) sender).getUniqueId()), Perm.MANAGE_CHEAT)) {
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
			if (!Perm.hasPerm(NegativityPlayer.getCached(p.getUniqueId()), Perm.MANAGE_CHEAT)) {
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
		} else if (arg[0].equalsIgnoreCase("debug")) {
			if (!(sender instanceof Player)) {
				Messages.sendMessage(sender, "only_player");
				return true;
			}
			Player p = (Player) sender;
			NegativityPlayer np = NegativityPlayer.getNegativityPlayer(p);
			p.sendMessage(ChatColor.YELLOW + "--- Checking debug for bypass | no alert ---");
			Adapter ada = Adapter.getAdapter();
			p.sendMessage(ChatColor.GOLD + ada.getName() + ": " + ada.getVersion() + ". Negativity " + UniversalUtils.NEGATIVITY_VERSION);
			long time = System.currentTimeMillis();
			boolean hasBypass = false;
			if (np.TIME_INVINCIBILITY > time) {
				p.sendMessage(ChatColor.RED + "Invincibility (stay " + (time - np.TIME_INVINCIBILITY) + "ms)");
				hasBypass = true;
			}
			if (np.isFreeze) {
				p.sendMessage(ChatColor.RED + "You are currently freezed.");
				hasBypass = true;
			}
			if(ada.getConfig().getDouble("tps_alert_stop") > ada.getLastTPS()) {
				p.sendMessage(ChatColor.RED + "Too low TPS : " + ada.getLastTPS());
				hasBypass = true;
			}
			if(!(p.getGameMode().equals(GameMode.SURVIVAL) || p.getGameMode().equals(GameMode.ADVENTURE))) {
				p.sendMessage(ChatColor.RED + "Lot of detection are disabled if you're not on survival/adventure.");
				hasBypass = true;
			}
			if(np.isInFight || p.getPing() > 150)
				hasBypass = true;
			if(arg.length > 1) {
				Cheat c = Cheat.fromString(arg[1]);
				if(c != null) {
					p.sendMessage(ChatColor.GREEN + "Detected cheat " + c.getName() + ".");
					if(!c.isActive()) {
						p.sendMessage(ChatColor.RED + "Cheat disabled.");
						hasBypass = true;
					}
					if(!np.already_blink && c.getKey().equals(CheatKeys.BLINK)) {
						p.sendMessage(ChatColor.RED + "Bypass for blink.");
						hasBypass = true;
					}
					ItemStack itemInHand = p.getItemInHand();
					Material blockBelow = p.getLocation().clone().sub(0, 1, 0).getBlock().getType();
					boolean hasLoadTargetVisual = false;
					List<Block> targetVisual = null;
					String bypassResult = "";
					for(Entry<String, ItemUseBypass> itemUseBypass : ItemUseBypass.ITEM_BYPASS.entrySet()) {
						String id = itemUseBypass.getKey();
						ItemUseBypass itemBypass = itemUseBypass.getValue();
						if(itemBypass.getWhen().equals(WhenBypass.ALWAYS)) {
							if(itemInHand != null && itemInHand.getType().getId().equalsIgnoreCase(id)) {
								bypassResult = "Always " + id;
							}
						} else if(itemBypass.getWhen().equals(WhenBypass.BELOW)) {
							if(blockBelow.getId().equalsIgnoreCase(id)) {
								bypassResult = "Below " + id;
							}
						} else if(itemBypass.getWhen().equals(WhenBypass.LOOKING)) {
							if(!hasLoadTargetVisual) {
								targetVisual = p.getTargetBlock(7);
								hasLoadTargetVisual = true;
							}
							if(!targetVisual.isEmpty()) {
								for(Block b : targetVisual)
									if(b.getType().getId().equalsIgnoreCase(id))
										bypassResult = "Looking " + id;
							}
						}
					}
					if(!bypassResult.isEmpty()) {
						p.sendMessage(ChatColor.RED + "Bypass : found " + bypassResult);
						hasBypass = true;
					}
					if(WorldRegionBypass.hasBypass(c, p.getLocation())) {
						p.sendMessage(ChatColor.RED + "World region bypass with this cheat.");
						hasBypass = true;
					}
					if (Negativity.gadgetMenuSupport && c.getCheatCategory().equals(CheatCategory.MOVEMENT) 
							&& GadgetMenuSupport.checkGadgetsMenuPreconditions(p)) {
						p.sendMessage(ChatColor.RED + "GadgetMenu movement bypass.");
						hasBypass = true;
					}
					if (Negativity.essentialsSupport && c.getKey().equals(CheatKeys.FLY) && p.hasPermission("essentials.fly")
							&& EssentialsSupport.checkEssentialsPrecondition(p)) {
						p.sendMessage(ChatColor.RED + "Essentials fly bypass.");
						hasBypass = true;
					}
					int ping = p.getPing();
					if(np.isInFight && c.isBlockedInFight()) {
						p.sendMessage(ChatColor.RED + "Bypass because in fight.");
						hasBypass = true;
					}
					if(ping > c.getMaxAlertPing()) {
						p.sendMessage(ChatColor.RED + "To high ping ! " + ChatColor.YELLOW + "(" + ping + " > " + c.getMaxAlertPing() + ")");
						hasBypass = true;
					}
					if(!hasBypass)
						Negativity.alertMod(ReportType.INFO, p, c, 100, "", "");
				} else
					p.sendMessage(ChatColor.RED + "Unknow cheat " + arg[1] + ".");
			} else
				p.sendMessage(ChatColor.YELLOW + (np.isInFight ? "In fight, " : "") + "Ping: " + p.getPing() + "ms (by default, at 150ms you bypass it)");
			p.sendMessage(hasBypass ? ChatColor.RED + "Warn: You have bypass, so you cannot be detected." : ChatColor.GREEN + "Good news: you can be detected !");
			return true;
		}

		Player targetPlayer = Adapter.getAdapter().getPlayer(arg[0]);
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
			
			InventoryManager.open(NegativityInventory.CHECK_MENU, playerSender, targetPlayer);
			return true;
		}

		Messages.sendMessageList(sender, "negativity.verif.help");
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String[] arg, String prefix) {
		List<String> suggestions = new ArrayList<>();
		if (arg.length == 1) {
			// /negativity |
			for (com.elikill58.negativity.api.entity.Player p : Adapter.getAdapter().getOnlinePlayers()) {
				if (p.getName().toLowerCase(Locale.ROOT).startsWith(prefix.toLowerCase()) || prefix.isEmpty()) {
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
			if (arg[0].equalsIgnoreCase("verif")) {
				if (arg.length == 2) {
					// /negativity verif |
					for (com.elikill58.negativity.api.entity.Player p : Adapter.getAdapter().getOnlinePlayers()) {
						if (p.getName().toLowerCase(Locale.ROOT).startsWith(prefix.toLowerCase()) || prefix.isEmpty()) {
							suggestions.add(p.getName());
						}
					}
				} else if (Adapter.getAdapter().getPlayer(arg[1]) != null) {
					// /negativity verif <target> |
					for (Cheat c : Cheat.values()) {
						if (c.getName().toLowerCase(Locale.ROOT).startsWith(prefix.toLowerCase()) || prefix.isEmpty()) {
							suggestions.add(c.getName());
						}
					}
				}
			} else if (arg[0].equalsIgnoreCase("debug")) {
				for (Cheat c : Cheat.values()) {
					if (c.getName().toLowerCase(Locale.ROOT).startsWith(prefix.toLowerCase()) || prefix.isEmpty()) {
						suggestions.add(c.getName());
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
