package com.base.lib.wall;

import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.support.annotation.NonNull;

import com.base.lib.engine.Base;
import com.base.lib.engine.BaseObject;
import com.base.lib.interfaces.ConfigChangedListener;

import java.util.ArrayList;
import java.util.List;

public class PaperBase extends BaseObject {

    public BasePaperService service;
    public volatile float screenOffset;
    public volatile float screenStep;
    public float bgWidth;
    public float bgHeight;
    public float bgRatio;

    private List<ConfigChangedListener> configListener;
    private float cameraRange;

    public PaperBase(@NonNull Base base, @NonNull BasePaperService service) {
        super(base);
        this.service = service;
    }

    private final Runnable updateCamera = new Runnable() {
        @Override
        public void run() {
            base.camera.translate(cameraRange * screenOffset, 0.0f);
            base.camera.update();
        }
    };

    /**
     * called by BasePaperService to update camera X position
     */
    void onOffsetChange(float xOffset, float xOffsetStep) {
        screenOffset = xOffset - 0.5f;
        screenStep = xOffsetStep;

        base.render.runOnBaseThread(updateCamera);
        if(!base.render.isFPSRender()) {
            base.render.render();
        }
    }

    void onConfigurationChanged(Configuration newConfig) {

        //bgHeight = Base.camera.getHeight();
        setBackgroundWidth(bgHeight * bgRatio);

        if (configListener != null) {
            for (ConfigChangedListener listener : configListener) {
                listener.onConfigurationChanged(newConfig);
            }
        }

        if (base.render != null) {
            base.render.render();
        }
    }

    public void init(float ratio) {
        setBackgroundRatio(ratio);
        setBackgroundWidth(bgHeight * ratio);
    }

    public void setBackgroundWidth(float width) {

        bgWidth = width;
        //cameraRange = width - Base.camera.getWidth();
        if (cameraRange < 0) {
            cameraRange = 0;
        }
        onOffsetChange(getRealOffset(), screenStep);
    }

    public void setBackgroundRatio(float ratio) {
        bgRatio = ratio;
    }

    public int getNumberOfScreens() {

        return (int) ((1.0f / screenStep)) + 1;
    }

    public float getRealOffset() {

        return screenOffset + 0.5f;
    }

    public boolean isOnEdge() {

        return screenOffset == -0.5f || screenOffset == 0.5f;
    }

    public boolean isOnLeftEdge() {

        return screenOffset == -0.5f;
    }

    public boolean isOnRightEdge() {

        return screenOffset == 0.5f;
    }

    public void addConfigListener(ConfigChangedListener listener) {

        if (configListener == null) {
            configListener = new ArrayList<>();
        }

        configListener.add(listener);
    }

    public void addConfigListenerIfNotContains(ConfigChangedListener listener) {

        if (configListener == null) {
            configListener = new ArrayList<>();
        } else {
            if (configListener.contains(listener)) {
                return;
            }
        }

        configListener.add(listener);
    }

    public void removeConfigListener(ConfigChangedListener listener) {

        configListener.remove(listener);
    }

    public void wallIntent(String packageName, String canonicalName) {

        Intent intent = new Intent();

        if (Build.VERSION.SDK_INT > 15) {
            intent.setAction(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
            intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, new ComponentName(packageName, canonicalName));
        } else {
            intent.setAction(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER);
        }
        service.startActivity(intent);
    }
}
