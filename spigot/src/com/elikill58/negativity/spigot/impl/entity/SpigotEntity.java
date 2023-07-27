package com.elikill58.negativity.spigot.impl.entity;

import com.elikill58.negativity.api.entity.AbstractEntity;
import com.elikill58.negativity.api.entity.BoundingBox;
import com.elikill58.negativity.api.entity.EntityType;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.location.Vector;
import com.elikill58.negativity.api.location.World;
import com.elikill58.negativity.spigot.impl.location.SpigotLocation;
import com.elikill58.negativity.spigot.nms.SpigotVersionAdapter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;

public class SpigotEntity<E extends Entity> extends AbstractEntity {

    protected final E entity;

    public SpigotEntity(E entity) {
        this.entity = entity;
    }

    @Override
    public boolean isOnGround() {
        return entity.isOnGround();
    }

    @Override
    public boolean isDead() {
        return entity.isDead();
    }

    @Override
    public boolean isOp() {
        return entity.isOp();
    }

    @Override
    public Location getLocation() {
        return SpigotLocation.toCommon(entity.getLocation());
    }

    @Override
    public World getWorld() {
        return World.getWorld(entity.getWorld().getName());
    }

    @Override
    public double getEyeHeight() {
        return entity.getHeight();
    }

    @Override
    public EntityType getType() {
        if (entity == null)
            return EntityType.UNKNOWN;
        String typeName = entity.getType().name().toUpperCase();
        if (typeName.contains("BOAT"))
            return EntityType.BOAT;
        return EntityType.get(typeName);
    }

    @Override
    public E getDefault() {
        return entity;
    }

    @Override
    public void sendMessage(String msg) {
        if (entity instanceof CommandSender)
            entity.sendMessage(msg);
    }

    @Override
    public String getName() {
        if (entity instanceof HumanEntity) // prevent 1.7 error
            return entity.getName();
        return entity.getName();
    }

    @Override
    public Location getEyeLocation() {
        if (entity instanceof LivingEntity) {
            return SpigotLocation.toCommon(((LivingEntity) entity).getEyeLocation());
        }
        return null;
    }

    @Override
    public Vector getRotation() {
        org.bukkit.util.Vector vec = entity.getLocation().getDirection();
        return new Vector(vec.getX(), vec.getY(), vec.getZ());
    }

    @Override
    public int getEntityId() {
        return entity.getEntityId();
    }

    @Override
    public boolean isSameId(com.elikill58.negativity.api.entity.Entity other) {
        return getEntityId() == other.getEntityId() || entity.getUniqueId().equals(((Entity) other.getDefault()).getUniqueId());
    }

    @Override
    public BoundingBox getBoundingBox() {
        return SpigotVersionAdapter.getVersionAdapter().getBoundingBox(entity);
    }
}
