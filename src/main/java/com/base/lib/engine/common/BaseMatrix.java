package com.base.lib.engine.common;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.base.lib.engine.BaseCamera;

/**
 * 13 Created by doctor on 13.10.13.
 */
public class BaseMatrix {

    public static final float[] _matrix = new float[16];

    private static final float RAD = (float) (Math.PI / 180.0f);
    private static final float[] temp = new float[16];
    private static final float[] vert = new float[4];

    public static void setIdentity(float[] matrix) {

        matrix[0] = 1.0f;
        matrix[1] = 0.0f;
        matrix[2] = 0.0f;
        matrix[3] = 0.0f;

        matrix[4] = 0.0f;
        matrix[5] = 1.0f;
        matrix[6] = 0.0f;
        matrix[7] = 0.0f;

        matrix[8] = 0.0f;
        matrix[9] = 0.0f;
        matrix[10] = 1.0f;
        matrix[11] = 0.0f;

        matrix[12] = 0.0f;
        matrix[13] = 0.0f;
        matrix[14] = 0.0f;
        matrix[15] = 1.0f;
    }

    /**
     * @return new identity matrix
     */
    public static float[] newMatrix() {

        float[] out = new float[16];
        setIdentity(out);

        return out;
    }

    public static void setSMIdentity() {

        setIdentity(_matrix);
    }

    public static void translate(float[] matrix, float x, float y, float z) {

        for (int i = 0; i < 3; i++) {
            matrix[i + 12] += matrix[i] * x + matrix[i + 4] * y + matrix[i + 8] * z;
        }
    }

    public static void translateS(float[] matrix, float x, float y, float z) {

        matrix[12] = x;
        matrix[13] = y;
        matrix[14] = z;
    }

    public static void translateA(float[] matrix, float x, float y, float z) {

        matrix[12] += x;
        matrix[13] += y;
        matrix[14] += z;
    }

    public static void scale(float[] matrix, float ratio) {

        for (int i = 0; i < 3; i++) {
            matrix[i] *= ratio;
            matrix[i + 4] *= ratio;
            matrix[i + 8] *= ratio;
        }
    }

    public static void scale(float[] matrix, float x, float y, float z) {

        for (int i = 0; i < 3; i++) {
            matrix[i] *= x;
            matrix[i + 4] *= y;
            matrix[i + 8] *= z;
        }
    }

    public static void rotateX(float[] matrix, float angle) {

        angle *= RAD;
        float s = (float) Math.sin(angle);
        float c = (float) Math.cos(angle);

        matrix[5] = c;
        matrix[10] = c;
        matrix[6] = s;
        matrix[9] = -s;
        matrix[1] = 0;
        matrix[2] = 0;
        matrix[4] = 0;
        matrix[8] = 0;
        matrix[0] = 1;
    }

    public static void rotateY(float[] matrix, float angle) {

        angle *= RAD;
        float s = (float) Math.sin(angle);
        float c = (float) Math.cos(angle);

        matrix[0] = c;
        matrix[10] = c;
        matrix[8] = s;
        matrix[2] = -s;
        matrix[1] = 0;
        matrix[4] = 0;
        matrix[6] = 0;
        matrix[9] = 0;
        matrix[5] = 1;
    }

    public static void rotateZ(float[] matrix, float angle) {

        angle *= RAD;
        float s = (float) Math.sin(angle);
        float c = (float) Math.cos(angle);

        matrix[0] = c;
        matrix[1] = s;
        matrix[2] = 0;

        matrix[4] = -s;
        matrix[5] = c;
        matrix[6] = 0;

        matrix[8] = 0;
        matrix[9] = 0;
        matrix[10] = 1;
    }

    public static void radRotateZ(float[] matrix, float angle) {

        float s = (float) Math.sin(angle);
        float c = (float) Math.cos(angle);

        matrix[0] = c;
        matrix[5] = c;
        matrix[1] = s;
        matrix[4] = -s;
        matrix[2] = 0;
        matrix[6] = 0;
        matrix[8] = 0;
        matrix[9] = 0;
        matrix[10] = 1;
    }

    public static void rotate(float[] matrix, float x, float y, float z) { //todo

        if (x != 0) Matrix.rotateM(matrix, 0, x, 1.0f, 0.0f, 0.0f);
        if (y != 0) Matrix.rotateM(matrix, 0, y, 0.0f, 1.0f, 0.0f);
        if (z != 0) Matrix.rotateM(matrix, 0, z, 0.0f, 0.0f, 1.0f);
    }

    public static void transform(float[] matrix, float x, float y, float z, float angle, float scale) {

        angle *= RAD;
        float s = (float) Math.sin(angle) * scale;
        float c = (float) Math.cos(angle) * scale;

        matrix[0] = c;
        matrix[1] = s;
        matrix[2] = 0;
        matrix[3] = 0;

        matrix[4] = -s;
        matrix[5] = c;
        matrix[6] = 0;
        matrix[7] = 0;

        matrix[8] = 0;
        matrix[9] = 0;
        matrix[10] = scale;
        matrix[11] = 0;

        matrix[12] = x;
        matrix[13] = y;
        matrix[14] = z;
        matrix[15] = 1;
    }

    public static void transform(float[] matrix, float x, float y, float z, float angle, float w, float h) {

        angle *= RAD;
        float s = (float) Math.sin(angle);
        float c = (float) Math.cos(angle);

        matrix[0] = c * w;
        matrix[1] = s * w;
        matrix[2] = 0;
        matrix[3] = 0;

        matrix[4] = -s * h;
        matrix[5] = c * h;
        matrix[6] = 0;
        matrix[7] = 0;

        matrix[8] = 0;
        matrix[9] = 0;
        matrix[10] = 1.0f;
        matrix[11] = 0;

        matrix[12] = x;
        matrix[13] = y;
        matrix[14] = z;
        matrix[15] = 1;
    }

    public static void multiplyMM(float[] resultM, float[] leftM, float[] rightM) {

        Matrix.multiplyMM(resultM, 0, leftM, 0, rightM, 0);
    }

    public static float[] multiplyMV(float[] matrix, float x, float y, float z) {

        vert[0] = x;
        vert[1] = y;
        vert[2] = z;
        vert[3] = 1.0f;

        Matrix.multiplyMV(vert, 0, matrix, 0, vert, 0);

        return vert;
    }

    public static void multiplyMA(float[] matrix, float[] array) {

        for (int i = 0; i < array.length; i += 3) {
            multiplyMV(matrix, array[i], array[i + 1], array[i + 2]);
            array[i] = vert[0];
            array[i + 1] = vert[1];
            array[i + 2] = vert[2];
        }
    }

    public static void multiplyMA(float[] matrix, float[] src, float[] dst) {

        for (int i = 0; i < src.length; i += 3) {
            multiplyMV(matrix, src[i], src[i + 1], src[i + 2]);
            dst[i] = vert[0];
            dst[i + 1] = vert[1];
            dst[i + 2] = vert[2];
        }
    }

    public static void multiplyMC(float[] matrix, BaseCamera camera) {

        Matrix.multiplyMM(matrix, 0, camera.mVPMatrix, 0, matrix, 0);
    }

    public static void multiplyMCV(float[] matrix, BaseCamera camera){

        Matrix.multiplyMM(matrix, 0, camera.VPMatrix[0], 0, matrix, 0);
    }

    public static void multiplyMA2(float[] matrix, float[] src, float[] dst){

        for (int i = 0; i < src.length; i += 2) {
            multiplyMV(matrix, src[i], src[i + 1], 0.0f);
            dst[i] = vert[0];
            dst[i + 1] = vert[1];
        }
    }

    public static void billboard(float[] matrix) {

        matrix[0] = 1.0f;
        matrix[1] = 0.0f;
        matrix[2] = 0.0f;

        matrix[4] = 0.0f;
        matrix[5] = 1.0f;
        matrix[6] = 0.0f;

        matrix[8] = 0.0f;
        matrix[9] = 0.0f;
        matrix[10] = 1.0f;
    }

    public static void billboard(BaseCamera camera, float[] matrix) {

        camera.billboard(matrix);
    }

    public static void copy(float[] src, float[] dst) {

        System.arraycopy(src, 0, dst, 0, 16);
    }

    public static void copy3(float[] src, float[] dst) {

        dst[0] = src[0];
        dst[5] = src[5];
        dst[9] = src[9];
        dst[1] = src[1];
        dst[6] = src[6];
        dst[10] = src[10];
        dst[2] = src[2];
        dst[7] = src[7];
        dst[11] = src[11];
    }

    public static void copyPos(float[] src, float[] dst) {

        dst[12] = src[12];
        dst[13] = src[13];
        dst[14] = src[14];
    }

    public static void add(float[] matrix, float[] ma, float[] mb) {

        for (int i = 0; i < 15; i++) {
            matrix[i] = ma[i] + mb[i];
        }
    }

    public static void glPutMatrix(float[] matrix, int shaderHandle) {

        GLES20.glUniformMatrix4fv(shaderHandle, 1, false, matrix, 0);
    }

    public static String toString(float[] m) {

        return m + " \n" +
                m[0] + " " + m[4] + " " + m[8] + " " + m[12] + "\n" +
                m[1] + " " + m[5] + " " + m[9] + " " + m[12] + "\n" +
                m[2] + " " + m[6] + " " + m[10] + " " + m[14] + "\n" +
                m[3] + " " + m[7] + " " + m[11] + " " + m[15];
    }
}
