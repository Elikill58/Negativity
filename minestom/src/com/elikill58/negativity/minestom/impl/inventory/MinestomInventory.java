package com.elikill58.negativity.minestom.impl.inventory;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;

import com.elikill58.negativity.api.inventory.Inventory;
import com.elikill58.negativity.api.inventory.InventoryType;
import com.elikill58.negativity.api.inventory.NegativityHolder;
import com.elikill58.negativity.api.inventory.PlatformHolder;
import com.elikill58.negativity.api.item.ItemStack;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.minestom.impl.item.MinestomItemStack;

public class MinestomInventory extends Inventory {

	private net.minestom.server.inventory.Inventory inv;
	private final NegativityHolder holder;
	
	public MinestomInventory(net.minestom.server.inventory.Inventory inv) {
		this.inv = inv;
		this.holder = HolderById.getHolder(inv.getWindowId());
	}

	public MinestomInventory(String inventoryName, int size, NegativityHolder holder) {
		net.minestom.server.inventory.InventoryType type = net.minestom.server.inventory.InventoryType.CHEST_6_ROW;
		if(size % 9 == 0)
			type = net.minestom.server.inventory.InventoryType.valueOf("CHEST_" + (size / 9) + "_ROW");
		this.inv = new net.minestom.server.inventory.Inventory(type, inventoryName);
		this.holder = holder;
		HolderById.add(inv.getWindowId(), holder);
	}
	
	@Override
	public InventoryType getType() {
		switch(inv.getInventoryType()) {
		case ANVIL:
			return InventoryType.ANVIL;
		case BEACON:
			return InventoryType.BEACON;
		case BLAST_FURNACE:
			return InventoryType.BLAST_FURNACE;
		case BREWING_STAND:
			return InventoryType.BREWING;
		case CARTOGRAPHY:
			return InventoryType.CARTOGRAPHY;
		case CHEST_1_ROW:
		case CHEST_2_ROW:
		case CHEST_3_ROW:
		case CHEST_4_ROW:
		case CHEST_5_ROW:
		case CHEST_6_ROW:
			return InventoryType.CHEST;
		case CRAFTING:
			return InventoryType.CRAFTING;
		case ENCHANTMENT:
			return InventoryType.ENCHANTING;
		case FURNACE:
			return InventoryType.FURNACE;
		case GRINDSTONE:
			return InventoryType.GRINDSTONE;
		case HOPPER:
			return InventoryType.HOPPER;
		case LECTERN:
			return InventoryType.LECTERN;
		case LOOM:
			return InventoryType.LOOM;
		case MERCHANT:
			return InventoryType.MERCHANT;
		case SHULKER_BOX:
			return InventoryType.SHULKER_BOX;
		case SMITHING:
			return InventoryType.SMITHING;
		case SMOKER:
			return InventoryType.SMOKER;
		case STONE_CUTTER:
			return InventoryType.STONECUTTER;
		case CRAFTER_3X3:
		case WINDOW_3X3:
			return InventoryType.PLAYER;
		}
		return InventoryType.CHEST;
	}

	@Override
	public ItemStack get(int slot) {
		return new MinestomItemStack(inv.getItemStack(slot));
	}

	@Override
	public void set(int slot, ItemStack item) {
		inv.setItemStack(slot, (net.minestom.server.item.ItemStack) item.getDefault());
	}

	@Override
	public void remove(int slot) {
		inv.setItemStack(slot, net.minestom.server.item.ItemStack.AIR);
	}

	@Override
	public void clear() {
		inv.clear();
	}

	@Override
	public void addItem(ItemStack build) {
		inv.addItemStack((net.minestom.server.item.@NotNull ItemStack) build.getDefault());
	}

	@Override
	public int getSize() {
		return inv.getSize();
	}

	@Override
	public String getInventoryName() {
		return inv.getTitle().examinableName();
	}

	@Override
	public @Nullable PlatformHolder getHolder() {
		return holder;
	}

	@Override
	public net.minestom.server.inventory.Inventory getDefault() {
		return inv;
	}
	
	@Override
	public boolean contains(Material type) {
		net.minestom.server.item.Material m = (net.minestom.server.item.Material) type.getDefault();
		for(net.minestom.server.item.ItemStack i : inv.getItemStacks()) {
			if(i.material().equals(m))
				return true;
		}
		return false;
	}
}
