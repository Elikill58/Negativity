package com.elikill58.negativity.spigot.events;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import com.elikill58.negativity.spigot.Messages;
import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.spigot.SpigotNegativityPlayer;
import com.elikill58.negativity.spigot.commands.ReportCommand;
import com.elikill58.negativity.spigot.listeners.NegativityPlayerMoveEvent;
import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.Minerate.MinerateType;
import com.elikill58.negativity.universal.NegativityAccount;
import com.elikill58.negativity.universal.ProxyCompanionManager;
import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.ban.Ban;
import com.elikill58.negativity.universal.ban.BanManager;
import com.elikill58.negativity.universal.ban.BanType;
import com.elikill58.negativity.universal.permissions.Perm;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class PlayersEvents implements Listener {

	private final SpigotNegativity pl;
	private final ConfigurationSection invalidNameSection;
	
	public PlayersEvents(SpigotNegativity pl) {
		this.pl = pl;
		ConfigurationSection specialSection = pl.getConfig().getConfigurationSection("cheats").getConfigurationSection("special");
		ConfigurationSection invalidNameSection = specialSection.getConfigurationSection("invalid_name");
		if(invalidNameSection == null)
			invalidNameSection = specialSection.createSection("invalid_name");
		this.invalidNameSection = invalidNameSection;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPreLogin(AsyncPlayerPreLoginEvent e) {
		if(!e.getLoginResult().equals(AsyncPlayerPreLoginEvent.Result.ALLOWED)) // already kicked
			return;
		UUID playerId = e.getUniqueId();

		NegativityAccount account = NegativityAccount.get(playerId);
		
		Ban activeBan = BanManager.getActiveBan(playerId);
		if (BanManager.shouldNegativityHandleBans() && activeBan != null) {
			String kickMsgKey;
			String formattedExpiration;
			if (activeBan.isDefinitive()) {
				kickMsgKey = "ban.kick_def";
				formattedExpiration = "definitively";
			} else {
				kickMsgKey = "ban.kick_time";
				LocalDateTime expirationDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(activeBan.getExpirationTime()), ZoneId.systemDefault());
				formattedExpiration = UniversalUtils.GENERIC_DATE_TIME_FORMATTER.format(expirationDateTime);
			}
			e.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_BANNED);
			e.setKickMessage(Messages.getMessage(account, kickMsgKey, "%reason%", activeBan.getReason(), "%time%", formattedExpiration, "%by%", activeBan.getBannedBy()));
			Adapter.getAdapter().getAccountManager().dispose(playerId);
		} else if(!UniversalUtils.isValidName(e.getName())) {
			// check for ban / kick only if the player is not already banned
			String banReason = invalidNameSection.getString("name", "Invalid Name");
			if(invalidNameSection.getBoolean("ban", false)) {
				if(!BanManager.banActive) {
					SpigotNegativity.getInstance().getLogger().warning("Cannot ban player " + e.getName() + " for " + banReason + " because ban is NOT config.");
					SpigotNegativity.getInstance().getLogger().warning("Please, enable ban in config and restart your server");
					if(invalidNameSection.getBoolean("kick", true)) {
						e.setKickMessage(Messages.getMessage(account, "kick.kicked", "%name%", "Negativity", "%reason%", banReason));
						e.setLoginResult(Result.KICK_OTHER);
					}
				} else {
					BanManager.executeBan(Ban.active(playerId, banReason, "Negativity", BanType.PLUGIN, invalidNameSection.getLong("ban_time", -1), banReason));
					e.setLoginResult(Result.KICK_BANNED);
				}
			} else if(invalidNameSection.getBoolean("kick", true)) {
				e.setKickMessage(Messages.getMessage(account, "kick.kicked", "%name%", "Negativity", "%reason%", banReason));
				e.setLoginResult(Result.KICK_OTHER);
			}
		} else {
			int maxAllowedIP = Adapter.getAdapter().getConfig().getInt("cheats.special.max-player-by-ip.number");
			int currentOnIP = SpigotNegativityPlayer.getAllPlayers().values().stream().filter((np) -> {
				try {
					return np.getPlayer().isOnline() && np.getIP().equals(e.getAddress().getHostAddress());
				} catch (NullPointerException exc) {
					return false;
				}
			}).collect(Collectors.toList()).size();
			if(currentOnIP >= maxAllowedIP) {
				e.setKickMessage(Messages.getMessage(account, "kick.kicked", "%name%", "Negativity", "%reason%", Adapter.getAdapter().getConfig().getString("cheats.special.max-player-by-ip.name")));
				e.setLoginResult(Result.KICK_BANNED);
			}
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		SpigotNegativityPlayer.removeFromCache(p.getUniqueId());
		if(UniversalUtils.isMe(p.getUniqueId()))
			p.sendMessage(ChatColor.GREEN + "Ce serveur utilise Negativity ! Waw :')");
		if(!ProxyCompanionManager.searchedCompanion) {
			ProxyCompanionManager.searchedCompanion = true;
			Bukkit.getScheduler().runTaskLater(pl, () -> SpigotNegativity.sendProxyPing(p), 20);
		}
		SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(e.getPlayer());
		np.TIME_INVINCIBILITY = System.currentTimeMillis() + 8000;
		if (Perm.hasPerm(np, Perm.SHOW_REPORT)) {
			if(ReportCommand.REPORT_LAST.size() > 0) {
				for (String msg : ReportCommand.REPORT_LAST)
					p.sendMessage(msg);
				ReportCommand.REPORT_LAST.clear();
			}
			Bukkit.getScheduler().runTaskAsynchronously(pl, () -> Utils.sendUpdateMessageIfNeed(p));
		}
		SpigotNegativity.manageAutoVerif(p);
	}

	@EventHandler
	public void onLeave(PlayerQuitEvent e) {
		Player p = e.getPlayer();

		Bukkit.getScheduler().runTaskLater(pl, () -> SpigotNegativityPlayer.removeFromCache(p.getUniqueId()), 2);
	}

	@EventHandler
	public void slimeManager(PlayerMoveEvent e){
		Player p = e.getPlayer();
		SpigotNegativityPlayer np = SpigotNegativityPlayer.getNegativityPlayer(p);
		Location locBelow = p.getLocation().clone().subtract(0, 1, 0);
		if(!np.isFreeze && np.isUsingSlimeBlock && e.getFrom().getY() < e.getTo().getY()) // checking if need to check for freeze / slime
			return;
		Block b = locBelow.getBlock();
		if(np.isFreeze && !b.getType().equals(Material.AIR)) // freeze management
			e.setCancelled(true);
		
		if(b.getType().name().contains("SLIME")) { // manage slime
			np.isUsingSlimeBlock = true;
		} else if(np.isUsingSlimeBlock && (np.isOnGround() && !b.getType().name().contains("AIR")) && !locBelow.subtract(0, 1, 0).getBlock().getType().name().contains("PISTON")) {
			np.isUsingSlimeBlock = false;
		}
		if(!e.isCancelled()) {
			Bukkit.getPluginManager().callEvent(new NegativityPlayerMoveEvent(e));
		}
	}

	@EventHandler
	public void onBlockBreakEvent(BlockBreakEvent e) {
		Player p = e.getPlayer();
		NegativityAccount.get(p.getUniqueId()).getMinerate().addMine(MinerateType.getMinerateType(e.getBlock().getType().name()), p);
		SpigotNegativityPlayer.getNegativityPlayer(p).mustToBeSaved = true;
	}
	
	@EventHandler
	public void onTeleport(PlayerTeleportEvent e) {
		SpigotNegativityPlayer.getNegativityPlayer(e.getPlayer()).TIME_INVINCIBILITY = System.currentTimeMillis() + 2000;
	}
	
	@EventHandler
	public void onBedLeave(PlayerBedLeaveEvent e) {
		SpigotNegativityPlayer.getNegativityPlayer(e.getPlayer()).TIME_INVINCIBILITY = System.currentTimeMillis() + 2000;
	}
}
