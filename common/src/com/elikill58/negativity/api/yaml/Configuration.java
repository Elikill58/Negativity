package com.elikill58.negativity.api.yaml;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.elikill58.negativity.universal.Adapter;

public class Configuration {

	protected Map<String, Object> self;
	protected Configuration defaults;
	protected File file;
	protected boolean isSaving = false;
	protected long lastSaveAsked = 0;

	public Configuration() {
		this(null);
	}

	public Configuration(Configuration defaults) {
		this(null, new LinkedHashMap<String, Object>(), defaults);
	}

	Configuration(File file, Map<?, ?> map, Configuration defaults) {
		this.file = file;
		this.self = new LinkedHashMap<String, Object>();
		this.defaults = defaults;
		for (Map.Entry<?, ?> entry : map.entrySet()) {
			String key = (entry.getKey() == null) ? "null" : entry.getKey().toString();
			if (entry.getValue() instanceof Map) {
				Configuration nextDef = defaults == null ? null : defaults.getSection(key);
				String checkingStr = entry.getValue().toString();
				if (checkingStr.contains(key + "=(this Map)") || checkingStr.contains(key + "=(this Collection)"))
					continue;
				this.self.put(key, new Configuration(file, (Map<?, ?>) entry.getValue(), nextDef == this ? null : nextDef)); // prevent infinite loop
			} else {
				this.self.put(key, entry.getValue());
			}
		}
	}

	private Configuration getSectionFor(String path) {
		int index = path.indexOf(46);
		if (index == -1) {
			return this;
		}
		String root = path.substring(0, index);
		Object section = this.self.get(root);
		if (section == null) {
			section = new Configuration((this.defaults == null) ? null : this.defaults.getSection(root));
			this.self.put(root, section);
		}
		try {
			return (Configuration) section;
		} catch (ClassCastException e) {
			Adapter.getAdapter().getLogger()
					.warn("[Yaml-Configuration] Failed to cast " + section + " into Configuration while trying to get " + path + " in " + file.getAbsolutePath() + ".");
			return this;
		}
	}

	private String getChild(String path) {
		int index = path.indexOf(46);
		return (index == -1) ? path : path.substring(index + 1);
	}

	public <T> T get(String path, T def) {
		Configuration section = this.getSectionFor(path);
		Object val;
		if (section == this) {
			val = this.self.get(path);
		} else {
			val = section.get(this.getChild(path), (Object) def);
		}
		if (val == null && def instanceof Configuration) {
			this.self.put(path, def);
		}
		return (T) ((val != null) ? val : def);
	}

	public boolean contains(String path) {
		return this.get(path, (Object) null) != null;
	}

	public Object get(String path) {
		return this.get(path, this.getDefault(path));
	}

	public Object getDefault(String path) {
		return (this.defaults == null) ? null : this.defaults.get(path);
	}

	public void set(String path, Object value) {
		if (value instanceof Map) {
			value = new Configuration(file, (Map<?, ?>) value, (this.defaults == null) ? null : this.defaults.getSection(path));
		}
		Configuration section = this.getSectionFor(path);
		if (section == this) {
			if (value == null) {
				this.self.remove(path);
			} else if(value != this) {
				this.self.put(path, value);
			}
		} else {
			section.set(this.getChild(path), value);
		}
	}

	public Configuration createSection(String path) {
		Configuration conf = getSection(path);
		if (conf == null)
			set(path, conf = new Configuration());
		return conf;
	}

	public boolean isSection(String path) {
		return contains(path) && getSectionFor(path) != null;
	}

	public Configuration getSection(String path) {
		Object def = this.getDefault(path);
		return (Configuration) this.get(path, (def instanceof Configuration) ? def : new Configuration((this.defaults == null) ? null : this.defaults.getSection(path)));
	}

	public Collection<String> getKeys() {
		return new LinkedHashSet<String>(this.self.keySet());
	}

	public byte getByte(String path) {
		Object def = this.getDefault(path);
		return this.getByte(path, (def instanceof Number) ? ((Number) def).byteValue() : 0);
	}

	public byte getByte(String path, byte def) {
		Object val = this.get(path, def);
		return (val instanceof Number) ? ((Number) val).byteValue() : def;
	}

	public List<Byte> getByteList(String path) {
		List<?> list = this.getList(path);
		List<Byte> result = new ArrayList<Byte>();
		for (Object object : list) {
			if (object instanceof Number) {
				result.add(((Number) object).byteValue());
			}
		}
		return result;
	}

	public short getShort(String path) {
		Object def = this.getDefault(path);
		return this.getShort(path, (def instanceof Number) ? ((Number) def).shortValue() : 0);
	}

	public short getShort(String path, short def) {
		Object val = this.get(path, def);
		return (val instanceof Number) ? ((Number) val).shortValue() : def;
	}

	public List<Short> getShortList(String path) {
		List<?> list = this.getList(path);
		List<Short> result = new ArrayList<Short>();
		for (Object object : list) {
			if (object instanceof Number) {
				result.add(((Number) object).shortValue());
			}
		}
		return result;
	}

	public int getInt(String path) {
		Object def = this.getDefault(path);
		return this.getInt(path, (def instanceof Number) ? ((Number) def).intValue() : 0);
	}

	public int getInt(String path, int def) {
		Object val = this.get(path, def);
		return (val instanceof Number) ? ((Number) val).intValue() : def;
	}

	public List<Integer> getIntList(String path) {
		List<?> list = this.getList(path);
		List<Integer> result = new ArrayList<Integer>();
		for (Object object : list) {
			if (object instanceof Number) {
				result.add(((Number) object).intValue());
			}
		}
		return result;
	}

	public long getLong(String path) {
		Object def = this.getDefault(path);
		return this.getLong(path, (def instanceof Number) ? ((Number) def).longValue() : 0L);
	}

	public long getLong(String path, long def) {
		Object val = this.get(path, def);
		return (val instanceof Number) ? ((Number) val).longValue() : def;
	}

	public List<Long> getLongList(String path) {
		List<?> list = this.getList(path);
		List<Long> result = new ArrayList<Long>();
		for (Object object : list) {
			if (object instanceof Number) {
				result.add(((Number) object).longValue());
			}
		}
		return result;
	}

	public float getFloat(String path) {
		Object def = this.getDefault(path);
		return this.getFloat(path, (def instanceof Number) ? ((Number) def).floatValue() : 0.0f);
	}

	public float getFloat(String path, float def) {
		Object val = this.get(path, def);
		return (val instanceof Number) ? ((Number) val).floatValue() : def;
	}

	public List<Float> getFloatList(String path) {
		List<?> list = this.getList(path);
		List<Float> result = new ArrayList<Float>();
		for (Object object : list) {
			if (object instanceof Number) {
				result.add(((Number) object).floatValue());
			}
		}
		return result;
	}

	public double getDouble(String path) {
		Object def = this.getDefault(path);
		return this.getDouble(path, (def instanceof Number) ? ((Number) def).doubleValue() : 0.0);
	}

	public double getDouble(String path, double def) {
		Object val = this.get(path, def);
		return (val instanceof Number) ? ((Number) val).doubleValue() : def;
	}

	public List<Double> getDoubleList(String path) {
		List<?> list = this.getList(path);
		List<Double> result = new ArrayList<Double>();
		for (Object object : list) {
			if (object instanceof Number) {
				result.add(((Number) object).doubleValue());
			}
		}
		return result;
	}

	public boolean getBoolean(String path) {
		Object def = this.getDefault(path);
		return this.getBoolean(path, def instanceof Boolean && (boolean) def);
	}

	public boolean getBoolean(String path, boolean def) {
		Object val = this.get(path, def);
		return (boolean) ((val instanceof Boolean) ? val : def);
	}

	public List<Boolean> getBooleanList(String path) {
		List<?> list = this.getList(path);
		List<Boolean> result = new ArrayList<Boolean>();
		for (Object object : list) {
			if (object instanceof Boolean) {
				result.add((Boolean) object);
			}
		}
		return result;
	}

	public char getChar(String path) {
		Object def = this.getDefault(path);
		return this.getChar(path, (def instanceof Character) ? ((char) def) : '\0');
	}

	public char getChar(String path, char def) {
		Object val = this.get(path, def);
		return (char) ((val instanceof Character) ? val : def);
	}

	public List<Character> getCharList(String path) {
		List<?> list = this.getList(path);
		List<Character> result = new ArrayList<Character>();
		for (Object object : list) {
			if (object instanceof Character) {
				result.add((Character) object);
			}
		}
		return result;
	}

	public String getString(String path) {
		Object def = this.getDefault(path);
		return this.getString(path, (def instanceof String) ? ((String) def) : "");
	}

	public String getString(String path, String def) {
		Object val = this.get(path, def);
		return (String) ((val instanceof String) ? val : def);
	}

	public List<String> getStringList(String path) {
		List<?> list = this.getList(path);
		List<String> result = new ArrayList<String>();
		for (Object object : list) {
			if (object instanceof String) {
				result.add((String) object);
			}
		}
		return result;
	}

	public List<?> getList(String path) {
		Object def = this.getDefault(path);
		return this.getList(path, (List<?>) ((def instanceof List) ? def : Collections.EMPTY_LIST));
	}

	public List<?> getList(String path, List<?> def) {
		Object val = this.get(path, def);
		return (List<?>) ((val instanceof List) ? ((List<?>) val) : def);
	}

	/**
	 * Save but thread-safely.
	 */
	public void save() {
		if (isSaving)
			return;
		isSaving = true;
		CompletableFuture.runAsync(this::directSave);
	}

	/**
	 * Directly save on the current thread
	 */
	public void directSave() {
		if (lastSaveAsked + 500 > System.currentTimeMillis())
			return;
		lastSaveAsked = System.currentTimeMillis();
		try {
			YamlConfiguration.save(this, file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		isSaving = false;
	}
}
