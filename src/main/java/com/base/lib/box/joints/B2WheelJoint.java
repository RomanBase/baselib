package com.base.lib.box.joints;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.joints.Joint;
import org.jbox2d.dynamics.joints.WheelJointDef;

import com.base.lib.box.B2Profile;
import com.base.lib.box.B2World;

/**
 * 17 Created by doctor on 17.1.14.
 */
public class B2WheelJoint {

    private WheelJointDef jd;
    private Joint joint;

    public B2WheelJoint(Body bodyA, Body bodyB){
        this(bodyA, bodyB, bodyA.getPosition(), bodyB.getPosition(), false);
    }

    public B2WheelJoint(Body bodyA, Body bodyB, Vec2 anchorA, Vec2 anchorB, boolean collideItselfs){

        jd = new WheelJointDef();

        jd.bodyA = bodyA;
        jd.bodyB = bodyB;
        jd.localAnchorA.set(anchorA);
        jd.localAnchorB.set(anchorB);
        jd.localAxisA.set(0, 0);
    }

    public void motor(float torgue, float speed){

        jd.enableMotor = true;
        jd.maxMotorTorque = torgue;
        jd.motorSpeed = speed;
    }

    /** damping 1 = critical, frequency 0 = stratch */
    public void suspension(float damping, float frequency){

        jd.dampingRatio = damping;
        jd.frequencyHz = frequency;
    }

    public Joint create(B2World world){

        joint = world.create(jd);
        B2Profile.joint(joint);
        return joint;
    }

    public Joint joint(){

        return joint;
    }
}

