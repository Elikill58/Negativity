package com.elikill58.negativity.universal.playerModifications;

import com.elikill58.negativity.api.entity.Entity;
import com.elikill58.negativity.api.entity.Player;

/**
 * An extension used to indicate server-side modifications of a player.
 * These modifications are not Forge or Fabric mods the player might have installed
 * but whatever server-sided modding (of any nature) can do to change the
 * behaviour/capacities of a player (think of the fly mode added by Essentials.)
 * <p>
 * Such modifications might trigger false positives, this is why this class exists.
 *
 * @see PlayerModificationsManager
 * @see PlayerModificationsProvider
 */
public interface PlayerModifications {
	
	default String getDisplayname() {
		// Create the displayname based on implementing class name
		int suffixLength = PlayerModifications.class.getSimpleName().length();
		String implName = getClass().getSimpleName();
		return implName.substring(0, implName.length() - suffixLength);
	}
	
	/**
	 * Indicates whether the given player can be damaged by the given entity.
	 * <p>
	 * Implementors might need to use more context, like the player location,
	 * to determine if they are in a region of a world that protects them for example.
	 *
	 * @param player the player to check if is protected
	 * @param damager the entity that try to damage player
	 * @return true if should be protected
	 *
	 * @see PlayerModificationsManager#isProtected(Player, Entity)
	 */
	default boolean isProtected(Player player, Entity damager) {
		return false;
	}
	
	/**
	 * Indicates whether the given player is permitted to fly.
	 * 
	 * @param player the player which is looking for fly
	 * @return true if the player can fly
	 *
	 * @see PlayerModificationsManager#canFly(Player)
	 */
	default boolean canFly(Player player) {
		return false;
	}
	
	/**
	 * Indicates whether the given player can move as fast as they want.
	 * 
	 * @param player the player which is looking for speed
	 * @return true if the player can speed
	 *
	 * @see PlayerModificationsManager#isSpeedUnlocked(Player)
	 */
	default boolean isSpeedUnlocked(Player player) {
		return false;
	}
	
	/**
	 * Indicates whether the given player's movements should not be checked
	 * 
	 * @param player the player which can have ignore movement
	 * @return true if movement of this player should be ignored
	 *
	 * @see PlayerModificationsManager#shouldIgnoreMovementChecks(Player)
	 */
	default boolean shouldIgnoreMovementChecks(Player player) {
		return false;
	}
}
