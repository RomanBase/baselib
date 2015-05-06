package com.base.lib.engine.font;

import com.base.lib.engine.common.DrawableData;

/**
 *
 */
public class BitmapLetter {

    protected char ch;
    protected float[] textureCoords;

    protected float xsize;
    protected float ysize;
    protected float advance;
    protected float offsetX;
    protected float offsetY;

    public char getCh() {
        return ch;
    }

    public void setCh(char ch) {
        this.ch = ch;
    }

    public void initTextureCoords(float x, float y, float w, float h, float textureWidth, float textureHeight){

        xsize = w;
        ysize = h;

        w *= 0.5f;
        h *= 0.5f;

        x += w;
        y += h;

        x /= textureWidth;
        y /= textureHeight;
        w /= textureWidth;
        h /= textureHeight;

        textureCoords = DrawableData.rectangleTextures(x, y, w, h);
    }

    public void setPositioning(float xoffset, float yoffset, float ad){

        offsetX = xoffset;
        offsetY = yoffset;
        advance = ad;
    }
}
