package com.base.lib.engine.common;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

import com.base.lib.engine.Base;
import com.base.lib.engine.BaseGL;
import com.base.lib.engine.Type;
import com.base.lib.engine.common.file.FileHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 10 Created by doctor on 16.8.13.
 */
public class TextureAtlas {

    private List<TextureInfo> textureList;
    private List<BitmapInfo> bitmapList;

    private String atlasName;
    private boolean prescale;

    private enum PlaceTo{ LEFT, DOWN }
    private static final float maxDim = 2048*2048;
    private float dim;
    private float currWidth = 0;
    private float currHeight = 0;

    public TextureAtlas(String atlasName){

        this.atlasName = atlasName;
        bitmapList = new ArrayList<BitmapInfo>();
    }

    public void preScaleLargerTexturesThenScreen(boolean prescale){

        this.prescale = prescale;
    }

    public void add(String file, Type type){

        String name = file.substring(file.lastIndexOf('/')+1, file.lastIndexOf('.'));

        switch (type){
            case STORAGE_ASSETS: add(BitmapHelper.loadBitmap(file), name); break;
            case STORAGE_INTERNAL: add(BitmapHelper.loadBitmap(FileHelper.loadInternal(file)), name); break;
            case STORAGE_SDCARD: add(BitmapHelper.loadBitmap(FileHelper.sdReadFile(file)), name); break;
        }
    }

    public void add(int resourceId){

        add(BitmapHelper.loadBitmap(resourceId), Base.appContext.getResources().getResourceName(resourceId));
    }

    public void add(byte[] bytes, String name){

        add(BitmapHelper.loadBitmap(bytes), name);
    }

    public void add(Bitmap bitmap, String name){

        float w = bitmap.getWidth();
        float h = bitmap.getHeight();

        /*if(prescale){ //todo

            if(w > Base.screenWidth) w = Base.screenWidth;

            if(h > Base.screenHeight) h = Base.screenHeight;

            if(w != bitmap.getWidth() || h != bitmap.getHeight()){
                bitmap = Bitmap.createScaledBitmap(bitmap, (int)w, (int)h, true);  //scale down
            }
        }*/

        dim += w*h;
        if(dim > maxDim){
            Base.logV("Monkey's can't add next image to current atlas. \n " +
                      "Maximum bitmap size: "+ BaseGL.getTextureMaxSize() +" x "+ BaseGL.getTextureMaxSize());
        } else {
            bitmapList.add(new BitmapInfo(bitmap, bitmapList.size(), name, bitmap.getWidth(), bitmap.getHeight()));
        }
    }

    private void calcSizes(){
        currWidth = 0;
        currHeight = 0;
        for(BitmapInfo bi : bitmapList){
            float right = bi.centerX+bi.hWidth;
            float bottom = bi.centerY+bi.hHeight;
            if(right > currWidth){
                currWidth = right;
            }
            if(bottom > currHeight){
                currHeight = bottom;
            }
        }
    }

    private boolean toLeft(BitmapInfo toPlace, BitmapInfo placed){

       return tryToPlace(toPlace, placed, placed.centerX + placed.hWidth + toPlace.hWidth, placed.centerY - placed.hHeight + toPlace.hHeight);
    }

    private boolean toDown(BitmapInfo toPlace, BitmapInfo placed){

        return tryToPlace(toPlace, placed, placed.centerX - placed.hWidth + toPlace.hWidth, placed.centerY + placed.hHeight + toPlace.hHeight);
    }

    private boolean tryToPlace(BitmapInfo toPlace, BitmapInfo placed, float centerX, float centerY){

        if (!Collision2.rectsHit(placed.centerX, placed.centerY, placed.hWidth, placed.hHeight, centerX, centerY, toPlace.hWidth, toPlace.hHeight)) {
            toPlace.centerX = centerX;
            toPlace.centerY = centerY;
            calcSizes();
            tryToPlaceAnother(toPlace, currWidth, currHeight, PlaceTo.LEFT);
            return true;
        } else {
            return false;
        }
    }

    private void tryToPlaceAnother(BitmapInfo placed, float maxWidth, float maxHeight, PlaceTo placeTo){

        final float rightB = placed.centerX + placed.hWidth;
        final float bottomB = placed.centerY + placed.hHeight;

        final float leftB = placed.centerX - placed.hWidth;
        final float topB = placed.centerY - placed.hHeight;

        for(BitmapInfo toPlace : bitmapList){

            if (toPlace.centerX == 0) {
                boolean newPlaced = false;
                switch (placeTo) {
                    case LEFT:
                        if (tryToPlaceOnLeft(toPlace, leftB, rightB, topB, maxWidth, maxHeight)) {
                            newPlaced = true;
                        } else if (tryToPlaceDown(toPlace, leftB, topB, bottomB, maxWidth, maxHeight)) {
                            newPlaced = true;
                        }
                        break;
                    case DOWN:
                        if (tryToPlaceDown(toPlace, leftB, topB, bottomB, maxWidth, maxHeight)) {
                            newPlaced = true;
                        } else if (tryToPlaceOnLeft(toPlace, leftB, rightB, topB, maxWidth, maxHeight)) {
                            newPlaced = true;
                        }
                        break;
                }
                if(newPlaced){
                    break;
                }
            }
        }
    }

    private boolean tryToPlaceOnLeft(BitmapInfo toPlace, float leftB, float rightB, float topB, float maxWidth, float maxHeight){

        if (rightB + toPlace.hWidth * 2 <= maxWidth && topB + toPlace.hHeight * 2 <= maxHeight && notCollide(toPlace, rightB+toPlace.hWidth, topB+toPlace.hHeight)) {
            toPlace.centerX = rightB + toPlace.hWidth;
            toPlace.centerY = topB + toPlace.hHeight;
            tryToPlaceAnother(toPlace, maxWidth, maxHeight, PlaceTo.DOWN);
            return true;
        } else if(notCollide(toPlace, leftB-toPlace.hWidth, topB+toPlace.hHeight) && leftB-toPlace.hWidth*2 >= 0){
            toPlace.centerX = leftB - toPlace.hWidth;
            toPlace.centerY = topB + toPlace.hHeight;
            tryToPlaceAnother(toPlace, maxWidth, maxHeight, PlaceTo.DOWN);
            return true;
        } else {
            return false;
        }
    }

    private boolean tryToPlaceDown(BitmapInfo toPlace, float leftB, float topB, float bottomB, float maxWidth, float maxHeight){

        if (bottomB + toPlace.hHeight * 2 <= maxHeight && leftB + toPlace.hWidth * 2 <= maxWidth && notCollide(toPlace, leftB+toPlace.hWidth, bottomB+toPlace.hHeight)) {
            toPlace.centerX = leftB + toPlace.hWidth;
            toPlace.centerY = bottomB + toPlace.hHeight;
            tryToPlaceAnother(toPlace, maxWidth, maxHeight, PlaceTo.LEFT);
            return true;
        } else if(notCollide(toPlace, leftB+toPlace.hWidth, topB-toPlace.hHeight) && topB-toPlace.hHeight*2 >= 0){
            toPlace.centerX = leftB + toPlace.hWidth;
            toPlace.centerY = topB - toPlace.hHeight;
            tryToPlaceAnother(toPlace, maxWidth,maxHeight, PlaceTo.LEFT);
            return true;
        } else {
            return false;
        }
    }

    private boolean notCollide(BitmapInfo toPlace, float centerX, float centerY){

        for(BitmapInfo placed : bitmapList){
            if(placed.centerX != 0){
                if(Collision2.rectsHit(placed.centerX, placed.centerY, placed.hWidth, placed.hHeight,
                    centerX, centerY, toPlace.hWidth, toPlace.hHeight)){
                    return false;
                }
            }
        }
        return true;
    }

    public Bitmap buildAtlas(){

        bitmapList = sort(bitmapList);

        BitmapInfo placed = bitmapList.get(0);
        placed.centerX = placed.hWidth;
        placed.centerY = placed.hHeight;
        currWidth = placed.hWidth*2;
        currHeight = placed.hHeight*2;

        final int count = bitmapList.size();
        for (int i = 1; i < count; i++) {
            BitmapInfo toPlace = bitmapList.get(i);
            if (toPlace.centerX == 0) {
                for (int j = 0; j < i; j++) {
                    placed = bitmapList.get(j);

                    if (Collision2.rectsHit(placed.centerX, placed.centerY, placed.hWidth, placed.hHeight, toPlace.centerX, toPlace.centerY, toPlace.hWidth, toPlace.hHeight)) {
                        if (currWidth <= currHeight) {
                            if (!toLeft(toPlace, placed)) {
                                toDown(toPlace, placed);
                            }
                        } else {
                            if (!toDown(toPlace, placed)) {
                                toLeft(toPlace, placed);
                            }
                        }
                    }
                }
            }
        }

        Bitmap bitmap = Bitmap.createBitmap((int)(currWidth), (int)(currHeight), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        for(BitmapInfo bi : bitmapList){
            int left = (int)(bi.centerX-bi.hWidth);
            int top = (int)(bi.centerY-bi.hHeight);
            canvas.drawBitmap(bi.bitmap, null, new Rect(left, top, left + (int)bi.hWidth*2, top + (int)bi.hHeight*2), null);
        }

        textureList = createTextureInfo(bitmapList, currWidth, currHeight);

        return bitmap;
    }

    public List<TextureInfo> getTextureInfoList(){

        return textureList;
    }

    public TextureInfo getTextureInfo(String name){

        for(TextureInfo info : textureList){
            if(info.name.equals(name)){
                return info;
            }
        }

        return null;
    }

    public TextureInfo getTextureInfo(int index){

        if(index > -1 && index < textureList.size()){
            return textureList.get(index);
        }

        return null;
    }


    static List<TextureInfo> createTextureInfo(List<BitmapInfo> bitmapList, float width, float height){

        List<TextureInfo> out = new ArrayList<TextureInfo>();

        for(BitmapInfo bitmap : bitmapList){

            out.add(new TextureInfo(bitmap.id, bitmap.name, bitmap.centerX/width, 1.0f - bitmap.centerY/height, bitmap.hWidth/width, 1.0f/bitmap.hHeight/height));
            bitmap.bitmap.recycle();
            bitmap = null;
        }

        bitmapList.clear();
        bitmapList = null;

        return out;
    }


    static List<BitmapInfo> sort(List<BitmapInfo> textureList){

        Collections.sort(textureList, new Comparator<BitmapInfo>() {
            @Override
            public int compare(BitmapInfo texture, BitmapInfo texture2) {

                if(texture.hWidth*texture.hHeight > texture2.hWidth*texture2.hHeight){
                    return -1;
                } else {
                    return 1;
                }
            }
        });

        return textureList;
    }

    class BitmapInfo extends TextureInfo{

        protected Bitmap bitmap;

        protected BitmapInfo(Bitmap bitmap, int id, String name, float width, float height){
            this.bitmap = bitmap;
            this.id = id;
            this.name = name;
            this.hWidth = width/2.0f;
            this.hHeight = height/2.0f;
        }

    }


}


