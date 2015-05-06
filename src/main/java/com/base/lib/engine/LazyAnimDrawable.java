package com.base.lib.engine;

/**
 * 15 Created by doctor on 22.1.14.
 */
public class LazyAnimDrawable extends DrawableAnim{

    public LazyAnimDrawable(DrawableBuffer drawableBuffer, DrawableAction[] drawableActions) {
        super(drawableBuffer, drawableActions);
    }

    @Override
    protected void action(DrawableAction action, int currentFrame) {

        putVerticesIntoBuffer(action.vertices[currentFrame]);
    }
}
