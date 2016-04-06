package com.base.lib.engine.common.gl;


import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;

public class EGLHolder {

    public EGL10 egl;
    public EGLDisplay display;
    public EGLConfig config;
    public EGLContext context;
    public EGLSurface surface;
}
