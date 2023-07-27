package com.elikill58.negativity.spigot.impl.entity;

import com.elikill58.negativity.api.entity.AbstractEntity;
import com.elikill58.negativity.api.entity.BoundingBox;
import com.elikill58.negativity.api.entity.OfflinePlayer;

import java.util.UUID;

public class SpigotOfflinePlayer extends AbstractEntity implements OfflinePlayer {

    private final org.bukkit.OfflinePlayer op;

    public SpigotOfflinePlayer(org.bukkit.OfflinePlayer op) {
        this.op = op;
    }

    @Override
    public boolean isOp() {
        return op.isOp();
    }

    @Override
    public Object getDefault() {
        return op;
    }

    @Override
    public String getName() {
        return op.getName();
    }

    @Override
    public boolean isOnline() {
        return op.isOnline();
    }

    @Override
    public UUID getUniqueId() {
        return op.getUniqueId();
    }

    @Override
    public boolean hasPlayedBefore() {
        return op.hasPlayedBefore();
    }

    @Override
    public int getEntityId() {
        return op.getUniqueId().hashCode();
    }

    @Override
    public BoundingBox getBoundingBox() {
        return null;
    }
}
