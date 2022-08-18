package com.elikill58.negativity.universal.warn.processor;

import java.util.List;
import java.util.UUID;

import com.elikill58.negativity.universal.warn.WarnResult;
import com.elikill58.negativity.universal.warn.Warn;
import com.elikill58.negativity.universal.warn.WarnManager;

/**
 * Decides what to do with warn and unban requests as well as active and logged bans queries.
 * <p>
 * {@link WarnManager} basically delegates actions to a WarnProcessor.
 *
 * @see WarnProcessorProvider BanProcessorProvider - the preferred way to register WarnProcessors
 * @see WarnManager#registerProcessor(String, WarnProcessor)
 */
public interface WarnProcessor {

	/**
	 * Enable the given processor.
	 */
	default void enable() {
		
	}
	
	/**
	 * Disable the given processor.
	 */
	default void disable() {
		
	}
	
	/**
	 * Executes the given warn.
	 * <p>
	 * The warn may not be executed for any processor-specific reason.
	 *
	 * @param warn the warn to execute
	 * @return the result of the execution
	 */
	WarnResult executeWarn(Warn warn);

	/**
	 * Revokes all active warns for the given player
	 * <p>
	 * The revocation may fail for any processor-specific reason.
	 * <p>
	 * If the revocation was successful a LoggedWarn must always be returned, even if it will not persist in any way.
	 *
	 * @param playerId the UUID of the player to unwarn
	 * @param revoker who revoke warn
	 *
	 * @return the logged revoked warn or {@code null} if the revocation failed.
	 */
	WarnResult revokeWarn(UUID playerId, String revoker);

	/**
	 * Revokes only given warn for the given player
	 * <p>
	 * The revocation may fail for any processor-specific reason.
	 * <p>
	 * If the revocation was successful a LoggedWarn must always be returned, even if it will not persist in any way.
	 *
	 * @param warn warn to remove
	 * @param revoker who revoke warn
	 *
	 * @return the logged revoked warn or {@code null} if the revocation failed.
	 */
	WarnResult revokeWarn(Warn warn, String revoker);

	default boolean isWarned(UUID playerId) {
		return !getActiveWarn(playerId).isEmpty();
	}

	/**
	 * Get all warns of a given player
	 * 
	 * @param playerId UUID of player
	 * @return all warns of player, or empty
	 */
	List<Warn> getActiveWarn(UUID playerId);
	
	/**
	 * Get all active warn on the same IP
	 * 
	 * @param ip the IP where we are looking for warn
	 * @return all ban on IP
	 */
	List<Warn> getActiveWarnOnSameIP(String ip);
	
	/**
	 * Get all current warn.
	 * 
	 * @return all active warns
	 */
	List<Warn> getAllWarns();
	
	/**
	 * Get the name of the processor
	 * 
	 * @return processor name
	 */
	String getName();
	
	/**
	 * Get the description of the processor, including what's not available with this
	 * 
	 * @return the processor description
	 */
	List<String> getDescription();
}
