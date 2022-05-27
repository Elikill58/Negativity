package com.elikill58.negativity.sponge7;

import java.util.HashMap;
import java.util.Map;

import org.spongepowered.api.entity.living.player.Player;

public class SpongeForgeSupport {

	public static boolean isOnSpongeForge = false;
	
	@SuppressWarnings("unchecked")
	public static HashMap<String, String> getClientMods(Player p){
		HashMap<String, String> hash = new HashMap<>();
		try {
			Object entityPlayerMP = Class.forName("net.minecraft.entity.player.EntityPlayerMP").cast(p);
			Object connection = entityPlayerMP.getClass().getField("field_71135_a").get(entityPlayerMP);
			Object networkManager = connection.getClass().getField("field_147371_a").get(connection);
			Class<?> networkDispatcher = Class.forName("net.minecraftforge.fml.common.network.handshake.NetworkDispatcher");
			Object localNetworkDispatcher = networkDispatcher.getMethod("get", networkManager.getClass()).invoke(networkDispatcher, networkManager);
			Object tempHash = localNetworkDispatcher.getClass().getMethod("getModList").invoke(localNetworkDispatcher);
			if(tempHash instanceof Map)
				hash.putAll((Map<String, String>) tempHash); 
			else if(tempHash instanceof HashMap)
				hash = (HashMap<String, String>) tempHash;
			else
				System.out.println("[Negativity - Forge] UNKNOW MODS LIST: " + tempHash.getClass());
		} catch (NoSuchFieldException e) {
			System.out.println("This version of SpongeForge is not supported.");
			System.out.println("Field not found: " + e.getMessage());
			System.out.println("Please, report this to Elikill58 (@Elikill58#0743 on discord or arpetzouille@gmail.com by email)");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return hash;
	}
	
}
