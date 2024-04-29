package com.elikill58.negativity.common.special;

import java.util.HashMap;
import java.util.Map.Entry;
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
import com.elikill58.negativity.universal.utils.IpAddressMatcher;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class VpnChecker extends Special implements Listeners {

	private final HashMap<IpAddressMatcher, VpnResult> subnets = new HashMap<>();

	public VpnChecker() {
		super(SpecialKeys.VPN_CHECKER, Materials.EYE_OF_ENDER);
	}

	@EventListener
	public void onLogin(LoginEvent e) {
		if (!e.getLoginResult().equals(Result.ALLOWED) || !isActive()) // already kicked
			return;
		if (e.getAddress() == null || e.getAddress().getHostAddress() == null) // no IP to use
			return;
		String ip = e.getAddress().getHostAddress();
		for (Entry<IpAddressMatcher, VpnResult> entries : new HashMap<>(subnets).entrySet()) {
			if (entries.getKey().matches(ip)) {
				manageVpn(e, ip, entries.getValue());
				return;
			}
		}
		CompletableFuture.runAsync(() -> {
			UniversalUtils.getContentFromURL("https://api.negativity.fr/ip/" + ip).ifPresent(result -> {
				try {
					VpnResult vpn = new VpnResult((JSONObject) new JSONParser().parse(result));
					subnets.put(vpn.getMatcher(), vpn);
					manageVpn(e, ip, vpn);
				} catch (ParseException e1) {
					e1.printStackTrace();
				}
			});
		});
	}

	private void manageVpn(LoginEvent e, String ip, VpnResult result) {
		boolean shouldKick = false, shouldBan = false;
		if (result.isVPN()) {
			shouldKick = getConfig().getBoolean("vpn.kick", true);
			shouldBan = getConfig().getBoolean("vpn.ban", false);
		}
		if (result.isProxy()) {
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
	}

	public class VpnResult {

		private final boolean proxy, vpn, hosting;
		private final String ip, code, name;

		public VpnResult(JSONObject data) {
			this.proxy = UniversalUtils.getBoolean(data.get("proxy").toString());
			this.hosting = UniversalUtils.getBoolean(data.get("hosting").toString());
			this.vpn = UniversalUtils.getBoolean(data.get("vpn").toString());

			this.ip = data.get("ip").toString();
			this.code = data.get("code").toString();
			this.name = data.get("name").toString();
		}

		/**
		 * WARN: This is not safe yet and can have false flag (mostly some known as
		 * false but really are. Opposite never happen.
		 * 
		 * @return true if it's hosting
		 */
		public boolean isHosting() {
			return hosting;
		}

		public boolean isProxy() {
			return proxy;
		}

		public boolean isVPN() {
			return vpn;
		}

		public String getIp() {
			return ip;
		}

		public String getCode() {
			return code;
		}

		public String getName() {
			return name;
		}
		
		public IpAddressMatcher getMatcher() {
			return new IpAddressMatcher(ip);
		}
	}
}
