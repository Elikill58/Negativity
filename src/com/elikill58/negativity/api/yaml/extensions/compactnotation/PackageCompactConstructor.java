package com.elikill58.negativity.api.yaml.extensions.compactnotation;

public class PackageCompactConstructor extends CompactConstructor {
	private String packageName;

	public PackageCompactConstructor(final String packageName) {
		this.packageName = packageName;
	}

	@Override
	protected Class<?> getClassForName(final String name) throws ClassNotFoundException {
		if (name.indexOf(46) < 0) {
			try {
				final Class<?> clazz = Class.forName(this.packageName + "." + name);
				return clazz;
			} catch (ClassNotFoundException ex) {
			}
		}
		return super.getClassForName(name);
	}
}
