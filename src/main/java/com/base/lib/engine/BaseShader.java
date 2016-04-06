package com.base.lib.engine;

import android.opengl.GLES20;

import com.base.lib.engine.common.file.FileHelper;
import com.base.lib.engine.common.gl.ShaderHelper;
import com.base.lib.engine.common.other.TrainedMonkey;

import java.util.ArrayList;
import java.util.List;


public class BaseShader implements Runnable {

    public static final String TEXTURE = "texture";
    public static final String COLOR = "color";
    public static final String TEXTURE_COLOR = "texture_color";
    public static final String INSTANCING = "instancing";

    public int glid;
    public int[] handle;

    private String name;
    private String[] attributes;

    private String vertexShaderCode;
    private String fragmentShaderCode;
    private int vertexShader;
    private int fragmentShader;

    private int collectionSize = 1024;

    private final BaseGL gl;

    public BaseShader(BaseGL gl, String name, String... atrs) {
        this.gl = gl;
        gl.shaders.add(this);

        this.name = name;
        setAttributes(atrs);
    }

    public void setAttributes(String... atrs) {

        if (atrs != null) {
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

    public int getHandle(int index) {

        return handle[index];
    }

    public int getHandle(String atr) {

        int index = -1;
        for (int i = 0; i < attributes.length; i++) {
            if (attributes[i].equals(atr)) {
                index = i;
                break;
            }
        }

        if (index == -1) {
            Base.logE("Monkey's can't find attribute");
        }

        return handle[index];
    }

    public String[] getAttributes() {

        return attributes;
    }

    public int getAttributesCount() {

        return handle.length;
    }

    public int getVertexShader() {

        return vertexShader;
    }

    public int getFragmentShader() {

        return fragmentShader;
    }

    public void useProgram() {

        BaseGL.useProgram(glid);
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

        gl.glRun(this);
    }

    @Override
    public void run() {

        if (glid == 0) {
            List<String> atrs = new ArrayList<String>();
            for (String atr : attributes) {
                if (atr.startsWith("a")) {
                    atrs.add(atr);
                }
            }

            vertexShader = ShaderHelper.compileShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
            BaseGL.glError(name + " vertex");
            fragmentShader = ShaderHelper.compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
            BaseGL.glError(name + " fragment");
            glid = ShaderHelper.createAndLinkProgram(vertexShader, fragmentShader, TrainedMonkey.toStringArray(atrs));
            BaseGL.glError(name + " program");

            for (int i = 0; i < attributes.length; i++) {
                switch (attributes[i].charAt(0)) {
                    case 'u':
                        handle[i] = GLES20.glGetUniformLocation(glid, attributes[i]);
                        break;
                    case 'a':
                        handle[i] = GLES20.glGetAttribLocation(glid, attributes[i]);
                        break;
                    default:
                        handle[i] = GLES20.glGetUniformLocation(glid, attributes[i]);
                }
            }

            BaseGL.glError(name + " attribs");
        }
    }

    public void delete() {

        GLES20.glDeleteShader(vertexShader);
        GLES20.glDeleteShader(fragmentShader);
        GLES20.glDeleteProgram(glid);
        glid = 0;
        vertexShader = 0;
        fragmentShader = 0;
    }

    @Override
    public String toString() {

        return "Shader " + name + "  glid:" + glid;
    }
}
