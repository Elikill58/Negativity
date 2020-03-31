package com.elikill58.negativity.universal.ban;

import java.sql.Timestamp;

import javax.annotation.Nullable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.sponge.SpongeNegativity;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.NegativityPlayer;
import com.elikill58.negativity.universal.adapter.Adapter;

public class BanUtils {

	public static int computeBanDuration(NegativityPlayer player, int reliability, Cheat cheat) {
		try {
			ScriptEngineManager factory = new ScriptEngineManager();
			ScriptEngine engine = factory.getEngineByName("JavaScript");
			String script = Adapter.getAdapter().getStringInConfig("ban.time.calculator")
					.replaceAll("%reliability%", String.valueOf(reliability))
					.replaceAll("%alert%", String.valueOf(player.getWarn(cheat)))
					.replaceAll("%all_alert%", String.valueOf(player.getAllWarn(cheat)));
			return Integer.parseInt(engine.eval(script).toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	public static boolean shouldBan(Cheat cheat, NegativityPlayer np, int relia) {
		if (!cheat.isActive() || !BanManager.banActive) {
			return false;
		}
		Adapter ada = Adapter.getAdapter();
		return ada.getIntegerInConfig("ban.reliability_need") <= relia && ada.getIntegerInConfig("ban.alert_need") <= np.getAllWarn(cheat);
	}

	/**
	 * Basically common code for {@link SpigotNegativity#alertMod} and {@link SpongeNegativity#alertMod}.
	 * @return see {@link BanManager#executeBan}, null if banning was not needed
	 */
	@Nullable
	public static Ban banIfNeeded(NegativityPlayer player, Cheat cheat, int reliability) {
		if (!shouldBan(cheat, player, reliability)) {
			return null;
		}
		String reason = player.getReason(cheat);
		long banDuration = -1;
		int banDefThreshold = Adapter.getAdapter().getIntegerInConfig("ban.def.ban_time");
		boolean isDefinitive = BanManager.getLoggedBans(player.getUUID()).size() >= banDefThreshold;
		if (!isDefinitive) {
			banDuration = System.currentTimeMillis() + BanUtils.computeBanDuration(player, reliability, cheat);
		}
		return BanManager.executeBan(new Ban(player.getUUID(), "Cheat (" + reason + ")", "Negativity", BanType.MOD, banDuration, reason, BanStatus.ACTIVE));
	}

	public static void kickForBan(NegativityPlayer player, Ban ban) {
		player.banEffect();
		String formattedExpTime = new Timestamp(ban.getExpirationTime()).toString().split("\\.", 2)[0];
		player.kickPlayer(ban.getReason(), formattedExpTime, ban.getBannedBy(), ban.isDefinitive());
	}
}
