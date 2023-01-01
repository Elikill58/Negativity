// 
// Decompiled by Procyon v0.5.36
// 

package com.elikill58.deps.yaml.snakeyaml.emitter;

import java.io.IOException;

import com.elikill58.deps.yaml.snakeyaml.events.Event;

public interface Emitable
{
    void emit(final Event p0) throws IOException;
}
