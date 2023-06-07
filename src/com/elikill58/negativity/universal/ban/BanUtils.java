package com.elikill58.negativity.universal.ban;

import java.sql.Timestamp;
import java.util.Locale;

import javax.annotation.Nullable;

import com.elikill58.deps.mariuszgromada.mxparser.Expression;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.NegativityPlayer;
import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.config.ConfigAdapter;
import com.elikill58.negativity.universal.permissions.Perm;

public class BanUtils {

	public static double computeBanDuration(NegativityPlayer player, int reliability, Cheat cheat) {
		try {
			Expression expression = new Expression(Adapter.getAdapter().getConfig().getString("ban.time.calculator")
					.replaceAll("%reliability%", String.valueOf(reliability))
					.replaceAll("%alert%", String.valueOf(player.getWarn(cheat)))
					.replaceAll("%all_alert%", String.valueOf(player.getAllWarn(cheat))));
			double d = expression.calculate();
			if(((int) d) == Integer.MAX_VALUE)
				Adapter.getAdapter().debug("Reach max int value for ban time value: " + d);
			if(((long) d) == Long.MAX_VALUE)
				Adapter.getAdapter().debug("Reach max long value for ban time value: " + d);
			return d;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	public static boolean shouldBan(Cheat cheat, NegativityPlayer np, int relia) {
		Adapter ada = Adapter.getAdapter();
		ConfigAdapter config = ada.getConfig();
		if (!cheat.isActive() || !BanManager.banActive || np.isInBanning() || (config.getBoolean("Permissions.bypass.active") && Perm.hasPerm(np, Perm.BYPASS_BAN))) {
			ada.debug("[BanUtils-shouldBan] cheatActive: " + cheat.isActive()  + ", banActive: " + BanManager.banActive + ", alreadyBanning: " + np.isInBanning() + ", bypassPerm: " + Perm.hasPerm(np, Perm.BYPASS_BAN));
			return false;
		}
		if(config.getStringList("ban.cheat_disabled").contains(cheat.getKey().toLowerCase(Locale.ROOT))) {
			ada.debug("[BanUtils-shouldBan] Cheat " + cheat.getKey() + " disabled.");
			return false;
		}
		ada.debug("[BanUtils-shouldBan] Reliability need: " + (config.getInt("ban.reliability_need") <= relia)  + ", alert needed: " + (config.getInt("ban.alert_need") <= np.getAllWarn(cheat)));
		return config.getInt("ban.reliability_need") <= relia && config.getInt("ban.alert_need") <= np.getAllWarn(cheat);
	}

	/**
	 * Basically common code for {@link SpigotNegativity#alertMod} and {@link SpongeNegativity#alertMod}.
	 * @return see {@link BanManager#executeBan}, null if banning was not needed
	 */
	@Nullable
	public static Ban banIfNeeded(NegativityPlayer player, Cheat cheat, int reliability) {
		if (!shouldBan(cheat, player, reliability)) {
			Adapter.getAdapter().debug("[BanUtils-shouldBan] Should NOT ban " + player.getName() + ".");
			return null;
		}
		player.setInBanning(true);
		Adapter.getAdapter().getLogger().info("Banning " + player.getName() + " ...");
		String reason = player.getReason(cheat);
		long banDuration = -1;
		int banDefThreshold = Adapter.getAdapter().getConfig().getInt("ban.def.ban_time");
		boolean isDefinitive = BanManager.getLoggedBans(player.getUUID()).size() >= banDefThreshold;
		if (!isDefinitive) {
			banDuration = (long) (System.currentTimeMillis() + BanUtils.computeBanDuration(player, reliability, cheat));
		}
		return BanManager.executeBan(Ban.active(player.getUUID(), "Cheat (" + reason + ")", "Negativity", BanType.MOD, banDuration, reason));
	}

	public static void kickForBan(NegativityPlayer player, Ban ban) {
		player.banEffect();
		String formattedExpTime = new Timestamp(ban.getExpirationTime()).toString().split("\\.", 2)[0];
		player.kickPlayer(ban.getReason(), formattedExpTime, ban.getBannedBy(), ban.isDefinitive());
	}
}
