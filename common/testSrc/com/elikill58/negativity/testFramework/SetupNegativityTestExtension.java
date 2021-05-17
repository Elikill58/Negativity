package com.elikill58.negativity.testFramework;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import com.elikill58.negativity.universal.Adapter;

public class SetupNegativityTestExtension implements BeforeEachCallback, AfterEachCallback {
	
	@Override
	public void beforeEach(ExtensionContext context) {
		Adapter.setAdapter(new TestAdapter());
	}
	
	@Override
	public void afterEach(ExtensionContext context) {
		Adapter.getAdapter().reloadConfig();
	}
}
