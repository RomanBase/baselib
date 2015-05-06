package com.base.lib.engine;

import android.os.SystemClock;

import java.util.Calendar;

/**
 *
 */
public class BaseTime {

    private static long hint;
    private static int index;
    private static long zeroTime;

    /** delta time per second (1.0 / fps) */
    public static volatile float delta;

    /** delta time for current step (delay / requestedFrameDelay)*/
    public static volatile float deltaStep;

    /** delay time for last frame */
    public static volatile float delay;

    public static void hint(){

        hint = SystemClock.uptimeMillis();
        index = 0;
    }

    public static void print(){

        Base.logV("Tim_"+index++, SystemClock.uptimeMillis()-hint);
    }

    public static void resetAppTime() {

        zeroTime = SystemClock.uptimeMillis();
    }

    public static long millis() {

        return SystemClock.uptimeMillis();
    }

    public static int sec() {

        return Calendar.getInstance().get(Calendar.SECOND);
    }

    public static int min() {

        return Calendar.getInstance().get(Calendar.MINUTE);
    }

    public static int hour() {

        return Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
    }

    public static int year() {

        return Calendar.getInstance().get(Calendar.YEAR);
    }

    public static long unixTime() {

        return System.currentTimeMillis();
    }

    public static long realTime(){

        return SystemClock.elapsedRealtime();
    }

    public static long appTime() {

        return SystemClock.uptimeMillis() - zeroTime;
    }

    @Override
    public String toString() {

      return appTime() +"  "+ delay + "  " + delta +"  "+ deltaStep;
    }
}
