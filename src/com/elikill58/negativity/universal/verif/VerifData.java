package com.elikill58.negativity.universal.verif;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.json.simple.JSONObject;

import com.elikill58.negativity.universal.verif.data.DataCounter;
import com.elikill58.negativity.universal.verif.data.DoubleDataCounter;
import com.elikill58.negativity.universal.verif.data.IntegerDataCounter;
import com.elikill58.negativity.universal.verif.data.LongDataCounter;

public class VerifData {

	@SuppressWarnings("rawtypes")
	private final HashMap<DataType, DataCounter> hash = new HashMap<>();
	
	public VerifData() {
		for(DataType<?> data : DataType.DATA_TYPES)
			hash.put(data, data.create(null));
	}
	
	public DataType<?> getType(String name){
		for(DataType<?> data : DataType.DATA_TYPES)
			if(data.getKey().equalsIgnoreCase(name))
				return data;
		return null;
	}
	
	public void addObj(JSONObject json) {
		String name = json.get("type").toString();
		for(DataType<?> data : DataType.DATA_TYPES)
			if(data.getKey().equalsIgnoreCase(name))
				hash.put(data, data.create(json));
	}
	
	@SuppressWarnings("unchecked")
	public <T> DataCounter<T> getData(DataType<T> type){
		return hash.get(type);
	}

	@SuppressWarnings("rawtypes")
	public HashMap<DataType, DataCounter> getAllData() {
		return hash;
	}
	
	public boolean hasSomething() {
		for(DataCounter<?> data : hash.values())
			if(data.has())
				return true;
		return false;
	}

	public static class DataType<T> {
		
		private String key;
		
		public DataType(String key) {
			this.key = key;
		}

		public String getKey() {
			return key;
		}
		
		public DataCounter<T> create(JSONObject json) {
			return null;
		}
		
		public static final DataType<Integer> INTEGER = new DataType<Integer>("integer") {
			@Override public DataCounter<Integer> create(JSONObject json) { return new IntegerDataCounter(json, getKey()); }
		};
		
		public static final DataType<Double> DOUBLE = new DataType<Double>("double") {
			@Override public DataCounter<Double> create(JSONObject json) { return new DoubleDataCounter(json, getKey()); }
		};
		
		public static final DataType<Long> LONG = new DataType<Long>("long") {
			@Override public DataCounter<Long> create(JSONObject json) { return new LongDataCounter(json, getKey()); }
		};

		private static final List<DataType<?>> DATA_TYPES = Arrays.asList(INTEGER, DOUBLE, LONG);
	}
}
