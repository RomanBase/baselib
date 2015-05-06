package com.base.lib.box;

import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.joints.Joint;

import com.base.lib.engine.BaseDrawable;
import com.base.lib.engine.common.Colorf;

/**
 * RunningWild Created by doctor on 3.9.13.
 */
public class B2Profile {

    public static Colorf bodyColor = new Colorf(0.75f, 0.25f, 0.0f, 1.0f);
    public static Colorf jointColor = new Colorf(0.75f, 0.75f, 0.75f, 1.0f);

    protected static boolean useProfile = false;

    public static boolean usingProfiles(){

        return useProfile;
    }

    public static BaseDrawable body(Body... bodies) {
        return body(bodyColor, bodies);
    }

    public static BaseDrawable body(Colorf color, Body... bodies) { //todo

        if (useProfile) {
            for (Body body : bodies) {
                for (Fixture f = body.getFixtureList(); f != null; f = f.getNext()) {

                    Shape shape = f.getShape();
                    switch (shape.m_type) {
                        case POLYGON:
                            return new B2PolygonProfile(body, shape, color);
                        case CIRCLE:
                            return new B2CircleProfile(body, shape, color);
                        case CHAIN:
                            return new B2ChainProfile(body, shape, color);
                        case EDGE:
                            return new B2EdgeProfile(body, shape, color);
                    }
                }
            }
        }

        return null;
    }

    public static BaseDrawable fixture(Fixture fixture) {

        Body body = fixture.getBody();
        Shape shape = fixture.getShape();

        switch (shape.m_type) {
            case POLYGON:
                return new B2PolygonProfile(body, shape, bodyColor);
            case CIRCLE:
                return new B2CircleProfile(body, shape, bodyColor);
            case CHAIN:
                return new B2ChainProfile(body, shape, bodyColor);
            case EDGE:
                return new B2EdgeProfile(body, shape, bodyColor);
        }

        return null;
    }

    public static void joint(Joint... joins) {
        joint(jointColor, joins);
    }

    public static void joint(Colorf color, Joint... joints) {

        if (useProfile) {
            for(Joint joint : joints){
                new B2JointProfile(joint, color);
            }
        }
    }
}
