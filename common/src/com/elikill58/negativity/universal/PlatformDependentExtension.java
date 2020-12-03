package com.elikill58.negativity.universal;

/**
 * Implemented on extensions that should only be registered when on the
 * provided {@link #getPlatform() platform}.
 *
 * @see Adapter#getPlatformID()
 * @see Negativity#loadExtensions
 */
public interface PlatformDependentExtension {
	Platform getPlatform();
}
