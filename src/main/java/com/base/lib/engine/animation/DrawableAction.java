package com.base.lib.engine.animation;

/**
 * 14 Created by doctor on 22.1.14.
 */
public class DrawableAction {

    protected String name;
    protected BaseSkelet skelet;
    protected float[][] vertices;
    protected int length;
    protected int stopsAt;
    protected int startsAt;

    public DrawableAction(String name, BaseSkelet skelet, float[][] vertices){

        this.name = name;
        this.skelet = skelet;
        this.vertices = vertices;

        if(skelet != null){
            length = skelet.getFramesCount();
        } else if(vertices != null){
            length = vertices.length;
        } else {
            length = 0;
        }

        startsAt = 0;
        stopsAt = -1;
    }

    public void asLazyAnimAction(){

        if(vertices != null){
            vertices = null;
        }

        int count = VertGroup.getVertsCount(skelet.getVertGroups());
        vertices = new float[length][];
        for(int i = 0; i<length; i++){
            skelet.update(i);
            vertices[i] = new float[count];
            vertices[i] = skelet.getVerts();
        }
    }

    public void resize(float oldSize, float newSize){

        resize(newSize/oldSize);
    }

    public void resize(float ratio){

        if(skelet != null){
            skelet.scale(ratio);
        }

        if (vertices != null) {
            for(int i = 0; i<vertices.length; i++){
                for(int j = 0; j<vertices[i].length; j++){
                    vertices[i][j] *= ratio;
                }
            }
        }
    }

    public int getStopsAt() {
        return stopsAt+1;
    }

    public int getStartsAt(){
        return  startsAt+1;
    }

    /** 0 -> never   1 -> first frame   length -> last frame */
    public void stopsAt(int stopsAt) {
        this.stopsAt = stopsAt-1;
    }

    /** 1 -> first frame   length -> last frame */
    public void startsAt(int startsAt){
        this.startsAt = startsAt-1;
    }

    public void once(){
        this.stopsAt = length-1;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BaseSkelet getSkelet() {
        return skelet;
    }

    public void setSkelet(BaseSkelet skelet) {
        this.skelet = skelet;
    }

    public float[][] getVertices() {
        return vertices;
    }

    public void setVertices(float[][] vertices) {
        this.vertices = vertices;
    }

}
