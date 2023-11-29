package com.elikill58.negativity.common.special;

import java.util.concurrent.CompletableFuture;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.EventListener;
import com.elikill58.negativity.api.events.Listeners;
import com.elikill58.negativity.api.events.player.LoginEvent;
import com.elikill58.negativity.api.events.player.LoginEvent.Result;
import com.elikill58.negativity.api.item.Materials;
import com.elikill58.negativity.api.json.JSONObject;
import com.elikill58.negativity.api.json.parser.JSONParser;
import com.elikill58.negativity.api.json.parser.ParseException;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.SanctionnerType;
import com.elikill58.negativity.universal.ban.Ban;
import com.elikill58.negativity.universal.ban.BanManager;
import com.elikill58.negativity.universal.detections.Special;
import com.elikill58.negativity.universal.detections.keys.SpecialKeys;
import com.elikill58.negativity.universal.logger.Debug;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class VpnChecker extends Special implements Listeners {

	public VpnChecker() {
		super(SpecialKeys.VPN_CHECKER, Materials.EYE_OF_ENDER);
	}

	@EventListener
	public void onLogin(LoginEvent e) {
		if (!e.getLoginResult().equals(Result.ALLOWED) || !isActive()) // already kicked
			return;
		String ip = e.getAddress().getHostAddress();
		CompletableFuture.runAsync(() -> {
			UniversalUtils.getContentFromURL("https://api.negativity.fr/ip/" + ip).ifPresent(result -> {
				try {
					JSONObject data = (JSONObject) new JSONParser().parse(result);
					boolean proxy = UniversalUtils.getBoolean(data.get("proxy").toString());
					boolean vpn = UniversalUtils.getBoolean(data.get("vpn").toString());
					Adapter.getAdapter().debug(Debug.FEATURE, "IP data for " + ip + ": " + result +" > " + data.get("proxy").toString() + " : " + data.get("vpn").toString() + " >> " + proxy + " / " + vpn);
					boolean shouldKick = false, shouldBan = false;
					if (vpn) {
						shouldKick = getConfig().getBoolean("vpn.kick", true);
						shouldBan = getConfig().getBoolean("vpn.ban", false);
					}
					if (proxy) {
						shouldKick = getConfig().getBoolean("proxy.kick", true) || shouldKick;
						shouldBan = getConfig().getBoolean("proxy.ban", false) || shouldBan;
					}
					String reason = getConfig().getString("message-reason");
					if (shouldBan) {
						String banTime = getConfig().getString("vpn.time");
						BanManager.executeBan(Ban.active(e.getUUID(), reason, "Negativity", SanctionnerType.CONSOLE, banTime == "" ? -1 : Integer.parseInt(banTime) * 1000, "VPN/Proxy", ip));
					} else if (shouldKick) {
						e.setKickMessage(reason);
						e.setLoginResult(Result.KICK_OTHER);
						Player cible = Adapter.getAdapter().getPlayer(e.getUUID());
						if (cible != null)
							cible.kick(reason);
					}
				} catch (ParseException e1) {
					e1.printStackTrace();
				}
			});
		});
	}
}
