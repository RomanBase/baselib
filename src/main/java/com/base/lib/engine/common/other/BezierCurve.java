package com.base.lib.engine.common.other;

import android.opengl.GLES20;

import com.base.lib.engine.BaseCamera;
import com.base.lib.engine.BaseDrawable;
import com.base.lib.engine.Type;
import com.base.lib.engine.common.Colorf;
import com.base.lib.engine.common.DrawableData;

/**
 * Simple Bezier curve
 */
public class BezierCurve { //todo redesign

	private Point3[] p = new Point3[4];
    public double t = 0.0;
    public double s = 0.01;

    /**
     * Simple Bezier curve with four control points
     * */
    public BezierCurve(){

    }

    /**
     * Simple Bezier curve with four control points
     * */
	public BezierCurve(Point2 p0, Point2 p1, Point2 p2, Point2 p3) {

		setControlPoints(p0, p1, p2, p3);
	}

    public BezierCurve(Point3 p0, Point3 p1, Point3 p2, Point3 p3){

        setControlPoints(p0, p1, p2, p3);
    }

    public static double point(float sp, float cp1, float cp2, float ep, float t){

        return sp * (Math.pow(-t, 3) + 3 * Math.pow(t, 2) - 3 * t + 1)
                + 3 * cp1 * t * (Math.pow(t, 2) - 2 * t + 1)
                + 3 * cp2 * Math.pow(t, 2) * (1 - t)
                + ep * Math.pow(t, 3);
    }

    /**
     * sets bezier curve control points
     * @param p0,p1,p2,p3 Point2
     * */
    public void setControlPoints(Point2 p0, Point2 p1, Point2 p2, Point2 p3){

        p[0] = p0.toPoint3();
        p[1] = p1.toPoint3();
        p[2] = p2.toPoint3();
        p[3] = p3.toPoint3();
    }

    public void setControlPoints(Point3 p0, Point3 p1, Point3 p2, Point3 p3){

        p[0] = p0;
        p[1] = p1;
        p[2] = p2;
        p[3] = p3;
    }

	/**
     * calculate point on bezier curve from control points by given parametr
     * @param t 0.0 to 1.0
     * @return a point
     * */
	public Point2 bezierFunction(double t) {
		
		final double x, y;

		x =     p[0].x * (Math.pow(-t, 3)
                + 3 * Math.pow(t, 2) - 3 * t + 1)
                + 3 * p[1].x * t * (Math.pow(t, 2) - 2 * t + 1)
                + 3 * p[2].x * Math.pow(t, 2) * (1 - t)
                + p[3].x * Math.pow(t, 3);

		y = p[0].y * (Math.pow(-t, 3) + 3 * Math.pow(t, 2) - 3 * t + 1) + 3
				* p[1].y * t * (Math.pow(t, 2) - 2 * t + 1) + 3 * p[2].y
				* Math.pow(t, 2) * (1 - t) + p[3].y * Math.pow(t, 3);

        return new Point2((float)x, (float)y);
	}

    public Point3 bezier3Function(double t){

        final Point3 out = new Point3();

        out.x = (float) (p[0].x * (Math.pow(-t, 3) + 3 * Math.pow(t, 2) - 3 * t + 1) + 3
                        * p[1].x * t * (Math.pow(t, 2) - 2 * t + 1) + 3 * p[2].x
                        * Math.pow(t, 2) * (1 - t) + p[3].x * Math.pow(t, 3));

        out.y = (float) (p[0].y * (Math.pow(-t, 3) + 3 * Math.pow(t, 2) - 3 * t + 1) + 3
                        * p[1].y * t * (Math.pow(t, 2) - 2 * t + 1) + 3 * p[2].y
                        * Math.pow(t, 2) * (1 - t) + p[3].y * Math.pow(t, 3));

        out.z = (float) (p[0].z * (Math.pow(-t, 3) + 3 * Math.pow(t, 2) - 3 * t + 1) + 3
                * p[1].z * t * (Math.pow(t, 2) - 2 * t + 1) + 3 * p[2].z
                * Math.pow(t, 2) * (1 - t) + p[3].z * Math.pow(t, 3));

        return out;
    }

    public Point2 next2(){

        if(t > 1.0) t = 1.0f;
        return bezierFunction(t+=s);
    }

    public Point3 next3(){

        if(t > 1.0) t = 1.0;

        return bezier3Function(t+=s);
    }
	
	public boolean isDone(){

        if(s > 0) {
            return t >= 1.0;
        } else {
            return  t <= 0.0;
        }
    }

    public void reset(){

        t = 0.0;
    }

    public void reverse(){

        s *= -1.0;
    }

    public BaseDrawable showCurve(BaseCamera camera){ //todo

        double t = 0.0;
        int count = (int)(this.t / s);
        float[] vert = new float[count*3];
        float[] color = DrawableData.oneColor(new Colorf(1.0f, 1.0f, 1.0f, 1.0f), count);
        short[] faces = new short[count];

        int j = 0;
        for(short i = 0; i<count; i++){

            Point3 p = bezier3Function(t);
            vert[j++] = p.x;
            vert[j++] = p.y;
            vert[j++] = p.z;
            faces[i] = i;

            t += s;
        }

        BaseDrawable drawable = new BaseDrawable(DrawableData.CUSTOM(vert, color, faces, Type.OBJECT_3D));
        // TODO: 31. 1. 2016  drawable.setShader(BaseShader.get(1));
        drawable.getBuffer().setGlDrawMode(GLES20.GL_LINE_LOOP);

        //todo Base.render.addDrawable(drawable);

        return drawable;
    }

}
