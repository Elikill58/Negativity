package com.elikill58.negativity.universal.translation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.function.BiConsumer;

import com.elikill58.negativity.universal.TranslatedMessages;
import com.elikill58.negativity.universal.adapter.Adapter;
import com.elikill58.negativity.universal.utils.FileUtils;

public class MessagesUpdater {

	public static void performUpdate(String dirName, BiConsumer<String, String[]> messageSink) {
		Adapter adapter = Adapter.getAdapter();
		Path messagesDir = adapter.getDataFolder().toPath().resolve(dirName);
		Path backupDir = messagesDir.resolveSibling(dirName + "_backup");

		try {
			FileUtils.cleanDirectory(backupDir);
			// Non-empty directories may be moved quickly this way
			Files.move(messagesDir, backupDir, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException ignore) {
			// But it may fail so we have a fallback solution if needed
			try {
				FileUtils.moveDirectory(messagesDir, backupDir);
			} catch (IOException e) {
				adapter.error("Failed to backup messages directory");
				e.printStackTrace();
				messageSink.accept("messages_update.backup_failed", new String[]{"%error%", e.getMessage()});
			}
		}

		TranslatedMessages.loadMessages();

		messageSink.accept("messages_update.update_done", new String[0]);
	}

}
