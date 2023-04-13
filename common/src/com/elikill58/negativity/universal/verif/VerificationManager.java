package com.elikill58.negativity.universal.verif;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.elikill58.negativity.api.yaml.Configuration;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.detections.Cheat;
import com.elikill58.negativity.universal.verif.storage.VerificationStorage;

public class VerificationManager {
	
	public static final UUID CONSOLE = UUID.fromString("daa451b2-2171-49a2-b861-edfa4ceb4449");

	/**
	 * HashMap to allow multiple verification at the same time
	 * 
	 * Map<Target, Map<Mod, Verificator>>
	 * 
	 */
	private static final Map<UUID, Map<UUID, Verificator>> VERIFICATIONS_BY_MOD = new HashMap<>();
	
	public static int timeVerif = 200;
	public static boolean disableAlertOnVerif = false;

	/**
	 * Load verifications
	 */
	public static void init() {
		Configuration config = Adapter.getAdapter().getConfig();
		timeVerif = config.getInt("verif.time");
		disableAlertOnVerif = config.getBoolean("verif.disable_alert_on_verif");
		VerificationStorage.init();
	}
	
	/**
	 * Get the default time of a verification in ticks
	 * 20 tick = 1 second
	 * 
	 * @return the time of verifications
	 */
	public static int getTimeVerif() {
		return timeVerif;
	}
	
	/**
	 * Check if alert are disabled during verif 
	 * 
	 * @return true if alert are disabled
	 */
	public static boolean isDisablingAlertOnVerif() {
		return disableAlertOnVerif;
	}
	
	/**
	 * Create a new verification
	 * 
	 * @param asker the UUID of the player which are the verif
	 * @param target the UUID of the verified player
	 * @param verificator the verificator
	 * @return the created and used verificator object
	 */
	public static Verificator create(UUID asker, UUID target, Verificator verificator) {
		VERIFICATIONS_BY_MOD.computeIfAbsent(target, id -> new HashMap<>()).put(asker, verificator);
		return verificator;
	}
	
	/**
	 * Remove the verification of someone
	 * 
	 * @param asker the mod which ask the verif
	 * @param target the verified player
	 */
	public static void remove(UUID asker, UUID target) {
		Map<UUID, Verificator> verifsByMod = VERIFICATIONS_BY_MOD.get(target);
		if (verifsByMod != null) {
			verifsByMod.remove(asker);
		}
	}
	
	/**
	 * Check if player is currently verified
	 * 
	 * @param target the verified player
	 * @return true if the player is verified
	 */
	public static boolean hasVerifications(UUID target) {
		Map<UUID, Verificator> map = VERIFICATIONS_BY_MOD.get(target);
		return map != null && !map.isEmpty();
	}
	
	/**
	 * Get all verification of a player
	 * 
	 * @param target the player which can have verif
	 * @return verifications or empty
	 */
	public static Collection<Verificator> getVerifications(UUID target) {
		Map<UUID, Verificator> verifsByMod = VERIFICATIONS_BY_MOD.get(target);
		if (verifsByMod != null) {
			return verifsByMod.values();
		}
		return Collections.emptyList();
	}
	
	/**
	 * Get verification of a player made by a specific player
	 * 
	 * @param target the verified player
	 * @param asker the mod which ask for verif
	 * @return optional of the verificator
	 */
	public static Optional<Verificator> getVerificationsFrom(UUID target, UUID asker) {
		Map<UUID, Verificator> verifsByMod = VERIFICATIONS_BY_MOD.get(target);
		return verifsByMod != null ? Optional.ofNullable(verifsByMod.get(asker)) : Optional.empty();
	}
	
	/**
	 * Record data for each verificator that the player is currently register on
	 * 
	 * @param <T> type of what is counted
	 * @param target the player which create the data
	 * @param cheat the cheat which record the data
	 * @param type the type of the data
	 * @param value the recorded value
	 */
	public static <T extends Number> void recordData(UUID target, Cheat cheat, VerifData.DataType<T> type, T value) {
		getVerifications(target).forEach(verif -> verif.getVerifData(cheat).ifPresent(data -> data.getData(type).add(value)));
	}
}
