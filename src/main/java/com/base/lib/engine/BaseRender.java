package com.base.lib.engine;

import android.opengl.GLSurfaceView;

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
     * creates GLSurfaceView and sets this Renderer. elgConfigChooser and elgContextFactory is null (System specified)
     */
    public BaseRender(BaseOptions cfg) {
        this(cfg, null, null);
    }

    /**
     * creates GLSurfaceView and sets this Renderer.
     *
     * @param eglConfigChooser  can be null.
     * @param eglContextFactory can be null.
     */
    public BaseRender(BaseOptions cfg, GLSurfaceView.EGLConfigChooser eglConfigChooser, GLSurfaceView.EGLContextFactory eglContextFactory) {
        super();

        if (cfg != null) {
            cfg.bind();
        } else {
            cfg = new BaseOptions();
        }

        new BaseGLView(this, eglConfigChooser, eglContextFactory);

        preUpdatables = new BaseUpdateableCollection(64);
        preRenderables = new BaseRootCollection(64);
        postRenderables = new BaseRootCollection(64);

        float rfps = cfg.getRfps();
        setRequestedFPS(rfps);
        if (rfps > 0.0f) useFPSRendering();
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

        if(preUpdatables.size > 0){
            preUpdatables.update();
        }

        Base.camera.update();
        if (uiLayer != null) {
            uiLayer.update();
        }
    }

    @Override
    protected void onPreDraw() {

        if(preRenderables.size > 0){
            preRenderables.updateToDraw();
        }
    }

    @Override
    protected void onPostDraw() {

        if (uiLayer != null) {
            BaseGL.useProgram(uiLayer.shader.glProgram);
            uiLayer.draw();
        }

        if(postRenderables.size > 0){
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

    public void addPreUpdateable(BaseUpdateable updateable){

        preUpdatables.add(updateable);
    }

    public void addPreDrawable(BaseRenderable renderable){

        preRenderables.add(renderable);
    }

    public void addPostDrawable(BaseRenderable renderable){

        postRenderables.add(renderable);
    }


}
