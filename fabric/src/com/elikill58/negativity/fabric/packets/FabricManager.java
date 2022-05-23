package com.elikill58.negativity.fabric.packets;

import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.events.packets.PacketEvent.PacketSourceType;
import com.elikill58.negativity.api.packets.AbstractPacket;
import com.elikill58.negativity.api.packets.packet.NPacket;
import com.elikill58.negativity.fabric.impl.entity.FabricEntityManager;
import com.elikill58.negativity.fabric.impl.packet.FabricPacketManager;
import com.elikill58.negativity.fabric.nms.FabricVersionAdapter;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking.PlayChannelHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class FabricManager extends FabricPacketManager implements PlayChannelHandler {

	private final Identifier identifier = new Identifier("negativity:channel");
	
	public FabricManager() {
		ServerPlayNetworking.registerGlobalReceiver(identifier, this);
	}
	
	public AbstractPacket onPacketSent(NPacket packet, Player sender, Object nmsPacket) {
		FabricPacket customPacket = new FabricPacket(packet, nmsPacket, sender);
		notifyHandlersSent(PacketSourceType.CUSTOM, customPacket);
		return customPacket;
	}

	public AbstractPacket onPacketReceive(NPacket packet, Player sender, Object nmsPacket) {
		FabricPacket customPacket = new FabricPacket(packet, nmsPacket, sender);
		notifyHandlersReceive(PacketSourceType.CUSTOM, customPacket);
		return customPacket;
	}

	@Override
	public void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler,
			PacketByteBuf buf, PacketSender responseSender) {
		FabricVersionAdapter ada = FabricVersionAdapter.getVersionAdapter();
		Packet<?> nmsPacket = responseSender.createPacket(identifier, buf);
		Player p = FabricEntityManager.getPlayer(player);
		onPacketReceive(ada.getPacket(p, nmsPacket), p, nmsPacket);
	}
}
