package com.elikill58.negativity.api.events;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map.Entry;

import com.elikill58.negativity.api.commands.CommandManager;
import com.elikill58.negativity.api.inventory.InventoryManager;
import com.elikill58.negativity.common.ConnectionManager;
import com.elikill58.negativity.common.FightListeners;
import com.elikill58.negativity.common.GameEventsManager;
import com.elikill58.negativity.common.PacketListener;
import com.elikill58.negativity.common.ProxyEventsManager;
import com.elikill58.negativity.universal.Adapter;

public class EventManager {

	private static final HashMap<Class<?>, HashMap<ListenerCaller, EventListener>> EVENT_METHOD = new HashMap<>();

	public static void load() {
		EVENT_METHOD.clear();
		
		registerEvent(new CommandManager());
		registerEvent(new InventoryManager());
		registerEvent(new ConnectionManager());
		registerEvent(new PacketListener());
		registerEvent(new FightListeners());
		if(Adapter.getAdapter().getPlatformID().isProxy())
			registerEvent(new ProxyEventsManager());
		else
			registerEvent(new GameEventsManager());
	}
	
	/**
	 * Add all event in the given class
	 * Warn: all registered event are removed when doing /negativity reload.
	 *
	 * @param src the given object which contains event method
	 */
	public static void registerEvent(Listeners src) {
		/*if (src instanceof BakedListeners) {
			((BakedListeners) src).bakeListeners((eventClass, listener) -> registerListener((Listener) listener, (Class) eventClass));
			return;
		}*/
		
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
		HashMap<ListenerCaller, EventListener> allCall = EVENT_METHOD.get(src.getClass());
		if(allCall == null)
			return;
		synchronized (allCall) {
			for(Entry<ListenerCaller, EventListener> entries : allCall.entrySet()) {
				ListenerCaller call = entries.getKey();
				// TODO allow any Listener implementation to be unregistered
				if(call instanceof ReflectionBasedListener && ((ReflectionBasedListener) call).source == src) {
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
			EventListener eventListener = m.getAnnotation(EventListener.class);
			if (eventListener == null) {
				continue;
			}
			if(m.getParameterCount() > 1)
				Adapter.getAdapter().getLogger().error("Too many arguments for method " + m.getName() + " in " + clazz.getCanonicalName());
			else if(m.getParameterCount() == 0)
				Adapter.getAdapter().getLogger().error("You forget the event for the method " + m.getName() + " in " + clazz.getCanonicalName());
			else {
				Class<?> paramEvent = m.getParameterTypes()[0];
				if(isAssignableFrom(paramEvent)) {
					ListenerCaller listener = new HandleBasedListener(MethodHandles.lookup().unreflect(m), src);
					registerListener(listener, (Class<Event>) paramEvent, eventListener);
				} else {
					Adapter.getAdapter().getLogger().error(paramEvent.getCanonicalName() + " isn't an Event ! (Located at " + clazz.getCanonicalName() + ")");
				}
			}
		}
	}
	
	public static void registerListener(ListenerCaller listener, Class<Event> paramEvent, EventListener eventListener) {
		EVENT_METHOD.computeIfAbsent(paramEvent, c -> new HashMap<>()).put(listener, eventListener);
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
		HashMap<ListenerCaller, EventListener> allMethods = new HashMap<>();
		allMethods.putAll(getEventForClass(ev, ev.getClass()));
		Class<?> superClass = ev.getClass().getSuperclass();
		while(superClass != Object.class) {
			allMethods.putAll(getEventForClass(ev, superClass));
			superClass = superClass.getSuperclass();
		}
		HashMap<ListenerCaller, EventListener> map = new HashMap<>(allMethods);
		if(ev instanceof CancellableEvent) {
			CancellableEvent cancel = (CancellableEvent) ev;
			callEvent(cancel, EventPriority.PRE, map);
			if(cancel.isCancelled()) // if pre event cancel
				return;
			callEvent(cancel, EventPriority.BASIC, map);
			if(cancel.isCancelled()) // if basic event cancel
				return;
			callEvent(cancel, EventPriority.POST, map);
		} else {
			callEvent(ev, EventPriority.PRE, map);
			callEvent(ev, EventPriority.BASIC, map);
			callEvent(ev, EventPriority.POST, map);
		}
	}
	
	private static void callEvent(Event ev, EventPriority priority, HashMap<ListenerCaller, EventListener> allMethods) {
		allMethods.forEach((method, tag) -> {
			try {
				if(tag.priority().equals(priority)) {
					method.call(ev);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	private static HashMap<ListenerCaller, EventListener> getEventForClass(Event ev, Class<?> clazz) {
		return (HashMap<ListenerCaller, EventListener>) (Object) EVENT_METHOD.getOrDefault(clazz, new HashMap<>());
	}
	
	public static class ReflectionBasedListener implements ListenerCaller {
		private final Method method;
		private final Object source;
		
		public ReflectionBasedListener(Method method, Object src) {
			this.method = method;
			this.source = src;
		}
		
		@Override
		public void call(Event ev) {
			try {
				method.invoke(source, ev);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static class HandleBasedListener implements ListenerCaller {
		
		private final MethodHandle method;
		private final Object source;
		
		public HandleBasedListener(MethodHandle method, Object src) {
			this.method = method;
			this.source = src;
		}
		
		@Override
		public void call(Event ev) {
			try {
				method.invoke(source, ev);
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}
}
