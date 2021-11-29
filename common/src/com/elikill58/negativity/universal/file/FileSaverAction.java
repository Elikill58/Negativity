package com.elikill58.negativity.universal.file;

public interface FileSaverAction {

    /**
     * Technically save the save sync with the thread which run this
     * 
     * @param finished the action to run when it's finished
     */
    void save(FileSaverTimer timer);
}
