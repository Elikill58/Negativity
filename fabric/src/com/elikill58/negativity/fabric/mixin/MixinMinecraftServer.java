package com.elikill58.negativity.fabric.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.elikill58.negativity.fabric.FabricAdapter;
import com.elikill58.negativity.fabric.FabricScheduler;
import com.elikill58.negativity.universal.Adapter;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.profiler.Profiler;

@Mixin(MinecraftServer.class)
public class MixinMinecraftServer {
	
	@Shadow private int ticks;
	
	@Shadow private Profiler profiler;
	
	@Inject(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;tickWorlds(Ljava/util/function/BooleanSupplier;)V"))
	private void onTick(CallbackInfo ci) {
		if (FabricAdapter.getAdapter().getScheduler() instanceof FabricScheduler scheduler) {
			this.profiler.push("negativityScheduler");
			try {
				scheduler.tick(this.ticks);
			} catch (Exception e) {
				Adapter.getAdapter().getLogger().error("Error occurred when ticking scheduler: " + e.getMessage());
				e.printStackTrace();
			} finally {
				this.profiler.pop();
			}
		}
	}
}
