package com.base.lib.engine.common;

/**
 * 20 Created by doctor on 18.8.13.
 */
public class TextureInfo {

    protected int id;
    protected String name;
    protected float centerX;
    protected float centerY;
    protected float hWidth;
    protected float hHeight;

    public TextureInfo(){

    }

    public TextureInfo(int id, String name, float centerX, float centerY, float hWidth, float hHeight){

        this.id = id;
        this.name = name;
        this.centerX = centerX;
        this.centerY = centerY;
        this.hWidth = hWidth;
        this.hHeight = hHeight;
    }

    public TextureInfo(float centerX, float centerY, float hWidth, float hHeight){

        this.centerX = centerX;
        this.centerY = centerY;
        this.hWidth = hWidth;
        this.hHeight = hHeight;
    }

    public void setId(int id) {

        this.id = id;
    }

    public void setName(String name) {

        this.name = name;
    }

    public void setCenterX(float centerX) {

        this.centerX = centerX;
    }

    public void setCenterY(float centerY) {

        this.centerY = centerY;
    }

    public void sethWidth(float hWidth) {

        this.hWidth = hWidth;
    }

    public void sethHeight(float hHeight) {

        this.hHeight = hHeight;
    }

    public String getName(){

        return name;
    }

    public float getCenterX() {

        return centerX;
    }

    public float getCenterY() {

        return centerY;
    }

    public float gethWidth() {

        return hWidth;
    }

    public float gethHeight() {

        return hHeight;
    }

    public float[] getTextureCoords(){

        return DrawableData.rectangleTextures(centerX, centerY, hWidth, hHeight);
    }

    public float[] getTextureCoords(int circleParts){

        return DrawableData.circleTextures(circleParts, centerX, centerY, hWidth);
    }

    public static TextureInfo sprite(float textureWidth, float textureHeight, float spriteWidth, float spriteHeight, int row, int column){

        TextureInfo info = new TextureInfo();

        float width = (spriteWidth/textureWidth);
        float height = (spriteHeight/textureHeight);
        info.hWidth = width*0.5f;
        info.hHeight = height*0.5f;
        info.centerX = width*column+info.hWidth;
        info.centerY = height*row+info.hHeight;

        return info;
    }

    public static TextureInfo[] sprite(float textureWidth, float textureHeight, int rows, int columns){

        int count = rows * columns;
        TextureInfo[] info = new TextureInfo[count];

        float width = 1.0f/(float)columns;
        float height = 1.0f/(float)rows;
        float hWidth = width*0.5f;
        float hHeight = height*0.5f;

        int i = 0;
        for(int r = 0; r<rows; r++){
            for(int c = 0; c<columns; c++){
                TextureInfo ti = new TextureInfo();
                ti.hWidth = hWidth;
                ti.hHeight = hHeight;
                ti.centerX = width*c+hWidth;
                ti.centerY = height*r+hHeight;
                info[i++] = ti;
            }
        }

        return info;
    }

    public static TextureInfo reverse(TextureInfo info){

        TextureInfo out = new TextureInfo();
        out.centerX = info.centerX;
        out.centerY = info.centerY;
        out.hWidth = info.hWidth * -1.0f;
        out.hHeight = info.hHeight * -1.0f;

        return out;
    }

    public static float[][] rectangleTextureCoords(TextureInfo[] info){

        float[][] out = new float[info.length][];

        for(int i = 0; i<info.length; i++){
            out[i] = DrawableData.rectangleTextures(info[i]);
        }

        return out;
    }

    public void modifyTextureCoords(float[] coords){ //todo not tested yet..,

        float left = centerX - hWidth;
        float bot = centerY - hHeight;
        float width = hWidth*2.0f;
        float height = hHeight*2.0f;

        for(int i = 0; i<coords.length; i+=2){

            coords[i] = left + width*coords[i];
            coords[i+1] = bot + height*coords[i+1];
        }
    }

    @Override
    public String toString() {

        return name + " " + centerX + " " + centerY + " " + hWidth + " " + hHeight;
    }
}
