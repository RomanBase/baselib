package com.base.lib.box.base;

import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

import com.base.lib.box.B2;
import com.base.lib.box.B2BodyListener;
import com.base.lib.engine.BaseDrawable;

/**
 *
 */
public class BaseSensor extends BelBase implements B2BodyListener {

    public float posX;
    public float posY;
    public float sizeX;
    public float sizeY;
    public float rotZ;

    public void copyOf(BaseSensor src){

        this.name = src.name;
        this.posX = src.posX;
        this.posY = src.posY;
        this.sizeX = src.sizeX;
        this.sizeY = src.sizeY;
        this.rotZ = src.rotZ;
    }

    public void intoB2RectSenzor(Object userData, float offsetPosX, float offsetPosY){

        B2.POLYGON_SHAPE.setAsBox(sizeX, sizeY);

        initSenzor(B2.POLYGON_SHAPE, userData, offsetPosX, offsetPosY);
    }

    public void intoB2CircleSenzor(Object userData, float offsetPosX, float offsetPosY){

        B2.CIRCLE_SHAPE.setRadius(sizeX);

        initSenzor(B2.CIRCLE_SHAPE, userData, offsetPosX, offsetPosY);
    }

    private void initSenzor(Shape shape, Object userData, float offsetPosX, float offsetPosY){

        FixtureDef fd = B2.clearFixtureDef();
        fd.setShape(shape);
        fd.setSensor(true);
        fd.setUserData(userData);

        BodyDef bd = B2.clearBodyDef();
        bd.setType(BodyType.STATIC);
        bd.setAngle(rotZ);
        bd.setPosition(posX+offsetPosX, posY+offsetPosY);

        B2.world.create(this, bd, fd);
    }

    @Override
    public void onCreate(Body body, BaseDrawable profile) {

        b2body = body;
    }
}
