package com.elikill58.negativity.build;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.gradle.api.Task;
import org.gradle.api.tasks.TaskExecutionException;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class EventListenerTransformer extends ClassVisitor {
	
	private static final String BAKE_NAME = "bakeListeners";
	private static final String BAKE_DESC = "(Ljava/util/function/BiConsumer;)V";
	private static final String BAKE_SIGNATURE = "(Ljava/util/function/BiConsumer<Ljava/lang/Class<+Lcom/elikill58/negativity/api/events/Event;>;Lcom/elikill58/negativity/api/events/Listener<*>;>;)V";
	
	private static final Handle METAFACTORY = new Handle(Opcodes.H_INVOKESTATIC,
		"java/lang/invoke/LambdaMetafactory",
		"metafactory",
		"(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;",
		false);
	
	private final Task task;
	
	private boolean isListenerClass = false;
	private boolean isBakedListeners = false;
	
	private String className;
	private String classSignature;
	
	// Event type to listener method name
	private final List<ListenerInfo> listeners = new ArrayList<>();
	
	public EventListenerTransformer(Task task, int api, ClassVisitor classVisitor) {
		super(api, classVisitor);
		this.task = task;
	}
	
	@Override
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
		className = name;
		classSignature = signature;
		for (String itf : interfaces) {
			if (itf.equals("com/elikill58/negativity/api/events/Listeners")) {
				System.out.println("Visiting listener class " + className);
				isListenerClass = true;
				continue;
			}
			
			if (itf.equals("com/elikill58/negativity/api/events/BakedListeners")) {
				isBakedListeners = true;
				continue;
			}
		}
		
		String[] actualInterfaces = interfaces;
		if (isListenerClass && !isBakedListeners) {
			actualInterfaces = Arrays.copyOf(interfaces, interfaces.length + 1);
			actualInterfaces[interfaces.length] = "com/elikill58/negativity/api/events/BakedListeners";
		}
		super.visit(version, access, name, signature, superName, actualInterfaces);
	}
	
	@Override
	public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
		MethodVisitor superVisitor = super.visitMethod(access, name, descriptor, signature, exceptions);
		if (isListenerClass && !isBakedListeners) {
			Type methodType = Type.getMethodType(descriptor);
			return new ListenerMethodFinder(api, superVisitor, methodType, name);
		}
		return superVisitor;
	}
	
	@Override
	public void visitEnd() {
		if (!isListenerClass || isBakedListeners) {
			super.visitEnd();
			return;
		}
		
		System.out.println("Emitting bakeListeners");
		
		MethodVisitor method = super.visitMethod(Opcodes.ACC_PUBLIC, BAKE_NAME, BAKE_DESC, BAKE_SIGNATURE, new String[0]);
		method.visitParameter("registrator", 0);
		
		method.visitCode();
		
		Label start = new Label();
		method.visitLabel(start);
		
		for (ListenerInfo listenerInfo : listeners) {
			Type methodDescriptor = listenerInfo.methodDescriptor;
			String methodName = listenerInfo.methodName;
			
			// Emits bytecode equivalent to BiConsumer.accept(EventClass.class, this::onEvent)
			
			method.visitVarInsn(Opcodes.ALOAD, 1);
			method.visitLdcInsn(methodDescriptor.getArgumentTypes()[0]);
			method.visitVarInsn(Opcodes.ALOAD, 0);
			
			// Emits the method reference
			Type lambdaDescriptor = Type.getMethodType("(Lcom/elikill58/negativity/api/events/Event;)V");
			Handle listenerHandle = new Handle(Opcodes.H_INVOKEVIRTUAL, EventListenerTransformer.this.className, methodName, methodDescriptor.getDescriptor(), false);
			method.visitInvokeDynamicInsn("call", "(L" + EventListenerTransformer.this.className + ";)Lcom/elikill58/negativity/api/events/Listener;", METAFACTORY, lambdaDescriptor, listenerHandle, methodDescriptor);
			
			method.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/function/BiConsumer", "accept", "(Ljava/lang/Object;Ljava/lang/Object;)V", true);
			
			method.visitLabel(new Label());
		}
		
		method.visitInsn(Opcodes.RETURN);
		
		Label end = new Label();
		method.visitLabel(end);
		
		String className = "L" + EventListenerTransformer.this.className + ";";
		method.visitLocalVariable("this", className, EventListenerTransformer.this.classSignature, start, end, 0);
		
		method.visitLocalVariable("registrator", "Ljava/util/function/BiConsumer;", "Ljava/util/function/BiConsumer<Ljava/lang/Class<+Lcom/elikill58/negativity/api/events/Event;>;Lcom/elikill58/negativity/api/events/Listener<*>;>;", start, end, 1);
		method.visitMaxs(3, 2);
		
		method.visitEnd();
		
		super.visitEnd();
	}
	
	private class ListenerMethodFinder extends MethodVisitor {
		
		private final String methodName;
		private final Type methodDescriptor;
		
		public ListenerMethodFinder(int api, MethodVisitor methodVisitor, Type methodDescriptor, String methodName) {
			super(api, methodVisitor);
			this.methodName = methodName;
			this.methodDescriptor = methodDescriptor;
		}
		
		@Override
		public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
			if (descriptor.equals("Lcom/elikill58/negativity/api/events/EventListener;")) {
				Type[] parameterTypes = methodDescriptor.getArgumentTypes();
				if (parameterTypes.length != 1) {
					throw new TaskExecutionException(task, new Exception("Listener method must have exactly one parameter"));
				}
				
				System.out.println("Found listener method " + methodName + " for event " + parameterTypes[0]);
				EventListenerTransformer.this.listeners.add(new ListenerInfo(methodDescriptor, methodName));
			}
			return super.visitAnnotation(descriptor, visible);
		}
	}
	
	private static class ListenerInfo {
		public final Type methodDescriptor;
		public final String methodName;
		
		private ListenerInfo(Type methodDescriptor, String methodName) {
			this.methodDescriptor = methodDescriptor;
			this.methodName = methodName;
		}
	}
	
	public static class Factory implements BytecodeTransformerFactory {
		
		@Override
		public ClassVisitor transform(Task task, int api, ClassVisitor baseVisitor) {
			return new EventListenerTransformer(task, api, baseVisitor);
		}
	}
}
