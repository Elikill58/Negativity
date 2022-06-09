package com.elikill58.negativity.universal.translation;

import java.util.List;
import java.util.stream.Collectors;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Provides messages, usually from a specific language.
 */
public interface TranslationProvider {

	/**
	 * Returns a message of a single line.
	 * @param key the key of the requested message
	 * 
	 * @return get the message according to the key, or null if not found
	 */
	@Nullable
	String get(String key);

	/**
	 * Returns a message of a single line.
	 * @param key the key of the requested message
	 * @param placeholders the placeholders to use in this message
	 * 
	 * @return get the message according to the key, or null if not found and replace placehodlers
	 */
	@Nullable
	default String get(String key, Object... placeholders) {
		String rawMessage = get(key);
		if (rawMessage == null || placeholders.length == 0) {
			return rawMessage;
		}
		return applyPlaceholders(rawMessage, placeholders);
	}

	/**
	 * Returns a message of one or more lines.
	 * @param key the key of the requested message
	 * 
	 * @return get all messages according to the key, or null if not found
	 */
	@Nullable
	List<String> getList(String key);

	/**
	 * Returns a message of one or more lines.
	 * @param key the key of the requested message
	 * @param placeholders the placeholders to use in this message
	 * 
	 * @return get all messages according to the key, or null if not found
	 */
	@Nullable
	default List<String> getList(String key, Object... placeholders) {
		List<String> rawLines = getList(key);
		if (rawLines == null || placeholders.length == 0) {
			return rawLines;
		}
		return rawLines.stream()
				.map(line -> applyPlaceholders(line, placeholders))
				.collect(Collectors.toList());
	}

	/**
	 * Applied placeholders to the given raw message.
	 * @param raw the raw message to process
	 * @param placeholders placeholders to use
	 * @return the message with placeholders applied
	 */
	String applyPlaceholders(String raw, Object... placeholders);
}
