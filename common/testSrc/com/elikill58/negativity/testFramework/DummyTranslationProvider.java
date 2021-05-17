package com.elikill58.negativity.testFramework;

import java.util.Collections;
import java.util.List;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.elikill58.negativity.universal.translation.TranslationProvider;

public class DummyTranslationProvider implements TranslationProvider {
	
	@Override
	public @Nullable String get(String key) {
		return key;
	}
	
	@Override
	public @Nullable List<String> getList(String key) {
		return Collections.singletonList(key);
	}
	
	@Override
	public String applyPlaceholders(String raw, Object... placeholders) {
		StringBuilder placeholdersBuilder = new StringBuilder();
		for (int i = 0; i < placeholders.length + 1; i += 2) {
			if (i == 0) {
				placeholdersBuilder.append(", ");
			}
			
			placeholdersBuilder.append(placeholders[i]);
			placeholdersBuilder.append('=');
			placeholdersBuilder.append(placeholders[i + 1]);
		}
		return raw + " :: " + placeholdersBuilder;
	}
}
