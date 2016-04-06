package com.base.lib.engine.controls;

import com.base.lib.engine.BaseRender;
import com.base.lib.engine.BaseRenderable;
import com.base.lib.engine.DrawableModel;
import com.base.lib.engine.common.other.Point2;
import com.base.lib.interfaces.BaseUiTouchListener;

/**
 *
 */
public abstract class BaseUIItem extends BaseRenderable implements BaseUiTouchListener {

    protected float x;
    protected float y;
    protected float hWidth;
    protected float hHeight;
    protected int touchID;

    protected boolean moveOut;
    protected boolean moveIn;
    protected boolean isVisible;
    protected boolean dynamic;

    public BaseUIItem() {

        touchID = -1;
        moveOut = false;
        moveIn = false;
        isVisible = true;
        dynamic = true;
    }

    public abstract void onGoDown();

    public abstract boolean onGoUp(boolean above);

    public abstract boolean isTouched(float x, float y);

    @Override
    public void update() {

    }

    @Override
    public void destroy() {

    }

    public void secondaryDrawPass() {

    }

    public void setItemInfo(DrawableModel model) {

        x = model.posX;
        y = model.posY;
        hWidth = model.sizeX * 0.5f;
        hHeight = model.sizeY * 0.5f;
    }

    public void setItemInfo(float posX, float posY, float hWidth, float hHeight) {

        this.x = posX;
        this.y = posY;
        this.hWidth = hWidth;
        this.hHeight = hHeight;
    }

    public void setResolveMoveAction(boolean moveIn, boolean moveOut) {

        this.moveIn = moveIn;
        this.moveOut = moveOut;
    }

    protected boolean checkRectHit(float px, float py) {

        return Point2.length(px, x) <= hWidth && Point2.length(py, y) <= hHeight;
    }

    protected boolean checkCircleHit(float px, float py) {

        return Point2.distance(x, y, px, py) <= hWidth;
    }

    protected void goDown(int onTouchID) {

        touchID = onTouchID;
        onGoDown();
    }

    protected boolean goUp(boolean above) {

        touchID = -1;
        return onGoUp(above);
    }

    @Override
    public void onTouchDown(int id, float x, float y) {

        if (touchID == -1 && isVisible) {
            if (isTouched(x, y)) {
                goDown(id);
            }
        }
    }

    @Override
    public boolean onTouchUp(int id, float x, float y) {

        return touchID == id && goUp(isTouched(x, y));

    }

    @Override
    public void onTouchMove(int id, float x, float y) {

        if (touchID == id) {
            if (moveOut) {
                if (!isTouched(x, y)) {
                    goUp(false);
                }
            }
        } else if (touchID == -1 && isVisible) {
            if (moveIn) {
                if (isTouched(x, y)) {
                    goDown(id);
                }
            }
        }
    }

    public boolean isDown() {

        return touchID != -1;
    }

    public void updatePosition(float xmove, float ymove) {

        if (dynamic) {
            x += xmove;
            y += ymove;
        }
    }

    public BaseUIItem register() {

        ((BaseRender) base.render).getUiLayer().add(weakRef());
        return this;
    }

    public BaseUIItem unregister() {

        ((BaseRender) base.render).getUiLayer().remove(weakRef());
        return this;
    }

    public void asStatic() {
        dynamic = false;
    }

    public void asDynamic() {
        dynamic = true;
    }

    public boolean isDynamic() {
        return dynamic;
    }

    public void resetTouchID() {
        touchID = -1;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float gethWidth() {
        return hWidth;
    }

    public float gethHeight() {
        return hHeight;
    }

    public int getTouchID() {
        return touchID;
    }

    public boolean isMoveOut() {
        return moveOut;
    }

    public boolean isMoveIn() {
        return moveIn;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public BaseUIItem weakRef() {

        return this;
    }
}
