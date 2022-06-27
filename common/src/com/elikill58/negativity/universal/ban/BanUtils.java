package com.elikill58.negativity.universal.ban;

import java.sql.Timestamp;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.maths.Expression;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.ban.BanResult.BanResultType;
import com.elikill58.negativity.universal.detections.Cheat;
import com.elikill58.negativity.universal.permissions.Perm;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class BanUtils {

	public static int computeBanDuration(NegativityPlayer player, int reliability, Cheat cheat) {
		try {
			Expression expression = new Expression(UniversalUtils.replacePlaceholders(BanManager.getString(cheat, "time.calculator"), "%reliability%", reliability, "%alert%", player.getWarn(cheat), "%all_alert%", player.getAllWarn(cheat)));
			return (int) expression.calculate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	/**
	 * Check if the given player should be banned for the given cheat
	 * 
	 * @param cheat the cheat that can ban player
	 * @param np the concerned player
	 * @param relia the reliability of the alert
	 * @return the result of the ban
	 * @deprecated Use {@link #shouldBan(Cheat, NegativityPlayer, int, long)}
	 */
	public static BanResult shouldBan(Cheat cheat, NegativityPlayer np, int relia) {
		return shouldBan(cheat, np, relia, 0);
	}

	/**
	 * Check if the given player should be banned for the given cheat
	 * 
	 * @param cheat the cheat that can ban player
	 * @param np the concerned player
	 * @param relia the reliability of the alert
	 * @return the result of the ban
	 */
	public static BanResult shouldBan(Cheat cheat, NegativityPlayer np, int relia, long oldWarnAmount) {
		if(!BanManager.banActive || !cheat.getConfig().getBoolean("ban.active", true))
			return new BanResult(BanResultType.NOT_ENABLED);
		if(np.getAccount().isInBanning())
			return new BanResult(BanResultType.ALREADY_BANNED);
		if (!cheat.isActive() || Perm.hasPerm(np, Perm.BYPASS_BAN) || BanManager.getInt(cheat, "reliability_need") <= relia)
			return new BanResult(BanResultType.BYPASS);
		int alertNeeded = BanManager.getInt(cheat, "alert_need");
		return new BanResult(((long) oldWarnAmount / alertNeeded) < ((long) np.getAllWarn(cheat) / alertNeeded) ? BanResultType.DONE : BanResultType.BYPASS);
	}

	/**
	 * Basically common code for {@code SpigotNegativity#alertMod} and {@code SpongeNegativity#alertMod}.
	 * 
	 * @param player the player to ban if need
	 * @param cheat the cheat concerned by the try of need
	 * @param reliability the reliability of the cheat
	 * @return see {@link BanManager#executeBan}, null if banning was not needed
	 * @deprecated Use {@link #banIfNeeded(NegativityPlayer, Cheat, int, long)}
	 */
	public static BanResult banIfNeeded(NegativityPlayer player, Cheat cheat, int reliability) {
		return banIfNeeded(player, cheat, reliability, 0);
	}

	/**
	 * Basically common code for {@code SpigotNegativity#alertMod} and {@code SpongeNegativity#alertMod}.
	 * 
	 * @param player the player to ban if need
	 * @param cheat the cheat concerned by the try of need
	 * @param reliability the reliability of the cheat
	 * @return see {@link BanManager#executeBan}, null if banning was not needed
	 */
	public static BanResult banIfNeeded(NegativityPlayer player, Cheat cheat, int reliability, long oldWarnAmount) {
		BanResult result = shouldBan(cheat, player, reliability, oldWarnAmount);
		if (!result.isSuccess()) {
			return result;
		}
		player.getAccount().setInBanning(true);
		Adapter.getAdapter().getLogger().info("Banning " + player.getName() + " ...");
		String reason = player.getReason(cheat);
		long banDuration = -1;
		int banDefThreshold = BanManager.getInt(cheat, "def.ban_time");
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
