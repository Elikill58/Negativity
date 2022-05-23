package com.elikill58.negativity.fabric.nms;

import com.elikill58.negativity.api.packets.nms.VersionAdapter;
import com.elikill58.negativity.api.packets.packet.NPacket;

import net.minecraft.server.network.ServerPlayerEntity;

public abstract class FabricVersionAdapter extends VersionAdapter<ServerPlayerEntity> {
	
	public FabricVersionAdapter(String version) {
		super(version);
	}
	
	@Override
	public NPacket getPacket(ServerPlayerEntity player, Object nms) {
		String packetClassName = nms.getClass().getName();
		String packetName = packetClassName.substring(packetClassName.lastIndexOf('.') + 1);
		return super.getPacket(player, nms, packetName);
	}
	
	private static FabricVersionAdapter instance = new Fabric_1_18_2();
	
	public static FabricVersionAdapter getVersionAdapter() {
		return instance;
	}
}
