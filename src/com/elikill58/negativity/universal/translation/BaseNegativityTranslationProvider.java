package com.elikill58.negativity.universal.translation;

import com.elikill58.negativity.universal.utils.UniversalUtils;

/**
 * Base TranslationProvider for the default Negativity messages system
 */
public abstract class BaseNegativityTranslationProvider implements TranslationProvider {

	@Override
	public String applyPlaceholders(String raw, Object... placeholders) {
		return UniversalUtils.replacePlaceholders(raw, placeholders);
	}
}
