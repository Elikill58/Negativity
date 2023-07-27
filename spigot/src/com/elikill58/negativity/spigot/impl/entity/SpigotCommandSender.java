package com.elikill58.negativity.spigot.impl.entity;

import com.elikill58.negativity.api.commands.CommandSender;

public class SpigotCommandSender implements CommandSender {

    private final org.bukkit.command.CommandSender sender;

    public SpigotCommandSender(org.bukkit.command.CommandSender sender) {
        this.sender = sender;
    }

    @Override
    public void sendMessage(String msg) {
        sender.sendMessage(msg);
    }

    @Override
    public String getName() {
        return sender.getName();
    }

    @Override
    public Object getDefault() {
        return sender;
    }
}
