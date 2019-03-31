package com.elikill58.negativity.sponge.listeners;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.entity.living.humanoid.player.TargetPlayerEvent;
import org.spongepowered.api.event.impl.AbstractEvent;

import com.elikill58.negativity.sponge.SpongeNegativity;
import com.elikill58.negativity.universal.Cheat;

public class PlayerCheatEvent extends AbstractEvent implements TargetPlayerEvent {

    private Player p;
    private Cheat c;
    private int relia;

    public PlayerCheatEvent(Player p, Cheat c, int reliability) {
        this.p = p;
        this.c = c;
        this.relia = reliability;
    }

    @Override
    public Cause getCause() {
        return Cause.builder().append(SpongeNegativity.INSTANCE).append(p).build(EventContext.empty());
    }

    @Override
    public Player getTargetEntity() {
        return this.p;
    }

    public Cheat getCheat() {
        return c;
    }

    public int getReliability() {
        return relia;
    }

    public static class Alert extends PlayerCheatEvent implements Cancellable {

        private boolean hasRelia, alert;

        public Alert(Player p, Cheat c, int reliability, boolean hasRelia) {
            super(p, c, reliability);
            this.hasRelia = hasRelia;
            this.alert = hasRelia;
        }

        private boolean cancelled = false;
        @Override
        public boolean isCancelled() {
            return cancelled;
        }

        @Override
        public void setCancelled(boolean cancel) {
            this.cancelled = cancel;
        }

        public boolean hasManyReliability() {
            return hasRelia;
        }

        public void setAlert(boolean b) {
            alert = true;
        }

        public boolean isAlert() {
            return alert;
        }
    }

    public static class Kick extends PlayerCheatEvent implements Cancellable {

        public Kick(Player p, Cheat c, int reliability) {
            super(p, c, reliability);
        }

        private boolean cancelled = false;
        @Override
        public boolean isCancelled() {
            return cancelled;
        }

        @Override
        public void setCancelled(boolean cancel) {
            this.cancelled = cancel;
        }
    }
    
    public static class Bypass extends PlayerCheatEvent implements Cancellable {

        public Bypass(Player p, Cheat c, int reliability) {
            super(p, c, reliability);
        }

        private boolean cancelled = false;
        @Override
        public boolean isCancelled() {
            return cancelled;
        }

        @Override
        public void setCancelled(boolean cancel) {
            this.cancelled = cancel;
        }
    }
}
