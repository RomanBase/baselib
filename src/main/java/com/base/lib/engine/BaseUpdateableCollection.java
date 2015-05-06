package com.base.lib.engine;

import java.util.Arrays;
import java.util.List;

/**
 * Be aware: Objects can't be in adding order !
 */
public class BaseUpdateableCollection extends BaseUpdateable {

    protected final BaseUpdateable[] updateables;

    protected int size;
    protected int index;

    public BaseUpdateableCollection(int capacity){

        this.updateables = new BaseUpdateable[capacity];
    }

    public void add(BaseUpdateable object){

        updateables[size++] = object;
    }

    public void add(BaseUpdateable... objects){

        if(objects == null){
            return;
        }

        for (BaseUpdateable object : objects) {
            add(object);
        }
    }

    public void addAtFront(BaseUpdateable object){

        System.arraycopy(updateables, 0, updateables, 1, size++);
        updateables[0] = object;
    }

    public BaseUpdateable[] getAll(){

        BaseUpdateable[] out = new BaseUpdateable[size];
        System.arraycopy(updateables, 0, out, 0, size);

        return out;
    }

    public List<BaseUpdateable> getAllAsList(){

        return Arrays.asList(updateables);
    }

    public void remove(BaseUpdateable object){

        for(int i = 0; i<size; i++){
            if(updateables[i].equals(object)){
                removeStandart(i);
                break;
            }
        }
    }

    private void removeFast(int index){

        updateables[index] = updateables[--size].reference();
        updateables[size] = null;
    }

    private void removeStandart(int index){

        int numMoved = size - index - 1;
        if (numMoved > 0)
            System.arraycopy(updateables, index+1, updateables, index, numMoved);

        updateables[--size] = null;
    }

    @Override
    public void update(){

        while (index < size){
            BaseUpdateable updateable = updateables[index];
            if(updateable.inUse) {
                updateable.update();
                index++;
            } else {
                removeStandart(index);
                updateable.destroy();
            }
        }

        index = 0;
    }

    public void clear(){

        for(int i = 0; i<size; i++){
            updateables[i] = null;
        }
        size = 0;
    }

    @Override
    public void destroy(){

        int count = size;
        size = 0;

        for(int i = 0; i<count; i++){
            if(updateables[i] != null) {
                updateables[i].destroy();
                updateables[i] = null;
            }
        }
    }
}