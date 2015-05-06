package com.base.lib.googleservices;

import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.base.lib.engine.Base;
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

    public BaseAdMod(String banner_ca_app_pub, String interstitial_ca_app_pub) {
        this(null, banner_ca_app_pub, interstitial_ca_app_pub);
    }

    public BaseAdMod(AdRequest adRequest, String banner_ca_app_pub, String interstitial_ca_app_pub) {
        this(adRequest, AdSize.SMART_BANNER, banner_ca_app_pub, interstitial_ca_app_pub);
    }

    public BaseAdMod(AdRequest adRequest, AdSize adSize, String banner_ca_app_pub, String interstitial_ca_app_pub) {

        Base.activity.addActivityStateListener(this);

        request = adRequest == null ? new AdRequest.Builder().build() : adRequest;

        if (banner_ca_app_pub != null && !banner_ca_app_pub.isEmpty()) {
            adView = new AdView(Base.context);
            adView.setAdUnitId(banner_ca_app_pub);
            adView.setAdSize(adSize);

            layout = new RelativeLayout(Base.context);
            layout.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
            layout.addView(adView);
            layout.setVisibility(View.GONE);

            adView.loadAd(request);
        }

        if (interstitial_ca_app_pub != null && !interstitial_ca_app_pub.isEmpty()) {
            interstitial = new InterstitialAd(Base.context);
            interstitial.setAdListener(new InterstitialAdListener());
            interstitial.setAdUnitId(interstitial_ca_app_pub);

            interstitial.loadAd(request);
        }
    }

    public void setBannerSize(AdSize size) {

        adView.setAdSize(size);
    }

    public int getAdSize(){

       return adView.getAdSize().getHeightInPixels(Base.context);
    }

    public boolean isVisible(){

        return layout.getVisibility() == View.VISIBLE;
    }

    public void showAtBottom() {

        Base.activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showAtPosition(Base.screenWidth / 2 - adView.getAdSize().getWidthInPixels(Base.context) / 2,
                        Base.screenHeight - adView.getAdSize().getHeightInPixels(Base.context));
            }
        });
    }

    public void showAtTop() {

        Base.activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showAtPosition(Base.screenWidth / 2 - adView.getAdSize().getWidthInPixels(Base.context) / 2, 0);
            }
        });
    }

    public void showAt(final float x, final float y) {

        Base.activity.runOnUiThread(new Runnable() {
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
        Base.activity.addContentView(layout, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
    }

    public void hide() {

        Base.activity.runOnUiThread(new Runnable() {
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

        Base.activity.runOnUiThread(new Runnable() {
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
