package com.base.lib.engine;

import com.base.lib.engine.builders.RenderConfig;
import com.base.lib.engine.controls.BaseUILayer;

/**
 * Extens BaseRenderer and adds some usefull functionality
 */
public class BaseRender extends BaseRenderer {

    protected BaseUpdateableCollection preUpdatables;
    protected BaseRootCollection preRenderables;
    protected BaseRootCollection postRenderables;

    protected BaseUILayer uiLayer;

    /**
     * creates GLSurfaceView and sets this Renderer.
     */
    public BaseRender(RenderConfig config) {
        super(config);

        setGLView(new BaseGLView(config));

        preUpdatables = new BaseUpdateableCollection(base, 64);
        preRenderables = new BaseRootCollection(base, 64);
        postRenderables = new BaseRootCollection(base, 64);
    }

    /**
     * sets ui layer and register touch listener if required
     */
    public void setUiLayer(BaseUILayer ui, boolean registerListener) {

        uiLayer = ui;
        if (registerListener) {
            setTouchListener(uiLayer);
        }
    }

    /**
     * destroys ui layer and sets its reference to null
     */
    public void destroyUiLayer() {

        setTouchListener(null);
        if (uiLayer != null) {
            uiLayer.destroy();
            uiLayer = null;
        }
    }

    /**
     * @return current ui layer
     */
    public BaseUILayer getUiLayer() {

        return uiLayer;
    }

    @Override
    protected void onCreate() {

    }

    @Override
    protected void onUpdate() {

        if (preUpdatables.size > 0) {
            preUpdatables.update();
        }

        base.camera.update();
        if (uiLayer != null) {
            uiLayer.update();
        }
    }

    @Override
    protected void onPreDraw() {

        if (preRenderables.size > 0) {
            preRenderables.updateToDraw();
        }
    }

    @Override
    protected void onPostDraw() {

        if (uiLayer != null) {
            BaseGL.useProgram(uiLayer.shader.glid);
            uiLayer.draw();
        }

        if (postRenderables.size > 0) {
            postRenderables.updateToDraw();
        }
    }

    @Override
    protected void onDestroy() {

        if (uiLayer != null) {
            uiLayer.destroy();
        }

        preUpdatables.clear();
        preRenderables.clear();
        postRenderables.clear();
    }

    public void addPreUpdateable(BaseUpdateable updateable) {

        preUpdatables.add(updateable);
    }

    public void addPreDrawable(BaseRenderable renderable) {

        preRenderables.add(renderable);
    }

    public void addPostDrawable(BaseRenderable renderable) {

        postRenderables.add(renderable);
    }


}
