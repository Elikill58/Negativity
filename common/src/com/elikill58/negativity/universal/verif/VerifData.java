package com.elikill58.negativity.universal.verif;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.elikill58.negativity.api.json.JSONObject;
import com.elikill58.negativity.universal.verif.data.DataCounter;

public class VerifData {

	private final HashMap<DataType<?>, DataCounter<?>> data = new HashMap<>();
	
	/**
	 * Get a data counter (or create a new one) of the specified datatype
	 * 
	 * @param <T> type of what is counted
	 * @param type the type of the data counter
	 * @return the data counter
	 */
	public <T extends Number> DataCounter<T> getData(DataType<T> type){
		return (DataCounter<T>) data.computeIfAbsent(type, DataType::create);
	}

	/**
	 * Get all data type and their counter
	 * 
	 * @return all data
	 */
	public HashMap<DataType<?>, DataCounter<?>> getAllData() {
		return data;
	}
	
	/**
	 * Check if the verif data have found something in their data counter
	 * 
	 * @return true if there is something
	 */
	public boolean hasSomething() {
		for(DataCounter<?> data : data.values())
			if(data.has())
				return true;
		return false;
	}
	
	/**
	 * Convert all data and their counter into a list of json object
	 * 
	 * @return all data in json
	 */
	public List<JSONObject> toJson() {
		List<JSONObject> list = new ArrayList<>();
		data.forEach((type, counter) -> {
			JSONObject jsonCounter = new JSONObject();
			jsonCounter.put("type", type.getName());
			jsonCounter.put("display", type.getDisplay());
			jsonCounter.put("data", counter.getTotal());
			list.add(jsonCounter);
		});
		return list;
	}

	public static class DataType<T extends Number> {
		
		private final DataTypeCallable<T> create;
		private final String name, display;
		
		/**
		 * Create a new data type
		 * @param name the name of the data type
		 * @param display the display name of the data type
		 * @param create the creator of data counter
		 */
		public DataType(String name, String display, DataTypeCallable<T> create) {
			this.name = name;
			this.display = display;
			this.create = create;
		}

		/**
		 * Get the name of the data type
		 * 
		 * @return the name
		 */
		public String getName() {
			return name;
		}

		/**
		 * Get the display of the data type
		 * 
		 * @return the display
		 */
		public String getDisplay() {
			return display;
		}

		/**
		 * Create a new data counter
		 * 
		 * @return a new data counter
		 */
		public DataCounter<T> create() {
			return create.call();
		}
		
		public interface DataTypeCallable<T extends Number> {
			DataCounter<T> call();
		}
		
		@Override
		public int hashCode() {
			return name.hashCode();
		}
	}
}
