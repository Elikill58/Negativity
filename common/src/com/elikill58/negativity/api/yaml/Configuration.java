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

import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.file.FileSaverTimer;

@SuppressWarnings("unchecked")
public final class Configuration {
	
    //private static final char SEPARATOR = '.';
	protected final Map<String, Object> self;
	protected final Configuration defaults;
	protected final File file;
	protected boolean isSaving = false;
    
    public Configuration() {
        this(null);
    }
    
    public Configuration(final Configuration defaults) {
        this(null, new LinkedHashMap<Object, Object>(), defaults);
    }
    
    Configuration(final File file, final Map<?, ?> map, final Configuration defaults) {
    	this.file = file;
        this.self = new LinkedHashMap<String, Object>();
        this.defaults = defaults;
        for (final Map.Entry<?, ?> entry : map.entrySet()) {
            final String key = (entry.getKey() == null) ? "null" : entry.getKey().toString();
            if (entry.getValue() instanceof Map) {
                this.self.put(key, new Configuration(file, (Map<?, ?>) entry.getValue(), (defaults == null) ? null : defaults.getSection(key)));
            }
            else {
                this.self.put(key, entry.getValue());
            }
        }
    }
    
    private Configuration getSectionFor(final String path) {
        final int index = path.indexOf(46);
        if (index == -1) {
            return this;
        }
        final String root = path.substring(0, index);
        Object section = this.self.get(root);
        if (section == null) {
            section = new Configuration((this.defaults == null) ? null : this.defaults.getSection(root));
            this.self.put(root, section);
        }
        try {
        	return (Configuration)section;
        } catch (ClassCastException e) {
        	Adapter.getAdapter().getLogger().warn("[Yaml-Configuration] Failed to cast " + section + " into Configuration while trying to get " + path + " in " + file.getAbsolutePath() + ".");
        	return this;
		}
    }
    
    private String getChild(final String path) {
        final int index = path.indexOf(46);
        return (index == -1) ? path : path.substring(index + 1);
    }
    
	public <T> T get(final String path, final T def) {
        final Configuration section = this.getSectionFor(path);
        Object val;
        if (section == this) {
            val = this.self.get(path);
        }
        else {
            val = section.get(this.getChild(path), (Object)def);
        }
        if (val == null && def instanceof Configuration) {
            this.self.put(path, def);
        }
        return (T)((val != null) ? val : def);
    }
    
    public boolean contains(final String path) {
        return this.get(path, (Object)null) != null;
    }
    
    public Object get(final String path) {
        return this.get(path, this.getDefault(path));
    }
    
    public Object getDefault(final String path) {
        return (this.defaults == null) ? null : this.defaults.get(path);
    }
    
    public void set(final String path, Object value) {
        if (value instanceof Map) {
            value = new Configuration(file, (Map<?, ?>)value, (this.defaults == null) ? null : this.defaults.getSection(path));
        }
        final Configuration section = this.getSectionFor(path);
        if (section == this) {
            if (value == null) {
                this.self.remove(path);
            }
            else {
                this.self.put(path, value);
            }
        } else {
            section.set(this.getChild(path), value);
        }
    }
    
    public Configuration createSection(final String path) {
    	Configuration conf = getSection(path);
    	if(conf == null)
    		set(path, conf = new Configuration());
    	return conf;
    }
    
    public Configuration getSection(final String path) {
        final Object def = this.getDefault(path);
        return (Configuration) this.get(path, (def instanceof Configuration) ? def : new Configuration((this.defaults == null) ? null : this.defaults.getSection(path)));
    }
    
    public Collection<String> getKeys() {
        return new LinkedHashSet<String>(this.self.keySet());
    }
    
    public byte getByte(final String path) {
        final Object def = this.getDefault(path);
        return this.getByte(path, (def instanceof Number) ? ((Number)def).byteValue() : 0);
    }
    
    public byte getByte(final String path, final byte def) {
        final Object val = this.get(path, def);
        return (val instanceof Number) ? ((Number)val).byteValue() : def;
    }
    
    public List<Byte> getByteList(final String path) {
        final List<?> list = this.getList(path);
        final List<Byte> result = new ArrayList<Byte>();
        for (final Object object : list) {
            if (object instanceof Number) {
                result.add(((Number)object).byteValue());
            }
        }
        return result;
    }
    
    public short getShort(final String path) {
        final Object def = this.getDefault(path);
        return this.getShort(path, (def instanceof Number) ? ((Number)def).shortValue() : 0);
    }
    
    public short getShort(final String path, final short def) {
        final Object val = this.get(path, def);
        return (val instanceof Number) ? ((Number)val).shortValue() : def;
    }
    
    public List<Short> getShortList(final String path) {
        final List<?> list = this.getList(path);
        final List<Short> result = new ArrayList<Short>();
        for (final Object object : list) {
            if (object instanceof Number) {
                result.add(((Number)object).shortValue());
            }
        }
        return result;
    }
    
    public int getInt(final String path) {
        final Object def = this.getDefault(path);
        return this.getInt(path, (def instanceof Number) ? ((Number)def).intValue() : 0);
    }
    
    public int getInt(final String path, final int def) {
        final Object val = this.get(path, def);
        return (val instanceof Number) ? ((Number)val).intValue() : def;
    }
    
    public List<Integer> getIntList(final String path) {
        final List<?> list = this.getList(path);
        final List<Integer> result = new ArrayList<Integer>();
        for (final Object object : list) {
            if (object instanceof Number) {
                result.add(((Number)object).intValue());
            }
        }
        return result;
    }
    
    public long getLong(final String path) {
        final Object def = this.getDefault(path);
        return this.getLong(path, (def instanceof Number) ? ((Number)def).longValue() : 0L);
    }
    
    public long getLong(final String path, final long def) {
        final Object val = this.get(path, def);
        return (val instanceof Number) ? ((Number)val).longValue() : def;
    }
    
    public List<Long> getLongList(final String path) {
        final List<?> list = this.getList(path);
        final List<Long> result = new ArrayList<Long>();
        for (final Object object : list) {
            if (object instanceof Number) {
                result.add(((Number)object).longValue());
            }
        }
        return result;
    }
    
    public float getFloat(final String path) {
        final Object def = this.getDefault(path);
        return this.getFloat(path, (def instanceof Number) ? ((Number)def).floatValue() : 0.0f);
    }
    
    public float getFloat(final String path, final float def) {
        final Object val = this.get(path, def);
        return (val instanceof Number) ? ((Number)val).floatValue() : def;
    }
    
    public List<Float> getFloatList(final String path) {
        final List<?> list = this.getList(path);
        final List<Float> result = new ArrayList<Float>();
        for (final Object object : list) {
            if (object instanceof Number) {
                result.add(((Number)object).floatValue());
            }
        }
        return result;
    }
    
    public double getDouble(final String path) {
        final Object def = this.getDefault(path);
        return this.getDouble(path, (def instanceof Number) ? ((Number)def).doubleValue() : 0.0);
    }
    
    public double getDouble(final String path, final double def) {
        final Object val = this.get(path, def);
        return (val instanceof Number) ? ((Number)val).doubleValue() : def;
    }
    
    public List<Double> getDoubleList(final String path) {
        final List<?> list = this.getList(path);
        final List<Double> result = new ArrayList<Double>();
        for (final Object object : list) {
            if (object instanceof Number) {
                result.add(((Number)object).doubleValue());
            }
        }
        return result;
    }
    
    public boolean getBoolean(final String path) {
        final Object def = this.getDefault(path);
        return this.getBoolean(path, def instanceof Boolean && (boolean)def);
    }
    
    public boolean getBoolean(final String path, final boolean def) {
        final Object val = this.get(path, def);
        return (boolean)((val instanceof Boolean) ? val : def);
    }
    
    public List<Boolean> getBooleanList(final String path) {
        final List<?> list = this.getList(path);
        final List<Boolean> result = new ArrayList<Boolean>();
        for (final Object object : list) {
            if (object instanceof Boolean) {
                result.add((Boolean)object);
            }
        }
        return result;
    }
    
    public char getChar(final String path) {
        final Object def = this.getDefault(path);
        return this.getChar(path, (def instanceof Character) ? ((char)def) : '\0');
    }
    
    public char getChar(final String path, final char def) {
        final Object val = this.get(path, def);
        return (char)((val instanceof Character) ? val : def);
    }
    
    public List<Character> getCharList(final String path) {
        final List<?> list = this.getList(path);
        final List<Character> result = new ArrayList<Character>();
        for (final Object object : list) {
            if (object instanceof Character) {
                result.add((Character)object);
            }
        }
        return result;
    }
    
    public String getString(final String path) {
        final Object def = this.getDefault(path);
        return this.getString(path, (def instanceof String) ? ((String)def) : "");
    }
    
    public String getString(final String path, final String def) {
        final Object val = this.get(path, def);
        return (String)((val instanceof String) ? val : def);
    }
    
    public List<String> getStringList(final String path) {
        final List<?> list = this.getList(path);
        final List<String> result = new ArrayList<String>();
        for (final Object object : list) {
            if (object instanceof String) {
                result.add((String)object);
            }
        }
        return result;
    }
    
    public List<?> getList(final String path) {
        final Object def = this.getDefault(path);
        return this.getList(path, (List<?>)((def instanceof List) ? def : Collections.EMPTY_LIST));
    }
    
    public List<?> getList(final String path, final List<?> def) {
        final Object val = this.get(path, def);
        return (List<?>)((val instanceof List) ? ((List<?>)val) : def);
    }
    
    public void save() {
    	if(isSaving)
    		return;
    	isSaving = true;
    	FileSaverTimer.getInstance().addAction(timer -> {
        	try {
    			YamlConfiguration.save(this, file);
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
        	isSaving = false;
    		timer.removeActionRunning();
    	});
    }
}
