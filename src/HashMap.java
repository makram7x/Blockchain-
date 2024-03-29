import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class HashMap<K, V> implements Serializable {
    private static final int DEFAULT_CAPACITY = 200;
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;
    private Set test;
    private int size;
    private int capacity;
    private float loadFactor;
    private Entry<K, V>[] table;

    public HashMap() {
        this(DEFAULT_CAPACITY, DEFAULT_LOAD_FACTOR);
    }

    @SuppressWarnings("unchecked")
    public HashMap(int capacity, float loadFactor) {
        this.size = 0;
        this.capacity = capacity;
        this.loadFactor = loadFactor;
        this.table = new Entry[capacity];
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public V get(K key) {
        int index = (getIndex(key) < 0) ? getIndex(key) * -1 : getIndex(key);
        Entry<K, V> entry = table[index];
        while (entry != null) {
            if (entry.key.equals(key)) {
                return entry.value;
            }
            entry = entry.next;
        }
        return null;
    }

    public void put(K key, V value) {

        int index = (getIndex(key) < 0) ? getIndex(key) * -1 : getIndex(key) ;

        Entry<K, V> entry = table[index];
        while (entry != null) {
            if (entry.key.equals(key)) {
                entry.value = value;
                return;
            }
            entry = entry.next;
        }
        table[index] = new Entry<K, V>(key, value, table[index]);
        size++;
        if (size > capacity * loadFactor) {
            resize();
        }
    }

    public Set<Entry<K, V>> entrySet() {
        Set<Entry<K, V>> set = new HashSet<>();
        for (int i = 0; i < table.length; i++) {
            Entry<K, V> entry = table[i];
            while (entry != null) {
                set.add(entry);
                entry = entry.next;
            }
        }
        return set;
    }

    public void remove(K key) {
        int index = (getIndex(key) < 0) ? getIndex(key) * -1 : getIndex(key) ;
        Entry<K, V> entry = table[index];
        if (entry == null) {
            return;
        }
        if (entry.key.equals(key)) {
            table[index] = entry.next;
            size--;
            return;
        }
        Entry<K, V> prev = entry;
        entry = entry.next;
        while (entry != null) {
            if (entry.key.equals(key)) {
                prev.next = entry.next;
                size--;
                return;
            }
            prev = entry;
            entry = entry.next;
        }
    }

    private int getIndex(K key) {
        return key.hashCode() % capacity;
    }

    private void resize() {
        capacity *= 2;
        Entry<K, V>[] oldTable = table;
        table = new Entry[capacity];
        for (int i = 0; i < oldTable.length; i++) {
            Entry<K, V> entry = oldTable[i];
            while (entry != null) {
                Entry<K, V> next = entry.next;
                int index = getIndex(entry.key);
                entry.next = table[index];
                table[index] = entry;
                entry = next;
            }
        }
    }
    public static class Entry<K, V> implements Serializable {
        K key;
        V value;
        Entry<K, V> next;

        public Entry(K key, V value, Entry<K, V> next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }

        public V getValue() {
            return value;
        }
    }
}
