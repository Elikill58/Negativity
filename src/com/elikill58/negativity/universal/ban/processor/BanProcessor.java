package com.elikill58.negativity.universal.ban.processor;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import com.elikill58.negativity.universal.ban.Ban;
import com.elikill58.negativity.universal.ban.BanManager;

/**
 * Decides what to do with ban and unban requests as well as active and logged bans queries.
 * <p>
 * {@link BanManager} basically delegates actions to a BanProcessor.
 */
public interface BanProcessor {

	/**
	 * Executes the given ban.
	 * <p>
	 * The ban may not be executed for any processor-specific reason.
	 *
	 * @return the ban that has been executed, or {@code null} if the ban has not been executed.
	 * @param ban
	 */
	@Nullable
	Ban executeBan(Ban ban);

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
	@Nullable
	Ban revokeBan(UUID playerId);

	default boolean isBanned(UUID playerId) {
		return getActiveBan(playerId) != null;
	}

	@Nullable
	Ban getActiveBan(UUID playerId);

	/**
	 * Get all old ban for the given UUID
	 * 
	 * @param playerId player UUID who we are looking for bans
	 * @return all logged bans
	 */
	List<Ban> getLoggedBans(UUID playerId);
	
	/**
	 * Get all current ban.
	 * 
	 * @return all active bans
	 */
	List<Ban> getAllBans();

	/**
	 * @return whether Negativity should kick banned players when this BanProcessor
	 * 	is used, should return {@code false} if bans are handled by another plugin.
	 */
	default boolean isHandledByNegativity() {
		return false;
	}
}
