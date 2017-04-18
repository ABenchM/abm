package de.fraunhofer.abm.http.client.cache;

import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Cache<K, V> implements Map<K, V> {

    private static transient Logger logger = LoggerFactory.getLogger(Cache.class);

    private int maxSize;

    private int expiringPeriod;

    private TimeUnit timeUnit;

    private String name;

    private Hashtable<K, V> hashtable = new Hashtable<>();

    private Map<K, Long> insertTime = new HashMap<>();

    private Map<K, Long> lastUsed = new HashMap<>();

    public Cache(int maxSize, int expiringPeriod, TimeUnit timeUnit) {
        this("Unnamed Cache", maxSize, expiringPeriod, timeUnit);
    }

    public Cache(String name, int maxSize, int expiringPeriod, TimeUnit timeUnit) {
        this.name = name;
        this.maxSize = maxSize;
        this.expiringPeriod = expiringPeriod;
        this.timeUnit = timeUnit;
    }

    @Override
    public boolean containsKey(Object key) {
        if (hashtable.containsKey(key)) {
            if (isExpired(key)) {
                remove(key);
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    @Override
    public boolean containsValue(Object value) {
        for (Entry<K, V> entry : entrySet()) {
            if (entry.getValue().equals(value)) {
                K key = entry.getKey();
                if (isExpired(key)) {
                    remove(key);
                    return false;
                } else {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public Set<java.util.Map.Entry<K, V>> entrySet() {
        evict();
        return hashtable.entrySet();
    }

    private void evict() {
        // remove too old values
        for (Iterator<Entry<K, V>> iterator = hashtable.entrySet().iterator(); iterator.hasNext();) {
            Entry<K, V> entry = iterator.next();
            K key = entry.getKey();
            if (isExpired(key)) {
                logger.trace("[{}] Evicting {}. It got too old.", name, key);
                iterator.remove();
                lastUsed.remove(key);
                insertTime.remove(key);
            }
        }
    }

    private boolean isExpired(Object key) {
        long _insertTime = insertTime.get(key);
        long now = System.currentTimeMillis();
        return (now - _insertTime) > timeUnit.toMillis(expiringPeriod);
    }

    @Override
    @SuppressWarnings("unchecked")
    public V get(Object key) {
        V v = hashtable.get(key);
        if (v != null) {
            if (isExpired(key)) {
                remove(key);
                return null;
            } else {
                lastUsed.put((K) key, System.currentTimeMillis());
                return v;
            }
        } else {
            return null;
        }
    }

    @Override
    public Set<K> keySet() {
        evict();
        return hashtable.keySet();
    }

    @Override
    public V put(K key, V value) {
        long now = System.currentTimeMillis();
        insertTime.put(key, now);
        lastUsed.put(key, now);
        V v = hashtable.put(key, value);
        if (hashtable.size() > maxSize) {
            evict();
            if (hashtable.size() > maxSize) {
                removeLRU();
            }
        }
        return v;
    }

    private void removeLRU() {
        // remove least recently used value
        long lruTime = Long.MAX_VALUE;
        K lruKey = null;
        for (Entry<K, Long> entry : lastUsed.entrySet()) {
            if (entry.getValue() < lruTime) {
                lruTime = entry.getValue();
                lruKey = entry.getKey();
            }
        }

        logger.trace("[{}] Removing LRU {}.", name, lruKey);
        remove(lruKey);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        for (Entry<? extends K, ? extends V> entry : m.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public synchronized boolean isEmpty() {
        evict();
        return hashtable.isEmpty();
    }

    @Override
    public V remove(Object key) {
        V v = hashtable.remove(key);
        insertTime.remove(key);
        lastUsed.remove(key);
        return v;
    }

    @Override
    public int size() {
        evict();
        return hashtable.size();
    }

    @Override
    public Collection<V> values() {
        evict();
        return hashtable.values();
    }

    @Override
    public void clear() {
        hashtable.clear();
        lastUsed.clear();
        insertTime.clear();
    }
}