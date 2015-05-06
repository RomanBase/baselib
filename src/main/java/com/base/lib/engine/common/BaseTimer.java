package com.base.lib.engine.common;

import com.base.lib.engine.BaseTime;
import com.base.lib.engine.BaseUpdateable;

/**
 *
 */
public abstract class BaseTimer extends BaseUpdateable{

    private long delay;
    private long currentDelay;

    public BaseTimer(long millis){
        delay = millis;
    }

    public abstract void onDone();

    public void reset(){
        inUse = true;
        currentDelay = 0;
    }

    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    public long getCurrentDelay() {
        return currentDelay;
    }

    public void setCurrentDelay(long currentDelay) {
        this.currentDelay = currentDelay;
    }

    @Override
    public void update() {

        if((currentDelay += BaseTime.delay) > delay){
            inUse = false;
            onDone();
        }
    }

    @Override
    public void destroy() {

    }
}
