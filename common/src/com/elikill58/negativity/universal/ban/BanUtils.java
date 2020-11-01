package com.elikill58.negativity.universal.ban;

import java.sql.Timestamp;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.maths.Expression;
import com.elikill58.negativity.api.yaml.config.Configuration;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.ban.BanResult.BanResultType;
import com.elikill58.negativity.universal.permissions.Perm;

public class BanUtils {

	public static int computeBanDuration(NegativityPlayer player, int reliability, Cheat cheat) {
		try {
			Expression expression = new Expression(BanManager.getBanConfig().getString("time.calculator")
					.replaceAll("%reliability%", String.valueOf(reliability))
					.replaceAll("%alert%", String.valueOf(player.getWarn(cheat)))
					.replaceAll("%all_alert%", String.valueOf(player.getAllWarn(cheat))));
			return (int) expression.calculate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	public static BanResult shouldBan(Cheat cheat, NegativityPlayer np, int relia) {
		if(!BanManager.banActive)
			return new BanResult(BanResultType.NOT_ENABLED);
		if(np.getAccount().isInBanning())
			return new BanResult(BanResultType.ALREADY_BANNED);
		if (!cheat.isActive() || Perm.hasPerm(np, Perm.BYPASS_BAN))
			return new BanResult(BanResultType.BYPASS);
		Configuration conf = BanManager.getBanConfig();
		return new BanResult(conf.getInt("reliability_need") <= relia && conf.getInt("alert_need") <= np.getAllWarn(cheat) ? BanResultType.DONE : BanResultType.BYPASS);
	}

	/**
	 * Basically common code for {@code SpigotNegativity#alertMod} and {@code SpongeNegativity#alertMod}.
	 * @return see {@link BanManager#executeBan}, null if banning was not needed
	 */
	public static BanResult banIfNeeded(NegativityPlayer player, Cheat cheat, int reliability) {
		BanResult result = shouldBan(cheat, player, reliability);
		if (!result.isSuccess()) {
			return result;
		}
		player.getAccount().setInBanning(true);
		Adapter.getAdapter().getLogger().info("Banning " + player.getName() + " ...");
		String reason = player.getReason(cheat);
		long banDuration = -1;
		int banDefThreshold = BanManager.getBanConfig().getInt("def.ban_time");
		boolean isDefinitive = BanManager.getLoggedBans(player.getUUID()).size() >= banDefThreshold;
		if (!isDefinitive) {
			banDuration = System.currentTimeMillis() + BanUtils.computeBanDuration(player, reliability, cheat);
		}
		return BanManager.executeBan(Ban.active(player.getUUID(), "Cheat (" + reason + ")", "Negativity", BanType.MOD, banDuration, reason, player.getPlayer().getIP()));
	}

	public static void kickForBan(NegativityPlayer player, Ban ban) {
		player.banEffect();
		String formattedExpTime = new Timestamp(ban.getExpirationTime()).toString().split("\\.", 2)[0];
		player.kickPlayer(ban.getReason(), formattedExpTime, ban.getBannedBy(), ban.isDefinitive());
	}
}
