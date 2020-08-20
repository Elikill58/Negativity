package com.elikill58.negativity.api.inventory;

public enum InventoryType {

	ANVIL,
	BARREL,
	BEACON,
	BLAST_FURNACE,
	BREWING,
	CARTOGRAPHY,
	CHEST,
	CRAFTING,
	CREATIVE,
	DISPENSER,
	DROPPER,
	ENCHANTING,
	ENDER_CHEST,
	FURNACE,
	GRINDSTONE,
	HOPPER,
	LECTERN,
	LOOM,
	MERCHANT,
	PLAYER,
	SMITHING,
	SMOKER,
	SHULKER_BOX,
	STONECUTTER,
	WORKBENCH,
	UNKNOW;
	
	public static InventoryType get(String name) {
		if(name == null)
			return UNKNOW;
		name = name.toUpperCase();
		for(InventoryType type : values())
			if(type.name().equalsIgnoreCase(name) || name.contains(type.name()))
				return type;
		return UNKNOW;
	}
}
