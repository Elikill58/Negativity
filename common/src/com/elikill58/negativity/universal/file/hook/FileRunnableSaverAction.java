package com.elikill58.negativity.universal.file.hook;

import com.elikill58.negativity.universal.file.FileSaverAction;
import com.elikill58.negativity.universal.file.FileSaverTimer;

public class FileRunnableSaverAction implements FileSaverAction {

	private final Runnable run;
	
	public FileRunnableSaverAction(Runnable run) {
		this.run = run;
	}
	
	@Override
	public void save(FileSaverTimer timer) {
		run.run();
    	
    	timer.removeActionRunning();
    }
}
