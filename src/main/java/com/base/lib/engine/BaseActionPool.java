package com.base.lib.engine;

import com.base.lib.interfaces.GLPoolRunnable;
import com.base.lib.engine.common.gl.EGLHolder;
import com.base.lib.engine.common.other.TrainedMonkey;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class BaseActionPool extends Thread {

    private List<Runnable> actions;
    private List<GLPoolRunnable> glActions;

    private EGLHolder egl;

    private boolean running;
    private boolean eglInitialized;

    private final Object glLock = new Object();

    private BaseRenderer render;

    public BaseActionPool() {
        setName("BaseActionPool");

        actions = new ArrayList<Runnable>(64);
        glActions = new ArrayList<GLPoolRunnable>(64);
    }

    public void initEGL(EGLHolder egl, BaseRenderer render) {

        this.render = render;
        this.egl = egl;
        eglInitialized = true;
        running = true;
        start();
    }

    public void addTask(Runnable action) {

        actions.add(action);

        if (eglInitialized) {
            TrainedMonkey.notify(this);
        }
    }

    public void addGLTask(GLPoolRunnable action) {

        synchronized (glLock) {
            glActions.add(action);

            if (eglInitialized) {
                TrainedMonkey.notify(this);
            }
        }
    }

    public void kill() {

        eglInitialized = false;
        running = false;
        interrupt();
        actions.clear();
        glActions.clear();
    }

    @Override
    public void run() {

        /*while (!eglInitialized) {
            TrainedMonkey.sleep(this, 30);
            eglInitialized = egl != null && egl.surface != null;
        }

        boolean eglBinded = egl.egl.eglMakeCurrent(egl.display, egl.surface, egl.surface, egl.context);
        if (eglBinded) {
            Base.logV("Separate EGL maked");
        } else {
            Base.logE("Separate EGL failed!");
        }*/

        while (running) {

            if (!actions.isEmpty()) {
                for (Runnable action : actions) {
                    if (action != null) {
                        action.run();
                    }
                }

                actions.clear();
            }

            if (!glActions.isEmpty()) {
                synchronized (glLock) {
                    for (final GLPoolRunnable action : glActions) {
                        if (action != null) {
                            final Object result = action.run();
                            render.glQueueEvent(new Runnable() {
                                @Override
                                public void run() {
                                    //noinspection unchecked
                                    action.glRun(result);
                                }
                            });
                        }
                    }

                    glActions.clear();
                }
            }

            TrainedMonkey.wait(this);
        }
    }
}
