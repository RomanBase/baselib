package com.base.lib.box.base;

import org.jbox2d.dynamics.Body;

import com.base.lib.box.B2;

/**
 *
 */
public class BelBase {

    public String name;
    protected Body b2body;

    public void removeB2Body(){

        if(b2body != null) {
            B2.world.remove(b2body);
        }
    }
}
