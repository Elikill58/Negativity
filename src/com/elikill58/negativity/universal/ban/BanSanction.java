package com.elikill58.negativity.universal.ban;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.item.ItemRegistrar;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.yaml.config.Configuration;
import com.elikill58.negativity.universal.permissions.Perm;

public class BanSanction {
	
	private final String key, name;
	private final Material type;
	private final int slot;
	private final String permission, command, message;
	
	public BanSanction(String key, Configuration config) {
		this.key = key;
		this.name = config.getString("name", key);
		this.type = ItemRegistrar.getInstance().get(config.getString("material"));
		this.slot = config.getInt("slot", 0);
		this.permission = config.getString("permission");
		this.command = config.getString("command");
		this.message = config.getString("message", name);
	}
	
	public String getKey() {
		return key;
	}
	
	public String getName() {
		return name;
	}
	
	public String getMessage() {
		return message;
	}
	
	public Material getType() {
		return type;
	}
	
	public int getSlot() {
		return slot;
	}
	
	public String getPermission() {
		return permission;
	}
	
	public boolean hasPermission(Player p) {
		return permission != null && Perm.hasPerm(p, permission);
	}
	
	public String getCommand() {
		return command;
	}
}
