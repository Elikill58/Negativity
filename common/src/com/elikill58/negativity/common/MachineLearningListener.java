package com.elikill58.negativity.common;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.CompletableFuture;

import com.elikill58.negativity.universal.Adapter;
import com.elikill58.negativity.universal.utils.FileUtils;
import com.elikill58.negativity.universal.utils.UniversalUtils;

// TODO implement this ML
public class MachineLearningListener {

	
	public MachineLearningListener() {
		CompletableFuture.runAsync(() -> {
			try {
				File modelFile = getModelFile();
				if(modelFile.exists()) {
					String sha = FileUtils.getHashSHA256(modelFile);
					String real = UniversalUtils.getContentFromURL("https://api.negativity.fr/ml").orElse("nothing");
					if(real.equalsIgnoreCase("nothing") || sha.equalsIgnoreCase(real))
						return; // no need to change it
				}
				URL website = new URL("https://api.negativity.fr/negativity.model");
				try (InputStream in = website.openStream()) {
				    Files.copy(in, modelFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
				}
				Adapter.getAdapter().getLogger().info("Loaded machine learning model.");
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}
	
	private File getModelFile() {
		return new File(new File(Adapter.getAdapter().getDataFolder(), "models"), "negativity.model");
	}
}
