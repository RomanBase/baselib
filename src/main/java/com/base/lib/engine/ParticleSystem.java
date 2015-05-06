package com.base.lib.engine;

import android.opengl.GLES20;
import android.os.SystemClock;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.base.lib.engine.common.BaseDrawableData;
import com.base.lib.engine.common.BaseMatrix;
import com.base.lib.engine.common.Buffers;
import com.base.lib.engine.common.Colorf;
import com.base.lib.engine.common.DrawableData;
import com.base.lib.engine.common.Point3;

/**
 *
 */
public class ParticleSystem {

    private Random random = new Random(SystemClock.uptimeMillis());
    private DrawableBuffer sBuffer;
    private FloatBuffer[] sTexCoords;
    private InnerActions sAction;
    private InnerActions[] sActions;

    private BaseCamera sCamera;
    private Texture sTexture = BaseGL.baseTexture;

    private List<Particle> toUse;
    private List<Particle> used;

    private float boxX = 0.0f;
    private float boxY = 0.0f;
    private float boxZ = 0.0f;

    private float dirRandom = 0.0f;
    private float rotRandom = 0.0f;
    private float scaleRandom = 0.0f;

    private boolean resetWhenDone = true;
    private boolean useBillboards = false;

    public ParticleSystem(BaseCamera camera, float sx, float sy, float[][] textureCoords, InnerAction[] actions){
        this(camera, DrawableData.RECTANGLE(sx, sy), textureCoords, actions);
    }

    public ParticleSystem(BaseCamera camera, BaseDrawableData data, float[][] texturesCoords, InnerAction[] actions){
        this(camera, new DrawableBuffer(data), texturesCoords, new InnerActions(actions));
    }

    public ParticleSystem(BaseCamera camera, DrawableBuffer dBuffer, float[][] texturesCoords, InnerActions actions){
        sCamera = camera;
        sAction = actions;

        sBuffer = dBuffer;
        sBuffer.setShader(BaseShader.get(3));
        if(texturesCoords != null){
            int count = texturesCoords.length;
            sTexCoords = new FloatBuffer[count];
            for(int i = 0; i<count; i++){
                sTexCoords[i] = Buffers.floatBuffer(texturesCoords[i]);
            }
        } else {
            sTexCoords = new FloatBuffer[1];
            sTexCoords[0] = dBuffer.getTextureBuffer();
        }

        toUse = new ArrayList<Particle>();
        used = new ArrayList<Particle>();
    }

    public void init(int count){

        sActions = new InnerActions[count];
        float[] matrix = new float[16];

        for (int i = 0; i<count; i++){
            InnerAction[] actions = new InnerAction[sAction.actions.length];
            for(int n = 0; n<actions.length; n++){
                actions[n] = new InnerAction(sAction.actions[n]);
            }
            float r = random.nextFloat()*2.0f-1.0f;

            for(int j = 0; j<actions.length; j++) {
                if (j == 0 && boxX != 0.0f && boxY != 0.0f && boxZ != 0.0f) {
                    actions[j].modifyStartPos(boxX * r, boxY * r, boxZ * r);
                }

                Point3 trans = new Point3();
                if (j == 0 && dirRandom != 0.0f) {
                    float angle = 180 * dirRandom * r;
                    for(int k = 0; k<actions.length; k++) {
                        Point3 fPos = actions[k].getfPos();
                        Point3 sPos = actions[k].getsPos();
                        Point3.copy(sPos, trans);
                        if (k == 0) {
                            BaseMatrix.setIdentity(matrix);
                            angle *= random.nextFloat();
                        } else {
                            Point3.sub(trans, actions[k-1].getsPos());
                            angle = 0;
                        }

                        BaseMatrix.translate(matrix, trans.x, trans.y, trans.z);
                        BaseMatrix.rotate(matrix, 0, 0, angle);

                        actions[k].setfPos(BaseMatrix.multiplyMV(matrix, fPos.x-sPos.x, fPos.y-sPos.y, fPos.z-sPos.z));
                    }
                }

                if (rotRandom != 0.0f) {
                    Point3 sr = actions[j].getsRot();
                    Point3 fr = actions[j].getfRot();
                    float rr = r*rotRandom;
                    float rx = (fr.x - sr.x)*rr;
                    float ry = (fr.y - sr.y)*rr;
                    float rz = (fr.z - sr.z)*rr;

                    actions[j].setFinalRot(fr.x+rx, fr.y+ry, fr.z+rz);
                }

                if(scaleRandom != 0.0f){
                    float ss = actions[j].getsScale();
                    float fs = actions[j].getfScale();
                    float rr = scaleRandom*r;
                    actions[j].setsScale(ss+ss*rr);
                    actions[j].setfScale(fs+fs*rr);
                }

            }
            sActions[i] = new InnerActions(actions);
        }

        initModels(count);
    }

    void initModels(int count){

        int texCount = sTexCoords.length -1;
        for(int i = 0; i<count; i++){
            int texIndex = (texCount > 0) ? random.nextInt(texCount) : 0;
            toUse.add(new Particle(sActions[i], texIndex));
        }

        Collections.shuffle(toUse);
    }

    public void blow(final float x, final float y, final float z, final int count){

        if(!toUse.isEmpty()) {
            final BaseRenderer render = Base.render;
            render.runOnBaseThread(new Runnable() {
                @Override
                public void run() {
                    int size = toUse.size();
                    boolean thru = size > count;
                    int length = (thru) ? count : size;
                    int index = (thru) ? random.nextInt(size - count) : 0;
                    for (int i = 0; i < length; i++) {
                        Particle particle = toUse.remove(index);
                        render.addDrawable(particle.blow(x, y, z));
                        used.add(particle);
                    }
                }
            });
        }
    }

    public void setSpawnArea(float hx, float hy, float hz){

        boxX = hx;
        boxY = hy;
        boxZ = hz;
    }

    public void setDirectionRandomness(float randomness/*, boolean 2d ?*/){

        dirRandom = randomness;
    }

    public void setScaleRandomness(float randomness){

        scaleRandom = randomness;
    }

    public void setRotationRandomness(float randomness){

        rotRandom = randomness;
    }

    public void setLooping(boolean looping){

        resetWhenDone = looping;
    }

    public Texture getTexture() {
        return sTexture;
    }

    public void setTexture(Texture texture){
        sTexture = texture;
    }

    public BaseShader getShader(){
        return sBuffer.getShader();
    }

    public void setShader(BaseShader shader){
        sBuffer.setShader(shader);
    }

    public boolean isDone(){

        return toUse.isEmpty() && used.get(used.size()-1).action.currentAction.isDone();
    }

    public void reset(){

        toUse.addAll(used);
        used.clear();
        Collections.shuffle(toUse);
    }

    private class Particle {

        private InnerActions action;
        private int texCoordsIndex;

        private Particle(InnerActions action, int texCoordsIndex){

            this.action = action;
            this.texCoordsIndex = texCoordsIndex;
        }

        BaseRenderable blow(float x, float y, float z){

            return new DrawableParticle(this, x, y, z);
        }
    }

    private class DrawableParticle extends DrawableModel {

        private Particle particle;
        private float t = 0.0f;
        private float cX, cY, cZ;

        private DrawableParticle(Particle particle, float centerX, float centerY, float centerZ){
            super(sCamera);

            particle.action.reset();
            this.particle = particle;

            texture = sTexture;
            shader = sBuffer.shader;
            cX = centerX;
            cY = centerY;
            cZ = centerZ;
        }

        @Override
        public void update() {

            if(particle.action.isDone()){
                if(resetWhenDone){
                    used.remove(particle);
                    toUse.add(particle);
                }
                unUse();
            }

            BaseMatrix.translateS(modelMatrix, cX, cY, cZ);
            transform(particle.action.next());
            super.update();
        }

        @Override
        public void draw() {

            Colorf c = particle.action.currentAction.color;
            GLES20.glUniform4f(shader.handle[3], c.r, c.g, c.b, c.a);
            BaseGL.glError();
            sBuffer.glPutVerticeBuffer();
            sBuffer.glPutTextureBuffer(sTexCoords[particle.texCoordsIndex]);
            sBuffer.drawPutModel(this);
            sBuffer.glDisableAttribArray();
        }
    }
}
