package com.base.lib.engine;

import android.opengl.GLES20;

import com.base.lib.engine.common.BaseDrawableData;
import com.base.lib.engine.common.other.Point3;

/**
 * 16 Created by doctor on 18.2.14.
 */
public class BumpDrawableBuffer extends DrawableBuffer {

    protected BaseTexture bumpMap;
    private Point3 lightPos;

    public BumpDrawableBuffer(BaseDrawableData data) {
        super(data);
        // TODO: 31. 1. 2016  shader = BaseShader.get(2);

        lightPos = new Point3(0.0f, 1.0f, 1.25f);
    }

    public void setBumpMap(BaseTexture normalsTexture){

        bumpMap = normalsTexture;
    }

    public BaseTexture getBumpMap(){

        return bumpMap;
    }

    @Override
    public void glBindTexture(int textureID) {

        BaseGL.bindTexture(bumpMap.glid, 1, shader.handle[5]);
        BaseGL.bindTexture(textureID, 0, shader.handle[4]);
    }

    public void glPutLightPos(float x, float y, float z){
        GLES20.glUniform3f(shader.handle[3], x, y, z);
    }

    public void glPutLightPos(Point3 pos){
        GLES20.glUniform3f(shader.handle[3], pos.x, pos.y, pos.z);
    }

    public void setLightPos(Point3 lightPos) {
        this.lightPos = lightPos;
    }

    @Override
    public synchronized void draw() {
        glPutLightPos(lightPos);
        super.draw();
    }

    @Override
    public void draw(DrawableModel model) {
        glPutLightPos(lightPos);
        super.draw(model);
    }
}
