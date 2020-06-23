package com.elikill58.negativity.universal.verif;

import java.util.HashMap;

import com.elikill58.negativity.universal.verif.data.DataCounter;

public class VerifData {

	private final HashMap<DataType<?>, DataCounter<?>> hash = new HashMap<>();
	
	@SuppressWarnings("unchecked")
	public <T> DataCounter<T> getData(DataType<T> type){
		return (DataCounter<T>) hash.computeIfAbsent(type, DataType::create);
	}

	public HashMap<DataType<?>, DataCounter<?>> getAllData() {
		return hash;
	}
	
	public boolean hasSomething() {
		for(DataCounter<?> data : hash.values())
			if(data.has())
				return true;
		return false;
	}

	public static class DataType<T> {
		
		private final DataTypeCallable<T> create;
		
		public DataType(DataTypeCallable<T> create) {
			this.create = create;
		}

		public DataCounter<T> create() {
			return create.call();
		}
		
		public interface DataTypeCallable<T> {
			DataCounter<T> call();
		}
	}
}
