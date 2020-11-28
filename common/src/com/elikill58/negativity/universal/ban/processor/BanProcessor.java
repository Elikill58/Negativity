package com.elikill58.negativity.universal.ban.processor;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import com.elikill58.negativity.universal.ban.Ban;
import com.elikill58.negativity.universal.ban.BanManager;
import com.elikill58.negativity.universal.ban.BanResult;

/**
 * Decides what to do with ban and unban requests as well as active and logged bans queries.
 * <p>
 * {@link BanManager} basically delegates actions to a BanProcessor.
 *
 * @see BanProcessorProvider BanProcessorProvider - the preferred way to register BanProcessors
 * @see BanManager#registerProcessor(String, BanProcessor)
 */
public interface BanProcessor {

	/**
	 * Executes the given ban.
	 * <p>
	 * The ban may not be executed for any processor-specific reason.
	 *
	 * @param ban the ban to execute
	 * @return the result of the execution
	 */
	BanResult executeBan(Ban ban);

	/**
	 * Revokes the active ban of the player identified by the given UUID.
	 * <p>
	 * The revocation may fail for any processor-specific reason.
	 * <p>
	 * If the revocation was successful a LoggedBan must always be returned, even if it will not persist in any way.
	 *
	 * @param playerId the UUID of the player to unban
	 *
	 * @return the logged revoked ban or {@code null} if the revocation failed.
	 */
	BanResult revokeBan(UUID playerId);

	default boolean isBanned(UUID playerId) {
		return getActiveBan(playerId) != null;
	}

	@Nullable
	Ban getActiveBan(UUID playerId);

	/**
	 * Get revoked ban of the specified player
	 * Don't include active one
	 * 
	 * @param playerId the UUID of the player to get logged ban
	 * @return all logged ban
	 */
	List<Ban> getLoggedBans(UUID playerId);
	
	/**
	 * Get all active ban on the same IP
	 * 
	 * @param ip the IP where we are looking for ban
	 * @return all ban on IP
	 */
	List<Ban> getActiveBanOnSameIP(String ip);
}
