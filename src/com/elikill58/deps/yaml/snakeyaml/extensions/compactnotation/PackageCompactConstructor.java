// 
// Decompiled by Procyon v0.5.36
// 

package com.elikill58.deps.yaml.snakeyaml.extensions.compactnotation;

import org.yaml.snakeyaml.extensions.compactnotation.CompactConstructor;

public class PackageCompactConstructor extends CompactConstructor
{
    private String packageName;
    
    public PackageCompactConstructor(final String packageName) {
        this.packageName = packageName;
    }
    
    @Override
    protected Class<?> getClassForName(final String name) throws ClassNotFoundException {
        if (name.indexOf(46) < 0) {
            try {
                final Class<?> clazz = Class.forName(this.packageName + "." + name);
                return clazz;
            }
            catch (ClassNotFoundException ex) {}
        }
        return super.getClassForName(name);
    }
}
