package com.elikill58.negativity.sponge.packets;

import java.lang.reflect.Field;
import java.util.HashMap;

public class PacketContent {

	private AbstractPacket packet;
	
	public PacketContent(AbstractPacket packet) {
		this.packet = packet;
	}
	
	public AbstractPacket getPacket() {
		return packet;
	}
	
	public <T> ContentModifier<T> getSpecificModifier(T type){
		return new ContentModifier<T>(packet.getPacket(), type.getClass());
	}
	
	public <T> ContentModifier<T> getSpecificModifier(Class<T> clazz){
		return new ContentModifier<T>(packet.getPacket(), clazz);
	}
	
	public ContentModifier<String> getStrings(){
		return new ContentModifier<String>(packet.getPacket(), String.class);
	}
	
	public ContentModifier<Byte> getBytes(){
		return new ContentModifier<Byte>(packet.getPacket(), byte.class);
	}
	
	public ContentModifier<Boolean> getBooleans(){
		return new ContentModifier<Boolean>(packet.getPacket(), boolean.class);
	}
	
	public ContentModifier<Integer> getIntegers(){
		return new ContentModifier<Integer>(packet.getPacket(), int.class);
	}
	
	public ContentModifier<byte[]> getByteArrays(){
		return new ContentModifier<byte[]>(packet.getPacket(), byte[].class);
	}
	
	public static class ContentModifier<T> {
		
		private Object obj;
		private HashMap<String, T> content = new HashMap<>();

		@SuppressWarnings("unchecked")
		public ContentModifier(Object obj, Class<?> clazz) {
			this.obj = obj;
			
			for(Field f : obj.getClass().getDeclaredFields()) {
				try {
					if(f.getType().isAssignableFrom(clazz)) {
						f.setAccessible(true);
						content.put(f.getName(), (T) f.get(obj));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		@SuppressWarnings("unchecked")
		public T read(int i) {
			return (T) content.values().toArray()[i];
		}
		
		public void write(int i, T value) {
			String key = content.keySet().toArray(new String[] {})[i];
			if(key == null) {
				new NoSuchFieldException("Not enough value in " + obj.getClass() + ".").printStackTrace();
			} else {
				content.put(key, value);
				try {
					Field f = obj.getClass().getDeclaredField(key);
					f.setAccessible(true);
					f.set(obj, value);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
