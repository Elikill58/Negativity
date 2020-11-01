package com.elikill58.negativity.universal.ban.storage;

import java.util.List;
import java.util.UUID;

import com.elikill58.negativity.universal.ban.Ban;

/**
 * A class responsible for loading and saving logged bans.
 * <p>
 * Implementations must not retain any state since they can be replaced at any time,
 * caching is fine as long as it does not require saving cached values implicitly.
 */
public interface BanLogsStorage {

	/**
	 * Loads logged bans into a <b>mutable</b> list.
	 *
	 * @param playerId the UUID of a player.
	 * @return a <b>mutable</b> list containing bans of the player identified by the given UUID
	 */
	List<Ban> load(UUID playerId);

	/**
	 * Adds a new entry in the bans log.
	 *
	 * @param ban the new entry ot add
	 */
	void save(Ban ban);
}
