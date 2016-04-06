package com.base.lib.engine.common.gl;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import com.base.lib.engine.Base;
import com.base.lib.engine.BaseTexture;

public class TextureHelper {

    private static final int[] textureHandle = new int[1];

    /**
     * loads bitmap texture into gl
     */
    public static int loadTexture(Bitmap bitmap, BaseTexture.Options options) {

        GLES20.glGenTextures(1, textureHandle, 0);

        if (textureHandle[0] != 0) {
            changeTexture(textureHandle[0], bitmap, options);
        } else {
            Base.logE(String.format("Monkeys can generate textures only from GLThread ! [CurrentThread: %s]", Thread.currentThread().getName()));
            throw new RuntimeException("Error creating texture.");
        }

        return textureHandle[0];
    }

    /**
     * change texture by given ID and recycle bitmap
     */
    public static void changeTexture(int textureID, Bitmap bitmap, BaseTexture.Options options) {

        // Bind texture into gl
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureID);

        // Set filtering when texture application is smaller(MIN_FILTER) or larger(MAG_FILTER)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, options.min_filter);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, options.mag_filter);

        // Set wrapping on axis S(x) and T(y)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, options.wrap_s);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, options.wrap_t);

        // Load the bitmap into the bound texture.
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

        // Generate texture Mipmap
        if (options.mipmap) {
            GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
        }

        // Recycle used bitmap
        bitmap.recycle();
    }

    public static int[] generateTextureUnit(BaseTexture.Options options) {

        GLES20.glGenTextures(1, textureHandle, 0);

        // Bind texture into gl
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);

        // Set filtering when texture application is smaller(MIN_FILTER) or larger(MAG_FILTER)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, options.min_filter);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, options.mag_filter);

        // Set wrapping on axis S(x) and T(y)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, options.wrap_s);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, options.wrap_t);

        return textureHandle;
    }

    /**
     * delete texture from gl
     */
    public static void deleteTexture(int textureID) {

        textureHandle[0] = textureID;
        GLES20.glDeleteTextures(1, textureHandle, 0);
    }

    /**
     * delete textures from gl
     */
    public static void deleteTextures(int... textureIDs) {

        if (textureIDs != null) {
            GLES20.glDeleteTextures(textureIDs.length, textureIDs, 0);
        }
    }

    /**
     * delete textures from gl
     */
    public static void deleteTextures(BaseTexture... textures) {

        if (textures != null) {
            int[] ids = new int[textures.length];
            for (int i = 0; i < textures.length; i++) {
                ids[i] = textures[i].glid;
            }
            GLES20.glDeleteTextures(textures.length, ids, 0);
        }
    }
}
