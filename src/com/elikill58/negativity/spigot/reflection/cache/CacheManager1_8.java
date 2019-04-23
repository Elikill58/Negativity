package com.elikill58.negativity.spigot.reflection.cache;

import java.io.File;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.bukkit.configuration.file.YamlConfiguration;

import com.elikill58.negativity.spigot.SpigotNegativity;
import com.elikill58.negativity.universal.NegativityAccount;
import com.elikill58.negativity.universal.TranslatedMessages;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;


public class CacheManager1_8 extends CacheManager {
	
	private LoadingCache<UUID, NegativityAccount> accountCache = CacheBuilder.newBuilder()
			.expireAfterAccess(10, TimeUnit.MINUTES)
			.build(new NegativityAccountLoader());
	
	@Override
	public void invalidate(UUID playerId) {
		accountCache.invalidate(playerId);
	}

	@Override
	public NegativityAccount get(UUID playerId) {
		try {
			return accountCache.get(playerId);
		} catch (ExecutionException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static class NegativityAccountLoader extends CacheLoader<UUID, NegativityAccount> {

		@Override
		public NegativityAccount load(UUID playerId) {
			NegativityAccount account = new NegativityAccount(playerId);

			File userFile = new File(SpigotNegativity.getInstance().getDataFolder(), "user" + File.separator + playerId + ".yml");
			YamlConfiguration userData = YamlConfiguration.loadConfiguration(userFile);
			account.setLang(userData.getString("lang", TranslatedMessages.DEFAULT_LANG));

			account.loadBanRequest();
			return account;
		}
	}
}
