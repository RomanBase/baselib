package com.base.lib.box;

import org.jbox2d.collision.shapes.EdgeShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

import com.base.lib.engine.BaseDrawable;

/**
 * 16 Created by doctor on 10.9.13.
 */
public class B2Edge implements B2BodyListener{

    private Body body;

    public B2Edge(B2World world, Vec2 from, Vec2 to){

        this(world, from, to, null);
    }

    public B2Edge(B2World world, Vec2 from, Vec2 to, Object customData){

        EdgeShape shape = new EdgeShape();
        shape.set(from, to);

        FixtureDef fd = new FixtureDef();
        fd.shape = shape;
        fd.userData = customData;

        BodyDef bd = new BodyDef();
        bd.type = BodyType.STATIC;
        bd.fixedRotation = true;
        bd.allowSleep = true;

        world.create(this, bd, fd);
    }

    public Body body(){

        return body;
    }

    @Override
    public void onCreate(Body body, BaseDrawable profile) {

        this.body = body;
    }
}
