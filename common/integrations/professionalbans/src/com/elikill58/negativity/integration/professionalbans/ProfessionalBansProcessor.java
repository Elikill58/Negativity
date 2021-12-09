package com.elikill58.negativity.integration.professionalbans;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.PluginDependentExtension;
import com.elikill58.negativity.universal.ban.Ban;
import com.elikill58.negativity.universal.ban.BanResult;
import com.elikill58.negativity.universal.ban.BanResult.BanResultType;
import com.elikill58.negativity.universal.ban.BanStatus;
import com.elikill58.negativity.universal.ban.BanType;
import com.elikill58.negativity.universal.ban.BanUtils;
import com.elikill58.negativity.universal.ban.processor.BanProcessor;
import com.elikill58.negativity.universal.ban.processor.BanProcessorProvider;

import de.tutorialwork.professionalbans.main.Main;

public class ProfessionalBansProcessor implements BanProcessor {

	@Override
	public BanResult executeBan(Ban ban) {
		NegativityPlayer player = NegativityPlayer.getCached(ban.getPlayerId());
		if (player == null) {
			return new BanResult(BanResultType.UNKNOW_PLAYER, null);
		}
		try {
            PreparedStatement ps = Main.mysql.getCon().prepareStatement("UPDATE bans SET BANNED='1', REASON=?, END=?, TEAMUUID=? WHERE UUID=?");
            ps.setString(1, ban.getReason());
            ps.setLong(2, ban.getExpirationTime());
            ps.setString(3, ban.getBannedByUUID().toString());
            ps.setString(4, ban.getPlayerId().toString());
            ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		BanUtils.kickForBan(player, ban);
		return new BanResult(BanResultType.DONE, ban);
	}

	@Override
	public BanResult revokeBan(UUID playerId) {
		NegativityPlayer player = NegativityPlayer.getCached(playerId);
		if (player == null) {
			return new BanResult(BanResultType.UNKNOW_PLAYER);
		}
		Main.ban.unban(playerId.toString());
		return new BanResult(new Ban(playerId, null, null, BanType.UNKNOW, 0, null, null, BanStatus.REVOKED));
	}

	@Nullable
	@Override
	public Ban getActiveBan(UUID playerId) {
		NegativityPlayer player = NegativityPlayer.getCached(playerId);
		if (player == null) {
			return null;
		}
		try {
			PreparedStatement ps = Main.mysql.getCon().prepareStatement("SELECT * FROM bans WHERE UUID = ? AND BANNED = 1");
			ps.setString(1, playerId.toString());
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				String by = rs.getString("TEAMUUID");
				String reason = rs.getString("REASON");
				long expireTime = rs.getLong("END");
				return new Ban(playerId, reason, by, BanType.UNKNOW, expireTime, null, null, expireTime > System.currentTimeMillis() ? BanStatus.ACTIVE : BanStatus.EXPIRED);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public List<Ban> getLoggedBans(UUID playerId) {
		return new ArrayList<>();
	}

	@Override
	public List<Ban> getActiveBanOnSameIP(String ip) {
		List<Ban> list = new ArrayList<>();
		try {
			PreparedStatement ps = Main.mysql.getCon().prepareStatement("SELECT * FROM ips WHERE IP = ? AND BANNED = 1");
			ps.setString(1, ip);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				UUID playerId = UUID.fromString(rs.getString("USED_BY"));
				String by = rs.getString("TEAMUUID");
				String reason = rs.getString("REASON");
				long expireTime = rs.getLong("END");
				int nbBans = rs.getInt("BANS");
				for(int i = 0; i < nbBans; i++)
					list.add(new Ban(playerId, reason, by, BanType.UNKNOW, expireTime, null, ip, expireTime > System.currentTimeMillis() ? BanStatus.ACTIVE : BanStatus.EXPIRED));
			}
			rs.close();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public List<Ban> getAllBans() {
		List<Ban> list = new ArrayList<>();
		try {
			PreparedStatement ps = Main.mysql.getCon().prepareStatement("SELECT * FROM bans WHERE BANNED = 1");
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				UUID playerId = UUID.fromString(rs.getString("USED_BY"));
				String by = rs.getString("TEAMUUID");
				String reason = rs.getString("REASON");
				long expireTime = rs.getLong("END");
				int nbBans = rs.getInt("BANS");
				for(int i = 0; i < nbBans; i++)
					list.add(new Ban(playerId, reason, by, BanType.UNKNOW, expireTime, null, null, expireTime > System.currentTimeMillis() ? BanStatus.ACTIVE : BanStatus.EXPIRED));
			}
			rs.close();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public static class Provider implements BanProcessorProvider, PluginDependentExtension {

		@Override
		public String getId() {
			return "professionalbans";
		}

		@Override
		public BanProcessor create(Adapter adapter) {
			return new ProfessionalBansProcessor();
		}

		@Override
		public String getPluginId() {
			return "ProfessionalBansReloaded";
		}
	}
}
