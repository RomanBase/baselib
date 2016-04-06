package com.base.lib.interfaces;

public interface GLPoolRunnable<T> {

    T run();

    void glRun(T result);
}
