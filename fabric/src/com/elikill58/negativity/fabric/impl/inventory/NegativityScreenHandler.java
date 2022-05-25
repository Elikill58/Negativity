package com.elikill58.negativity.fabric.impl.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.SlotActionType;

public class NegativityScreenHandler extends ScreenHandler {
	
	private static int syncId = Integer.MIN_VALUE;
	
	private final ScreenHandlerContext context;
	
	public NegativityScreenHandler(SimpleInventory si, ScreenHandlerContext context) {
		super(getTypeForRow(si.size() / 9), syncId++);
		this.context = context;
	}
	
	public ScreenHandlerContext getContext() {
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
		return true;
	}
	
	@Override
	public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
		
	}
}
