package com.base.lib.engine.other.dev;


import com.base.lib.R;
import com.base.lib.engine.Base;
import com.base.lib.engine.BaseCamera;
import com.base.lib.engine.BaseDrawable;
import com.base.lib.engine.BaseShader;
import com.base.lib.engine.DrawableBuffer;
import com.base.lib.engine.common.BaseDrawableData;
import com.base.lib.engine.common.BeoParser;
import com.base.lib.interfaces.BaseTouchListener;

/**
 * 08 Created by doctor on 3.12.13.
 */
public class TouchMarker implements BaseTouchListener {

    private final DrawableBuffer markBuffer;
    private TouchMarkDrawable[] touches;
    private BaseCamera scamera;

    public TouchMarker(){

        this.scamera = BaseCamera.ortho(2.0f);
        touches = new TouchMarkDrawable[255];
        BaseDrawableData bufferData = new BeoParser(R.drawable.touchmark).getBaseDrawableData();
        bufferData.setSizeX(0.225f, true);
        markBuffer = new DrawableBuffer(bufferData);
    }

    @Override
    public void onTouchDown(int id, float x, float y) {

        if(Base.debug)touches[id] = new TouchMarkDrawable(x*scamera.getRatioX(), y*scamera.getRatioY());
    }

    @Override
    public void onTouchUp(int id, float x, float y) {

        if(touches[id] != null)touches[id].unUse();
    }

    @Override
    public void onTouchMove(int id, float x, float y) {

        if(touches[id] != null){
            touches[id].posX = x*scamera.getRatioX();
            touches[id].posY = y*scamera.getRatioY();
        }
    }

    private class TouchMarkDrawable extends BaseDrawable {

        protected float posX;
        protected float posY;

        private TouchMarkDrawable(float x, float y){
            super();
            setShader(BaseShader.get(1));
            translate(x, y);
            posX = x;
            posY = y;
            setBuffer(markBuffer);
            setCamera(scamera);
            prepareDrawable();
            setIdentityMM();
            Base.render.addDrawable(this);
        }

        @Override
        public void update() {

            translate(posX, posY);
            super.update();
            super.setIdentityMM();
        }
    }
}
