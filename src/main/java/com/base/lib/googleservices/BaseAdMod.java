package com.base.lib.googleservices;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.base.lib.engine.Base;
import com.base.lib.engine.BaseActivity;
import com.base.lib.engine.Screen;
import com.base.lib.interfaces.ActivityStateListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

/**
 *
 */
public class BaseAdMod implements ActivityStateListener {

    private AdView adView;
    private InterstitialAd interstitial;
    private RelativeLayout layout;
    private AdRequest request;

    private final BaseActivity activity;
    private final Screen screen;
    private final Context context;

    public BaseAdMod(Base base, String banner_ca_app_pub, String interstitial_ca_app_pub) {
        this(base, null, banner_ca_app_pub, interstitial_ca_app_pub);
    }

    public BaseAdMod(Base base, AdRequest adRequest, String banner_ca_app_pub, String interstitial_ca_app_pub) {
        this(base, adRequest, AdSize.SMART_BANNER, banner_ca_app_pub, interstitial_ca_app_pub);
    }

    public BaseAdMod(Base base, AdRequest adRequest, AdSize adSize, String banner_ca_app_pub, String interstitial_ca_app_pub) {
        this.activity = base.activity;
        this.screen = base.screen;
        this.context = base.appContext;

        activity.addActivityStateListener(this);

        request = adRequest == null ? new AdRequest.Builder().build() : adRequest;

        if (banner_ca_app_pub != null && !banner_ca_app_pub.isEmpty()) {
            adView = new AdView(context);
            adView.setAdUnitId(banner_ca_app_pub);
            adView.setAdSize(adSize);

            layout = new RelativeLayout(context);
            layout.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
            layout.addView(adView);
            layout.setVisibility(View.GONE);

            adView.loadAd(request);
        }

        if (interstitial_ca_app_pub != null && !interstitial_ca_app_pub.isEmpty()) {
            interstitial = new InterstitialAd(context);
            interstitial.setAdListener(new InterstitialAdListener());
            interstitial.setAdUnitId(interstitial_ca_app_pub);

            interstitial.loadAd(request);
        }
    }

    public void setBannerSize(AdSize size) {

        adView.setAdSize(size);
    }

    public int getAdSize(){

       return adView.getAdSize().getHeightInPixels(context);
    }

    public boolean isVisible(){

        return layout.getVisibility() == View.VISIBLE;
    }

    public void showAtBottom() {

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showAtPosition(screen.width / 2 - adView.getAdSize().getWidthInPixels(context) / 2,
                        screen.height - adView.getAdSize().getHeightInPixels(context));
            }
        });
    }

    public void showAtTop() {

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showAtPosition(screen.width / 2 - adView.getAdSize().getWidthInPixels(context) / 2, 0);
            }
        });
    }

    public void showAt(final float x, final float y) {

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showAtPosition(x, y);
            }
        });
    }

    private void showAtPosition(float x, float y) {

        if (layout.getParent() != null) {
            ((ViewGroup) layout.getParent()).removeView(layout);
        }

        layout.setVisibility(View.VISIBLE);

        layout.setX(x);
        layout.setY(y);
        activity.addContentView(layout, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
    }

    public void hide() {

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (layout.getVisibility() == View.VISIBLE) {
                    if (layout.getParent() != null) {
                        ((ViewGroup) layout.getParent()).removeView(layout);
                    }
                    layout.setVisibility(View.GONE);
                }
            }
        });
    }

    public void showInterstitial() {

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (interstitial.isLoaded()) {
                    interstitial.show();
                } else {
                    Base.log("Interstitial Ad is not loaded");
                }
            }
        });
    }

    public void reloadInterstitial(){

        interstitial.loadAd(request);
    }

    public void reloadBanner(){

        adView.loadAd(request);
    }

    public AdView getAdView() {

        return adView;
    }

    public RelativeLayout getLayout() {

        return layout;
    }

    public InterstitialAd getInterstitial() {

        return interstitial;
    }

    public void setAdView(AdView adView) {
        this.adView = adView;
    }

    public void setInterstitial(InterstitialAd interstitial) {
        this.interstitial = interstitial;
    }

    public void setLayout(RelativeLayout layout) {
        this.layout = layout;
    }

    public AdRequest getRequest() {
        return request;
    }

    public void setRequest(AdRequest request) {
        this.request = request;
    }

    public void setAdBannerListener(AdListener listener){
        adView.setAdListener(listener);
    }

    public void setAdInterstitialListener(AdListener listener){
        interstitial.setAdListener(listener);
    }

    @Override
    public void onPause() {

        if (adView != null) {
            adView.pause();
        }
    }

    @Override
    public void onResume() {

        if (adView != null) {
            adView.resume();
        }
    }

    @Override
    public void destroy() {

        if (adView != null) {
            adView.destroy();
            adView = null;
        }
    }

    public static AdRequest testRequest(String... deviceAdIDs){

        AdRequest.Builder builder = new AdRequest.Builder();

        if(deviceAdIDs != null) {
            for (String id : deviceAdIDs) {
                builder.addTestDevice(id);
            }
        }

        return builder.build();
    }

    public class InterstitialAdListener extends AdListener{

        @Override
        public void onAdClosed() {

            reloadInterstitial();
        }
    }
}
