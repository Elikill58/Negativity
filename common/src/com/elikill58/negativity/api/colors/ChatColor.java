package com.elikill58.negativity.api.colors;

import java.util.regex.Pattern;

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

	ChatColor(String name) {
		this.name = name;
	}

	/**
	 * Get the color code like "&6"
	 * 
	 * @return the color code
	 */
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return color(getName());
	}

	public static final char COLOR_CHAR = '§';
	private static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)" + COLOR_CHAR + "[0-9A-FK-OR]");

	/**
	 * Color the given string with all "&X"
	 * 
	 * @param textToColor the text to color
	 * @return the colored text
	 */
	public static String color(String textToColor) {
		return translateAlternateColorCodes('&', textToColor);
	}

	/**
	 * Color the given string with given color char
	 * 
	 * @param altColorChar the color char. Generally '&'
	 * @param textToColor the text to color
	 * @return the colored text
	 */
	public static String translateAlternateColorCodes(char altColorChar, String textToColor) {
		if(textToColor == null)
			return null;
		char[] b = textToColor.toCharArray();
		for (int i = 0; i < b.length - 1; i++) {
			if ((b[i] == altColorChar) && ("0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(b[(i + 1)]) > -1)) {
				b[i] = COLOR_CHAR;
				b[(i + 1)] = Character.toLowerCase(b[(i + 1)]);
			}
		}
		return new String(b);
	}

	/**
	 * Remove all colors from the given string
	 * 
	 * @param input colored string
	 * @return same text without color
	 */
	public static String stripColor(String input) {
		if (input == null)
			return null;
		return STRIP_COLOR_PATTERN.matcher(input).replaceAll("");
	}
}
