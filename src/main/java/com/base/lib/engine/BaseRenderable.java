package com.base.lib.engine;

/**
 * abstract class for Renderable objects
 * holds basic informations about renderable object
 * */
public abstract class BaseRenderable extends BaseUpdateable {

    /** shader program */
    protected BaseShader shader = BaseGL.baseShader;
    /** reference to camera */
    protected BaseCamera camera;

    public BaseRenderable() {
    }

    public BaseRenderable(Base base) {
        super(base);

        camera = base.camera;
    }

    /** draw method */
    public abstract void draw();

    @Deprecated
    /** used by previous version */
    public void prepareDrawable(){}

    /** @return shader program */
    public BaseShader getShader(){

        return shader;
    }

    /** sets shader program */
    public void setShader(BaseShader shader){

        this.shader = shader;
    }

    /** @return camera reference */
    public BaseCamera getCamera() {

        return camera;
    }

    /** sets camera instance */
    public void setCamera(BaseCamera camera) {

        this.camera = camera;
    }

    @Override
    /** puts drawable object into renderer */
    public void use() {

        inUse = true;
        base.render.addDrawable(this);
    }

    @Override
    /** @return weak reference of renderable object */
    public BaseRenderable reference(){

        return this;
    }

}