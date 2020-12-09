package com.elikill58.negativity.sponge8.impl.inventory;

import java.util.UUID;
import java.util.function.Supplier;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.item.inventory.Container;
import org.spongepowered.api.item.inventory.ContainerType;
import org.spongepowered.api.item.inventory.ContainerTypes;
import org.spongepowered.api.item.inventory.type.CarriedInventory;
import org.spongepowered.api.item.inventory.type.ViewableInventory;

import com.elikill58.negativity.api.inventory.Inventory;
import com.elikill58.negativity.api.inventory.InventoryType;
import com.elikill58.negativity.api.inventory.NegativityHolder;
import com.elikill58.negativity.api.inventory.PlatformHolder;
import com.elikill58.negativity.api.item.ItemStack;
import com.elikill58.negativity.sponge8.impl.item.SpongeItemStack;

public class SpongeInventory extends Inventory {
	
	public static final UUID NEGATIVITY_INV_ID = UUID.fromString("68f4d048-43cb-a15e-8a1b-2ce8d4a1baf5");
	
	private final org.spongepowered.api.item.inventory.Inventory inv;
	private SpongeNegativityHolder holder;
	
	public SpongeInventory(Container container) {
		this.inv = container;
		if (container instanceof CarriedInventory) {
			Object carrier = ((CarriedInventory<?>) container).getCarrier().orElse(null);
			if (carrier instanceof SpongeNegativityHolder) {
				this.holder = (SpongeNegativityHolder) carrier;
			}
		}
	}
	
	public SpongeInventory(String inventoryName, int size, NegativityHolder holder) {
		this.holder = new SpongeNegativityHolder(holder);
		// TODO set inventory name when possible
		this.inv = ViewableInventory.builder()
			.type(containerTypeForSize(size))
			.completeStructure()
			.carrier(this.holder)
			.identity(NEGATIVITY_INV_ID)
			.build();
	}
	
	@Override
	public InventoryType getType() {
		return InventoryType.UNKNOW; // TODO find how to implement
	}
	
	@Override
	public @Nullable ItemStack get(int slot) {
		return inv.peekAt(slot).filter(itemStack -> !itemStack.isEmpty()).map(SpongeItemStack::new).orElse(null);
	}
	
	@Override
	public void set(int slot, ItemStack item) {
		inv.set(slot, (org.spongepowered.api.item.inventory.ItemStack) item.getDefault());
	}
	
	@Override
	public void remove(int slot) {
		inv.set(slot, null);
	}
	
	@Override
	public void clear() {
		inv.clear();
	}
	
	@Override
	public void addItem(ItemStack build) {
		inv.offer((org.spongepowered.api.item.inventory.ItemStack) build.getDefault());
	}
	
	@Override
	public int getSize() {
		return inv.capacity();
	}
	
	@Override
	public String getInventoryName() {
		return ""; // TODO
	}
	
	@Override
	public @Nullable PlatformHolder getHolder() {
		return holder == null ? null : holder.getBasicHolder();
	}
	
	@Override
	public Object getDefault() {
		return inv;
	}

	private static Supplier<ContainerType> containerTypeForSize(int size) {
		switch (size / 9) {
			case 1:
				return ContainerTypes.GENERIC_9x1;
			case 2:
				return ContainerTypes.GENERIC_9x2;
			case 3:
				return ContainerTypes.GENERIC_9x3;
			case 4:
				return ContainerTypes.GENERIC_9x4;
			case 5:
				return ContainerTypes.GENERIC_9x5;
			case 6:
				return ContainerTypes.GENERIC_9x6;
			default:
				throw new IllegalArgumentException("Size (" + size + ") does not fit a generic ContainerType");
		}
	}
}
