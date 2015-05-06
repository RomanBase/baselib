package com.base.lib.engine.controls;

import android.os.SystemClock;

import com.base.lib.engine.BaseCamera;
import com.base.lib.engine.BaseRenderable;
import com.base.lib.engine.BaseShader;
import com.base.lib.interfaces.BaseTouchListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 */
public class BaseUILayer extends BaseRenderable implements BaseTouchListener {

    protected List<BaseUIItem> listeners;

    protected long touchDownTime;
    protected float lastTouchedPosX, lastTouchedPosY;
    protected float xmove, ymove, moveRatio;

    private int touchID;
    private boolean strictMode;
    private boolean dontCare;
    private long downUpDelay;
    private float maxDist;

    private boolean locked;

    public BaseUILayer(BaseCamera camera, BaseUIItem... items) {
        this.listeners = Collections.synchronizedList(new ArrayList<BaseUIItem>(64));
        this.camera = camera;
        strictMode = false;
        dontCare = true;
        locked = false;
        moveRatio = 2.5f;
        touchID = -1;
        add(items);
    }

    public void add(BaseUIItem item) {

        synchronized (listeners) {
            item.setCamera(camera);
            item.setShader(shader);
            listeners.add(item);
        }
    }

    public void add(BaseUIItem... items) {

        if (items != null) {
            for (BaseUIItem listener : items) {
                add(listener);
            }
        }
    }

    public void addUnder(BaseUIItem item, float spacing) {

        add(item);
        BaseUIItem last = listeners.get(listeners.size());
        item.updatePosition(last.getX(), last.getY() + last.gethHeight() + item.gethHeight() + spacing);
    }

    public void addUnder(float spacing, BaseUIItem... items) {

        if (items != null) {
            for (BaseUIItem listener : items) {
                addUnder(listener, spacing);
            }
        }
    }

    public void remove(BaseUIItem listener) {

        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    public void clear() {

        for (BaseUIItem item : listeners) {
            item.destroy();
        }
        listeners.clear();
    }

    public void clear(int offset, boolean destroy) {

        clear(offset, listeners.size(), destroy);
    }

    public void clear(int offset, int count, boolean destroy) {

        if (offset >= count)
            return;

        List<BaseUIItem> temp = new ArrayList<BaseUIItem>();
        for (int i = 0; i < offset; i++) {
            temp.add(listeners.get(i));
        }

        if (destroy) {
            for (int i = offset; i < count; i++) {
                listeners.get(i).destroy();
            }
        }

        listeners = temp;
    }

    public BaseUIItem getItem(int index) {

        return listeners.get(index);
    }

    public List<BaseUIItem> getItems() {

        return listeners;
    }

    public void enableStrictMode() {

        strictMode = true;
    }

    public void setSelectableProperty(long downUpDelayMillis, float maxDist) {

        dontCare = false;
        this.downUpDelay = downUpDelayMillis;
        this.maxDist = maxDist;
    }

    public void setDontCareSelectableProperty() {

        dontCare = true;
    }

    public void setClassicSelectableProperty() {

        dontCare = false;
        downUpDelay = 100;
        maxDist = camera.getSmallerSideSize() * 0.025f;
    }

    public void setMoveRatio(float ratio) {

        moveRatio = ratio;
    }

    public boolean isSelectable() {

        return dontCare || (SystemClock.uptimeMillis() - touchDownTime < downUpDelay && Math.sqrt(xmove * xmove + ymove * ymove) < maxDist);
    }

    public boolean isTouched() {

        return touchID > -1;
    }

    public void untouch() {

        touchID = -1;
    }

    public void lock() {

        locked = true;
    }

    public void unlock() {

        locked = false;
    }

    @Override
    public void onTouchDown(int id, float x, float y) {

        if (locked || (strictMode && touchID != -1)) {
            return;
        }

        touchID = id;
        touchDownTime = SystemClock.uptimeMillis();
        x *= camera.getRatioX();
        y *= camera.getRatioY();
        lastTouchedPosX = x;
        lastTouchedPosY = y;
        xmove = 0.0f;
        ymove = 0.0f;
        synchronized (listeners) {
            for (BaseTouchListener listener : listeners) {
                listener.onTouchDown(id, x, y);
            }
        }
    }

    @Override
    public void onTouchUp(int id, float x, float y) {

        if (locked || (strictMode && touchID != id)) {
            return;
        }

        touchID = -1;
        x *= camera.getRatioX();
        y *= camera.getRatioY();
        lastTouchedPosX = x;
        lastTouchedPosY = y;
        if (isSelectable()) {
            synchronized (listeners) {
                for (BaseTouchListener listener : listeners) {
                    listener.onTouchUp(id, x, y);
                }
            }
        } else {
            synchronized (listeners) {
                for (BaseUIItem listener : listeners) {
                    listener.goUp(false);
                }
            }
        }
    }

    @Override
    public void onTouchMove(int id, float x, float y) {

        if (locked || (strictMode && touchID != id)) {
            return;
        }

        x *= camera.getRatioX();
        y *= camera.getRatioY();
        xmove = ((x - lastTouchedPosX) * moveRatio + xmove) * 0.5f;
        ymove = ((y - lastTouchedPosY) * moveRatio + ymove) * 0.5f;
        lastTouchedPosX = x;
        lastTouchedPosY = y;

        synchronized (listeners) {
            for (BaseTouchListener listener : listeners) {
                listener.onTouchMove(id, x, y);
            }
        }
    }

    @Override
    public void draw() {

        for (BaseRenderable listener : listeners) {
            listener.draw();
        }
    }

    public void secondaryDrawPass() {

        for (BaseUIItem listener : listeners) {
            listener.secondaryDrawPass();
        }
    }

    @Override
    public void update() {

        for (BaseRenderable listener : listeners) {
            listener.update();
        }
    }

    @Override
    public void setShader(BaseShader shader) {

        for (BaseRenderable listener : listeners) {
            listener.setShader(shader);
        }
        super.setShader(shader);
    }

    /**
     * @param lastItemIndex  very bottom item
     * @param firstItemIndex very top item
     * @param rollUp         bottom border line (usually negative value) - item center position value is compared
     * @param rollDown       top border line (usually positive value) - item center position value is compared
     *                       <p>note: lastItemIndex corresponds to rollUp value<p/>
     */
    protected void adjustVerticalPositions(int lastItemIndex, int firstItemIndex, float rollUp, float rollDown) {

        long delay = SystemClock.uptimeMillis() - touchDownTime;
        boolean adjust = ymove == 0.0f && delay > downUpDelay;
        boolean onAdjust = false;

        if (ymove > 0 || adjust) { // roll up (checking bottom border)
            float y = listeners.get(lastItemIndex).getY();
            if (y > rollUp) {
                ymove = (rollUp - y) * 0.25f;
                onAdjust = true;
            }
        }

        if (ymove < 0 || adjust) { // roll down (checking top border)
            float y = listeners.get(firstItemIndex).getY();
            if (y < rollDown) {
                ymove = (rollDown - y) * 0.25f;
                onAdjust = true;
            }
        }

        for (BaseUIItem item : listeners) {
            item.updatePosition(0.0f, ymove);
        }

        if (Math.abs(ymove *= 0.85f) < 0.1f || onAdjust) {
            ymove = 0.0f;
        }
    }

    public void destroyItems() {

        for (BaseRenderable listener : listeners) {
            if (listener != null) {
                listener.destroy();
            }
        }
    }

    @Override
    public void destroy() {

        destroyItems();
    }

}
