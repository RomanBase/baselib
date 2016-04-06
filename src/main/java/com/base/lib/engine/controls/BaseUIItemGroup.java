package com.base.lib.engine.controls;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class BaseUIItemGroup extends BaseUIItem {

    protected List<BaseUIItem> items;

    public BaseUIItemGroup() {

        items = new ArrayList<BaseUIItem>(10);
    }

    @Override
    public void onGoDown() {

    }

    @Override
    public boolean onGoUp(boolean above) {
        return false;
    }

    @Override
    public boolean isTouched(float x, float y) {
        return false;
    }

    @Override
    public void draw() {

        for (BaseUIItem item : items) {
            item.draw();
        }
    }

    @Override
    public void secondaryDrawPass() {

        for (BaseUIItem item : items) {
            item.secondaryDrawPass();
        }
    }

    @Override
    public void update() {

        for (BaseUIItem item : items) {
            item.update();
        }
    }

    public void add(BaseUIItem item) {

        item.setCamera(camera);
        item.setShader(shader);
        items.add(item);
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
        BaseUIItem last = items.get(items.size() - 1);
        item.updatePosition(last.getX(), last.getY() - last.gethHeight() - item.gethHeight() - spacing);
    }

    public void addUnder(float spacing, BaseUIItem... items) {

        if (items != null) {
            for (BaseUIItem listener : items) {
                addUnder(listener, spacing);
            }
        }
    }

    public void remove(BaseUIItem listener) {

        items.remove(listener);
    }

    public void clear() {

        for (BaseUIItem item : items) {
            item.destroy();
        }
        items.clear();
    }

    @Override
    public void onTouchDown(int id, float x, float y) {

        super.onTouchDown(id, x, y);
        for (BaseUIItem item : items) {
            item.onTouchDown(id, x, y);
        }
    }

    @Override
    public boolean onTouchUp(int id, float x, float y) {

        super.onTouchUp(id, x, y);
        for (BaseUIItem item : items) {
            if (item.onTouchUp(id, x, y)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void onTouchMove(int id, float x, float y) {

        super.onTouchMove(id, x, y);
        for (BaseUIItem item : items) {
            item.onTouchMove(id, x, y);
        }
    }
}
