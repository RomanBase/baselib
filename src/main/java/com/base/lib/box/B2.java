package com.base.lib.box;

import org.jbox2d.collision.shapes.ChainShape;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.Filter;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;

import com.base.lib.engine.Base;
import com.base.lib.engine.common.Point2;

/**
 * 14 Created by doctor on 12.8.13.
 *
 * By default B2 screen using camera dimensions
 * Can change this values by static way, but don't forgot that Box2D using meters unit.
 */
public class B2 {

    public static B2World world;
    public static World b2World;
    public static float width;
    public static float height;
    public static float ratio;

    public static float ratioX;
    public static float ratioY;

    public static float b2ratioX;
    public static float b2ratioY;

    public static final Filter FILTER = new Filter();
    public static final BodyDef BODY_DEF = new BodyDef();
    public static final FixtureDef FIXTURE_DEF = new FixtureDef();
    public static final PolygonShape POLYGON_SHAPE = new PolygonShape();
    public static final CircleShape CIRCLE_SHAPE = new CircleShape();
    public static final ChainShape CHAIN_SHAPE = new ChainShape();

    private static final float PI = (float)Math.PI;

    public static BodyDef clearBodyDef(){

        BODY_DEF.angle = 0.0f;
        BODY_DEF.position.x = 0.0f;
        BODY_DEF.position.y = 0.0f;
        BODY_DEF.userData = null;
        BODY_DEF.bullet = false;
        BODY_DEF.fixedRotation = false;
        BODY_DEF.allowSleep = true;
        BODY_DEF.type = BodyType.DYNAMIC;

        return BODY_DEF;
    }

    public static FixtureDef clearFixtureDef(){

        FIXTURE_DEF.density = 0.0f;
        FIXTURE_DEF.restitution = 0.0f;
        FIXTURE_DEF.friction = 0.2f;
        FIXTURE_DEF.isSensor = false;
        FIXTURE_DEF.filter = FILTER;
        FIXTURE_DEF.shape = null;
        FIXTURE_DEF.userData = null;

        return FIXTURE_DEF;
    }

    public static Filter clearFilter(){

        FILTER.categoryBits = 0x0001;
        FILTER.maskBits = 0xFFFF;
        FILTER.groupIndex = 0;

        return FILTER;
    }

    public static void calcRatio(){

        if(width + height == 0){
            width = Base.camera.getSemiWidth()*2.0f;
            height = Base.camera.getSemiHeight()*2.0f;
        }

        ratio = width/height;
        ratioX = Base.screenWidth/width;
        ratioY = Base.screenHeight/height;
        b2ratioX = width/Base.screenWidth;
        b2ratioY = height/Base.screenHeight;
    }

    public static float pixelsToMetersX(float pixels){

        return pixels/ratioX;
    }

    public static float pixelsToMetersY(float pixels){

        return pixels/ratioY;
    }

    public static float metersToPixelsX(float meters){

        return meters*ratioX;
    }

    public static float metersToPixelsY(float meters){

        return meters*ratioX;
    }

    public static Vec2 pixelsToMeters(float x, float y){

        return new Vec2(x * b2ratioX, y * b2ratioY);
    }

    public static Vec2 metersToPixels(Vec2 mVec){

        return new Vec2(mVec.x * ratioX, mVec.y * ratioY);
    }

    public static Vec2 pixelsToMeters(Point2 pVec){

        return new Vec2(pVec.x * b2ratioX, pVec.y * b2ratioY);
    }

    public static Vec2 point2Vec(Point2 point){

        return new Vec2(point.x, point.y);
    }

    public static Point2 vec2Point(Vec2 vec){

        return new Point2(vec.x, vec.y);
    }

    public static Point2 metersVec2pixelPoint(Vec2 vec){

        return new Point2(vec.x * ratioX, vec.y * ratioY);
    }

    public static Vec2 pixelsPoint2metersVec(Point2 point){

        return new Vec2(point.x * b2ratioX, point.y * b2ratioY);
    }

    public static float toRadians(float degrees){

        return degrees * PI / 180.0f;
    }

    public static float toDegrees(float radians){

        return radians * 180.0f / PI ;
    }

    public static Vec2[] createB2data(float[] vertices){

        return createB2data(vertices, 0, vertices.length);
    }

    public static Vec2[] createB2data(float[] vertices, int ofset, int length){

        if(vertices.length % 2 != 0) throw new RuntimeException("Monkeys can't create B2 data - vertices.length must be even !");

        Vec2[] out = new Vec2[length/2];

        int index = 0;
        for(int i = ofset; i<length; i++){
            out[index++] = new Vec2(vertices[i++], vertices[i]);
        }

        return out;
    }

}
