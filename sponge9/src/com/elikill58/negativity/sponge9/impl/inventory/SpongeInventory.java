package com.elikill58.negativity.sponge9.impl.inventory;

import java.util.UUID;
import java.util.function.Supplier;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.spongepowered.api.block.entity.BlockEntity;
import org.spongepowered.api.block.entity.EnderChest;
import org.spongepowered.api.block.entity.carrier.Barrel;
import org.spongepowered.api.block.entity.carrier.Beacon;
import org.spongepowered.api.block.entity.carrier.BrewingStand;
import org.spongepowered.api.block.entity.carrier.Dispenser;
import org.spongepowered.api.block.entity.carrier.Dropper;
import org.spongepowered.api.block.entity.carrier.Hopper;
import org.spongepowered.api.block.entity.carrier.ShulkerBox;
import org.spongepowered.api.block.entity.carrier.chest.Chest;
import org.spongepowered.api.block.entity.carrier.furnace.BlastFurnace;
import org.spongepowered.api.block.entity.carrier.furnace.Furnace;
import org.spongepowered.api.block.entity.carrier.furnace.Smoker;
import org.spongepowered.api.entity.vehicle.minecart.carrier.ChestMinecart;
import org.spongepowered.api.entity.vehicle.minecart.carrier.HopperMinecart;
import org.spongepowered.api.item.inventory.Carrier;
import org.spongepowered.api.item.inventory.Container;
import org.spongepowered.api.item.inventory.ContainerType;
import org.spongepowered.api.item.inventory.ContainerTypes;
import org.spongepowered.api.item.inventory.crafting.CraftingInventory;
import org.spongepowered.api.item.inventory.entity.UserInventory;
import org.spongepowered.api.item.inventory.type.BlockEntityInventory;
import org.spongepowered.api.item.inventory.type.CarriedInventory;
import org.spongepowered.api.item.inventory.type.ViewableInventory;
import org.spongepowered.api.item.merchant.Merchant;

import com.elikill58.negativity.api.inventory.Inventory;
import com.elikill58.negativity.api.inventory.InventoryType;
import com.elikill58.negativity.api.inventory.NegativityHolder;
import com.elikill58.negativity.api.inventory.PlatformHolder;
import com.elikill58.negativity.api.item.ItemStack;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.sponge9.SpongeNegativity;
import com.elikill58.negativity.sponge9.impl.item.SpongeItemStack;

public class SpongeInventory extends Inventory {
	
	public static final UUID NEGATIVITY_INV_ID = UUID.fromString("68f4d048-43cb-a15e-8a1b-2ce8d4a1baf5");
	
	private final org.spongepowered.api.item.inventory.Inventory inv;
	private final String inventoryName;
	private SpongeNegativityHolder holder;
	
	public SpongeInventory(Container container) {
		this.inv = container;
		this.inventoryName = "";
		if (container instanceof CarriedInventory) {
			Object carrier = ((CarriedInventory<?>) container).carrier().orElse(null);
			if (carrier instanceof SpongeNegativityHolder) {
				this.holder = (SpongeNegativityHolder) carrier;
			}
		}
	}
	
	public SpongeInventory(String inventoryName, int size, NegativityHolder holder) {
		this.holder = new SpongeNegativityHolder(holder);
		this.inv = ViewableInventory.builder()
			.type(containerTypeForSize(size))
			.completeStructure()
			.carrier(this.holder)
			.identity(NEGATIVITY_INV_ID)
			.plugin(SpongeNegativity.container())
			.build();
		this.inventoryName = inventoryName;
	}
	
	@Override
	public InventoryType getType() {
		if (this.inv instanceof BlockEntityInventory) {
			BlockEntity blockEntity = ((BlockEntityInventory<?>) this.inv).blockEntity().orElse(null);
			if (blockEntity instanceof Barrel) {
				return InventoryType.BARREL;
			} else if (blockEntity instanceof Beacon) {
				return InventoryType.BEACON;
			} else if (blockEntity instanceof BlastFurnace) {
				return InventoryType.BLAST_FURNACE;
			} else if (blockEntity instanceof BrewingStand) {
				return InventoryType.BREWING;
			} else if (blockEntity instanceof Chest) {
				return InventoryType.CHEST;
			} else if (blockEntity instanceof Dispenser) {
				return InventoryType.DISPENSER;
			} else if (blockEntity instanceof Dropper) {
				return InventoryType.DROPPER;
			} else if (blockEntity instanceof EnderChest) {
				return InventoryType.ENDER_CHEST;
			} else if (blockEntity instanceof Furnace) {
				return InventoryType.FURNACE;
			} else if (blockEntity instanceof Hopper) {
				return InventoryType.ENDER_CHEST;
			} else if (blockEntity instanceof Smoker) {
				return InventoryType.SMOKER;
			} else if (blockEntity instanceof ShulkerBox) {
				return InventoryType.SHULKER_BOX;
			}
		} else if (this.inv instanceof ViewableInventory) {
			return InventoryType.CHEST;
		} else if (this.inv instanceof CraftingInventory) {
			return InventoryType.CRAFTING;
		} else if (this.inv instanceof UserInventory) {
			return InventoryType.PLAYER;
		} else if (this.inv instanceof CarriedInventory) {
			Carrier carrier = ((CarriedInventory<?>) this.inv).carrier().orElse(null);
			if (carrier instanceof Merchant) {
				return InventoryType.MERCHANT;
			} else if (inv instanceof ChestMinecart) {
				return InventoryType.CHEST;
			} else if (inv instanceof HopperMinecart) {
				return InventoryType.HOPPER;
			}
		}
		return InventoryType.UNKNOW;
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
		return this.inventoryName;
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
				return ContainerTypes.GENERIC_9X1;
			case 2:
				return ContainerTypes.GENERIC_9X2;
			case 3:
				return ContainerTypes.GENERIC_9X3;
			case 4:
				return ContainerTypes.GENERIC_9X4;
			case 5:
				return ContainerTypes.GENERIC_9X5;
			case 6:
				return ContainerTypes.GENERIC_9X6;
			default:
				throw new IllegalArgumentException("Size (" + size + ") does not fit a generic ContainerType");
		}
	}

	@Override
	public boolean contains(Material type) {
		return inv.contains((org.spongepowered.api.item.ItemType) type.getDefault());
	}
}
