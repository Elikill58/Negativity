package com.elikill58.negativity.universal;

import com.elikill58.negativity.universal.utils.NonnullByDefault;

/**
 * Contains player-related data that can be accessed when the player is offline.
 */
@NonnullByDefault
public class NegativityAccount {

	private String lang = TranslatedMessages.DEFAULT_LANG;

	public NegativityAccount() {}

	public NegativityAccount(String lang) {
		this.lang = lang;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}
}
