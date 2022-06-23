package com.elikill58.negativity.api.packets;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.NoSuchElementException;

import org.checkerframework.checker.nullness.qual.Nullable;

public class PacketContent {
	
	private final @Nullable AbstractPacket packet;
	private final Object obj;
	
	/**
	 * Create a PacketContent to easily read and write value in it
	 * 
	 * @param packet the Negativity's packet
	 */
	public PacketContent(AbstractPacket packet) {
		this.packet = packet;
		this.obj = packet.getNmsPacket();
	}
	
	/**
	 * Create a PacketContent to easily read and write to the specified object
	 * 
	 * @param obj the object that will be read or edit
	 */
	public PacketContent(Object obj) {
		this.packet = null;
		this.obj = obj;
	}
	
	/**
	 * Get the packet where the packet content come from.
	 * Can be null if we are checking an object and not the packet.
	 * 
	 * @return the analyzed packet
	 */
	public @Nullable AbstractPacket getPacket() {
		return packet;
	}
	
	/**
	 * Get content modifier of a custom type
	 * 
	 * @param <T> the type used in the content modifier
	 * @param type the searched type
	 * @return the content modifier of the type
	 */
	public <T> ContentModifier<T> getSpecificModifier(T type){
		return new ContentModifier<T>(obj, type.getClass());
	}

	/**
	 * Get content modifier of a custom type
	 * 
	 * @param <T> the type used in the content modifier
	 * @param clazz the searched type
	 * @return the content modifier of the type
	 */
	public <T> ContentModifier<T> getSpecificModifier(Class<T> clazz){
		return new ContentModifier<T>(obj, clazz);
	}
	
	/**
	 * Get content modifier of all string
	 * 
	 * @return string modifier
	 */
	public ContentModifier<String> getStrings(){
		return new ContentModifier<String>(obj, String.class);
	}

	/**
	 * Get content modifier of all bytes
	 * 
	 * @return byte modifier
	 */
	public ContentModifier<Byte> getBytes(){
		return new ContentModifier<Byte>(obj, byte.class);
	}

	/**
	 * Get content modifier of all booleans
	 * 
	 * @return boolean modifier
	 */
	public ContentModifier<Boolean> getBooleans(){
		return new ContentModifier<Boolean>(obj, boolean.class);
	}

	/**
	 * Get content modifier of all integers
	 * 
	 * @return int modifier
	 */
	public ContentModifier<Integer> getIntegers(){
		return new ContentModifier<Integer>(obj, int.class);
	}

	/**
	 * Get content modifier of all byte arrays
	 * 
	 * @return byte array modifier
	 */
	public ContentModifier<byte[]> getByteArrays(){
		return new ContentModifier<byte[]>(obj, byte[].class);
	}

	/**
	 * Get content modifier of all long
	 * 
	 * @return long modifier
	 */
	public ContentModifier<Long> getLongs(){
		return new ContentModifier<Long>(obj, long.class);
	}

	/**
	 * Get content modifier of all float
	 * 
	 * @return float modifier
	 */
	public ContentModifier<Float> getFloats(){
		return new ContentModifier<Float>(obj, float.class);
	}

	/**
	 * Get content modifier of all double
	 * 
	 * @return double modifier
	 */
	public ContentModifier<Double> getDoubles(){
		return new ContentModifier<Double>(obj, double.class);
	}

	/**
	 * Get content modifier of all object, ignoring type
	 * 
	 * @return object modifier
	 */
	public ContentModifier<Object> getAllObjects() {
		return new ContentModifier<Object>(obj, null);
	}
	
	public static class ContentModifier<T> {
		
		private Object obj;
		private HashMap<Field, T> content = new HashMap<>();

		@SuppressWarnings("unchecked")
		public ContentModifier(Object obj, @Nullable Class<?> clazz) {
			this.obj = obj;
			for(Field f : obj.getClass().getDeclaredFields()) {
				if(Modifier.isStatic(f.getModifiers()))
					continue;
				try {
					if(clazz == null || f.getType().isAssignableFrom(clazz)) {
						f.setAccessible(true);
						addIfNotNull(f, (T) f.get(obj));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if(!obj.getClass().getSuperclass().equals(Object.class)) {
				for(Field f : obj.getClass().getSuperclass().getDeclaredFields()) {
					if(Modifier.isStatic(f.getModifiers()))
						continue;
					try {
						if(clazz == null || f.getType().isAssignableFrom(clazz)) {
							f.setAccessible(true);
							addIfNotNull(f, (T) f.get(obj));
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		private void addIfNotNull(Field f, T o) {
			if(o != null)
				content.put(f, o);
		}
		
		/**
		 * Get the size of the Map of content
		 * 
		 * @return the number of content
		 */
		public int size() {
			return content.size();
		}

		/**
		 * Get the value of the field which is named by the given one
		 * 
		 * @param fieldName the name of the needed field
		 * @return the value of the field
		 */
		public T read(String fieldName) {
			for(Field field : content.keySet())
				if(field.getName().equalsIgnoreCase(fieldName))
					return content.get(field);
			return null;
		}
		
		/**
		 * Get the value of the field which is named by the given one
		 * 
		 * @param fieldName the name of the needed field
		 * @param defaultValue the returned value if there is no value
		 * @return the value of the field
		 */
		public T read(String fieldName, T defaultValue) {
			for(Field field : content.keySet())
				if(field.getName().equalsIgnoreCase(fieldName))
					return content.get(field);
			return defaultValue;
		}
		
		/**
		 * Read the specified value.
		 * 
		 * @param i the number of the value to get getted. Start at 0.
		 * @return the requested value.
		 * 
		 * @throws NoSuchElementException if the value doesn't exist
		 */
		public T read(int i) {
			return content.values().stream().findFirst().get();
		}
		
		/**
		 * Read the specified value.
		 * 
		 * @param i the number of the value to get getted. Start at 0.
		 * @return the requested value, or null if it doesn't exist.
		 */
		public T readSafely(int i) {
			return readSafely(i, null);
		}
		
		/**
		 * Read the specified value.
		 * 
		 * @param i the number of the value to get getted. Start at 0.
		 * @param defaultValue the value which is return if the key doesn't exist
		 * @return the requested value.
		 */
		public T readSafely(int i, T defaultValue) {
			return content.values().stream().findFirst().orElse(defaultValue);
		}
		
		/**
		 * Set the value in the specified object.
		 * 
		 * @param i the key of the value to set
		 * @param value the new value which will be set
		 */
		public void write(int i, T value) {
			Field key = content.keySet().toArray(new Field[] {})[i];
			if(key == null) {
				new NoSuchFieldException("Not enough value in " + obj.getClass() + ".").printStackTrace();
			} else {
				content.put(key, value);
				try {
					key.set(obj, value);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		/**
		 * Get field which her value
		 * 
		 * @return all content
		 */
		public HashMap<Field, T> getContent(){
			return content;
		}
	}
}
