package com.base.lib.box;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

import com.base.lib.engine.BaseDrawable;

/**
 * 16 Created by doctor on 10.9.13.
 */
public class B2Chain implements B2BodyListener {

    private Body b2body;

    public B2Chain(B2World world, Object userData, Vec2... verts){

        B2.CHAIN_SHAPE.createChain(verts, verts.length);

        B2.clearFixtureDef();
        FixtureDef fd = B2.FIXTURE_DEF;
        fd.shape = B2.CHAIN_SHAPE;
        fd.density = 1.0f;
        fd.friction = 1.0f;
        fd.restitution = 0.0f;
        fd.userData = userData;

        B2.clearBodyDef();
        BodyDef bd = B2.BODY_DEF;
        bd.type = BodyType.STATIC;
        bd.fixedRotation = true;
        bd.allowSleep = true;

        world.create(this, bd, fd);
    }
    
    public static Body create(Object userData, float offsetX, float offsetY, Vec2... verts){

        B2.CHAIN_SHAPE.createChain(verts, verts.length);

        B2.clearFixtureDef();
        FixtureDef fd = B2.FIXTURE_DEF;
        fd.shape = B2.CHAIN_SHAPE;
        fd.density = 1.0f;
        fd.friction = 1.0f;
        fd.restitution = 0.0f;
        fd.userData = userData;

        B2.clearBodyDef();
        BodyDef bd = B2.BODY_DEF;
        bd.type = BodyType.STATIC;
        bd.fixedRotation = true;
        bd.allowSleep = true;
        bd.setPosition(offsetX, offsetY);

        return B2.b2World.createBody(bd).createFixture(fd).m_body;
    }

    @Override
    public void onCreate(Body body, BaseDrawable profile) {

        b2body = body;
    }

    public void removeBody(){

        B2.world.remove(b2body);
    }
}
