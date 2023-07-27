package com.elikill58.negativity.spigot;

import com.elikill58.negativity.spigot.utils.Utils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ClickableText {

    private final List<MessageComponent> component = new ArrayList<>();

    public ClickableText addMessage(MessageComponent... comp) {
        Collections.addAll(component, comp);
        return this;
    }

    public ClickableText addRunnableHoverEvent(String text, String hover, String cmd) {
        component.add(new MessageComponent(text, hover, Action.SHOW_TEXT, cmd, Action.RUN_COMMAND));
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

    public enum Action {

        SHOW_TEXT, SHOW_ENTITY, SHOW_ACHIEVEMENT, SHOW_ITEM,

        OPEN_URL, OPEN_FILE, RUN_COMMAND, SUGGEST_COMMAND, CHANGE_PAGE, TWITCH_USER_INFO

    }

    static class MessageComponent {

        Action action, secondAction = null;
        String data, secondData = null;
        String text;

        public MessageComponent(String text, String data, Action action) {
            this.text = text;
            this.action = action;
            this.data = data;
        }

        public MessageComponent(String text, String data, Action action, String secondData, Action secondAction) {
            this.text = text;
            this.action = action;
            this.data = data;
            this.secondAction = secondAction;
            this.secondData = secondData;
        }

        public void send(Player p) {
            TextComponent text = new TextComponent(this.text);
            if (action == Action.SHOW_TEXT)
                text.setHoverEvent(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT,
                        new ComponentBuilder(data).create()));
            else if (action == Action.SUGGEST_COMMAND)
                text.setClickEvent(new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.SUGGEST_COMMAND, data));
            else if (action == Action.OPEN_URL)
                text.setClickEvent(new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.OPEN_URL, data));
            else if (action == Action.RUN_COMMAND)
                text.setClickEvent(new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, data));

            if (secondAction == Action.OPEN_URL)
                text.setClickEvent(new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.OPEN_URL, secondData));
            else if (secondAction == Action.RUN_COMMAND)
                text.setClickEvent(new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, secondData));
            p.spigot().sendMessage(text);
        }
    }
}
