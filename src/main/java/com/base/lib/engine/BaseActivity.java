package com.base.lib.engine;


import android.app.Activity;
import android.app.ActivityManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ConfigurationInfo;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.base.lib.googleservices.BaseAchievements;
import com.base.lib.googleservices.BaseApiClient;
import com.base.lib.googleservices.BaseInAppBilling;
import com.base.lib.googleservices.InAppBillingHandler;
import com.base.lib.interfaces.ActivityStateListener;

import java.util.ArrayList;
import java.util.List;

/**
 * extends Activity
 * check for OpenGL ES versions on running device
 * call Base engine functions
 * automaticaly calls pause, resume on content view, audio manager etc.
 * creates new instace of Base class witch holds some info. about app
 */
public abstract class BaseActivity extends Activity {

    private int requestedScreenOrientation;
    private List<ActivityStateListener> activityStateListeners;
    private BaseInAppBilling inAppBilling;
    private BaseApiClient apiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Base.init(this);

        // Check if the system supports OpenGL ES 2.0.
        if (!glesSupport(0x20000)) return;

        // Set the hardware buttons to control the audio media volume
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);

        //hardware acceleration for non gl views
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);

        activityStateListeners = new ArrayList<ActivityStateListener>();

        onCreate(new BaseOptions());
    }

    /**
     * is called on end of Activity onCreate(Bundle) function
     */
    protected abstract void onCreate(BaseOptions cfg);

    /**
     * check if deviace runs on 18+ api and hw supports gles 3
     */
    protected boolean isGLES30Supported() {

        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2 && glesSupport(0x30000);
    }

    /**
     * creates configuration info and chcck for glesSupport
     *
     * @param glVersion in hex format eg. 0x20000
     * @return true if glVersion is supported
     */
    protected boolean glesSupport(int glVersion) {

        final ConfigurationInfo configurationInfo = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE)).getDeviceConfigurationInfo();
        if (configurationInfo != null) {
            return configurationInfo.reqGlEsVersion >= glVersion;
        } else {
            Base.logE("Monkeys can't find ConfigurationInfo. \n");
            return false;
        }
    }

    /**
     * rotate screen to portrait orientation
     */
    protected void setScreenOrientationPortrait() {

        requestedScreenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    /**
     * rotate screen to portrait orientation with reversed portrait rotation possibility
     */
    protected void setScreenOrientationSensorPortrait() {

        requestedScreenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
    }

    /**
     * rotate screen to landscape orientation
     */
    protected void setScreenOrientationLandscape() {

        requestedScreenOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    /**
     * rotate screen to landscape orientation with reversed landscepe rotation possibility
     */
    protected void setScreenOrientationSensorLandscape() {

        requestedScreenOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
    }

    /**
     * remove status bar and sets activity to whole screen
     */
    protected void setFullScreen() {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /**
     * hides devices soft keys
     * */
    protected void hideVirtualUI() {

        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LOW_PROFILE
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        } else {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LOW_PROFILE);
        }
    }

    /**
     * hides devices soft keys
     * note: this action is sent to ui thread
     * */
    public void hideScreenDeco() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (Base.isDeviceDecorated()) {
                    hideVirtualUI();
                }
            }
        });
    }

    /**
     * sets screen brightness
     * @param reqScreenBrightness 0.0 - 1.0
     * */
    protected void setScreenBrightness(float reqScreenBrightness) {

        final Window window = getWindow();
        final WindowManager.LayoutParams windowLayoutParams = window.getAttributes();
        windowLayoutParams.screenBrightness = reqScreenBrightness;
        window.setAttributes(windowLayoutParams);
    }

    /**
     * prevent device to go sleep or dim display
     */
    protected void preventSleep() {

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    /**
     * sets activity content view
     * can be called twice by system, when user starts with diferent screen orientation - use singletons or static classes etc..
     */
    protected void setView(BaseGLView view) {

        setContentView(view);
    }

    /**
     * sets activity content view
     * can be called twice by system, when user starts with diferent screen orientation - use singletons or static classes etc..
     */
    protected void setView(BaseRender render) {

        setContentView(render.getView());
    }

    /**
     * creates new instance of InAppBilling
     *
     * @param listener class for generating public key, listening user purchases and gathering products data
     */
    public void useInAppBilling(InAppBillingHandler listener) { //todo redesign BaseInAppBilling

        if (inAppBilling != null) {
            inAppBilling.destroy();
            inAppBilling = null;
        }
        inAppBilling = new BaseInAppBilling(listener.generatePublicKey(), listener);
    }

    /**
     * request to purchase by SKU
     *
     * @param ITEM_SKU     item sku in google play developer console - In-app Products
     * @param isConsumable true if unmanaged item
     */
    public void doPurchase(String ITEM_SKU, boolean isConsumable) { //todo singleton of BaseInAppBilling with static doPurchase method

        if (inAppBilling != null) {
            inAppBilling.doPurchase(ITEM_SKU, isConsumable);
        } else {
            Base.logE("Monkeys can't find valid BaseInAppBilling class \n -> Call useInAppBilling(key, listener) properly..");
        }
    }

    /**
     * @return instance of BaseInAppBilling operator
     */
    public BaseInAppBilling getInAppBilling() {

        return inAppBilling;
    }

    /**
     * creates new instance of BaseApiClient and binds Base.glView as parent view for popups (just after when connection is established)
     *
     * @param hardConnect set to true if you want to try to connect when no internet connection is available
     * */
    public void useAchievements(boolean hardConnect) {

        BaseApiClient.init(hardConnect);
        apiClient = BaseApiClient.getInstance();
        apiClient.setOnConnectedAction(new Runnable() {
            @Override
            public void run() {
                BaseAchievements.bindParentViewForPopups(Base.glView);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case BaseInAppBilling.REQUEST_PURCHASE:
                inAppBilling.onActivityResult(requestCode, resultCode, data);
                break;
            case BaseApiClient.REQUEST_RESOLVE_ERROR:
            case BaseApiClient.REQUEST_ACHIEVEMENTS:
            case BaseApiClient.REQUEST_LEADERBORDS:
                if (apiClient == null) {
                    apiClient = BaseApiClient.getInstance();
                }
                apiClient.onActivityResult(requestCode, resultCode, data);
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * add activity state listener, handled when activity calls onPause, onResume
     */
    public void addActivityStateListener(ActivityStateListener listener) {

        if (!activityStateListeners.contains(listener)) {
            activityStateListeners.add(listener);
        }
    }

    public void removeActivityStateListener(ActivityStateListener listener) {

        activityStateListeners.remove(listener);
    }

    /**
     * creates new instance of Handler and runs specific action on UI thread after delay
     */
    public void runOnUiThread(Runnable action, long millisecDelay) {

        new Handler(Looper.getMainLooper()).postAtTime(action, SystemClock.uptimeMillis() + millisecDelay);
    }

    /**
     * starts new activity by class name.. call as MyActivityName.class
     */
    public void startActivity(Class name) {

        startActivity(new Intent(this, name));
    }

    /**
     * starts new application by package name specified in AndroidManifest.. call as "com.mypackage.hello"
     */
    public void startApp(String packageName) {

        try {
            startActivity(getPackageManager().getLaunchIntentForPackage(packageName));
        } catch (ActivityNotFoundException e) {
            Base.toast("Application " + packageName + " not found !");
            e.printStackTrace();
        }
    }

    /**
     * starts Google play app store with this app opened
     * */
    public void showInGooglePlay() {

        showInGooglePlay(getPackageName());
    }

    /**
     * starts Google play app store with specific app opened
     *
     * @param app_package package name of choosen app
     * */
    public void showInGooglePlay(String app_package) {

        String url = "";

        try { //Check whether Google Play store is installed or not:
            this.getPackageManager().getPackageInfo("com.android.vending", 0);
            url = "market://details?id=" + app_package;
        } catch (final Exception e) {
            Base.logE("Monkeys can't find Google Play app store.");
            url = "https://play.google.com/store/apps/details?id=" + app_package;
        }

        final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        startActivity(intent);
    }

    // prevent duplicate calls of start/resume/pause etc.
    private boolean isOrientedWell() { //todo not working properly before

        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (!isOrientedWell()) {
            return;
        }

        if (apiClient != null && !apiClient.userNotLogIn()) {
            if (Base.isConnected()) {
                apiClient.connect();
            }
        }
    }

    @Override
    protected void onStop() {

        if (apiClient != null && isOrientedWell()) {
            apiClient.disconnect();
        }

        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!isOrientedWell()) {
            return;
        }

        if (activityStateListeners != null) {
            for (ActivityStateListener listener : activityStateListeners) {
                if (listener != null) {
                    listener.onResume();
                }
            }
        }

        Base.logV("activity resume");
    }

    @Override
    protected void onPause() {

        if (isOrientedWell()) {
            for (ActivityStateListener listener : activityStateListeners) {
                if (listener != null) {
                    listener.onPause();
                }
            }
        }

        super.onPause();

        Base.logV("activity pause");
    }

    @Override
    protected void onDestroy() {

        if (isOrientedWell()) {
            for (ActivityStateListener listener : activityStateListeners) {
                if (listener != null) {
                    listener.destroy();
                }
            }
            activityStateListeners.clear();
        }

        super.onDestroy();

        Base.logV("activity destroyed");
    }

}
