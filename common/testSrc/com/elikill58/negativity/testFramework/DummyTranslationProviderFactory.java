package com.elikill58.negativity.testFramework;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.elikill58.negativity.universal.translation.TranslationProvider;
import com.elikill58.negativity.universal.translation.TranslationProviderFactory;

public class DummyTranslationProviderFactory implements TranslationProviderFactory {
	
	@Override
	public @Nullable TranslationProvider createTranslationProvider(String language) {
		return createFallbackTranslationProvider();
	}
	
	@Override
	public @Nullable TranslationProvider createFallbackTranslationProvider() {
		return new DummyTranslationProvider();
	}
}
