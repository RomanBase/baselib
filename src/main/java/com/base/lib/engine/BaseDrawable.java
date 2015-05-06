package com.base.lib.engine;

import com.base.lib.engine.common.BaseDrawableData;

/**
 * 12 Created by doctor on 30.10.13.
 */
public class BaseDrawable extends DrawableModel {

    protected DrawableBuffer buffer;

    public BaseDrawable(){
        super();

        buffer = new DrawableBuffer();
    }

    public BaseDrawable(BaseDrawableData data){
        this(new DrawableBuffer(data));
    }

    public BaseDrawable(DrawableBuffer dbuffer){
        super();

        init(dbuffer);
    }

    protected void init(BaseDrawableData data){

        init(new DrawableBuffer(data));
    }

    protected void init(DrawableBuffer dbuffer){

        buffer = dbuffer;
        shader = dbuffer.shader;
        sizeX = dbuffer.getSizeX();
        sizeY = dbuffer.getSizeY();
        sizeZ = dbuffer.getSizeZ();
    }

    public void setShader(BaseShader shader){ //todo this is bad.,

        buffer.shader = shader;
        this.shader = shader;
    }

    public void setBuffer(DrawableBuffer buffer){

        this.buffer = buffer;
    }

    public DrawableBuffer getBuffer(){

        return buffer;
    }

    public BaseDrawable asVBO(){

        buffer = buffer.asVBO();
        return this;
    }

    public BaseDrawable asVBO(Type type){

        buffer = buffer.asVBO(type);
        return this;
    }

    @Override
    public void draw(){

        buffer.draw(this);
    }

    public void drawWithProgram(){

        buffer.glUseProgram();
        buffer.draw(this);
    }

    public void drawModel(){

        buffer.glPutTextureBuffer();
        buffer.glPutVerticeBuffer();
        buffer.glPutMVPMatrix(MVPMatrix);
        buffer.glPutDraw();
        buffer.glDisableAttribArray();
    }

}
