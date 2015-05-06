package com.base.lib.engine;

import android.opengl.GLES20;

import com.base.lib.engine.common.BaseMatrix;
import com.base.lib.engine.common.Buffers;
import com.base.lib.engine.common.DrawableData;
import com.base.lib.engine.glcommon.BaseGLBuffer;

/**
 *
 */
public class BaseDraw {

    private static boolean initialized = false;
    private static BaseShader shader = null;
    private static float matrix[] = BaseMatrix.newMatrix();

    public static void init(){

        if(!initialized) {
            initialized = true;
            if(shader == null) {
                shader = BaseGL.baseShader;
            }
            BaseGL.glRun(new Runnable() {
                @Override
                public void run() {
                    Rect.init();
                }
            });
        }
    }

    public static void destroy(){

        if(initialized) {
            initialized = false;
            shader = null;
            Rect.destroy();
        }
    }

    public static void useProgram(){

        BaseGL.useProgram(shader);
    }

    public static void setShader(BaseShader shader){

        BaseDraw.shader = shader;
    }

    public static void rect(float x, float y, float sx, float sy){

        BaseGLBuffer.glBindArray(Rect.vert, shader.handle[1], 2);
        BaseGLBuffer.glBindArray(Rect.text, shader.handle[2], 2);
        BaseMatrix.setIdentity(matrix);
        BaseMatrix.translate(matrix, x, y, 0.0f);
        BaseMatrix.scale(matrix, sx, sy, 1.0f);
        BaseMatrix.multiplyMC(matrix, Base.camera);
        GLES20.glUniformMatrix4fv(shader.handle[0], 1, false, matrix, 0);
        BaseGLBuffer.glBindElements(Rect.face);
        BaseGLBuffer.glDrawElements(8);
        BaseGLBuffer.glUnbind();
    }

    public static void rect(float x, float y, float z, float sx, float sy){

        BaseGLBuffer.glBindArray(Rect.vert, shader.handle[1], 2);
        BaseGLBuffer.glBindArray(Rect.text, shader.handle[2], 2);
        BaseMatrix.setIdentity(matrix);
        BaseMatrix.translate(matrix, x, y, z);
        BaseMatrix.scale(matrix, sx, sy, 1.0f);
        BaseMatrix.multiplyMC(matrix, Base.camera);
        GLES20.glUniformMatrix4fv(shader.handle[0], 1, false, matrix, 0);
        BaseGLBuffer.glBindElements(Rect.face);
        BaseGLBuffer.glDrawElements(8);
        BaseGLBuffer.glUnbind();
    }

    public static void rect(float x, float y, float z, float sx, float sy, float rot){

        BaseGLBuffer.glBindArray(Rect.vert, shader.handle[1], 2);
        BaseGLBuffer.glBindArray(Rect.text, shader.handle[2], 2);
        BaseMatrix.setIdentity(matrix);
        BaseMatrix.translate(matrix, x, y, z);
        BaseMatrix.rotateZ(matrix, rot);
        BaseMatrix.scale(matrix, sx, sy, 1.0f);
        BaseMatrix.multiplyMC(matrix, Base.camera);
        GLES20.glUniformMatrix4fv(shader.handle[0], 1, false, matrix, 0);
        BaseGLBuffer.glBindElements(Rect.face);
        BaseGLBuffer.glDrawElements(8);
        BaseGLBuffer.glUnbind();
    }

    public static void rect(BaseShader shader, float x, float y, float z, float sx, float sy){

        BaseGLBuffer.glBindArray(Rect.vert, shader.handle[1], 2);
        BaseGLBuffer.glBindArray(Rect.text, shader.handle[2], 2);
        BaseMatrix.setIdentity(matrix);
        BaseMatrix.translate(matrix, x, y, z);
        BaseMatrix.scale(matrix, sx, sy, 1.0f);
        BaseMatrix.multiplyMC(matrix, Base.camera);
        GLES20.glUniformMatrix4fv(shader.handle[0], 1, false, matrix, 0);
        BaseGLBuffer.glBindElements(Rect.face);
        BaseGLBuffer.glDrawElements(8);
        BaseGLBuffer.glUnbind();
    }

    public static void rect(BaseShader shader, float x, float y, float z, float sx, float sy, float rot){

        BaseGLBuffer.glBindArray(Rect.vert, shader.handle[1], 2);
        BaseGLBuffer.glBindArray(Rect.text, shader.handle[2], 2);
        BaseMatrix.setIdentity(matrix);
        BaseMatrix.translate(matrix, x, y, z);
        BaseMatrix.rotateZ(matrix, rot);
        BaseMatrix.scale(matrix, sx, sy, 1.0f);
        BaseMatrix.multiplyMC(matrix, Base.camera);
        GLES20.glUniformMatrix4fv(shader.handle[0], 1, false, matrix, 0);
        BaseGLBuffer.glBindElements(Rect.face);
        BaseGLBuffer.glDrawElements(8);
        BaseGLBuffer.glUnbind();
    }

    public static void bindRect(BaseShader shader){

        BaseGLBuffer.glBindArray(Rect.vert, shader.handle[1], 2);
        BaseGLBuffer.glBindArray(Rect.text, shader.handle[2], 2);
        BaseGLBuffer.glBindElements(Rect.face);
    }

    public static void bindRectVerts(BaseShader shader){

        BaseGLBuffer.glBindArray(Rect.vert, shader.handle[1], 2);
        BaseGLBuffer.glBindElements(Rect.face);
    }

    public static void drawRect(BaseShader shader, float x, float y, float z, float w, float h, float rot){

        BaseMatrix.transform(matrix, x, y, z, rot, w, h);
        BaseMatrix.multiplyMC(matrix, Base.camera);
        GLES20.glUniformMatrix4fv(shader.handle[0], 1, false, matrix, 0);
        BaseGLBuffer.glDrawElements(8);
    }

    public static void drawRect(BaseShader shader, float x, float y, float z, float scale, float rot){

        BaseMatrix.transform(matrix, x, y, z, rot, scale);
        BaseMatrix.multiplyMC(matrix, Base.camera);
        GLES20.glUniformMatrix4fv(shader.handle[0], 1, false, matrix, 0);
        BaseGLBuffer.glDrawElements(8);
    }

    public static void drawRect(BaseShader shader, float x, float y, float z, float scale){

        BaseMatrix.translateS(matrix, x, y, z);
        BaseMatrix.scale(matrix, scale);
        BaseMatrix.multiplyMC(matrix, Base.camera);
        GLES20.glUniformMatrix4fv(shader.handle[0], 1, false, matrix, 0);
        BaseGLBuffer.glDrawElements(8);
    }

    public static void drawRect(BaseShader shader, float x, float y, float z){

        BaseMatrix.translateS(matrix, x, y, z);
        BaseMatrix.multiplyMC(matrix, Base.camera);
        GLES20.glUniformMatrix4fv(shader.handle[0], 1, false, matrix, 0);
        BaseGLBuffer.glDrawElements(8);
    }

    public static void unbind(){

        BaseGLBuffer.glUnbind();
    }

    public static boolean isInitialized(){

        return initialized;
    }

    private static class Rect {

        private static int vert;
        private static int text;
        private static int face;

        private static void init(){

            vert = BaseGL.genArrayFloatBuffer(Buffers.floatBuffer(DrawableData.rectangleVertices(0.5f, 0.5f)));
            text = BaseGL.genArrayFloatBuffer(Buffers.floatBuffer(DrawableData.rectangleTextures(0.5f, 0.5f, 0.5f, 0.5f)));
            face = BaseGL.genElementShortBuffer(Buffers.shortBuffer(DrawableData.rectangleFaces()));
        }

        private static void destroy(){

            BaseGL.destroyBuffers(vert, text, face);
        }
    }

}
