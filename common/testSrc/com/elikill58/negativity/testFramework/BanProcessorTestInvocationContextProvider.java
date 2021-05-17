package com.elikill58.negativity.testFramework;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.jupiter.api.extension.TestTemplateInvocationContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContextProvider;

import com.elikill58.negativity.universal.ban.processor.BanProcessor;
import com.elikill58.negativity.universal.ban.processor.NegativityBanProcessor;
import com.elikill58.negativity.universal.ban.storage.DatabaseActiveBanStorage;
import com.elikill58.negativity.universal.ban.storage.DatabaseBanLogsStorage;
import com.elikill58.negativity.universal.ban.storage.FileActiveBanStorage;
import com.elikill58.negativity.universal.ban.storage.FileBanLogsStorage;

public class BanProcessorTestInvocationContextProvider implements TestTemplateInvocationContextProvider {
	
	@Override
	public boolean supportsTestTemplate(ExtensionContext context) {
		return true;
	}
	
	@Override
	public Stream<TestTemplateInvocationContext> provideTestTemplateInvocationContexts(ExtensionContext context) {
		List<TestTemplateInvocationContext> invocationContexts = new ArrayList<>();
		invocationContexts.add(new FileBanProcessorTestInvocationContext(context, Paths.get("bans")));
		
		String availableDatabases = context.getConfigurationParameter("negativity.databases").orElse(null);
		if (availableDatabases != null) {
			String[] split = availableDatabases.split(",");
			for (String database : split) {
				invocationContexts.add(new DatabaseBanProcessorTestInvocationContext(context, database));
			}
		}
		
		return invocationContexts.stream();
	}
	
	private static class DatabaseBanProcessorTestInvocationContext implements TestTemplateInvocationContext {
		
		private final ExtensionContext context;
		private final String databaseName;
		
		public DatabaseBanProcessorTestInvocationContext(ExtensionContext context, String databaseName) {
			this.context = context;
			this.databaseName = databaseName;
		}
		
		@Override
		public String getDisplayName(int invocationIndex) {
			return context.getDisplayName() + " [database: " + databaseName + "]";
		}
		
		@Override
		public List<Extension> getAdditionalExtensions() {
			List<Extension> extensions = new ArrayList<>();
			extensions.add(new SetupNegativityTestExtension());
			extensions.add(new SetupDatabaseTestExtension(databaseName));
			extensions.add(new ParameterResolver() {
				@Override
				public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
					return parameterContext.getParameter().getType() == BanProcessor.class;
				}
				
				@Override
				public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
					return new NegativityBanProcessor(new DatabaseActiveBanStorage(), new DatabaseBanLogsStorage());
				}
			});
			return extensions;
		}
		
	}
	
	private static class FileBanProcessorTestInvocationContext implements TestTemplateInvocationContext {
		
		private final ExtensionContext context;
		private final Path baseDir;
		
		public FileBanProcessorTestInvocationContext(ExtensionContext context, Path baseDir) {
			this.context = context;
			this.baseDir = baseDir;
		}
		
		@Override
		public String getDisplayName(int invocationIndex) {
			return context.getDisplayName() + " [file]";
		}
		
		@Override
		public List<Extension> getAdditionalExtensions() {
			List<Extension> extensions = new ArrayList<>();
			extensions.add(new CleanDirectoryTestExtension(baseDir));
			extensions.add(new SetupNegativityTestExtension());
			extensions.add(new ParameterResolver() {
				@Override
				public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
					return parameterContext.getParameter().getType() == BanProcessor.class;
				}
				
				@Override
				public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
					return new NegativityBanProcessor(new FileActiveBanStorage(baseDir), new FileBanLogsStorage(baseDir));
				}
			});
			return extensions;
		}
	}
}
