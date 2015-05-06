package com.base.lib.engine;

import android.graphics.Bitmap;
import android.opengl.GLES20;

import com.base.lib.engine.common.BitmapHelper;
import com.base.lib.engine.common.FileHelper;
import com.base.lib.engine.glcommon.TextureHelper;

/**
 * holds basic informations about texture.
 */
public class Texture { //todo loading on separate thead. (loadingType/Path and bitmap ?)

    public int glid;
    private String name;
    private Bitmap bitmap;

    private Type loadingType;
    private String loadingPath;

    public Texture(){

        this.name = "texture_"+BaseGL.textures.size()+"_"+BaseTime.appTime();
        loadingType = Type.OTHER;
    }

    public Texture(String name){

        this.name = name;
        loadingType = Type.OTHER;
    }

    public Texture(String name, Bitmap bitmap){

        this.name = name;
        this.bitmap = bitmap;
        loadingType = Type.OTHER;

        addToBaseGL();
    }

    public Texture(String name, byte[] bytes){

        this.name = name;
        this.bitmap = BitmapHelper.loadBitmap(bytes);
        loadingType = Type.OTHER;

        addToBaseGL();
    }

    public Texture(int resource){

        name = Base.context.getResources().getResourceEntryName(resource);
        loadingType = Type.STORAGE_RESOURCE;
        loadingPath = Integer.toString(resource);

        addToBaseGL();
    }

    public Texture(String path, Type storage){

        name = path.substring(path.lastIndexOf('/')+1, path.lastIndexOf('.'));
        loadingType = storage;
        loadingPath = path;

        addToBaseGL();
    }

    private void addToBaseGL(){

        BaseGL.textures.add(this);
        createGLTexture();
    }

    public void load(Bitmap bitmap){

        this.bitmap = bitmap;
        loadingType = Type.OTHER;

        createGLTexture();
    }

    public void load(byte[] bytes){

        bitmap = BitmapHelper.loadBitmap(bytes);
        loadingType = Type.OTHER;

        createGLTexture();
    }

    public void load(int resourceFile){

        loadingType = Type.STORAGE_RESOURCE;
        loadingPath = Integer.toString(resourceFile);

        createGLTexture();
    }

    public void load(String path, Type storage){

        loadingType = storage;
        loadingPath = path;

        createGLTexture();
    }

    protected void createGLTexture() {

            if (!BaseGL.isOnGLThread()) {
                Base.render.glQueueEvent(new Runnable() {
                    @Override
                    public void run() {
                        createGLTexture();
                    }
                });
            } else {
                switch (loadingType) {
                    case STORAGE_RESOURCE: bitmap = BitmapHelper.loadBitmap(Integer.parseInt(loadingPath)); break;
                    case STORAGE_ASSETS: bitmap = BitmapHelper.loadBitmap(loadingPath); break;
                    case STORAGE_INTERNAL: bitmap = BitmapHelper.loadBitmap(FileHelper.loadInternal(loadingPath)); break;
                    case STORAGE_SDCARD: bitmap = BitmapHelper.loadBitmap(FileHelper.sdReadFile(loadingPath)); break;
                    default:
                        if (bitmap == null || bitmap.isRecycled()) {
                            return;
                        }
                }

                if (glid == 0) {
                    glid = TextureHelper.loadTexture(bitmap);
                } else {
                    TextureHelper.changeTexture(glid, bitmap);
                }

                if(!bitmap.isRecycled()){
                    bitmap.recycle();
                }

                bitmap = null;
            }

    }

    public void reload(){

        createGLTexture();
    }

    public void bind(){

        BaseGL.bindTexture(glid);
    }

    public int getGlid(){

        return glid;
    }

    public void setGlid(int glid){

        this.glid = glid;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public void delete(){

        removeFromGL();
        BaseGL.textures.remove(this);
    }

    void removeFromGL(){

        if(BaseGL.isOnGLThread()) {
            TextureHelper.deleteTexture(glid);
            glid = 0;
        } else {
            Base.render.glQueueEvent(new Runnable() {
                @Override
                public void run() {
                    TextureHelper.deleteTexture(glid);
                    glid = 0;
                }
            });
        }
    }

    public static Texture get(String name){

        for(Texture texture : BaseGL.textures){
            if(texture.name.equals(name)){
                return texture;
            }
        }

        return BaseGL.textures.get(0);
    }

    public static Texture get(int index){

        return BaseGL.textures.get(index);
    }

    @Override
    public String toString() {

        return name + "  GL: " + glid;
    }

    //static constructors
    public static Texture resources(int resourcesID){

        return new Texture(resourcesID);
    }

    public static Texture assets(String file){

        return new Texture(file, Type.STORAGE_ASSETS);
    }

    public static Texture sd(String file){

        return new Texture(file, Type.STORAGE_SDCARD);
    }

    public static Texture internal(String file){

        return new Texture(file, Type.STORAGE_INTERNAL);
    }

    public static Texture bitmap(String name, Bitmap bitmap){

        return new Texture(name, bitmap);
    }

    public static Texture bytes(Object name, byte[] bytes){

        return new Texture(name.toString(), bytes);
    }

    public static void glMinFilter(int param){

        TextureHelper.MIN_FILTER = param;
    }

    public static void glMagFilter(int param){

        TextureHelper.MAG_FILTER = param;
    }

    public static void glWrapS(int param){

        TextureHelper.WRAP_S = param;
    }

    public static void glWrapT(int param){

        TextureHelper.WRAP_T = param;
    }

    public static void glGenMipmap(boolean param){

        TextureHelper.MIPMAP = param;
    }

    public static void glNoMipmap(){

        TextureHelper.MIPMAP = false;
        TextureHelper.MIN_FILTER = GLES20.GL_LINEAR;
        TextureHelper.MAG_FILTER = GLES20.GL_LINEAR;
    }

    public static void glGenMipmap(){

        TextureHelper.MIPMAP = true;
        TextureHelper.MIN_FILTER = GLES20.GL_LINEAR_MIPMAP_NEAREST;
        TextureHelper.MAG_FILTER = GLES20.GL_LINEAR;
    }

    public static void glWrapRepeat(){

        TextureHelper.WRAP_S = GLES20.GL_REPEAT;
        TextureHelper.WRAP_T = GLES20.GL_REPEAT;
    }

    public static void glWrapRepeatX(){

        TextureHelper.WRAP_S = GLES20.GL_REPEAT;
        TextureHelper.WRAP_T = GLES20.GL_CLAMP_TO_EDGE;
    }

    public static void glWrapRepeatY(){

        TextureHelper.WRAP_S = GLES20.GL_CLAMP_TO_EDGE;
        TextureHelper.WRAP_T = GLES20.GL_REPEAT;
    }

    public static void glWrapToEdge(){

        TextureHelper.WRAP_S = GLES20.GL_CLAMP_TO_EDGE;
        TextureHelper.WRAP_T = GLES20.GL_CLAMP_TO_EDGE;
    }
}
