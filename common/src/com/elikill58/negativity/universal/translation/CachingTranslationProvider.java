package com.elikill58.negativity.universal.translation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.elikill58.negativity.universal.annotations.Nullable;

/**
 * A {@link TranslationProvider} that caches results returned from its wrapped provider if possible.
 * <p>
 * Messages with placeholders cannot be cached.
 */
public final class CachingTranslationProvider implements TranslationProvider {

	private final Map<String, String> cachedMessages = new HashMap<>();
	private final Map<String, List<String>> cachedMessageLists = new HashMap<>();

	private final TranslationProvider backingProvider;

	public CachingTranslationProvider(TranslationProvider backingProvider) {
		this.backingProvider = backingProvider;
	}

	@Nullable
	@Override
	public String get(String key) {
		return cachedMessages.computeIfAbsent(key, backingProvider::get);
	}

	@Nullable
	@Override
	public String get(String key, Object... placeholders) {
		if (placeholders.length == 0) {
			return get(key);
		}
		String rawMessage = get(key);
		if (rawMessage == null) {
			return null;
		}
		return applyPlaceholders(rawMessage, placeholders);
	}

	@Nullable
	@Override
	public List<String> getList(String key) {
		return cachedMessageLists.computeIfAbsent(key, msgKey -> {
			List<String> messageList = backingProvider.getList(msgKey);
			if (messageList == null || messageList.isEmpty()) {
				return null;
			}
			return messageList;
		});
	}

	@Nullable
	@Override
	public List<String> getList(String key, Object... placeholders) {
		if (placeholders.length == 0) {
			return getList(key);
		}
		List<String> rawMessages = getList(key);
		if (rawMessages == null) {
			return null;
		}
		return rawMessages.stream()
				.map(raw -> applyPlaceholders(raw, placeholders))
				.collect(Collectors.toList());
	}

	@Override
	public String applyPlaceholders(String raw, Object... placeholders) {
		return backingProvider.applyPlaceholders(raw, placeholders);
	}
}
