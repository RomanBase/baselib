package com.base.lib.engine;

import android.content.res.Configuration;
import android.provider.Settings;

import com.base.lib.engine.glcommon.BaseEGLConfig;
import com.base.lib.engine.other.ScreenDecorationHider;
import com.base.lib.googleservices.InAppBillingHandler;

/**
 *
 */
public class BaseOptions {

    private boolean isBinded = false;
    private boolean undecorated = false;
    private float rfps = 30.0f;

    private int cor;
    private int nor = -1;

    BaseOptions(){

        cor = Base.context.getResources().getConfiguration().orientation;
    }

    public void bind(){

        if(!isBinded){
            isBinded = false;

            if(undecorated){
                Base.recalcScreenDimensionsDecorated();
            } else {
                Base.recalcScreenDimensions();
            }
        }
    }

    public void enableDebugMod(){
        Base.debug = true;
    }

    public void disableDebugMod(){

        Base.debug = false;
    }

    public void setLogTag(String tag){
        Base.TAG = tag;
    }

    public void useFpsRendering(float fps){

        rfps = fps;
    }

    public void disableFpsRendering(){

        rfps = -1.0f;
    }

    public void useInAppBilling(InAppBillingHandler listener){

        Base.activity.useInAppBilling(listener);
    }

    public void useAchievements(boolean hardConnect){

        Base.activity.useAchievements(hardConnect);
    }

    public float getRfps(){

        return rfps;
    }

    public void screenLandscape(boolean enableReverseMode){

        nor = Configuration.ORIENTATION_LANDSCAPE;

        if(enableReverseMode){
            Base.activity.setScreenOrientationSensorLandscape();
        } else {
            Base.activity.setScreenOrientationLandscape();
        }
    }

    public void screenPortrait(boolean enableReverseMode){

        nor = Configuration.ORIENTATION_PORTRAIT;

        if(enableReverseMode){
            Base.activity.setScreenOrientationSensorPortrait();
        } else {
            Base.activity.setScreenOrientationPortrait();
        }
    }

    public int screenFix(boolean enableReverseMode){

        switch (cor){
            case Configuration.ORIENTATION_PORTRAIT:
                screenPortrait(enableReverseMode);
                break;
            case Configuration.ORIENTATION_LANDSCAPE:
                screenLandscape(enableReverseMode);
                break;
        }

        return cor;
    }

    public boolean isScreenRotationRequired(){

        return nor != cor;
    }

    public void setFullScreen(boolean hideVirtualKeys){

        Base.activity.setFullScreen();

        if(hideVirtualKeys && Base.isDeviceDecorated()){
            Base.activity.hideVirtualUI();
            Base.activity.addActivityStateListener(new ScreenDecorationHider());
        }

        undecorated = hideVirtualKeys;
    }

    public void preventScreenDim(){
        Base.activity.preventSleep();
    }

    public void setScreenBrightness(float value){
        if(value >= 0.0f){
            Base.activity.setScreenBrightness(value);
        }
    }

    public void setScreenBrigtnessLower(float value){
        float current = (float) Settings.System.getInt(Base.context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, -1) / 100.0f;
        if(value < current){
            Base.activity.setScreenBrightness(value);
        }
    }

    public void setCamera(BaseCamera camera){

        Base.camera = camera;
    }

    public void glChannels(int r, int g, int b){
        BaseEGLConfig.RED = r;
        BaseEGLConfig.GREEN = g;
        BaseEGLConfig.BLUE = b;
    }

    public void glDepthBuffer(int d){
        BaseEGLConfig.DEPTH = d;
    }

    public void glEnableStencilBuffer(int s){
        BaseEGLConfig.STENCIL = s;
    }

    public void glEnableSampleBuffer(int low, int hight){
        BaseEGLConfig.SAMPLEBUF = 1;
        BaseEGLConfig.SAMPLES = low;
        BaseEGLConfig.COVERAGE_SAMPLES = hight;
    }

}
