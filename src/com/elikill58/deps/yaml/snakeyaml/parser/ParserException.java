// 
// Decompiled by Procyon v0.5.36
// 

package com.elikill58.deps.yaml.snakeyaml.parser;

import com.elikill58.deps.yaml.snakeyaml.error.Mark;
import com.elikill58.deps.yaml.snakeyaml.error.MarkedYAMLException;

public class ParserException extends MarkedYAMLException
{
    private static final long serialVersionUID = -2349253802798398038L;
    
    public ParserException(final String context, final Mark contextMark, final String problem, final Mark problemMark) {
        super(context, contextMark, problem, problemMark, null, null);
    }
}
