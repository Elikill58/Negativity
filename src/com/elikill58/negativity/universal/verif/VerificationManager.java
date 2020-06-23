package com.elikill58.negativity.universal.verif;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.config.ConfigAdapter;

public class VerificationManager {
	
	public static final UUID CONSOLE = UUID.fromString("daa451b2-2171-49a2-b861-edfa4ceb4449");

	// HashMap to allow multiple verification at the same time
	// String for the name (to enable server to run command)
	private static final Map<UUID, Map<UUID, Verificator>> VERIFICATIONS_BY_MOD = new HashMap<>();
	
	public static int TIME_VERIF = 200;
	public static boolean DISABLE_ALERT_ON_VERIF = false;

	public static void init() {
		ConfigAdapter config = Adapter.getAdapter().getConfig();
		TIME_VERIF = config.getInt("verif.time");
		DISABLE_ALERT_ON_VERIF = config.getBoolean("verif.disable_alert_on_verif");
	}
	
	public static void create(UUID asker, UUID target, Verificator verificator) {
		Map<UUID, Verificator> map = VERIFICATIONS_BY_MOD.getOrDefault(target, new HashMap<>());
		map.put(asker, verificator);
		VERIFICATIONS_BY_MOD.put(target, map);
	}
	
	public static void remove(UUID asker, UUID target) {
		Map<UUID, Verificator> map = VERIFICATIONS_BY_MOD.getOrDefault(target, new HashMap<>());
		map.remove(asker);
		VERIFICATIONS_BY_MOD.put(target, map);
	}
	
	public static boolean hasVerifications(UUID target) {
		Map<UUID, Verificator> map = VERIFICATIONS_BY_MOD.get(target);
		return !(map == null || map.isEmpty());
	}
	
	public static Collection<Verificator> getVerifications(UUID target) {
		return VERIFICATIONS_BY_MOD.getOrDefault(target, new HashMap<>()).values();
	}
	
	public static Optional<Verificator> getVerificationsFrom(UUID target, UUID asker) {
		return Optional.ofNullable(VERIFICATIONS_BY_MOD.getOrDefault(target, new HashMap<>()).get(asker));
	}
}
