package com.base.lib.engine.common;

import com.base.lib.engine.Type;

/**
 * 12 Created by doctor on 4.7.13.
 */
public class DrawableData {

    public static BaseDrawableData CUSTOM(float[] vertices, float[] textures, short[] faces, Type objectType){

        BaseDrawableData data = new BaseDrawableData();

        data.vertices = vertices;
        data.textures = textures;
        data.faceOrder = faces;

        switch (objectType){
            case OBJECT_2D:
                data.use2Dvertices();
                break;
            case OBJECT_3D:
                data.use3Dvertices();
                break;
        }

        return data;
    }

    //   **** RECTANGLE DATA ****

    public static BaseDrawableData RECTANGLE(float width, float height){

        return RECTANGLE(width, height, 0.5f, 0.5f, 0.5f, 0.5f);
    }

    public static BaseDrawableData RECTANGLE(float width, float height, TextureInfo textureInfo){

        return RECTANGLE(width, height, textureInfo.getCenterX(), textureInfo.getCenterY(), textureInfo.gethWidth(), textureInfo.gethHeight());
    }

    public static BaseDrawableData RECTANGLE(float width, float height, float textureCenterX, float textureCenterY, float texturehWidth, float texturehHeight){

        BaseDrawableData data = new BaseDrawableData();
        data.use2Dvertices();

        data.vertices = rectangleVertices(width/2.0f, height/2.0f);
        data.textures = rectangleTextures(textureCenterX, textureCenterY, texturehWidth, texturehHeight);

        //faces
        short[] drawOrder = new short[6];

        drawOrder[0] = 0;
        drawOrder[1] = 1;
        drawOrder[2] = 2;

        drawOrder[3] = 1;
        drawOrder[4] = 3;
        drawOrder[5] = 2;

        data.faceOrder = drawOrder;

        data.setSizeX(width);
        data.setSizeY(height);

        return data;
    }

    public static float[] rectangleVertices(float hWidth, float hHeight){

        float[] vertices = new float[8];

        vertices[0] = -hWidth;
        vertices[1] = -hHeight;

        vertices[2] = hWidth;
        vertices[3] = -hHeight;

        vertices[4] = -hWidth;
        vertices[5] = hHeight;

        vertices[6] = hWidth;
        vertices[7] = hHeight;

        return vertices;
    }

    public static float[] rectangleTextures(float centerX, float centerY, float hWidth, float hHeight){

        float[] textures = new float[8];

        textures[0] = centerX-hWidth;
        textures[1] = centerY+hHeight;

        textures[2] = centerX+hWidth;
        textures[3] = centerY+hHeight;

        textures[4] = centerX-hWidth;
        textures[5] = centerY-hHeight;

        textures[6] = centerX+hWidth;
        textures[7] = centerY-hHeight;

        return textures;
    }

    public static float[] rectangleTextures(TextureInfo info){

        return rectangleTextures(info.getCenterX(), info.getCenterY(), info.gethWidth(), info.gethHeight());
    }

    public static short[] rectangleFaces(){

        short[] drawOrder = new short[6];
        drawOrder[0] = 0;
        drawOrder[1] = 1;
        drawOrder[2] = 2;

        drawOrder[3] = 1;
        drawOrder[4] = 3;
        drawOrder[5] = 2;
        return drawOrder;
    }

    //   **** CIRCLE DATA ****

    public static BaseDrawableData CIRCLE(int particles, float radius){

        return CIRCLE(particles, radius, 0.5f, 0.5f, 0.5f);
    }

    public static BaseDrawableData CIRCLE(int particles, float radius, TextureInfo textureInfo){

        return CIRCLE(particles, radius, textureInfo.getCenterX(), textureInfo.getCenterY(), textureInfo.gethWidth());
    }

    public static BaseDrawableData CIRCLE(int particles, float radius, float textureCenterX, float textureCenterY, float textureRadius) {

        BaseDrawableData data = new BaseDrawableData();
        data.use2Dvertices();

        data.sizeX = radius * 2;
        data.sizeY = radius * 2;

        data.vertices = circleVertices(particles, radius);
        data.textures = circleTextures(particles, textureCenterX, textureCenterY, textureRadius);

        short[] faces = new short[particles * 3];

        int id = 0;
        short j = 1;
        for (int i = 0; i < particles - 1; i++) {

            faces[id++] = 0;
            faces[id++] = j;
            faces[id++] = ++j;
        }

        if (particles < 31) {
            int index = particles * 3 - 3;
            faces[index++] = 0;
            faces[index++] = (short) particles;
            faces[index] = 1;
        }
        data.faceOrder = faces;

        return data;
    }

    public static float[] circleVertices(int particles, float radius){

        float[] vertices = new float[particles*2 + 2];

        final float step = 360/particles+1;
        float angle = 0;

        vertices[0] = 0;
        vertices[1] = 0;
        int j = 2;
        for(int i = 0; i<particles; i++){

            final Point2 point = Point2.circlePoint(0, 0, angle, radius);
            vertices[j++] = point.x;
            vertices[j++] = point.y;

            angle -= step;
        }

        return vertices;
    }

    public static float[] circleTextures(int particles, float centerX, float centerY, float radius){

        float[] textures = new float[particles*2+2];

        final float step = 360/particles+1;
        float angle = 0;

        textures[0] = centerX;
        textures[1] = centerY;
        int j = 2;
        for(int i = 0; i<particles; i++){

            final Point2 point = Point2.circlePoint(centerX, centerY, angle, radius);
            textures[j++] = point.x;
            textures[j++] = point.y;

            angle -= step;
        }

        return textures;
    }

    public static BaseDrawableData TRIANGLE(Point2 p1, Point2 p2, Point2 p3){

        BaseDrawableData data = new BaseDrawableData();
        data.use2Dvertices();

        float x1 = Point2.length(p1.x, p2.x);
        float x2 = Point2.length(p2.x, p3.x);
        float x3 = Point2.length(p1.x, p3.x);
        data.sizeX = Math.max(Math.max(x1, x2), Math.max(x1, x3));

        float y1 = Point2.length(p1.y, p2.y);
        float y2 = Point2.length(p2.y, p3.y);
        float y3 = Point2.length(p1.y, p3.y);
        data.sizeY = Math.max(Math.max(y1, y2), Math.max(y1, y3));

        data.vertices = new float[6];
        data.textures = new float[6];
        data.faceOrder = new short[3];

        data.vertices[0] = p1.x;
        data.vertices[1] = p1.y;

        data.vertices[2] = p2.x;
        data.vertices[3] = p2.y;

        data.vertices[4] = p3.x;
        data.vertices[5] = p3.y;

        data.textures[0] = 0.0f;
        data.textures[1] = 0.0f;

        data.textures[2] = 1.0f;
        data.textures[3] = 0.0f;

        data.textures[4] = 0.5f;
        data.textures[5] = 1.0f;

        data.faceOrder[0] = 0;
        data.faceOrder[1] = 1;
        data.faceOrder[2] = 2;

        return data;
    }

    public static BaseDrawableData LINE(Point3 p1, Point3 p2, Colorf c){

        BaseDrawableData data = new BaseDrawableData();
        data.use3Dvertices();

        data.vertices = new float[6];
        data.textures = new float[8];
        data.faceOrder = new short[2];

        data.vertices[0] = p1.x;
        data.vertices[1] = p1.y;
        data.vertices[2] = p1.z;

        data.vertices[3] = p2.x;
        data.vertices[4] = p2.y;
        data.vertices[5] = p2.z;

        data.textures[0] = c.r;
        data.textures[1] = c.g;
        data.textures[2] = c.b;
        data.textures[3] = c.a;

        data.textures[4] = c.r;
        data.textures[5] = c.g;
        data.textures[6] = c.b;
        data.textures[7] = c.a;

        data.faceOrder[0] = 0;
        data.faceOrder[1] = 1;

        return data;
    }


    public static BaseDrawableData TRISTAR(float hWidth, TextureInfo textureInfo){

        BaseDrawableData data = new BaseDrawableData();
        data.use3Dvertices();

        float[] vertices = new float[36];
        vertices[0] = -hWidth;
        vertices[1] = -hWidth;
        vertices[2] = 0;

        vertices[3] = hWidth;
        vertices[4] = -hWidth;
        vertices[5] = 0;

        vertices[6] = -hWidth;
        vertices[7] = hWidth;
        vertices[8] = 0;

        vertices[9] = hWidth;
        vertices[10] = hWidth;
        vertices[11] = 0;

        vertices[12] = -hWidth;
        vertices[13] = 0;
        vertices[14] = -hWidth;

        vertices[15] = hWidth;
        vertices[16] = 0;
        vertices[17] = -hWidth;

        vertices[18] = -hWidth;
        vertices[19] = 0;
        vertices[20] = hWidth;

        vertices[21] = hWidth;
        vertices[22] = 0;
        vertices[23] = hWidth;

        vertices[24] = 0;
        vertices[25] = -hWidth;
        vertices[26] = -hWidth;

        vertices[27] = 0;
        vertices[28] = -hWidth;
        vertices[29] = hWidth;

        vertices[30] = 0;
        vertices[31] = hWidth;
        vertices[32] = -hWidth;

        vertices[33] = 0;
        vertices[34] = hWidth;
        vertices[35] = hWidth;


        float[] textures = new float[24];
        float[] squareTex;
        if(textureInfo == null){
            squareTex = rectangleTextures(0.5f, 0.5f, 0.5f, 0.5f);
        } else {
            squareTex = rectangleTextures(textureInfo.getCenterX(), textureInfo.getCenterY(), textureInfo.gethWidth(), textureInfo.gethHeight());
        }
        System.arraycopy(squareTex, 0, textures, 0, 8);
        System.arraycopy(squareTex, 0, textures, 8, 8);
        System.arraycopy(squareTex, 0, textures, 16, 8);

        short[] faces = new short[18];
        faces[0] = 0;
        faces[1] = 1;
        faces[2] = 2;

        faces[3] = 1;
        faces[4] = 3;
        faces[5] = 2;

        faces[6] = 4;
        faces[7] = 5;
        faces[8] = 6;

        faces[9] = 5;
        faces[10] = 7;
        faces[11] = 6;

        faces[12] = 8;
        faces[13] = 9;
        faces[14] = 10;

        faces[15] = 9;
        faces[16] = 11;
        faces[17] = 10;

        data.vertices = vertices;
        data.textures = textures;
        data.faceOrder = faces;
        return data;
    }

    public static float[] tristarTextures(TextureInfo textureInfo){

        float[] textures = new float[24];
        float[] squareTex = rectangleTextures(textureInfo.getCenterX(), textureInfo.getCenterY(), textureInfo.gethWidth(), textureInfo.gethHeight());
        System.arraycopy(squareTex, 0, textures, 0, 8);
        System.arraycopy(squareTex, 0, textures, 8, 8);
        System.arraycopy(squareTex, 0, textures, 16, 8);

        return textures;
    }

    public static float[] oneColor(Colorf color, int vecCount){

        vecCount *= 4;
        float[] out = new float[vecCount];
        for(int i =0; i<vecCount;){
            out[i++] = color.r;
            out[i++] = color.g;
            out[i++] = color.b;
            out[i++] = color.a;
        }

        return out;
    }
}
