package com.elikill58.negativity.universal.config;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

public interface ConfigAdapter {

	String getString(String key);

	boolean getBoolean(String key);

	int getInt(String key);

	double getDouble(String key);

	List<String> getStringList(String key);

	ConfigAdapter getChild(String key);

	Collection<String> getKeys();

	void set(String key, Object value);

	void save() throws IOException;

	void load() throws IOException;
}
