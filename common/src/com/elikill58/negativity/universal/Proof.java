package com.elikill58.negativity.universal;

import java.sql.Timestamp;
import java.util.UUID;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.universal.detections.keys.CheatKeys;
import com.elikill58.negativity.universal.report.ReportType;

public class Proof {

	private final UUID uuid;
	private final Timestamp time;
	private final int id, ping, reliability;
	private final long warn, amount;
	private final CheatKeys cheatKey;
	private final ReportType reportType;
	private final String checkName, checkInformations;
	private final double[] tps;
	private final Version version;

	/**
	 * Create a new proof object. This is mostly used for new proof (it generate all missing value)
	 * 
	 * @param np the player concerned by this proof
	 * @param cheat the cheat
	 * @param checkName the name of the check
	 * @param amount amount of alert
	 * @param reliability the reliability
	 * @param explaination all explaination from check
	 */
	public Proof(NegativityPlayer np, ReportType reportType, CheatKeys cheat, @Nullable String checkName, int ping, long amount, int reliability, String checkInformations) {
		this(-1, np.getUUID(), reportType, cheat, checkName, ping, amount, reliability, new Timestamp(System.currentTimeMillis()), checkInformations, np.getPlayer().getPlayerVersion(), np.getAccount().getWarn(checkName), Adapter.getAdapter().getTPS());
	}

	/**
	 * Create a new proof object. This is mostly used for new proof (it generate all missing value)
	 * 
	 * @param id the ID in the storage
	 * @param uuid uuid of concerned player by this proof
	 * @param cheatKey the cheat
	 * @param checkName the name of the check
	 * @param ping the ping of player
	 * @param amount amount of alert
	 * @param reliability the reliability
	 * @param time the time when the alert have been raised
	 * @param explaination all explanation from check
	 * @param version the version of the player when the proof is made
	 * @param warn amount of warn at the time of proof
	 * @param tps tps when doing proof
	 */
	public Proof(int id, UUID uuid, ReportType reportType, CheatKeys cheatKey, @Nullable String checkName, int ping, long amount, int reliability, Timestamp time, String checkInformations, Version version, long warn, double[] tps) {
		this.id = id;
		this.uuid = uuid;
		this.reportType = reportType;
		this.cheatKey = cheatKey;
		this.checkName = checkName;
		this.ping = ping;
		this.amount = amount;
		this.reliability = reliability;
		this.time = time;
		this.checkInformations = checkInformations;
		this.version = version;
		this.warn = warn;
		this.tps = tps;
	}
	
	public int getId() {
		return id;
	}
	
	public UUID getUUID() {
		return uuid;
	}
	
	public long getAmount() {
		return amount;
	}
	
	public ReportType getReportType() {
		return reportType;
	}
	
	public CheatKeys getCheatKey() {
		return cheatKey;
	}
	
	public String getCheckName() {
		return checkName;
	}

	public String getCheckInformations() {
		return checkInformations;
	}
	
	public int getPing() {
		return ping;
	}
	
	public int getReliability() {
		return reliability;
	}
	
	public Timestamp getTime() {
		return time;
	}
	
	public double[] getTps() {
		return tps;
	}
	
	public Version getVersion() {
		return version;
	}
	
	public long getWarn() {
		return warn;
	}
	
	@Override
	public String toString() {
		return id + "{uuid=" + uuid.toString() + ",cheat_key=" + cheatKey.getLowerKey() + ",check_name=" + checkName + ",check_informations=" + checkInformations + ",ping=" + ping + ",amount=" + amount + ",reliability=" + reliability + "}";
	}
}
