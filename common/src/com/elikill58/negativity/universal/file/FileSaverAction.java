package com.elikill58.negativity.universal.file;

public interface FileSaverAction {

    /**
     * Save informations sync with current thread
     * 
     * @param finished the action to run when it's finished
     */
    void save(FileSaverTimer timer);
}
