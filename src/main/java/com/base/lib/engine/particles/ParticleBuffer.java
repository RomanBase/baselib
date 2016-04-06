package com.base.lib.engine.particles;

import android.opengl.GLES20;

import com.base.lib.engine.BaseCamera;
import com.base.lib.engine.BaseShader;
import com.base.lib.engine.common.Buffers;
import com.base.lib.engine.common.gl.BaseGLBuffer;

import java.nio.FloatBuffer;

/**
 *
 */
public class ParticleBuffer {

    private static final int ELEMENTS = 10;

    private int count;
    private final int stride;
    private final float[] array;
    private final FloatBuffer arrayBuffer;

    public ParticleBuffer(int capacity){

        stride = ELEMENTS * Buffers.BYTESPERFLOAT; //stride -> 40 : (4+2+4)*4
        array = new float[capacity*ELEMENTS];
        arrayBuffer = Buffers.floatBuffer(array);
    }

    protected void draw(BaseShader shader, BaseCamera camera, float spriteSize) {

        arrayBuffer.position(0);
        arrayBuffer.put(array);

        BaseGLBuffer.glPutArray(arrayBuffer, shader.handle[1], 4, 0, stride);
        BaseGLBuffer.glPutArray(arrayBuffer, shader.handle[2], 2, 4, stride);
        BaseGLBuffer.glPutArray(arrayBuffer, shader.handle[3], 4, 6, stride);
        GLES20.glUniform1f(shader.handle[4], camera.reverseRatio);
        GLES20.glUniform1f(shader.handle[5], spriteSize);
        GLES20.glUniformMatrix4fv(shader.handle[0], 1, false, camera.mVPMatrix, 0);
        BaseGLBuffer.glDrawPointsArray(count);
        BaseGLBuffer.glDisableAttribArray(shader.handle[1], shader.handle[2], shader.handle[3]);

        count = 0;
    }

    public void addParticleData(float[] data){

        System.arraycopy(data, 0, array, count*ELEMENTS, ELEMENTS);
        count++;
    }

    public void addParticleData(int particleCount, float... data){

        System.arraycopy(data, 0, array, count*ELEMENTS, data.length);
        count+=particleCount;
    }

    public boolean isEmpty(){

        return count == 0;
    }

}