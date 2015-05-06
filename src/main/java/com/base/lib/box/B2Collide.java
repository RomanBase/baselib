package com.base.lib.box;

import org.jbox2d.dynamics.Filter;
import org.jbox2d.dynamics.contacts.Contact;

/**
 *
 */
public class B2Collide {

    public static boolean collide(Contact contact){

        Filter filterA = contact.getFixtureA().getFilterData();
        Filter filterB = contact.getFixtureB().getFilterData();

        return (filterA.maskBits & filterB.categoryBits) != 0 && (filterA.categoryBits & filterB.maskBits) != 0;
    }
}
