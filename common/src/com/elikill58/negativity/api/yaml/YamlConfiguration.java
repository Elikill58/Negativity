package com.elikill58.negativity.api.yaml;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.MarkedYAMLException;
import org.yaml.snakeyaml.reader.ReaderException;
import org.yaml.snakeyaml.representer.Representer;

public class YamlConfiguration {
	private static final Yaml yaml;
	
	static {
		DumperOptions options = new DumperOptions();
		options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
		yaml = new Yaml(new Representer(options) {
			{
				this.representers.put(Configuration.class, (data) -> represent(((Configuration) data).self));
			}
		}, options);
	}
	
	/*private static final Yaml yaml = new ThreadLocal<Yaml>() {
		@SuppressWarnings("deprecation")
		@Override
		protected Yaml initialValue() {
			final DumperOptions options = new DumperOptions();
			options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
			return new Yaml(new Constructor(), new Representer() {
				{
					this.representers.put(Configuration.class, (data) -> represent(((Configuration) data).self));
				}
			}, options);
		}
	};*/

	public static void save(Configuration config, File file) throws IOException {
		try (final FileWriter writer = new FileWriter(file)) {
			yaml.dump(config.self, writer);
		}
	}

	public static Configuration load(File file) {
		try {
			beSureItsGoodYaml(file);
			try (FileReader reader = new FileReader(file)) {
				return load(file, reader);
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Configuration load(File file, Reader reader) {
		Map<String, Object> map = yaml.loadAs(reader, LinkedHashMap.class);
		if (map == null) {
			map = new LinkedHashMap<String, Object>();
		}
		return new Configuration(file, map, null);
	}

	private static void beSureItsGoodYaml(File f) throws IOException {
		beSureItsGoodList(f, Files.readAllLines(f.toPath()));
	}

	private static void beSureItsGoodList(File f, List<String> lines) throws IOException {
		try {
			if(lines.size() > 0 && lines.get(0).startsWith("&id")) // remove ref
				Files.write(f.toPath(), new ArrayList<>(), StandardOpenOption.TRUNCATE_EXISTING);
			else
				yaml.loadAs(new StringReader(String.join("\n", lines)), LinkedHashMap.class);
		} catch (ReaderException | MarkedYAMLException e) { // clean bugged file
			Files.write(f.toPath(), new ArrayList<>(), StandardOpenOption.TRUNCATE_EXISTING);
		}
	}
}
