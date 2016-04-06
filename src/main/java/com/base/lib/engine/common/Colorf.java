package com.base.lib.engine.common;

import java.util.Locale;
import java.util.Random;

/**
 * GLES float based color
 * <p>
 * rgb 0.0f 0.0f 0.0f is black color</br>
 * rgb 1.0f 1.0f 1.0f is white color</br>
 * alfa 0.0f is full transparent</br>
 * alfa 1.0f is full solid</br>
 * </p>
 */
public class Colorf {

    public float a;
    public float r;
    public float g;
    public float b;

    /**
     * Empty constructor for gles float color model
     * argb sets to 0.0f
     */
    public Colorf() {
        a = r = g = b = 1.0f;
    }

    /**
     * create gles float based color model from standart rgba color model
     *
     * @param red,green,blue values 0 - 255</br>
     *                       alfa channel is set to 255(1.0f)
     */
    public Colorf(int red, int green, int blue) {

        set(red, green, blue, 255);
    }

    /**
     * create gles float based color model from standart rgba color model
     *
     * @param red,green,blue,alfa values 0 - 255
     */
    public Colorf(int red, int green, int blue, int alfa) {

        set(red, green, blue, alfa);
    }

    /**
     * create gles float based color model from standart rgba color model
     *
     * @param red,green,blue values 0.0 - 255.0</br>
     *                       alfa channel is set to 255(1.0f)
     */
    public Colorf(double red, double green, double blue) {

        set(red, green, blue, 255);
    }

    /**
     * create gles float based color model from standart rgba color model
     *
     * @param red,green,blue,alfa values 0.0 - 255.0
     */
    public Colorf(double red, double green, double blue, double alfa) {

        set(red, green, blue, alfa);
    }

    /**
     * create new float based color
     *
     * @param red,green,blue values 0.0f - 1.0f</br>
     *                       alfa channel is set to 1.0f
     */
    public Colorf(float red, float green, float blue) {

        setf(red, green, blue, 1.0f);
    }

    /**
     * create new float based color
     *
     * @param red,green,blue,alfa values 0.0f - 1.0f
     */
    public Colorf(float red, float green, float blue, float alfa) {

        setf(red, green, blue, alfa);
    }

    public Colorf(Colorf color) {

        setf(color.r, color.g, color.b, color.a);
    }

    /**
     * convert from standart rgba color model into gles float based color model
     *
     * @param red,green,blue,alfa values 0 - 255
     */
    public void set(double red, double green, double blue, double alfa) {

        a = (float) alfa / 255.0f;
        r = (float) red / 255.0f;
        g = (float) green / 255.0f;
        b = (float) blue / 255.0f;
    }

    /**
     * sets rgba float values into color
     *
     * @param red,green,blue,alfa values 0.0f - 1.0f
     */
    public void setf(float red, float green, float blue, float alfa) {

        a = alfa;
        r = red;
        g = green;
        b = blue;
    }

    public float[] getArray() {

        return new float[]{r, g, b, a};
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "rgba: [%s %s %s %s]", r, g, b, a);
    }

    /**
     * creates random color, based on standart Random method nextFloat
     *
     * @return a color
     */
    public static Colorf randomColor() {

        Random random = new Random();

        return new Colorf(random.nextFloat(), random.nextFloat(), random.nextFloat(), 1.0f);
    }

    /**
     * creates random color, base on standart Random method nextFloat
     *
     * @param alfa 0.0f - fully transparent, 1.0f - solid
     * @return a color
     */
    public static Colorf randomColor(float alfa) {

        Random random = new Random();

        return new Colorf(random.nextFloat(), random.nextFloat(), random.nextFloat(), alfa);
    }

    public Colorf copy() {

        return new Colorf(this);
    }

    public Colorf copy(float alpha) {

        return new Colorf(this.r, this.g, this.b, alpha);
    }

    public Colorf copy(float dim, float alpha) {

        return new Colorf(this.r + dim, this.g + dim, this.b + dim, alpha);
    }
}
