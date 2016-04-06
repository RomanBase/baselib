package com.base.lib.engine;

import android.content.Context;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.PowerManager;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.WindowManager;

import com.base.lib.BuildConfig;
import com.base.lib.engine.builders.BaseFactory;

import java.util.Random;

/**
 * Base class is initialized by BaseActivity
 * <p>
 * This class holds important inforamtions about running device and application
 * </p>
 */
public final class Base { //TODO just one render

    public static String TAG = "Base";
    public static boolean debug = BuildConfig.DEBUG;

    public static Context appContext;

    public final Context context;
    public final BaseActivity activity;

    public final Screen screen;
    public final Random random;
    public final BaseTime time;

    public BaseRenderer render;
    public BaseCamera camera;
    public BaseGL gl;
    public BaseFactory factory;

    public Base(BaseActivity activity, Context context) {

        this.activity = activity;
        this.context = context;

        this.random = new Random(SystemClock.uptimeMillis());
        this.screen = new Screen(this);
        this.screen.initDeviceDimensions(true);
        this.time = new BaseTime();
    }

    public void init(BaseCamera camera, BaseRenderer render) {
        this.camera = camera;
        this.render = render;
    }

    public void initGL(BaseGL gl) {
        this.gl = gl;
    }

    public void initFactory(BaseFactory factory) {
        this.factory = factory;
    }

    @SuppressWarnings("unchecked")
    public <T extends BaseRenderer> T getRender() {

        return (T) render;
    }

    public void onConfigrationChanged() {

        screen.initDeviceDimensions(true);
    }

    public boolean isScreenOn() {

        return ((PowerManager) context.getSystemService(Context.POWER_SERVICE)).isScreenOn();
    }

    public boolean isFinishing() {

        return activity.isFinishing();
    }

    public static long getMaxMemoryHeap() {

        return Runtime.getRuntime().maxMemory();
    }

    public static boolean hasSoftKeys() {

        boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
        boolean hasHomeKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_HOME);

        return hasBackKey && hasHomeKey;
    }

    public static void log(Object... o) {

        if (!debug) {
            return;
        }

        StringBuilder builder = new StringBuilder(o.length * 2 + 1);
        for (Object ob : o) {
            builder.append(ob).append(" ");
        }
        Log.i(TAG, builder.toString());
    }

    public static void logI(Object o) {

        if (debug) Log.i(TAG, o.toString());
    }

    public static void logD(Object o) {

        if (debug) Log.d(TAG, o.toString());
    }

    public static void logE(Object o) {

        if (debug) Log.e(TAG, o.toString());
    }

    public static void logV(Object o) {

        if (debug) Log.v(TAG, o.toString());
    }

    public static void logI(String TAG, Object o) {

        if (debug) Log.i(TAG, o.toString());
    }

    public static void logD(String TAG, Object o) {

        if (debug) Log.d(TAG, o.toString());
    }

    public static void logE(String TAG, Object o) {

        if (debug) Log.e(TAG, o.toString());
    }

    public static void logV(String TAG, Object o) {

        if (debug) Log.v(TAG, o.toString());
    }

    /**
     * @return screen dimensions - [0]screenWidth, [1]screenHeight
     */
    public float[] getScreenDimensions(boolean includeDecorations) {

        float[] dim = new float[2];
        final WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        final Display dis = wm.getDefaultDisplay();
        final DisplayMetrics displayMetrics = new DisplayMetrics();
        dis.getMetrics(displayMetrics);

        if (includeDecorations) {
            try {
                Point realSize = new Point();
                Display.class.getMethod("getRealSize", Point.class).invoke(dis, realSize);
                dim[0] = realSize.x;
                dim[1] = realSize.y;
            } catch (Exception ex) {
                Base.logE("Monkeys can't measure real display size.");
                return getScreenDimensions(false);
            }
        } else {
            Point size = new Point();
            dis.getSize(size);
            dim[0] = size.x;
            dim[1] = size.y;
        }

        return dim;
    }

    public boolean isDeviceDecorated() {

        float[] deco = getScreenDimensions(true);
        float[] undeco = getScreenDimensions(false);

        return !(deco[0] == undeco[0] && deco[1] == undeco[1]);
    }

    public boolean isConnected() {

        ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public boolean isWifiConnected() {

        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        return mWifi.isConnectedOrConnecting();
    }
}
