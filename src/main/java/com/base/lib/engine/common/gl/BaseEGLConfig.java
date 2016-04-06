package com.base.lib.engine.common.gl;

/**
 *
 */
import android.opengl.GLSurfaceView;
import android.util.Log;

import com.base.lib.engine.Base;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;


public class BaseEGLConfig implements GLSurfaceView.EGLConfigChooser {

    private static final String kTag = "GDC11";

    public static int RED = 8;
    public static int GREEN = 8;
    public static int BLUE = 8;
    public static int DEPTH = 16;
    public static int STENCIL = 0;
    public static int GLTYPE = 4; // es2
    public static int SAMPLEBUF = 1; // true
    public static int SAMPLES = 2;
    public static int COVERAGE_SAMPLES = 5;

    private int[] mValue;
    private boolean mUsesCoverageAa;

    @Override
    public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display) {
        mValue = new int[1];

        // Try to find a normal multisample configuration first.
        int[] configSpec = {
                EGL10.EGL_RED_SIZE, RED,
                EGL10.EGL_GREEN_SIZE, GREEN,
                EGL10.EGL_BLUE_SIZE, BLUE,
                EGL10.EGL_DEPTH_SIZE, DEPTH,
                EGL10.EGL_STENCIL_SIZE, STENCIL,
                EGL10.EGL_RENDERABLE_TYPE, GLTYPE,
                EGL10.EGL_SAMPLE_BUFFERS, SAMPLEBUF,
                EGL10.EGL_SAMPLES, SAMPLES,
                EGL10.EGL_NONE
        };

        if (!egl.eglChooseConfig(display, configSpec, null, 0, mValue)) {
            throw new IllegalArgumentException("eglChooseConfig failed");
        }
        int numConfigs = mValue[0];
        if(numConfigs > 0) {
            Base.logV("Multisample created");
        } else {
            // No normal multisampling config was found. Try to create a
            // converage multisampling configuration, for the nVidia Tegra2.
            // See the EGL_NV_coverage_sample documentation.

            final int EGL_COVERAGE_BUFFERS_NV = 0x30E0;
            final int EGL_COVERAGE_SAMPLES_NV = 0x30E1;

            configSpec = new int[]{
                    EGL10.EGL_RED_SIZE, RED,
                    EGL10.EGL_GREEN_SIZE, GREEN,
                    EGL10.EGL_BLUE_SIZE, BLUE,
                    EGL10.EGL_DEPTH_SIZE, DEPTH,
                    EGL10.EGL_STENCIL_SIZE, STENCIL,
                    EGL10.EGL_RENDERABLE_TYPE, GLTYPE,
                    EGL_COVERAGE_BUFFERS_NV, SAMPLEBUF,
                    EGL_COVERAGE_SAMPLES_NV, COVERAGE_SAMPLES,
                    EGL10.EGL_NONE
            };

            if (!egl.eglChooseConfig(display, configSpec, null, 0, mValue)) {
                throw new IllegalArgumentException("2nd eglChooseConfig failed");
            }
            numConfigs = mValue[0];
            if(numConfigs > 0){
                Base.logV("Tegra Multisample created");
                mUsesCoverageAa = true;
            } else {
                // Give up, try without multisampling.
                configSpec = new int[]{
                        EGL10.EGL_RED_SIZE, RED,
                        EGL10.EGL_GREEN_SIZE, GREEN,
                        EGL10.EGL_BLUE_SIZE, BLUE,
                        EGL10.EGL_DEPTH_SIZE, DEPTH,
                        EGL10.EGL_STENCIL_SIZE, STENCIL,
                        EGL10.EGL_RENDERABLE_TYPE, GLTYPE,
                        EGL10.EGL_NONE
                };

                if (!egl.eglChooseConfig(display, configSpec, null, 0, mValue)) {
                    throw new IllegalArgumentException("3rd eglChooseConfig failed");
                }

                numConfigs = mValue[0];

                if (numConfigs <= 0) {
                    throw new IllegalArgumentException("No configs match configSpec");
                }
            }
        }

        Base.log(mValue[0]);

        // Get all matching configurations.
        EGLConfig[] configs = new EGLConfig[numConfigs];
        if (!egl.eglChooseConfig(display, configSpec, configs, numConfigs, mValue)) {
            throw new IllegalArgumentException("data eglChooseConfig failed");
        }

        // CAUTION! eglChooseConfigs returns configs with higher bit depth
        // first: Even though we asked for rgb565 configurations, rgb888
        // configurations are considered to be "better" and returned first.
        // You need to explicitly filter the data returned by eglChooseConfig!
        int index = -1;
        for (int i = 0; i < configs.length; ++i) {
            if (findConfigAttrib(egl, display, configs[i], EGL10.EGL_RED_SIZE, 0) == RED) {
                index = i;
                break;
            }
        }
        if (index == -1) {
            Log.w(kTag, "Did not find sane config, using first");
        }
        EGLConfig config = configs.length > 0 ? configs[index] : null;
        if (config == null) {
            throw new IllegalArgumentException("No config chosen");
        }
        return config;
    }

    private int findConfigAttrib(EGL10 egl, EGLDisplay display, EGLConfig config, int attribute, int defaultValue) {
        if (egl.eglGetConfigAttrib(display, config, attribute, mValue)) {
            return mValue[0];
        }
        return defaultValue;
    }

    public boolean usesCoverageAa() {
        return mUsesCoverageAa;
    }
}
