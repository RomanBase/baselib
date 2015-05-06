package com.base.lib.engine.controls;

import android.hardware.SensorManager;
import android.provider.Settings.System;
import android.view.OrientationEventListener;

import com.base.lib.engine.Base;
import com.base.lib.interfaces.ActivityStateListener;

/**
 *
 */
public abstract class BaseOrientationHandler extends OrientationEventListener implements ActivityStateListener{

    private int currentOrientation = -1;
    private boolean isEnabled = false;

    public BaseOrientationHandler() {
        super(Base.context, SensorManager.SENSOR_DELAY_NORMAL);
        Base.activity.addActivityStateListener(this);
    }

    protected abstract void orientationChanged(float rotationChange, float rotation);

    @Override
    public void onOrientationChanged(int orientation) { //currently set in 30degrees range for each orientation - 15degrees each side

        if (orientation > 345 || orientation < 15) {        //portrait          (315 || 45)90deg
            orientation = 0;
        }
        else if (orientation < 285 && orientation > 255) {  //landscape         (315 && 225)90deg
            orientation = 1;
        }
        else if (orientation < 195 && orientation > 165) {  //portrait 180      (225 && 135)90deg
            orientation = 2;
        }
        else if (orientation < 105 && orientation > 75){    //landscape 180     (135 && 45)90deg
            orientation = 3;
        } else {
            orientation = currentOrientation;
        }

        if(orientation != currentOrientation){
            float rot = (orientation - currentOrientation) * 90;
            if(orientation == 3 && currentOrientation == 0){
                rot -= 360;
            } else if(currentOrientation == 3 && orientation == 0){
                rot += 360;
            }
            currentOrientation = orientation;
            orientationChanged(rot, currentOrientation * 90);
        }
    }

    @Override
    public void enable() {
        if(canDetectOrientation() && !isEnabled) {
            isEnabled = true;
            super.enable();
        }
    }

    @Override
    public void disable() {
        if (isEnabled) {
            super.disable();
            isEnabled = false;
        }
    }

    @Override
    public void onPause() {
        disable();
    }

    @Override
    public void onResume() {
        enable();
    }

    @Override
    public void destroy() {
        disable();
    }

    public static boolean isScreenRotationEnabled(){

       return System.getInt(Base.context.getContentResolver(), System.ACCELEROMETER_ROTATION, 0) == 1;
    }
}
