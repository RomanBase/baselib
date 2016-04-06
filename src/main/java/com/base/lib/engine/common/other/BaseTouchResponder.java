package com.base.lib.engine.common.other;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.base.lib.interfaces.BaseTouchListener;

/**
 * 17 Created by doctor on 9.2.14.
 */
public class BaseTouchResponder implements BaseTouchListener{

    private List<BaseTouchListener> listeners = new ArrayList<BaseTouchListener>();

    public BaseTouchResponder(BaseTouchListener... listeners){

        Collections.addAll(this.listeners, listeners);
    }

    public void add(BaseTouchListener listener){

        listeners.add(listener);
    }

    public void add(BaseTouchListener... listeners){

        Collections.addAll(this.listeners, listeners);
    }

    public void remove(BaseTouchListener listener){

        listeners.remove(listener);
    }

    public void clear(){

        listeners.clear();
    }

    @Override
    public void onTouchDown(int id, float x, float y) {

        for(BaseTouchListener listener : listeners){
            listener.onTouchDown(id, x, y);
        }
    }

    @Override
    public void onTouchUp(int id, float x, float y) {

        for(BaseTouchListener listener : listeners){
            listener.onTouchUp(id, x, y);
        }
    }

    @Override
    public void onTouchMove(int id, float x, float y) {

        for(BaseTouchListener listener : listeners){
            listener.onTouchMove(id, x, y);
        }
    }
}
