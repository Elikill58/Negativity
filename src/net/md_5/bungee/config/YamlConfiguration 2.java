// 
// Decompiled by Procyon v0.5.36
// 

package net.md_5.bungee.config;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.LinkedHashMap;
import java.util.Map;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.representer.Represent;
import org.yaml.snakeyaml.representer.Representer;

@SuppressWarnings("unchecked")
public class YamlConfiguration extends ConfigurationProvider
{
    private final ThreadLocal<Yaml> yaml;
    
    @Override
    public void save(final Configuration config, final File file) throws IOException {
        try (final FileWriter writer = new FileWriter(file)) {
            this.save(config, writer);
        }
    }
    
    @Override
    public void save(final Configuration config, final Writer writer) {
        this.yaml.get().dump(config.self, writer);
    }
    
    @Override
    public Configuration load(final File file) throws IOException {
        return this.load(file, null);
    }
    
    @Override
    public Configuration load(final File file, final Configuration defaults) throws IOException {
        try (final FileReader reader = new FileReader(file)) {
            return this.load(reader, defaults);
        }
    }
    
    @Override
    public Configuration load(final Reader reader) {
        return this.load(reader, null);
    }
    
    @Override
    public Configuration load(final Reader reader, final Configuration defaults) {
        Map<String, Object> map = this.yaml.get().loadAs(reader, LinkedHashMap.class);
        if (map == null) {
            map = new LinkedHashMap<String, Object>();
        }
        return new Configuration(map, defaults);
    }
    
    @Override
    public Configuration load(final InputStream is) {
        return this.load(is, null);
    }
    
    @Override
    public Configuration load(final InputStream is, final Configuration defaults) {
		Map<String, Object> map = this.yaml.get().loadAs(is, LinkedHashMap.class);
        if (map == null) {
            map = new LinkedHashMap<String, Object>();
        }
        return new Configuration(map, defaults);
    }
    
    @Override
    public Configuration load(final String string) {
        return this.load(string, null);
    }
    
    @Override
    public Configuration load(final String string, final Configuration defaults) {
        Map<String, Object> map = this.yaml.get().loadAs(string, LinkedHashMap.class);
        if (map == null) {
            map = new LinkedHashMap<String, Object>();
        }
        return new Configuration(map, defaults);
    }
    
    YamlConfiguration() {
        this.yaml = new ThreadLocal<Yaml>() {
            @Override
            protected Yaml initialValue() {
                final Representer representer = new Representer() {
                    {
                        this.representers.put(Configuration.class, new Represent() {
                            @Override
                            public Node representData(final Object data) {
                                return represent(((Configuration)data).self);
                            }
                        });
                    }
                };
                final DumperOptions options = new DumperOptions();
                options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
                return new Yaml(new Constructor(), representer, options);
            }
        };
    }
}
