package com.base.lib.wall;

import com.base.lib.engine.BaseUpdateable;
import com.base.lib.engine.builders.RenderConfig;

public class BasePaperStepRender extends BasePaperRender {

    protected long renderMillis = -1;

    public BasePaperStepRender(RenderConfig config) {
        super(config);
    }

    @Override
    public void useFPSRendering() {

        useFPSRendering(1000);
    }

    public void useFPSRendering(long millis) {

        renderMillis = millis;
        if (!isFPSRender()) {
            super.startFPSRendering();
        }
    }

    @Override
    protected void stopFPSRendering() {

        renderMillis = -1;
        super.stopFPSRendering();
    }

    public void addRenderTime(long millis) {

        if (!isFPSRender()) {
            useFPSRendering(millis);
        } else {
            renderMillis += millis;
        }
    }

    @Override
    public void render() {

        if (!isFPSRender()) {
            super.onUpdateFrame();
            super.render();
        } else {
            renderMillis += 1000.0f / getRequestedFPS();
        }
    }

    @Override
    protected void onUpdate() {

        if (uiLayer != null) {
            uiLayer.update();
        }

        if (isFPSRender()) {
            renderMillis -= base.time.delay;
            if (renderMillis < 0) {
                addUpdateable(new BaseUpdateable() {
                    @Override
                    public void update() {
                        stopFPSRendering();
                        unUse();
                    }

                    @Override
                    public void destroy() {

                    }
                });
            }
        }
    }
}
