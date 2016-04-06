package com.base.lib.engine.particles;

/**
 *
 */
public abstract class EmptyParticleAction {

    public long actionLifeTime;
    public long actionRemainingTime;
    public float x, y, z, s;
    public float progress;

    public abstract void onActionEnds();
    public abstract void update(float delta, float delay);

    public void setLifeTime(long millis){
        actionLifeTime = actionRemainingTime = millis;
    }

    public void setAttr(float ax, float ay, float az, float as){

        x = ax;
        y = ay;
        z = az;
        s = as;
    }

    public void copy(EmptyParticleAction action, long millis){

        x = action.x;
        y = action.y;
        z = action.z;
        s = action.s;
        actionLifeTime = actionRemainingTime = millis;
    }

    public void setSteps(float velocity, ParticleEmiter emiter, float randomness){

        float[] vec = emiter.getDir(0, randomness);
        x = velocity * vec[0];
        y = velocity * vec[1];
        z = velocity * vec[2];
    }

    public long getActionLifeTime() {
        return actionLifeTime;
    }

    public void setActionLifeTime(long actionLifeTime) {
        this.actionLifeTime = actionLifeTime;
    }

    public long getActionRemainingTime() {
        return actionRemainingTime;
    }

    public void setActionRemainingTime(long actionRemainingTime) {
        this.actionRemainingTime = actionRemainingTime;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getZ() {
        return z;
    }

    public void setZ(float z) {
        this.z = z;
    }

    public float getS() {
        return s;
    }

    public void setS(float s) {
        this.s = s;
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
    }
}
