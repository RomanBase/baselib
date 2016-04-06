package com.base.lib.engine.particles;

import java.util.Random;

/**
 *
 */
public class BaseParticle {

    protected boolean inUse;
    protected float[] data; //0x 1y 2z 3s 4tx 5ty 6r 7g 8b 9a
    protected EmptyParticleAction action;

    public BaseParticle() {
        data = new float[10];

        data[6] = 1.0f;
        data[7] = 1.0f;
        data[8] = 1.0f;
        data[9] = 1.0f;
    }

    public BaseParticle setBaseAction(){

        action = new EmptyParticleAction() {
            @Override
            public void onActionEnds() {
                inUse = false;
            }

            @Override
            public void update(float delta, float delay) {
                actionRemainingTime -= delay;

                if (actionRemainingTime > 0) {
                    progress = (float) actionRemainingTime /(float) actionLifeTime;
                    data[0] += x * delta;
                    data[1] += y * delta;
                    data[2] += z * delta;
                    data[3] += s * delta;
                } else {
                    action.onActionEnds();
                }
            }
        };

        return this;
    }

    public BaseParticle setAction(EmptyParticleAction action){

        this.action = action;

        return this;
    }

    protected void init(BaseParticleSystem ps, int emiterOffset) {

        Random random = ps.random;
        action.actionLifeTime = action.actionRemainingTime = (long) (ps.minLifeTime + ps.lifetimeOffset * random.nextFloat());

        float speedModifier = ps.minSpeed + ps.speedOffset * random.nextFloat();
        float[] vec = ParticleEmiter.vecs;

        ps.emiter.getPos(emiterOffset);
        data[0] = vec[0];
        data[1] = vec[1];
        data[2] = vec[2];
        if (ps.directionRandomness > 0) {
            ps.emiter.getDir(emiterOffset, ps.directionRandomness);
        } else {
            ps.emiter.getDir(emiterOffset);
        }
        action.x = vec[0] * speedModifier;
        action.y = vec[1] * speedModifier;
        action.z = vec[2] * speedModifier;

        data[3] = ps.minScale + ps.scaleOffset * random.nextFloat();
        action.s = ps.minScaleStep + ps.scaleStepOffset * random.nextFloat();

        data[4] = ps.spriteSize * random.nextInt(ps.spriteCount);
        data[5] = ps.spriteSize * random.nextInt(ps.spriteCount);

        inUse = true;
    }

    public void modifyPos(float x, float y, float z){

        data[0] += x;
        data[1] += y;
        data[2] += z;
    }

    protected BaseParticle weakReference(){

        return this;
    }
}
