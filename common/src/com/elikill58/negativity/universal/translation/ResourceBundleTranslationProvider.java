package com.elikill58.negativity.universal.translation;

import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.elikill58.negativity.universal.utils.UniversalUtils;

public class ResourceBundleTranslationProvider implements TranslationProvider {

	private final ResourceBundle bundle;

	public ResourceBundleTranslationProvider(ResourceBundle bundle) {
		this.bundle = bundle;
	}

	@Nullable
	@Override
	public String get(String key) {
		try {
			return bundle.getString(key);
		} catch (MissingResourceException ignore) {
		}
		return null;
	}

	@Nullable
	@Override
	public List<String> getList(String key) {
		List<String> lines = new ArrayList<>();
		try {
			// Arbitrary limit, should be enough for all use cases
			for (int i = 0; i < 32; i++) {
				lines.add(bundle.getString(key + '.' + i));
			}
		} catch (MissingResourceException ignore) {
		}
		if (lines.isEmpty()) {
			String rootLine = get(key);
			if (rootLine != null) {
				lines.add(rootLine);
			}
		}
		return lines;
	}

	@Override
	public String applyPlaceholders(String raw, Object... placeholders) {
		return UniversalUtils.replacePlaceholders(raw, placeholders);
	}

	// We may use to use this implementation if we migrate to MessageFormat one day
	//@Override
	//public String applyPlaceholders(String raw, Object... placeholders) {
	//	if (placeholders.length == 0) {
	//		return raw;
	//	}
	//
	//	// Collects every placeholders values for MessageFormat.format
	//	Object[] formatPlaceholders = new Object[placeholders.length / 2];
	//	for (int i = 0; i < formatPlaceholders.length; i++) {
	//		formatPlaceholders[i] = placeholders[i * 2 + 1];
	//	}
	//
	//	// Allows usage of legacy placeholders (like %name%)
	//	// May be removed in the future if we decide to drop these and only use MessageFormat
	//	String firstPass = UniversalUtils.replacePlaceholders(raw, placeholders);
	//	return MessageFormat.format(firstPass, formatPlaceholders);
	//}
}
