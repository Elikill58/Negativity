package com.elikill58.negativity.fabric.mixin;

import java.util.OptionalInt;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.EventManager;
import com.elikill58.negativity.api.events.player.PlayerDamagedByEntityEvent;
import com.elikill58.negativity.api.events.player.PlayerDeathEvent;
import com.elikill58.negativity.api.events.player.PlayerItemConsumeEvent;
import com.elikill58.negativity.api.events.player.PlayerRegainHealthEvent;
import com.elikill58.negativity.fabric.bridge.ServerPlayerEntityBridge;
import com.elikill58.negativity.fabric.impl.entity.FabricEntityManager;
import com.elikill58.negativity.fabric.impl.item.FabricItemStack;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

@Mixin(ServerPlayerEntity.class)
public abstract class MixinServerPlayerEntity extends PlayerEntity implements ServerPlayerEntityBridge {
	
	private boolean negativity$doNotSendScreenClosePacket;
	
	public MixinServerPlayerEntity() {
		super(null, null, 0f, null);
	}
	
	@Shadow public ServerPlayNetworkHandler networkHandler;
	
	@Shadow public abstract void closeScreenHandler();
	
	@Shadow public abstract OptionalInt openHandledScreen(@Nullable NamedScreenHandlerFactory factory);
	
	@Shadow public abstract void closeHandledScreen();
	
	public ServerPlayerEntity getPlayer() {
		return networkHandler.getPlayer();
	}
	
	@Override
	public void negativity$doNotSendScreenClosePacket(boolean flag) {
		this.negativity$doNotSendScreenClosePacket = flag;
	}
	
	@Inject(at = @At(value = "HEAD"), method = "onDeath(Lnet/minecraft/entity/damage/DamageSource;)V")
	private void onPlayerDeath(DamageSource source, CallbackInfo ci) {
		Player p = FabricEntityManager.getPlayer(getPlayer());
		EventManager.callEvent(new PlayerDeathEvent(p));
		NegativityPlayer.getNegativityPlayer(p).unfight();
	}
	
	@Redirect(method = "openHandledScreen", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;closeHandledScreen()V"))
	private void noCloseInventoryPacket(ServerPlayerEntity instance) {
		if (negativity$doNotSendScreenClosePacket) {
			this.closeScreenHandler();
		} else {
			closeHandledScreen();
		}
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		EventManager.callEvent(new PlayerDamagedByEntityEvent(FabricEntityManager.getPlayer(getPlayer()),
				FabricEntityManager.getEntity(source.getAttacker())));
		return super.damage(source, amount);
	}

	@Override
	public void heal(float amount) {
		PlayerRegainHealthEvent event = new PlayerRegainHealthEvent(FabricEntityManager.getPlayer(getPlayer()));
		EventManager.callEvent(event);
		if (!event.isCancelled())
			super.heal(amount);
	}

	@Override
	public void consumeItem() {
		if (!this.activeItemStack.isEmpty() && isUsingItem()) {
			PlayerItemConsumeEvent event = new PlayerItemConsumeEvent(FabricEntityManager.getPlayer(getPlayer()),
					new FabricItemStack(activeItemStack));
			EventManager.callEvent(event);
			if (!event.isCancelled())
				super.consumeItem();
		}
	}
}
