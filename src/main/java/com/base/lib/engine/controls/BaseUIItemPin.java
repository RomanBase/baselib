package com.base.lib.engine.controls;

import com.base.lib.engine.DrawableModel;
import com.base.lib.engine.common.other.Point2;

/**
 *
 */
public abstract class BaseUIItemPin extends BaseUIItem {

    protected float xpin;
    protected float ypin;
    protected boolean onGoUpReset;

    public BaseUIItemPin() {
        super();

        onGoUpReset = true;
    }

    protected abstract void adjustPin();

    protected void adjustOnCircle(float r) {

        if (Point2.distance(x, y, xpin, ypin) > r) {
            Point2 pin = Point2.circlePoint(x, y, xpin, ypin, r);
            xpin = pin.x;
            ypin = pin.y;
        }
    }

    protected void adjustOnRectangle(float w, float h) {

        if (Point2.length(x, xpin) > w) {
            xpin = w;
        }

        if (Point2.length(y, ypin) > h) {
            ypin = h;
        }
    }

    public void setItemInfo(DrawableModel model) {
        super.setItemInfo(model);
        xpin = x;
        ypin = y;
    }

    public void setItemInfo(float posX, float posY, float hWidth, float hHeight) {
        super.setItemInfo(posX, posY, hWidth, hHeight);
        xpin = x;
        ypin = y;
    }

    protected boolean goUp(boolean above) {

        touchID = -1;
        if (onGoUpReset) {
            xpin = this.x;
            ypin = this.y;
        }
        adjustPin();
        return onGoUp(above);
    }

    @Override
    public void onTouchDown(int id, float x, float y) {

        if (touchID == -1 && isVisible) {
            if (isTouched(x, y)) {
                xpin = x;
                ypin = y;
                goDown(id);
            }
        }
    }

    @Override
    public boolean onTouchUp(int id, float x, float y) {

        if (touchID == id) {
            return goUp(isTouched(x, y));
        }

        return false;
    }

    @Override
    public void onTouchMove(int id, float x, float y) {

        if (touchID == id) {
            xpin = x;
            ypin = y;
            adjustPin();
            if (moveOut) {
                if (!isTouched(x, y)) {
                    goUp(false);
                }
            }
        } else if (touchID == -1 && isVisible) {
            if (moveIn) {
                xpin = x;
                ypin = y;
                adjustPin();
                if (isTouched(x, y)) {
                    goDown(id);
                }
            }
        }
    }

    @Override
    public void updatePosition(float xmove, float ymove) {

        if (dynamic) {
            x += xmove;
            y += ymove;
            xpin += xmove;
            ypin += ymove;
        }
    }

    public float getXpin() {
        return xpin;
    }

    public void setXpin(float xpin) {
        this.xpin = xpin;
    }

    public float getYpin() {
        return ypin;
    }

    public void setYpin(float ypin) {
        this.ypin = ypin;
    }

    public boolean isOnGoUpReset() {
        return onGoUpReset;
    }

    public void setOnGoUpReset(boolean onGoUpReset) {
        this.onGoUpReset = onGoUpReset;
    }
}
