package com.elikill58.negativity.fabric.impl.inventory;

import static java.util.Objects.requireNonNull;

import java.util.List;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.elikill58.negativity.api.inventory.Inventory;
import com.elikill58.negativity.api.inventory.InventoryType;
import com.elikill58.negativity.api.inventory.NegativityHolder;
import com.elikill58.negativity.api.inventory.PlatformHolder;
import com.elikill58.negativity.api.item.ItemStack;
import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.fabric.bridge.NegativityHolderOwner;
import com.elikill58.negativity.fabric.impl.inventory.holders.FabricNegativityHolder;
import com.elikill58.negativity.fabric.impl.item.FabricItemStack;

import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerFactory;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

public class FabricInventory extends Inventory {

	@Nullable
	private ScreenHandler inv;
	private final int size;
	private final String name;
	private final NegativityHolder holder;
	private final ScreenHandlerType<? extends ScreenHandler> platformType;
	private final InventoryType type;
	
	@Nullable
	private List<net.minecraft.item.ItemStack> earlySlots;
	@Nullable
	private ScreenHandlerFactory factory;
	
	public FabricInventory(ScreenHandler inv) {
		this.inv = inv;
		this.name = null;
		
		if (inv instanceof NegativityHolderOwner holderOwner) {
			this.holder = holderOwner.negativity$getHolder();
		} else {
			this.holder = new FabricNegativityHolder(null);
		}
		
		ScreenHandlerType<? extends ScreenHandler> pType;
		try {
			pType = inv.getType();
		} catch (UnsupportedOperationException e) {
			pType = null;
		}
		this.platformType = pType;
		
		this.type = typeFromPlatform(this.platformType);
		this.size = inv.slots.size();
		this.earlySlots = null;
		this.factory = null;
	}

	public FabricInventory(String inventoryName, int size, NegativityHolder holder) {
		this.name = inventoryName;
		this.holder = holder;
		//this.inv = new NegativityScreenHandler(size, this.holder = new FabricNegativityHolder(holder));
		this.platformType = getPlatformTypeForSize(size);
		this.type = typeFromPlatform(this.platformType);
		this.size = size;
		this.earlySlots = DefaultedList.ofSize(size, net.minecraft.item.ItemStack.EMPTY);
		this.factory = new SimpleNamedScreenHandlerFactory((syncId, inv1, player) -> {
			this.inv = platformType.create(syncId, inv1);
			this.inv.updateSlotStacks(this.inv.nextRevision(), this.earlySlots, net.minecraft.item.ItemStack.EMPTY);
			((NegativityHolderOwner) this.inv).negativity$setHolder(this.holder);
			requireNonNull(this.earlySlots).clear();
			this.factory = null;
			this.earlySlots = null;
			return this.inv;
		}, Text.of(inventoryName));
	}
	
	@Override
	public InventoryType getType() {
		return this.type;
	}

	@Override
	public @Nullable ItemStack get(int slot) {
		net.minecraft.item.ItemStack item;
		if (this.factory != null) {
			item = requireNonNull(this.earlySlots).get(slot);
		} else {
			item = requireNonNull(inv).getSlot(slot).getStack();
		}
		
		if (item.isEmpty()) {
			return null;
		}
		
		return new FabricItemStack(item);
	}

	@Override
	public void set(int slot, ItemStack item) {
		if (this.factory != null) {
			requireNonNull(this.earlySlots).set(slot, (net.minecraft.item.ItemStack) item.getDefault());
			return;
		}
		
		requireNonNull(inv).getSlot(slot).setStack((net.minecraft.item.ItemStack) item.getDefault());
	}

	@Override
	public void remove(int slot) {
		if (this.factory != null) {
			requireNonNull(this.earlySlots).remove(slot);
			return;
		}
		
		requireNonNull(inv).getSlot(slot).setStack(new net.minecraft.item.ItemStack(Items.AIR));
	}

	@Override
	public void clear() {
		if (this.factory != null) {
			requireNonNull(this.earlySlots).clear();
			return;
		}
		
		requireNonNull(inv).slots.forEach(s -> s.setStack(null));
	}

	@Override
	public void addItem(ItemStack build) {
		if (this.factory != null) {
			List<net.minecraft.item.ItemStack> slotItems = requireNonNull(this.earlySlots);
			for (int i = 0; i < slotItems.size(); i++) {
				net.minecraft.item.ItemStack slotItem = slotItems.get(i);
				if (!slotItem.isEmpty()) {
					slotItems.set(i, (net.minecraft.item.ItemStack) build.getDefault());
					return;
				}
			}
			return;
		}
		
		for(Slot s : requireNonNull(inv).slots) {
			if(s.isEnabled() && !s.hasStack()) {
				s.setStack((net.minecraft.item.ItemStack) build.getDefault());
				return;
			}
		}
	}

	@Override
	public int getSize() {
		return this.size;
	}

	@Override
	public String getInventoryName() {
		return name;
	}

	@Override
	public @Nullable PlatformHolder getHolder() {
		return holder;
	}

	@Override
	public ScreenHandler getDefault() {
		return inv;
	}
	
	@Override
	public boolean contains(Material type) {
		Item i = (Item) type.getDefault();
		if (this.factory != null) {
			return requireNonNull(this.earlySlots).stream().anyMatch(item -> item.isOf(i));
		}
		return requireNonNull(inv).slots.stream().anyMatch(s -> s.hasStack() && s.getStack().isOf(i));
	}
	
	public @Nullable ScreenHandlerFactory getFactory() {
		return this.factory;
	}
	
	private static ScreenHandlerType<GenericContainerScreenHandler> getPlatformTypeForSize(int size) {
		return switch (size / 9) {
			case 1 -> ScreenHandlerType.GENERIC_9X1;
			case 2 -> ScreenHandlerType.GENERIC_9X2;
			case 3 -> ScreenHandlerType.GENERIC_9X3;
			case 4 -> ScreenHandlerType.GENERIC_9X4;
			case 5 -> ScreenHandlerType.GENERIC_9X5;
			case 6 -> ScreenHandlerType.GENERIC_9X6;
			default -> throw new IllegalArgumentException("No ScreenHandlerType for inventory size " + size);
		};
	}
	
	private static InventoryType typeFromPlatform(@Nullable ScreenHandlerType<? extends ScreenHandler> platformType) {
		if (platformType == null) {
			return InventoryType.UNKNOW;
		}
		
		Identifier typeIdentifier = Registry.SCREEN_HANDLER.getKey(platformType).map(RegistryKey::getValue).orElse(null);
		if (typeIdentifier == null || !typeIdentifier.getNamespace().equals(Identifier.DEFAULT_NAMESPACE)) {
			return InventoryType.UNKNOW;
		}
		
		if (typeIdentifier.getPath().startsWith("generic_9x")) {
			return InventoryType.CHEST;
		}
		
		return InventoryType.UNKNOW;
	}
}
