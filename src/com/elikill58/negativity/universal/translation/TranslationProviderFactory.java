package com.elikill58.negativity.universal.translation;

import javax.annotation.Nullable;

public interface TranslationProviderFactory {

	@Nullable
	TranslationProvider createTranslationProvider(String language);

	/**
	 * Creates the {@link TranslationProvider} that will be used as fallback,
	 * in cases where the normally used {@link TranslationProvider} is
	 * not available or does not return a message (returns {@code null}).
	 * <p>
	 * If this fallback provider also returns no message then the
	 * message key is returned, as last resort.
	 *
	 * @return the fallback TranslationProvider
	 */
	@Nullable
	TranslationProvider createFallbackTranslationProvider();
}
