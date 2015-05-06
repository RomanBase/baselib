package com.base.lib.engine.common;

/**
 *
 */
public abstract class BaseObjectPool<T> {

    private final int capacity;
    private final Object[] objects;
    private int index;

    public BaseObjectPool(int capacity){

        objects = new Object[capacity];

        this.capacity = capacity;
        this.index = -1;

        init(objects);
    }

    public abstract void init(final Object[] objects);

    @SuppressWarnings("unchecked")
    public T next(){

        if(++index == capacity){
            index = 0;
        }

        return (T)objects[index];
    }

    public int getCapacity(){

        return capacity;
    }

    public int getCurrentIndex(){

        return index;
    }

    public void setCurrentIndex(int index){

        this.index = index;
    }
}
