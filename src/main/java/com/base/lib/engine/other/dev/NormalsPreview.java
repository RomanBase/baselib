package com.base.lib.engine.other.dev;

import android.opengl.GLES20;

import com.base.lib.R;
import com.base.lib.engine.Base;
import com.base.lib.engine.BaseRender;
import com.base.lib.engine.BaseRenderable;
import com.base.lib.engine.BaseShader;
import com.base.lib.engine.common.BaseMatrix;
import com.base.lib.engine.common.Buffers;
import com.base.lib.engine.glcommon.BaseGLBuffer;

import java.nio.FloatBuffer;

/**
 *
 */
public class NormalsPreview extends BaseRenderable {

    float[] modelMatrix;
    float[] lineVerts;
    FloatBuffer linesBuffer;
    int count;

    public NormalsPreview(float[] vertices, float[] normals, short[] faces, float[] matrix, float w) {

        shader = new BaseShader("normal_sh", "u_MVPMatrix", "a_Position", "u_Color")
                .loadShadersFromResources(R.raw.one_color_vertex, R.raw.color_fragment_shader);

        if (faces == null) {
            faces = new short[vertices.length / 3];
            for (short i = 0; i < faces.length; i++) {
                faces[i] = i;
            }
        }

        count = faces.length / 3;
        lineVerts = new float[count * 3 * 2];
        modelMatrix = matrix;

        fillLineVerts(vertices, normals, faces, w);
    }

    public NormalsPreview(float[] vertices, float[] normals, float[] matrix, float w) {

        shader = new BaseShader("normal_sh", "u_MVPMatrix", "a_Position", "u_Color")
                .loadShadersFromResources(R.raw.one_color_vertex, R.raw.color_fragment_shader);

        count = normals.length / 3;
        lineVerts = new float[count * 3 * 2];
        modelMatrix = matrix;

        fillLineVerts(vertices, normals, w);
    }

    private void fillLineVerts(float[] verts, float[] norms, short[] faces, float w) {

        int index = 0;
        for (int i = 0; i < faces.length; ) {
            int stV = faces[i++] * 3;
            int ndV = faces[i++] * 3;
            int thV = faces[i++] * 3;
            int bIndex = index;
            lineVerts[index++] = (verts[stV] + verts[ndV] + verts[thV]) / 3;
            lineVerts[index++] = (verts[stV + 1] + verts[ndV + 1] + verts[thV + 1]) / 3;
            lineVerts[index++] = (verts[stV + 2] + verts[ndV + 2] + verts[thV + 2]) / 3;

            lineVerts[index++] = lineVerts[bIndex] + norms[stV] * w;
            lineVerts[index++] = lineVerts[bIndex + 1] + norms[stV + 1] * w;
            lineVerts[index++] = lineVerts[bIndex + 2] + norms[stV + 2] * w;
        }

        linesBuffer = Buffers.floatBuffer(lineVerts);
    }

    private void fillLineVerts(float[] verts, float[] norms, float w) {

        int index = 0;
        for (int i = 0; i < verts.length; i += 3) {
            lineVerts[index++] = verts[i];
            lineVerts[index++] = verts[i+1];
            lineVerts[index++] = verts[i+2];

            lineVerts[index++] = verts[i] + norms[i] * w;
            lineVerts[index++] = verts[i+1] + norms[i+1] * w;
            lineVerts[index++] = verts[i+2] + norms[i+2] * w;
        }

        linesBuffer = Buffers.floatBuffer(lineVerts);
    }

    @Override
    public void draw() {

        GLES20.glUniform4f(shader.handle[2], 0.0f, 1.0f, 1.0f, 1.0f);
        BaseGLBuffer.glPutArray(linesBuffer, shader.handle[1], 3);
        BaseMatrix.multiplyMC(modelMatrix, Base.camera);
        BaseMatrix.glPutMatrix(modelMatrix, shader.handle[0]);
        BaseGLBuffer.glDrawArrays(GLES20.GL_LINES, count);
        BaseGLBuffer.glDisableAttribArray(shader.handle[1]);
    }

    @Override
    public void update() {

    }

    @Override
    public void destroy() {

    }

    @Override
    public void use() {

        inUse = true;
        ((BaseRender) Base.render).addPostDrawable(this);
    }
}
