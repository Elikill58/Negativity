package com.elikill58.negativity.universal;

import java.util.Arrays;
import java.util.List;

/**
 * Implemented on extensions that should only be registered when on the
 * provided {@link #getPlatform() platform}.
 *
 * @see Adapter#getPlatformID()
 * @see Negativity#loadExtensions
 */
public interface PlatformDependentExtension {
	
	/**
	 * Get the platform. Prefer use {@link #getPlatforms()} to support multi-platform processors
	 * 
	 * @return the platform or null if multiple platform
	 */
	default Platform getPlatform() {
		return null;
	}
	
	/**
	 * Get all possible platform
	 * 
	 * @return all available platform
	 */
	default List<Platform> getPlatforms() {
		return Arrays.asList(getPlatform());
	}
	
}
