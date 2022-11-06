package com.elikill58.negativity.api;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import com.elikill58.negativity.universal.Adapter;

/**
 * A map that have timed content to let expire content
 * <br>
 * Source: https://stackoverflow.com/a/36418233/10952503
 * 
 * @author Vivekananthan M
 *
 * @param <K> the type of key
 * @param <V> the type of value
 */
public class TimedHashMap<K, V> extends ConcurrentHashMap<K, V> {

	private static final long serialVersionUID = 1L;

    private Map<K, Long> timeMap = new ConcurrentHashMap<K, Long>();
    private long expiryInMillis = 1000;

    public TimedHashMap() {
        initialize();
    }

    public TimedHashMap(long expiryInMillis) {
        this.expiryInMillis = expiryInMillis;
        initialize();
    }

    void initialize() {
    	Adapter.getAdapter().getScheduler().runRepeatingAsync(new CleanerThread(), Duration.ofMillis(expiryInMillis), Duration.ofMillis(expiryInMillis), "clear-map");
    }

    @Override
	public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
    	V obj = get(key);
        if (obj == null) {
        	obj = mappingFunction.apply(key);
        	put(key, obj);
        }
        return obj;
	}
    
    @Override
    public V put(K key, V value) {
        timeMap.put(key, System.currentTimeMillis());
        return super.put(key, value);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        for (K key : m.keySet()) {
            put(key, m.get(key));
        }
    }

    @Override
    public V putIfAbsent(K key, V value) {
        if (!containsKey(key))
            return put(key, value);
        else
            return get(key);
    }

    class CleanerThread implements Runnable {
        @Override
        public void run() {
            long currentTime = System.currentTimeMillis();
            for (K key : timeMap.keySet()) {
                if (currentTime > (timeMap.get(key) + expiryInMillis)) {
                    timeMap.remove(key);
                }
            }
        }
    }
}