package com.base.lib.engine.other;

import android.opengl.GLES20;
import android.os.SystemClock;

import com.base.lib.engine.Base;
import com.base.lib.engine.BaseDraw;
import com.base.lib.engine.BaseGL;
import com.base.lib.engine.BaseRender;
import com.base.lib.engine.BaseRenderable;
import com.base.lib.engine.BaseShader;
import com.base.lib.engine.Texture;
import com.base.lib.engine.common.Colorf;

/**
 * <p>First preload screen is usually called inside BaseRender(as prefered renderer) constructor</p>
 * <p>draws background and other items are as near as possible to camera.</p>
 * <p><i>w, h, r, z</i> holds important values about surface</p>
 * <p><i>t</i> and <i>currentState</i> indicates actual progress</p>
 * <p>note: using BaseCamera from Base class -> this camera must be specified before preloader is constructed</p>
 */
public class BasePreloader extends BaseRenderable {

    protected static final byte RISING = 0;
    protected static final byte LOADING = 1;
    protected static final byte HIDING = 2;

    protected byte currentState;
    protected float w, h, r, z, t;

    private PreloadItem[] items;
    private Runnable[] actions;
    private Colorf color;
    private Texture background;
    private long dimTimeout;
    private float currentDelay;
    private long timeHint;

    public BasePreloader(long dimTimeout, Texture background, Runnable... actions) {
        super();

        if(!BaseDraw.isInitialized()){
            BaseDraw.init();
        }

        setShader(BaseShader.mixTextureColorShader());
        this.actions = actions;
        this.dimTimeout = dimTimeout;
        this.background = background;

        color = new Colorf(1.0f, 1.0f, 1.0f, 0.0f);

        r = Base.camera.getNearNegativeRatio(z = Base.camera.getPosition().z + Base.camera.getNearZ() + 0.1f);

        w = Base.camera.getWidth() * r;
        h = Base.camera.getHeight() * r;

        if(actions != null) {
            use();
        }
    }

    @Override
    public void use(){

        currentState = RISING;
        currentDelay = 0;
        inUse = true;
        timeHint = SystemClock.uptimeMillis();

        if(Base.render instanceof BaseRender){
            ((BaseRender) Base.render).addPostDrawable(this);
        } else {
            Base.render.addDrawable(this);
        }
    }

    protected void loadActions() {

        if (actions != null) {
            for (Runnable action : actions) {
                if (action != null) {
                    action.run();
                }
            }

            actions = null;
        }

        timeHint = SystemClock.uptimeMillis();
        currentDelay = 0;
        currentState = HIDING;
    }

    @Override
    public void draw() {

        BaseGL.bindTexture(background.glid);
        GLES20.glUniform4f(shader.handle[3], color.r, color.g, color.b, t);
        BaseDraw.rect(shader, 0.0f, 0.0f, z, w, h);

        if(items != null) {
            GLES20.glUniform4f(shader.handle[3], 1.0f, 1.0f, 1.0f, t);
            for(PreloadItem item : items){
                if(item != null) {
                    item.draw();
                }
            }
        }
    }

    @Override
    public void update() {

        switch (currentState) {
            case RISING:
                if (t < 1.0f) {
                    long time = SystemClock.uptimeMillis();
                    currentDelay += time - timeHint;
                    timeHint = time;
                    t = (float) currentDelay / (float) dimTimeout;
                } else {
                    currentState = LOADING;
                    Base.render.runOnBaseThread(new Runnable() { // send to next tick
                        @Override
                        public void run() {
                            Base.render.glQueueEvent(new Runnable() { // send to gl thread
                                @Override
                                public void run() {
                                    loadActions();
                                }
                            });
                        }
                    });
                }
                break;
            case LOADING:
                break;
            case HIDING:
                if (t > 0.0f) {
                    long time = SystemClock.uptimeMillis();
                    currentDelay += time - timeHint;
                    timeHint = time;
                    t = 1.0f - ((float) currentDelay / (float) dimTimeout);
                } else {
                    unUse();
                }
                break;
        }
    }

    public void setScreenColor(float r, float g, float b) {
        color.setf(r, g, b, 0.0f);
    }

    public void setPreloadItems(PreloadItem... items){
        this.items = items;
    }

    public void setBackground(Texture texture){
        background = texture;
    }

    public void setDimTimeout(long millis){
        dimTimeout = millis;
    }

    public void setActions(Runnable... actions) {
        this.actions = actions;
    }

    @Override
    public void destroy() {

    }

    public class PreloadItem {

        protected float x, y, w, h;

        public PreloadItem(float x, float y, float w, float h) {

            this.x = x * r;
            this.y = y * r;
            this.w = w * r;
            this.h = h * r;
        }

        protected void draw(){

            BaseDraw.rect(shader, x, y, z, w, h);
        }
    }
}
