package com.base.lib.engine.common;

public class Point3 { //todo

	public float x;
	public float y;
	public float z;
	
	public Point3(){
		x = 0.0f;
		y = 0.0f;
		z = 0.0f;
	}
	
	public Point3(float x, float y, float z){
		this.x = x;
		this.y = y;
		this.z = z;
	}

    public Point3(Point3 point){
        x = point.x;
        y = point.y;
        z = point.z;
    }

    public void set(float x, float y, float z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static float length(Point3 p){

        return (float) Math.sqrt(p.x*p.x + p.y*p.y + p.z*p.z);
    }

    public static float distance(Point3 p1, Point3 p2){

        return (float) Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2) + Math.pow(p1.z - p2.z, 2));
    }

    public static float distance(float p1x, float p2x, float p1z, float p1y, float p2y, float p2z){

        return (float) Math.sqrt(Math.pow(p1x - p2x, 2) + Math.pow(p1y - p2y, 2) + Math.pow(p1z - p2z, 2));
    }

    public static Point3 direction(Point3 p1, Point3 p2){

        Point3 out = new Point3();

        out.x = p2.x - p1.x;
        out.y = p2.y - p1.y;
        out.z = p2.z - p1.z;

        out.normalize();

        return out;
    }

    public static void copy(Point3 src, Point3 dst){

        dst.x = src.x;
        dst.y = src.y;
        dst.z = src.z;
    }

    public static void sub(Point3 from, Point3 suber){

        from.x -= suber.x;
        from.y -= suber.y;
        from.z -= suber.z;
    }

    public static void sum(Point3 to, Point3 add){

        to.x += add.x;
        to.y += add.y;
        to.z += add.z;
    }

    public static void mul(Point3 src, float m){

        src.x *= m;
        src.y *= m;
        src.z *= m;
    }

    public double magnitude(){

        return Math.sqrt(x*x + y*y + z*z);
    }

    public static float magnitude(Point3 p){

        return (float) Math.sqrt(p.x*p.x + p.y*p.y + p.z*p.z);
    }

    public void normalize(){

        double m = magnitude();

        x /= m;
        y /= m;
        z /= m;
    }

    public static float dotProduct(Point3 p1, Point3 p2){

        return (p1.x * p2.x) + (p1.y * p2.y) + (p1.z * p2.z);
    }

    public static float angle(Point3 p1, Point3 p2){

        float dot = dotProduct(p1, p2);
        float mag1 = magnitude(p1);
        float mag2 = magnitude(p2);

        return (float) Math.cos(dot/(mag1*mag2));
    }

    public static Point3 avg(Point3... points){

        Point3 out = new Point3();

        for(Point3 point : points){
            out.x += point.x;
            out.y += point.y;
            out.z += point.z;
        }

        out.x /= points.length;
        out.y /= points.length;
        out.z /= points.length;

        return out;
    }

    public static void interpolate(Point3 out, Point3 p0, Point3 p1, float t){

        out.x = p0.x + (p1.x - p0.x) * t;
        out.y = p0.y + (p1.y - p0.y) * t;
        out.z = p0.z + (p1.z - p0.z) * t;
    }

    @Override
    public String toString() {
        return "["+x+" "+y+" "+z+"]";
    }
}
