package com.base.lib.box;

import org.jbox2d.dynamics.Body;

import com.base.lib.engine.BaseDrawable;

/**
 * 14 Created by doctor on 12.9.13.
 */
public interface B2BodyListener {

    void onCreate(Body body, BaseDrawable profile);
}
