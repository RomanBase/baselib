package com.base.lib.engine.glcommon;

import android.opengl.GLES20;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 *
 */
public abstract class BaseGLBuffer {

    //---------------------------SYSTEM ELEMENT BUFFER----------------------------------------------
    public static void glPutArray(FloatBuffer buffer, int shaderHandle, int elementSize){
        buffer.position(0);
        GLES20.glVertexAttribPointer(shaderHandle, elementSize, GLES20.GL_FLOAT, false, 0, buffer);
        GLES20.glEnableVertexAttribArray(shaderHandle);
    }

    public static void glPutArray(FloatBuffer buffer, int shaderHandle, int elementSize, int bufferPositionOffset, int stride){
        buffer.position(bufferPositionOffset);
        GLES20.glVertexAttribPointer(shaderHandle, elementSize, GLES20.GL_FLOAT, false, stride, buffer);
        GLES20.glEnableVertexAttribArray(shaderHandle);
    }

    public static void glDrawElements(ShortBuffer buffer){
        buffer.position(0);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, buffer.capacity(), GLES20.GL_UNSIGNED_SHORT, buffer);
    }

    public static void glDrawElements(ShortBuffer buffer, int glDrawMode){
        buffer.position(0);
        GLES20.glDrawElements(glDrawMode, buffer.capacity(), GLES20.GL_UNSIGNED_SHORT, buffer);
    }

    public static void glDrawElements(ShortBuffer buffer, int count, int glDrawMode){
        buffer.position(0);
        GLES20.glDrawElements(glDrawMode, count, GLES20.GL_UNSIGNED_SHORT, buffer);
    }

    public static void glDisableAttribArray(int shaderHandle){

        GLES20.glDisableVertexAttribArray(shaderHandle);
    }

    public static void glDisableAttribArray(int vsh, int tsh){

        GLES20.glDisableVertexAttribArray(vsh);
        GLES20.glDisableVertexAttribArray(tsh);
    }

    public static void glDisableAttribArray(int vsh, int tsh, int nsh){

        GLES20.glDisableVertexAttribArray(vsh);
        GLES20.glDisableVertexAttribArray(tsh);
        GLES20.glDisableVertexAttribArray(nsh);
    }

    public static void glDisableAttribArray(int... shaderHandles){

        for(int handle : shaderHandles){
            GLES20.glDisableVertexAttribArray(handle);
        }
    }

    //------------------------VIDEO ELEMENT BUFFER--------------------------------------------------
    public static void glBindArray(int glBufferID, int shaderHandle, int elementSize){

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, glBufferID);
        GLES20.glVertexAttribPointer(shaderHandle, elementSize, GLES20.GL_FLOAT, false, 0, 0);
        GLES20.glEnableVertexAttribArray(shaderHandle);
    }

    public static void glBindArray(int glBufferID, int shaderHandle, int elementSize, int stride){

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, glBufferID);
        GLES20.glVertexAttribPointer(shaderHandle, elementSize, GLES20.GL_FLOAT, false, stride, 0);
        GLES20.glEnableVertexAttribArray(shaderHandle);
    }

    public static void glBindArray(int glBufferID, int shaderHandle, int elementSize, int stride, int offset){

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, glBufferID);
        GLES20.glVertexAttribPointer(shaderHandle, elementSize, GLES20.GL_FLOAT, false, stride, offset);
        GLES20.glEnableVertexAttribArray(shaderHandle);
    }

    public static void glBindElements(int glBufferID){

        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, glBufferID);
    }

    public static void glDrawElements(int count, int glDrawMode){

        GLES20.glDrawElements(glDrawMode, count, GLES20.GL_UNSIGNED_SHORT, 0);
    }

    public static void glDrawElements(int count){

        GLES20.glDrawElements(GLES20.GL_TRIANGLES, count, GLES20.GL_UNSIGNED_SHORT, 0);
    }

    public static void glUnbind(){

        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
    }

    //-----------------------------ARRAY DRAW-------------------------------------------------------
    public static void glDrawArrays(int drawMode, int count){

        GLES20.glDrawArrays(drawMode, 0, count);
    }

    public static void glDrawArrays(int count){

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, count);
    }

    public static void glDrawPointsArray(int count){

        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, count);
    }

    public interface Interface {
        public void glBindData();
        public void glDraw();
        public void glUnbindData();
    }
}
