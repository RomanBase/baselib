package com.base.lib.engine.particles;

import com.base.lib.engine.common.BaseDrawableData;
import com.base.lib.engine.common.BaseMatrix;

import java.util.Random;

/**
 *
 */
public class ParticleEmiter {

    protected static final float[] vecs = new float[3];

    private float[] vertices;
    private float[] normals;
    private float[] segments;
    public float[] verticesOut;
    public float[] normalsOut;
    private float[] segmentsOut;

    protected float largestSegment;
    protected float segmentsSurface;
    protected int emitCount;
    protected int currentFaceIndex = -3;

    private int loopCount;

    private Random random;

    public int nextFaceIndex() {

        if (currentFaceIndex < loopCount) {
            currentFaceIndex += 3;
        } else {
            currentFaceIndex = 0;
        }

        return currentFaceIndex;
    }

    public void initMesh(BaseDrawableData data) {//todo

    }

    public void initCurve(float[] verts) {

        int count = verts.length - 3;
        loopCount = count - 3;
        emitCount = count / 3;
        normals = new float[count];
        vertices = new float[count];
        segments = new float[count];
        float[] vec = new float[2];
        int index = 0;
        for (int i = 0; i < count; ) {

            vec[0] = verts[i + 1] - verts[i + 4];
            vec[1] = verts[i] - verts[i + 3];
            normalizeVec(vec);
            normals[index] = vec[0];
            normals[index + 1] = -vec[1];
            normals[index + 2] = 0.0f;

            vertices[index] = (verts[i] + verts[i + 3]) * 0.5f;
            vertices[index + 1] = (verts[i + 1] + verts[i + 4]) * 0.5f;
            vertices[index + 2] = (verts[i + 2] + verts[i + 5]) * 0.5f;

            /*vertices[index] = verts[i];
            vertices[index + 1] = verts[i + 1];
            vertices[index + 2] = verts[i + 2];*/

            segments[index] = (verts[i + 3] - verts[i]) * 0.5f;
            segments[index + 1] = (verts[i + 4] - verts[i + 1]) * 0.5f;
            segments[index + 2] = (verts[i + 5] - verts[i + 2]) * 0.5f;

            i += 3;
            index += 3;
        }

        verticesOut = new float[count];
        normalsOut = new float[count];
        segmentsOut = new float[count];
        System.arraycopy(vertices, 0, verticesOut, 0, count);
        System.arraycopy(normals, 0, normalsOut, 0, count);
        System.arraycopy(segments, 0, segmentsOut, 0, count);
    }

    private void findLargestSegment() {

        largestSegment = 0;
        segmentsSurface = 0;
        float temp;
        for (int i = 0; i < segments.length; i += 3) {
            temp = getSegmentWeight(i);
            segmentsSurface += temp;
            if (temp > largestSegment) {
                largestSegment = temp;
            }
        }
    }

    private float pow2(float num) {

        return num * num;
    }

    private void normalizeVec(float[] vec) {

        double mag = Math.sqrt(vec[0] * vec[0] + vec[1] * vec[1]);

        vec[0] /= mag;
        vec[1] /= mag;
    }

    public void initPoint() {

        vertices = new float[3];
        normals = new float[3];
        segments = new float[3];
        verticesOut = new float[3];
        normalsOut = new float[3];
        segmentsOut = new float[3];
        normals[0] = normalsOut[0] = 0.0f;
        normals[1] = normalsOut[1] = 1.0f;
        normals[2] = normalsOut[2] = 0.0f;
    }

    public void setNormalDirection(float x, float y, float z) {

        for (int i = 0; i < normals.length; ) {
            normals[i] = normalsOut[i++] = x;
            normals[i] = normalsOut[i++] = y;
            normals[i] = normalsOut[i++] = z;
        }
    }

    public float[] getPos(int index) {

        vecs[0] = verticesOut[index];
        vecs[1] = verticesOut[index + 1];
        vecs[2] = verticesOut[index + 2];

        return vecs;
    }

    public float[] getDir(int index) {

        vecs[0] = normalsOut[index];
        vecs[1] = normalsOut[index + 1];
        vecs[2] = normalsOut[index + 2];

        return vecs;
    }

    public float[] getDir(int index, float randomness) {

        BaseMatrix.setSMIdentity();
        BaseMatrix.rotateZ(BaseMatrix._matrix, -180.0f * randomness + 360.0f * randomness * random.nextFloat());
        float[] v = BaseMatrix.multiplyMV(BaseMatrix._matrix, normalsOut[index], normalsOut[index + 1], normalsOut[index + 2]);

        vecs[0] = v[0];
        vecs[1] = v[1];
        vecs[2] = v[2];

        return vecs;
    }

    public float[] getSegmentSize(int index) {

        vecs[0] = segmentsOut[index];
        vecs[1] = segmentsOut[index + 1];
        vecs[2] = segmentsOut[index + 2];

        return vecs;
    }

    public float getSegmentWeight(int index) {

        return (float) Math.sqrt(pow2(segmentsOut[index]) + pow2(segmentsOut[index + 1]) + pow2(segmentsOut[index + 2]));
    }

    public void transformVertices(float posX, float posY, float posZ, float scaleX, float scaleY, float scaleZ) {

        BaseMatrix.setSMIdentity();
        BaseMatrix.scale(BaseMatrix._matrix, scaleX, scaleY, scaleZ);
        BaseMatrix.translateS(BaseMatrix._matrix, posX, posY, posZ);
        BaseMatrix.multiplyMA(BaseMatrix._matrix, vertices, verticesOut);
    }

    public void transformSegments(float scaleX, float scaleY, float scaleZ) {

        BaseMatrix.setSMIdentity();
        BaseMatrix.scale(BaseMatrix._matrix, scaleX, scaleY, scaleZ);
        BaseMatrix.multiplyMA(BaseMatrix._matrix, segments, segmentsOut);
    }

    public void rotateVerticesZ(float rotZ) {

        BaseMatrix.setSMIdentity();
        BaseMatrix.rotateZ(BaseMatrix._matrix, rotZ);
        BaseMatrix.multiplyMA(BaseMatrix._matrix, vertices, verticesOut);
    }

    public void rotateDirections(float rotx, float roty, float rotz) {

        BaseMatrix.setSMIdentity();
        BaseMatrix.rotate(BaseMatrix._matrix, rotx, roty, rotz);
        BaseMatrix.multiplyMA(BaseMatrix._matrix, normals, normalsOut);
    }

    public void reverseDirection() {

        BaseMatrix.setSMIdentity();
        BaseMatrix.rotateZ(BaseMatrix._matrix, 180.0f);
        BaseMatrix.multiplyMA(BaseMatrix._matrix, normals, normalsOut);
    }

    public void translatePoint(float x, float y, float z) {

        verticesOut[0] = x;
        verticesOut[1] = y;
        verticesOut[2] = z;
    }

    public void persist() {

        vertices[0] = verticesOut[0];
        vertices[1] = verticesOut[1];
        vertices[2] = verticesOut[2];
    }

    public void direct() {

        normalsOut[0] = verticesOut[0];
        normalsOut[1] = verticesOut[1];
        normalsOut[2] = verticesOut[2];

        double m = Math.sqrt(normalsOut[0] * normalsOut[0] + normalsOut[1] * normalsOut[1] + normalsOut[2] * normalsOut[2]);

        normalsOut[0] /= m;
        normalsOut[1] /= m;
        normalsOut[2] /= m;
    }

    public int getEmit() {

        return emitCount;
    }

    public void setRandom(Random random) {
        this.random = random;
    }

    public static ParticleEmiter point() {

        ParticleEmiter emiter = new ParticleEmiter();
        emiter.initPoint();
        emiter.emitCount = 1;

        return emiter;
    }

    public static ParticleEmiter curve(float[] vertices) {

        ParticleEmiter emiter = new ParticleEmiter();
        emiter.initCurve(vertices);

        return emiter;
    }

    public static ParticleEmiter rectangular(float width, float height) {

        float[] verts = new float[15];
        float w = width * 0.5f;
        float h = height * 0.5f;

        verts[0] = verts[9] = verts[12] = -w;
        verts[1] = verts[4] = verts[13] = h;
        verts[3] = verts[6] = w;
        verts[7] = verts[10] = -h;

        return curve(verts);
    }
}
