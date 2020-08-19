package com.elikill58.negativity.universal.verif;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.config.ConfigAdapter;
import com.elikill58.negativity.universal.verif.storage.VerificationStorage;

public class VerificationManager {
	
	public static final UUID CONSOLE = UUID.fromString("daa451b2-2171-49a2-b861-edfa4ceb4449");

	// HashMap to allow multiple verification at the same time
	// Map<Target, Map<Mod, Verificator>>
	private static final Map<UUID, Map<UUID, Verificator>> VERIFICATIONS_BY_MOD = new HashMap<>();
	
	public static int timeVerif = 200;
	public static boolean disableAlertOnVerif = false;

	public static void init() {
		ConfigAdapter config = Adapter.getAdapter().getConfig();
		timeVerif = config.getInt("verif.time");
		disableAlertOnVerif = config.getBoolean("verif.disable_alert_on_verif");
		VerificationStorage.init();
	}
	
	public static int getTimeVerif() {
		return timeVerif;
	}
	
	public static boolean isDisablingAlertOnVerif() {
		return disableAlertOnVerif;
	}
	
	public static void create(UUID asker, UUID target, Verificator verificator) {
		VERIFICATIONS_BY_MOD.computeIfAbsent(target, id -> new HashMap<>()).put(asker, verificator);
	}
	
	public static void remove(UUID asker, UUID target) {
		Map<UUID, Verificator> verifsByMod = VERIFICATIONS_BY_MOD.get(target);
		if (verifsByMod != null) {
			verifsByMod.remove(asker);
		}
	}
	
	public static boolean hasVerifications(UUID target) {
		Map<UUID, Verificator> map = VERIFICATIONS_BY_MOD.get(target);
		return map != null && !map.isEmpty();
	}
	
	public static Collection<Verificator> getVerifications(UUID target) {
		Map<UUID, Verificator> verifsByMod = VERIFICATIONS_BY_MOD.get(target);
		if (verifsByMod != null) {
			return verifsByMod.values();
		}
		return Collections.emptyList();
	}
	
	public static Optional<Verificator> getVerificationsFrom(UUID target, UUID asker) {
		Map<UUID, Verificator> verifsByMod = VERIFICATIONS_BY_MOD.get(target);
		return verifsByMod != null ? Optional.ofNullable(verifsByMod.get(asker)) : Optional.empty();
	}
	
	public static <T> void recordData(UUID target, Cheat cheat, VerifData.DataType<T> type, T value) {
		getVerifications(target).forEach(verif -> verif.getVerifData(cheat).ifPresent(data -> data.getData(type).add(value)));
	}
}
