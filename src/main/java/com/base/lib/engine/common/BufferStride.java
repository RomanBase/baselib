package com.base.lib.engine.common;

import java.nio.FloatBuffer;

/**
 *
 */
public class BufferStride {


    public static FloatBuffer stride332(float[] verts, float[] norms, float[] texts){

        float[] data = new float[verts.length + norms.length + texts.length];

        int v = 0;
        int n = 0;
        int t = 0;
        for(int i = 0; i<data.length;){
            data[i++] = verts[v++];
            data[i++] = verts[v++];
            data[i++] = verts[v++];

            data[i++] = norms[n++];
            data[i++] = norms[n++];
            data[i++] = norms[n++];

            data[i++] = texts[t++];
            data[i++] = texts[t++];
        }

        return Buffers.floatBuffer(data);
    }
}
