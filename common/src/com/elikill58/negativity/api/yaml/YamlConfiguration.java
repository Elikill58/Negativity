package com.elikill58.negativity.api.yaml;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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
public class YamlConfiguration {
	private static final ThreadLocal<Yaml> yaml = new ThreadLocal<Yaml>() {
		@Override
		protected Yaml initialValue() {
			final Representer representer = new Representer() {
				{
					this.representers.put(Configuration.class, new Represent() {
						@Override
						public Node representData(final Object data) {
							return represent(((Configuration) data).self);
						}
					});
				}
			};
			final DumperOptions options = new DumperOptions();
			options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
			return new Yaml(new Constructor(), representer, options);
		}
	};

	public static void save(final Configuration config, final File file) throws IOException {
		try (final FileWriter writer = new FileWriter(file)) {
			save(config, writer);
		}
	}

	public static void save(final Configuration config, final Writer writer) {
		yaml.get().dump(config.self, writer);
	}

	public static Configuration load(final File file) {
		try {
			return load(file, (Configuration) null);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Configuration load(final File file, final Configuration defaults) throws IOException {
		try (final FileReader reader = new FileReader(file)) {
			return load(file, reader, defaults);
		}
	}

	public static Configuration load(final File file, final Reader reader) {
		return load(file, reader, null);
	}

	public static Configuration load(final File file, final Reader reader, final Configuration defaults) {
		Map<String, Object> map = yaml.get().loadAs(reader, LinkedHashMap.class);
		if (map == null) {
			map = new LinkedHashMap<String, Object>();
		}
		return new Configuration(file, map, defaults);
	}
}
