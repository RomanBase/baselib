package com.base.lib.engine;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.base.lib.engine.builders.BaseFactory;
import com.base.lib.engine.builders.RenderConfig;
import com.base.lib.engine.common.BaseTimer;
import com.base.lib.interfaces.ActivityStateListener;
import com.base.lib.interfaces.BaseTouchListener;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * implements GLSurfaceView.Renderer and prepare some features for base engine
 * <p>class is aimed to deal with all renderables and updateables..<p/>
 */
public abstract class BaseRenderer extends BaseObject implements GLSurfaceView.Renderer, ActivityStateListener {

    private BaseGLView view;

    private final List<Runnable> glQueueList;
    private final List<Runnable> updateQueueList;
    private final BaseUpdateableCollection updateables;

    private final DrawCollection drawables;
    private final BaseRootCollection baseCollection;

    float requestedFPS;
    boolean fpsRendering;

    protected volatile float currentFPS;
    protected volatile boolean renderDone;

    public BaseRenderer(RenderConfig config) {
        super(config.base);
        base.render = this;

        glQueueList = new ArrayList<Runnable>(256);
        updateQueueList = new ArrayList<Runnable>(256);

        updateables = new BaseUpdateableCollection(base, 4096);
        drawables = new DrawCollection();

        baseCollection = new BaseRootCollection(base, 4096);
        updateables.add(baseCollection);

        requestedFPS = config.getRequestedFps();
        fpsRendering = requestedFPS > 0;

        onBaseGLInits(config);
    }

    protected void onBaseGLInits(RenderConfig config) {

        BaseFactory factory = config.getFactory();
        BaseGL gl = new BaseGL(base);

        base.initGL(gl);
        base.initFactory(factory);

        gl.initGLPool(config.getEglHolder());
        gl.init(factory);

        rebindShaderCollection();
    }

    /**
     * called with GL Context after base initialization
     */
    protected abstract void onCreate();

    /**
     * called by Update Thread, but before of any other updatable object
     */
    protected abstract void onUpdate();

    /**
     * called by GL Thread, but before of any other drawable object
     */
    protected abstract void onPreDraw();

    /**
     * called by GL Thread, but after of all other drawable objects
     */
    protected abstract void onPostDraw();

    /**
     * called with Application life cycle
     */
    protected abstract void onDestroy();

    /**
     * rebind Shader collection to set newly defined shaders to use, note: just call this after Shaders construction block
     */
    public void rebindShaderCollection() {

        drawables.init(base.gl.shaders);
    }

    /**
     * sets GLSurfaceView
     */
    public void setGLView(BaseGLView glView) {

        view = glView;
    }

    /**
     * @return GLSurfaceView used by this Renderer
     */
    public BaseGLView getView() {

        return view;
    }

    /**
     * sets touch listener, note: only one listener is supported. Use BaseTouchResponer or some modification to provide more listeners
     */
    public void setTouchListener(BaseTouchListener listener) {

        view.setTouchListener(listener);
    }

    /**
     * sets update - render ratio (eg. delay value 1 means: update-update->render-update-update->render)
     * note: rendering runs on half fps (eg. 60 updates to 30 draws)
     */
    public void setRenderDelay(int frames) {

        view.setRenderDelay(frames);
    }

    /**
     * @see android.opengl.GLSurfaceView requestRender()
     */
    public void render() {

        view.requestRender();
    }

    /**
     * @return current fps calculated in BaseThread
     */
    public float getCurrentFPS() {

        return currentFPS;
    }

    /**
     * sets requested fps for BaseThread
     */
    public void setRequestedFPS(float fps) {

        this.requestedFPS = fps;
    }

    /**
     * @return requested fps for BaseThread
     */
    public float getRequestedFPS() {

        return requestedFPS;
    }

    /**
     * tells renderer to start fps BaseThead at the end of onCreate method
     */
    public void useFPSRendering() {

        fpsRendering = true;
    }

    /**
     * @return true if renderer is used for continual updating
     */
    public boolean isFPSRender() {

        return fpsRendering;
    }

    /**
     * starts BaseThead
     */
    protected void startFPSRendering() {

        if (requestedFPS < 1.0f) {
            requestedFPS = 30.0f;
        }
        fpsRendering = true;
        view.startRenderThread();
    }

    /**
     * stops BaseThread
     */
    protected void stopFPSRendering() {

        view.stopRenderThread();
        fpsRendering = false;
    }

    /**
     * push renderable for next draw cycle
     */
    public void addRenderable(BaseRenderable renderable) {

        drawables.add(renderable);
    }

    /**
     * adds updateable to collection
     */
    public void addUpdateable(BaseUpdateable updateable) {

        updateables.add(updateable);
    }

    /**
     * adds updateable to collection
     */
    public void addUpdateableAtFront(BaseUpdateable updateable) {

        updateables.addAtFront(updateable);
    }

    /**
     * removes updateable from collection
     */
    public void removeUpdateable(BaseUpdateable updateable) {

        updateables.remove(updateable);
    }

    /**
     * adds renderable to base root collection
     */
    public void addDrawable(BaseRenderable drawable) {

        baseCollection.add(drawable);
    }

    /**
     * adds renderables to base root collection
     */
    public void addDrawable(List<BaseRenderable> drawables) {

        for (BaseRenderable drawable : drawables) {
            addDrawable(drawable);
        }
    }

    /**
     * adds renderables to base root collection
     */
    public void addDrawable(BaseRenderable[] drawables) {

        for (BaseRenderable drawable : drawables) {
            addDrawable(drawable);
        }
    }

    /**
     * remove renderable from base root collection
     */
    public void removeDrawable(BaseRenderable drawable) {

        baseCollection.remove(drawable);
    }

    /**
     * remove renderables from base root collection
     */
    public void removeDrawable(List<BaseRenderable> drawables) {

        for (BaseRenderable drawable : drawables) {
            removeDrawable(drawable);
        }
    }

    /**
     * remove renderables from base root collection
     */
    public void removeDrawable(BaseRenderable[] drawables) {

        for (BaseRenderable drawable : drawables) {
            removeDrawable(drawable);
        }
    }

    /**
     * destroy renderable from base root collection, note: in game cycle use renderable unUse() method instead..
     */
    public void destroyDrawable(BaseRenderable drawable) {

        removeDrawable(drawable);
        drawable.destroy();
        drawable = null;
    }

    /**
     * destroy renderables from base root collection, note: in game cycle use renderable unUse() method instead..
     */
    public void destroyDrawable(List<BaseRenderable> drawables) {

        for (BaseRenderable drawable : drawables) {
            destroyDrawable(drawable);
        }

        drawables = null;
    }

    /**
     * destroy renderables from base root collection, note: in game cycle use renderable unUse() method instead..
     */
    public void destroyDrawable(BaseRenderable[] drawables) {

        for (BaseRenderable drawable : drawables) {
            destroyDrawable(drawable);
        }

        drawables = null;
    }

    /**
     * clear or destroy all objects in all collections
     */
    public void clearDrawables(boolean destroy) {

        if (destroy) {
            baseCollection.destroy();
        } else {
            baseCollection.clear();
        }
    }

    /**
     * @return all renderable objects from collections
     */
    public List<BaseRenderable> getDrawables() {

        List<BaseRenderable> renderables = new ArrayList<BaseRenderable>();
        List<BaseUpdateable> updateables = new ArrayList<BaseUpdateable>();

        for (int i = 0; i < this.updateables.size; i++) {
            getUpdateableIteration(this.updateables.updateables[i], updateables);
        }

        for (BaseUpdateable updateable : updateables) {
            if (updateable != null) {
                getDrawableIteration(updateable, renderables);
            } else {
                break;
            }
        }

        return renderables;
    }

    /**
     * iterate thru collections and fill list of updateables
     */
    private void getUpdateableIteration(BaseUpdateable updateable, List<BaseUpdateable> toFill) {

        if (updateable instanceof BaseUpdateableCollection) {
            BaseUpdateable[] collection = ((BaseUpdateableCollection) updateable).getAll();
            for (BaseUpdateable u : collection) {
                getUpdateableIteration(u, toFill);
            }
        } else {
            toFill.add(updateable);
        }
    }

    /**
     * iterate thru collections and fill list of renderables
     */
    private void getDrawableIteration(BaseUpdateable updateable, List<BaseRenderable> toFill) {

        if (updateable instanceof BaseRootCollection) {
            BaseRenderable[] collection = ((BaseRootCollection) updateable).getAll();
            for (BaseRenderable renderable : collection) {
                getDrawableIteration(renderable, toFill);
            }
        } else if (updateable instanceof BaseRenderable) {
            toFill.add((BaseRenderable) updateable);
        }
    }

    public List<BaseUpdateable> getUpdateables() {

        return updateables.getAllAsList();
    }

    /**
     * perform add at the end of the update method
     */
    public void addDrawableSafety(final BaseRenderable drawable) {

        runOnBaseThread(new Runnable() {
            @Override
            public void run() {
                addDrawable(drawable);
            }
        });
    }

    /**
     * perform action at the end of update method
     */
    public void runOnBaseThread(Runnable runnable) {

        updateQueueList.add(runnable);
    }

    /**
     * perform action at the end of update method after delay
     */
    public void runOnBaseThread(final Runnable runnable, long millisecDelay) {

        (new BaseTimer(millisecDelay) {
            @Override
            public void onDone() {
                updateQueueList.add(runnable);
            }
        }).use();
    }

    /**
     * @see android.opengl.GLSurfaceView queueEvent(action)
     */
    public void runOnRenderThreadImmediately(Runnable runnable) {

        view.queueEvent(runnable);
    }

    /**
     * perform action at the start of next Draw method
     */
    public void glQueueEvent(Runnable runnable) {

        glQueueList.add(runnable);
    }

    /**
     * called by BaseThread cycle
     */
    protected void onUpdateFrame() {

        if (!updateQueueList.isEmpty()) {
            int count = updateQueueList.size();
            for (int i = 0; i < count; i++) {
                Runnable r = updateQueueList.remove(0);
                if (r != null) {
                    r.run();
                    r = null;
                }
            }
        }

        onUpdate();

        updateables.update();
    }

    /**
     * GL10 UNUSED!
     * Main render method
     */
    @Override
    public void onDrawFrame(GL10 glUnused) { //TODO

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        if (!glQueueList.isEmpty()) {
            int count = glQueueList.size();
            for (int i = 0; i < count; i++) {
                Runnable r = glQueueList.remove(0);
                if (r != null) {
                    r.run();
                    r = null;
                }
            }
        }

        onPreDraw();
        drawables.draw();
        onPostDraw();

        drawables.clear();

        renderDone = true;
        //GLES20.glFlush(); //This is unnecessary. The SwapBuffer command takes care of flushing and command processing.
    }

    /**
     * GL10 UNUSED!
     * Set up glViewport to screen size!
     */
    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height) {

        // Set the OpenGL viewport to the same size as the screen.
        GLES20.glViewport(0, 0, width, height);

        Base.logD("BaseRender", "surface changed:  " + width + "  " + height + " | " + ((float) width / (float) height) + " (" + ((float) height / (float) width) + ")");
    }

    /**
     * GL10 UNUSED!
     */
    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {

        // Set the background clear color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        BaseGL.disbaleDithering(); //gl ignores indexing(in 16/32 bit mode), but for sure disable it to get some performance

        //enableTextureMapping(); // trows invalid enum
        BaseGL.enableDepthTest();
        BaseGL.enableCulling();
        BaseGL.glError();

        BaseGL.enableTransparency();
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        //GLES20.glEnable(GLES20.GL_ALPHA_BITS); // trows invalid enum
        BaseGL.setDepthFuncLequal();
        BaseGL.glError();

        base.gl.onCreate();
        onCreate();
        BaseGL.glError();

        Base.logD("BaseRender", "surface created! BaseGL initialized.");

        if (fpsRendering) {
            view.startRenderThread(); // start render thread if fpsRendering
        }
    }

    /**
     * empty
     */
    @Override
    public void onResume() {

    }

    /**
     * empty
     */
    @Override
    public void onPause() {

    }

    /**
     * destroy collections
     */
    @Override
    public void destroy() {

        BaseUpdateable[] ups = updateables.getAll();
        for (BaseUpdateable updateable : ups) {
            updateable.destroy();
        }
        updateables.clear();
        glQueueList.clear();
        updateQueueList.clear();
        clearDrawables(true);
        view = null;

        onDestroy();
    }

    /**
     * @return weak reference
     */
    public BaseRenderer reference() {

        return this;
    }


    /*! INNER CLASSES !*/

    /**
     * holds reference for shader and objects to draw with this shader
     */
    class ShaderCollection extends BaseRootCollection {

        protected BaseShader shader;

        protected ShaderCollection(Base base, BaseShader shader) {
            super(base, shader.getCollectionSize());

            this.shader = shader;
            index = -1;
        }

        protected void draw() {

            BaseGL.glError("clear");

            if (size > 0) {
                BaseGL.useProgram(shader.glid);
                while (++index < size) {
                    renderables[index].draw();
                }
                index = -1;
            }

            BaseGL.glError(shader.toString());
        }
    }

    /**
     * holds all shader collections and draw them
     */
    class DrawCollection {

        protected Map<BaseShader, ShaderCollection> shaderCollections;

        protected DrawCollection() {

        }

        void init(List<BaseShader> shaders) {

            if (shaders != null) {
                int count = shaders.size();
                shaderCollections = new LinkedHashMap<>(shaders.size());
                for (int i = 0; i < count; i++) {
                    BaseShader shader = shaders.get(i);
                    if (shaderCollections.get(shader) == null) {
                        shaderCollections.put(shader, new ShaderCollection(base, shader));
                    }
                }
            }
        }

        void add(BaseRenderable object) {

            shaderCollections.get(object.shader).add(object);
        }

        void draw() {

            for (ShaderCollection collection : shaderCollections.values()) {
                if (collection != null) {
                    collection.draw();
                }
            }
        }

        void clear() {

            if (shaderCollections != null) {
                for (ShaderCollection collection : shaderCollections.values()) {
                    if (collection != null) {
                        collection.clear();
                    }
                }
            }
        }

        void destroy() {

            clear();
        }
    }
}