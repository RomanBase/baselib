package com.base.lib.engine;

/**
 * 12 Created by doctor on 4.2.14.
 */
public class SkeletAnimDrawable extends DrawableAnim {


    public SkeletAnimDrawable(DrawableBuffer drawableBuffer, DrawableAction[] drawableActions){
        super(drawableBuffer, drawableActions);
    }

    @Override
    protected void action(DrawableAction action, int currentFrame) {

        action.skelet.update(currentFrame);
        putVerticesIntoBuffer(action.skelet.getVerts());
    }

}
