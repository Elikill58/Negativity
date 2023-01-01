// 
// Decompiled by Procyon v0.5.36
// 

package com.elikill58.deps.yaml.snakeyaml.scanner;

import com.elikill58.deps.yaml.snakeyaml.error.Mark;
import com.elikill58.deps.yaml.snakeyaml.error.MarkedYAMLException;

public class ScannerException extends MarkedYAMLException
{
    private static final long serialVersionUID = 4782293188600445954L;
    
    public ScannerException(final String context, final Mark contextMark, final String problem, final Mark problemMark, final String note) {
        super(context, contextMark, problem, problemMark, note);
    }
    
    public ScannerException(final String context, final Mark contextMark, final String problem, final Mark problemMark) {
        this(context, contextMark, problem, problemMark, (String)null);
    }
}
