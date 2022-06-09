package com.elikill58.negativity.universal.playerModifications;

import com.elikill58.negativity.universal.Adapter;

/**
 * Used for registering {@link PlayerModifications}s automatically via the {@link java.util.ServiceLoader ServiceLoader} mechanism.
 * <p>
 * To register a provider add the fully qualified name of the class implementing this class to the file
 * {@code META-INF/services/com.elikill58.negativity.universal.protection.PlayerModificationsProvider}
 * <p>
 * Plugins providing PlayerProtections <b>MUST</b> declare a dependency (hard or soft) on Negativity.
 */
public interface PlayerModificationsProvider {
	
	PlayerModifications create(Adapter adapter);
}
