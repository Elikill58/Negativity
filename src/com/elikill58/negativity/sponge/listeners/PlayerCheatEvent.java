package com.elikill58.negativity.sponge.listeners;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.entity.living.humanoid.player.TargetPlayerEvent;
import org.spongepowered.api.event.impl.AbstractEvent;

import com.elikill58.negativity.sponge.SpongeNegativity;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.ReportType;

public class PlayerCheatEvent extends AbstractEvent implements TargetPlayerEvent {

    private final ReportType reportType;
    private Player p;
    private Cheat c;
    private int relia;
    private final String hoverProof;
    private final int ping;

    public PlayerCheatEvent(ReportType reportType, Player p, Cheat c, int reliability, String hoverProof, int ping) {
        this.reportType = reportType;
        this.p = p;
        this.c = c;
        this.relia = reliability;
        this.hoverProof = hoverProof;
        this.ping = ping;
    }

    @Override
    public Cause getCause() {
        return Cause.builder().append(SpongeNegativity.INSTANCE).append(p).build(EventContext.empty());
    }

    @Override
    public Player getTargetEntity() {
        return this.p;
    }

    public ReportType getReportType() {
        return reportType;
    }

    public Cheat getCheat() {
        return c;
    }

    public int getReliability() {
        return relia;
    }

    public String getHoverProof() {
        return hoverProof;
    }

    public int getPing() {
        return ping;
    }

    public static class Alert extends PlayerCheatEvent implements Cancellable {

        private boolean hasRelia, alert;
    	private final int nbAlert;
    	private int nbConsole;
        private final String proof;

        @Deprecated
        public Alert(ReportType reportType, Player p, Cheat c, int reliability, boolean hasRelia, String hoverProof, int ping, String stats_send) {
        	this(reportType, p, c, reliability, hasRelia, ping, "", hoverProof);	
    	}
    	
    	public Alert(ReportType type, Player p, Cheat c, int reliability, boolean hasRelia, int ping, String proof, String hover_proof) {
    		this(type, p, c, reliability, hasRelia, ping, proof, hover_proof, 1);
    	}
    	
    	public Alert(ReportType type, Player p, Cheat c, int reliability, boolean hasRelia, int ping, String proof, String hover_proof, int nbAlert) {
    		this(type, p, c, reliability, hasRelia, ping, proof, hover_proof, nbAlert, 1);
    	}
        
        public Alert(ReportType reportType, Player p, Cheat c, int reliability, boolean hasRelia, int ping, String proof, String hoverProof, int nbAlert, int nbAlertConsole) {
            super(reportType, p, c, reliability, hoverProof, ping);
            this.hasRelia = hasRelia;
            this.alert = hasRelia;
            this.proof = proof;
            this.nbAlert = nbAlert;
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
    	
    	public String getProof() {
    		return proof;
    	}

        @Deprecated
        public String getStatsSend() {
            return "";
        }
    	
    	public int getNbAlert() {
    		return nbAlert;
    	}
    	
    	public String getAlertMessageKey() {
    		return (nbAlert > 1 ? "negativity.alert_multiple" : "negativity.alert");
    	}
    	
    	public int getNbAlertConsole() {
    		return nbConsole;
    	}
    	
    	public void clearNbAlertConsole() {
    		this.nbConsole = 0;
    	}
    }

    public static class Kick extends PlayerCheatEvent implements Cancellable {

        public Kick(ReportType reportType, Player p, Cheat c, int reliability, String hoverProof, int ping) {
            super(reportType, p, c, reliability, hoverProof, ping);
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

        public Bypass(ReportType reportType, Player p, Cheat c, int reliability, String hoverProof, int ping) {
            super(reportType, p, c, reliability, hoverProof, ping);
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
