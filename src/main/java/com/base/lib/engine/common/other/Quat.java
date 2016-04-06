package com.base.lib.engine.common.other;


public class Quat { //todo native multiplication

    private static final float RAD = (float) (Math.PI / 180.0f);

    public float x;
    public float y;
    public float z;
    public float w;

    public float[] toArray(){

    	return new float[]{w, x, y, z};
    }
    
    public double magnitude(){

        return Math.sqrt(w*w + x*x + y*y + z*z);
    }

    public void normalize(){

		float sqrt = w * w + x * x + y * y + z * z;

		if (sqrt != 1.0f) {
			double m = Math.sqrt(sqrt);

			w /= m;
			x /= m;
			y /= m;
			z /= m;
		}
    }
    
    public void identity(){
    	
    	w = 1.0f;
    	x = 0.0f;
    	y = 0.0f;
    	z = 0.0f;
    }
    
    public void w(){
		
		float t = 1.0f - ( x * x ) - ( y * y ) - ( z * z );
        if ( t < 0.0f ){
        	w = 0.0f;
        } else {
        	w = (float) -Math.sqrt(t);
        }
	}

    public void set(float x, float y, float z){

        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void set(float w){

        this.w = w;
    }

    public static float dotProduct (Quat qa, Quat qb){
		
    	return qa.x * qb.x + qa.y * qb.y + qa.z * qb.z + qa.w * qb.w;
  	}
    
    public static void copy(Quat src, Quat dst){
    	
    	dst.w = src.w;
    	dst.x = src.x;
    	dst.y = src.y;
    	dst.z = src.z;
    }
    
    /** is NOT commutative! */
    public static Quat mul(Quat qa, Quat qb){
    	
    	Quat out = new Quat();
    	
    	out.w = qa.w*qb.w - qa.x*qb.x - qa.y*qb.y - qa.z*qb.z;
    	out.x = qa.w*qb.x + qa.x*qb.w + qa.y*qb.z - qa.z*qb.y;
    	out.y = qa.w*qb.y - qa.x*qb.z + qa.y*qb.w + qa.z*qb.x;
    	out.z = qa.w*qb.z + qa.x*qb.y - qa.y*qb.x + qa.z*qb.w;
    	
    	return out;
    }
    
    public static Quat mul(Quat q, float x, float y, float z){
    	
    	Quat out = new Quat();
		
		out.w = - (q.x * x) - (q.y * y) - (q.z * z);
		out.x =   (q.w * x) + (q.y * z) - (q.z * y);
		out.y =   (q.w * y) + (q.z * x) - (q.x * z);
		out.z =   (q.w * z) + (q.x * y) - (q.y * x);
		
		return out;
    }
    
    public void mulByLeft(Quat left){
    	
    	copy(mul(this, left), this);
    }
    
    public void mulByRight(Quat right){
    	
    	copy(mul(right, this), this);
    }
    
    /** angle in RADIANS, x,y,z vector */
    public static void localRot(Quat q, float angle, float x, float y, float z){

        angle = (angle*RAD)*0.5f;
    	float s = (float)Math.sin(angle);
    	q.w = (float)Math.cos(angle);
    	q.x = x * s;
    	q.y = y * s;
    	q.z = z * s;
    }

    public void localRot(float angle, float x, float y, float z){

        localRot(this, angle, x, y, z);
    }
    
    public void genRotationMatrix(float[] m){
    	
    	float xx = x*x*2.0f;
    	float yy = y*y*2.0f;
    	float zz = z*z*2.0f;

    	float xy = x*y*2.0f;
    	float xz = x*z*2.0f;
    	float yz = y*z*2.0f;

    	float wx = w*x*2.0f;
    	float wy = w*y*2.0f;
    	float wz = w*z*2.0f;
    	
    	m[0] = 1.0f - yy - zz;
    	m[4] = xy - wz;
    	m[8] = xz + wy;
    	
    	m[1] = xy + wz;
    	m[5] = 1.0f - xx - zz;
    	m[9] = yz - wx;
    	
    	m[2] = xz + wy;
    	m[6] = yz + wx;
    	m[10] = 1.0f - xx - yy;
    }
    
    public static Quat slerp (Quat qa, Quat qb, float t) {
		
		Quat out = new Quat();

		/* Check for out-of range parameter and return edge points if so */
		if (t <= 0.0) {
			return qa;
		}

		if (t >= 1.0) {
			return qb;
		}

		/* Compute "cosine of angle between quaternions" using dot product */
		float cosOmega = dotProduct(qa, qb);

		/*
		 * If negative dot, use -q1. Two quaternions q and -q represent the same
		 * rotation, but may produce different slerp. We chose q or -q to rotate
		 * using the acute angle.
		 */
		float q1w = qb.w;
		float q1x = qb.x;
		float q1y = qb.y;
		float q1z = qb.z;

		if (cosOmega < 0.0f) {
			q1w = -q1w;
			q1x = -q1x;
			q1y = -q1y;
			q1z = -q1z;
			cosOmega = -cosOmega;
		}

		/* We should have two unit quaternions, so dot should be <= 1.0 */
		assert (cosOmega < 1.1f);

		/*
		 * Compute interpolation fraction, checking for quaternions almost
		 * exactly the same
		 */
		float k0, k1;

		if (cosOmega > 0.9999f) {
			/*
			 * Very close - just use linear interpolation, which will protect
			 * againt a divide by zero
			 */

			k0 = 1.0f - t;
			k1 = t;
		} else {
			/*
			 * Compute the sin of the angle using the trig identity sin^2(omega)
			 * + cos^2(omega) = 1
			 */
			float sinOmega = (float) Math.sqrt(1.0f - (cosOmega * cosOmega));

			/* Compute the angle from its sin and cosine */
			float omega = (float) Math.atan2(sinOmega, cosOmega);

			/*
			 * Compute inverse of denominator, so we only have to divide once
			 */
			float oneOverSinOmega = 1.0f / sinOmega;

			/* Compute interpolation parameters */
			k0 = (float) (Math.sin((1.0f - t) * omega) * oneOverSinOmega);
			k1 = (float) (Math.sin(t * omega) * oneOverSinOmega);
		}

		/* Interpolate and return new quaternion */
		out.w = (k0 * qa.w) + (k1 * q1w);
		out.x = (k0 * qa.x) + (k1 * q1x);
		out.y = (k0 * qa.y) + (k1 * q1y);
		out.z = (k0 * qa.z) + (k1 * q1z);
		
		return out;
	}
    
    @Override
    public String toString() {

    	return w +" "+ x +" "+ y +" "+ z +"   ("+(w * w + x * x + y * y + z * z)+")";
    }

}
