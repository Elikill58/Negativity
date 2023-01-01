// 
// Decompiled by Procyon v0.5.36
// 

package com.elikill58.deps.yaml.snakeyaml.scanner;

import com.elikill58.deps.yaml.snakeyaml.tokens.Token;

public interface Scanner
{
    boolean checkToken(final Token.ID... p0);
    
    Token peekToken();
    
    Token getToken();
}
