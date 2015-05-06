package com.base.lib.interfaces;

/**
 * 09 Created by doctor on 24.9.13.
 */
public interface BaseTouchListener {

    public void onTouchDown(int id, float x, float y);
    public void onTouchUp(int id, float x, float y);
    public void onTouchMove(int id, float x, float y);
}
