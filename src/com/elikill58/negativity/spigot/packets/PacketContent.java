package com.elikill58.negativity.spigot.packets;

import java.lang.reflect.Field;
import java.util.HashMap;

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
		this.obj = packet.getPacket();
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
	
	public AbstractPacket getPacket() {
		return packet;
	}
	
	public <T> ContentModifier<T> getSpecificModifier(T type){
		return new ContentModifier<T>(obj, type.getClass());
	}
	
	public <T> ContentModifier<T> getSpecificModifier(Class<T> clazz){
		return new ContentModifier<T>(obj, clazz);
	}
	
	public ContentModifier<String> getStrings(){
		return new ContentModifier<String>(obj, String.class);
	}
	
	public ContentModifier<Byte> getBytes(){
		return new ContentModifier<Byte>(obj, byte.class);
	}
	
	public ContentModifier<Boolean> getBooleans(){
		return new ContentModifier<Boolean>(obj, boolean.class);
	}
	
	public ContentModifier<Integer> getIntegers(){
		return new ContentModifier<Integer>(obj, int.class);
	}
	
	public ContentModifier<byte[]> getByteArrays(){
		return new ContentModifier<byte[]>(obj, byte[].class);
	}
	
	public static class ContentModifier<T> {
		
		private Object obj;
		private HashMap<Field, T> content = new HashMap<>();

		public ContentModifier(Object obj, Class<?> clazz) {
			this.obj = obj;
			
			for(Field f : obj.getClass().getDeclaredFields()) {
				try {
					if(f.getType().isAssignableFrom(clazz)) {
						f.setAccessible(true);
						content.put(f, (T) f.get(obj));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if(!obj.getClass().getSuperclass().equals(Object.class)) {
				for(Field f : obj.getClass().getSuperclass().getDeclaredFields()) {
					try {
						if(f.getType().isAssignableFrom(clazz)) {
							f.setAccessible(true);
							content.put(f, (T) f.get(obj));
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
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
