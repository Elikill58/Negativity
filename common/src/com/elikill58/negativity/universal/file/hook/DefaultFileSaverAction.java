package com.elikill58.negativity.universal.file.hook;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import com.elikill58.negativity.universal.file.FileSaverAction;
import com.elikill58.negativity.universal.file.FileSaverTimer;

public class DefaultFileSaverAction implements FileSaverAction {

	private final Path file;
	private final String content;
	
	public DefaultFileSaverAction(Path file, String content) {
		this.file = file;
		this.content = content;
	}
	
	@Override
	public void save(FileSaverTimer timer) {
        try {
            Files.write(file, content.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    	
    	timer.removeActionRunning();
    }
}
