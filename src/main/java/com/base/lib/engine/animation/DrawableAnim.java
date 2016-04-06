package com.base.lib.engine.animation;

import com.base.lib.engine.DrawableBuffer;
import com.base.lib.engine.DrawableModel;

import java.nio.FloatBuffer;

/**
 * 12 Created by doctor on 4.2.14.
 */
public abstract class DrawableAnim extends DrawableModel {

    protected DrawableBuffer buffer;
    protected DrawableAction[] actions;

    private DrawableAction currentAction;
    private FloatBuffer[] vertBuffer;
    private int bIndex;
    private int currentFrame;

    public DrawableAnim(DrawableBuffer drawableBuffer, DrawableAction[] drawableActions){
        super();

        actions = drawableActions;
        if(actions != null) {
            currentAction = actions[0];
            currentFrame = currentAction.startsAt;
            for(int i = 1; i<actions.length; i++){
                if(actions[i].getSkelet().getVertGroups() == null){
                    actions[i].getSkelet().setVertGroups(actions[0].getSkelet().getVertGroups());
                }
            }
        }

        buffer = drawableBuffer;
        sizeX = buffer.getSizeX();
        sizeY = buffer.getSizeY();
        sizeZ = buffer.getSizeZ();

        bIndex = 0;
        vertBuffer = new FloatBuffer[2];
        vertBuffer[0] = buffer.getVerticeBuffer();
        vertBuffer[1] = vertBuffer[0].duplicate();
    }

    public void popAction(String name) {

        for (DrawableAction action : actions) {
            if (action.name.equals(name)) {
                currentAction = action;
                currentFrame = action.startsAt;
                break;
            }
        }
    }

    public void popAction(int index) {

        currentAction = actions[index];
        currentFrame = currentAction.startsAt;
    }

    public void noAction() {

        currentAction = null;
        currentFrame = 0;
    }

    /**
     * slow - realocate array
     */
    public void addAction(DrawableAction action) {

        DrawableAction[] temp = new DrawableAction[actions.length + 1];
        System.arraycopy(actions, 0, temp, 0, actions.length);
        temp[actions.length + 1] = action;
        actions = null;
        actions = temp;
    }

    public void setFrame(int actionIndex, int frameIndex){

        currentAction = actions[actionIndex];
        currentFrame = frameIndex;

        action(currentAction, currentFrame);
    }

    protected abstract void action(DrawableAction action, int currentFrame);

    protected void atStopsAt(){

    }

    protected void putVerticesIntoBuffer(float[] vertices) {

        vertBuffer[bIndex].put(vertices).position(0);
    }

    @Override
    public void update() {

        if (currentAction != null) {

            action(currentAction, currentFrame);

            switch (bIndex) {
                case 0:
                    bIndex = 1;
                    break;
                case 1:
                    bIndex = 0;
                    break;
            }

            currentFrame++;
            if (currentAction.stopsAt == -1) {
                if (currentFrame >= currentAction.length) {
                    currentFrame = currentAction.startsAt;
                }
            } else {
                if (currentFrame > currentAction.stopsAt) {
                    currentFrame = currentAction.stopsAt;
                    atStopsAt();
                }
            }
        }

        updateModel();
    }

    public void updateModel(){
        super.update();
    }

    @Override
    public void draw() {

        buffer.glPutTextureBuffer();
        buffer.glPutVerticeBuffer(vertBuffer[bIndex]);
        buffer.bindTexturePutMVPMatrix(this);
        buffer.glPutDraw();
        buffer.glDisableAttribArray();
    }

    public DrawableAction[] getActions(){

        return actions;
    }

    public DrawableAction getCurrentAction() {

        return currentAction;
    }

    public void setNextFrameIndex(int nextFrameIndex) {

        currentFrame = nextFrameIndex;
    }

    public int getCurrentFrameIndex() {

        return currentFrame;
    }

    public boolean isStoped(){

        return currentFrame == currentAction.stopsAt;
    }

    public boolean isCycleEnd(){

        return currentFrame == currentAction.length-2;
    }

    public DrawableBuffer getBuffer(){

        return buffer;
    }

}
