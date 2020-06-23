package com.elikill58.negativity.universal.verif;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.simple.JSONObject;

import com.elikill58.negativity.universal.verif.data.DataCounter;

public class VerifData {

	private final HashMap<DataType<?>, DataCounter<?>> data = new HashMap<>();
	
	@SuppressWarnings("unchecked")
	public <T> DataCounter<T> getData(DataType<T> type){
		return (DataCounter<T>) data.computeIfAbsent(type, DataType::create);
	}

	public HashMap<DataType<?>, DataCounter<?>> getAllData() {
		return data;
	}
	
	public boolean hasSomething() {
		for(DataCounter<?> data : data.values())
			if(data.has())
				return true;
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public List<String> toJson() {
		List<String> list = new ArrayList<>();
		data.forEach((type, counter) -> {
			JSONObject jsonCounter = new JSONObject();
			jsonCounter.put("type", type.getName());
			jsonCounter.put("display", type.getDisplay());
			jsonCounter.put("data", counter.getList().toString());
			list.add(jsonCounter.toJSONString());
		});
		return list;
	}

	public static class DataType<T> {
		
		private final DataTypeCallable<T> create;
		private final String name, display;
		
		public DataType(String name, String display, DataTypeCallable<T> create) {
			this.name = name;
			this.display = display;
			this.create = create;
		}

		public String getName() {
			return name;
		}

		public String getDisplay() {
			return display;
		}

		public DataCounter<T> create() {
			return create.call();
		}
		
		public interface DataTypeCallable<T> {
			DataCounter<T> call();
		}
	}
}
