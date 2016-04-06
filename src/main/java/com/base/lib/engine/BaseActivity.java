package com.base.lib.engine;


import android.app.ActivityManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ConfigurationInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.view.WindowManager;

import com.base.lib.engine.builders.BaseBuilder;
import com.base.lib.engine.builders.CameraBuilder;
import com.base.lib.engine.builders.RenderConfig;
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
public abstract class BaseActivity extends FragmentActivity {

    private int requestedScreenOrientation;
    private List<ActivityStateListener> activityStateListeners;
    private BaseInAppBilling inAppBilling;
    private BaseApiClient apiClient;
    protected Base base;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if the system supports OpenGL ES 2.0.
        if (!glesSupport(0x20000)) {
            Base.logE("MISSING OPENGL ES (2+)");
            return;
        }

        // Set the hardware buttons to control the audio media volume
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);

        //hardware acceleration for non gl views
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);

        activityStateListeners = new ArrayList<ActivityStateListener>();

        Base.appContext = getApplicationContext();
        base = onCreate(new BaseBuilder(this));
        base.init(onCreateCamera(new CameraBuilder(base)), onCreateRender(new RenderConfig(base)));

        setContentView(base.render.getView());
    }

    /**
     * is called on end of Activity onCreate(Bundle) function
     */
    protected abstract Base onCreate(BaseBuilder builder);

    protected abstract BaseCamera onCreateCamera(CameraBuilder builder);

    protected abstract BaseRender onCreateRender(RenderConfig config);

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
     * creates new instance of InAppBilling
     *
     * @param listener class for generating public key, listening user purchases and gathering products data
     */
    public void useInAppBilling(InAppBillingHandler listener) { //todo redesign BaseInAppBilling

        if (inAppBilling != null) {
            inAppBilling.destroy();
            inAppBilling = null;
        }
        inAppBilling = new BaseInAppBilling(base, listener.generatePublicKey(), listener);
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
     */
    public void useAchievements(boolean hardConnect) {

        BaseApiClient.init(base, hardConnect);
        apiClient = BaseApiClient.getInstance();
        apiClient.setOnConnectedAction(new Runnable() {
            @Override
            public void run() {
                BaseAchievements.bindParentViewForPopups(base.render.getView());
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
            Base.logE("Application " + packageName + " not found !");
            e.printStackTrace();
        }
    }

    /**
     * starts Google play app store with this app opened
     */
    public void showInGooglePlay() {

        showInGooglePlay(getPackageName());
    }

    /**
     * starts Google play app store with specific app opened
     *
     * @param app_package package name of choosen app
     */
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
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        base.onConfigrationChanged();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (!isOrientedWell()) {
            return;
        }

        if (apiClient != null && !apiClient.userNotLogIn()) {
            if (base.isConnected()) {
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
