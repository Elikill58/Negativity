package com.elikill58.negativity.common.protocols.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.packets.packet.playin.NPacketPlayInFlying;
import com.elikill58.negativity.api.protocols.CheckData;

public class CriticalData extends CheckData {

	public List<TimedFlyingPacket> positions = new ArrayList<>(Arrays.asList(new TimedFlyingPacket(),
			new TimedFlyingPacket(), new TimedFlyingPacket(), new TimedFlyingPacket()));

	public CriticalData(NegativityPlayer np) {
		super(np);
	}

	public void add(NPacketPlayInFlying packet) {
		positions.add(new TimedFlyingPacket(System.currentTimeMillis(), packet));
		positions.remove(0);
	}
	
	public static class TimedFlyingPacket {
		
		private long time;
		private NPacketPlayInFlying flying;
		
		public TimedFlyingPacket() {
			this(System.currentTimeMillis(), new NPacketPlayInFlying());
		}
		
		public TimedFlyingPacket(long time, NPacketPlayInFlying flying) {
			this.time = time;
			this.flying = flying;
		}
		
		public NPacketPlayInFlying getFlying() {
			return flying;
		}
		
		public long getTime() {
			return time;
		}
		
		public double getX() {
			return flying == null ? 0 : flying.getX();
		}
		
		public double getY() {
			return flying == null ? 0 : flying.getY();
		}
		
		public double getZ() {
			return flying == null ? 0 : flying.getZ();
		}
		
		public boolean isGround() {
			return flying != null && flying.isGround;
		}
		
		@Override
		public String toString() {
			return "TimedPacket{time=" + getTime() + ",packet=" + getFlying() + "}";
		}
	}
}
