package com.elikill58.negativity.universal.setBack.processor;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.location.Vector;
import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.setBack.SetBackEntry;
import com.elikill58.negativity.universal.setBack.SetBackProcessor;

public class TeleportProcessor implements SetBackProcessor {

	private Vector direction;
	
	public TeleportProcessor(SetBackEntry entry) {
		try {
			direction = TeleportDirection.valueOf(entry.getKey().toUpperCase()).getVector();
			if(direction == null) {
				String[] data = entry.getValue().split(",");
				direction = new Vector(Double.parseDouble(data[0]), Double.parseDouble(data[1]), Double.parseDouble(data[2]));
			} else {
				direction = direction.clone().multiply(Double.parseDouble(entry.getValue()));
			}
		} catch (Exception e) {
			Adapter.getAdapter().getLogger().error("Cannot load teleport set back processor " + entry.getKey());
			e.printStackTrace();
		}
	}
	
	@Override
	public String getName() {
		return "teleport";
	}

	@Override
	public void perform(Player p) {
		Adapter.getAdapter().runSync(() -> p.teleport(p.getLocation().clone().add(direction)));
	}
	
	public static enum TeleportDirection {
		BELOW(new Vector(0, -1, 0)),
		UP(new Vector(0, 1, 0)),
		CUSTOM(null);
		
		private final Vector vector;
		
		private TeleportDirection(Vector vector) {
			this.vector = vector;
		}

		public Vector getVector() {
			return vector;
		}
	}
}
