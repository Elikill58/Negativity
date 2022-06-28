package com.elikill58.negativity.common;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.colors.ChatColor;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.EventListener;
import com.elikill58.negativity.api.events.Listeners;
import com.elikill58.negativity.api.events.player.LoginEvent;
import com.elikill58.negativity.api.events.player.LoginEvent.Result;
import com.elikill58.negativity.api.events.player.PlayerConnectEvent;
import com.elikill58.negativity.api.events.player.PlayerLeaveEvent;
import com.elikill58.negativity.api.events.player.PlayerTeleportEvent;
import com.elikill58.negativity.common.commands.ReportCommand;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Messages;
import com.elikill58.negativity.universal.account.NegativityAccount;
import com.elikill58.negativity.universal.ban.AltAccountBan;
import com.elikill58.negativity.universal.ban.Ban;
import com.elikill58.negativity.universal.ban.BanManager;
import com.elikill58.negativity.universal.ban.BanStatus;
import com.elikill58.negativity.universal.ban.BanType;
import com.elikill58.negativity.universal.permissions.Perm;
import com.elikill58.negativity.universal.storage.account.NegativityAccountStorage;
import com.elikill58.negativity.universal.utils.SemVer;
import com.elikill58.negativity.universal.utils.UniversalUtils;
import com.elikill58.negativity.universal.webhooks.WebhookManager;

public class ConnectionManager implements Listeners {

	@EventListener
	public void onConnect(PlayerConnectEvent e) {
		Player p = e.getPlayer();
		NegativityPlayer np = e.getNegativityPlayer();
		np.timeInvincibility = System.currentTimeMillis() + 8000;
		
		if(UniversalUtils.isMe(p.getUniqueId()))
			p.sendMessage(ChatColor.GREEN + "Ce serveur utilise Negativity ! Waw :')");
		
		String ip = p.getIP();
		List<Ban> banOnIP = BanManager.getActiveBanOnSameIP(ip);
		if(banOnIP != null && !banOnIP.isEmpty()) {
			List<NegativityAccount> accounts = new ArrayList<>();
			banOnIP.forEach((ban) -> accounts.add(NegativityAccount.get(ban.getPlayerId())));
			
			long warns = accounts.stream().mapToLong(NegativityAccount::countAllWarns).sum();
			
			AltAccountBan alt = BanManager.getAltBanFor(banOnIP.size() + 1);
			if(alt != null && alt.hasCondition(warns, warns)) {
				String reason = alt.getAlertMessage() == null ? "Alt unauthorized" : alt.getAlertMessage();
				switch (alt.getAction()) {
				case ALERT:
					p.sendMessage(alt.getAlertMessage());
					break;
				case ALERT_MOD:
					Adapter.getAdapter().getOnlinePlayers().stream().filter((mod) -> Perm.hasPerm(mod, Perm.SHOW_ALERT))
							.forEach((mod) -> mod.sendMessage(alt.getAlertMessage().replaceAll("%name%", p.getName())));
					break;
				case BAN:
					BanManager.executeBan(new Ban(p.getUniqueId(), reason, "Negativity", BanType.PLUGIN, alt.isBanDef() ? -1 : alt.getBanTime(), "Alt", ip, BanStatus.ACTIVE));
					break;
				case BAN_ALL:
					long time = alt.isBanDef() ? -1 : alt.getBanTime();
					for(UUID allPlayers : NegativityAccountStorage.getStorage().getPlayersOnIP(ip)) {
						BanManager.executeBan(new Ban(allPlayers, reason, "Negativity", BanType.PLUGIN, time, "Alt", ip, BanStatus.ACTIVE));
					}
					break;
				}
			}
		}
		
		if (Perm.hasPerm(np, Perm.SHOW_REPORT)) {
			if(ReportCommand.REPORT_LAST.size() > 0) {
				for (String msg : ReportCommand.REPORT_LAST)
					p.sendMessage(msg);
				ReportCommand.REPORT_LAST.clear();
			}
			
			if (Perm.hasPerm(np, Perm.SHOW_ALERT)) {
				CompletableFuture.runAsync(() -> {
					SemVer latestVersion = UniversalUtils.getLatestVersionIfNewer();
					if (latestVersion != null) {
						Adapter.getAdapter().sendMessageRunnableHover(p,
							ChatColor.YELLOW + "New version of Negativity available: " + latestVersion.toFormattedString() + ". "
								+ ChatColor.BOLD + "Download it here.",
							"Click here", "https://www.spigotmc.org/resources/48399/");
					}
				});
			}
		}
	}

	@EventListener
	public void onLogin(LoginEvent e) {
		if(!BanManager.shouldNegativityHandleBans() || !e.getLoginResult().equals(Result.ALLOWED) || !BanManager.banActive) // already kicked or ban not enabled
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
		}
	}
	
	@EventListener
	public void onTeleport(PlayerTeleportEvent e) {
		Player p = e.getPlayer();
		NegativityPlayer.getNegativityPlayer(p).timeInvincibility = System.currentTimeMillis() + 1000;
	}
	
	@EventListener
	public void onLeft(PlayerLeaveEvent e) {
		WebhookManager.getWebhooks().forEach(w -> w.clean(e.getPlayer()));
	}
}
