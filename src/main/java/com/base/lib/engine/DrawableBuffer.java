package com.base.lib.engine;

import android.opengl.GLES20;

import com.base.lib.engine.common.BaseDrawableData;
import com.base.lib.engine.common.Buffers;
import com.base.lib.interfaces.GLStateListener;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * 09 Created by doctor on 11.10.13.
 */
public class DrawableBuffer extends BaseRenderable implements GLStateListener { //todo refactor

    private float sizeX;
    private float sizeY;
    private float sizeZ;

    private FloatBuffer verticeBuffer;
    private FloatBuffer textureBuffer;
    private ShortBuffer faceBuffer;
    private int faceCount;

    private int cpv = 3;
    private int cpt = 2;

    private int glDrawMode = GLES20.GL_TRIANGLES;

    protected List<DrawableModel> models;

    public DrawableBuffer(){

    }

    public DrawableBuffer(BaseDrawableData data){

        init(data);
    }

    public void init(BaseDrawableData data){

        init(data.vertices, data.textures, data.faceOrder);
        sizeX = data.sizeX;
        sizeY = data.sizeY;
        sizeZ = data.sizeZ;
        cpv = data.cpv;
    }

    private void init(float[] vertices, float[] textures, short[] faces){

        verticeBuffer = Buffers.floatBuffer(vertices);
        if(textures != null) textureBuffer = Buffers.floatBuffer(textures);
        faceBuffer = Buffers.shortBuffer(faces);
        faceCount = faces.length;
    }

    public void setFaceCount(int count){

        faceCount = count;
    }

    public void setCoordsPerVertice(int cpv){

        this.cpv = cpv;
    }

    public void setCoordsPerColor(int cpt){

        this.cpt = cpt;
    }

    public int getCpv() {

        return cpv;
    }

    public int getCpt() {

        return cpt;
    }

    public void setVerticeBuffer(FloatBuffer buffer){

        verticeBuffer = buffer;
    }

    public void setTextureBuffer(FloatBuffer buffer){

        textureBuffer = buffer;
    }

    public void setFaceBuffer(ShortBuffer buffer){

        faceBuffer = buffer;
    }

    public void putVertices(float[] vertices){

        verticeBuffer.put(vertices).position(0);
    }

    public void putTextureCoords(float[] coords){

        textureBuffer.put(coords).position(0);
    }

    public int getGlDrawMode() {

        return glDrawMode;
    }

    public void setGlDrawMode(int glDrawMode) {

        this.glDrawMode = glDrawMode;
    }

    public VBO asVBO(){

        return asVBO(Type.VBO_STATIC);
    }

    public VBO asVBO(final Type type){

        final VBO vbo = new VBO();

        if(BaseGL.isOnGLThread()) {
            vbo.glGenBuffers(type);
        } else {
            Base.render.glQueueEvent(new Runnable() {
                @Override
                public void run() {
                    vbo.glGenBuffers(type);
                }
            });
        }

        return vbo;
    }

    public void glUseProgram(){
        BaseGL.useProgram(shader.glProgram);
    }

    public void glPutVerticeBuffer(){
        verticeBuffer.position(0);
        GLES20.glVertexAttribPointer(shader.handle[1], cpv, GLES20.GL_FLOAT, false, 0, verticeBuffer);
        GLES20.glEnableVertexAttribArray(shader.handle[1]);
    }

    public void glPutVerticeBuffer(FloatBuffer verticeBuffer){
        verticeBuffer.position(0);
        GLES20.glVertexAttribPointer(shader.handle[1], cpv, GLES20.GL_FLOAT, false, 0, verticeBuffer);
        GLES20.glEnableVertexAttribArray(shader.handle[1]);
    }

    public void glPutTextureBuffer(){
        textureBuffer.position(0);
        GLES20.glVertexAttribPointer(shader.handle[2], cpt, GLES20.GL_FLOAT, false, 0, textureBuffer);
        GLES20.glEnableVertexAttribArray(shader.handle[2]);
    }

    public void glPutTextureBuffer(FloatBuffer textureBuffer){
        textureBuffer.position(0);
        GLES20.glVertexAttribPointer(shader.handle[2], cpt, GLES20.GL_FLOAT, false, 0, textureBuffer);
        GLES20.glEnableVertexAttribArray(shader.handle[2]);
    }

    public void glPutDraw(){
        faceBuffer.position(0);
        GLES20.glDrawElements(glDrawMode, faceCount, GLES20.GL_UNSIGNED_SHORT, faceBuffer);
    }

    public void glPutMVPMatrix(float[] MVPMatrix){
        GLES20.glUniformMatrix4fv(shader.handle[0], 1, false, MVPMatrix, 0);
    }

    public void glBindTexture(int textureID){
        BaseGL.bindTexture(textureID);
    }

    public void glDisableAttribArray(){
        GLES20.glDisableVertexAttribArray(shader.handle[1]);
        GLES20.glDisableVertexAttribArray(shader.handle[2]);
    }

    public Collection<DrawableModel> getModels(){
        return Collections.unmodifiableCollection(models);
    }

    public void add(DrawableModel model){

        if(models == null){
            models = Collections.synchronizedList(new ArrayList<DrawableModel>());
        }

        model.sizeX = sizeX;
        model.sizeY = sizeY;
        model.sizeZ = sizeZ;
        model.camera = camera;

        synchronized (this){
            models.add(model);
        }
    }

    public void add(DrawableModel[] models){

        for(DrawableModel model : models){
            add(model);
        }
    }

    public void remove(DrawableModel model){

        synchronized (this){
            models.remove(model);
        }
    }

    public synchronized void clear(boolean destroy){

        Iterator<DrawableModel> iterator = models.iterator();
        while (iterator.hasNext()){
            DrawableModel model = iterator.next();
            iterator.remove();
            if(destroy){
                model.unUse();
                model.destroy();
                model = null;
            }
        }
    }

    public synchronized void update() {

        Iterator<DrawableModel> iterator = models.iterator();
        while (iterator.hasNext()) {
            DrawableModel model = iterator.next();
            if (model.inUse) {
                model.update();
            } else {
                iterator.remove();
                model.destroy();
                model = null;
            }
        }
    }

    public synchronized void draw() {

        glPutTextureBuffer();
        glPutVerticeBuffer();
        for (DrawableModel model : models) {
            bindTexturePutMVPMatrix(model);
            glPutDraw();
        }
        glDisableAttribArray();
    }

    public void draw(DrawableModel model) {

        glPutTextureBuffer();
        glPutVerticeBuffer();
        bindTexturePutMVPMatrix(model);
        glPutDraw();
        glDisableAttribArray();
    }

    public void drawPutModel(DrawableModel model){

        bindTexturePutMVPMatrix(model);
        glPutDraw();
    }

    protected void bindTexturePutMVPMatrix(DrawableModel model){
        glBindTexture(model.texture.glid);
        glPutMVPMatrix(model.MVPMatrix);
    }

    public float getSizeX() {
        return sizeX;
    }

    public float getSizeY() {
        return sizeY;
    }

    public float getSizeZ() {
        return sizeZ;
    }

    public FloatBuffer getVerticeBuffer() {
        return verticeBuffer;
    }

    public ShortBuffer getFaceBuffer() {
        return faceBuffer;
    }

    public FloatBuffer getTextureBuffer() {
        return textureBuffer;
    }

    @Override
    public void destroy() {

        for(DrawableModel model : models){
            model.destroy();
        }
    }

    @Override
    public void onGLCreate() {

    }

    @Override
    public void onGLEnd() {

    }

    public class VBO extends DrawableBuffer {

        private int glVerticeBufferID;
        private int glTextureBufferID;
        private int glFaceBufferID;

        protected void glGenBuffers(Type type){

            int vBuf = 0;
            int tBuf = 0;

            switch (type){
                case VBO_STATIC:
                    vBuf = GLES20.GL_STATIC_DRAW;
                    tBuf = GLES20.GL_STATIC_DRAW;
                    break;
                case VBO_VERTICES_DYNAMIC:
                    vBuf = GLES20.GL_DYNAMIC_DRAW;
                    tBuf = GLES20.GL_STATIC_DRAW;
                    break;
                case VBO_TEXTURES_DYNAMIC:
                    vBuf = GLES20.GL_STATIC_DRAW;
                    tBuf = GLES20.GL_DYNAMIC_DRAW;
                    break;
                case VBO_DYNAMIC:
                    vBuf = GLES20.GL_DYNAMIC_DRAW;
                    tBuf = GLES20.GL_DYNAMIC_DRAW;
                    break;
            }

            BaseGL.addGLEndListener(this);

            glVerticeBufferID = BaseGL.genArrayFloatBuffer(verticeBuffer, vBuf);
            glTextureBufferID = BaseGL.genArrayFloatBuffer(textureBuffer, tBuf);
            glFaceBufferID = BaseGL.genElementShortBuffer(faceBuffer);

            verticeBuffer = null;
            textureBuffer = null;
            faceBuffer = null;
        }

        public void glBindVerticeBuffer(){

            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, glVerticeBufferID);
            GLES20.glVertexAttribPointer(shader.handle[1], cpv, GLES20.GL_FLOAT, false, 0, 0);
            GLES20.glEnableVertexAttribArray(shader.handle[1]);
        }

        public void glBindTextureBuffer(){

            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, glTextureBufferID);
            GLES20.glVertexAttribPointer(shader.handle[2], cpt, GLES20.GL_FLOAT, false, 0, 0);
            GLES20.glEnableVertexAttribArray(shader.handle[2]);
        }

        public void glBindDraw(){ //glBindFaceBuffer and glDrawBindedElements

            GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, glFaceBufferID);
            GLES20.glDrawElements(glDrawMode, faceCount, GLES20.GL_UNSIGNED_SHORT, 0);
        }

        void glBindFaceBuffer(){

            GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, glFaceBufferID);
        }

        void glDrawBindedElements(){

            GLES20.glDrawElements(glDrawMode, faceCount, GLES20.GL_UNSIGNED_SHORT, 0);
        }

        public void glUnbindBuffer(){

            GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
            GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
        }

        @Override
        public synchronized void draw() {

            glBindTextureBuffer();
            glBindVerticeBuffer();
            glBindFaceBuffer();
            for(DrawableModel model : models){
                bindTexturePutMVPMatrix(model);
                glDrawBindedElements();
            }
            glUnbindBuffer();
            glDisableAttribArray();
        }

        @Override
        public void draw(DrawableModel model) {

            glBindTextureBuffer();
            glBindVerticeBuffer();
            bindTexturePutMVPMatrix(model);
            glBindDraw();
            glUnbindBuffer();
            glDisableAttribArray();
        }

        @Override
        public void onGLCreate() {
            super.onGLCreate();

        }

        @Override
        public void onGLEnd() {
            super.onGLEnd();
            BaseGL.destroyBuffers(glVerticeBufferID, glTextureBufferID, glFaceBufferID);
        }

        @Override
        public void destroy() {

            BaseGL.destroyBuffers(glVerticeBufferID, glTextureBufferID, glFaceBufferID);
            BaseGL.removeGLEndListener(this);

            super.destroy();
        }
    }
}
