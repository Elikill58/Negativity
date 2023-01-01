// 
// Decompiled by Procyon v0.5.36
// 

package com.elikill58.deps.yaml.snakeyaml.parser;

import com.elikill58.deps.yaml.snakeyaml.events.Event;

public interface Parser
{
    boolean checkEvent(final Event.ID p0);
    
    Event peekEvent();
    
    Event getEvent();
}
