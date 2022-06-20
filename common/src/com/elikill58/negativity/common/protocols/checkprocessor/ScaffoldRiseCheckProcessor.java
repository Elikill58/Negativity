package com.elikill58.negativity.common.protocols.checkprocessor;

import java.util.ArrayList;
import java.util.List;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.packets.PacketReceiveEvent;
import com.elikill58.negativity.api.inventory.PlayerInventory;
import com.elikill58.negativity.api.item.ItemStack;
import com.elikill58.negativity.api.location.Location;
import com.elikill58.negativity.api.packets.AbstractPacket;
import com.elikill58.negativity.api.packets.PacketType;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInBlockPlace;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInFlying;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInHeldItemSlot;
import com.elikill58.negativity.api.protocols.CheckProcessor;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Negativity;
import com.elikill58.negativity.universal.detections.Cheat;
import com.elikill58.negativity.universal.detections.Cheat.CheatHover;
import com.elikill58.negativity.universal.detections.keys.CheatKeys;
import com.elikill58.negativity.universal.report.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class ScaffoldRiseCheckProcessor implements CheckProcessor {

	private final Cheat c = Cheat.forKey(CheatKeys.SCAFFOLD);
	private final NegativityPlayer np;
	private long lastValid = 0, lastInvalid = 0;
	private double speed = 0.0;
	private int amountInvalid = 0;
	private RiseCheckState state = RiseCheckState.UNKNOW;
	public int amount = 0; // alert information
	public List<String> proofs = new ArrayList<>();
	private NPacketPlayInFlying lastFlying = null;

	public ScaffoldRiseCheckProcessor(NegativityPlayer np) {
		this.np = np;
	}

	@Override
	public void handlePacketReceived(PacketReceiveEvent e) {
		if(!np.hasDetectionActive(c) || !c.checkActive("rise-slot"))
			return;
		
		Player p = e.getPlayer();
		AbstractPacket pa = e.getPacket();
		if(pa.getPacketType().equals(PacketType.Client.HELD_ITEM_SLOT)) {
			NPacketPlayInHeldItemSlot held = (NPacketPlayInHeldItemSlot) pa.getPacket();
			int possibleSlot = -1;
			PlayerInventory inv = p.getInventory();
			ItemStack item = null;
			while(possibleSlot < inv.getSize() && ((item = inv.get(++possibleSlot)) == null || !item.getType().isSolid()));
			if(possibleSlot == held.slot) { // select first with block
				setState(amountInvalid > 0 ? RiseCheckState.SLOT_CHANGE_INVALID : RiseCheckState.SLOT_CHANGE_VALID);
			} else
				setState(RiseCheckState.SLOT_CHANGE_BASIC);
			Adapter.getAdapter().debug("Slots: " + held.slot + ", possible: " + possibleSlot + ", old: " + inv.getHeldItemSlot() + ", invalid: " + amountInvalid + ", times v/inv: " + (System.currentTimeMillis() - lastValid) + "/" + (System.currentTimeMillis() - lastInvalid));

			if(!proofs.isEmpty()) {
				String last = proofs.get(proofs.size() - 1);
				proofs.clear();
				proofs.add(last);
			}
			proofs.add(System.currentTimeMillis() + ": Change slot: " + held.slot + ", invalid: " + amountInvalid + ", times v/inv: " + (System.currentTimeMillis() - lastValid) + "/" + (System.currentTimeMillis() - lastInvalid));
			
			amount = 0;
			amountInvalid = 0;
		} else if(pa.getPacketType().isFlyingPacket()) {
			NPacketPlayInFlying flying = (NPacketPlayInFlying) pa.getPacket();
			if(lastFlying == null)
				lastFlying = flying; // prevent NPE
			if(!flying.hasLook) {
				if(flying.hasPos)
					lastFlying = flying;
				return;
			}
			speed = flying.hasPos && lastFlying.hasPos ? lastFlying.getLocation(p.getWorld()).distance(flying.getLocation(p.getWorld())) : speed;
			String sloc = ", dis: " + String.format("%.3f", speed) + ": " + amount + " (" + amountInvalid + "%), { \"yaw\": " + String.format("%.3f", flying.yaw).replace(",", ".") + ", \"pitch\": " + String.format("%.3f", flying.pitch).replace(",", ".") + ", \"sprint\": " + (p.isSprinting() ? 1 : 0) +  "}, ";
			if(flying.pitch > 65) { // looking strangely
				lastInvalid = System.currentTimeMillis();
				String proofLine = "Seems fucking strange: ";
				if(state == RiseCheckState.VALID) {
					proofLine = "Just became invalid: ";
				} else if(state == RiseCheckState.SLOT_CHANGE_INVALID || state == RiseCheckState.SLOT_CHANGE_VALID || state == RiseCheckState.INVALID) {
					if(amountInvalid % 10 == 0)
						proofLine = "Strange guy: ";
					else
						proofLine = "";
					amountInvalid++;
				}
				//Adapter.getAdapter().debug(proofLine + amountInvalid + sloc);
				proofs.add(proofLine + " { \"yaw\": " + String.format("%.3f", flying.yaw).replace(",", ".") + ", \"pitch\": " + String.format("%.3f", flying.pitch).replace(",", ".") + ", \"sprint\": " + (p.isSprinting() ? 1 : 0) + "} ");
				setState(RiseCheckState.INVALID);
				if (p.isSprinting())
					amount += amountInvalid;
				if ((System.currentTimeMillis() - lastValid) > 1000)
					amount += amountInvalid;
			} else {
				lastValid = System.currentTimeMillis();
				if(state == RiseCheckState.SLOT_CHANGE_INVALID) {
					Adapter.getAdapter().debug("CHEAT !!! " + amountInvalid + " / " + amount + sloc);
					proofs.add(System.currentTimeMillis() + ": Cheat ! Is that clear now ? " + sloc);
				} else if(state == RiseCheckState.INVALID) {
					proofs.add(System.currentTimeMillis() + ": Now valid, before wasn't: " + amountInvalid + sloc);
					Adapter.getAdapter().debug("Now valid, before wasn't: " + amountInvalid + sloc);
					amountInvalid = 0;
				}
				setState(RiseCheckState.VALID);
			}
			lastFlying = flying;
		} else if(pa.getPacketType().equals(PacketType.Client.BLOCK_PLACE)) {
			NPacketPlayInBlockPlace place = (NPacketPlayInBlockPlace) pa.getPacket();
			Location loc = p.getLocation();
			if(place.x == loc.getBlockX() && place.y == loc.getBlockY() - 1 && place.z == loc.getBlockZ()) {
				if(amountInvalid > 40 && speed >= p.getWalkSpeed()) // if lot of invalid changes and go fast
					Negativity.alertMod(ReportType.WARNING, p, c, UniversalUtils.parseInPorcent(amountInvalid), "rise-slot", proofs.toString(), new CheatHover.Literal("Pitch: " + lastFlying.pitch), amount);
				else
					Adapter.getAdapter().debug("Amount invalid: " + amountInvalid);
			}
		}
	}

	private void setState(RiseCheckState state) {
		if(this.state != state)
			Adapter.getAdapter().debug("new state: " + state.name() + " (old: " + this.state.name() + ")");
		this.state = state;
	}
	
	public static enum RiseCheckState {
		VALID, INVALID, SLOT_CHANGE_VALID, SLOT_CHANGE_INVALID, SLOT_CHANGE_BASIC, UNKNOW;
	}
}
