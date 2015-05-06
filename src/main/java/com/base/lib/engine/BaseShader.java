package com.base.lib.engine;

import android.opengl.GLES20;

import com.base.lib.engine.common.FileHelper;
import com.base.lib.engine.common.TrainedMonkey;
import com.base.lib.engine.glcommon.ShaderHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * 08 Created by doctor on 1.11.13.
 */
public class BaseShader { //todo redesign (separate class for each predefined shaders)

    protected static int collection;

    public int id;
    public int glProgram;
    public int[] handle;

    private String name;
    private String[] attributes;

    private String vertexShaderCode;
    private String fragmentShaderCode;
    private int vertexShader;
    private int fragmentShader;

    private int collectionSize = 1024;

    public BaseShader(boolean addToCollection, String name, String... atrs) {

        this.name = name;
        setAttributes(atrs);

        if(addToCollection) {
            id = collection++;
        } else {
            id = -1;
        }

        BaseGL.shaders.add(this);
    }

    public BaseShader(String name, String... atrs) {
        this(true, name, atrs);
    }

    public void setAttributes(String... atrs){

        if(atrs != null) {
            attributes = atrs;
            handle = new int[atrs.length];
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getHandle(int index){

        return handle[index];
    }

    public int getHandle(String atr){

        int index = -1;
        for(int i = 0; i<attributes.length; i++){
            if(attributes[i].equals(atr)){
                index = i;
                break;
            }
        }

        if(index == -1){
            Base.logE("Monkey's can't find attribute");
        }

        return handle[index];
    }

    public String[] getAttributes(){

        return attributes;
    }

    public int getAttributesCount(){

        return handle.length;
    }

    public int getShaderId(){

        return id;
    }

    public int getGlProgram(){

        return glProgram;
    }

    public int getVertexShader(){

        return vertexShader;
    }

    public int getFragmentShader(){

        return fragmentShader;
    }

    public void useProgram(){

        BaseGL.useProgram(glProgram);
    }

    public int getCollectionSize() {
        return collectionSize;
    }

    public void setCollectionSize(int collectionSize) {
        this.collectionSize = collectionSize;
    }

    /**
     * read vertex and fragment glsl files and then creates and link shader glProgram
     *
     * @param vertexShaderGLSL   vs resource file
     * @param fragmentShaderGLSL fs resource file
     */
    public BaseShader loadShadersFromResources(int vertexShaderGLSL, int fragmentShaderGLSL) {

        setShaderSourceCode(FileHelper.resourceText(vertexShaderGLSL), FileHelper.resourceText(fragmentShaderGLSL));

        return this;
    }

    /**
     * read vertex and fragment glsl files and then creates and link shader glProgram
     *
     * @param vertexShaderGLSL   vs file path in assets
     * @param fragmentShaderGLSL fs file path in assets
     */
    public BaseShader loadShadersFromAssets(String vertexShaderGLSL, String fragmentShaderGLSL) {

        setShaderSourceCode(FileHelper.loadInternalText(vertexShaderGLSL), FileHelper.loadInternalText(fragmentShaderGLSL));

        return this;
    }

    /**
     * read vertex and fragment glsl files and then creates and link shader glProgram
     *
     * @param vertexShaderGLSL   vs file path on sd card
     * @param fragmentShaderGLSL fs file path on sd card
     */
    public BaseShader loadShadersFromSDCard(String vertexShaderGLSL, String fragmentShaderGLSL) {

        setShaderSourceCode(FileHelper.sdReadTextFile(vertexShaderGLSL), FileHelper.sdReadTextFile(fragmentShaderGLSL));

        return this;
    }

    public void setShaderSourceCode(String vertexShaderCode, String fragmentShaderCode) {

        this.vertexShaderCode = vertexShaderCode;
        this.fragmentShaderCode = fragmentShaderCode;

        if (BaseGL.isOnGLThread()) {
            createGLProgram();
        } else {
            Base.render.glQueueEvent(new Runnable() {
                @Override
                public void run() {
                    createGLProgram();
                }
            });
        }
    }

    protected void createGLProgram() {

        if (glProgram == 0) {
            List<String> atrs = new ArrayList<String>();
            for (String atr : attributes) {
                if (atr.startsWith("a")) {
                    atrs.add(atr);
                }
            }

            vertexShader = ShaderHelper.compileShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
            BaseGL.glError(name +" vertex");
            fragmentShader = ShaderHelper.compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
            BaseGL.glError(name +" fragment");
            glProgram = ShaderHelper.createAndLinkProgram(vertexShader, fragmentShader, TrainedMonkey.toStringArray(atrs));
            BaseGL.glError(name +" program");

            for (int i = 0; i < attributes.length; i++) {
                switch (attributes[i].charAt(0)) {
                    case 'u':
                        handle[i] = GLES20.glGetUniformLocation(glProgram, attributes[i]);
                        break;
                    case 'a':
                        handle[i] = GLES20.glGetAttribLocation(glProgram, attributes[i]);
                        break;
                    default:
                        handle[i] = GLES20.glGetUniformLocation(glProgram, attributes[i]);
                }
            }

            BaseGL.glError(name +" attribs");
        }
    }

    public void delete() {

        GLES20.glDeleteShader(vertexShader);
        GLES20.glDeleteShader(fragmentShader);
        GLES20.glDeleteProgram(glProgram);
        glProgram = 0;
        vertexShader = 0;
        fragmentShader = 0;
    }

    public static BaseShader get(int i){

        return BaseGL.shaders.get(i);
    }

    public static BaseShader get(String name){

        for(BaseShader shader : BaseGL.shaders){
            if(name.equals(shader.name)){
                return shader;
            }
        }

        return null;
    }

    @Override
    public String toString() {

        return "Shader "+ id +" " + name +"  glid:" + glProgram;
    }

    public static BaseShader textureShader(){

        return get("texture");
    }

    public static BaseShader perVertexColorShader(){

        return get("color");
    }

    public static BaseShader bumpmapShader(){

        return get("bump");
    }

    public static BaseShader mixTextureColorShader(){

        return get("mix");
    }

    public static BaseShader oneColorShader(){

        return get(BaseGL.SHADERS[4]);
    }
}
