package com.base.lib.engine;

import android.content.Context;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.PowerManager;
import android.os.SystemClock;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.Toast;

import com.base.lib.engine.other.dev.FpsBar;

import java.util.Random;

/**
 * Base class is initialized by BaseActivity
 * <p>
 * This class holds important inforamtions about running deviace and application
 * </p>
 */
public final class Base {

    public static String TAG = "Base";

    public static Context context;
    public static BaseActivity activity;

    public static BaseGLView glView;
    public static BaseRenderer render;
    public static BaseCamera camera;

    public static final Random random = new Random(SystemClock.uptimeMillis());

    public static float screenWidth;
    public static float screenHeight;
    public static float screenRatio;
    public static float screenDensity;

    public static boolean debug = false;

    /**
     * called with BaseActivity onCreate function
     * <p/>
     * gather base informations about current app and deviace and hold it by static way
     */
    static void init(final BaseActivity baseActivity) {

        activity = baseActivity;
        init(activity.getBaseContext());
    }

    public static void init(final Context appContext) {

        context = appContext;
        BaseTime.resetAppTime();
    }

    public static void init(final BaseActivity baseActivity, BaseRenderer renderer) {

        activity = baseActivity;
        context = baseActivity.getBaseContext();

        render = renderer;
        glView = renderer.getView();
    }

    protected static void recalcScreenDimensionsDecorated() {

        recalcScreenDimensions(true);
        rebindCam();
    }

    protected static void recalcScreenDimensions() {

        recalcScreenDimensions(false);
        rebindCam();
    }

    static void rebindCam() {

        if (camera == null || !camera.equalsToScreenDimension()) {
            camera = BaseCamera.perspective(landscape() ? 45.0f : 30.0f, 10.0f, 1.0f, 1.1f);
        }
    }

    public static boolean landscape() {

        return screenWidth > screenHeight;
    }

    public static boolean isScreenOn() {

        return ((PowerManager) context.getSystemService(Context.POWER_SERVICE)).isScreenOn();
    }

    public static boolean isFinishing() {

        return activity.isFinishing();
    }

    public static FpsBar showFpsBar() {

        return new FpsBar();
    }

    public static long getMaxMemoryHeap() {

        return Runtime.getRuntime().maxMemory();
    }

    public static float getScreenDpi() {

        return screenDensity * 2.0f;
    }

    public static boolean hasSoftKeys() {

        boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
        boolean hasHomeKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_HOME);

        return hasBackKey && hasHomeKey;
    }

    public static void log(Object... o) {

        if (!debug)
            return;

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

    public static void toast(final Object o) {

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, o.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void toastLong(final Object o) {

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, o.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public static void vibre(int millis) {

        ((Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(millis);
    }

    /**
     * calculate app screen width, height, density and screen ratio
     *
     * @param includeDecorations include status bar and menu etc.
     */
    public static void recalcScreenDimensions(boolean includeDecorations) {

        final WindowManager wm = (WindowManager) Base.context.getSystemService(Context.WINDOW_SERVICE);
        final Display dis = wm.getDefaultDisplay();
        final DisplayMetrics displayMetrics = new DisplayMetrics();
        dis.getMetrics(displayMetrics);

        if (includeDecorations) {
            try {
                Point realSize = new Point();
                Display.class.getMethod("getRealSize", Point.class).invoke(dis, realSize);
                Base.screenWidth = realSize.x;
                Base.screenHeight = realSize.y;
            } catch (Exception ex) {
                Base.logE("Monkeys can't measure real display size.");
                recalcScreenDimensions(false);
            }
        } else {
            Point size = new Point();
            dis.getSize(size);
            Base.screenWidth = size.x;
            Base.screenHeight = size.y;
        }

        Base.screenDensity = displayMetrics.density;
        Base.screenRatio = Base.screenWidth / Base.screenHeight;
    }

    /**
     * @return screen dimensions - [0]screenWidth, [1]screenHeight
     */
    public static float[] getScreenDimensions(boolean includeDecorations) {

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
                recalcScreenDimensions(false);
            }
        } else {
            Point size = new Point();
            dis.getSize(size);
            dim[0] = size.x;
            dim[1] = size.y;
        }

        return dim;
    }

    public static boolean isDeviceDecorated() {

        float[] deco = getScreenDimensions(true);
        float[] undeco = getScreenDimensions(false);

        return !(deco[0] == undeco[0] && deco[1] == undeco[1]);
    }

    public static boolean isConnected() {

        ConnectivityManager conMgr = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        return netInfo != null && netInfo.isConnected();
    }

    public static boolean hasContext() {

        return activity != null && context != null;
    }
}
