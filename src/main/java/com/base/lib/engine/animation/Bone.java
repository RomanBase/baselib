package com.base.lib.engine.animation;

import com.base.lib.engine.common.BaseMatrix;
import com.base.lib.engine.common.other.Point3;

/**
 * Holds information about single bone and joints,
 * name is important for vertex group parring
 * head and tail joint
 * parent bone
 * actions per frame
 */
public class Bone {

    protected String name;
    protected Point3 head;
    protected Point3 tail;
    protected Bone parent;
    protected Point3[] frameAction;
    protected float weight;
    protected float[] transformMatrix;

    private Point3 trans;

    public Bone(){

        trans = new Point3();
        transformMatrix = new float[16];
        BaseMatrix.setIdentity(transformMatrix);
    }

    /**
     * update bone and calculate transformation matrix based on parent matrix and bone frame actions
     * @param frame index
     * */
    public void update(int frame){

        Point3.copy(head, trans);
        if(parent != null){
            BaseMatrix.copy(parent.transformMatrix, transformMatrix);
            Point3.sub(trans, parent.head);
        } else {
            BaseMatrix.setIdentity(transformMatrix);
        }

        Point3 rot = frameAction[frame];

        BaseMatrix.translate(transformMatrix, trans.x, trans.y, trans.z);
        BaseMatrix.rotate(transformMatrix, rot.x, rot.y, rot.z);
    }

    /** reverse direction of rotations */
    public void reverseRotation(){

        for (int i = 0; i < frameAction.length; i++) {

            frameAction[i].x *= -1;
            frameAction[i].y *= -1;
            frameAction[i].z *= -1;
        }
    }

    public void scale(float x, float y, float z){

        head.x *= x;
        head.y *= y;
        head.z *= z;

        tail.x *= x;
        tail.y *= y;
        tail.z *= z;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Point3 getHead() {
        return head;
    }

    public void setHead(Point3 head) {
        this.head = head;
    }

    public Point3 getTail() {
        return tail;
    }

    public void setTail(Point3 tail) {
        this.tail = tail;
    }

    public void setFramesAction(Point3[] actions){

        frameAction = actions;
    }

    public void setFrameAction(int index, float x, float y, float z){

        frameAction[index].set(x, y, z);
    }

    public Point3 getFrameAction(int index){

        return frameAction[index];
    }

    public Point3[] getFrameAction(){

        return frameAction;
    }

    public int getFramesCount(){

        return frameAction.length;
    }

    public void setParent(Bone parent) {

        this.parent = parent;
    }

    public float[] getTransformMatrix() {
        return transformMatrix;
    }

    public void setTransformMatrix(float[] transformMatrix) {
        this.transformMatrix = transformMatrix;
    }

    @Override
    public String toString() {
        return name +"\nhead: "+head+"\ntail: "+tail+"\nframes: "+getFramesCount();
    }
}
