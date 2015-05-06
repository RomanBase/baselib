package com.base.lib.engine;

import com.base.lib.engine.common.BaseMatrix;

/**
 *
 */
public class BaseStaticDrawable extends BaseRenderable{

    protected float[] modelMatrix;
    protected DrawableBuffer buffer;
    protected Texture texture = BaseGL.baseTexture;

    public BaseStaticDrawable(){

        modelMatrix = new float[16];
        BaseMatrix.setIdentity(modelMatrix);
    }

    public BaseStaticDrawable(DrawableBuffer buffer){
        this();

        this.buffer = buffer;
    }

    public void translate(float x, float y, float z){

        BaseMatrix.translateS(modelMatrix, x, y, z);
    }

    public void scale(float x, float y, float z){

        BaseMatrix.scale(modelMatrix, x, y, z);
    }

    public void scale(float ratio){

        BaseMatrix.scale(modelMatrix, ratio, ratio, ratio);
    }

    public void rotateZ(float angle){

        BaseMatrix.rotateZ(modelMatrix, angle);
    }

    public void rotateY(float angle){

        BaseMatrix.rotateY(modelMatrix, angle);
    }

    public void rotateX(float angle){

        BaseMatrix.rotateX(modelMatrix, angle);
    }

    public void rotate(float x, float y, float z){

        BaseMatrix.rotate(modelMatrix, x, y, z);
    }

    public float[] getModelMatrix() {
        return modelMatrix;
    }

    public void setModelMatrix(float[] modelMatrix) {
        this.modelMatrix = modelMatrix;
    }

    public DrawableBuffer getBuffer() {
        return buffer;
    }

    public void setBuffer(DrawableBuffer buffer) {
        this.buffer = buffer;
    }

    public Texture getTexture() {
        return texture;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    @Override
    public void setShader(BaseShader shader) {

        this.shader = shader;
        this.buffer.shader = shader;
    }

    public void glCalcAndPutMatrix(DrawableBuffer buffer){

        BaseMatrix.multiplyMM(BaseMatrix._matrix, camera.mVPMatrix, modelMatrix);
        buffer.glPutMVPMatrix(BaseMatrix._matrix);
    }

    @Override
    public void draw(){

        buffer.glPutVerticeBuffer();
        buffer.glPutTextureBuffer();
        buffer.glBindTexture(texture.glid);
        glCalcAndPutMatrix(buffer);
        buffer.glPutDraw();
        buffer.glDisableAttribArray();
    }

    /** Empty */
    @Override
    public void update() {

    }

    /** Empty */
    @Override
    public void destroy() {

    }
}
