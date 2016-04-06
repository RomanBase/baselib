package com.base.lib.box;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

import java.util.ArrayList;
import java.util.List;

import com.base.lib.box.joints.B2RevoluteJoint;
import com.base.lib.engine.BaseDrawable;
import com.base.lib.engine.BaseRenderable;
import com.base.lib.engine.common.other.Point2;

/**
 * 10 Created by doctor on 15.8.13.
 */
public class B2Blob extends BaseRenderable {

    private List<B2Circle> drawList = new ArrayList<B2Circle>();

    public B2Blob(B2World world, float x, float y){

        Vec2 pos = new Vec2(x, y);

        CircleShape cs = new CircleShape();
        cs.m_radius = 0.1f;

        FixtureDef fd = new FixtureDef();
        fd.shape = cs;
        fd.density = 0.3f;
        fd.friction = 0.6f;
        fd.restitution = 0.5f;

        BodyDef bd = new BodyDef();
        bd.type = BodyType.DYNAMIC;
        bd.fixedRotation = true;
        bd.allowSleep = true;
        bd.setBullet(true);
        bd.setPosition(pos);

        drawList.add(new B2Circle(world, bd, fd));

        int hWidth = 2;
        final int count = 20;
        final float step = 360/count+1;
        float angle = 90;
        for(int i = 0; i<count; i++){

            bd.setPosition(B2.point2Vec(Point2.circlePoint(pos.x, pos.y, angle, hWidth)));
            drawList.add(new B2Circle(world, bd, fd));
            angle += step;

            world.createBodies();
            Body mid = drawList.get(0).body();
            Body curr = drawList.get(i+1).body();

            //new B2DistanceJoint(world, mid, curr, mid.getPosition(), curr.getPosition(), true, 1, 0);
            new B2RevoluteJoint(mid, curr, mid.getPosition(), false).create(world);
            if(i != 0){
                Body last = drawList.get(i).body();
                //new B2DistanceJoint(world, curr, last, curr.getPosition(), last.getPosition(), true, 1, 0);
                new B2RevoluteJoint(last, curr, last.getPosition(), false).create(world);
            }
        }

        Body first = drawList.get(1).body();
        Body last = drawList.get(drawList.size()-1).body();

        new B2RevoluteJoint(first, last, first.getPosition(), false).create(world);
    }

    public B2Circle getMiddle(){

        return drawList.get(0);
    }

    @Override
    public void update() {

        for(B2Circle drawable : drawList){
            drawable.update();
        }
    }

    @Override
    public void draw() {

        for(BaseDrawable drawable : drawList){
            drawable.draw();
        }
    }

    @Override
    public void destroy() {

    }
}
