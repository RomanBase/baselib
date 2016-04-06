package com.base.lib.interfaces;

public interface BaseUiTouchListener {

    public void onTouchDown(int id, float x, float y);

    public boolean onTouchUp(int id, float x, float y);

    public void onTouchMove(int id, float x, float y);
}
