package com.base.lib.googleservices;

import com.base.lib.engine.Base;
import com.base.lib.engine.common.FileHelper;
import com.base.lib.googleservices.util.IabHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 10 Created by doctor on 31.7.13.
 */
public abstract class InAppBillingHandler implements IabHelper.QueryInventoryFinishedListener{

    protected List<String> skuList;

    public InAppBillingHandler(){

        skuList = new ArrayList<String>();
    }

    public abstract String generatePublicKey();
    public abstract void onPurchaseDone(String sku, boolean successful);

    public void addSku(String sku){

        skuList.add(sku);
    }

    public void addSku(String... skus){

        if(skus != null){
            Collections.addAll(skuList, skus);
        }
    }

    public void addSku(int resrouceID){

        skuList.add(FileHelper.resourceString(resrouceID));
    }

    public void addSku(int... resrouceIDs){

        for(int id : resrouceIDs){
            skuList.add(FileHelper.resourceString(id));
        }
    }

    public void clearSkus(){

        skuList.clear();
    }

    public static void doPurchase(String sku, boolean consumable) {

        Base.activity.doPurchase(sku, consumable);
    }

    public void fakePurchase(String... skus){

        if(Base.debug && skus != null){
            for(String sku : skus){
                onPurchaseDone(sku, true);
            }
        }
    }

    public List<String> getSkuList() {
        return skuList;
    }

    public void setSkuList(List<String> skuList) {
        this.skuList = skuList;
    }

    public static InAppBillingHandler reference(){

        return Base.activity.getInAppBilling().getBillingListener();
    }
}
