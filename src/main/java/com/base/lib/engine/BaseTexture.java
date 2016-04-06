package com.base.lib.engine;

import android.graphics.Bitmap;
import android.opengl.GLES20;

import com.base.lib.engine.common.BitmapHelper;
import com.base.lib.engine.common.file.FileHelper;
import com.base.lib.interfaces.GLPoolRunnable;
import com.base.lib.engine.common.gl.TextureHelper;

/**
 * holds basic informations about texture.
 */
public class BaseTexture implements GLPoolRunnable<Bitmap> {

    private static final Options baseTextureOptions = new Options();

    public int glid;

    private String name;
    private Type loadingType;
    private String loadingPath;
    private final BaseGL gl;

    private Options options = baseTextureOptions;

    public BaseTexture(BaseGL gl) {
        this.gl = gl;

        this.name = "texture_" ; //// TODO: 18. 2. 2016
        loadingType = Type.OTHER;
    }

    public BaseTexture(BaseGL gl, String name, Bitmap bitmap) {
        this.gl = gl;

        this.name = name;
        load(bitmap);
    }

    public BaseTexture(BaseGL gl, String name, byte[] bytes) {
        this.gl = gl;

        this.name = name;
        load(bytes);
    }

    public BaseTexture(BaseGL gl, int resource) {
        this.gl = gl;

        this.name = Base.appContext.getResources().getResourceEntryName(resource);
        load(resource);
    }

    public BaseTexture(BaseGL gl, String path, Type storage) {
        this.gl = gl;

        this.name = path.substring(path.lastIndexOf('/') + 1, path.lastIndexOf('.'));
        load(path, storage);
    }

    public void load(final Bitmap bitmap) {

        loadingType = Type.OTHER;
        gl.glRun(new Runnable() {
            @Override
            public void run() {
                glRun(bitmap);
            }
        });
    }

    public void load(final byte[] bytes) {

        loadingType = Type.OTHER;
        gl.glTask(new Runnable() {
            @Override
            public void run() {
                final Bitmap bitmap = BitmapHelper.loadBitmap(bytes);
                gl.glRun(new Runnable() {
                    @Override
                    public void run() {
                        glRun(bitmap);
                    }
                });
            }
        });
    }

    public void load(int resourceFile) {

        loadingType = Type.STORAGE_RESOURCE;
        loadingPath = Integer.toString(resourceFile);

        gl.glTask(this);
    }

    public void load(String path, Type storage) {

        loadingType = storage;
        loadingPath = path;

        gl.glTask(this);
    }

    @Override
    public Bitmap run() {

        switch (loadingType) {
            case STORAGE_RESOURCE:
                return BitmapHelper.loadBitmap(Integer.parseInt(loadingPath));
            case STORAGE_ASSETS:
                return BitmapHelper.loadBitmap(loadingPath);
            case STORAGE_INTERNAL:
                return BitmapHelper.loadBitmap(FileHelper.loadInternal(loadingPath));
            case STORAGE_SDCARD:
                return BitmapHelper.loadBitmap(FileHelper.sdReadFile(loadingPath));
            default:
                return null;
        }
    }

    @Override
    public void glRun(Bitmap bitmap) {

        if (bitmap == null) {
            return;
        }

        if (glid == 0) {
            glid = TextureHelper.loadTexture(bitmap, options);
        } else {
            TextureHelper.changeTexture(glid, bitmap, options);
        }

        if (!bitmap.isRecycled()) {
            bitmap.recycle();
        }
    }

    public void reload() {

        gl.glTask(this);
    }

    public void bind() {

        BaseGL.bindTexture(glid);
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public void delete() {

        removeFromGL();
    }

    void removeFromGL() {

        gl.glTask(new Runnable() {
            @Override
            public void run() {
                TextureHelper.deleteTexture(glid);
                glid = 0;
            }
        });
    }

    @Override
    public String toString() {

        return name + "  GL: " + glid;
    }

    public void setMinFilterXRepeat() {
        options.wrap_s = GLES20.GL_REPEAT;
    }

    public void setMagFilterYRepeat() {
        options.wrap_t = GLES20.GL_REPEAT;
    }

    public static class Options {

        public int min_filter = GLES20.GL_LINEAR_MIPMAP_NEAREST;
        public int mag_filter = GLES20.GL_LINEAR;
        public int wrap_s = GLES20.GL_REPEAT;
        public int wrap_t = GLES20.GL_REPEAT;
        public boolean mipmap = true;
    }
}
