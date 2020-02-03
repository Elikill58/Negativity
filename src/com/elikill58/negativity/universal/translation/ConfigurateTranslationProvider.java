package com.elikill58.negativity.universal.translation;

import java.util.List;

import javax.annotation.Nullable;

import com.elikill58.negativity.universal.adapter.Adapter;
import com.google.common.reflect.TypeToken;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

public class ConfigurateTranslationProvider extends BaseNegativityTranslationProvider {

	private static final TypeToken<String> STRING_TOKEN = TypeToken.of(String.class);

	private final ConfigurationNode configNode;

	public ConfigurateTranslationProvider(ConfigurationNode configNode) {
		this.configNode = configNode;
	}

	@Nullable
	@Override
	public String get(String key) {
		Object[] path = key.split("\\.");
		return configNode.getNode(path).getString(key);
	}

	@Nullable
	@Override
	public List<String> getList(String key) {
		Object[] path = key.split("\\.");
		try {
			return configNode.getNode(path).getList(STRING_TOKEN, () -> null);
		} catch (ObjectMappingException e) {
			Adapter.getAdapter().error("Could not load message list: " + e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
}
