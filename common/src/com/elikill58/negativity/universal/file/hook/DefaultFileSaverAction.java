package com.elikill58.negativity.universal.file.hook;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import com.elikill58.negativity.universal.file.FileSaverAction;
import com.elikill58.negativity.universal.file.FileSaverTimer;

public class DefaultFileSaverAction implements FileSaverAction {

	private final File file;
	private final String content;
	
	public DefaultFileSaverAction(File file, String content) {
		this.file = file;
		this.content = content;
	}
	
	@Override
	public void save(FileSaverTimer timer) {
        try {
            Files.write(file.toPath(), content.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    	
    	timer.removeActionRunning();
    }
}
