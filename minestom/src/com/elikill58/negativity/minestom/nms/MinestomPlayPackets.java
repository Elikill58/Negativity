package com.elikill58.negativity.minestom.nms;

import java.util.HashMap;

import net.minestom.server.network.packet.client.ClientPacket;
import net.minestom.server.network.packet.client.play.*;

public class MinestomPlayPackets {

	private static HashMap<Class<? extends ClientPacket>, Integer> idPerPacket = new HashMap<>();
	
	static {
		// same as https://github.com/Minestom/Minestom/blob/master/src/main/java/net/minestom/server/network/packet/client/ClientPacketsHandler.java

        register(0x00, ClientTeleportConfirmPacket.class);
        register(0x01, ClientQueryBlockNbtPacket.class);
        // 0x02 difficulty packet
        register(0x03, ClientChatAckPacket.class);
        register(0x04, ClientCommandChatPacket.class);
        register(0x05, ClientChatMessagePacket.class);
        register(0x06, ClientChatPreviewPacket.class);
        register(0x07, ClientStatusPacket.class);
        register(0x08, ClientSettingsPacket.class);
        register(0x09, ClientTabCompletePacket.class);
        register(0x0A, ClientClickWindowButtonPacket.class);
        register(0x0B, ClientClickWindowPacket.class);
        register(0x0C, ClientCloseWindowPacket.class);
        register(0x0D, ClientPluginMessagePacket.class);
        register(0x0E, ClientEditBookPacket.class);
        register(0x0F, ClientQueryEntityNbtPacket.class);
        register(0x10, ClientInteractEntityPacket.class);
        register(0x11, ClientGenerateStructurePacket.class);
        register(0x12, ClientKeepAlivePacket.class);
        // 0x12 packet not used server-side
        register(0x14, ClientPlayerPositionPacket.class);
        register(0x15, ClientPlayerPositionAndRotationPacket.class);
        register(0x16, ClientPlayerRotationPacket.class);
        register(0x17, ClientPlayerPacket.class);
        register(0x18, ClientVehicleMovePacket.class);
        register(0x19, ClientSteerBoatPacket.class);
        register(0x1A, ClientPickItemPacket.class);
        register(0x1B, ClientCraftRecipeRequest.class);
        register(0x1C, ClientPlayerAbilitiesPacket.class);
        register(0x1D, ClientPlayerDiggingPacket.class);
        register(0x1E, ClientEntityActionPacket.class);
        register(0x1F, ClientSteerVehiclePacket.class);
        register(0x20, ClientPongPacket.class);
        register(0x21, ClientSetRecipeBookStatePacket.class);
        register(0x22, ClientSetDisplayedRecipePacket.class);
        register(0x23, ClientNameItemPacket.class);
        register(0x24, ClientResourcePackStatusPacket.class);
        register(0x25, ClientAdvancementTabPacket.class);
        register(0x26, ClientSelectTradePacket.class);
        register(0x27, ClientSetBeaconEffectPacket.class);
        register(0x28, ClientHeldItemChangePacket.class);
        register(0x29, ClientUpdateCommandBlockPacket.class);
        register(0x2A, ClientUpdateCommandBlockMinecartPacket.class);
        register(0x2B, ClientCreativeInventoryActionPacket.class);
        // 0x2B Update Jigsaw Block
        register(0x2D, ClientUpdateStructureBlockPacket.class);
        register(0x2E, ClientUpdateSignPacket.class);
        register(0x2F, ClientAnimationPacket.class);
        register(0x30, ClientSpectatePacket.class);
        register(0x31, ClientPlayerBlockPlacementPacket.class);
        register(0x32, ClientUseItemPacket.class);
	}
	
	private static void register(int id, Class<? extends ClientPacket> clazz) {
		idPerPacket.put(clazz, id);
	}
	
	public static Integer getPacketId(Class<? extends ClientPacket> clazz) {
		return idPerPacket.get(clazz);
	}
}
