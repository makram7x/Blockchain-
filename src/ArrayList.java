import java.io.Serializable;
import java.util.Arrays;

class ArrayList<T> implements Serializable {
    private static final int DEFAULT_CAPACITY = 10;
    private T[] items;
    private int size;

    @SuppressWarnings("unchecked")
    public ArrayList(int capacity) {
        items = (T[]) new Object[capacity];
        size = 0;
    }

    public ArrayList() {
        this(DEFAULT_CAPACITY);
    }

    public void add(T item) {
        if (size == items.length) {
            resize(2 * items.length);
        }
        items[size++] = item;
    }

    public T get(int index) {
        if (index < 0 || index >= size) {
            throw new ArrayIndexOutOfBoundsException();
        }
        return items[index];
    }

    public T remove(int index) {
        if (index < 0 || index >= size) {
            throw new ArrayIndexOutOfBoundsException();
        }
        T item = items[index];
        for (int i = index; i < size - 1; i++) {
            items[i] = items[i + 1];
        }
        items[--size] = null;
        if (size > 0 && size == items.length / 4) {
            resize(items.length / 2);
        }
        return item;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    @SuppressWarnings("unchecked")
    private void resize(int newCapacity) {
        T[] newArray = (T[]) new Object[newCapacity];
        for (int i = 0; i < size; i++) {
            newArray[i] = items[i];
        }
        items = newArray;
    }

    public T[] getItems(){
        return this.items;
    }

    @Override
    public String toString() {
        return  Arrays.toString(items);
    }
}
