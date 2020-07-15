package com.elikill58.negativity.common;

public enum ChatColor {

	BLACK("&0"),
	DARK_BLUE("&1"),
	DARK_GREEN("&2"),
	DARK_AQUA("&3"),
	DARK_RED("&4"),
	DARK_PURPLE("&5"),
	GOLD("&6"),
	GRAY("&7"),
	DARK_GRAY("&8"),
	BLUE("&9"),
	GREEN("&a"),
	AQUA("&b"),
	RED("&c"),
	LIGHT_PURPLE("&d"),
	YELLOW("&e"),
	WHITE("&f"),
	MAGIC("&k"),
	BOLD("&l"),
	STRIKETHROUGH("&m"),
	UNDERLINE("&n"),
	ITALIC("&o"),
	RESET("&r");
	
	private final String name;

	private ChatColor(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return getName().replace('&', '§');
	}

	public static String translateAlternateColorCodes(char altColorChar, String textToTranslate) {
		char[] b = textToTranslate.toCharArray();
		for (int i = 0; i < b.length - 1; i++) {
			if ((b[i] == altColorChar) && ("0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(b[(i + 1)]) > -1)) {
				b[i] = '§';
				b[(i + 1)] = Character.toLowerCase(b[(i + 1)]);
			}
		}
		return new String(b);
	}
}
