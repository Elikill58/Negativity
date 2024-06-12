package com.elikill58.negativity.minestom.nms;

import java.util.HashMap;

import net.minestom.server.network.packet.client.ClientPacket;
import net.minestom.server.network.packet.client.common.ClientKeepAlivePacket;
import net.minestom.server.network.packet.client.common.ClientPingRequestPacket;
import net.minestom.server.network.packet.client.common.ClientPluginMessagePacket;
import net.minestom.server.network.packet.client.common.ClientPongPacket;
import net.minestom.server.network.packet.client.common.ClientResourcePackStatusPacket;
import net.minestom.server.network.packet.client.common.ClientSettingsPacket;
import net.minestom.server.network.packet.client.play.*;

public class MinestomPlayPackets {

	private static HashMap<Class<? extends ClientPacket>, Integer> idPerPacket = new HashMap<>();
    private static int nextId = 0;
    private static int nextId() {
        return nextId++;
    }
    
	static {
		// same as https://github.com/Minestom/Minestom/blob/master/src/main/java/net/minestom/server/network/packet/client/ClientPacketsHandler.java

        register(nextId(), ClientTeleportConfirmPacket.class);
        register(nextId(), ClientQueryBlockNbtPacket.class);
        nextId(); // difficulty packet
        register(nextId(), ClientChatAckPacket.class);
        register(nextId(), ClientCommandChatPacket.class);
        register(nextId(), ClientChatMessagePacket.class);
        register(nextId(), ClientChatSessionUpdatePacket.class);
        register(nextId(), ClientChunkBatchReceivedPacket.class);
        register(nextId(), ClientStatusPacket.class);
        register(nextId(), ClientSettingsPacket.class);
        register(nextId(), ClientTabCompletePacket.class);
        register(nextId(), ClientConfigurationAckPacket.class);
        register(nextId(), ClientClickWindowButtonPacket.class);
        register(nextId(), ClientClickWindowPacket.class);
        register(nextId(), ClientCloseWindowPacket.class);
        register(nextId(), ClientWindowSlotStatePacket.class);
        register(nextId(), ClientPluginMessagePacket.class);
        register(nextId(), ClientEditBookPacket.class);
        register(nextId(), ClientQueryEntityNbtPacket.class);
        register(nextId(), ClientInteractEntityPacket.class);
        register(nextId(), ClientGenerateStructurePacket.class);
        register(nextId(), ClientKeepAlivePacket.class);
        nextId(); // lock difficulty
        register(nextId(), ClientPlayerPositionPacket.class);
        register(nextId(), ClientPlayerPositionAndRotationPacket.class);
        register(nextId(), ClientPlayerRotationPacket.class);
        register(nextId(), ClientPlayerPacket.class);
        register(nextId(), ClientVehicleMovePacket.class);
        register(nextId(), ClientSteerBoatPacket.class);
        register(nextId(), ClientPickItemPacket.class);
        register(nextId(), ClientPingRequestPacket.class);
        register(nextId(), ClientCraftRecipeRequest.class);
        register(nextId(), ClientPlayerAbilitiesPacket.class);
        register(nextId(), ClientPlayerDiggingPacket.class);
        register(nextId(), ClientEntityActionPacket.class);
        register(nextId(), ClientSteerVehiclePacket.class);
        register(nextId(), ClientPongPacket.class);
        register(nextId(), ClientSetRecipeBookStatePacket.class);
        register(nextId(), ClientSetDisplayedRecipePacket.class);
        register(nextId(), ClientNameItemPacket.class);
        register(nextId(), ClientResourcePackStatusPacket.class);
        register(nextId(), ClientAdvancementTabPacket.class);
        register(nextId(), ClientSelectTradePacket.class);
        register(nextId(), ClientSetBeaconEffectPacket.class);
        register(nextId(), ClientHeldItemChangePacket.class);
        register(nextId(), ClientUpdateCommandBlockPacket.class);
        register(nextId(), ClientUpdateCommandBlockMinecartPacket.class);
        register(nextId(), ClientCreativeInventoryActionPacket.class);
        nextId(); // Update Jigsaw Block
        register(nextId(), ClientUpdateStructureBlockPacket.class);
        register(nextId(), ClientUpdateSignPacket.class);
        register(nextId(), ClientAnimationPacket.class);
        register(nextId(), ClientSpectatePacket.class);
        register(nextId(), ClientPlayerBlockPlacementPacket.class);
        register(nextId(), ClientUseItemPacket.class);
	}
	
	private static void register(int id, Class<? extends ClientPacket> clazz) {
		idPerPacket.put(clazz, id);
	}
	
	public static Integer getPacketId(Class<? extends ClientPacket> clazz) {
		return idPerPacket.get(clazz);
	}
}
