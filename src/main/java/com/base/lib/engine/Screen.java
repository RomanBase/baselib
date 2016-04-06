package com.base.lib.engine;

import android.content.pm.ActivityInfo;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class Screen extends BaseObject {

    public float width;
    public float height;
    public float ratio;
    public float density;

    public Screen(Base base) {
        super(base);
    }

    public void initDeviceDimensions(boolean includeDecorations) {

        float[] dims = base.getScreenDimensions(includeDecorations);

        width = dims[0];
        height = dims[1];
        ratio = width / height;
        density = base.context.getResources().getDisplayMetrics().density;
    }

    public boolean isLandscpateOriented() {

        return width > height;
    }

    /**
     * rotate screen to portrait orientation
     */
    public void setOrientationPortrait() {

        base.activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    /**
     * rotate screen to portrait orientation with reversed portrait rotation possibility
     */
    public void setOrientationSensorPortrait() {

        base.activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
    }

    /**
     * rotate screen to landscape orientation
     */
    public void setOrientationLandscape() {

        base.activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    /**
     * rotate screen to landscape orientation with reversed landscepe rotation possibility
     */
    public void setOrientationSensorLandscape() {

        base.activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
    }

    /**
     * remove status bar and sets activity to whole screen
     */
    public void setFullScreen() {

        base.activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
        base.activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /**
     * hides devices soft keys
     */
    public void hideVirtualUI() {

        if (Build.VERSION.SDK_INT >= 19) {
            base.activity.getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LOW_PROFILE
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        } else {
            base.activity.getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LOW_PROFILE);
        }
    }

    /**
     * hides devices soft keys
     * note: this action is sent to ui thread
     */
    public void hideVirtualUIHandle() {

        base.activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (base.isDeviceDecorated()) {
                    hideVirtualUI();
                }
            }
        });
    }

    /**
     * sets screen brightness
     *
     * @param reqScreenBrightness 0.0 - 1.0
     */
    public void setScreenBrightness(float reqScreenBrightness) {

        final Window window = base.activity.getWindow();
        final WindowManager.LayoutParams windowLayoutParams = window.getAttributes();
        windowLayoutParams.screenBrightness = reqScreenBrightness;
        window.setAttributes(windowLayoutParams);
    }

    /**
     * prevent device to go sleep or dim display
     */
    public void preventSleep() {

        base.activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
}
