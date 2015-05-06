package com.base.lib.engine;

import java.util.Arrays;
import java.util.List;

/**
 * Be aware: Objects can't be in adding order !
 */
public class BaseRootCollection extends BaseUpdateable {

    protected final BaseRenderable[] renderables;

    protected int size;
    protected int index;
    protected final int capacity;

    public BaseRootCollection(int capacity){

        this.capacity = capacity;
        this.renderables = new BaseRenderable[capacity];
    }

    public void add(BaseRenderable object){

        renderables[size] = object;
        size++;
    }

    public void add(BaseRenderable[] objects){

        System.arraycopy(objects, 0, renderables, size, objects.length);
        size += objects.length;
    }

    public BaseRenderable[] getAll(){

        BaseRenderable[] out = new BaseRenderable[size];
        System.arraycopy(renderables, 0, out, 0, size);

        return out;
    }

    public List<BaseRenderable> getAllAsList(){

        return Arrays.asList(renderables);
    }

    public BaseRenderable get(int index){

        return renderables[index];
    }

    public int getCapacity() {

        return capacity;
    }

    public boolean isEmpty(){

        return size == 0;
    }

    public void remove(BaseRenderable object){

        for(int i = 0; i<size; i++){
            if(renderables[i].equals(object)){
                removeFast(i);
                break;
            }
        }
    }

    protected void removeFast(int index){

        renderables[index] = renderables[--size].reference();
        renderables[size] = null;
    }

    protected void removeStandart(int index){

        int numMoved = size - index - 1;
        if (numMoved > 0)
            System.arraycopy(renderables, index+1, renderables, index, numMoved);

        renderables[--size] = null;
    }

    @Override
    public void update(){

        while (index < size){
            BaseRenderable renderable = renderables[index];
            renderable.update();
            if(renderable.inUse) {
                Base.render.addRenderable(renderable);
                index++;
            } else {
                removeFast(index);
                renderable.destroy();
            }
        }

        index = 0;
    }

    public void updateToDraw(){

        while (index < size){
            BaseRenderable renderable = renderables[index];
            renderable.update();
            if(renderable.inUse) {
                BaseGL.useProgram(renderable.shader.glProgram);
                renderable.draw();
                index++;
            } else {
                removeFast(index);
                renderable.destroy();
            }
        }

        index = 0;
    }

    public void clear(){

        for(int i = 0; i<size; i++){
            renderables[i] = null;
        }
        size = 0;
    }

    @Override
    public void destroy(){

        int count = size;
        size = 0;

        for(int i = 0; i<count; i++){
            BaseRenderable renderable = renderables[i];
            if(renderable != null) {
                if(renderable.inUse) {
                    renderable.destroy();
                }
                renderables[i] = null;
            }
        }
    }
}