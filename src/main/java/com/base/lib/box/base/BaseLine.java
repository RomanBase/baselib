package com.base.lib.box.base;

import org.jbox2d.common.Vec2;

import com.base.lib.box.B2;
import com.base.lib.box.B2Chain;
import com.base.lib.box.B2Profile;
import com.base.lib.engine.BaseRenderable;
import com.base.lib.engine.common.Point2;

/**
 *
 */
public class BaseLine extends BelBase { //todo remove Point2 dependency

    public Vec2[] vecs;
    public float[] vertices;
    public int length;

    private int index;

    public BaseLine(){}

    public BaseLine(String name){
        this.name = name;
    }

    public BaseLine(float[] vertices){
        setVertices(vertices);
    }

    public BaseLine(String name, float[] vertices){
        this.name = name;
        setVertices(vertices);
    }

    public void setVertices(float[] vertices){

        this.vertices = vertices;
        this.length = vertices.length/2 - 1;
    }

    public void initB2Vecs(){

        vecs = B2.createB2data(vertices);
    }

    public void setIndex(int index){

        this.index = index*2;
    }

    public float getAngle(){

        return Point2.angle(vertices[index], vertices[index + 1], vertices[index + 2], vertices[index + 3]);
    }

    public float getAngleBetween(int index, int offset){

        index *= 2;

        return Point2.angle(vertices[index], vertices[++index], vertices[index=+offset], vertices[index+1]);
    }

    public float getDistance(){

        return Point2.distance(vertices[index], vertices[index+1], vertices[index+2], vertices[index+3]);
    }

    public float getDistanceBetween(int index, int offset){

        index *= 2;

        return Point2.distance(vertices[index], vertices[++index], vertices[index+=offset], vertices[index+1]);
    }

    public float getXLength(){

        return vertices[index+2] - vertices[index];
    }

    public float getYLength(){

        return vertices[index+3] - vertices[index+1];
    }

    public float xStepToNext(float steps){

        return getXLength()/steps;
    }

    public float yStepToNext(float steps){

        return getYLength()/steps;
    }

    public float getXVert(){

        return vertices[index];
    }

    public float getYVert(){

        return vertices[index+1];
    }

    public void intoB2Chain(Object userData, final float offsetPosX, final float offsetPosY){

        b2body = B2Chain.create(userData, offsetPosX, offsetPosY, vecs);

        if(B2Profile.usingProfiles()) {
            BaseRenderable renderable = B2Profile.body(b2body);
            if (renderable != null) {
                renderable.use();
            }
        }
    }
}
