package com.base.lib.box.joints;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.joints.DistanceJointDef;
import org.jbox2d.dynamics.joints.Joint;

import com.base.lib.box.B2Profile;
import com.base.lib.box.B2World;

/**
 * 10 Created by doctor on 15.8.13.
 */
public class B2DistanceJoint {

    private Joint joint;

    /** damping 0 - 1 (1 stretch), frequency 0 - 5 (0 stretch)*/
    public B2DistanceJoint(B2World world, Body bodyA, Body bodyB, Vec2 anchorA, Vec2 anchorB, boolean collideItselfs, float damping, float frequency){

        DistanceJointDef jd = new DistanceJointDef();

        jd.bodyA = bodyA;
        jd.bodyB = bodyB;
        jd.localAnchorA.set(anchorA.x, anchorA.y);
        jd.localAnchorB.set(anchorB.x, anchorB.y);
        jd.collideConnected = collideItselfs;

        jd.dampingRatio = damping;
        jd.frequencyHz = frequency;

        joint = world.create(jd);

        B2Profile.joint(joint);
    }

    /** damping 0 - 1 (1 stretch), frequency 0 - 5 (0 stretch)*/
    public B2DistanceJoint(B2World world, Body bodyA, Body bodyB, float damping, float frequency){

        DistanceJointDef jd = new DistanceJointDef();

        jd.bodyA = bodyA;
        jd.bodyB = bodyB;
        jd.localAnchorA.set(0, 0);
        jd.localAnchorB.set(0, 0);
        jd.collideConnected = false;

        jd.dampingRatio = damping;
        jd.frequencyHz = frequency;

        joint = world.create(jd);

        B2Profile.joint(joint);
    }

    public Joint getJoint(){

        return joint;
    }


}
