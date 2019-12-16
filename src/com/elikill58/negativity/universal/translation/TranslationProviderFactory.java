package com.elikill58.negativity.universal.translation;

import javax.annotation.Nullable;

public interface TranslationProviderFactory {

	@Nullable
	TranslationProvider createTranslationProvider(String language);
}
