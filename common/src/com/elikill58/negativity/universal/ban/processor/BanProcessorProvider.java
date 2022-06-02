package com.elikill58.negativity.universal.ban.processor;

import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.annotations.Nullable;
import com.elikill58.negativity.universal.ban.BanManager;

/**
 * Used for registering BanProcessors automatically via the {@link java.util.ServiceLoader ServiceLoader} mechanism.
 * <p>
 * To register a provider add the fully qualified name of the class implementing {@code Provider} to the file
 * {@code META-INF/services/com.elikill58.negativity.universal.ban.processor.BanProcessorProvider}
 * <p>
 * The advantage of using a Provider instead of calling {@link BanManager#registerProcessor(String, BanProcessor)}
 * yourself is that it is guaranteed that your BanProcessor will be registered at the right time,
 * and will be registered again if Negativity reloads without your plugin noticing.
 * <p>
 * Plugins providing BanProcessors <b>MUST</b> declare a dependency (hard or soft) on Negativity.
 */
public interface BanProcessorProvider {
	
	/**
	 * @return the identifier of the BanProcessor to register
	 */
	String getId();
	
	/**
	 * Creates a BanProcessor, or returns {@code null} if it can't be used on the current system
	 * (missing plugin dependency, disabled in configuration, etc...)
	 *
	 * @return the BanProcessor, or {@code null} if the processor can't be used on the current system
	 */
	@Nullable
	BanProcessor create(Adapter adapter);
}
