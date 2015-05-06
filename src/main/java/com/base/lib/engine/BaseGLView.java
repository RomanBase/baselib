package com.base.lib.engine;

import android.annotation.SuppressLint;
import android.opengl.GLSurfaceView;
import android.os.SystemClock;
import android.view.MotionEvent;

import com.base.lib.engine.common.BaseTouchResponder;
import com.base.lib.engine.other.dev.TouchMarker;
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
    private float touchModifierX;
    private float touchModifierY;
    private BaseTouchListener touchListener;
    int renderDelay;

    /**
     * initialize GL context (OpenGL ES 2.0) and sets renderer
     */
    public BaseGLView(BaseRenderer renderer) {
        this(renderer, null, null);
    }

    /**
     * initialize GL context (OpenGL ES 2.0) and sets renderer
     *
     * @param configChooser  can be null.
     * @param contextFactory can be null.
     */
    public BaseGLView(BaseRenderer renderer, EGLConfigChooser configChooser, EGLContextFactory contextFactory) {
        super(Base.context);

        Base.glView = this;
        Base.activity.addActivityStateListener(this);

        touchModifierX = Base.screenWidth * 0.5f;
        touchModifierY = Base.screenHeight * 0.5f;

        // Create an OpenGL ES 2.0 context.
        setEGLContextClientVersion(2);

        //setPreserveEGLContextOnPause(true);

        if (configChooser != null) {
            setEGLConfigChooser(configChooser);
        }

        if (contextFactory != null) {
            setEGLContextFactory(contextFactory);
        }

        if (Base.debug) {
            setDebugFlags(DEBUG_CHECK_GL_ERROR);
            touchListener = new TouchMarker();
        } else {
            touchListener = new BaseTouchResponder();
        }

        setRenderer(renderer);
        setRenderMode(RENDERMODE_WHEN_DIRTY);
        this.renderer = renderer;
        renderer.setGLView(this);

        queueEvent(new Runnable() {
            @Override
            public void run() {
                BaseGL.glThead = Thread.currentThread();
                BaseGL.glThead.setPriority(Thread.MAX_PRIORITY);
            }
        });

        init();
    }

    protected void init() {

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

        if (renderer.isFPSRender() && BaseGL.GLCreated) {
            BaseTime.deltaStep = 0.0f;
            BaseTime.delta = 0.0f;
            BaseTime.delay = 0.0f;
            renderer.onUpdateFrame();
            requestRender();
            Base.activity.runOnUiThread(new Runnable() {
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

        if (Base.isFinishing()) {
            queueEvent(new Runnable() {
                @Override
                public void run() {
                    BaseGL.destroy();
                }
            });
            super.onPause();
        }
    }

    @Override
    public void destroy() {

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
                if(framesSkipped == renderDelay) {
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

            BaseTime.delay = lastFrameDelay;
            BaseTime.deltaStep = (float) requestedFrameDelay / renderer.currentFPS;
            BaseTime.delta = 1.0f / renderer.currentFPS;
        }

        public int getFramesSkipped() {

            return framesSkipped;
        }
    }
}

