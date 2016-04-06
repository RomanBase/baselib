package com.base.lib.engine;

/**
 *
 */
public abstract class BaseCollection<T> extends BaseUpdateable {

    protected final Object[] objects;

    protected int size;
    protected int index;
    protected final int capacity;

    public BaseCollection(Base base, int capacity) {
        super(base);

        this.capacity = capacity;
        this.objects = new BaseRenderable[capacity];
    }

    public void add(T object) {

        objects[size] = object;
        size++;
    }

    public void add(T[] objects) {

        System.arraycopy(objects, 0, objects, size, objects.length);
        size += objects.length;
    }

    @SuppressWarnings("unchecked")
    public T[] getAll() {

        Object[] out = new Object[size];
        System.arraycopy(objects, 0, out, 0, size);

        return (T[]) out;
    }

    @SuppressWarnings("unchecked")
    public T get(int index) {

        return (T) objects[index];
    }

    public int getCapacity() {

        return capacity;
    }

    public boolean isEmpty() {

        return size == 0;
    }

    public void remove(T object) {

        for (int i = 0; i < size; i++) {
            if (objects[i].equals(object)) {
                removeFast(i);
                break;
            }
        }
    }

    protected void removeFast(int index) {

        objects[index] = objects[--size];
        objects[size] = null;
    }

    protected void removeStandart(int index) {

        int numMoved = size - index - 1;
        if (numMoved > 0)
            System.arraycopy(objects, index + 1, objects, index, numMoved);

        objects[--size] = null;
    }

    public void clear() {

        int count = size;
        size = 0;

        for (int i = 0; i < count; i++) {
            objects[i] = null;
        }
    }

    protected abstract void iterate(T[] objects);

    @Override
    @SuppressWarnings("unchecked")
    public void update() {

        iterate((T[]) objects);
    }

    @Override
    public void destroy() {

        clear();
    }
}
