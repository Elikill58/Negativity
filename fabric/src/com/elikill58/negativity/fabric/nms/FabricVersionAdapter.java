package com.elikill58.negativity.fabric.nms;

import com.elikill58.negativity.api.packets.nms.VersionAdapter;

import net.minecraft.server.network.ServerPlayerEntity;

public abstract class FabricVersionAdapter extends VersionAdapter<ServerPlayerEntity> {
	
	public FabricVersionAdapter(String version) {
		super(version);
	}
	
	@Override
	public String getNameOfPacket(Object nms) {
		String packetClassName = nms.getClass().getName();
		return packetClassName.substring(packetClassName.lastIndexOf('.') + 1);
	}
	
	private static FabricVersionAdapter instance = new Fabric_1_19();
	
	public static FabricVersionAdapter getVersionAdapter() {
		return instance;
	}
}
