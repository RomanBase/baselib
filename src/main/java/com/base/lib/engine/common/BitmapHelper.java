package com.base.lib.engine.common;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.base.lib.engine.Base;

/**
 * Bitmap operations helper.
 * loading bitmaps from any resource
 * compresing bitmaps into png, etc.
 * */
public class BitmapHelper {

    /**
     * create bitmap from resource
     * @param resourceId resource - eg. R.drawable.glid
     * @return a bitmap
     * */
    public static Bitmap loadBitmap(int resourceId){

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;	// No pre-scaling

        return BitmapFactory.decodeResource(Base.appContext.getResources(), resourceId, options);
    }

    /**
     * create bitmap from assets file
     * @param file assets file path
     * @return a bitmap
     * */
    public static Bitmap loadBitmap(String file){

        Bitmap bitmap = null;

        try {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;	// No pre-scaling

            InputStream is = Base.appContext.getAssets().open(file, AssetManager.ACCESS_STREAMING);
            bitmap = BitmapFactory.decodeStream(is, null, options);

            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    /**
     * create bitmap from byte array
     * @param bytes byte[] array of bitmap's data
     * @return a bitmap
     * */
    public static Bitmap loadBitmap(byte[] bytes){

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;	// No pre-scaling

        return BitmapFactory.decodeStream(new ByteArrayInputStream(bytes), null, options); //close ByteArrayInputStream is not required
    }



    /**
     * @param bitmap Bitmap to compress
     * @return byte[] array of bitmap's data
     * */
    public static byte[] getBitmapBytes(Bitmap bitmap){

        ByteArrayOutputStream bytes = new ByteArrayOutputStream(); //close ByteArrayOutupStream is not required
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes);

        return bytes.toByteArray();
    }

    /**
     * @param bitmap Bitmap to compress
     * @param format PNG, JPEG, WEBR
     * @param quality 0 - 100 (0 - small, 100 - max) PNG(lossless) will ignore quality value
     * @return byte[] array of bitmap's data
     * */
    public static byte[] getBitmapBytes(Bitmap bitmap, Bitmap.CompressFormat format, int quality){

        ByteArrayOutputStream bytes = new ByteArrayOutputStream(); //close ByteArrayOutupStream is not required
        bitmap.compress(format, quality, bytes);

        return bytes.toByteArray();
    }

    /**
     * create resized bitmap
     * @param bitmap Bitmap to resize
     * @param newWidth requested width of bitmap
     * @param newHeight requested height of bitmap
     * @return resized bitmap
     * */
	public static Bitmap resize(Bitmap bitmap, float newWidth, float newHeight) {

		int width = bitmap.getWidth();
		int height = bitmap.getHeight();

		float scaleWidth  = newWidth / (float)width;
		float scaleHeight = newHeight / (float)height;

		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);

        Bitmap out = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false);

        bitmap.recycle();

        return out;
	}

    /**
     * create bitmap from resource with given width and height
     * @param resourceID resource glid of bitmap ( eg. R.drawable.glid)
     * @param width requested width of bitmap
     * @param height requested height of bitmap
     * @return resized bitmap
     * */
	public static Bitmap loadResizedBitmap(int resourceID, float width, float height){

		return resize(loadBitmap(resourceID), width, height);
	}

    /**
     * create bitmap from assets file with given width and height
     * @param file assets file path of bitmap
     * @param width requested width of bitmap
     * @param height requested height of bitmap
     * @return resized bitmap
     * */
    public static Bitmap loadResizedBitmap(String file, float width, float height){

        return resize(loadBitmap(file), width, height);
    }

    /**
     * create bitmap from byte array with given width and height
     * @param bytes byte[] array of bitmap
     * @param width requested width of bitmap
     * @param height requested height of bitmap
     * @return resized bitmap
     * */
    public static Bitmap loadResizedBitmap(byte[] bytes, float width, float height){

        return resize(loadBitmap(bytes), width, height);
    }

}
