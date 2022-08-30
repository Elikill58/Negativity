package com.elikill58.negativity.universal.warn.processor;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.warn.WarnManager;

/**
 * Used for registering WarnProcessors automatically via the {@link java.util.ServiceLoader ServiceLoader} mechanism.
 * <p>
 * To register a provider add the fully qualified name of the class implementing {@code Provider} to the file
 * {@code META-INF/services/com.elikill58.negativity.universal.warn.processor.WarnProcessorProvider}
 * <p>
 * The advantage of using a Provider instead of calling {@link WarnManager#registerProcessor(String, WarnProcessor)}
 * yourself is that it is guaranteed that your BanProcessor will be registered at the right time,
 * and will be registered again if Negativity reloads without your plugin noticing.
 * <p>
 * Plugins providing WarnProcessors <b>MUST</b> declare a dependency (hard or soft) on Negativity.
 */
public interface WarnProcessorProvider {
	
	/**
	 * @return the identifier of the WarnProcessor to register
	 */
	String getId();
	
	/**
	 * Creates a WarnProcessor, or returns {@code null} if it can't be used on the current system
	 * (missing plugin dependency, disabled in configuration, etc...)
	 *
	 * @param adapter the actual adapter used to create warn processor
	 * @return the BanProcessor, or {@code null} if the processor can't be used on the current system
	 */
	@Nullable
	WarnProcessor createWarnProcessor(Adapter adapter);
}
