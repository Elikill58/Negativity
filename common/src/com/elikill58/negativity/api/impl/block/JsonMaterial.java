package com.elikill58.negativity.api.impl.block;

import com.elikill58.negativity.api.item.Material;
import com.elikill58.negativity.api.json.JSONObject;

public class JsonMaterial extends Material {

	private JSONObject json;
	
	public JsonMaterial(JSONObject json) {
		this.json = json;
	}
	
	@Override
	public boolean isSolid() {
		return (boolean) json.get("diggable");
	}

	@Override
	public boolean isTransparent() {
		return (boolean) json.get("transparent");
	}

	@Override
	public String getId() {
		return json.get("name").toString();
	}

	@Override
	public Object getDefault() {
		return this;
	}
}
