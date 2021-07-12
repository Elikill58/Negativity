package com.elikill58.negativity.api.events;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.elikill58.negativity.api.commands.CommandManager;
import com.elikill58.negativity.api.inventory.InventoryManager;
import com.elikill58.negativity.common.ConnectionManager;
import com.elikill58.negativity.universal.Adapter;

public class EventManager {

	private static final HashMap<Class<?>, List<Listener<?>>> EVENT_METHOD = new HashMap<>();

	public static void load() {
		EVENT_METHOD.clear();
		
		registerEvent(new CommandManager());
		registerEvent(new InventoryManager());
		registerEvent(new ConnectionManager());
	}
	
	/**
	 * Add all event in the given class
	 * Warn: all registered event are removed when doing /negativity reload.
	 *
	 * @param src the given object which contains event method
	 */
	@SuppressWarnings({"unchecked", "rawtypes"})
	public static void registerEvent(Listeners src) {
		if (src instanceof BakedListeners) {
			((BakedListeners) src).bakeListeners((eventClass, listener) -> registerListener((Listener) listener, (Class) eventClass));
			return;
		}
		
		try {
			checkClass(src);
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Remove all event for the specified instance
	 *
	 * @param src the instance that will not longer be in listener
	 */
	public static void unregisterEvent(Listeners src) {
		List<Listener<?>> allCall = EVENT_METHOD.get(src.getClass());
		if(allCall == null)
			return;
		synchronized (allCall) {
			for(Listener<?> call : allCall) {
				// TODO allow any Listener implementation to be unregistered
				if(call instanceof ReflectionBasedListener && ((ReflectionBasedListener<?>) call).source == src) {
					allCall.remove(call); // remove concerned call
				}
			}
		}
	}
	
	/**
	 * Remove all event for the specified class, including ALL instance
	 *
	 * @param clazz the class removed
	 */
	public static void unregisterEventForClass(Class<?> clazz) {
		EVENT_METHOD.remove(clazz);
	}
	
	@SuppressWarnings("unchecked")
	private static void checkClass(Listeners src) throws InstantiationException, IllegalAccessException {
		Class<?> clazz = src.getClass();
		for(Method m : clazz.getDeclaredMethods()) {
			if(!m.isAnnotationPresent(EventListener.class))
				continue;
			if(m.getParameterCount() > 1)
				Adapter.getAdapter().getLogger().error("Too many arguments for method " + m.getName() + " in " + clazz.getCanonicalName());
			else if(m.getParameterCount() == 0)
				Adapter.getAdapter().getLogger().error("You forget the event for the method " + m.getName() + " in " + clazz.getCanonicalName());
			else {
				Class<?> paramEvent = m.getParameterTypes()[0];
				if(isAssignableFrom(paramEvent)) {
					Listener<Event> listener = new HandleBasedListener<>(MethodHandles.lookup().unreflect(m), src);
					registerListener(listener, (Class<Event>) paramEvent);
				} else {
					Adapter.getAdapter().getLogger().error(paramEvent.getCanonicalName() + " isn't an Event ! (Located at " + clazz.getCanonicalName() + ")");
				}
			}
		}
	}
	
	public static <E extends Event> void registerListener(Listener<E> listener, Class<E> paramEvent) {
		List<Listener<?>> list = EVENT_METHOD.computeIfAbsent(paramEvent, c -> new ArrayList<>());
		list.add(listener);
		EVENT_METHOD.put(paramEvent, list);
	}
	
	private static boolean isAssignableFrom(Class<?> clazz) {
		try {
			if(clazz.isAssignableFrom(Event.class))
				return true;
			for(Type type : clazz.getGenericInterfaces()) {
				if(type.getTypeName().equals(Event.class.getCanonicalName()))
					return true;
				else if(isAssignableFrom(Class.forName(type.getTypeName())))
					return true;
			}
			if(!clazz.getSuperclass().equals(Object.class))
				return isAssignableFrom(clazz.getSuperclass());
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Call an event
	 *
	 * @param ev the event which have to be called
	 */
	public static void callEvent(Event ev) {
		runEventForClass(ev, ev.getClass());
		Class<?> superClass = ev.getClass().getSuperclass();
		if(!superClass.equals(Object.class)) {
			runEventForClass(ev, superClass);
		}
	}
	
	@SuppressWarnings("unchecked")
	private static void runEventForClass(Event ev, Class<?> clazz) {
		List<Listener<Event>> methods = (List<Listener<Event>>) (Object) EVENT_METHOD.get(clazz);
		if(methods != null) {
			new ArrayList<>(methods).forEach((m) -> {
				try {
					m.call(ev);
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
		}
	}
	
	public static class ReflectionBasedListener<E extends Event> implements Listener<E> {
		private final Method method;
		private final Object source;
		
		public ReflectionBasedListener(Method method, Object src) {
			this.method = method;
			this.source = src;
		}
		
		@Override
		public void call(E ev) {
			try {
				method.invoke(source, ev);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static class HandleBasedListener<E extends Event> implements Listener<E> {
		private final MethodHandle method;
		private final Object source;
		
		public HandleBasedListener(MethodHandle method, Object src) {
			this.method = method;
			this.source = src;
		}
		
		@Override
		public void call(E ev) {
			try {
				method.invoke(source, ev);
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}
}
