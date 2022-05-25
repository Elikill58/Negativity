package com.elikill58.negativity.fabric.impl.inventory;

import com.elikill58.negativity.fabric.impl.inventory.holders.FabricNegativityHolder;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;

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
		this.enableSyncing();
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
	
	public NegativityScreenHandler open(ServerPlayerEntity p) {
		inv.onOpen(p);
		return this;
	}
}
