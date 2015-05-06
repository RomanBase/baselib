package com.base.lib.engine;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLU;

import com.base.lib.R;
import com.base.lib.engine.common.Buffers;
import com.base.lib.engine.common.Colorf;
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

public class BaseGL { //todo GLStateListener

    /** true if GL Context is created */
    protected static boolean GLCreated = false;

    /** list of all shaders created by Shader class */
    protected static List<BaseShader> shaders;
    /** list of all textures created by Texture class */
    protected static List<Texture> textures;
    /** list of all GL state listeners */
    protected static List<GLStateListener> glends;

    /** base texture for all objects */
    protected static Texture baseTexture;
    /** base shader for all objects */
    protected static BaseShader baseShader;

    /** reference to thread with GL Context */
    protected static Thread glThead;

    public static final String[] SHADERS = new String[] {"texture", "color", "bump", "mix", "one_color", "blur"};

    public static int glViewWidth;
    public static int glViewHeight;

    private static GLTPos[] glTPos;
    private static int currentProgram;

    public static void rebindCollections(int shaderCapacity, int textureCapacity){

        shaders = new ArrayList<BaseShader>(shaderCapacity);
        textures = new ArrayList<Texture>(textureCapacity);
        glends = new ArrayList<GLStateListener>();
    }

    /** inits some base texture and shaders */
    static void init() {

        GLCreated = isGLContextCreated();

        baseTexture = new Texture(R.drawable.uvmap2);

        baseShader = new BaseShader(SHADERS[0], "u_MVPMatrix", "a_Position", "a_TexCoordinate")
                .loadShadersFromResources(R.raw.texture_vertex_shader, R.raw.texture_fragment_shader);

        /*if (contains(1)) {
            new Shader(SHADERS[1], "u_MVPMatrix", "a_Position", "a_Color")
                    .loadShadersFromResources(R.raw.color_vertex_shader, R.raw.color_fragment_shader);
        }*/

        /*if (contains(2)) {
            new Shader(SHADERS[2], "u_MVPMatrix", "a_Position", "a_TexCoordinate", "u_Light", "u_Texture", "u_Normals")
                    .loadShadersFromResources(R.raw.bump_vertex_shader, R.raw.bump_fragment_shader);
        }*/

            new BaseShader(SHADERS[3], "u_MVPMatrix", "a_Position", "a_TexCoordinate", "u_Color")
                    .loadShadersFromResources(R.raw.texture_fade_vertex, R.raw.texture_fade_fragment);

            new BaseShader(SHADERS[4], "u_MVPMatrix", "a_Position", "u_Color")
                    .loadShadersFromResources(R.raw.one_color_vertex, R.raw.color_fragment_shader);

        /*if (contains(5)) {
            new Shader(SHADERS[5], "u_MVPMatrix", "a_Position", "a_TexCoordinate", "u_Center")
                    .loadShadersFromResources(R.raw.blur_vert, R.raw.blur_frag);
        }*/

        new BaseShader("particle", "u_VPMatrix", "a_Position", "a_Texture", "a_Color", "u_ScaleRatio", "u_SpriteSize")
                .loadShadersFromResources(R.raw.particle_vert, R.raw.particle_frag);
    }

    /** called by Renderer on GLThread */
    static void onCreate(){

        GLCreated = true;
        currentProgram = -1;

        int count = BaseGL.getTextureMaxCombined();
        glTPos = new GLTPos[count];
        for(int i = 0; i<count; i++){
            glTPos[i] = new GLTPos();
            glTPos[i].position = GLES20.GL_TEXTURE0+i;
        }
    }

    public static void rebindResources(){

        for(Texture texture : textures){
            if(texture.glid == 0) {
                texture.createGLTexture();
            }
        }

        for(BaseShader shader : shaders){
            if(shader.glProgram == 0) {
                shader.createGLProgram();
            }
        }
    }

    public static void setGLCreated(){

        GLCreated = true;
    }

    /** sets initial texture */
    public static void setBaseTexture(Texture texture){

        BaseGL.baseTexture = texture;
    }

    /** sets initial shader */
    public static void setBaseShader(BaseShader shader){

        BaseGL.baseShader = shader;
    }

    public static void pushShaderToFront(BaseShader shader){

        shaders.remove(shader);
        shaders.add(0, shader);

        int size = shaders.size();
        for(int i = 0; i<size; i++){
            shaders.get(i).id = i;
        }

        Base.render.rebindShaderCollection();
    }

    /** log GL ERROR code and string message, note: logs only if Base.debug is true */
    public static void glError(){

        glError("GL Error");
    }

    /** log GL ERROR code and string message with custom TAG, note: logs only if Base.debug is true */
    public static void glError(String tag){

        int errCode = GLES20.glGetError();
        if(errCode != GLES20.GL_NO_ERROR){
            String err = GLU.gluErrorString(errCode);
            Base.logE(tag, errCode + ": " + err);
        }
    }

    /** perform GL Enable operation in correct thread */
    public static void glEnable(final int GLID){

        glRun(new Runnable() {
            @Override
            public void run() {
                GLES20.glEnable(GLID);
            }
        });
    }

    /** perform GL Disable operation in correct thread */
    public static void glDisable(final int GLID){

        glRun(new Runnable() {
            @Override
            public void run() {
                GLES20.glDisable(GLID);
            }
        });
    }

    /** perform GL action in correct thread */
    public static void glRun(Runnable action){

        if(isOnGLThread()){
            action.run();
        } else {
            Base.render.glQueueEvent(action);
        }
    }

    /**
     * check current glProgram, if is different send new one into GL, note: must be performed in correct GL thread
     * @param program gl shader glid (Shader.glProgram)
     * */
    public static void useProgram(int program){

        if(program != currentProgram) {
            GLES20.glUseProgram(program);
            currentProgram = program;
        }
    }

    /**
     * check current glProgram, if is different send new one into GL, note: must be performed in correct GL thread
     * @param shader Shader
     * */
    public static void useProgram(BaseShader shader){

        if(shader.glProgram != currentProgram) {
            useProgram(shader.glProgram);
            currentProgram = shader.glProgram;
        }
    }

    public static int getCurrentProgram(){

        return currentProgram;
    }

    /**
     * active gl texture unit at given position into GL
     * @param index gl active texture indexed -> (GL_TEXTURE0 = 0, GL_TEXTURE1 = 1, etc.)
     * */
    public static void activeTexture(int index){

        GLES20.glActiveTexture(glTPos[index].position);
    }

    /**
     * active gl texture unit at given position into GL
     * @param index gl active texture indexed -> (GL_TEXTURE0 = 0, GL_TEXTURE1 = 1, etc.)
     * @param handle shader handle index (Shader.handle[index])
     * */
    public static void activeTexture(int index, int handle){

        GLES20.glActiveTexture(glTPos[index].position);
        GLES20.glUniform1i(handle, index);
    }

    /**
     * active gl texture unit at given position into GL
     * @param index gl active texture position indexed -> (GL_TEXTURE0 = 0, GL_TEXTURE1 = 1, etc.)
     * @param handle shader handle index (Shader.handle[index])
     * @param sampler texture position for shader sampler (typically same as index)
     * */
    public static void activeTexture(int index, int handle, int sampler){

        GLES20.glActiveTexture(glTPos[index].position);
        GLES20.glUniform1i(handle, sampler);
    }

    /**
     * check current texture, if is different bind new one into GL, note: must be performed in correct GL thread
     * @param glid gl texture id (Texture.glid)
     * */
    public static void bindTexture(int glid){

        glTPos[0].bind(glid);
    }

    /** binds texture GL unit at current position, note: must be performed in correct GL thread */
    public static void bindTexture(Texture texture){

        glTPos[0].bind(texture.glid);
    }

    /**
     * bind texture into gl at specific position, note: must be performed in correct GL thread
     * from 0 to getTextureMaxCount()
     * @param glid gl texture id (Texture.glid)
     * @param index gl active texture indexed -> (GL_TEXTURE0 = 0, GL_TEXTURE1 = 1, etc.)
     * */
    public static void bindTexture(int glid, int index){

        glTPos[index].bind(glid);
    }

    /**
     * bind texture into gl at specific position, note: must be performed in correct GL thread
     * from 0 to getTextureMaxCount()
     * @param texture gl texture id (Texture.glid)
     * @param index gl active texture indexed -> (GL_TEXTURE0 = 0, GL_TEXTURE1 = 1, etc.)
     * */
    public static void bindTexture(Texture texture, int index){

        glTPos[index].bind(texture.glid);
    }

    /**
     * binds texture GL unit at specific position for specific shader handle and sampler index,
     * note: must be performed in correct GL thread
     * index from 0 to getTextureMaxCount()
     * @param glid gl texture id (Texture.glid)
     * @param index gl active texture position indexed -> (GL_TEXTURE0 = 0, GL_TEXTURE1 = 1, etc.), sampler id = index
     * @param handle shader handle index (Shader.handle[index])
     * */
    public static void bindTexture(int glid, int index, int handle){

        glTPos[index].bind(glid);
        GLES20.glUniform1i(handle, index);
    }

    /**
     * binds texture GL unit at specific position for specific shader handle and sampler index,
     * note: must be performed in correct GL thread
     * index from 0 to getTextureMaxCount()
     * @param glid gl texture id (Texture.glid)
     * @param index gl active texture position indexed -> (GL_TEXTURE0 = 0, GL_TEXTURE1 = 1, etc.)
     * @param handle shader handle index (Shader.handle[index])
     * @param sampler texture position for shader sampler (typically same as index)
     * */
    public static void bindTexture(int glid, int index, int handle, int sampler){

        glTPos[index].bind(glid);
        GLES20.glUniform1i(handle, sampler);
    }

    /**
     * generate custom GL buffer, note: must be performed in correct GL thread
     * @param buffer System buffer
     * @param bufferType ARRAY, ELEMENT
     * @param bytesPerUnit number of bytes per element (FLOAT - 4, SHORT - 2, Buffers.BYTESPER*)
     * @param usage STATIC, DYNAMIC, STREAM
     * */
    public static int genBuffer(Buffer buffer, int bufferType, int bytesPerUnit, int usage){

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
     * @param usage STATIC, DYNAMIC, STREAM
     * */
    public static int genArrayFloatBuffer(FloatBuffer buffer, int usage){

        return genBuffer(buffer, GLES20.GL_ARRAY_BUFFER, Buffers.BYTESPERFLOAT, usage);
    }

    /** generate static GL float buffer, note: must be performed in correct GL thread */
    public static int genArrayFloatBuffer(FloatBuffer buffer){

        return genBuffer(buffer, GLES20.GL_ARRAY_BUFFER, Buffers.BYTESPERFLOAT, GLES20.GL_STATIC_DRAW);
    }

    /** generate static GL short buffer, note: must be performed in correct GL thread */
    public static int genElementShortBuffer(ShortBuffer buffer){

        return genBuffer(buffer, GLES20.GL_ELEMENT_ARRAY_BUFFER, Buffers.BYTESPERSHORT, GLES20.GL_STATIC_DRAW);
    }

    /** delete GL buffer, note: must be performed in correct GL thread */
    public static void destroyBuffer(int bufferID){

        GLES20.glDeleteBuffers(1, new int[]{bufferID}, 0);
    }

    /** delete all GL buffers, note: must be performed in correct GL thread */
    public static void destroyBuffers(int... bufferIDs){

        if(bufferIDs != null) {
            GLES20.glDeleteBuffers(bufferIDs.length, bufferIDs, 0);
        }
    }

    /**
     * asks gl for image max size, which can be bound into gl, depends on device SW HW
     * typically 2048px, note: must be performed in correct GL thread
     * @return size in pixels
     * */
    public static int getTextureMaxSize(){

        int[] maxSize = new int[1];
        GLES20.glGetIntegerv(GLES20.GL_MAX_TEXTURE_SIZE, maxSize, 0);
        return maxSize[0];
    }

    /**
     * asks gl for maximum number of textures bound into gl in same time, note: must be performed in correct GL thread
     * @return maximum number of binded textures in same time
     * */
    public static int getTextureMaxCombined(){

        int[] maxCount = new int[1];
        GLES20.glGetIntegerv(GLES20.GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS, maxCount, 0);
        return maxCount[0];
    }

    /** reads data from framebuffer and creates a bitmap ([0,0] = center)*/
    public static Bitmap getScreen(int x, int y, int width, int height){

        int screenSize = width * height;
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(screenSize*4).order(ByteOrder.nativeOrder());
        GLES20.glPixelStorei(GLES20.GL_PACK_ALIGNMENT, 1);
        GLES20.glReadPixels(glViewWidth/2 - width/2 + x, glViewHeight/2 - height/2 + y, width, height, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, byteBuffer);

        int pixelsBuffer[] = new int[screenSize];
        byteBuffer.asIntBuffer().get(pixelsBuffer);
        byteBuffer = null;

        for (int i = 0; i < screenSize; ++i) {
            // The alpha and green channels' positions are preserved while the red and blue are swapped
            pixelsBuffer[i] = ((pixelsBuffer[i] & 0xff00ff00)) | ((pixelsBuffer[i] & 0x000000ff) << 16) | ((pixelsBuffer[i] & 0x00ff0000) >> 16);
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixelsBuffer, screenSize-width, -width, 0, 0, width, height);

        return bitmap;
    }

    /** checks for GL Context */
    public static boolean isGLContextCreated(){

        return !((EGL10) EGLContext.getEGL()).eglGetCurrentContext().equals(EGL10.EGL_NO_CONTEXT);
    }

    /** checks if current thread is GLThread */
    public static boolean isOnGLThread(){

        return glThead != null && Thread.currentThread() == glThead;
    }

    /** listen application life cycle and GL Context state */
    public static void addGLEndListener(GLStateListener listener){

        glends.add(listener);
    }

    /** listen application life cycle and GL Context state */
    public static void removeGLEndListener(GLStateListener listener){

        glends.remove(listener);
    }

    public static void useBaseShader(){

        useProgram(baseShader.glProgram);
    }

    public static void bindBaseTexture(){

        bindTexture(baseTexture.glid);
    }

    public static Texture getBaseTexture() {
        return baseTexture;
    }

    public static BaseShader getBaseShader() {
        return baseShader;
    }

    public static List<BaseShader> getShaders() {
        return shaders;
    }

    public static List<Texture> getTextures() {
        return textures;
    }

    /** remove all textures from gl */
    public static void destroyTextures(){

        for(Texture texture : textures){
            texture.removeFromGL();
        }

        textures.clear();
    }

    /** destroy textures shaders and other GL resources */
    public static void destroy(){

        useProgram(0);
        BaseDraw.destroy();
        BaseShader.collection = 0;

        for(GLStateListener glend : glends){
            glend.onGLEnd();
        }

        for(BaseShader shader : shaders){
            shader.delete();
        }

        for(Texture texture : textures){
            texture.removeFromGL();
        }

        textures.clear();
        shaders.clear();
        glends.clear();
        textures = null;
        shaders = null;
        glends = null;


        GLES20.glFlush();
        GLES20.glFinish();

        GLCreated = false;

        Base.logI("BaseGL destroyed");
    }

    //------------------------- GL OVERRIDES -------------------------//  //todo thread check ?

    public static void glClear(){

        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
    }

    public static void setDepthFuncLequal(){

        GLES20.glDepthFunc(GLES20.GL_LEQUAL);
    }

    public static void setDepthFuncLess(){

        GLES20.glDepthFunc(GLES20.GL_LESS);
    }

    public static void setDepthFuncGreater(){

        GLES20.glDepthFunc(GLES20.GL_GREATER);
    }

    public static void clearStencilBuffer(){

        GLES20.glClear(GLES20.GL_STENCIL_BUFFER_BIT);
    }

    public static void enableStencil(){

        GLES20.glEnable(GLES20.GL_STENCIL_TEST);
    }

    public static void disableStencil(){

        GLES20.glDisable(GLES20.GL_STENCIL_TEST);
    }

    public static void setStencilClear(int s){

        GLES20.glClearStencil(s);
    }

    public static void setDepthClear(float depth){

        GLES20.glClearDepthf(depth);
    }

    @Deprecated
    public static void enableTextureMapping(){ //throws invalid enum

        GLES20.glEnable(GLES20.GL_TEXTURE_2D);
    }

    @Deprecated
    public static void disableTextureMapping(){ //throws invalid enum

        GLES20.glDisable(GLES20.GL_TEXTURE_2D);
    }

    public static void enableTransparency(){

        GLES20.glEnable(GLES20.GL_BLEND);
    }

    public static void disableTransparency(){

        GLES20.glDisable(GLES20.GL_BLEND);
    }

    public static void enableDepthTest(){

        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glDepthMask(true);
    }

    public static void disableDepthTest(){

        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        GLES20.glDepthMask(false);
    }

    public static void enableCulling(){

        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glCullFace(GLES20.GL_BACK);
    }

    public static void disableCulling(){

        GLES20.glDisable(GLES20.GL_CULL_FACE);
    }

    public static void enableDithering(){

        GLES20.glEnable(GLES20.GL_DITHER);
    }

    public static void disbaleDithering(){

        GLES20.glDisable(GLES20.GL_DITHER);
    }

    public static void setClearColor(float r, float g, float b, float a){

        GLES20.glClearColor(r, g, b, a);
    }

    public static void setClearColor(Colorf color){

        setClearColor(color.r, color.g, color.b, color.a);
    }

    private static class GLTPos {

        int glid = -1;
        int position = -1;

        void bind(int nglid){

            GLES20.glActiveTexture(position);
            if(glid != nglid){
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, nglid);
                glid = nglid;
            }
        }
    }
}


