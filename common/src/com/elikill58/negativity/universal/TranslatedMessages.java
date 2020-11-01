package com.elikill58.negativity.universal;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

import com.elikill58.negativity.universal.account.NegativityAccount;
import com.elikill58.negativity.universal.translation.TranslationProvider;
import com.elikill58.negativity.universal.translation.TranslationProviderFactory;

public class TranslatedMessages {

	public static final String PLATFORM_PROVIDER_ID = "platform";

	public static String DEFAULT_LANG = Adapter.getAdapter().getConfig().getString("Translation.default");
	public static List<String> LANGS = Adapter.getAdapter().getConfig().getStringList("Translation.lang_available");
	public static boolean activeTranslation = Adapter.getAdapter().getConfig().getBoolean("Translation.active");
	private static String providerFactoryId = PLATFORM_PROVIDER_ID;
	private static TranslationProviderFactory platformFactory = null;
	private static final Map<String, TranslationProviderFactory> registeredFactories = new HashMap<>();

	private static final Map<String, TranslationProvider> translationProviders = new HashMap<>();
	@Nullable
	private static TranslationProvider fallbackTranslationProvider = null;

	public static void init() {
		DEFAULT_LANG = Adapter.getAdapter().getConfig().getString("Translation.default");
		LANGS = Adapter.getAdapter().getConfig().getStringList("Translation.lang_available");
		activeTranslation = Adapter.getAdapter().getConfig().getBoolean("Translation.active");

		platformFactory = Adapter.getAdapter().getPlatformTranslationProviderFactory();
		registerTranslationProviderFactory(PLATFORM_PROVIDER_ID, platformFactory);

		providerFactoryId = Adapter.getAdapter().getConfig().getString("Translation.provider");
		loadMessages();
	}

	public static void loadMessages() {
		translationProviders.clear();
		TranslationProviderFactory factory = registeredFactories.getOrDefault(providerFactoryId, platformFactory);
		if (activeTranslation) {
			for (String lang : LANGS) {
				TranslationProvider provider = factory.createTranslationProvider(lang);
				if (provider != null) {
					translationProviders.put(lang, provider);
				}
			}
		} else {
			TranslationProvider provider = factory.createTranslationProvider(DEFAULT_LANG);
			if (provider == null) {
				Adapter.getAdapter().getLogger().warn("Could not load the default translation provider");
				return;
			}
			translationProviders.put(DEFAULT_LANG, provider);
		}

		fallbackTranslationProvider = factory.createFallbackTranslationProvider();
	}

	public static void registerTranslationProviderFactory(String id, TranslationProviderFactory factory) {
		if (id == null || id.isEmpty()) {
			Adapter.getAdapter().getLogger().warn("Could not register TranslationProviderFactory " + factory.getClass().getName() + " because of invalid id " + id);
			return;
		}
		registeredFactories.put(id, factory);
	}

	public static String getDefaultLang() {
		return DEFAULT_LANG;
	}

	public static String getLang(UUID playerId) {
		if (activeTranslation) {
			return NegativityAccount.get(playerId).getLang();
		}
		return DEFAULT_LANG;
	}

	public static List<String> getStringListFromLang(String lang, String key, Object... placeholders) {
		TranslationProvider provider = getProviderFor(lang);
		if (provider != null) {
			List<String> messageList = provider.getList(key, placeholders);
			if (messageList != null && !messageList.isEmpty()) {
				return messageList;
			}
		}

		if (!lang.equals(DEFAULT_LANG)) {
			return getStringListFromLang(DEFAULT_LANG, key, placeholders);
		}

		if (fallbackTranslationProvider != null) {
			List<String> fallbackMessageList = fallbackTranslationProvider.getList(key, placeholders);
			if (fallbackMessageList != null && !fallbackMessageList.isEmpty()) {
				return fallbackMessageList;
			}
		}

		return Collections.singletonList(key);
	}

	public static String getStringFromLang(String lang, String key, Object... placeholders) {
		TranslationProvider provider = getProviderFor(lang);
		if (provider != null) {
			String message = provider.get(key, placeholders);
			if (message != null) {
				return message;
			}
		}

		if (!lang.equals(DEFAULT_LANG)) {
			return getStringFromLang(DEFAULT_LANG, key, placeholders);
		}

		if (fallbackTranslationProvider != null) {
			String fallbackMessage = fallbackTranslationProvider.get(key, placeholders);
			if (fallbackMessage != null) {
				return fallbackMessage;
			}
		}

		return key;
	}

	@Nullable
	private static TranslationProvider getProviderFor(String lang) {
		if (activeTranslation) {
			TranslationProvider provider = TranslatedMessages.translationProviders.get(lang);
			if (provider != null) {
				return provider;
			}
		}
		return TranslatedMessages.translationProviders.get(DEFAULT_LANG);
	}
}
