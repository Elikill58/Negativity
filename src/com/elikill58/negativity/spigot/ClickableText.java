package com.elikill58.negativity.spigot;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import com.elikill58.negativity.spigot.utils.PacketUtils;
import com.elikill58.negativity.spigot.utils.Utils;
import com.elikill58.negativity.universal.Version;

public class ClickableText {

	private List<MessageComponent> component = new ArrayList<>();

	public ClickableText addMessage(MessageComponent... comp) {
		for (MessageComponent c : comp)
			component.add(c);
		return this;
	}

	public ClickableText addClickableLink(String text, String link) {
		component.add(new MessageComponent(text, link, Action.OPEN_URL));
		return this;
	}

	public ClickableText addRunnableCommand(String text, String command) {
		component.add(new MessageComponent(text, command, Action.RUN_COMMAND));
		return this;
	}

	public ClickableText addChatSuggestion(String text, String suggestion) {
		component.add(new MessageComponent(text, suggestion, Action.SUGGEST_COMMAND));
		return this;
	}

	public ClickableText addHoverEvent(String text, String hover) {
		component.add(new MessageComponent(text, hover, Action.SHOW_TEXT));
		return this;
	}

	public ClickableText addRunnableHoverEvent(String text, String hover, String cmd) {
		component.add(new MessageComponent(text, hover, Action.SHOW_TEXT, cmd, Action.RUN_COMMAND));
		return this;
	}

	public ClickableText addOpenURLHoverEvent(String text, String hover, String url) {
		component.add(new MessageComponent(text, hover, Action.SHOW_TEXT, url, Action.OPEN_URL));
		return this;
	}

	public void sendToPlayer(Player player) {
		for (MessageComponent mc : component)
			mc.send(player);
	}

	public void sendToAllPlayers() {
		for (Player player : Utils.getOnlinePlayers())
			sendToPlayer(player);
	}

	static class MessageComponent {

		Action a, a2 = null;
		String data, data2 = null;
		String text;
		Object[] nmsChat, nmsChat2 = null;

		public MessageComponent(String text, String data, Action a) {
			this.a = a;
			this.text = text;
			this.data = data;
			try {
				Class<?> craftChatMessage = Class
						.forName("org.bukkit.craftbukkit." + Utils.VERSION + ".util.CraftChatMessage");
				nmsChat = (Object[]) craftChatMessage.getMethod("fromString", String.class).invoke(craftChatMessage,
						text);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public MessageComponent(String text, String data, Action a, String data2, Action a2) {
			this.a = a;
			this.text = text;
			this.data = data;
			try {
				Class<?> craftChatMessage = Class
						.forName("org.bukkit.craftbukkit." + Utils.VERSION + ".util.CraftChatMessage");
				nmsChat = (Object[]) craftChatMessage.getMethod("fromString", String.class).invoke(craftChatMessage,
						text);
			} catch (Exception e) {
				e.printStackTrace();
			}
			this.a2 = a2;
			this.data2 = data2;
			try {
				Class<?> craftChatMessage = Class
						.forName("org.bukkit.craftbukkit." + Utils.VERSION + ".util.CraftChatMessage");
				nmsChat2 = (Object[]) craftChatMessage.getMethod("fromString", String.class).invoke(craftChatMessage,
						text);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public void send(Player p) {
			try {
				if (Version.getVersion().isNewerOrEquals(Version.V1_13)) {
					ClickableText1_13.send(p, this);
				} else {
					for (Object obj : compile()) {
						Class<?> chatBaseComponent = Class
								.forName("net.minecraft.server." + Utils.VERSION + ".ChatBaseComponent");

						chatBaseComponent.getMethod("a", Iterable.class).invoke(chatBaseComponent, obj);
						PacketUtils.sendPacket(p, "PacketPlayOutChat", PacketUtils.getNmsClass("IChatBaseComponent"), obj);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public Object[] compile() {
			try {
				for (Object c : nmsChat) {
					Object chatModifier = c.getClass().getMethod("getChatModifier").invoke(c);
					if (a.isHover()) {
						Class<?> chatHoverable = Class
								.forName("net.minecraft.server." + Utils.VERSION + ".ChatHoverable");
						Method m = null;
						try {
							m = chatModifier.getClass().getMethod("setChatHoverable", chatHoverable);
						} catch (NoSuchMethodException e) {
							m = chatModifier.getClass().getMethod("a", chatHoverable);
						}
						Class<?> enumHover = null;
						if (Version.getVersion().equals(Version.V1_7))
							enumHover = Class.forName("net.minecraft.server." + Utils.VERSION + ".EnumHoverAction");
						else
							enumHover = a.getClazz().getDeclaredClasses()[0];
						Class<?> chatComponent = Class
								.forName("net.minecraft.server." + Utils.VERSION + ".ChatComponentText");
						Constructor<?> clickableConstructor = chatHoverable.getConstructor(enumHover,
								Class.forName("net.minecraft.server." + Utils.VERSION + ".IChatBaseComponent"));
						m.invoke(chatModifier,
								clickableConstructor.newInstance(enumHover.getField(a.name()).get(enumHover),
										chatComponent.getConstructor(String.class).newInstance(data)));
					} else {
						Class<?> chatClickable = Class
								.forName("net.minecraft.server." + Utils.VERSION + ".ChatClickable");
						Method setChatClickable = chatModifier.getClass().getMethod("setChatClickable", chatClickable);
						Class<?> enumClick = null;
						if (Version.getVersion().equals(Version.V1_7))
							enumClick = Class.forName("net.minecraft.server." + Utils.VERSION + ".EnumClickAction");
						else
							enumClick = a.getClazz().getDeclaredClasses()[0];
						Object obj = chatClickable.getConstructor(enumClick, String.class)
								.newInstance(enumClick.getDeclaredField(a.name()).get(enumClick), data);
						setChatClickable.invoke(chatModifier, obj);
					}
					if (a2 != null && data2 != null) {
						if (a2.isHover()) {
							Class<?> chatHoverable = Class
									.forName("net.minecraft.server." + Utils.VERSION + ".ChatHoverable");
							Method m = null;
							try {
								m = chatModifier.getClass().getMethod("setChatHoverable", chatHoverable);
							} catch (NoSuchMethodException e) {
								m = chatModifier.getClass().getMethod("a", chatHoverable);
							}
							Class<?> enumHover = null;
							if (Version.getVersion().equals(Version.V1_7))
								enumHover = Class.forName("net.minecraft.server." + Utils.VERSION + ".EnumHoverAction");
							else
								enumHover = a2.getClazz().getDeclaredClasses()[0];
							Class<?> chatComponent = Class
									.forName("net.minecraft.server." + Utils.VERSION + ".ChatComponentText");
							Constructor<?> clickableConstructor = chatHoverable.getConstructor(enumHover,
									Class.forName("net.minecraft.server." + Utils.VERSION + ".IChatBaseComponent"));
							m.invoke(chatModifier,
									clickableConstructor.newInstance(enumHover.getField(a2.name()).get(enumHover),
											chatComponent.getConstructor(String.class).newInstance(data2)));
						} else {
							Class<?> chatClickable = Class
									.forName("net.minecraft.server." + Utils.VERSION + ".ChatClickable");
							Method setChatClickable = chatModifier.getClass().getMethod("setChatClickable",
									chatClickable);
							Class<?> enumClick = null;
							if (Version.getVersion().equals(Version.V1_7))
								enumClick = Class.forName("net.minecraft.server." + Utils.VERSION + ".EnumClickAction");
							else
								enumClick = a2.getClazz().getDeclaredClasses()[0];
							Object obj = chatClickable.getConstructor(enumClick, String.class)
									.newInstance(enumClick.getDeclaredField(a2.name()).get(enumClick), data2);
							setChatClickable.invoke(chatModifier, obj);
						}
					}
				}
			} catch (Exception e) {
				SpigotNegativity.getInstance().getLogger().severe(
						"Error while making ClickableText: " + e.getMessage() + ", " + e.getLocalizedMessage() + ". Please report this to Elikill58.");
			}
			return nmsChat;
		}
	}

	public static enum Action {

		SHOW_TEXT(true), SHOW_ENTITY(true), SHOW_ACHIEVEMENT(true), SHOW_ITEM(true),

		OPEN_URL(false), OPEN_FILE(false), RUN_COMMAND(false), SUGGEST_COMMAND(false), CHANGE_PAGE(
				false), TWITCH_USER_INFO(false);

		boolean isHover;

		Action(boolean isHover) {
			this.isHover = isHover;
		}

		public boolean isHover() {
			return isHover;
		}

		public Class<?> getClazz() throws ClassNotFoundException {
			if (isHover)
				return Class.forName("net.minecraft.server." + Utils.VERSION + ".ChatHoverable");
			else
				return Class.forName("net.minecraft.server." + Utils.VERSION + ".ChatClickable");
		}
	}
}
