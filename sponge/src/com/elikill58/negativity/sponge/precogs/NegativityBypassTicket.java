package com.elikill58.negativity.sponge.precogs;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.spongepowered.api.entity.living.player.Player;

import com.elikill58.negativity.universal.Cheat;
import com.me4502.precogs.detection.DetectionType;
import com.me4502.precogs.service.BypassTicket;

public class NegativityBypassTicket implements BypassTicket {
	
	private static final HashMap<UUID, NegativityBypassTicket> BYPASS_TICKET = new HashMap<>();
	
	private Player p;
	private Object owner;
	private List<DetectionType> detections;
	private boolean closed = false;
	
	public NegativityBypassTicket(Player p, List<DetectionType> detections, Object owner) {
		this.p = p;
		this.detections = detections;
		this.owner = owner;
		BYPASS_TICKET.put(p.getUniqueId(), this);
	}
	
	@Override
	public void close() {
		if(closed)
			return;
		closed = true;
		detections.clear();
		BYPASS_TICKET.remove(p.getUniqueId());
	}

	@Override
	public List<DetectionType> getDetectionTypes() {
		return detections;
	}
	
	public boolean containsDetection(Cheat c) {
		for(DetectionType type : detections)
			if(type.getId().equalsIgnoreCase(c.getKey()) || type.getName().equalsIgnoreCase(c.getName()))
				return true;
		return false;
	}

	@Override
	public Player getPlayer() {
		return p;
	}

	@Override
	public boolean isClosed() {
		return closed;
	}
	
	public Object getOwner() {
		return owner;
	}
	
	public static boolean hasBypassTicket(Cheat c, Player p) {
		if(!NegativityBypassTicket.BYPASS_TICKET.containsKey(p.getUniqueId()))
			return false;
		return NegativityBypassTicket.BYPASS_TICKET.get(p.getUniqueId()).containsDetection(c);
	}
}
