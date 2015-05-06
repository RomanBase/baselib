package com.base.lib.engine.glcommon;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import com.base.lib.engine.Base;
import com.base.lib.engine.Texture;

public class TextureHelper
{
    public static int MIN_FILTER = GLES20.GL_LINEAR_MIPMAP_NEAREST;
    public static int MAG_FILTER = GLES20.GL_LINEAR;
    public static int WRAP_S = GLES20.GL_CLAMP_TO_EDGE;
    public static int WRAP_T = GLES20.GL_CLAMP_TO_EDGE;
    public static boolean MIPMAP = true;

    private static final int[] textureHandle = new int[1];

    /** loads bitmap texture into gl */
	public static int loadTexture(Bitmap bitmap){
		
		GLES20.glGenTextures(1, textureHandle, 0);

		if (textureHandle[0] != 0){

            changeTexture(textureHandle[0], bitmap);
		}
		
		else if (textureHandle[0] == 0){
            Base.logE("Monkeys can generate textures only from GLThread !");
			throw new RuntimeException("Error creating texture.");
		}
		
		return textureHandle[0];
	}

    /** change texture by given ID and recycle bitmap */
    public static void changeTexture(int textureID, Bitmap bitmap){

        // Bind texture into gl
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureID);

        // Set filtering when texture application is smaller(MIN_FILTER) or larger(MAG_FILTER)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, MIN_FILTER);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, MAG_FILTER);

        // Set wrapping on axis S(x) and T(y)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, WRAP_S);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, WRAP_T);

        // Load the bitmap into the bound texture.
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

        // Generate texture Mipmap
        if(MIPMAP){
            GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);
        }

        // Recycle used bitmap
        bitmap.recycle();
    }

    public static int[] generateTextureUnit(){

        GLES20.glGenTextures(1, textureHandle, 0);

        // Bind texture into gl
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);

        // Set filtering when texture application is smaller(MIN_FILTER) or larger(MAG_FILTER)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, MIN_FILTER);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, MAG_FILTER);

        // Set wrapping on axis S(x) and T(y)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, WRAP_S);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, WRAP_T);

        return textureHandle;
    }

    /** delete texture from gl */
    public static void deleteTexture(int textureID){

        textureHandle[0] = textureID;
        GLES20.glDeleteTextures(1, textureHandle, 0);
    }

    /** delete textures from gl */
    public static void deleteTextures(int... textureIDs){

        if(textureIDs != null) {
            GLES20.glDeleteTextures(textureIDs.length, textureIDs, 0);
        }
    }

    /** delete textures from gl */
    public static void deleteTextures(Texture... textures){

        if(textures != null) {
            int[] ids = new int[textures.length];
            for(int i = 0; i<textures.length; i++){
                ids[i] = textures[i].glid;
            }
            GLES20.glDeleteTextures(textures.length, ids, 0);
        }
    }
}
