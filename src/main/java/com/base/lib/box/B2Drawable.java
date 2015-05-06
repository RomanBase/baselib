package com.base.lib.box;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.FixtureDef;

import com.base.lib.engine.Base;
import com.base.lib.engine.BaseDrawable;
import com.base.lib.engine.common.BaseDrawableData;
import com.base.lib.engine.DrawableBuffer;

/**
 * 19 Created by doctor on 15.1.14.
 */
public class B2Drawable extends BaseDrawable implements B2BodyListener {

    protected Body body;

    public B2Drawable(){
        super();
    }

    public B2Drawable(BaseDrawableData data){
        super(data);
    }

    public B2Drawable(DrawableBuffer dbuffer){
        super(dbuffer);
    }

    public void gravity(float gScale){

        body.setGravityScale(gScale);
    }

    public void fixture(FixtureDef fd){

        body.m_fixtureList = body.createFixture(fd);
    }

    public Fixture fixture(){

        return body.m_fixtureList;
    }

    public Body body(){

        return body;
    }

    public void transform(float x, float y, float angle){

        body.setTransform(new Vec2(x, y), B2.toRadians(angle));
    }

    public void transform(float x, float y){

        body.setTransform(new Vec2(x, y), body.getAngle());
    }

    public void transform(float angle){

        body.setTransform(body.getPosition(), B2.toRadians(angle));
    }

    public void impulse(final float inpX, final float inpY){

        if(body != null) {
            body.applyLinearImpulse(inpX, inpY, posX, posY);
        } else {
            Base.render.runOnBaseThread(new Runnable() {
                @Override
                public void run() {
                    impulse(inpX, inpY);
                }
            });
        }
    }

    @Override
    public void update() {

        if (body != null) {
            translate(body.getPosition());
            rotateZ(B2.toDegrees(body.getAngle()));

            super.update();
            super.setIdentityMM();
        }
    }

    @Override
    public void onCreate(Body body, BaseDrawable profile) {

        this.body = body;
        transform(posX, posY);
        rotateZ(rotZ);
        setIdentityMM();
        if(profile != null){
            profile.setIdentityMM();
        }
    }

    @Override
    public void destroy() {

        B2.world.remove(body);
        super.destroy();
    }
}
