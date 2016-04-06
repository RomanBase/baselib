package com.base.lib.engine;

import android.opengl.Matrix;

import com.base.lib.engine.common.BaseMatrix;
import com.base.lib.engine.common.other.Point2;
import com.base.lib.engine.common.other.Point3;

import org.jbox2d.common.Vec2;

/**
 * 09 Created by doctor on 11.10.13.
 */
public class DrawableModel extends BaseRenderable {

    public float posX;
    public float posY;
    public float posZ;
    public float sizeX;
    public float sizeY;
    public float sizeZ;
    public float rotX;
    public float rotY;
    public float rotZ;
    public float ratioX;
    public float ratioY;
    public float ratioZ;

    protected float[] MVPMatrix;
    protected float[] modelMatrix;

    protected BaseTexture texture = BaseGL.baseTexture;

    public DrawableModel() {

        init();
    }

    public DrawableModel(Base base){
        super(base);

        init();
    }

    public DrawableModel(Base base, BaseCamera camera) {
        super(base);

        init();
        this.camera = camera;
    }

    private void init() {

        inUse = true;

        ratioX = 1.0f;
        ratioY = 1.0f;
        ratioZ = 1.0f;

        MVPMatrix = new float[16];
        modelMatrix = new float[16];

        Matrix.setIdentityM(modelMatrix, 0);
    }

    @Override
    public void update() {

        Matrix.multiplyMM(MVPMatrix, 0, camera.mVPMatrix, 0, modelMatrix, 0);
    }

    public void updateModelStaticVP() {

        Matrix.multiplyMM(modelMatrix, 0, modelMatrix, 0, camera.VPMatrix[0], 0);
        Matrix.multiplyMM(MVPMatrix, 0, modelMatrix, 0, camera.VPMatrix[1], 0);
    }

    public void updateModelVP() {

        Matrix.multiplyMM(MVPMatrix, 0, camera.mVPMatrix, 0, modelMatrix, 0);
    }

    public void updateWithModificationCalls() {

        translate(posX, posY, posZ);
        scale(ratioX, ratioY, ratioZ);
        rotate(rotX, rotY, rotZ);
        Matrix.multiplyMM(MVPMatrix, 0, camera.mVPMatrix, 0, modelMatrix, 0);
    }

    /**
     * Empty
     */
    @Override
    public void draw() {

    }

    /**
     * apply 2D translate on model matrix by Box2D Vec2
     */
    public void translate(Vec2 vec) {

        BaseMatrix.translate(modelMatrix, vec.x, vec.y, posZ);
        posX = modelMatrix[12];
        posY = modelMatrix[13];
    }

    /**
     * apply 2D(x,y) translate on model matrix
     */
    public void translate(Point2 pos) {

        BaseMatrix.translate(modelMatrix, pos.x, pos.y, posZ);
        posX = modelMatrix[12];
        posY = modelMatrix[13];
    }

    /**
     * apply 2D(x,y) translate on model matrix
     */
    public void translate(float x, float y) {

        BaseMatrix.translate(modelMatrix, x, y, posZ);
        posX = modelMatrix[12];
        posY = modelMatrix[13];
    }

    /**
     * apply translate on model matrix
     */
    public void translate(Point3 pos) {

        BaseMatrix.translate(modelMatrix, pos.x, pos.y, pos.z);
        posX = modelMatrix[12];
        posY = modelMatrix[13];
        posZ = modelMatrix[14];
    }

    /**
     * apply translate on model matrix
     */
    public void translate(float x, float y, float z) {

        BaseMatrix.translate(modelMatrix, x, y, z);
        posX = modelMatrix[12];
        posY = modelMatrix[13];
        posZ = modelMatrix[14];
    }

    /**
     * apply scale ratio on model matrix, 1.0f = 100% (no scale)
     */
    public void scale(float x, float y, float z) {

        ratioX = x;
        ratioY = y;
        ratioZ = z;
        BaseMatrix.scale(modelMatrix, x, y, z);
    }

    /**
     * apply scale ratio on model matrix, 1.0f = 100% (no scale)
     */
    public void scale(float ratio) {

        ratioX = ratioY = ratioZ = ratio;
        BaseMatrix.scale(modelMatrix, ratio, ratio, ratio);
    }

    /**
     * apply rotation on model matrix
     */
    public void rotate(float angleX, float angleY, float angleZ) {

        rotX = angleX;
        rotY = angleY;
        rotZ = angleZ;
        BaseMatrix.rotate(modelMatrix, angleX, angleY, angleZ);
    }

    /**
     * apply rotation on model matrix - 2D rotation around Z axis
     */
    public void rotateZ(float angle) {

        rotZ = angle;
        BaseMatrix.rotateZ(modelMatrix, angle);
    }

    /**
     * apply rotation on model matrix - 2D rotation around Y axis
     */
    public void rotateY(float angle) {

        rotY = angle;
        BaseMatrix.rotateY(modelMatrix, angle);
    }

    /**
     * apply rotation on model matrix - 2D rotation around X axis
     */
    public void rotateX(float angle) {

        rotX = angle;
        BaseMatrix.rotateX(modelMatrix, angle);
    }

    /**
     * apply transformation on model matrix from InnerAction
     * note: action isn't updated here
     */
    public void transform(InnerAction action) {

        translate(action.posX, action.posY, action.posZ);
        rotate(action.rotX, action.rotY, action.rotZ);
        scale(action.scale);
    }

    /**
     * sets model matrix to identity matrix
     */
    public void setIdentityMM() {
        BaseMatrix.setIdentity(modelMatrix);
    }

    /**
     * multiply model metrix by another matrix array [16] - column matrix
     */
    public void multiplyModelMatrix(float[] leftMatrix) {
        BaseMatrix.multiplyMM(modelMatrix, modelMatrix, leftMatrix);
    }

    /**
     * sets model matrix array [16] - column matrix
     */
    public void setModelMatrix(float[] modelMatrix) {
        this.modelMatrix = modelMatrix;
    }

    /**
     * @return float array as model matrix
     */
    public float[] getModelMatrix() {
        return modelMatrix;
    }

    /**
     * apply billboarding on model matrix - all rotations are mulled
     * note: scale must be applied after this
     */
    public void billboard() {
        BaseMatrix.billboard(modelMatrix);
    }

    /**
     * apply billboarding on model matrix - model points straight to camera
     * note: scale must be applied after this
     */
    public void billboardByCammera() {
        BaseMatrix.copy3(camera.VPMatrix[0], modelMatrix);
    }

    /**
     * @return absolute value of sizeX * ratioX
     */
    public float getScaledSizeX() {
        return Math.abs(sizeX * ratioX);
    }

    /**
     * @return absolute value of sizeY * ratioY
     */
    public float getScaledSizeY() {
        return Math.abs(sizeY * ratioY);
    }

    /**
     * @return absolute value of sizeZ * ratioZ
     */
    public float getScaledSizeZ() {
        return Math.abs(sizeZ * ratioZ);
    }

    /**
     * @return texture reference
     */
    public BaseTexture getTexture() {
        return texture;
    }

    /**
     * sets reference to texture
     */
    public void setTexture(BaseTexture texture) {
        this.texture = texture;
    }

    /**
     * @return reference to this object instance
     */
    public DrawableModel reference() {
        return this;
    }

    @Override
    public String toString() {

        return "\nPos: " + posX + " " + posY + " " + posZ
                + "\nSize: " + sizeX + " " + sizeY + " " + sizeZ
                + "\nScale: " + ratioX + " " + ratioY + " " + ratioZ;
    }

    /**
     * Empty
     */
    @Override
    public void destroy() {

    }
}