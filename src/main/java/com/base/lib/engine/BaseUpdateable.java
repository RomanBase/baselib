package com.base.lib.engine;

/**
 * abstract class for Updatable objects
 */
public abstract class BaseUpdateable {

    /** every cycle is checked if object is still in use. if not, object is destroyed */
    public boolean inUse = true;

    /** update method */
    public abstract void update();

    /** destroy method */
    public abstract void destroy();

    /** tells renderer to destroy this object in next update cycle */
    public void unUse() {
        inUse = false;
    }

    /** puts object into renderers updateables */
    public void use(){

        inUse = true;
        Base.render.addUpdateable(this);
    }

    /** @return weak reference of this object */
    public BaseUpdateable reference(){

        return this;
    }
}
