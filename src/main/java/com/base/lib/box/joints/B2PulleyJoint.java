package com.base.lib.box.joints;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.joints.Joint;
import org.jbox2d.dynamics.joints.PulleyJointDef;

import com.base.lib.box.B2World;

/**
 * 17 Created by doctor on 17.1.14.
 */
public class B2PulleyJoint {

    public B2PulleyJoint(B2World world, Body bodyA, Body bodyB, Vec2 groundA, Vec2 groundB, Vec2 anchorA, Vec2 anchorB, float lengthA, float lengthB, float ratio, boolean collide){

        PulleyJointDef jd = new PulleyJointDef();

        jd.bodyA = bodyA;
        jd.bodyB = bodyB;
        jd.groundAnchorA = groundA;
        jd.groundAnchorB = groundB;
        jd.localAnchorA = anchorA;
        jd.localAnchorB = anchorB;
        jd.lengthA = lengthA;
        jd.lengthB = lengthB;
        jd.ratio = ratio;
        jd.collideConnected = collide;

        Joint joint = world.create(jd);
    }
}
