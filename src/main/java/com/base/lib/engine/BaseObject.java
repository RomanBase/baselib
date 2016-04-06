package com.base.lib.engine;

public class BaseObject {

    protected final Base base;

    public BaseObject() {
        this.base = null;
    }

    public BaseObject(Base base) {
        this.base = base;
    }

    public Base getBase() {
        return base;
    }
}
