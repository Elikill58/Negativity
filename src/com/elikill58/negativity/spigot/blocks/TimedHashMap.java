package com.elikill58.negativity.spigot.blocks;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import org.bukkit.Bukkit;

import com.elikill58.negativity.spigot.SpigotNegativity;

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
    	int ticks = (int) ((expiryInMillis * 20) / 1000);
    	Bukkit.getScheduler().runTaskTimerAsynchronously(SpigotNegativity.getInstance(), new CleanerThread(), ticks, ticks);
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