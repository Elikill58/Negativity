package com.elikill58.negativity.fabric.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.EventManager;
import com.elikill58.negativity.api.events.player.PlayerDamagedByEntityEvent;
import com.elikill58.negativity.api.events.player.PlayerDeathEvent;
import com.elikill58.negativity.api.events.player.PlayerItemConsumeEvent;
import com.elikill58.negativity.api.events.player.PlayerRegainHealthEvent;
import com.elikill58.negativity.fabric.impl.entity.FabricEntityManager;
import com.elikill58.negativity.fabric.impl.item.FabricItemStack;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

@Mixin(ServerPlayerEntity.class)
public abstract class MixinServerPlayerEntity extends PlayerEntity {
	
	public MixinServerPlayerEntity() {
		super(null, null, 0f, null);
	}
	
	@Shadow public ServerPlayNetworkHandler networkHandler;
	public ServerPlayerEntity getPlayer() {
		return networkHandler.getPlayer();
	}

	@Inject(at = @At(value = "HEAD"), method = "onDeath(Lnet/minecraft/entity/damage/DamageSource;)V")
	private void onPlayerDeath(DamageSource source, CallbackInfo ci) {
		Player p = FabricEntityManager.getPlayer(getPlayer());
		EventManager.callEvent(new PlayerDeathEvent(p));
		NegativityPlayer.getNegativityPlayer(p).unfight();
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
