package com.base.lib.engine.particles;

import com.base.lib.engine.Base;
import com.base.lib.engine.BaseGL;
import com.base.lib.engine.BaseRenderable;
import com.base.lib.engine.BaseShader;
import com.base.lib.engine.BaseTime;
import com.base.lib.engine.Texture;

import java.util.Random;

/**
 *
 */
public class BaseParticleSystem extends BaseRenderable { //todo variables per sec instead of step? just think about it ;)

    protected Random random = Base.random;

    protected ParticleEmiter emiter;
    protected long minLifeTime, lifetimeOffset;
    protected float minSpeed, speedOffset, minScale, scaleOffset, directionRandomness;
    protected float /*minRotStep, rotStepOffset,*/ minScaleStep, scaleStepOffset;
    protected int spriteCount;
    protected float spriteSize;

    private long curretDelay;
    private long emitDelay;
    private int requestedCount;
    private int currentIndex;
    private boolean continiously;
    private boolean isEmitTime;

    private Texture texture;
    private ParticleBuffer buffer;
    private final BaseParticle[] particles;
    private final int capacity;

    /**
     * use a lof of setMethods to customize particle effect, create you own BaseParticle constructor and after effects via ParticleAction.,
     * @param capacity initial capacity of buffer
     * @param buffer can be null
     * @param constructor can be null
     * @param emiter can be null
     */
    public BaseParticleSystem(int capacity, ParticleBuffer buffer, ParticleConstructor constructor, ParticleEmiter emiter) {

        init();
        this.capacity = capacity;
        particles = new BaseParticle[capacity];

        if (buffer != null) {
            setParticleBuffer(buffer);
        } else {
            setParticleBuffer(new ParticleBuffer(capacity));
        }

        if (constructor != null) {
            initBaseParticles(constructor);
        } else {
            initBaseParticles();
        }

        if (emiter != null) {
            setEmiter(emiter);
        } else {
            setEmiter(ParticleEmiter.point());
        }
    }

    private void init() {

        inUse = false;
        isEmitTime = true;
        minLifeTime = 1000;
        minScale = 1.0f;
        minSpeed = 0.01f;
        requestedCount = 1;
        spriteSize = 1.0f;
        shader = BaseShader.get("particle");
    }

    public void reset(){

        currentIndex = 0;
        for(BaseParticle particle : particles){
            particle.inUse = false;
        }
    }

    public void initBaseParticles(final ParticleConstructor constructor) {

        for (int i = 0; i < capacity; i++) {
            particles[i] = constructor.newInstance();
        }
    }

    protected void initBaseParticles(){

        initBaseParticles(new ParticleConstructor() {
            @Override
            public BaseParticle newInstance() {

                return new BaseParticle().setBaseAction();
            }
        });
    }

    public void setParticleBuffer(ParticleBuffer customBuffer) {

        buffer = customBuffer;
    }

    public boolean isEmitTime(){

        return isEmitTime = (curretDelay += BaseTime.delay) > emitDelay;
    }

    public void emitParticles() {

        if (isEmitTime) {
            curretDelay = 0;

            if(!inUse){
                use();
            }

            if (currentIndex == capacity) {
                if (continiously) {
                    currentIndex = 0;
                } else {
                    inUse = false;
                }
            }

            int count = capacity - currentIndex;

            if (count > requestedCount) {
                count = currentIndex + requestedCount;
            } else {
                count = capacity;
            }

            if (count > currentIndex+emiter.emitCount) {
                for (int i = 0; i<emiter.emitCount; i++) {
                    if (particles[currentIndex].inUse) {
                        return;
                    }

                    particles[currentIndex++].init(this, emiter.nextFaceIndex());
                }
                int cycle = 5;
                while (currentIndex < count) {
                    emiter.currentFaceIndex = -3;

                    if(cycle-- > 1){
                        emitWholeEmiter(cycle, count, cycle*0.25f);
                        emitWholeEmiter(cycle, count, -cycle*0.25f);
                    } else {
                        emitWholeEmiter(cycle, count, random.nextFloat());
                        emitWholeEmiter(cycle, count, -random.nextFloat());
                    }
                }
            } else {
                while (currentIndex < count) {
                    if (particles[currentIndex].inUse) {
                        break;
                    }

                    particles[currentIndex++].init(this, emiter.nextFaceIndex());
                }
            }
        }
    }

    private void emitWholeEmiter(int cycle, int count, float modifier){ //todo emit in particle init

        if(count - currentIndex < emiter.emitCount){
            count = count - currentIndex;
        } else {
            count = emiter.emitCount;
        }

        float[] segment = ParticleEmiter.vecs;
        for (int i = 0; i<count; i++){
            BaseParticle particle = particles[currentIndex++];
            if (particle.inUse) {
                break;
            }
            emiter.nextFaceIndex();
            particle.init(this, emiter.currentFaceIndex);
            emiter.getSegmentSize(emiter.currentFaceIndex);
            particle.modifyPos(segment[0]*modifier, segment[1]*modifier, segment[2]*modifier);
        }
    }

    public void emitContinuoslyWithDraw(){

        if(isEmitTime()) {
            inUse = true;
            emitParticles();
        }

        BaseGL.useProgram(shader);
        draw();
    }

    public void emitContinuosly(){

        if(isEmitTime()) {
            emitParticles();
        }
    }

    @Override
    public void update() {

    }

    @Override
    public void draw() {

        for (BaseParticle particle : particles) {
            if (particle.inUse) {
                particle.action.update(BaseTime.deltaStep);
                buffer.addParticleData(particle.data);
            }
        }

        if(!buffer.isEmpty()) {
            BaseGL.bindTexture(texture.glid);
            buffer.draw(shader, camera, spriteSize);
        } else {
            unUse();
        }
    }

    public Texture getTexture() {
        return texture;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
        spriteSize = 1.0f;
        spriteCount = 1;
    }

    public void setTexture(Texture texture, float spriteSize) {
        this.texture = texture;
        this.spriteSize = spriteSize;
        spriteCount = (int) (1.0f/spriteSize);
    }

    public void setEmiter(ParticleEmiter particleEmiter) {

        emiter = particleEmiter;
    }

    public void setLifeTime(long millis, long offset) {

        minLifeTime = millis;
        lifetimeOffset = offset;
    }

    public void setScale(float minSize, float offset) {

        minScale = minSize;
        scaleOffset = offset;
    }

    public void setScaleStep(float minPerStep, float offset) {

        minScaleStep = minPerStep;
        scaleStepOffset = offset;
    }

    public void setSpeed(float speedPerStep, float offset) {

        minSpeed = speedPerStep;
        speedOffset = offset;
    }

    /*public void setRotation(float rotationPerStep, float offset) {

        minRotStep = rotationPerStep;
        rotStepOffset = offset;
    }*/

    public void setDirectionRandomness(float ratio) {
        directionRandomness = ratio;
    }

    public void setParticleCountPerEmit(int count) {
        requestedCount = count;
    }

    public void setContiniously(boolean value) {
        continiously = value;
    }

    public void setRandom(Random customRandom) {
        random = customRandom;
    }

    public void setEmitDelay(long millis) {
        emitDelay = millis;
        curretDelay = millis + 1;
    }

    public ParticleEmiter getEmiter() {
        return emiter;
    }

    public long getMinLifeTime() {
        return minLifeTime;
    }

    public long getLifetimeOffset() {
        return lifetimeOffset;
    }

    public float getMinSpeed() {
        return minSpeed;
    }

    public float getSpeedOffset() {
        return speedOffset;
    }

    public float getMinScale() {
        return minScale;
    }

    public float getScaleOffset() {
        return scaleOffset;
    }

    public float getDirectionRandomness() {
        return directionRandomness;
    }

   /* public float getMinRotStep() {
        return minRotStep;
    }

    public float getRotStepOffset() {
        return rotStepOffset;
    }*/

    public float getMinScaleStep() {
        return minScaleStep;
    }

    public float getScaleStepOffset() {
        return scaleStepOffset;
    }

    public int getSpriteCount() {
        return spriteCount;
    }

    public int getRequestedCount() {
        return requestedCount;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public boolean isContiniously() {
        return continiously;
    }

    public ParticleBuffer getBuffer() {
        return buffer;
    }

    public BaseParticle[] getParticles() {
        return particles;
    }

    public int getParticleDataLength(){
        return particles[0].data.length;
    }

    public int getCapacity() {
        return capacity;
    }

    public long getEmitDelay() {
        return emitDelay;
    }

    public float getSpriteSize() {
        return spriteSize;
    }

    public void setSpriteSize(float spriteSize) {
        this.spriteSize = spriteSize;
    }

    public void setSpriteCount(int spriteCount) {
        this.spriteCount = spriteCount;
    }

    @Override
    public void destroy() {

        inUse = false;
    }
}
