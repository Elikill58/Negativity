package com.elikill58.negativity.universal.bypass;

import java.util.Collection;

import com.elikill58.negativity.universal.Adapter;

/**
 * Used for registering {@link BypassChecker}s automatically via the {@link java.util.ServiceLoader ServiceLoader} mechanism.
 * <p>
 * To register a provider add the fully qualified name of the class implementing this class to the file
 * {@code META-INF/services/com.elikill58.negativity.universal.bypass.BypassCheckerProvider}
 * <p>
 * The advantage of using this class instead of calling {@link BypassManager#addBypassChecker(BypassChecker)}
 * yourself is that it is guaranteed that your BypassChecker will be registered at the right time,
 * and will be registered again if Negativity reloads without your plugin noticing.
 * <p>
 * Plugins providing BypassCheckers <b>MUST</b> declare a dependency (hard or soft) on Negativity.
 */
public interface BypassCheckerProvider {
	
	Collection<BypassChecker> create(Adapter adapter);
}
