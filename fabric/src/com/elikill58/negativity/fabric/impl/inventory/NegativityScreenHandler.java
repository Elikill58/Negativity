package com.elikill58.negativity.fabric.impl.inventory;

import java.util.stream.Collectors;

import com.elikill58.negativity.fabric.impl.inventory.holders.FabricNegativityHolder;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;

public class NegativityScreenHandler extends ScreenHandler {
	
	private static int syncId = Integer.MIN_VALUE;
	
	private final FabricNegativityHolder context;
	private final int size;
	private final SimpleInventory inv;
	
	public NegativityScreenHandler(int size, FabricNegativityHolder context) {
		super(getTypeForRow(size / 9), syncId++);
		this.size = size;
		this.context = context;
		this.inv = new SimpleInventory(size);
	    for (int i = 0; i < size; i++)
	        addSlot(new Slot(inv, i, i / 9, i % 9));
	}
	
	public FabricNegativityHolder getContext() {
		return context;
	}
	
	private static ScreenHandlerType<GenericContainerScreenHandler> getTypeForRow(int row) {
		switch (row) {
		case 1:
			return ScreenHandlerType.GENERIC_9X1;
		case 2:
			return ScreenHandlerType.GENERIC_9X2;
		case 3:
			return ScreenHandlerType.GENERIC_9X3;
		case 4:
			return ScreenHandlerType.GENERIC_9X4;
		case 5:
			return ScreenHandlerType.GENERIC_9X5;
		}
		return ScreenHandlerType.GENERIC_9X6;
	}

	@Override
	public boolean canUse(PlayerEntity player) {
		return inv.canPlayerUse(player);
	}
	
	public int getSize() {
		return size;
	}
	
	@Override
	public String toString() {
		return "NegativityScreenHandler{size=" + size +  ",slots=" + slots.stream().map(Slot::getStack).map(net.minecraft.item.ItemStack::getItem).map(Item::toString).collect(Collectors.toList()) + "}";
	}
}
