package com.base.lib.box;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Filter;
import org.jbox2d.dynamics.FixtureDef;

/**
 * 10 Created by doctor on 17.1.14.
 */
public class B2Def {

    public static FixtureDef fixtureDef(Shape shape, float density, float friction, float restitution, int categoryBits, int maskBits, Object userData){

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = density;
        fixtureDef.friction = friction;
        fixtureDef.restitution = restitution;
        fixtureDef.userData = userData;

        if(categoryBits != 0 || maskBits != 0) {
            Filter filter = new Filter();
            filter.categoryBits = categoryBits;
            filter.maskBits = maskBits;

            fixtureDef.filter = filter;
        }

        return fixtureDef;
    }

    public static FixtureDef fixtureDef(Shape shape, float density, float friction, float restitution, Object userData){

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = density;
        fixtureDef.friction = friction;
        fixtureDef.restitution = restitution;
        fixtureDef.userData = userData;

        return fixtureDef;
    }

    public static BodyDef bodyDef(BodyType type, boolean rotation, float posX, float posY, Object userData){

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = type;
        bodyDef.fixedRotation = !rotation;
        bodyDef.allowSleep = true;
        bodyDef.setPosition(new Vec2(posX, posY));
        bodyDef.setUserData(userData);

        return bodyDef;
    }

    public static PolygonShape rectShape(float semiWidth, float semiHeight, float centerX, float centerY, float angle){

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(semiWidth, semiHeight, new Vec2(centerX, centerY), angle);

        return shape;
    }
}
