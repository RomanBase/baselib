package com.base.lib.engine.other.dev;

import android.opengl.GLES20;

import com.base.lib.R;
import com.base.lib.engine.Base;
import com.base.lib.engine.BaseCamera;
import com.base.lib.engine.BaseGL;
import com.base.lib.engine.BaseRender;
import com.base.lib.engine.BaseRenderable;
import com.base.lib.engine.BaseShader;
import com.base.lib.engine.BaseStaticDrawable;
import com.base.lib.engine.BaseTime;
import com.base.lib.engine.BaseUpdateable;
import com.base.lib.engine.DrawableBuffer;
import com.base.lib.engine.Texture;
import com.base.lib.engine.common.Buffers;
import com.base.lib.engine.common.Colorf;
import com.base.lib.engine.common.DrawableData;
import com.base.lib.engine.common.TextureInfo;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * 13 Created by doctor on 15.8.13.
 */
public class FpsBar extends BaseRenderable{

    private final List<Num> collection;
    private final FloatBuffer[] texCoors;

    private Colorf color;
    private float requestedFps;
    private float goodFps;
    private float badFps;

    public FpsBar() {
        collection = new ArrayList<Num>(6);

        BaseCamera _camera = BaseCamera.ortho(Base.landscape() ? 10.0f : 10.0f * Base.screenRatio);
        BaseShader _shader = BaseShader.mixTextureColorShader();

        if (_shader == null) {
            String shaderName = BaseGL.SHADERS[3];
            _shader = new BaseShader(shaderName, "u_MVPMatrix", "a_Position", "a_TexCoordinate", "u_Color");
            _shader.loadShadersFromResources(R.raw.texture_fade_vertex, R.raw.texture_fade_fragment);
            Base.render.rebindShaderCollection();
            Base.logI("FpBar", "Initialized '" + shaderName + "' shader");
        }

        color = new Colorf();
        requestedFps = (float) Base.render.getRequestedFPS();
        goodFps = requestedFps - 6.0f;
        badFps = goodFps - 6.0f;

        float x = -_camera.getSemiWidth() + 0.175f;
        float y = _camera.getSemiHeight() - 0.175f;
        float w = 0.15f;

        texCoors = new FloatBuffer[12];
        float[][] coords = TextureInfo.rectangleTextureCoords(TextureInfo.sprite(512, 512, 4, 4));
        for (int i = 0; i < texCoors.length; i++) {
            texCoors[i] = Buffers.floatBuffer(coords[i]);
        }

        Texture texture = Texture.resources(R.drawable.nums);
        DrawableBuffer buffer = new DrawableBuffer(DrawableData.RECTANGLE(w, w));
        buffer.setShader(_shader);
        for (int i = 0; i < 6; i++) {
            Num num = new Num(buffer, _camera, _shader);
            num.setTexture(texture);
            collection.add(num);
        }

        collection.get(0).translate(x, y, 0);
        collection.get(1).translate(x += w, y, 0);
        collection.get(2).translate(x += w * 0.435f, y - w * 0.25f, 0);
        collection.get(3).translate(x += w * 0.55f, y, 0);
        collection.get(4).translate(x += w, y, 0);
        collection.get(5).translate(x += w, y, 0);

        collection.get(2).num = 10;
        collection.get(2).scale(0.35f);

        Base.activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (Base.render != null) {
                    Base.render.addUpdateable(new FpsRecorder());
                }
            }
        }, 3000);

        if(Base.render instanceof BaseRender){
            ((BaseRender) Base.render).addPostDrawable(this);
        } else {
            Base.render.addDrawable(this);
        }
    }

    public void update() {

        float fps = Base.render.getCurrentFPS();

        String sfps = String.format("%.2f", fps);

        int len = sfps.length();
        if (len < 5) {
            sfps = "0" + sfps;
        } else if (len > 5) {
            sfps = "99.99";
        }

        collection.get(0).num = Character.getNumericValue(sfps.charAt(0));
        collection.get(1).num = Character.getNumericValue(sfps.charAt(1));
        collection.get(3).num = Character.getNumericValue(sfps.charAt(3));
        collection.get(4).num = Character.getNumericValue(sfps.charAt(4));
        collection.get(5).num = 11;

        if (fps > requestedFps) {
            color.setf(0.0f, 0.75f, 0.25f, 1.0f);
        } else if (fps > goodFps) {
            color.setf(0.85f, 0.85f, 0.85f, 1.0f);
        } else if (fps > badFps) {
            color.setf(1.0f, 0.5f, 0.0f, 1.0f);
        } else {
            color.setf(1.0f, 0.25f, 0.0f, 1.0f);
        }
    }

    @Override
    public void draw() {

        for(Num num : collection){
            num.draw();
        }
    }

    @Override
    public void destroy() {

    }

    private class Num extends BaseStaticDrawable {

        private int num;

        private Num(DrawableBuffer buffer, BaseCamera _camera, BaseShader _shader) {
            super(buffer);
            setShader(_shader);
            setCamera(_camera);
        }

        @Override
        public void draw() {

            BaseGL.useProgram(shader);
            GLES20.glUniform4f(shader.handle[3], color.r, color.g, color.b, 1.0f);
            buffer.setTextureBuffer(texCoors[num]);
            BaseGL.activeTexture(0);
            super.draw();
        }
    }

    private class FpsRecorder extends BaseUpdateable {

        private float lowest;
        private float highest;
        private long under;

        public FpsRecorder() {

            lowest = 100;
            highest = 0;
            under = 0;
        }

        @Override
        public void update() {

            float fps = Base.render.getCurrentFPS();

            if (fps < Base.render.getRequestedFPS()) {
                under += BaseTime.delay;
            }

            if (fps < lowest) {
                lowest = fps;
            } else if (fps > highest) {
                highest = fps;
            }
        }

        @Override
        public void destroy() {

            Base.logI(
                "Fps Stats\n" +
                "Lowest: " + lowest + "\n" +
                "Highest: " + highest + "\n" +
                "Under Time: " + under + " ms\n" +
                "App Time: " + BaseTime.appTime() + " ms"
            );
        }
    }
}
