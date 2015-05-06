package com.base.lib.box;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

/**
 * 20 Created by doctor on 15.1.14.
 */
public class B2Polygon extends B2Drawable {


    /** CCW ! min 3 - max 8(default) vecs*/
    public B2Polygon(B2World world, float posX, float posY, Vec2... vecs){

        PolygonShape shape = new PolygonShape();
        shape.set(vecs, vecs.length);

        FixtureDef fd = new FixtureDef();
        fd.shape = shape;
        fd.density = 0.3f;
        fd.friction = 0.3f;
        fd.restitution = 0.5f;

        BodyDef bd = new BodyDef();
        bd.type = BodyType.DYNAMIC;
        bd.fixedRotation = false;
        bd.allowSleep = true;
        bd.setPosition(new Vec2(posX, posY));

        world.create(this, bd, fd);
    }
}
