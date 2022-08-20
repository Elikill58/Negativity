package com.elikill58.negativity.minestom.listeners;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.api.events.EventManager;
import com.elikill58.negativity.api.events.player.LoginEvent;
import com.elikill58.negativity.api.events.player.LoginEvent.Result;
import com.elikill58.negativity.minestom.impl.entity.MinestomPlayer;
import com.elikill58.negativity.api.events.player.PlayerConnectEvent;
import com.elikill58.negativity.api.events.player.PlayerLeaveEvent;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.text.Text;

public class PlayersListeners {

	public static void register() {
		ServerPlayConnectionEvents.DISCONNECT.register(PlayersListeners::onLeave);
		ServerPlayConnectionEvents.INIT.register(PlayersListeners::onPreLogin);
		ServerPlayConnectionEvents.JOIN.register(PlayersListeners::onPlayerJoin);
	}

	public static void onPreLogin(ServerPlayNetworkHandler e, MinecraftServer srv) {
		LoginEvent event = new LoginEvent(e.getPlayer().getUuid(), e.getPlayer().getName().getString(), Result.ALLOWED,
				null, "");
		EventManager.callEvent(event);
		if (!event.getLoginResult().equals(Result.ALLOWED))
			e.disconnect(Text.of(event.getKickMessage()));
	}

	public static void onPlayerJoin(ServerPlayNetworkHandler e, PacketSender sender, MinecraftServer srv) {
		NegativityPlayer np = NegativityPlayer.getNegativityPlayer(e.getPlayer().getUuid(), () -> new MinestomPlayer(e.getPlayer()));
		PlayerConnectEvent event = new PlayerConnectEvent(np.getPlayer(), np, "");
		EventManager.callEvent(event);
		// TODO add again e.setMessage(Text.of(event.getJoinMessage()));
	}

	public static void onLeave(ServerPlayNetworkHandler e, MinecraftServer srv) {
		NegativityPlayer np = NegativityPlayer.getNegativityPlayer(e.getPlayer().getUuid(),
				() -> new MinestomPlayer(e.getPlayer()));
		PlayerLeaveEvent event = new PlayerLeaveEvent(np.getPlayer(), np, "");
		EventManager.callEvent(event);
		// TODO add again e.setMessage(Text.of(event.getQuitMessage()));
		NegativityPlayer.removeFromCache(e.getPlayer().getUuid());
	}
}
