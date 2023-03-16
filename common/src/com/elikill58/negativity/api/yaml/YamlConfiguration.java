package com.elikill58.negativity.api.yaml;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.MarkedYAMLException;
import org.yaml.snakeyaml.reader.ReaderException;
import org.yaml.snakeyaml.representer.Representer;

import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Tuple;

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
		Tuple<Integer, String> content = beSureItsGoodList(f, Files.readAllLines(f.toPath()), false);
		if(content != null && Adapter.getAdapter() != null) // should not appear but idk
			Adapter.getAdapter().getLogger().warn("Fixed file " + f.getName() + " by removing line " + content.getA() + ": " + content.getB());
	}

	private static Tuple<Integer, String> beSureItsGoodList(File f, List<String> lines, boolean changed) throws IOException {
		try {
			yaml.loadAs(new StringReader(String.join("\n", lines)), LinkedHashMap.class);
			if(changed)
				Files.write(f.toPath(), lines, StandardOpenOption.TRUNCATE_EXISTING);
		} catch (ReaderException e) {
			
		} catch (MarkedYAMLException e) {
			int line = e.getProblemMark().getLine();
			if (lines.size() > line) {
				String removedLine = lines.remove(line);
				beSureItsGoodList(f, lines, true);
				return new Tuple<>(line, removedLine);
			}
		}
		return null;
	}
}
