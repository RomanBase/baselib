package com.base.lib.engine.other.dev;

import android.opengl.GLES20;

import com.base.lib.R;
import com.base.lib.engine.Base;
import com.base.lib.engine.BaseGL;
import com.base.lib.engine.BaseRenderable;
import com.base.lib.engine.BaseShader;
import com.base.lib.engine.BaseTexture;
import com.base.lib.engine.BaseUpdateable;
import com.base.lib.engine.Type;
import com.base.lib.engine.builders.CameraBuilder;
import com.base.lib.engine.common.BaseMatrix;
import com.base.lib.engine.common.Buffers;
import com.base.lib.engine.common.Colorf;
import com.base.lib.engine.common.DrawableData;
import com.base.lib.engine.common.TextureInfo;
import com.base.lib.engine.common.gl.BaseGLBuffer;
import com.base.lib.engine.common.other.TrainedMonkey;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class FpsBar extends BaseRenderable {

    private final Num[] nums;
    private final FloatBuffer[] texCoors;
    private final FloatBuffer verts;
    private final ShortBuffer faces;
    private final BaseTexture texture;

    private Colorf color;
    private float requestedFps;
    private float goodFps;
    private float badFps;

    public FpsBar(final Base base) {
        super(base);
        nums = new Num[6];

        camera = new CameraBuilder(base).setDimensions(2).setSize(base.screen.width > base.screen.height ? 10.0f : 10.0f * base.screen.ratio).build();
        shader = base.factory.getShader(BaseShader.TEXTURE_COLOR);

        color = new Colorf();
        requestedFps = (float) base.render.getRequestedFPS();
        goodFps = requestedFps - 6.0f;
        badFps = goodFps - 6.0f;

        float w = 0.175f;
        float x = -camera.getSemiWidth() + w * 1.125f;
        float y = camera.getSemiHeight() - w * 1.125f;

        texCoors = new FloatBuffer[12];
        float[][] coords = TextureInfo.rectangleTextureCoords(TextureInfo.sprite(512, 512, 4, 4));
        for (int i = 0; i < texCoors.length; i++) {
            texCoors[i] = Buffers.floatBuffer(coords[i]);
        }

        texture = base.factory.getTexture(R.drawable.nums, Type.STORAGE_RESOURCE);
        verts = Buffers.floatBuffer(DrawableData.rectangleVertices(w*0.5f, w*0.5f));
        faces = Buffers.shortBuffer(DrawableData.rectangleFaces());

        for (int i = 0; i < 6; i++) {
            nums[i] = new Num();
        }

        nums[5].num = 11;
        nums[2].num = 10;
        nums[2].scale(0.35f);

        w *= 0.75f;
        nums[0].translate(x, y, 0);
        nums[1].translate(x += w, y, 0);
        nums[2].translate(x += w * 0.435f, y - w * 0.5f + w * 0.175f, 0);
        nums[3].translate(x += w * 0.55f, y, 0);
        nums[4].translate(x += w, y, 0);
        nums[5].translate(x += w, y, 0);

        TrainedMonkey.handle(new Runnable() {
            @Override
            public void run() {
                if (base.render != null) {
                    base.render.addUpdateable(new FpsRecorder(base));
                }
            }
        }, 3000);
    }

    public void update() {

        float fps = base.render.getCurrentFPS();

        String sfps = String.format("%.2f", fps);

        int len = sfps.length();
        if (len < 5) {
            sfps = "0" + sfps;
        } else if (len > 5) {
            sfps = "99.99";
        }

        nums[0].num = Character.getNumericValue(sfps.charAt(0));
        nums[1].num = Character.getNumericValue(sfps.charAt(1));
        nums[3].num = Character.getNumericValue(sfps.charAt(3));
        nums[4].num = Character.getNumericValue(sfps.charAt(4));

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

        BaseGL.bindTexture(texture.glid);
        BaseGLBuffer.glPutArray(verts, shader.handle[1], 2);
        GLES20.glUniform4f(shader.handle[3], color.r, color.g, color.b, color.a);
        for (Num num : nums) {
            BaseGLBuffer.glPutArray(texCoors[num.num], shader.handle[2], 2);

            float[] matrix = BaseMatrix.setSMIdentity();
            BaseMatrix.translate(matrix, num.x, num.y, 0.0f);
            if(num.scale != 1.0f) {
                BaseMatrix.scale(matrix, num.scale);
            }
            BaseMatrix.multiplyMC(matrix, camera);

            GLES20.glUniformMatrix4fv(shader.handle[0], 1, false, matrix, 0);
            BaseGLBuffer.glDrawElements(faces);
        }
        BaseGLBuffer.glDisableAttribArray(shader.handle[1], shader.handle[2], shader.handle[3]);
    }

    @Override
    public void destroy() {

    }

    private class Num {

        int num;
        float x;
        float y;
        float scale = 1.0f;

        void translate(float x, float y, float z) {
            this.x = x;
            this.y = y;
        }

        void scale(float scale) {
            this.scale = scale;
        }
    }

    private class FpsRecorder extends BaseUpdateable {

        private float lowest;
        private float highest;
        private long under;

        public FpsRecorder(Base base) {
            super(base);

            lowest = 100;
            highest = 0;
            under = 0;
        }

        @Override
        public void update() {

            float fps = base.render.getCurrentFPS();

            if (fps < base.render.getRequestedFPS()) {
                under += base.time.delay;
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
                            "App Time: " + base.time.appTime() + " ms"
            );
        }
    }
}
