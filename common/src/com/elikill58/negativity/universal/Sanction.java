package com.elikill58.negativity.universal;

import java.util.List;
import java.util.stream.Collectors;

import com.elikill58.negativity.api.entity.OfflinePlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.item.ItemBuilder;
import com.elikill58.negativity.api.item.ItemStack;
import com.elikill58.negativity.api.utils.Utils;
import com.elikill58.negativity.api.yaml.Configuration;
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
		this.command = config.getString("command");
		this.message = config.getString("message", name);
		this.lore = config.getStringList("lore"); 
		if(type == null)
			Adapter.getAdapter().getLogger().error("Failed to load sanction item " + key + ": No type specified.");
		else if(command == null)
			Adapter.getAdapter().getLogger().error("Failed to load sanction item " + key + ": No command specified.");
	}

	private String applyPlaceholders(String value, OfflinePlayer cible) {
		return value.replaceAll("%name%", cible.getName()).replaceAll("%reason%", name);
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

	/**
	 * Check if has permission
	 * 
	 * @param p the player that we are looking if has permission
	 * @return true if has permission
	 */
	public boolean hasPermission(Player p) {
		return permission == null || Perm.hasPerm(p, permission);
	}

	/**
	 * The command to run
	 * 
	 * @return the command to run
	 */
	public String getCommand() {
		return command;
	}

	/**
	 * Get the item of this sanction
	 * 
	 * @param cible the player which is actually sanctionned
	 * @return the item
	 */
	public ItemStack getItem(OfflinePlayer cible) {
		return ItemBuilder.Builder(type).displayName(Utils.coloredMessage(applyPlaceholders(getName(), cible)))
				.lore(getLore().stream().map((s) -> Utils.coloredMessage(applyPlaceholders(s, cible))).collect(Collectors.toList())).build();
	}

	/**
	 * Get max slot for the sanction list
	 * 
	 * @param list all sanctions that will be used
	 * @return the max slot
	 */
	public static int getMaxSlot(List<Sanction> list) {
		return list.isEmpty() ? 9
				: list.stream().sorted((s1, s2) -> s2.getSlot() - s1.getSlot()).findFirst().get().getSlot();
	}
}
