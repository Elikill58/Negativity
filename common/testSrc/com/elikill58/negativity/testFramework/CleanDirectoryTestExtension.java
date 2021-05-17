package com.elikill58.negativity.testFramework;

import java.nio.file.Path;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import com.elikill58.negativity.universal.utils.FileUtils;

public class CleanDirectoryTestExtension implements BeforeEachCallback, AfterEachCallback {
	
	private final Path directory;
	
	public CleanDirectoryTestExtension(Path directory) {
		this.directory = directory;
	}
	
	
	@Override
	public void beforeEach(ExtensionContext context) throws Exception {
		FileUtils.cleanDirectory(directory);
	}
	
	@Override
	public void afterEach(ExtensionContext context) throws Exception {
		FileUtils.cleanDirectory(directory);
	}
}
