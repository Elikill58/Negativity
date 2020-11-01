package com.elikill58.negativity.universal;

import java.util.List;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.item.ItemBuilder;
import com.elikill58.negativity.api.item.ItemStack;
import com.elikill58.negativity.api.yaml.config.Configuration;
import com.elikill58.negativity.universal.permissions.Perm;

public class Sanction {

	private final String key, name, type;
	private final int slot;
	private final String permission, command, message;
	private final List<String> lore;
	
	public Sanction(String key, Configuration config) {
		this.key = key;
		this.name = config.getString("name", key);
		this.type = config.getString("material");
		this.slot = config.getInt("slot", 0);
		this.permission = config.getString("permission");
		this.command = config.getString("command", "");
		this.message = config.getString("message", name);
		this.lore = config.getStringList("lore");
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
	
	public List<String> getLore() {
		return lore;
	}
	
	public int getSlot() {
		return slot;
	}
	
	public String getPermission() {
		return permission;
	}
	
	public boolean hasPermission(Player p) {
		return permission == null || Perm.hasPerm(p, permission);
	}
	
	public String getCommand() {
		return command;
	}
	
	public ItemStack getItem() {
		return ItemBuilder.Builder(type).displayName(name).lore(lore).build();
	}
	
	public static int getMaxSlot(List<Sanction> list) {
		return list.isEmpty() ? 9 : list.stream().sorted((s1, s2) -> s2.getSlot() - s1.getSlot()).findFirst().get().getSlot();
	}
}
