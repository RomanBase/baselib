package com.base.lib.engine.font;

import com.base.lib.engine.BaseTexture;

/**
 *
 */
public class BitmapFont {

    protected BitmapLetter[] chars;
    protected BaseTexture texture;

    protected float size;
    protected float lineHeight;
    protected float base;

    public void setTexture(BaseTexture map){

        texture = map;
    }

    protected void initCharArray(int count){

        chars = new BitmapLetter[count];
    }

    protected void reArrange(){

        int count = 0;
        for(BitmapLetter l : chars){
            if(l.ch > count){
                count = l.ch;
            }
        }

        BitmapLetter[] letters = new BitmapLetter[count+1];
        for (BitmapLetter l : chars) {
            letters[l.ch] = l;
        }

        chars = letters;
    }

    protected BitmapLetter newChar(int position){

        return chars[position] = new BitmapLetter();
    }

    protected void setLineProperties(float h, float b){

        lineHeight = h;
        base = b;
    }

    public void scale(float ratio){

        size *= ratio;
        lineHeight *= ratio;
        base *= ratio;
        for(BitmapLetter letter : chars){
            if(letter != null) {
                letter.advance *= ratio;
                letter.offsetX *= ratio;
                letter.offsetY *= ratio;
                letter.xsize *= ratio;
                letter.ysize *= ratio;
            }
        }
    }

    public void setSize(float fontSize){

        scale(fontSize/size);
    }

    public void scaleLineHeight(float ratio){

        lineHeight *= ratio;
        base *= ratio;
    }

    public float getSize() {
        return size;
    }

    public float getLength(String text){

        if(text == null)
            return 0.0f;

        float len = 0.0f;
        int count = text.length();
        for(int i = 0; i<count; i++) {
            BitmapLetter l = chars[text.charAt(i)];
            if (l == null) {
                l = chars[(char) 32];
            }

            len += l.advance;
        }

        return len;
    }

    public BitmapLetter[] getChars() {
        return chars;
    }

    public BaseTexture getTexture() {
        return texture;
    }

    public float getLineHeight() {
        return lineHeight;
    }

    public float getBase() {
        return base;
    }
}
