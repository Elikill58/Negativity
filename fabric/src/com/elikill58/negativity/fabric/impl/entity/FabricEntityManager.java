package com.elikill58.negativity.fabric.impl.entity;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.commands.CommandSender;
import com.elikill58.negativity.api.entity.Entity;

import net.minecraft.command.CommandSource;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class FabricEntityManager {

	public static com.elikill58.negativity.api.entity.@Nullable Player getPlayer(@Nullable ServerPlayerEntity p){
		return p == null ? null : NegativityPlayer.getNegativityPlayer(p.getUuid(), () -> new FabricPlayer(p)).getPlayer();
	}
	
	public static @Nullable Entity getEntity(net.minecraft.entity.@Nullable Entity e) {
		if(e == null)
			return null;
		if(e.getType().equals(EntityType.PLAYER))
			return getPlayer((ServerPlayerEntity) e);
		else if(e.getType().equals(EntityType.IRON_GOLEM))
			return new FabricIronGolem((IronGolemEntity) e);
		else if(e.getType().equals(EntityType.ARROW))
			return new FabricArrow((ArrowEntity) e);
		else if(e.getType().equals(EntityType.POTION))
			return new FabricSplashPotion((PotionEntity) e);
		else
			return new FabricEntity<>(e);
	}

	public static @Nullable CommandSender getExecutor(@Nullable CommandSource src) {
		if(src == null) {
			return null;
		}
		
		if (src instanceof ServerPlayerEntity) {
			return new FabricPlayer((ServerPlayerEntity) src);
		} else if (src instanceof ServerCommandSource serverSource && serverSource.getEntity() instanceof ServerPlayerEntity player) {
			return new FabricPlayer(player);
		}
		
		return new FabricCommandSender((ServerCommandSource) src);
	}

	public static @Nullable Entity getProjectile(net.minecraft.entity.@Nullable Entity shooter) {
		if(shooter == null)
			return null;
		if(shooter instanceof ServerPlayerEntity)
			return getPlayer((ServerPlayerEntity) shooter);
		else if(shooter instanceof net.minecraft.entity.Entity)
			return new FabricEntity<>((net.minecraft.entity.Entity) shooter);
		return null;
	}
}
