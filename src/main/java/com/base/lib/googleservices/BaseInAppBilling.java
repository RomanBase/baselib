package com.base.lib.googleservices;

import android.content.Intent;

import com.base.lib.engine.Base;
import com.base.lib.googleservices.util.IabHelper;
import com.base.lib.googleservices.util.IabResult;
import com.base.lib.googleservices.util.Inventory;
import com.base.lib.googleservices.util.Purchase;
import com.base.lib.interfaces.ActivityStateListener;

/**
 * 07 Created by doctor on 31.7.13.
 */
public class BaseInAppBilling implements ActivityStateListener { //todo switch to singleton

    private static final String TAG = "InAppBilling";
    public static final int REQUEST_PURCHASE = 9000;

    public static final String TEST_SKU = "android.test.purchased";

    private InAppBillingHandler billingListener;
    private String sku = TEST_SKU;
    private IabHelper mHelper;
    private boolean toConsume;

    public BaseInAppBilling(final String PUBLIC_KEY, final InAppBillingHandler listener){

        Base.activity.addActivityStateListener(this);

        billingListener = listener;
        mHelper = new IabHelper(Base.context, PUBLIC_KEY);
        mHelper.enableDebugLogging(Base.debug);

        startSetup(listener);
    }

    void startSetup(final InAppBillingHandler listener){

        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if(result.isSuccess() && mHelper != null){
                    mHelper.queryInventoryAsync(true, listener.getSkuList(), listener);
                    Base.logV(TAG, "Setup OK");
                } else {
                    Base.logE(TAG, "Setup Failure");
                }
            }
        });
    }

    public void doPurchase(String ITEM_SKU, boolean consumable){ //todo payload

        try{
            sku = ITEM_SKU;
            toConsume = consumable;
            mHelper.launchPurchaseFlow(Base.activity, ITEM_SKU, REQUEST_PURCHASE, mPurchaseFinishedListener, "base");
        } catch (IllegalStateException ex){
            mHelper.flagEndAsync();
            errorToast();
        }
    }

    public void consumeItem() {

        mHelper.queryInventoryAsync(new IabHelper.QueryInventoryFinishedListener() {

            public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
                if (!result.isFailure()) {
                        mHelper.consumeAsync(inventory.getPurchase(sku), mConsumeFinishedListener);
                } else {
                    Base.logE(TAG, "quering inventory failed at " +sku);
                }
            }
        });
    }

    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {

        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            if (result.isFailure()) {
                if(result.getResponse() == IabHelper.BILLING_RESPONSE_RESULT_ITEM_ALREADY_OWNED){
                    billingListener.onPurchaseDone(sku, true);
                    Base.logV(TAG, "item already owned - " + sku);
                } else {
                    billingListener.onPurchaseDone(sku, false);
                    Base.logE(TAG, "purchase was failed at " + sku);
                }
            } else if (purchase.getSku().equals(sku)) {
                if(toConsume) {
                    consumeItem();
                } else if (result.isSuccess()){
                    billingListener.onPurchaseDone(sku, true);
                }
            }

        }
    };

    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {

        public void onConsumeFinished(Purchase purchase, IabResult result) {
            if (result.isSuccess()) {
                billingListener.onPurchaseDone(sku, true);
            } else {
                Base.logE(TAG, "conusume was failed at " +sku);
            }
        }
    };

    public void onActivityResult(int requestCode, int resultCode, Intent data){

        mHelper.handleActivityResult(requestCode, resultCode, data);
    }

    public void errorToast(){

        Base.toastLong("Something went wrong. A team of highly trained monkeys has not received your purchase.");
    }

    @Override
    public void onPause() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void destroy() {

        if (mHelper != null){
            mHelper.flagEndAsync();
            mHelper.dispose();
        }

        mHelper = null;
        billingListener = null;
        sku = null;
    }

    public InAppBillingHandler getBillingListener() {
        return billingListener;
    }

    public void setBillingListener(InAppBillingHandler billingListener) {
        this.billingListener = billingListener;
    }
}

