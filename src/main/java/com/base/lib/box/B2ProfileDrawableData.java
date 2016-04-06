package com.base.lib.box;

import android.opengl.GLES20;

import com.base.lib.engine.BaseDrawable;
import com.base.lib.engine.Type;
import com.base.lib.engine.common.BaseDrawableData;
import com.base.lib.engine.common.Colorf;
import com.base.lib.engine.common.DrawableData;

import org.jbox2d.collision.shapes.ChainShape;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.EdgeShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.joints.Joint;

/**
 * RunningWild Created by doctor on 3.9.13.
 */

class B2ProfileDrawableData {

    static BaseDrawableData POLYGONSHAPE(PolygonShape shape){

        final int vecCount = shape.getVertexCount();
        float[] vertices = new float[vecCount*2];

        int index = 0;
        for(int i = 0; i<vecCount; i++){
            vertices[index++] = shape.m_vertices[i].x;
            vertices[index++] = shape.m_vertices[i].y;
        }

        short[] drawOrder = new short[vecCount];
        for(short i = 0; i<drawOrder.length; i++){
            drawOrder[i] = i;
        }

        Colorf color = B2Profile.bodyColor;
        float[] textures = new float[vecCount*4];
        for(int i = 0; i<textures.length;){
            textures[i++] = color.r;
            textures[i++] = color.g;
            textures[i++] = color.b;
            textures[i++] = color.a;
        }

        return DrawableData.CUSTOM(vertices, textures, drawOrder, Type.OBJECT_2D);
    }

    static BaseDrawableData CIRCLESHAPE(CircleShape shape){

        final float radius = shape.m_radius;
        final int particles = 64;

        short[] drawOrder = new short[particles+1];
        for(short i = 0; i<particles; i++){
            drawOrder[i] = i;
        }
        drawOrder[particles] = 1;

        Colorf color = B2Profile.bodyColor;
        float[] textures = new float[particles*4];
        for(int i = 0; i<textures.length;){
            textures[i++] = color.r;
            textures[i++] = color.g;
            textures[i++] = color.b;
            textures[i++] = color.a;
        }

        return DrawableData.CUSTOM(DrawableData.circleVertices(particles-1, radius), textures, drawOrder, Type.OBJECT_2D);
    }

    static BaseDrawableData CHAINSHAPE(ChainShape shape){

        final int vecCount = shape.m_count;

        float[] vertices = new float[vecCount*2];
        int index = 0;
        for(int i = 0; i<vecCount; i++){
            vertices[index++] = shape.m_vertices[i].x;
            vertices[index++] = shape.m_vertices[i].y;
        }

        short[] drawOrder = new short[vecCount*2];
        short lindex = 0;
        for(short i = 0; i<drawOrder.length-2;){
            drawOrder[i++] = lindex++;
            drawOrder[i++] = lindex;
        }

        Colorf color = B2Profile.bodyColor;
        float[] textures = new float[vecCount*4];
        for(int i = 0; i<textures.length;){
            textures[i++] = color.r;
            textures[i++] = color.g;
            textures[i++] = color.b;
            textures[i++] = color.a;
        }

        return DrawableData.CUSTOM(vertices, textures, drawOrder, Type.OBJECT_2D);
    }

    static BaseDrawableData EDGESHAPE(EdgeShape shape){

        int vecCount = 2;
        if(shape.m_hasVertex0) vecCount++;
        if(shape.m_hasVertex3) vecCount++;

        float[] vertices = new float[vecCount*2];
        int index = 0;
        if(shape.m_hasVertex0){
            vertices[index++] = shape.m_vertex0.x;
            vertices[index++] = shape.m_vertex0.y;
        }

        vertices[index++] = shape.m_vertex1.x;
        vertices[index++] = shape.m_vertex1.y;
        vertices[index++] = shape.m_vertex2.x;
        vertices[index++] = shape.m_vertex2.y;

        if(shape.m_hasVertex3){
            vertices[index++] = shape.m_vertex3.x;
            vertices[index  ] = shape.m_vertex3.y;
        }


        short[] drawOrder = new short[vecCount];
        for(short i = 0; i<drawOrder.length; i++){
            drawOrder[i] = i;
        }

        Colorf color = B2Profile.bodyColor;
        float[] textures = new float[vecCount*4];
        for(int i = 0; i<textures.length;){
            textures[i++] = color.r;
            textures[i++] = color.g;
            textures[i++] = color.b;
            textures[i++] = color.a;
        }

        return DrawableData.CUSTOM(vertices, textures, drawOrder, Type.OBJECT_2D);
    }

    static BaseDrawableData JOINTSHAPE(Joint joint){

        Vec2 aPos = new Vec2();
        Vec2 bPos = new Vec2();
        joint.getAnchorA(aPos);
        joint.getAnchorB(bPos);

        float[] vertices = new float[4];
        vertices[0] = aPos.x;
        vertices[1] = aPos.y;
        vertices[2] = bPos.x;
        vertices[3] = bPos.y;

        short[] drawOrder = new short[2];
        drawOrder[0] = 0;
        drawOrder[1] = 1;

        Colorf color = B2Profile.jointColor;
        float[] textures = new float[8];
        for(int i = 0; i<textures.length;){
            textures[i++] = color.r;
            textures[i++] = color.g;
            textures[i++] = color.b;
            textures[i++] = color.a;
        }

        return DrawableData.CUSTOM(vertices, textures, drawOrder, Type.OBJECT_2D);
    }
}

                 /* ---PROFILES--- */

class B2PolygonProfile extends B2ProfileDrawable {

    private Body body;

    protected B2PolygonProfile(Body body, Shape shape, Colorf color){
        super(B2ProfileDrawableData.POLYGONSHAPE((PolygonShape) shape), color);
        this.body = body;
    }

    @Override
    public void update() {

        translate(body.getPosition());
        rotateZ(B2.toDegrees(body.getAngle()));

        super.updateModelVP();
        super.setIdentityMM();
    }
}

class B2CircleProfile extends B2ProfileDrawable {

    private Body body;

    protected B2CircleProfile(Body body, Shape shape, Colorf color){
        super(B2ProfileDrawableData.CIRCLESHAPE((CircleShape) shape), color);
        this.body = body;
    }

    @Override
    public void update() {

        translate(body.getPosition());
        rotateZ(B2.toDegrees(body.getAngle()));

        super.updateModelVP();
        super.setIdentityMM();
    }
}

class B2ChainProfile extends B2ProfileDrawable {

    private Body body;

    protected B2ChainProfile(Body body, Shape shape, Colorf color){
        super(B2ProfileDrawableData.CHAINSHAPE((ChainShape) shape), color);
        buffer.setGlDrawMode(GLES20.GL_LINES);
        this.body = body;
    }

    @Override
    public void update() {

        translate(body.getPosition());
        rotateZ(B2.toDegrees(body.getAngle()));

        super.updateModelVP();
        super.setIdentityMM();
    }
}

class B2EdgeProfile extends B2ProfileDrawable {

    private Body body;

    protected B2EdgeProfile(Body body, Shape shape, Colorf color){
        super(B2ProfileDrawableData.EDGESHAPE((EdgeShape) shape), color);
        this.body = body;
    }

    @Override
    public void update() {

        translate(body.getPosition());
        rotateZ(B2.toDegrees(body.getAngle()));

        super.updateModelVP();
        super.setIdentityMM();
    }
}

class B2JointProfile extends B2ProfileDrawable {

    private Joint joint;
    private float[] vertices;

    protected B2JointProfile(Joint joint, Colorf color){
        super(B2ProfileDrawableData.JOINTSHAPE(joint), color);

        this.joint = joint;
        vertices = new float[4];
        base.render.addDrawable(this);
    }

    @Override
    public void update() {

        Vec2 aPos = joint.getBodyA().getPosition();
        Vec2 bPos = joint.getBodyB().getPosition();

        vertices[0] = aPos.x;
        vertices[1] = aPos.y;
        vertices[2] = bPos.x;
        vertices[3] = bPos.y;

        getBuffer().getVerticeBuffer().put(vertices).position(0);

        super.updateModelVP();
        super.setIdentityMM();
    }
}

class B2ProfileDrawable extends BaseDrawable{

    B2ProfileDrawable(BaseDrawableData data, Colorf color){
         super(data);

        // TODO: 31. 1. 2016  shader = BaseShader.perVertexColorShader();
         buffer.setGlDrawMode(GLES20.GL_LINE_LOOP);

        getBuffer().setCoordsPerColor(4);
        /*if(color != B2Profile.bodyColor){
            setColor(color, data.getVertices().length);
        }*/
    }

    private void setColor(Colorf color, int count){

        float[] newColor = new float[count];
        for(int i = 0; i<count;){
            newColor[i++] = color.r;
            newColor[i++] = color.g;
            newColor[i++] = color.b;
            newColor[i++] = color.a;
        }

        getBuffer().getTextureBuffer().put(newColor).position(0);
    }
}
