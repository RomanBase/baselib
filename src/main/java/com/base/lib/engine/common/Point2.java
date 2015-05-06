package com.base.lib.engine.common;

public class Point2 { //todo revise

	public float x;
	public float y;
	
	/**
	 * base constructor for float Point2 class
	 * x = 0.0f
	 * y = 0.0f
	 * */
	public Point2(){
		x = 0.0f;
		y = 0.0f;
	}
	
	/**
	 * Point2 floating point
	 * set x, y floats
	 * */
	public Point2(float x, float y){
		
		this.x = x;
		this.y = y;
	}

    public static void add(Point2 out, Point2 add){

        out.x += add.x;
        out.y += add.y;
    }

    public static void sub(Point2 out, Point2 sub){

        out.x -= sub.x;
        out.y -= sub.y;
    }

    public void sub(Point2 sub){

        x -= sub.x;
        y -= sub.y;
    }

    public static float dotProduct(Point2 p1, Point2 p2){

        return p1.x*p2.x + p1.y*p2.y;
    }

    public static float crossProduct(Point2 p1, Point2 p2){

        return p1.x*p2.y - p1.y*p2.x;
    }

    /**
	 * returns axis distance between points
	 * */
	public static float length(float p1, float p2){

		return Math.abs(p1 - p2);
	}
	
	/**
	 * returns distance between Point(p1X, p2X) and Point2(p2X, p2Y)
	 * */
	public static float distance(float p1X, float p1Y, float p2X, float p2Y){
		
		return (float) Math.sqrt( (Math.pow(p2X - p1X, 2) + Math.pow(p2Y - p1Y, 2)));
	}

    public static float distance(float lenghtX, float lenghtY){

        return (float) Math.sqrt(lenghtX*lenghtX + lenghtY*lenghtY);
    }
	
	/**
	 * returns distance between Points
	 * */
	public static float distance(Point2 p1, Point2 p2){
		
		return distance(p1.x, p1.y, p2.x, p2.y);
	}

    public static float avarage(float[] nums){

        float sum = 0;
        final int count = nums.length;
        for (float num : nums) {
            sum += num;
        }

        return sum/count;
    }

    public static float avarage(float num1, float num2){

        return (num1+num2)/2;
    }
	
	/**
	 * returns Point2 which representing point on circle with specified diameter
	 * x, y  -  center
	 * pointerX, pointerY  -  direction
	 * */
	public static Point2 circlePoint(float x, float y, float pointerX, float pointerY, float diameter){
		
		Point2 newPoint = new Point2();
		
		final float w = Point2.length(x, pointerX);
		final float h = Point2.length(y, pointerY);
		final float p = (float) Math.sqrt(w*w + h*h);

        if (p != 0) {
            float sin = h / p;
            float cos = w / p;

            if (x > pointerX) {
                cos *= -1;
            }
            if (y > pointerY) {
                sin *= -1;
            }

            newPoint.x = x + cos * diameter;
            newPoint.y = y + sin * diameter;
        } else {
            newPoint.x = x;
            newPoint.y = y;
        }

        return newPoint;
	}
	
	/**
	 * returns Point2 which representing point on circle with specified angle and diameter
	 * x, y  -  center
	 * angle  -  direction
	 * */
	public static Point2 circlePoint(float x, float y, float angle, float diametr){
		
		Point2 newPoint = new Point2();
		
		newPoint.x = (float) (x + Math.sin(Math.toRadians(angle))*diametr);
		newPoint.y = (float) (y + Math.cos(Math.toRadians(angle))*diametr);
		
		return newPoint;
	}
	
	/**
	 * returns float which representing angle between two points
	 * x, y  -  center
	 * pointerX, pointerY  -  direction
	 * */
	public static float angle(float x, float y, float pointerX, float pointerY){
		
		final float w = pointerX - x;
		final float h = pointerY - y;

		return (float) Math.toDegrees(Math.atan2(w, h));
	}
	
	/**
	 * returns interpolation between two points
	 * t  -  0.0 -> 1.0 
	 * */
	public static Point2 interpolatePoint2(Point2 p0, Point2 p1, float t){
		
		return new Point2(p0.x + (p1.x - p0.x) * t, p0.y + (p1.y - p0.y) * t);
	}
	
	/**
	 * returns interpolated float[] array
	 * t  -  0.0 -> 1.0
	 * */
	public static float[] interpolateArray(float[] p0, float[] p1, float t){
		
		float[] out = new float[p0.length];
		
		for(int i = 0; i < out.length; i++){
			
			out[i] = p0[i] + (p1[i] - p0[i]) * t; i++;
			out[i++] = 0;
			out[i] = p0[i] + (p1[i] - p0[i]) * t;
		}
		
		return out;
	}

    public Point3 toPoint3(){

        return new Point3(x, y, 0);
    }

    @Override
    public String toString() {

        return x +"  "+ y;
    }
}
