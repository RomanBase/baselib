package com.base.lib.box.base;

import com.base.lib.engine.common.TrainedMonkey;

/**
 *
 */
public class BaseSensorGroup {

    public BaseSensor[] sensors;
    private int index;

    public BaseSensorGroup(){}

    public BaseSensorGroup(BaseSensor[] sensors){
        this.sensors = sensors;
    }

    public void initB2RectSensorByName(String name, Object userData, float offsetPosX, float offsetPosY){

        for(BaseSensor sensor : sensors){
            if(name.equals(sensor.name)){
                sensor.intoB2RectSenzor(userData, offsetPosX, offsetPosY);
            }
        }
    }
    public void initB2CircleSensorByName(String name, Object userData, float offsetPosX, float offsetPosY){

        for(BaseSensor sensor : sensors){
            if(name.equals(sensor.name)){
                sensor.intoB2CircleSenzor(userData, offsetPosX, offsetPosY);
            }
        }
    }

    /** note: return null if no more object with requested name */
    public BaseSensor getNext(String name){

        if(index < sensors.length) {
            for (int i = index; i < sensors.length; i++) {
                if (name.equals(sensors[i].name)) {
                    index = i + 1;
                    return sensors[i];
                }
            }
        }

        index = 0;
        return null;
    }

    /** note: slow if lots of objects */
    public static String[] getUniqueNames(BaseSensorGroup group){

        String[] out = new String[1];
        out[0] = group.sensors[0].name;

        for(int i = 1; i<group.sensors.length; i++){
            boolean contains = false;
            for(String in : out){
                if(group.sensors[i].name.equals(in)){
                    contains = true;
                    break;
                }
            }
            if(!contains){
                out = TrainedMonkey.arrayUp(out, group.sensors[i].name);
            }
        }

        return out;
    }
}
