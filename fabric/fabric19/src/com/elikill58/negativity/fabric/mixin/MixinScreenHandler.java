package com.elikill58.negativity.fabric.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.elikill58.negativity.api.events.EventManager;
import com.elikill58.negativity.api.events.inventory.InventoryAction;
import com.elikill58.negativity.api.events.inventory.InventoryClickEvent;
import com.elikill58.negativity.api.events.inventory.InventoryCloseEvent;
import com.elikill58.negativity.api.inventory.NegativityHolder;
import com.elikill58.negativity.api.item.ItemStack;
import com.elikill58.negativity.fabric.bridge.NegativityHolderOwner;
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
public abstract class MixinScreenHandler implements NegativityHolderOwner {
	
	@Shadow @Final public DefaultedList<Slot> slots;
	
	@Shadow public abstract net.minecraft.item.ItemStack getCursorStack();
	
	private NegativityHolder negativity$holder;
	
	@Inject(at = @At(value = "HEAD"), method = "onSlotClick(IILnet/minecraft/screen/slot/SlotActionType;Lnet/minecraft/entity/player/PlayerEntity;)V", cancellable = true)
	private void onInvClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci) {
		InventoryAction action = switch (actionType) {
			case PICKUP -> switch (button) {
				case 0 -> InventoryAction.LEFT;
				case 1 -> InventoryAction.RIGHT;
				default -> InventoryAction.UNKNOWN;
			};
			case QUICK_MOVE -> switch (button) {
				case 0 -> InventoryAction.LEFT_SHIFT;
				case 1 -> InventoryAction.RIGHT_SHIFT;
				default -> InventoryAction.UNKNOWN;
			};
			case SWAP -> InventoryAction.NUMBER;
			case CLONE -> InventoryAction.MIDDLE;
			case THROW -> InventoryAction.DROP;
			case QUICK_CRAFT -> InventoryAction.UNKNOWN;
			case PICKUP_ALL -> InventoryAction.UNKNOWN;
		};
		ItemStack item;
		if (slotIndex == ScreenHandler.EMPTY_SPACE_SLOT_INDEX) {
			item = new FabricItemStack(getCursorStack());
		} else {
			item = new FabricItemStack(slots.get(slotIndex).getStack());
		}
		InventoryClickEvent event = new InventoryClickEvent(FabricEntityManager.getPlayer((ServerPlayerEntity) player), action, slotIndex, item, new FabricInventory(player.currentScreenHandler));
		EventManager.callEvent(event);
		if (event.isCancelled()) {
			ci.cancel();
		}
	}

	@Inject(at = @At(value = "HEAD"), method = "close(Lnet/minecraft/entity/player/PlayerEntity;)V")
	private void onInventoryClose(PlayerEntity player, CallbackInfo ci) {
		EventManager.callEvent(new InventoryCloseEvent(FabricEntityManager.getPlayer((ServerPlayerEntity) player), new FabricInventory(player.currentScreenHandler)));
	}
	
	@Override
	public NegativityHolder negativity$getHolder() {
		return this.negativity$holder;
	}
	
	@Override
	public void negativity$setHolder(NegativityHolder holder) {
		this.negativity$holder = holder;
	}
}
