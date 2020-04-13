package com.elikill58.negativity.universal.translation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.function.BiConsumer;

import com.elikill58.negativity.universal.TranslatedMessages;
import com.elikill58.negativity.universal.adapter.Adapter;

public class MessagesUpdater {

	public static void performUpdate(String dirName, BiConsumer<String, String[]> messageSink) {
		Adapter adapter = Adapter.getAdapter();
		Path messagesDir = adapter.getDataFolder().toPath().resolve(dirName);
		Path backupDir = messagesDir.resolveSibling(dirName + "_backup");
		try {
			Files.createDirectories(backupDir);
		} catch (IOException e) {
			messageSink.accept("messages_update.dir_creation_failed", new String[]{e.getMessage()});
			return;
		}

		if (TranslatedMessages.activeTranslation) {
			for (String language : TranslatedMessages.LANGS) {
				backupAndCopyFile(language, messagesDir, backupDir, adapter, messageSink);
			}
		} else {
			backupAndCopyFile(TranslatedMessages.getDefaultLang(), messagesDir, backupDir, adapter, messageSink);
		}

		TranslatedMessages.loadMessages();

		messageSink.accept("messages_update.update_done", new String[0]);
	}

	private static void backupAndCopyFile(String language, Path messagesDir, Path backupDir, Adapter adapter, BiConsumer<String, String[]> messageSink) {
		String fileName = language + ".yml";
		Path messageFile = messagesDir.resolve(fileName);
		try {
			if (Files.exists(messageFile)) {
				Path backupFile = backupDir.resolve(fileName);
				Files.move(messageFile, backupFile, StandardCopyOption.REPLACE_EXISTING);
			}

			adapter.copy(language, messageFile.toFile());
		} catch (IOException e) {
			messageSink.accept("messages_update.file_update_failed", new String[]{language, e.getMessage()});
		}
	}
}
