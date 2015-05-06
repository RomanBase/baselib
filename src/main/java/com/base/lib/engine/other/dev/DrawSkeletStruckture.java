package com.base.lib.engine.other.dev;

import android.opengl.GLES20;

import com.base.lib.engine.BaseCamera;
import com.base.lib.engine.BaseDrawable;
import com.base.lib.engine.BaseShader;
import com.base.lib.engine.DrawableBuffer;
import com.base.lib.engine.common.BaseDrawableData;
import com.base.lib.engine.BaseRenderable;
import com.base.lib.engine.BaseSkelet;
import com.base.lib.engine.Bone;
import com.base.lib.engine.DrawableAction;
import com.base.lib.engine.common.DrawableData;
import com.base.lib.engine.SkeletAnimDrawable;
import com.base.lib.engine.Texture;
import com.base.lib.engine.common.BaseMatrix;
import com.base.lib.engine.common.Colorf;
import com.base.lib.engine.common.Point3;
import com.base.lib.interfaces.BaseTouchListener;

/**
 *
 */
public class DrawSkeletStruckture extends BaseRenderable implements BaseTouchListener{

    private DBone[] bones;
    private BaseSkelet skelet;
    private SkeletAnimDrawable drawable;

    public DrawSkeletStruckture(BaseSkelet skelet, BaseDrawableData data, Texture texture){

        this.skelet = skelet;
        shader = BaseShader.get(1);

        Bone[] bone = skelet.getBones();
        int count = bone.length;
        bones = new DBone[count];

        for(int i = 0; i<count; i++){
            bones[i] = new DBone(bone[i], camera);
        }

        if(data != null){
            drawable = new SkeletAnimDrawable(new DrawableBuffer(data), new DrawableAction[]{new DrawableAction("SkeletTest", skelet, null)});
            drawable.prepareDrawable();
            if(texture != null){
                drawable.setTexture(texture);
            }
        }
    }

    @Override
    public void prepareDrawable() {

    }

    @Override
    public void update() {

        skelet.update();
        Bone[] bone = skelet.getBones();
        for(int i = 0; i<bone.length; i++){
            bones[i].update(bone[i]);
        }

        if(drawable != null){
            drawable.update();
        }
    }

    @Override
    public void draw() {

        for(DBone bone : bones){
            bone.draw();
        }

        if(drawable != null){
            if(drawable.getTexture().getGlid() != 0) {
                BaseShader.get(0).useProgram();
            }
            drawable.draw();
            BaseShader.get(1).useProgram();
        }
    }

    @Override
    public void destroy() {

        for(DBone bone : bones){
            bone.destroy();
        }

        if(drawable != null){
            drawable.destroy();
        }
    }

    @Override
    public void onTouchDown(int id, float x, float y) {

    }

    @Override
    public void onTouchUp(int id, float x, float y) {

    }

    @Override
    public void onTouchMove(int id, float x, float y) {

    }

    private class DBone extends BaseDrawable {

        private float[] temp;

        private DBone(Bone bone, BaseCamera camera){
            super();

            BaseDrawableData data = DrawableData.LINE(bone.getHead(), bone.getTail(), new Colorf(1.0f, 0.0f, 0.0f, 1.0f));
            temp = new float[6];
            buffer.init(data);
            buffer.setCoordsPerColor(4);

            shader = BaseShader.get(1);
            buffer.setGlDrawMode(GLES20.GL_LINES);

            setCamera(camera);
            prepareDrawable();
        }

        private void update(Bone bone){

            float[] matrix = bone.getTransformMatrix();
            Point3 h = bone.getHead();
            Point3 t = bone.getTail();
            float[] head = BaseMatrix.multiplyMV(matrix, 0, 0, 0);
            temp[0] = head[0];
            temp[1] = head[1];
            temp[2] = head[2];
            float[] tail = BaseMatrix.multiplyMV(matrix, t.x-h.x, t.y-h.y, t.z-h.z);
            temp[3] = tail[0];
            temp[4] = tail[1];
            temp[5] = tail[2];

            buffer.putVertices(temp);

            update();
        }
    }
}
