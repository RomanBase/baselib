package com.base.lib.box.joints;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.joints.Joint;
import org.jbox2d.dynamics.joints.PrismaticJointDef;

import com.base.lib.box.B2;
import com.base.lib.box.B2Profile;
import com.base.lib.box.B2World;

/**
 * 07 Created by doctor on 15.8.13.
 */
public class B2PrismaticJoint {

    private PrismaticJointDef jd;
    private Joint joint;

    public B2PrismaticJoint(Body bodyA, Body bodyB, Vec2 anchorA, Vec2 anchorB, boolean collideItselfs){

        jd = new PrismaticJointDef();

        jd.bodyA = bodyA;
        jd.bodyB = bodyB;
        jd.localAnchorA.set(anchorA.x, anchorA.y);
        jd.localAnchorB.set(anchorB.x, anchorB.y);
        jd.localAxisA.set(0, 1);
        jd.localAxisA.normalize();
        jd.collideConnected = collideItselfs;
    }

    public B2PrismaticJoint(B2World world, Body bodyA, Body bodyB, Vec2 anchorA, Vec2 anchorB, boolean collideItselfs, float lowerLimit, float upperLimit, float force, float speed){

        this(bodyA, bodyB, anchorA, anchorB, collideItselfs);
        translationLimit(lowerLimit, upperLimit);
        motor(force, speed);
        create(world);
    }

    public void translationLimit(float lower, float upper){

        jd.enableLimit = true;
        jd.lowerTranslation = lower;
        jd.upperTranslation = upper;
    }

    public void motor(float force, float speed){

        jd.enableMotor = true;
        jd.maxMotorForce = force;
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
