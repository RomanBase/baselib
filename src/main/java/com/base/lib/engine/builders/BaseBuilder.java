package com.base.lib.engine.builders;

import com.base.lib.engine.Base;
import com.base.lib.engine.BaseActivity;
import com.base.lib.engine.common.gl.BaseEGLConfig;
import com.base.lib.engine.other.ScreenDecorationHider;

/**
 *
 */
public class BaseBuilder {

    private final BaseActivity activity;

    private boolean fullScrean = false;
    private boolean undecorated = false;
    private boolean landscape;
    private boolean preventSleep;
    private boolean reverseMode;

    public BaseBuilder(BaseActivity activity) {
        this.activity = activity;
    }

    public Base build() {

        Base base = new Base(activity, activity);

        if (fullScrean) {
            base.screen.setFullScreen();
        }

        if (undecorated) {
            base.screen.hideVirtualUI();
            activity.addActivityStateListener(new ScreenDecorationHider());
        }

        if (landscape) {
            if (reverseMode) {
                base.screen.setOrientationSensorLandscape();
            } else {
                base.screen.setOrientationLandscape();
            }
        } else {
            if (reverseMode) {
                base.screen.setOrientationSensorPortrait();
            } else {
                base.screen.setOrientationPortrait();
            }
        }

        if (preventSleep) {
            base.screen.preventSleep();
        }

        return base;
    }

    public BaseBuilder setScreenLandscape(boolean enableReverseMode) {

        landscape = true;
        reverseMode = enableReverseMode;
        return this;
    }

    public BaseBuilder setScreenPortrait(boolean enableReverseMode) {

        landscape = false;
        reverseMode = enableReverseMode;
        return this;
    }

    public BaseBuilder setFullScreen(boolean hideVirtualKeys) {

        undecorated = hideVirtualKeys;
        fullScrean = true;
        return this;
    }

    public void setPreventSleep(boolean preventSleep) {
        this.preventSleep = preventSleep;
    }

    public void glChannels(int r, int g, int b) {
        BaseEGLConfig.RED = r;
        BaseEGLConfig.GREEN = g;
        BaseEGLConfig.BLUE = b;
    }

    public void glDepthBuffer(int d) {
        BaseEGLConfig.DEPTH = d;
    }

    public void glEnableStencilBuffer(int s) {
        BaseEGLConfig.STENCIL = s;
    }

    public void glEnableSampleBuffer(int low, int hight) {
        BaseEGLConfig.SAMPLEBUF = 1;
        BaseEGLConfig.SAMPLES = low;
        BaseEGLConfig.COVERAGE_SAMPLES = hight;
    }

}
