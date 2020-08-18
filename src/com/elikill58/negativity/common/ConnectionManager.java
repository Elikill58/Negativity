package com.elikill58.negativity.common;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;
import java.util.stream.Collectors;

import com.elikill58.negativity.api.ChatColor;
import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.EventListener;
import com.elikill58.negativity.api.events.Listeners;
import com.elikill58.negativity.api.events.player.LoginEvent;
import com.elikill58.negativity.api.events.player.LoginEvent.Result;
import com.elikill58.negativity.api.events.player.PlayerConnectEvent;
import com.elikill58.negativity.common.commands.ReportCommand;
import com.elikill58.negativity.universal.Messages;
import com.elikill58.negativity.universal.NegativityAccount;
import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.ban.Ban;
import com.elikill58.negativity.universal.ban.BanManager;
import com.elikill58.negativity.universal.ban.BanType;
import com.elikill58.negativity.universal.config.ConfigAdapter;
import com.elikill58.negativity.universal.permissions.Perm;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class ConnectionManager implements Listeners {

	@EventListener
	public void onConnect(PlayerConnectEvent e) {
		Player p = e.getPlayer();
		if(UniversalUtils.isMe(p.getUniqueId()))
			p.sendMessage(ChatColor.GREEN + "Ce serveur utilise Negativity ! Waw :')");
		NegativityPlayer np = e.getNegativityPlayer();
		np.TIME_INVINCIBILITY = System.currentTimeMillis() + 8000;
		if (Perm.hasPerm(np, Perm.SHOW_REPORT)) {
			if(ReportCommand.REPORT_LAST.size() > 0) {
				for (String msg : ReportCommand.REPORT_LAST)
					p.sendMessage(msg);
				ReportCommand.REPORT_LAST.clear();
			}
			new Thread(() -> {
				if(!Perm.hasPerm(np, Perm.SHOW_ALERT))
					return;
				String newerVersion = UniversalUtils.getLatestVersion().orElse("unknow");
				if(newerVersion.equalsIgnoreCase(Adapter.getAdapter().getVersion()))
					return;
				Adapter.getAdapter().sendMessageRunnableHover(p, ChatColor.YELLOW + "New version of Negativity available (" + newerVersion +  "). "
					+ ChatColor.BOLD + "Download it here.", "Click here", "https://www.spigotmc.org/resources/48399/");
			}).start();
		}
		np.manageAutoVerif();
	}

	@EventListener
	public void onLogin(LoginEvent e) {
		if(!e.getLoginResult().equals(Result.ALLOWED)) // already kicked
			return;
		UUID playerId = e.getUUID();

		NegativityAccount account = NegativityAccount.get(playerId);
		
		Adapter ada = Adapter.getAdapter();
		Ban activeBan = BanManager.getActiveBan(playerId);
		if (activeBan != null) {
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
			e.setLoginResult(Result.KICK_BANNED);
			e.setKickMessage(Messages.getMessage(account, kickMsgKey, "%reason%", activeBan.getReason(), "%time%", formattedExpiration, "%by%", activeBan.getBannedBy()));
			ada.getAccountManager().dispose(playerId);
		} else if(!UniversalUtils.isValidName(e.getName())) {
			// check for ban / kick only if the player is not already banned
			ConfigAdapter invalidNameSection = Adapter.getAdapter().getConfig().getChild("cheats.special.invalid_name");
			String banReason = invalidNameSection.getString("name");
			if(invalidNameSection.getBoolean("ban")) {
				if(!BanManager.banActive) {
					ada.getLogger().warn("Cannot ban player " + e.getName() + " for " + banReason + " because ban is NOT config.");
					ada.getLogger().warn("Please, enable ban in config and restart your server");
					if(invalidNameSection.getBoolean("kick")) {
						e.setKickMessage(Messages.getMessage(account, "kick.kicked", "%name%", "Negativity", "%reason%", banReason));
						e.setLoginResult(Result.KICK_OTHER);
					}
				} else {
					BanManager.executeBan(Ban.active(playerId, banReason, "Negativity", BanType.PLUGIN, invalidNameSection.getInt("ban_time"), banReason));
					e.setLoginResult(Result.KICK_BANNED);
				}
			} else if(invalidNameSection.getBoolean("kick")) {
				e.setKickMessage(Messages.getMessage(account, "kick.kicked", "%name%", "Negativity", "%reason%", banReason));
				e.setLoginResult(Result.KICK_OTHER);
			}
		} else {
			int maxAllowedIP = ada.getConfig().getInt("cheats.special.max-player-by-ip.number");
			int currentOnIP = NegativityPlayer.getAllPlayers().values().stream().filter((np) -> np.getPlayer().isOnline() && np.getPlayer().getIP().equals(e.getAddress().getHostAddress())).collect(Collectors.toList()).size();
			if(currentOnIP >= maxAllowedIP) {
				e.setKickMessage(Messages.getMessage(account, "kick.kicked", "%name%", "Negativity", "%reason%", ada.getConfig().getString("cheats.special.max-player-by-ip.name")));
				e.setLoginResult(Result.KICK_BANNED);
			}
		}
	}
}
