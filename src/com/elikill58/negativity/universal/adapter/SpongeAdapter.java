package com.elikill58.negativity.universal.adapter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.util.TypeTokens;

import com.elikill58.negativity.sponge.utils.Cheat;
import com.elikill58.negativity.sponge.SpongeNegativity;
import com.elikill58.negativity.universal.AbstractCheat;
import com.elikill58.negativity.universal.DefaultConfigValue;
import com.elikill58.negativity.universal.TranslatedMessages;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;

public class SpongeAdapter extends Adapter {

	private ConfigurationNode config;
	private Logger logger;
	private SpongeNegativity pl;
	private final HashMap<String, ConfigurationNode> LANGS = new HashMap<>();

	public SpongeAdapter(SpongeNegativity sn) {
		this.pl = sn;
		this.logger = sn.getLogger();
		this.config = SpongeNegativity.getConfig();
	}

	@Override
	public Object getConfig() {
		return config;
	}

	@Override
	public File getDataFolder() {
		return pl.getDataFolder().toFile();
	}

	@Override
	public String getStringInConfig(String dir) {
		try {
			return getFinalNode(dir).getString();
		} catch (Exception e) {
			return DefaultConfigValue.getDefaultValueString(dir);
		}
	}

	@Override
	public boolean getBooleanInConfig(String dir) {
		try {
			return getFinalNode(dir).getBoolean();
		} catch (Exception e) {
			return DefaultConfigValue.getDefaultValueBoolean(dir);
		}
	}

	private ConfigurationNode getFinalNode(String dir) throws Exception {
		ConfigurationNode node = config;
		String[] parts = dir.split("\\.");
		for (String s : parts)
			node = node.getNode(s);
		return node;
	}
	
	private ConfigurationNode getFinalNode(String dir, ConfigurationNode localConf) throws Exception {
		ConfigurationNode node = localConf;
		String[] parts = dir.split("\\.");
		for (String s : parts)
			node = node.getNode(s);
		return node;
	}

	@Override
	public void log(String msg) {
		logger.info(msg);
	}

	@Override
	public void warn(String msg) {
		logger.warn(msg);
	}

	@Override
	public void error(String msg) {
		logger.error(msg);
	}

	@Override
	public HashMap<String, String> getKeysListInConfig(String dir) {
		final HashMap<String, String> hash = new HashMap<>();
		try {
			getFinalNode(dir).getChildrenMap().forEach((obj, cn) -> {
				hash.put(obj.toString(), cn.getString());
			});
		} catch (Exception e) {
		}
		return hash;
	}

	@Override
	public int getIntegerInConfig(String dir) {
		try {
			return getFinalNode(dir).getInt();
		} catch (Exception e) {
			return DefaultConfigValue.getDefaultValueInt(dir);
		}
	}

	@Override
	public void set(String dir, Object value) {
		try {
			getFinalNode(dir).setValue(value);
		} catch (Exception e) {
		}
	}

	@Override
	public double getDoubleInConfig(String dir) {
		try {
			return getFinalNode(dir).getDouble();
		} catch (Exception e) {
			return DefaultConfigValue.getDefaultValueDouble(dir);
		}
	}

	@Override
	public List<String> getStringListInConfig(String dir) {
		try {
			return getFinalNode(dir).getList(TypeTokens.STRING_TOKEN);
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}

	@Override
	public String getStringInOtherConfig(String fileDir, String valueDir, String fileName) {
		File f = new File(pl.getDataFolder().toAbsolutePath() + fileDir);
		if (!f.exists())
			copy(fileName, f);
		try {
			ConfigurationNode node = HoconConfigurationLoader.builder().setFile(f).build().load();
			String[] parts = valueDir.split("\\.");
			for (String s : parts)
				node = node.getNode(s);
			return node.getString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "Unknow";
	}

	@Override
	public File copy(String lang, File f) {
		String fileName = "en_US.yml";
		if (lang.equals("no_active"))
			fileName = "messages.yml";
		else if (lang.toLowerCase().contains("fr") || lang.toLowerCase().contains("be"))
			fileName = "fr_FR.yml";
		else if (lang.toLowerCase().contains("pt") || lang.toLowerCase().contains("br"))
			fileName = "pt_BR.yml";
		else if (lang.toLowerCase().contains("no"))
			fileName = "no_NO.yml";
		else if (lang.toLowerCase().contains("ru"))
			fileName = "ru_RU.yml";
		// TODO : Espagnol & Allemand
		SpongeNegativity.getInstance().getContainer().getAsset(fileName).ifPresent(asset -> {
			try {
				asset.copyToFile(f.toPath(), false);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		return f;
	}

	@Override
	public void loadLang() {
		try {
			LANGS.put("no_active",
					HoconConfigurationLoader.builder().setFile(copy("default",
							new File(pl.getDataFolder().toAbsolutePath() + "\\messages\\"
									+ Adapter.getAdapter().getStringInConfig("Translation.no_active_file_name"))))
							.build().load());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			File langDir = new File(SpongeNegativity.getInstance().getDataFolder() + "/messages/");
			if (!langDir.exists())
				langDir.mkdir();
			for (String l : TranslatedMessages.LANGS)
				LANGS.put(l, HoconConfigurationLoader.builder()
						.setFile(copy(l, new File(langDir.getAbsolutePath() + "\\" + l + ".yml"))).build().load());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getStringFromLang(String lang, String key) {
		try {
			String node = getFinalNode(key, LANGS.get(lang)).getString();
			return node == null ? DefaultConfigValue.STRINGS.get(key) : node;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	@Override
	public List<String> getStringListFromLang(String lang, String key) {
		try {
			return getFinalNode(key, LANGS.get(lang)).getList(TypeTokens.STRING_TOKEN);
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}
	
	

	/*private void load(String l, ConfigurationNode config) {
		config.getChildrenMap().forEach((objKey, cn) -> {
			String key = objKey.toString();
			Object obj = cn.getValue();
			if (obj instanceof ConfigurationNode) {
				load(l, cn.getNode());
			} else if (obj instanceof String) {
				HashMap<String, String> msg = LANG_MSG.containsKey(cn.getString()) ? LANG_MSG.get(cn.getString())
						: new HashMap<>();
				msg.put(key, config.getString(key));
				LANG_MSG.put(l, msg);
			} else if (obj instanceof List) {
				HashMap<String, List<String>> msg = LANG_MSG_LIST.containsKey(key) ? LANG_MSG_LIST.get(key)
						: new HashMap<>();
				try {
					msg.put(key, config.getList(TypeToken.of(String.class)));
				} catch (ObjectMappingException e) {
					e.printStackTrace();
				}
				LANG_MSG_LIST.put(l, msg);
			} else {
				System.out.println("[Negativity] Unknow type for " + obj.getClass() + " Lang: " + l);
			}
		});
	}*/
	
	@Override
	public List<AbstractCheat> getAbstractCheats() {
		List<AbstractCheat> c = new ArrayList<>();
		for(Cheat tempCheats : Cheat.values())
			c.add(tempCheats);
		return c;
	}
	
	@Override
	public void reload() {
		
	}
	
	@Override
	public Object getItem(String itemName) {
		Collection<ItemType> list = Sponge.getRegistry().getAllOf(ItemType.class);
		for(ItemType it : list)
			if(it.getName().equalsIgnoreCase(itemName))
				return it;
		return null;
	}
}
