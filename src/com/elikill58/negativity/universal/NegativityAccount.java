package com.elikill58.negativity.universal;

import com.elikill58.negativity.universal.utils.NonnullByDefault;

/**
 * Contains player-related data that can be accessed when the player is offline.
 */
@NonnullByDefault
public abstract class NegativityAccount {

	private String lang = TranslatedMessages.DEFAULT_LANG;

	public String getLang() {
		return lang;
	}

	public void setLang(String s) {
		this.lang = s;
	}
}
