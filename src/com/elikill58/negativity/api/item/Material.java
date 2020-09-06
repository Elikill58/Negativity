package com.elikill58.negativity.api.item;

import com.elikill58.negativity.api.NegativityObject;

public abstract class Material extends NegativityObject {

	public abstract boolean isSolid();

	public abstract boolean isTransparent();

	public abstract String getId();

	public boolean isConsumable() {
		String id = getId().toLowerCase();
		return id.contains("cooked") || id.contains("mutton") || id.contains("beef") || id.contains("apple")
				|| id.contains("potato") || id.contains("carrot") || id.contains("bread") || id.contains("chicken")
				|| id.contains("salmon") || id.contains("rabbit") || id.contains("porkshop") || id.contains("fish");
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Material))
			return false;
		Material to = (Material) obj;
		if (this.getId().equals(to.getId()) && this.isSolid() == to.isSolid()
				&& this.isTransparent() == to.isTransparent())
			return true;
		return false;
	}

	@Override
	public String toString() {
		return "Material{id=" + getId() + "}";
	}
}
