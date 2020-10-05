package com.elikill58.negativity.universal.verif;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.elikill58.negativity.api.NegativityPlayer;
import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.Version;
import com.elikill58.negativity.universal.verif.storage.VerificationStorage;

public class Verificator {
	
	public static final int VERIFICATION_VERSION = 0;
	private static final Collector<Cheat, ?, Map<Cheat, VerifData>> COLLECTOR = Collectors.toMap(Function.identity(), t -> new VerifData());
	
	private final Map<Cheat, VerifData> cheats;
	private final NegativityPlayer np;
	private final String asker;
	private final List<String> messages;
	private final int version;
	private final Version playerVersion;
	
	public Verificator(NegativityPlayer np, String asker) {
		this(np, asker, new HashSet<>(Cheat.CHEATS));
	}
	
	public Verificator(NegativityPlayer np, String asker, Set<Cheat> list) {
		this(np, asker, list.stream().filter(Cheat::hasVerif).collect(COLLECTOR), new ArrayList<>(), VERIFICATION_VERSION, np.getPlayer().getPlayerVersion());
	}
	
	public Verificator(NegativityPlayer np, String asker, Map<Cheat, VerifData> cheats, List<String> messages, int version, Version playerVersion) {
		this.np = np;
		this.asker = asker;
		this.cheats = cheats;
		this.messages = messages;
		this.version = version;
		this.playerVersion = playerVersion;
	}

	public NegativityPlayer getNegativityPlayer() {
		return np;
	}
	
	public UUID getPlayerId() {
		return np.getUUID();
	}
	
	public String getAsker() {
		return asker;
	}

	public Map<Cheat, VerifData> getCheats() {
		return cheats;
	}
	
	public Optional<VerifData> getVerifData(Cheat c) {
		return Optional.ofNullable(cheats.get(c));
	}
	
	public List<String> getMessages(){
		return messages;
	}

	public int getVersion() {
		return version;
	}

	public Version getPlayerVersion() {
		return playerVersion;
	}

	public void generateMessage() {
		StringJoiner messageCheatNothing = new StringJoiner(", ");
		for(Entry<Cheat, VerifData> currentCheat : cheats.entrySet()) {
			Cheat c = currentCheat.getKey();
			VerifData data = currentCheat.getValue();
			if(data.hasSomething()) {
				String name = c.makeVerificationSummary(data, np);
				if(name != null) {
					messages.add("&6" + c.getName() + "&8: &7" + name);
					continue;
				}
			} else if(c.hasVerif())
				messageCheatNothing.add(c.getName());
		}
		if (messageCheatNothing.length() > 0)
			messages.add("Nothing detected: " + messageCheatNothing);
	}
	
	public void save() {
		if(messages.isEmpty())
			generateMessage();
		VerificationStorage.getStorage().saveVerification(this).exceptionally(t -> {
		    Adapter.getAdapter().getLogger().error("Error occurred while saving verification results");
		    t.printStackTrace();
		    return null;
		});
	}
}
