package com.elikill58.orebfuscator.packets;

import java.lang.reflect.Field;
import java.util.HashMap;

import com.elikill58.orebfuscator.utils.ReflectionUtils;
import com.elikill58.orebfuscator.utils.Utils;

@SuppressWarnings("unchecked")
public class PacketContent {

	private AbstractPacket packet;
	
	public PacketContent(AbstractPacket packet) {
		this.packet = packet;
	}
	
	public AbstractPacket getPacket() {
		return packet;
	}
	
	public String getPlayerDigTypeName() {
		try {
			Class<?> packetDigTypeClass = ReflectionUtils.getFirstSubClassWithName(Class.forName("net.minecraft.server." + Utils.VERSION + ".PacketPlayInBlockDig"), "EnumPlayerDigType");
			for(Field f : packet.getClass().getDeclaredFields()) {
				if(f.getType().isAssignableFrom(packetDigTypeClass)) {
					f.setAccessible(true);
					Object digType = f.get(packet);
					return (String) digType.getClass().getDeclaredMethod("name").invoke(digType);
				}
			}
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public <T> ContentModifier<T> getSpecificModifier(T type){
		return new ContentModifier<T>(packet.getPacket(), type.getClass());
	}
	
	public <T> ContentModifier<T> getSpecificModifier(Class<T> clazz){
		return new ContentModifier<T>(packet.getPacket(), clazz);
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
		
		public T read(int i) {
			return (T) content.values().toArray()[i];
		}
		
		public void write(int i, T value) {
			String key = content.keySet().toArray(new String[] {})[i];
			if(key == null) {
				try {
					throw new NoSuchFieldException("Not enough value in " + obj.getClass() + ".");
				} catch (NoSuchFieldException e) {
					e.printStackTrace();
				}
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
