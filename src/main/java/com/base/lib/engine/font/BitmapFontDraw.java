package com.base.lib.engine.font;

import com.base.lib.engine.BaseGL;
import com.base.lib.engine.common.Colorf;

/**
 *
 */
public class BitmapFontDraw {

    private static BitmapFontRenderable renderable;

    public static void init(){

        renderable = new BitmapFontRenderable();
    }

    public static void init(BitmapFont font){

        init();
        setFont(font);
    }

    public static void init(BitmapFont font, float size){

        init();
        setFont(font);
        setFontSize(size);
    }

    public static boolean isInitialized(){

        return renderable != null;
    }

    public static void relese(){

        renderable = null;
    }

    public static BitmapFontRenderable getBitmapFontRenderable(){

        return renderable;
    }

    public static void setFont(BitmapFont font){

        renderable.setFont(font);
    }

    public static void setScale(float scale){

        renderable.setScale(scale);
    }

    public static void setFontSize(float size){

        renderable.getFont().setSize(size);
    }

    public static void setAlpha(float a){

        renderable.setAlpha(a);
    }

    public static void setColor(float r, float g, float b, float a){

        renderable.setColor(r, g, b, a);
    }

    public static void setColor(Colorf color){

        renderable.setColor(color);
    }

    public static void horizontale(float x, float y, String text){

        if(text == null)
            return;

        renderable.setPosition(x, y);
        renderable.drawHorizontale(text);
    }

    public static void verticale(float x, float y, String text){

        if(text == null)
            return;

        renderable.setPosition(x, y);
        renderable.drawVerticale(text);
    }

    public static void verticaleUp(float x, float y, String text){

        if(text == null)
            return;

        renderable.setPosition(x, y);
        renderable.drawVerticaleUp(text);
    }

    public static void horizontale(float x, float y, float z, String text){

        if(text == null)
            return;

        renderable.setPosition(x, y, z);
        renderable.drawHorizontale(text);
    }

    public static void verticale(float x, float y, float z, String text){

        if(text == null)
            return;

        renderable.setPosition(x, y, z);
        renderable.drawVerticale(text);
    }

    public static void verticaleUp(float x, float y, float z, String text){

        if(text == null)
            return;

        renderable.setPosition(x, y, z);
        renderable.drawVerticaleUp(text);
    }

    public static void setZPos(float z){

        renderable.setZ(z);
    }

    public static void bindShader() {

        BaseGL.useProgram(renderable.getShader());
    }
}
