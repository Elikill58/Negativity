package com.elikill58.negativity.universal.verif;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.StringJoiner;

import com.elikill58.negativity.universal.Cheat;
import com.elikill58.negativity.universal.NegativityPlayer;

public class Verificator {
	
	private HashMap<Cheat, VerifData> cheats = new HashMap<>();
	
	private final NegativityPlayer np;
	private List<String> messages = new ArrayList<>();
	
	public Verificator(NegativityPlayer np) {
		this(np, Cheat.CHEATS);
	}
	
	public Verificator(NegativityPlayer np, List<Cheat> list) {
		this.np = np;
		list.forEach((c) -> cheats.put(c, new VerifData()));
	}
	
	public void generateMessage(String asker) {
		StringJoiner messageCheatNothing = new StringJoiner(", ");
		for(Entry<Cheat, VerifData> currentCheat : cheats.entrySet()) {
			Cheat c = currentCheat.getKey();
			String name = c.compile(currentCheat.getValue());
			if(name == null)
				messageCheatNothing.add(c.getName());
			else
				messages.add(c.getName() + ": " + name);
		}
		messages.add("Nothing detected: " + messageCheatNothing.toString());
		//np.verificatorForMod.remove(asker);
	}
	
	public List<String> getMessages(){
		return messages;
	}

	public NegativityPlayer getNp() {
		return np;
	}
}
