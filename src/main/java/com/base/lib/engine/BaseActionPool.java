package com.base.lib.engine;

import com.base.lib.engine.common.TrainedMonkey;
import com.base.lib.interfaces.ActivityStateListener;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;

/**
 *
 */
public class BaseActionPool extends Thread implements ActivityStateListener{

    private static BaseActionPool instance;

    private static List<Runnable> runnables;
    private static List<Runnable> glActions;

    private static EGL10 egl;
    private static EGLContext glContext;
    private static EGLSurface glSurface;
    private static EGLDisplay display;
    private static EGLSurface localSurface;

    private static boolean running;

    private BaseActionPool(){

        Base.activity.addActivityStateListener(this);
        runnables = new ArrayList<Runnable>(8);
        glActions = new ArrayList<Runnable>(8);

        running = true;
        start();
    }

    public static void initGLContext(EGL10 egl10, EGLContext renderContext, EGLDisplay display, EGLConfig eglConfig){

        egl = egl10;
        glContext = egl.eglCreateContext(display, eglConfig, renderContext, null);
        int pbufferAttribs[] = { EGL10.EGL_WIDTH, 1, EGL10.EGL_HEIGHT, 1, EGL10.EGL_NONE };

        localSurface = egl.eglCreatePbufferSurface(display, eglConfig, pbufferAttribs);
        BaseGL.glError("BaseActionPool");
    }

    public static void addTask(Runnable action){

        if(instance == null){
            instance = new BaseActionPool();
        }

        runnables.add(action);
        TrainedMonkey.notify(instance);
    }

    public static void addGLTask(Runnable action){

        if(instance == null){
            instance = new BaseActionPool();
        }

        glActions.add(action);
        TrainedMonkey.notify(instance);
    }


    @Override
    public void run() {

        while (running){

            if(!runnables.isEmpty()) {
                for (Runnable action : runnables) {
                    if (action != null) {
                        action.run();
                    }
                }

                runnables.clear();
            }

            if(!glActions.isEmpty() && egl != null) {

                egl.eglMakeCurrent(display, localSurface, localSurface, glContext);
                for (Runnable action : glActions) {
                    if (action != null) {
                        action.run();
                    }
                }

                glActions.clear();
            }

            TrainedMonkey.wait(this);
        }
    }

    @Override
    public void onPause() {

        if(instance != null){
            running = false;
            TrainedMonkey.notify(instance);
            instance.interrupt();
            instance = null;
        }
    }

    @Override
    public void onResume() {

        running = false;
        instance = null;
    }

    //just overriding ASL and not using deprecated super of Thread class
    @Override
    public void destroy() {

        onPause();
    }
}
