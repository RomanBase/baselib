package com.base.lib.box;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

import com.base.lib.engine.DrawableBuffer;
import com.base.lib.engine.common.DrawableData;
import com.base.lib.engine.common.other.Point2;

/**
 * 15 Created by doctor on 12.8.13.
 */
public class B2Rectangle extends B2Drawable {

    public B2Rectangle(B2World world, float width, float height){
        this(world, width, height, BodyType.DYNAMIC, true);
    }

    public B2Rectangle(B2World world, float width, float height, BodyType type, boolean rotation){
        this(world, width, height, type, rotation, 0.3f, 0.3f, 0.5f);
    }

    public B2Rectangle(B2World world, float width, float height, BodyType type, boolean rotation, float density, float friction, float restitution){
        this(world, null, 0, 0, width, height, type, rotation, density, friction, restitution, null);
    }

    public B2Rectangle(B2World world, DrawableBuffer buffer, float posX, float posY, float width, float height, BodyType type, boolean rotation, float density, float friction, float restitution, Object userData){
        super(buffer == null ? new DrawableBuffer(DrawableData.RECTANGLE(width, height)) : buffer);
        translate(posX, posY);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width/2, height/2);

        FixtureDef fd = new FixtureDef();
        fd.shape = shape;
        fd.density = density;
        fd.friction = friction;
        fd.restitution = restitution;
        fd.userData = userData;

        BodyDef bd = new BodyDef();
        bd.type = type;
        bd.fixedRotation = !rotation;
        bd.allowSleep = true;
        bd.setPosition(new Vec2(posX, posY));

        world.create(this, bd, fd);
    }

    public B2Rectangle(B2World world, float posX, float posY, float width, float height){
        this(world, null, posX, posY, width, height, BodyType.STATIC, false, 0.3f, 0.3f, 0.5f, null);
    }

    public B2Rectangle(B2World world, BodyDef bd, FixtureDef fd, PolygonShape ps){
        super(DrawableData.RECTANGLE(Point2.length(ps.m_vertices[0].x, ps.m_vertices[2].x)*B2.ratioX, Point2.length(ps.m_vertices[0].y, ps.m_vertices[2].y)*B2.ratioY));
        translate(bd.getPosition());

        world.create(this, bd, fd);
    }

}
