package com.elikill58.deps.md_5.config;

import java.util.HashMap;
import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;
import java.io.IOException;
import java.io.File;
import java.util.Map;

public abstract class ConfigurationProvider
{
    private static final Map<Class<? extends ConfigurationProvider>, ConfigurationProvider> providers;
    
    public static ConfigurationProvider getProvider(final Class<? extends ConfigurationProvider> provider) {
        return ConfigurationProvider.providers.get(provider);
    }
    
    public abstract void save(final Configuration p0, final File p1) throws IOException;
    
    public abstract void save(final Configuration p0, final Writer p1);
    
    public abstract Configuration load(final File p0) throws IOException;
    
    public abstract Configuration load(final File p0, final Configuration p1) throws IOException;
    
    public abstract Configuration load(final Reader p0);
    
    public abstract Configuration load(final Reader p0, final Configuration p1);
    
    public abstract Configuration load(final InputStream p0);
    
    public abstract Configuration load(final InputStream p0, final Configuration p1);
    
    public abstract Configuration load(final String p0);
    
    public abstract Configuration load(final String p0, final Configuration p1);
    
    static {
        (providers = new HashMap<Class<? extends ConfigurationProvider>, ConfigurationProvider>()).put(YamlConfiguration.class, new YamlConfiguration());
    }
}
