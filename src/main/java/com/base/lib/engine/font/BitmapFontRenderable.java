package com.base.lib.engine.font;

import android.opengl.GLES20;

import com.base.lib.engine.Base;
import com.base.lib.engine.BaseGL;
import com.base.lib.engine.BaseRenderable;
import com.base.lib.engine.BaseShader;
import com.base.lib.engine.DrawableBuffer;
import com.base.lib.engine.common.BaseDrawableData;
import com.base.lib.engine.common.BaseMatrix;
import com.base.lib.engine.common.Colorf;
import com.base.lib.engine.common.DrawableData;

/**
 *
 */
public class BitmapFontRenderable extends BaseRenderable { //todo new shader a(x y, w h, sc tc sw sh) u(r g b a)

    private DrawableBuffer buffer;
    private StringBuilder text;
    private BitmapFont font;
    private Colorf color;
    private float x, y, z, scale;

    public BitmapFontRenderable() {

        scale = 1.0f;
        text = new StringBuilder();
        color = new Colorf(1.0f, 1.0f, 1.0f, 1.0f);
        shader = BaseShader.mixTextureColorShader();
        BaseDrawableData data = DrawableData.RECTANGLE(1.0f, 1.0f);
        data.setOrigin(-0.5f, 0.5f);
        buffer = new DrawableBuffer(data);
        buffer.setShader(shader);
    }

    public BitmapFontRenderable(BitmapFont font) {
        this();
        this.font = font;
    }

    public BitmapFontRenderable(BitmapFont font, String text) {
        this();
        this.font = font;
        this.text.append(text);
    }

    public BitmapFontRenderable(float x, float y, BitmapFont font, String text) {
        this(font, text);
        this.x = x;
        this.y = y;
    }

    @Override
    public void draw() {

        drawHorizontale(text.toString());
    }

    public void drawHorizontale(String text) {

        int count = text.length();
        float[] m = BaseMatrix._matrix;

        BaseGL.bindTexture(font.texture);

        GLES20.glUniform4f(shader.handle[3], color.r, color.g, color.b, color.a);
        buffer.glPutVerticeBuffer();
        float px = x;
        float py = y + font.base * scale;
        for (int i = 0; i < count; i++) {
            BitmapLetter l = font.chars[text.charAt(i)];
            if (l == null) {
                if (text.charAt(i) == '\n') {
                    py -= font.lineHeight * scale;
                    px = x;
                    continue;
                }
                l = font.chars[(char) 32];
            }

            buffer.putTextureCoords(l.textureCoords);
            buffer.glPutTextureBuffer();

            BaseMatrix.setSMIdentity();
            m[12] = px + l.offsetX * scale;
            m[13] = py - l.offsetY * scale;
            m[14] = z;
            BaseMatrix.scale(m, l.xsize * scale, l.ysize * scale, 1.0f);
            BaseMatrix.multiplyMC(m, Base.camera);

            buffer.glPutMVPMatrix(m);
            buffer.glPutDraw();
            px += l.advance * scale;
        }
        buffer.glDisableAttribArray();
    }

    public void drawVerticale(String text) {

        int count = text.length();
        float[] m = BaseMatrix._matrix;

        BaseGL.bindTexture(font.texture);

        GLES20.glUniform4f(shader.handle[3], color.r, color.g, color.b, color.a);
        buffer.glPutVerticeBuffer();
        float px = y;
        float py = x + font.base * scale;
        for (int i = 0; i < count; i++) {
            BitmapLetter l = font.chars[text.charAt(i)];
            if (l == null) {
                if (text.charAt(i) == '\n') {
                    py -= font.lineHeight * scale;
                    px = y;
                    continue;
                }
                l = font.chars[(char) 32];
            }

            buffer.putTextureCoords(l.textureCoords);
            buffer.glPutTextureBuffer();

            BaseMatrix.setSMIdentity();
            m[12] = py - l.offsetY * scale;
            m[13] = px - l.offsetX * scale;
            m[14] = z;
            BaseMatrix.rotateZ(m, -90.0f);
            BaseMatrix.scale(m, l.xsize * scale, l.ysize * scale, 1.0f);
            BaseMatrix.multiplyMC(m, Base.camera);

            buffer.glPutMVPMatrix(m);
            buffer.glPutDraw();
            px -= l.advance * scale;
        }
        buffer.glDisableAttribArray();
    }

    public void drawVerticaleUp(String text) {

        int count = text.length();
        float[] m = BaseMatrix._matrix;

        BaseGL.bindTexture(font.texture);

        GLES20.glUniform4f(shader.handle[3], color.r, color.g, color.b, color.a);
        buffer.glPutVerticeBuffer();
        float px = y;
        float py = x - font.base * scale;
        for (int i = 0; i < count; i++) {
            BitmapLetter l = font.chars[text.charAt(i)];
            if (l == null) {
                if (text.charAt(i) == '\n') {
                    py += font.lineHeight * scale;
                    px = y;
                    continue;
                }
                l = font.chars[(char) 32];
            }

            buffer.putTextureCoords(l.textureCoords);
            buffer.glPutTextureBuffer();

            BaseMatrix.setSMIdentity();
            m[12] = py + l.offsetY * scale;
            m[13] = px + l.offsetX * scale;
            m[14] = z;
            BaseMatrix.rotateZ(m, 90.0f);
            BaseMatrix.scale(m, l.xsize * scale, l.ysize * scale, 1.0f);
            BaseMatrix.multiplyMC(m, Base.camera);

            buffer.glPutMVPMatrix(m);
            buffer.glPutDraw();
            px += l.advance * scale;
        }
        buffer.glDisableAttribArray();
    }

    @Override
    public void update() {

    }

    @Override
    public void destroy() {

    }

    public void setPosition(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public StringBuilder getText() {
        return text;
    }

    public void setText(String text) {
        this.text.setLength(0);
        this.text.append(text);
    }

    public void setTextBuffer(StringBuilder text) {
        this.text = text;
    }

    public void addText(String text) {
        this.text.append(text);
    }

    public BitmapFont getFont() {
        return font;
    }

    public void setFont(BitmapFont font) {
        this.font = font;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getZ() {
        return z;
    }

    public void setZ(float z) {
        this.z = z;
    }

    public Colorf getColor() {
        return color;
    }

    public void setColor(float r, float g, float b, float a) {
        this.color.setf(r, g, b, a);
    }

    public void setColor(Colorf color) {
        this.color = color;
    }

    public void setAlpha(float a){
        color.a = a;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }
}
