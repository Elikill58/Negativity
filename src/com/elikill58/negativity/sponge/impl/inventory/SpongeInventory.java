package com.elikill58.negativity.sponge.impl.inventory;

import java.util.Optional;

import org.spongepowered.api.item.inventory.Carrier;
import org.spongepowered.api.item.inventory.Container;
import org.spongepowered.api.item.inventory.property.InventoryDimension;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;
import org.spongepowered.api.item.inventory.type.CarriedInventory;
import org.spongepowered.api.item.inventory.type.GridInventory;
import org.spongepowered.api.text.Text;

import com.elikill58.negativity.api.inventory.Inventory;
import com.elikill58.negativity.api.inventory.InventoryType;
import com.elikill58.negativity.api.inventory.NegativityHolder;
import com.elikill58.negativity.api.inventory.PlatformHolder;
import com.elikill58.negativity.api.item.ItemStack;
import com.elikill58.negativity.sponge.Inv;
import com.elikill58.negativity.sponge.SpongeNegativity;
import com.elikill58.negativity.sponge.impl.item.SpongeItemStack;

public class SpongeInventory extends Inventory {

	private final org.spongepowered.api.item.inventory.Inventory inv;
	private GridInventory invGrid;
	
	public SpongeInventory(Container container) {
		this.inv = container;
	}
	
	public SpongeInventory(String inventoryName, int size, NegativityHolder holder) {
		int nbLine = size / 9;
		this.inv = org.spongepowered.api.item.inventory.Inventory.builder().withCarrier(new SpongeNegativityHolder(holder))
			.property(InventoryTitle.PROPERTY_NAME, new InventoryTitle(Text.of(inventoryName)))
			.property(InventoryDimension.PROPERTY_NAME, new InventoryDimension(9, nbLine))
			.property(Inv.INV_ID_KEY, Inv.NEGATIVITY_INV_ID)
			.build(SpongeNegativity.INSTANCE);
		this.invGrid = inv.query(QueryOperationTypes.INVENTORY_TYPE.of(GridInventory.class));
	}
	
	@Override
	public InventoryType getType() {
		return InventoryType.valueOf(inv.getArchetype().getId().toUpperCase());
	}

	@Override
	public ItemStack get(int slot) {
		Optional<org.spongepowered.api.item.inventory.ItemStack> opt = this.inv.query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotIndex.of(slot))).peek();
		return opt.isPresent() ? new SpongeItemStack(opt.get()) : null;
	}

	@Override
	public void set(int slot, ItemStack item) {
		int y = (int) slot / 9;
		int x = slot - (y * 9);
		invGrid.set(x, y, (org.spongepowered.api.item.inventory.ItemStack) item.getDefault());
	}

	@Override
	public void remove(int slot) {
		// TODO Auto-generated method stub
	}

	@Override
	public void clear() {
		inv.forEach((i) -> i.set(null));
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

	@SuppressWarnings("unchecked")
	@Override
	public PlatformHolder getHolder() {
		return new SpongeInventoryHolder(inv instanceof CarriedInventory ? ((CarriedInventory<Carrier>) inv).getCarrier().orElse(null) : null);
	}

	@Override
	public Object getDefault() {
		return inv;
	}

}
