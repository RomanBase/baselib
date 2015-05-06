package com.base.lib.engine.controls;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.base.lib.engine.Base;
import com.base.lib.interfaces.ActivityStateListener;

/**
 * 11 Created by doctor on 21.9.13.
 */
public class BaseAccelerometer implements SensorEventListener, ActivityStateListener{

    private SensorManager manager;
    private Sensor accelerometer;

    private float[] sensor;
    private float lastX;
    private float lastY;
    private float lastZ;
    private boolean registered;

    public BaseAccelerometer(){

        manager = (SensorManager) Base.context.getSystemService(Context.SENSOR_SERVICE);
        accelerometer = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        registered = false;
        sensor = new float[3];

        Base.activity.addActivityStateListener(this);
    }

    public void register(){

        if(!registered){
            registered = true;
            manager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    public void unregister(){

        if(registered){
            registered = false;
            manager.unregisterListener(this);
        }

    }

    @Override
    public void onResume(){

        register();
    }

    @Override
    public void onPause(){

       unregister();
    }

    @Override
    public void destroy() {

        unregister();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        sensor = sensorEvent.values;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    public float[] getSensor(){

        return sensor;
    }

    public float getMoveX(){

        return lastX-sensor[0];
    }

    public float getMoveY(){

        return lastY-sensor[1];
    }
}
