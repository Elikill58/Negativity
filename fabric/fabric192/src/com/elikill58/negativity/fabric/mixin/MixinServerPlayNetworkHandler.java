package com.elikill58.negativity.fabric.mixin;

import java.util.Set;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.elikill58.negativity.api.events.EventManager;
import com.elikill58.negativity.api.events.player.PlayerInteractEvent;
import com.elikill58.negativity.api.events.player.PlayerInteractEvent.Action;
import com.elikill58.negativity.api.events.player.PlayerTeleportEvent;
import com.elikill58.negativity.fabric.impl.entity.FabricEntityManager;
import com.elikill58.negativity.fabric.impl.location.FabricLocation;

import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class MixinServerPlayNetworkHandler {

	@Shadow public ServerPlayerEntity player;
	
	@Inject(at = @At(value = "HEAD"), method = "requestTeleport(DDDFFLjava/util/Set;)V")
	private void onTeleport(double x, double y, double z, float yaw, float pitch,
							Set<PlayerPositionLookS2CPacket.Flag> flags, CallbackInfo ci) {
		World w = player.getWorld();
		EventManager.callEvent(new PlayerTeleportEvent(FabricEntityManager.getPlayer(player),
				FabricLocation.toCommon(w, player.getPos()), FabricLocation.toCommon(w, x, y, z)));
	}

	@Inject(at = @At(value = "HEAD"), method = "onPlayerInteractItem(Lnet/minecraft/network/packet/c2s/play/PlayerInteractItemC2SPacket;)V", cancellable = true)
	private void onInteract(PlayerInteractItemC2SPacket packet, CallbackInfo ci) {
		PlayerInteractEvent ev = new PlayerInteractEvent(FabricEntityManager.getPlayer(player), Action.LEFT_CLICK_AIR);
		EventManager.callEvent(ev);
		if (ev.isCancelled()) {
			ci.cancel();
		}
	}
}
