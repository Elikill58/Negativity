package com.elikill58.negativity.common.protocols.checkprocessor;

import java.util.ArrayList;
import java.util.List;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.packets.PacketReceiveEvent;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInPositionLook;
import com.elikill58.negativity.api.protocols.CheckProcessor;

/**
 * WARN: This class is NOT used, but only exist to show how to do.
 * 
 * @author Elikill58
 *
 */
public class SpiderExampleCheckProcessor implements CheckProcessor {

	private final NegativityPlayer np;
	public List<Double> lastY = new ArrayList<>();
	
	public SpiderExampleCheckProcessor(NegativityPlayer np) {
		this.np = np;
	}
	
	@Override
	public void begin() {
		lastY.clear(); // here you can reset data
	}
	
	@Override
	public void handlePacketReceived(PacketReceiveEvent e) {
		/**
		 * WARN: This is an example, and the check "same-y" with less check. This should NOT be used.
		 */
		if(!(e.getPacket().getPacketType().equals(PacketType.Client.POSITION_LOOK) || e.getPacket().getPacketType().equals(PacketType.Client.POSITION)))
			return; // if it's not moving
		Player p = e.getPlayer();
		NPacketPlayInPositionLook pos = (NPacketPlayInPositionLook) e.getPacket().getPacket();
		int amount = 0;
		Location from = p.getLocation(), to = pos.getLocation(p.getWorld());
		double y = to.getY() - from.getY();
		if (y <= 0.0 || y == 0.25 || y == 0.5) {
			lastY.clear();
		} else {
			int i = lastY.size() - 1;
			while (i > 0) {
				double value = lastY.get(i);
				if (value == y) {
					++amount;
					--i;
				} else {
					if (i == np.lastY.size() - 1) {
						lastY.clear();
						break;
					}
					for (int x = 0; x < i; x++) {
						lastY.remove(0);
					}
					break;
				}
			}
		}
		if (amount > 1) {
			// here we can alert by using Negativity#alertMod
		}
		lastY.add(y);
	}
}
