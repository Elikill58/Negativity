package com.elikill58.negativity.universal.ban.storage;

import java.util.UUID;

import javax.annotation.Nullable;

import com.elikill58.negativity.universal.ban.ActiveBan;

/**
 * A class responsible for loading and saving active bans.
 * <p>
 * Implementations must not retain any state since they can be replaced at any time,
 * caching is fine as long as it does not require saving cached values implicitly.
 */
public interface ActiveBanStorage {

	/**
	 * Loads the active ban of the player identified by the given UUID.
	 *
	 * @param playerId the UUID of a player.
	 *
	 * @return the active ban of the player, or {@code null} if the player is not banned
	 */
	@Nullable
	ActiveBan load(UUID playerId);

	/**
	 * Saves the given active ban.
	 *
	 * @param ban the active ban to save.
	 */
	void save(ActiveBan ban);

	/**
	 * Removes the ban associated to the player identifier by the given UUID.
	 *
	 * @param playerId the UUID of the player
	 */
	void remove(UUID playerId);
}
