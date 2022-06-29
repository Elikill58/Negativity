package com.elikill58.negativity.universal.storage.proof;

import java.sql.Timestamp;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Proof;
import com.elikill58.negativity.universal.Version;
import com.elikill58.negativity.universal.detections.keys.CheatKeys;
import com.elikill58.negativity.universal.report.ReportType;
import com.elikill58.negativity.universal.utils.UniversalUtils;

public class OldProofFileMigration {

	public static @Nullable Proof getProof(UUID uuid, String line) {
		try {
			return line.contains("| Warn:") && getContent(line, "TPS: (.*?) \\/ (.*?) \\/ (.*?)") != null ? getForV2(uuid, line) : getForV1(uuid, line);
		} catch (Exception e) {
			Adapter.getAdapter().getLogger().printError("Failed to parse line: '" + line + "' as proof.", e);
			return null;
		}
	}

	private static @Nullable Proof getForV1(UUID uuid, String s) {
		CheatKeys cheatKey = CheatKeys.fromLowerKey(getContent(s, "\\% (.*?) > "));
		int ping = Integer.parseInt(getContent(s, "\\((.*?)ms\\)"));
		int reliability = Integer.parseInt(getContent(s, "\\) (.*?)\\% "));
		Timestamp date = Timestamp.valueOf(getContent(s, "(.*?): "));
		String checkInfo = getContent(s, " > (.*?). Player version");
		Version version = Version.getVersion(getContent(s, "Player version: (.*?)\\."));
		double[] tps = getTps(getContent(s, "TPS: \\[(.*?)\\]").split(", "));
		return new Proof(-1, uuid, ReportType.WARNING, cheatKey, null, ping, 1, reliability, date, checkInfo, version, 0, tps);
	}

	private static @Nullable Proof getForV2(UUID uuid, String s) {
		CheatKeys cheatKey = CheatKeys.fromLowerKey(getContent(s, "\\% (.*?) x"));
		String checkName = getContent(s, " - (.*?) > ");
		int ping = Integer.parseInt(getContent(s, "\\((.*?)ms\\)"));
		int reliability = Integer.parseInt(getContent(s, "\\) (.*?)\\% "));
		int amount = Integer.parseInt(getContent(s, " x(.*?) - "));
		Timestamp date = Timestamp.valueOf(getContent(s, "(.*?): "));
		String checkInfo = getContent(s, " > (.*?) \\|");
		Version version = Version.getVersion(getContent(s, ", Version: (.*?)\\."));
		long warn = Long.parseLong(getContent(s, "\\| Warn: (.*?), "));
		double[] tps = getTps(s.split("TPS: ")[1].replace(',', '.').split(" / "));
		if(checkInfo.equalsIgnoreCase("omega-craft")) { // was with bugged key
			checkInfo = checkName;
			checkName = "omega-craft";
		}
		return new Proof(-1, uuid, ReportType.WARNING, cheatKey, checkName, ping, amount, reliability, date, checkInfo, version, warn, tps);
	}
	
	private static double[] getTps(String[] args) {
		double[] tps = new double[args.length];
		for(int i = 0; i < args.length; i++) {
			String tpsArg = args[i];
			if(UniversalUtils.isDouble(tpsArg)) {
				tps[i] = Double.parseDouble(tpsArg);
			} else {
				Adapter.getAdapter().getLogger().warn(tpsArg + " isn't a valid TPS value.");
			}
		}
		return tps;
	}

	private static String getContent(String line, String pattern) {
		Pattern patternObject = Pattern.compile(pattern);
		Matcher matcher = patternObject.matcher(line);
		return matcher.find() ? matcher.group(1) : null;
	}
}
