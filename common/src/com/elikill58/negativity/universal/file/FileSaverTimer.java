package com.elikill58.negativity.universal.file;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import com.elikill58.negativity.universal.Adapter;

public class FileSaverTimer implements Runnable {

	private static FileSaverTimer instance;
	public static FileSaverTimer getInstance() {
		if(instance == null)
			instance = new FileSaverTimer();
		return instance;
	}
	private static final int MAX_RUNNING = 10, SKIP_WHEN_ALREADY = 2;
    private final List<FileSaverAction> allActions = new ArrayList<>();
    public void addAction(FileSaverAction action) {
        allActions.add(action);
    }
    
    private int actionRunning = 0;
    
    public FileSaverTimer() {
    	if(instance != null) {
    		Adapter.getAdapter().getLogger().error("Another instance of FileSaveTimer is created even if an old already exist");
    		return;
    	}
    }
    
    @Override
    public void run() {
		try {
			Files.createDirectories(Adapter.getAdapter().getDataFolder().getAbsoluteFile().toPath().resolve("user").resolve("proof"));
		} catch (IOException e) {
			e.printStackTrace();
		}
        if(actionRunning < SKIP_WHEN_ALREADY) {
	        for(int i = 0; !allActions.isEmpty() && i < MAX_RUNNING; i++) {
	            FileSaverAction action = allActions.remove(0); // removing first item
	            actionRunning++; // adding a running task
	            action.save(this); // save, and when it's finished removing running task
			}
        }// too many already running, skipping save of others ...
        
        // now check for old handle
        new ArrayList<>(FileHandle.getFileHandles()).stream().filter(FileHandle::shouldBeClosed).forEach(FileHandle::close);
    }
    
    public void runAll() {
    	allActions.forEach((a) -> a.save(this));
    }

    public void removeActionRunning() {
    	actionRunning--;
    }
}
