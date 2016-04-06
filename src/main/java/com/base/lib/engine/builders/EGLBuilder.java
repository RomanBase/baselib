package com.base.lib.engine.builders;

import android.opengl.GLSurfaceView;

import com.base.lib.engine.common.gl.EGLHolder;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;

public class EGLBuilder {

    private int version = 2;

    public EGLBuilder() {

    }

    public void setVersion(int version) {
        this.version = version;
    }

    public GLSurfaceView.EGLContextFactory build(final EGLHolder holder) {

        final int EGL_CONTEXT_CLIENT_VERSION = 0x3098;
        final int EGL_MIPMAP_TEXTURE = 0x3082;

        return new GLSurfaceView.EGLContextFactory() {
            @Override
            public EGLContext createContext(EGL10 egl, EGLDisplay display, EGLConfig eglConfig) {

                int[] attrib_list = {
                        EGL_CONTEXT_CLIENT_VERSION, version,
                        EGL10.EGL_NONE
                };

                int pbufferAttribs[] = {
                        EGL10.EGL_WIDTH, 1,
                        EGL10.EGL_HEIGHT, 1,
                        EGL10.EGL_NONE
                };

                EGLContext context = egl.eglCreateContext(display, eglConfig, EGL10.EGL_NO_CONTEXT, attrib_list);

                holder.egl = egl;
                holder.display = display;
                holder.config = eglConfig;
                holder.context = egl.eglCreateContext(display, eglConfig, context, attrib_list);
                holder.surface = egl.eglCreatePbufferSurface(display, eglConfig, pbufferAttribs);

                return context;
            }

            @Override
            public void destroyContext(EGL10 egl, EGLDisplay display, EGLContext context) {
                egl.eglDestroyContext(display, context);
            }
        };
    }
}
