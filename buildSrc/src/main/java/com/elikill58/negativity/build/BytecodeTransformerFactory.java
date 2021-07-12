package com.elikill58.negativity.build;

import org.gradle.api.Task;
import org.objectweb.asm.ClassVisitor;

@FunctionalInterface
public interface BytecodeTransformerFactory {
	
	ClassVisitor transform(Task task, int api, ClassVisitor baseVisitor);
}
