// 
// Decompiled by Procyon v0.5.36
// 

package com.elikill58.deps.yaml.snakeyaml;

public class LoaderOptions
{
    private boolean allowDuplicateKeys;
    
    public LoaderOptions() {
        this.allowDuplicateKeys = true;
    }
    
    public boolean isAllowDuplicateKeys() {
        return this.allowDuplicateKeys;
    }
    
    public void setAllowDuplicateKeys(final boolean allowDuplicateKeys) {
        this.allowDuplicateKeys = allowDuplicateKeys;
    }
}
