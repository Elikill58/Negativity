package com.elikill58.negativity.fabric.impl.inventory;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.fabricmc.fabric.api.screenhandler.v1.FabricScreenHandlerFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerFactory;
import net.minecraft.text.Text;

public class NegativityScreenHandlerFactory implements NamedScreenHandlerFactory, FabricScreenHandlerFactory {
	
	private final ScreenHandlerFactory baseFactory;
	private final Text displayName;
	
	public NegativityScreenHandlerFactory(ScreenHandlerFactory baseFactory, Text displayName) {
		this.baseFactory = baseFactory;
		this.displayName = displayName;
	}
	
	@Override
	public Text getDisplayName() {
		return this.displayName;
	}
	
	@Nullable
	@Override
	public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
		return this.baseFactory.createMenu(syncId, inv, player);
	}
	
	@Override
	public boolean shouldCloseCurrentScreen() {
		return false;
	}
}
