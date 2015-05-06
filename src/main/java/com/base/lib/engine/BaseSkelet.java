package com.base.lib.engine;

import com.base.lib.engine.common.BaseDrawableData;
import com.base.lib.engine.common.BaseMatrix;
import com.base.lib.engine.common.Point3;
import com.base.lib.engine.common.VertGroup;
import com.base.lib.engine.other.dev.DrawSkeletStruckture;

/**
 * Holds information about skeletal armature
 */
public class BaseSkelet {

    private Bone[] bones;
    private VertGroup[] groups;
    private float[] modifiedVerts;
    private int framesCount;

    private int currentFrame;

    /**
     * Creates new instance of BaseSkelet with bones and vert groups
     * Bones and Groups are parred by name
     * typically gets Bone and VertGroup information from beo file
     * */
    public BaseSkelet(Bone[] bones, VertGroup[] groups){

        setBones(bones);
        setVertGroups(groups);
        modifyVertices();

        currentFrame = 0;
    }

    /** sets bone array and number of frames */
    public void setBones(Bone[] bones){

        if (bones != null) {
            this.bones = bones;
            if(bones[0].frameAction != null) {
                framesCount = bones[0].frameAction.length - 1;
            }
        }
    }

    /** sets vertgroup array and calculate sum of verts */
    public void setVertGroups(VertGroup[] groups){

        if (groups != null) {
            this.groups = groups;
            int count = 0;
            for (VertGroup g : groups) {
                g.startIndex = VertGroup.startIndex(g, groups);
                count += g.verts.length;
            }
            modifiedVerts = new float[count];
        }
    }

    public void modifyVertices(){

        if(groups != null) {
            for (Bone bone : bones) {
                VertGroup group = VertGroup.getGroup(bone.name, groups);
                for (int i = 0; i < group.verts.length; ) {
                    group.verts[i++] -= bone.head.x;
                    group.verts[i++] -= bone.head.y;
                    group.verts[i++] -= bone.head.z;
                }
            }
        }
    }

    public void setFrameActions(Point3[][] actions){

        for(int i = 0; i<bones.length; i++){
            bones[i].setFramesAction(actions[i]);
        }
    }

    /** update all bones, next frame */
    public void update(){

        if(currentFrame < framesCount){
            currentFrame++;
        } else {
            currentFrame = 0;
        }

        update(currentFrame);
    }

    /**
     * update all bones
     * @param frame index
     * */
    public void update(int frame){

        for(Bone bone : bones){
            bone.update(frame);
        }
    }

    /**
     * apply bones transformations to vert groups and creates one array
     * @return verts
     * */
    public float[] getVerts(){

        for (Bone bone : bones) {
            VertGroup group = VertGroup.getGroup(bone.name, groups);
            if (group != null) {
                Point3 h = bone.head;
                float[] m = bone.transformMatrix;
                float[] v = group.verts;
                int index = group.startIndex;
                for (int j = 0; j < v.length; ) {
                    float[] p = BaseMatrix.multiplyMV(m, v[j++], v[j++], v[j++]);
                    modifiedVerts[index++] = p[0];
                    modifiedVerts[index++] = p[1];
                    modifiedVerts[index++] = p[2];
                }
            }
        }

        return modifiedVerts;
    }

    /** reverse rotation direction of all bones */
    public void reverseBonesRotation(){

        for(Bone bone : bones){
            bone.reverseRotation();
        }
    }

    /** hard scale of bones and vertgroups */
    public void scale(float x, float y, float z){

        for(Bone bone : bones){
            bone.scale(x, y, z);
        }

        if(groups != null) {
            for (int i = 0; i < groups.length; i++) {
                for (int j = 0; j < groups[i].verts.length; ) {
                    groups[i].verts[j++] *= x;
                    groups[i].verts[j++] *= y;
                    groups[i].verts[j++] *= z;
                }
            }
        }
    }

    /** hard scale of bones and vertgroups */
    public void scale(float ratio){

        scale(ratio, ratio, ratio);
    }

    /** @return number of frames */
    public int getFramesCount(){

        return framesCount+1;
    }

    /** @return Bone array */
    public Bone[] getBones(){

        return bones;
    }

    /** @return VertGroup array */
    public VertGroup[] getVertGroups(){

        return groups;
    }

    public void showSkelet(){

        Base.render.addDrawable(new DrawSkeletStruckture(this, null, null));
    }

    public void showSkelet(BaseDrawableData data){

        Base.render.addDrawable(new DrawSkeletStruckture(this, data, null));
    }

    public void showSkelet(BaseDrawableData data, Texture texture){

        Base.render.addDrawable(new DrawSkeletStruckture(this, data, texture));
    }
}
