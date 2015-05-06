package com.base.lib.box;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

import com.base.lib.engine.common.DrawableData;

/**
 * 12 Created by doctor on 12.8.13.
 */
public class B2Circle extends B2Drawable {

    public B2Circle(B2World world, float radius){
        this(world, radius, BodyType.DYNAMIC, true);
    }

    public B2Circle(B2World world, float radius, float posX, float posY){
        this(world, radius, posX, posY, BodyType.DYNAMIC, true, 0.3f, 0.3f, 0.5f, null);
    }

    public B2Circle(B2World world, float radius, BodyType type, boolean rotation){
        this(world, radius, type, rotation, 0.3f, 0.3f, 0.5f, null);
    }

    public B2Circle(B2World world, float radius, BodyType type, boolean rotation, float density, float friction, float restitution, Object someFixtureData){
        this(world, 0, 0, radius, type, rotation, density, friction, restitution, someFixtureData);
    }

    public B2Circle(B2World world, float radius, float posX, float posY, BodyType type, boolean rotation, float density, float friction, float restitution, Object someFixtureData){
        super(DrawableData.CIRCLE(32, radius));
        translate(posX, posY);

        CircleShape shape = new CircleShape();
        shape.m_radius = radius;

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = density;
        fixtureDef.friction = friction;
        fixtureDef.restitution = restitution;
        fixtureDef.setUserData(someFixtureData);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = type;
        bodyDef.fixedRotation = !rotation;
        bodyDef.allowSleep = true;
        bodyDef.setPosition(new Vec2(posX, posY));

        world.create(this, bodyDef, fixtureDef);
    }

    public B2Circle(B2World world, BodyDef bd, FixtureDef fd){
        super(DrawableData.CIRCLE(32, fd.getShape().getRadius()));
        translate(bd.getPosition());

        world.create(this, bd, fd);
    }

    public B2Circle(Body body){
        super(DrawableData.CIRCLE(32, body.getFixtureList().getShape().getRadius()));
        translate(body.getPosition());

        onCreate(body, null);
    }
}
