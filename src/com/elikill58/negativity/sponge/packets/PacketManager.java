package com.elikill58.negativity.sponge.packets;

import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;

import com.elikill58.negativity.sponge.SpongeNegativityPlayer;

import eu.crushedpixel.sponge.packetgate.api.event.PacketEvent;
import eu.crushedpixel.sponge.packetgate.api.listener.PacketListenerAdapter;
import eu.crushedpixel.sponge.packetgate.api.registry.PacketConnection;

public class PacketManager  extends PacketListenerAdapter {

    @Override
    public void onPacketRead(PacketEvent event, PacketConnection connection) {
        String[] parts = event.getPacket().getClass().getName().split("\\.");
        String packetName = parts[parts.length - 1];
        Optional<Player> optionalPlayer = Sponge.getServer().getPlayer(connection.getPlayerUUID());
        if(!optionalPlayer.isPresent())
            return;
        Player p = optionalPlayer.get();
        SpongeNegativityPlayer np = SpongeNegativityPlayer.getNegativityPlayer(p);
        np.ALL++;
        if(packetName.equalsIgnoreCase("CPacketEntityAction"))
            np.ENTITY_ACTION++;
        else if(packetName.equalsIgnoreCase("CPacketPlayer$Position"))
            np.POSITION++;
        else if(packetName.equalsIgnoreCase("CPacketPlayer$PositionRotation"))
            np.POSITION_LOOK++;
        else if(packetName.equalsIgnoreCase("CPacketPlayer"))
            np.FLYING++;
        else if(packetName.equalsIgnoreCase("CPacketKeepAlive"))
            np.KEEP_ALIVE++;
        else if(packetName.equalsIgnoreCase("CPacketPlayerDigging"))
            np.BLOCK_DIG++;
        else if(packetName.equalsIgnoreCase("CPacketPlayerTryUseItemOnBlock"))
            np.BLOCK_PLACE++;
        else if(packetName.equalsIgnoreCase("CPacketAnimation"))
            np.ARM++;

        if (!packetName.equalsIgnoreCase("CPacketPlayer")) {
            np.TIME_OTHER_KEEP_ALIVE = System.currentTimeMillis();
            np.LAST_OTHER_KEEP_ALIVE = packetName;
        }
    }
}
