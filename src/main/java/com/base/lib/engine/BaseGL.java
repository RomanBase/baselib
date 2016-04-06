package com.base.lib.engine;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLU;

import com.base.lib.R;
import com.base.lib.engine.builders.BaseFactory;
import com.base.lib.engine.common.Buffers;
import com.base.lib.engine.common.Colorf;
import com.base.lib.interfaces.GLPoolRunnable;
import com.base.lib.engine.common.gl.EGLHolder;
import com.base.lib.interfaces.GLStateListener;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLContext;

public class BaseGL extends BaseObject {

    /**
     * true if GL Context is created
     */
    public static boolean GLCreated = false;

    final List<BaseShader> shaders = new ArrayList<>(32);
    final List<GLStateListener> glends = new ArrayList<>();

    protected static BaseTexture baseTexture;
    protected static BaseShader baseShader;

    private static GLTPos[] glTPos;
    private static int currentProgram;

    private Thread glThead;
    private BaseActionPool glPool;

    public BaseGL(Base base) {
        super(base);

        glPool = new BaseActionPool();
    }

    public void initGLPool(EGLHolder egl) {

        glPool.initEGL(egl, base.render);
    }

    /**
     * inits base texture and shaders
     */
    public void init(BaseFactory factory) {

        GLCreated = isGLContextCreated();

        baseTexture = factory.gen.textureResources(R.drawable.uvmap2);
        baseShader = factory.gen.shaderResource(BaseShader.TEXTURE, R.raw.texture_vert, R.raw.texture_frag, "u_MVPMatrix", "a_Position", "a_TexCoordinate");
        factory.gen.shaderResource(BaseShader.COLOR, R.raw.one_color_vert, R.raw.color_frag, "u_MVPMatrix", "a_Position", "u_Color");
        factory.gen.shaderResource(BaseShader.TEXTURE_COLOR, R.raw.texture_fade_vert, R.raw.texture_fade_frag, "u_MVPMatrix", "a_Position", "a_TexCoordinate", "u_Color");
        factory.gen.shaderResource(BaseShader.INSTANCING, R.raw.particle_vert, R.raw.particle_frag, "u_VPMatrix", "a_Position", "a_Texture", "a_Color", "u_ScaleRatio", "u_SpriteSize");

/*
        new BaseShader(SHADERS[1], "u_MVPMatrix", "a_Position", "a_Color")
                .loadShadersFromResources(R.raw.color_vert, R.raw.color_frag);

        new BaseShader(SHADERS[2], "u_MVPMatrix", "a_Position", "a_TexCoordinate", "u_Light", "u_Texture", "u_Normals")
                .loadShadersFromResources(R.raw.bump_vert, R.raw.bump_frag);

        new BaseShader(SHADERS[5], "u_MVPMatrix", "a_Position", "a_TexCoordinate", "u_Center")
                .loadShadersFromResources(R.raw.blur_vert, R.raw.blur_frag);
*/
    }

    public void setRenderThread(Thread thread) {

        glThead = thread;
        glThead.setPriority(Thread.MAX_PRIORITY);
    }

    public void onCreate() {

        int count = BaseGL.getTextureMaxCombined();
        glTPos = new GLTPos[count];
        for (int i = 0; i < count; i++) {
            glTPos[i] = new GLTPos();
            glTPos[i].position = GLES20.GL_TEXTURE0 + i;
        }

        GLCreated = true;
        currentProgram = 0;
    }

    /**
     * sets initial texture
     */
    public static void setBaseTexture(BaseTexture texture) {

        BaseGL.baseTexture = texture;
    }

    /**
     * sets initial shader
     */
    public static void setBaseShader(BaseShader shader) {

        BaseGL.baseShader = shader;
    }

    public void pushShaderToFront(BaseShader shader) {

        shaders.remove(shader);
        shaders.add(0, shader);

        base.render.rebindShaderCollection();
    }

    /**
     * log GL ERROR code and string message, note: logs only if Base.debug is true
     */
    public static boolean glError() {

        return glError("GL Error");
    }

    /**
     * log GL ERROR code and string message with custom TAG, note: logs only if Base.debug is true
     */
    public static boolean glError(String tag) {

        int errCode = GLES20.glGetError();
        if (errCode != GLES20.GL_NO_ERROR) {
            String err = GLU.gluErrorString(errCode);
            Base.logE(tag, String.format("%s: %s", errCode, err));
            return true;
        }

        return false;
    }

    public void glTask(final Runnable action) {

        //glPool.addTask(action);
        glRun(new Runnable() {
            @Override
            public void run() {
                action.run();
            }
        });
    }

    public void glTask(final GLPoolRunnable action) {

        //glPool.addGLTask(action);
        glRun(new Runnable() {
            @Override
            public void run() {
                action.glRun(action.run());
            }
        });
    }

    /**
     * perform GL action in correct thread
     */
    public void glRun(Runnable action) {

        if (isOnGLThread()) {
            action.run();
        } else {
            base.render.glQueueEvent(action);
        }
    }

    /**
     * perform GL action in GLView thread
     */
    public void glRunAsync(Runnable action) {

        if (isOnGLThread()) {
            action.run();
        } else {
            base.render.getView().queueEvent(action);
        }
    }

    /**
     * check current glProgram, if is different send new one into GL, note: must be performed in correct GL thread
     *
     * @param program gl shader glid (Shader.glProgram)
     */
    public static void useProgram(int program) {

       // if (program != currentProgram) {
            GLES20.glUseProgram(program);
            currentProgram = program;
       // }
    }

    /**
     * check current glProgram, if is different send new one into GL, note: must be performed in correct GL thread
     *
     * @param shader Shader
     */
    public static void useProgram(BaseShader shader) {

       // if (shader.glid != currentProgram) {
            GLES20.glUseProgram(shader.glid);
            currentProgram = shader.glid;
       // }
    }

    public static int getCurrentProgram() {

        return currentProgram;
    }

    /**
     * active gl texture unit at given position into GL
     *
     * @param index gl active texture indexed -> (GL_TEXTURE0 = 0, GL_TEXTURE1 = 1, etc.)
     */
    public static void activeTexture(int index) {

        GLES20.glActiveTexture(glTPos[index].position);
    }

    /**
     * active gl texture unit at given position into GL
     *
     * @param index  gl active texture indexed -> (GL_TEXTURE0 = 0, GL_TEXTURE1 = 1, etc.)
     * @param handle shader handle index (Shader.handle[index])
     */
    public static void activeTexture(int index, int handle) {

        GLES20.glActiveTexture(glTPos[index].position);
        GLES20.glUniform1i(handle, index);
    }

    /**
     * active gl texture unit at given position into GL
     *
     * @param index   gl active texture position indexed -> (GL_TEXTURE0 = 0, GL_TEXTURE1 = 1, etc.)
     * @param handle  shader handle index (Shader.handle[index])
     * @param sampler texture position for shader sampler (typically same as index)
     */
    public static void activeTexture(int index, int handle, int sampler) {

        GLES20.glActiveTexture(glTPos[index].position);
        GLES20.glUniform1i(handle, sampler);
    }

    /**
     * check current texture, if is different bind new one into GL, note: must be performed in correct GL thread
     *
     * @param glid gl texture id (Texture.glid)
     */
    public static void bindTexture(int glid) {

        glTPos[0].bind(glid);
    }

    /**
     * binds texture GL unit at current position, note: must be performed in correct GL thread
     */
    public static void bindTexture(BaseTexture texture) {

        glTPos[0].bind(texture.glid);
    }

    /**
     * bind texture into gl at specific position, note: must be performed in correct GL thread
     * from 0 to getTextureMaxCount()
     *
     * @param glid  gl texture id (Texture.glid)
     * @param index gl active texture indexed -> (GL_TEXTURE0 = 0, GL_TEXTURE1 = 1, etc.)
     */
    public static void bindTexture(int glid, int index) {

        glTPos[index].bind(glid);
    }

    /**
     * bind texture into gl at specific position, note: must be performed in correct GL thread
     * from 0 to getTextureMaxCount()
     *
     * @param texture gl texture id (Texture.glid)
     * @param index   gl active texture indexed -> (GL_TEXTURE0 = 0, GL_TEXTURE1 = 1, etc.)
     */
    public static void bindTexture(BaseTexture texture, int index) {

        glTPos[index].bind(texture.glid);
    }

    /**
     * binds texture GL unit at specific position for specific shader handle and sampler index,
     * note: must be performed in correct GL thread
     * index from 0 to getTextureMaxCount()
     *
     * @param glid   gl texture id (Texture.glid)
     * @param index  gl active texture position indexed -> (GL_TEXTURE0 = 0, GL_TEXTURE1 = 1, etc.), sampler id = index
     * @param handle shader handle index (Shader.handle[index])
     */
    public static void bindTexture(int glid, int index, int handle) {

        glTPos[index].bind(glid);
        GLES20.glUniform1i(handle, index);
    }

    /**
     * binds texture GL unit at specific position for specific shader handle and sampler index,
     * note: must be performed in correct GL thread
     * index from 0 to getTextureMaxCount()
     *
     * @param glid    gl texture id (Texture.glid)
     * @param index   gl active texture position indexed -> (GL_TEXTURE0 = 0, GL_TEXTURE1 = 1, etc.)
     * @param handle  shader handle index (Shader.handle[index])
     * @param sampler texture position for shader sampler (typically same as index)
     */
    public static void bindTexture(int glid, int index, int handle, int sampler) {

        glTPos[index].bind(glid);
        GLES20.glUniform1i(handle, sampler);
    }

    /**
     * generate custom GL buffer, note: must be performed in correct GL thread
     *
     * @param buffer       System buffer
     * @param bufferType   ARRAY, ELEMENT
     * @param bytesPerUnit number of bytes per element (FLOAT - 4, SHORT - 2, Buffers.BYTESPER*)
     * @param usage        STATIC, DYNAMIC, STREAM
     */
    public static int genBuffer(Buffer buffer, int bufferType, int bytesPerUnit, int usage) {

        final int out[] = new int[1];

        buffer.position(0);
        GLES20.glGenBuffers(1, out, 0);
        GLES20.glBindBuffer(bufferType, out[0]);
        GLES20.glBufferData(bufferType, buffer.capacity() * bytesPerUnit, buffer, usage);
        GLES20.glBindBuffer(bufferType, 0);

        return out[0];
    }

    /**
     * generate GL float buffer, note: must be performed in correct GL thread
     *
     * @param usage STATIC, DYNAMIC, STREAM
     */
    public static int genArrayFloatBuffer(FloatBuffer buffer, int usage) {

        return genBuffer(buffer, GLES20.GL_ARRAY_BUFFER, Buffers.BYTESPERFLOAT, usage);
    }

    /**
     * generate static GL float buffer, note: must be performed in correct GL thread
     */
    public static int genArrayFloatBuffer(FloatBuffer buffer) {

        return genBuffer(buffer, GLES20.GL_ARRAY_BUFFER, Buffers.BYTESPERFLOAT, GLES20.GL_STATIC_DRAW);
    }

    /**
     * generate static GL short buffer, note: must be performed in correct GL thread
     */
    public static int genElementShortBuffer(ShortBuffer buffer) {

        return genBuffer(buffer, GLES20.GL_ELEMENT_ARRAY_BUFFER, Buffers.BYTESPERSHORT, GLES20.GL_STATIC_DRAW);
    }

    /**
     * delete GL buffer, note: must be performed in correct GL thread
     */
    public static void destroyBuffer(int bufferID) {

        GLES20.glDeleteBuffers(1, new int[]{bufferID}, 0);
    }

    /**
     * delete all GL buffers, note: must be performed in correct GL thread
     */
    public static void destroyBuffers(int... bufferIDs) {

        if (bufferIDs != null) {
            GLES20.glDeleteBuffers(bufferIDs.length, bufferIDs, 0);
        }
    }

    /**
     * asks gl for image max size, which can be bound into gl, depends on device SW HW
     * typically 2048px, note: must be performed in correct GL thread
     *
     * @return size in pixels
     */
    public static int getTextureMaxSize() {

        int[] maxSize = new int[1];
        GLES20.glGetIntegerv(GLES20.GL_MAX_TEXTURE_SIZE, maxSize, 0);
        return maxSize[0];
    }

    /**
     * asks gl for maximum number of textures bound into gl in same time, note: must be performed in correct GL thread
     *
     * @return maximum number of binded textures in same time
     */
    public static int getTextureMaxCombined() {

        int[] maxCount = new int[1];
        GLES20.glGetIntegerv(GLES20.GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS, maxCount, 0);
        return maxCount[0];
    }

    /**
     * reads data from framebuffer and creates a bitmap ([0,0] = center)
     */
    public Bitmap getScreen(int x, int y, int width, int height) {

        int screenSize = width * height;
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(screenSize * 4).order(ByteOrder.nativeOrder());
        GLES20.glPixelStorei(GLES20.GL_PACK_ALIGNMENT, 1);
        GLES20.glReadPixels((int) base.screen.width / 2 - width / 2 + x, (int) base.screen.height / 2 - height / 2 + y, width, height, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, byteBuffer);

        int pixelsBuffer[] = new int[screenSize];
        byteBuffer.asIntBuffer().get(pixelsBuffer);
        byteBuffer = null;

        for (int i = 0; i < screenSize; ++i) {
            // The alpha and green channels' positions are preserved while the red and blue are swapped
            pixelsBuffer[i] = ((pixelsBuffer[i] & 0xff00ff00)) | ((pixelsBuffer[i] & 0x000000ff) << 16) | ((pixelsBuffer[i] & 0x00ff0000) >> 16);
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixelsBuffer, screenSize - width, -width, 0, 0, width, height);

        return bitmap;
    }

    /**
     * checks for GL Context
     */
    public static boolean isGLContextCreated() {

        return !((EGL10) EGLContext.getEGL()).eglGetCurrentContext().equals(EGL10.EGL_NO_CONTEXT);
    }

    /**
     * checks if current thread is GLThread
     */
    public boolean isOnGLThread() {

        Thread currentThread = Thread.currentThread();

        return currentThread == glThead;
    }

    /**
     * listen application life cycle and GL Context state
     */
    public void addGLEndListener(GLStateListener listener) {

        glends.add(listener);
    }

    /**
     * listen application life cycle and GL Context state
     */
    public void removeGLEndListener(GLStateListener listener) {

        glends.remove(listener);
    }

    public static void useBaseShader() {

        useProgram(baseShader.glid);
    }

    public static void bindBaseTexture() {

        bindTexture(baseTexture.glid);
    }

    public static BaseTexture getBaseTexture() {
        return baseTexture;
    }

    public static BaseShader getBaseShader() {
        return baseShader;
    }

    /**
     * destroy textures shaders and other GL resources
     */
    public void destroy() {

        GLES20.glFlush();
        GLES20.glFinish();

        glPool.kill();
        useProgram(0);

        for (GLStateListener glend : glends) {
            glend.onGLEnd();
        }

        base.factory.clearTextures();
        base.factory.clearShaders();

        glends.clear();

        GLCreated = false;

        Base.logI("BaseGL destroyed");
    }

    //------------------------- GL OVERRIDES -------------------------//  //todo thread check ?

    public static void glClear() {

        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
    }

    public static void setDepthFuncLequal() {

        GLES20.glDepthFunc(GLES20.GL_LEQUAL);
    }

    public static void setDepthFuncLess() {

        GLES20.glDepthFunc(GLES20.GL_LESS);
    }

    public static void setDepthFuncGreater() {

        GLES20.glDepthFunc(GLES20.GL_GREATER);
    }

    public static void clearStencilBuffer() {

        GLES20.glClear(GLES20.GL_STENCIL_BUFFER_BIT);
    }

    public static void enableStencil() {

        GLES20.glEnable(GLES20.GL_STENCIL_TEST);
    }

    public static void disableStencil() {

        GLES20.glDisable(GLES20.GL_STENCIL_TEST);
    }

    public static void setStencilClear(int s) {

        GLES20.glClearStencil(s);
    }

    public static void setDepthClear(float depth) {

        GLES20.glClearDepthf(depth);
    }

    @Deprecated
    public static void enableTextureMapping() { //throws invalid enum

        GLES20.glEnable(GLES20.GL_TEXTURE_2D);
    }

    @Deprecated
    public static void disableTextureMapping() { //throws invalid enum

        GLES20.glDisable(GLES20.GL_TEXTURE_2D);
    }

    public static void enableTransparency() {

        GLES20.glEnable(GLES20.GL_BLEND);
    }

    public static void disableTransparency() {

        GLES20.glDisable(GLES20.GL_BLEND);
    }

    public static void enableDepthTest() {

        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glDepthMask(true);
    }

    public static void disableDepthTest() {

        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        GLES20.glDepthMask(false);
    }

    public static void enableCulling() {

        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glCullFace(GLES20.GL_BACK);
    }

    public static void disableCulling() {

        GLES20.glDisable(GLES20.GL_CULL_FACE);
    }

    public static void enableDithering() {

        GLES20.glEnable(GLES20.GL_DITHER);
    }

    public static void disbaleDithering() {

        GLES20.glDisable(GLES20.GL_DITHER);
    }

    public static void setClearColor(float r, float g, float b, float a) {

        GLES20.glClearColor(r, g, b, a);
    }

    public static void setClearColor(Colorf color) {

        setClearColor(color.r, color.g, color.b, color.a);
    }

    private static class GLTPos {

        int glid;
        int position;

        void bind(int nglid) {

            GLES20.glActiveTexture(position);
            //if (glid != nglid) {
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, nglid);
                glid = nglid;
            //}
        }
    }
}


