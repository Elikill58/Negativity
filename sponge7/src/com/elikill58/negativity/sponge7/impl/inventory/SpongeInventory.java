package com.elikill58.negativity.sponge7.impl.inventory;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.Container;
import org.spongepowered.api.item.inventory.property.InventoryDimension;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;
import org.spongepowered.api.item.inventory.type.CarriedInventory;
import org.spongepowered.api.text.Text;

import com.elikill58.negativity.api.inventory.Inventory;
import com.elikill58.negativity.api.inventory.InventoryType;
import com.elikill58.negativity.api.inventory.NegativityHolder;
import com.elikill58.negativity.api.inventory.PlatformHolder;
import com.elikill58.negativity.api.item.ItemStack;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.sponge7.Inv;
import com.elikill58.negativity.sponge7.SpongeNegativity;
import com.elikill58.negativity.sponge7.impl.item.SpongeItemStack;

public class SpongeInventory extends Inventory {

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
		int nbLine = size / 9;
		this.holder = new SpongeNegativityHolder(holder);
		this.inv = org.spongepowered.api.item.inventory.Inventory.builder().withCarrier(this.holder)
			.property(InventoryTitle.PROPERTY_NAME, new InventoryTitle(Text.of(inventoryName)))
			.property(InventoryDimension.PROPERTY_NAME, new InventoryDimension(9, nbLine))
			.property(Inv.INV_ID_KEY, Inv.NEGATIVITY_INV_ID)
			.build(SpongeNegativity.INSTANCE);
	}

	private org.spongepowered.api.item.inventory.Inventory getSlot(int slot) {
		return this.inv.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotIndex.of(slot)));
	}
	
	@Override
	public InventoryType getType() {
		return InventoryType.get(inv.getArchetype().getId());
	}

	@Override
	public ItemStack get(int slot) {
		return getSlot(slot).peek().filter(itemStack -> !itemStack.isEmpty()).map(SpongeItemStack::new).orElse(null);
	}

	@Override
	public void set(int slot, ItemStack item) {
		getSlot(slot).set((org.spongepowered.api.item.inventory.ItemStack) item.getDefault());
	}

	@Override
	public void remove(int slot) {
		getSlot(slot).clear();
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
		return inv.getName().get();
	}

	@Override
	public @Nullable PlatformHolder getHolder() {
		return holder == null ? null : holder.getBasicHolder();
	}

	@Override
	public Object getDefault() {
		return inv;
	}
	
	@Override
	public boolean contains(Material type) {
		return inv.contains((ItemType) type.getDefault());
	}
}
