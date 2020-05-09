package com.elikill58.negativity.universal.config;

import java.util.List;

public interface ConfigAdapter {

	String getString(String key);

	boolean getBoolean(String key);

	int getInt(String key);

	double getDouble(String key);

	List<String> getStringList(String key);

	void set(String key, Object value);
}
