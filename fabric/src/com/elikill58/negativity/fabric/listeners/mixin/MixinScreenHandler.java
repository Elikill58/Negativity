package com.elikill58.negativity.fabric.listeners.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import com.elikill58.negativity.api.events.EventManager;
import com.elikill58.negativity.api.events.inventory.InventoryAction;
import com.elikill58.negativity.api.events.inventory.InventoryClickEvent;
import com.elikill58.negativity.api.events.inventory.InventoryCloseEvent;
import com.elikill58.negativity.api.item.ItemStack;
import com.elikill58.negativity.fabric.impl.entity.FabricEntityManager;
import com.elikill58.negativity.fabric.impl.inventory.FabricInventory;
import com.elikill58.negativity.fabric.impl.item.FabricItemStack;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.collection.DefaultedList;

@Mixin(ScreenHandler.class)
public class MixinScreenHandler {
	
	@Shadow @Final public DefaultedList<Slot> slots;
	
	@Inject(at = @At(value = "INVOKE"), method = "onSlotClick(IILnet/minecraft/screen/slot/SlotActionType;Lnet/minecraft/entity/player/PlayerEntity;)V")
	public void onInvClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
		InventoryAction action = InventoryAction.valueOf(actionType.name());
		ItemStack item = new FabricItemStack(slots.get(slotIndex).getStack());
		InventoryClickEvent event = new InventoryClickEvent(FabricEntityManager.getPlayer((ServerPlayerEntity) player), action, slotIndex, item, new FabricInventory(player.currentScreenHandler));
		EventManager.callEvent(event);
		// TODO manage cancel
	}

	@Inject(at = @At(value = "INVOKE"), method = "close(Lnet/minecraft/entity/player/PlayerEntity;)V")
	public void onInventoryClose(PlayerEntity player) {
		EventManager.callEvent(new InventoryCloseEvent(FabricEntityManager.getPlayer((ServerPlayerEntity) player), new FabricInventory(player.currentScreenHandler)));
	}
}
