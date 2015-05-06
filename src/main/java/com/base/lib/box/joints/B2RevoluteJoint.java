package com.base.lib.box.joints;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.joints.Joint;
import org.jbox2d.dynamics.joints.RevoluteJointDef;

import com.base.lib.box.B2;
import com.base.lib.box.B2Profile;
import com.base.lib.box.B2World;

/**
 * 09 Created by doctor on 14.8.13.
 */
public class B2RevoluteJoint {

    private RevoluteJointDef jd;
    private Joint joint;

    public B2RevoluteJoint(Body bodyA, Body bodyB, Vec2 anchor, boolean collideItselfs){

        jd = new RevoluteJointDef();

        jd.bodyA = bodyA;
        jd.bodyB = bodyB;
        jd.localAnchorA.set(anchor.x, anchor.y);
        jd.localAnchorB.set(anchor.x, anchor.y);
        jd.collideConnected = collideItselfs;
        jd.referenceAngle = 0;
    }

    public B2RevoluteJoint(B2World world, Body bodyA, Body bodyB, Vec2 anchor, boolean collideItselfs, float minDegrees, float maxDegrees, float torgue, float speed){

        this(bodyA, bodyB, anchor, collideItselfs);
        angleLimit(minDegrees, maxDegrees);
        motor(torgue, speed);
        create(world);
    }

    public void angleLimit(float minDegrees, float maxDegrees){

        jd.enableLimit = true;
        jd.lowerAngle = B2.toRadians(minDegrees);
        jd.upperAngle = B2.toRadians(maxDegrees);
    }

    public void motor(float torgue, float speed){

        jd.enableMotor = true;
        jd.maxMotorTorque = torgue;
        jd.motorSpeed = B2.toRadians(speed);
    }

    public Joint create(B2World world){

        joint = world.create(jd);
        B2Profile.joint(joint);

        return joint;
    }

    public Joint joint(){

        return joint;
    }

    public Body[] getBodies(){

        return new Body[]{jd.bodyA, jd.bodyB};
    }
}
