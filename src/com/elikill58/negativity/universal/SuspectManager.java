package com.elikill58.negativity.universal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.elikill58.negativity.universal.adapter.Adapter;

public class SuspectManager {

	public static boolean ENABLED = Adapter.getAdapter().getConfig().getBoolean("suspect.enabled");
	public static boolean CHAT = Adapter.getAdapter().getConfig().getBoolean("suspect.chat");
	public static boolean WITH_REPORT = Adapter.getAdapter().getConfig().getBoolean("suspect.with_report_cmd");

	public static void init() {
		ENABLED = Adapter.getAdapter().getConfig().getBoolean("suspect.enabled");
		CHAT = Adapter.getAdapter().getConfig().getBoolean("suspect.chat");
		WITH_REPORT = Adapter.getAdapter().getConfig().getBoolean("suspect.with_report_cmd");
	}

	public static void analyzeText(NegativityPlayer np, String text) {
		String[] content = text.split(" ");
		List<Cheat> cheats = new ArrayList<>();
		for(String s : content) {
			for(Cheat c : Adapter.getAdapter().getAbstractCheats())
				for(String alias : c.getAliases())
					if(alias.equalsIgnoreCase(s) || alias.contains(s) || alias.startsWith(s))
						cheats.add(c);
		}
		analyzeText(np, cheats);
	}

	public static void analyzeText(NegativityPlayer np, Collection<Cheat> cheats) {
		for(Cheat ac : cheats) {
			np.startAnalyze(ac);
		}
	}
}
