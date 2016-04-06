package com.base.lib.engine;

import android.annotation.SuppressLint;
import android.opengl.GLSurfaceView;
import android.os.SystemClock;
import android.view.MotionEvent;

import com.base.lib.engine.builders.RenderConfig;
import com.base.lib.engine.common.other.TrainedMonkey;
import com.base.lib.interfaces.ActivityStateListener;
import com.base.lib.interfaces.BaseTouchListener;


@SuppressLint("ViewConstructor")
/**
 * extends GLSurfaceView and holds Renderer.
 * <p>This View dealing with user touch imputs and gestures<p/>
 * <p>Mainly includes working thread, this thead updates renderer and requesting screen redraw</p>
 * */
public class BaseGLView extends GLSurfaceView implements ActivityStateListener {

    private BaseThread baseThread;
    private final BaseRenderer renderer;
    private final Base base;
    private float touchModifierX;
    private float touchModifierY;
    private BaseTouchListener touchListener;
    int renderDelay;

    /**
     * initialize GL context (OpenGL ES 2.0) and sets renderer
     */
    public BaseGLView(RenderConfig config) {
        super(config.base.context);

        // Create an OpenGL ES 2.0 context.
        setEGLContextClientVersion(2);

        //setPreserveEGLContextOnPause(true);

        EGLConfigChooser configChooser = config.getEglConfigChooser();
        if (configChooser != null) {
            setEGLConfigChooser(configChooser);
        }

        EGLContextFactory contextFactory = config.getEglContextFactory();
        if (contextFactory != null) {
            setEGLContextFactory(contextFactory);
        }

        if (Base.debug) {
            setDebugFlags(DEBUG_CHECK_GL_ERROR);
        }

        this.base = config.base;
        this.renderer = base.render;

        touchModifierX = base.screen.width * 0.5f;
        touchModifierY = base.screen.height * 0.5f;

        setRenderer(base.render);
        setRenderMode(RENDERMODE_WHEN_DIRTY);

        queueEvent(new Runnable() {
            @Override
            public void run() {
                base.gl.setRenderThread(Thread.currentThread());
            }
        });

        init();
    }

    protected void init() {
        Base.logD("BaseGLView initialized");
    }

    /**
     * every touch position is modified by this values
     * defaultly this values are sets as halfs of screen dimensions [0, 0] -> screen center
     */
    public void setTouchCoordsModifier(float x, float y) {

        touchModifierX = x;
        touchModifierY = y;
    }

    /**
     * starts rendering
     */
    public void startRenderThread() {
        if (renderer.isFPSRender()) {
            if (baseThread == null) {
                baseThread = new BaseThread(renderer.getRequestedFPS());
                baseThread.start();
            }
        }
    }

    /**
     * stops rendering
     */
    public void stopRenderThread() {
        if (baseThread != null && baseThread.isAlive()) {
            baseThread.interrupt();
            baseThread = null;
        }
    }

    /**
     * @return render
     */
    public BaseRenderer getRenderer() {

        return renderer;
    }

    /**
     * add listener to handle touch events
     */
    public void setTouchListener(BaseTouchListener listener) {

        touchListener = listener;
    }

    public void setRenderDelay(int renderDelay) {
        this.renderDelay = renderDelay;
    }

    @Override
    public void onResume() {

        if (!BaseGL.GLCreated) {
            super.onResume();
        }

        if (renderer != null && renderer.isFPSRender() && BaseGL.GLCreated) {
            base.time.deltaStep = 0.0f;
            base.time.delta = 0.0f;
            base.time.delay = 0.0f;
            renderer.onResume();
            renderer.onUpdateFrame();
            requestRender();
            TrainedMonkey.handle(new Runnable() {
                @Override
                public void run() {
                    startRenderThread();
                }
            }, 1000);
        }
    }

    @Override
    public void onPause() {

        stopRenderThread();
        if(renderer != null){
            renderer.onPause();
        }

        if (base.isFinishing()) {
            queueEvent(new Runnable() {
                @Override
                public void run() {
                    base.gl.destroy();
                }
            });
            super.onPause();
        }
    }

    @Override
    public void destroy() {

        stopRenderThread();
        renderer.destroy();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (touchListener != null) {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_POINTER_DOWN:
                    int idD = event.getActionIndex();
                    touchListener.onTouchDown(event.getPointerId(idD), transformX(event.getX(idD)), transformY(event.getY(idD)));
                    break;

                case MotionEvent.ACTION_MOVE:
                    int size = event.getPointerCount();
                    for (int i = 0; i < size; i++) {
                        touchListener.onTouchMove(event.getPointerId(i), transformX(event.getX(i)), transformY(event.getY(i)));
                    }
                    break;

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_POINTER_UP:
                case MotionEvent.ACTION_CANCEL:
                    int idU = event.getActionIndex();
                    touchListener.onTouchUp(event.getPointerId(idU), transformX(event.getX(idU)), transformY(event.getY(idU)));
                    break;
            }
        }

        return true;
    }

    /**
     * transforms x position (defaultly [0] -> center)
     */
    protected float transformX(float x) {

        return x - touchModifierX;
    }

    /**
     * transforms y position (defaultly [0] -> center)
     */
    protected float transformY(float y) {

        return touchModifierY - y;
    }

    /**
     * @return main working thread, this thead updates renderer and requesting screen redraw
     */
    public BaseThread getBaseThread() {

        return baseThread;
    }

    /**
     * base thread
     */
    public class BaseThread extends Thread { //todo

        private final long requestedFrameDelay;
        private final float requestedFPS;
        private int framesSkipped;

        private boolean running;

        //fps
        private float[] fpsPool;
        private int cyclesCount;
        private long lastFrameDelay;

        public BaseThread(float fps) {
            super("BaseThread");
            setPriority(Thread.MAX_PRIORITY);

            requestedFPS = fps;
            lastFrameDelay = requestedFrameDelay = (long) (1000.0f / fps);
            fpsPool = new float[(int) fps];
            for (int i = 0; i < fpsPool.length; i++) {
                fpsPool[i] = fps;
            }

            running = false;
        }

        @Override
        public synchronized void start() {

            if (!isAlive()) {
                running = true;
                super.start();
                Base.logD("BaseGLView", "BaseThread start");
            }
        }

        @Override
        public void interrupt() {

            if (isAlive()) {
                running = false;
                super.interrupt();
                Base.logD("BaseGLView", "BaseThread stop");
            }
        }

        public void notifyIt() {

            synchronized (this) {
                notifyAll();
            }
        }

        @Override
        public void run() { //todo

            long cycleStart;
            long sleepDelay;
            long cycleDuration;
            boolean requestRender;

            while (running) {
                cycleStart = SystemClock.uptimeMillis();

                calcFPS();

                renderer.onUpdateFrame();
                if (framesSkipped == renderDelay) {
                    renderer.renderDone = false;
                    requestRender();
                    framesSkipped = 0;
                } else {
                    framesSkipped++;
                }

                cycleDuration = SystemClock.uptimeMillis() - cycleStart;
                sleepDelay = requestedFrameDelay - cycleDuration - 1;
                if (sleepDelay > 0) {
                    try {
                        sleep(sleepDelay);
                    } catch (InterruptedException e) {
                        //no big deal if chrashed here
                    }
                }

                if (!renderer.renderDone) {
                    while (!renderer.renderDone) {
                        try {
                            sleep(1);
                        } catch (InterruptedException e) {
                            //no big deal if chrashed here
                        }
                    }
                }

                lastFrameDelay = SystemClock.uptimeMillis() - cycleStart;
            }
        }

        private void calcFPS() {

            fpsPool[cyclesCount++] = 1000.0f / (float) lastFrameDelay;
            if (cyclesCount >= fpsPool.length) {
                cyclesCount = 0;
            }

            float fpsSum = 0;
            for (float fps : fpsPool) {
                fpsSum += fps;
            }

            renderer.currentFPS = fpsSum / (float) fpsPool.length;

            base.time.delay = lastFrameDelay;
            base.time.deltaStep = (float) requestedFrameDelay / renderer.currentFPS;
            base.time.delta = 1.0f / renderer.currentFPS;
        }

        public int getFramesSkipped() {

            return framesSkipped;
        }
    }
}

