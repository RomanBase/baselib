package com.base.lib.engine.builders;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.RawRes;

import com.base.lib.engine.Base;
import com.base.lib.engine.BaseObject;
import com.base.lib.engine.BaseShader;
import com.base.lib.engine.BaseTexture;
import com.base.lib.engine.Type;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class BaseFactory extends BaseObject {

    final Map<String, BaseTexture> textures;
    final Map<String, BaseShader> shaders;

    public final Generator gen;

    public BaseFactory(Base base) {
        super(base);

        textures = new HashMap<>();
        shaders = new HashMap<>();
        gen = new Generator(base);
    }

    //checks for texture stored in hashmap
    private BaseTexture getStoredTexture(String name) {

        BaseTexture texture = textures.get(name);
        if (texture == null) {
            int dot = name.lastIndexOf(".");
            if (dot > 0) {
                String entryName = name.substring(0, dot);
                texture = textures.get(entryName);
            }
        }

        return texture;
    }

    public BaseTexture getTexture(String name) {

        BaseTexture texture = getStoredTexture(name);

        if (texture == null) {
            texture = gen.texture(name);
        }

        return texture;
    }

    public BaseTexture getTexture(Object name, Type storage) {

        BaseTexture texture = null;

        if (name instanceof String) {
            texture = getStoredTexture(name.toString());

            if (texture != null) {
                return texture;
            }
        }

        switch (storage) {
            case STORAGE_ASSETS:
                texture = gen.textureAssets(name.toString());
                break;
            case STORAGE_RESOURCE:
                if (name instanceof Integer) {
                    int resource = (int) name;
                    texture = textures.get(base.context.getResources().getResourceEntryName(resource));
                    if (texture == null) {
                        texture = gen.textureResources((int) name);
                    }
                } else {
                    texture = gen.textureResources(base.context.getResources().getIdentifier(name.toString(), "drawable", base.context.getPackageName()));
                }
                break;
            case STORAGE_SDCARD:
                texture = gen.textureSD(name.toString());
                break;
            case STORAGE_INTERNAL:
                texture = gen.textureInternal(name.toString());
                break;
        }

        return texture;
    }

    public BaseShader getShader(String name) {

        BaseShader shader = shaders.get(name);
        if (shader == null) {
            shader = gen.shader(name);
        }

        return shader;
    }

    public void removeTexture(String name) {

        BaseTexture texture = textures.remove(name);
        if (texture != null && texture.glid > 0) {
            texture.delete();
        }
    }

    public void removeShader(String name) {

        BaseShader shader = shaders.remove(name);
        if (shader != null && shader.glid > 0) {
            shader.delete();
        }
    }

    public void clearTextures() {

        for (BaseTexture texture : textures.values()) {
            if (texture.glid > 0) {
                texture.delete();
            }
        }

        textures.clear();
    }

    public void clearShaders() {

        for (BaseShader shader : shaders.values()) {
            if (shader.glid > 0) {
                shader.delete();
            }
        }

        shaders.clear();
    }

    public Collection<BaseTexture> getTextures() {

        return textures.values();
    }

    public Collection<BaseShader> getShaders() {

        return shaders.values();
    }

    public class Generator extends BaseObject {

        private Type shaderPreferedStorage = Type.STORAGE_RESOURCE;

        public Generator(Base base) {
            super(base);
        }

        public void setShaderPreferedStorage(Type shaderPreferedStorage) {
            this.shaderPreferedStorage = shaderPreferedStorage;
        }

        public BaseTexture textureResources(@DrawableRes int resourcesID) {

            BaseTexture texture = new BaseTexture(base.gl, resourcesID);
            textures.put(texture.getName(), texture);

            return texture;
        }

        public BaseTexture textureAssets(@NonNull String file) {

            BaseTexture texture = new BaseTexture(base.gl, file, Type.STORAGE_ASSETS);
            textures.put(texture.getName(), texture);

            return texture;
        }

        public BaseTexture textureSD(@NonNull String file) {

            BaseTexture texture = new BaseTexture(base.gl, file, Type.STORAGE_SDCARD);
            textures.put(texture.getName(), texture);

            return texture;
        }

        public BaseTexture textureInternal(@NonNull String file) {

            BaseTexture texture = new BaseTexture(base.gl, file, Type.STORAGE_INTERNAL);
            textures.put(texture.getName(), texture);

            return texture;
        }

        public BaseTexture textureBitmap(@NonNull String name, Bitmap bitmap) {

            BaseTexture texture = new BaseTexture(base.gl, name, bitmap);
            textures.put(name, texture);

            return texture;
        }

        public BaseTexture textureBytes(@NonNull String name, byte[] bytes) {

            BaseTexture texture = new BaseTexture(base.gl, name, bytes);
            textures.put(name, texture);

            return texture;
        }

        public BaseShader shaderResource(@NonNull String name, @RawRes int vertexShader, @RawRes int fragmentShader, String... attrs) {

            BaseShader shader = new BaseShader(base.gl, name, attrs);
            shader.loadShadersFromResources(vertexShader, fragmentShader);
            shaders.put(name, shader);

            return shader;
        }

        public BaseShader shaderAssets(@NonNull String name, String vertexShader, String fragmentShader, String... attrs) {

            BaseShader shader = new BaseShader(base.gl, name, attrs);
            shader.loadShadersFromAssets(vertexShader, fragmentShader);
            shaders.put(name, shader);

            return shader;
        }

        public BaseShader shaderSD(@NonNull String name, String vertexShader, String fragmentShader, String... attrs) {

            BaseShader shader = new BaseShader(base.gl, name, attrs);
            shader.loadShadersFromSDCard(vertexShader, fragmentShader);
            shaders.put(name, shader);

            return shader;
        }

        public BaseShader shaderSource(String name, String vertexShader, String fragmentShader, String... attrs) {

            BaseShader shader = new BaseShader(base.gl, name, attrs);
            shader.setShaderSourceCode(vertexShader, fragmentShader);
            shaders.put(name, shader);

            return shader;
        }

        public BaseTexture texture(String name) { //// TODO: 3. 2. 2016

            int dot = name.lastIndexOf(".");
            BaseTexture texture = null;

            if (dot > 0) {
                texture = textureAssets(name);
            }

            if (texture == null) {
                String resName = name;
                if (dot > 0) {
                    resName = name.substring(0, dot);
                }
                int resource = base.context.getResources().getIdentifier(resName, "drawable", base.context.getPackageName());
                if (resource > 0) {
                    texture = textureResources(resource);
                }
            }

            if (texture == null) {
                texture = textureSD(name);
            }

            if (texture == null) {
                texture = textureInternal(name);
            }

            if (texture != null) {
                textures.put(texture.getName(), texture);
            }

            return texture;
        }

        public BaseShader shader(String name) {

            String vert = name + "_vert";
            String frag = name + "_frag";

            BaseShader shader = null;

            switch (shaderPreferedStorage) {
                case STORAGE_ASSETS:
                    shader = shaderAssets(name, vert + ".glsl", frag + ".glsl");
                    break;
                case STORAGE_RESOURCE:
                    Context context = base.context;
                    shader = shaderResource(name, context.getResources().getIdentifier(name, "raw", context.getPackageName()), context.getResources().getIdentifier(name, "raw", context.getPackageName()));
                    break;
                case STORAGE_SDCARD:
                    shader = shaderSD(name, vert + ".glsl", frag + ".glsl");
                    break;
            }

            if (shader != null) {
                shaders.put(name, shader);
            }

            return shader;
        }
    }
}
