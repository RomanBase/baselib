package com.base.lib.engine.builders;

import android.opengl.GLSurfaceView;

import com.base.lib.engine.Base;
import com.base.lib.engine.BaseObject;
import com.base.lib.engine.common.gl.EGLHolder;
import com.base.lib.wall.PaperBase;

public class RenderConfig extends BaseObject {

    private float rfps = 30.0f;

    private BaseFactory factory;
    private GLSurfaceView.EGLConfigChooser eglConfigChooser;
    private GLSurfaceView.EGLContextFactory eglContextFactory;
    private EGLHolder eglHolder;

    private PaperBase paperBase;

    public RenderConfig(Base base) {
        super(base);
    }

    public RenderConfig(Base base, PaperBase paperBase) {
        super(base);
        this.paperBase = paperBase;
    }

    public void setFps(float fps) {

        rfps = fps;
    }

    public void disableFpsRendering() {

        rfps = -1.0f;
    }

    public BaseFactory getFactory() {

        if (factory == null) {
            factory = new BaseFactory(base);
        }

        return factory;
    }

    public void setFactory(BaseFactory factory) {
        this.factory = factory;
    }

    public GLSurfaceView.EGLConfigChooser getEglConfigChooser() {

        if (eglConfigChooser == null) {
            //eglConfigChooser = new BaseEGLConfig();
        }

        return eglConfigChooser;
    }

    public void setEglConfigChooser(GLSurfaceView.EGLConfigChooser eglConfigChooser) {
        this.eglConfigChooser = eglConfigChooser;
    }

    public GLSurfaceView.EGLContextFactory getEglContextFactory() {

        if (eglContextFactory == null) {
            //eglContextFactory = new EGLBuilder().build(getEglHolder());
        }

        return eglContextFactory;
    }

    public void setEglContextFactory(GLSurfaceView.EGLContextFactory eglContextFactory) {
        this.eglContextFactory = eglContextFactory;
    }

    public EGLHolder getEglHolder() {

        if (eglHolder == null) {
            //eglHolder = new EGLHolder();
        }

        return eglHolder;
    }

    public void setEglHolder(EGLHolder eglHolder) {
        this.eglHolder = eglHolder;
    }

    public float getRequestedFps() {
        return rfps;
    }

    public PaperBase getPaperBase() {
        return paperBase;
    }
}
