package com.base.lib.engine.particles;

import com.base.lib.engine.Base;
import com.base.lib.engine.BaseGL;
import com.base.lib.engine.common.gl.RenderToTexture;

/**
 *
 */
public class BaseParticleSystemToTexture extends BaseParticleSystem {

    private RenderToTexture rtt;

    public BaseParticleSystemToTexture(Base base, int capacity, ParticleBuffer buffer, ParticleConstructor constructor, ParticleEmiter emiter, int textureWidth, int textureHeight) {
        super(base, capacity, buffer, constructor, emiter);

        rtt = new RenderToTexture(textureWidth, textureHeight);
        rtt.update();
    }

    public BaseParticleSystemToTexture(Base base, int capacity, ParticleBuffer buffer, ParticleConstructor constructor, ParticleEmiter emiter, RenderToTexture rtt) {
        super(base, capacity, buffer, constructor, emiter);

        this.rtt = rtt;
    }

    public void setPos(float x, float y, float z){

        rtt.translate(x, y, z);
        rtt.update();
    }

    public void setSize(float x, float y){

        rtt.setDrawableDimension(x, y);
    }

    @Override
    public void draw() {

        rtt.bind();
        super.draw();
        rtt.unbind();

        BaseGL.useProgram(rtt.getShader());
        rtt.draw();
    }

    @Override
    public void destroy() {
        super.destroy();

        base.gl.glRun(new Runnable() {
            @Override
            public void run() {
                rtt.destroy();
            }
        });
    }

    public RenderToTexture getRtt() {
        return rtt;
    }

    public void setRtt(RenderToTexture rtt) {
        this.rtt = rtt;
    }
}
