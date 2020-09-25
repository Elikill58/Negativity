package com.elikill58.negativity.universal.setBack.processor;

import com.elikill58.negativity.api.GameMode;
import com.elikill58.negativity.api.entity.Player;
import com.elikill58.negativity.api.location.Vector;
import com.elikill58.negativity.universal.setBack.SetBackEntry;
import com.elikill58.negativity.universal.setBack.SetBackProcessor;

public class ValueEditorProcessor implements SetBackProcessor {

	private ValueEditorAction valueEditor;
	private Object obj;
	
	public ValueEditorProcessor(SetBackEntry entry) {
		valueEditor = ValueEditorAction.valueOf(entry.getKey().toUpperCase());
		switch (valueEditor) {
		case GAMEMODE:
			obj = GameMode.get(entry.getValue());
			break;
		case VELOCITY:
			String[] data = entry.getValue().split(",");
			obj = new Vector(Double.parseDouble(data[0]), Double.parseDouble(data[1]), Double.parseDouble(data[2]));
			break;
		}
	}
	
	@Override
	public String getName() {
		return "value_editor";
	}
	
	@Override
	public void perform(Player p) {
		switch (valueEditor) {
		case GAMEMODE:
			// TODO edit gamemode
			break;
		case VELOCITY:
			p.setVelocity((Vector) obj);
			break;
		}
	}
	
	public static enum ValueEditorAction {
		
		GAMEMODE,
		VELOCITY;
	
	}
}
