package com.base.lib.engine;

import android.opengl.Matrix;

import com.base.lib.engine.common.BaseMatrix;
import com.base.lib.engine.common.Point3;

public class BaseCamera {

    public float reverseRatio;

    public float[] mVPMatrix; // multiplayed VPMatrix
	public float[][] VPMatrix; // [0]-view matrix, [1]-projection matrix

    private float[] transformationMatrix;
    private float[] rotationMatrix;

	private Point3 up;
	private Point3 lookAt;
    private Point3 lookFrom;
    private Point3 pos;

    protected float width;
    protected float height;

    private float px;
    private float ratioX;
    private float ratioY;
    private float nz;
    private float fz;

    /**
     * creates new base camera instance,
     * */
    public BaseCamera() {

        init();
    }

    /** inits required attributes */
    private void init(){

        transformationMatrix = new float[16];
        rotationMatrix = new float[16];
        mVPMatrix = new float[16];
        VPMatrix = new float[2][16];
        pos = new Point3();
        lookFrom = new Point3();
        lookAt = new Point3();
        up = new Point3();

        BaseMatrix.setIdentity(transformationMatrix);
        BaseMatrix.setIdentity(rotationMatrix);
    }

    /**
     * creates ortographical camera
     * @param width camera view width
     * */
    public void set2DCamera(float width){

        width *= 0.5f;
        set2DProjection(width, width / Base.screenRatio, width, width + 1);
        setFrontView(-width);
        update();
    }

    /**
     * creates ortographical camera
     * @param width camera view width
     * @param far clipping plane
     * */
    public void set2DCamera(float width, float far){

        width *= 0.5f;
        set2DProjection(width, width / Base.screenRatio, Base.screenRatio, width + far);
        setFrontView(-width);
        update();
    }

    /**
     * creates ortographical camera
     * @param width camera view width
     * @param near clipping plane
     * @param far clipping plane
     * */
    public void set2DCamera(float width, float near, float far){

        width *= 0.5f;
        set2DProjection(width, width / Base.screenRatio, near, width + far);
        setFrontView(-width);
        update();
    }

    /**
     * creates ortographical camera
     * @param width camera view width
     * @param height camera view height
     * @param near clipping plane
     * @param far clipping plane
     * */
    public void set2DCamera(float width, float height, float near, float far){

        width *= 0.5f;
        set2DProjection(width, height, near, width + far);
        setFrontView(-width);
        update();
    }

    /**
     * creates perspective camera
     * @param lookFromZ z >= ratio (typically minus value), camera.width is lookFromZ*2
     * @param far clipping plane
     * */
    @Deprecated
    public void set3DCameraOld(float lookFromZ, float far){

        set3DProjection(Base.screenRatio, Math.abs(lookFromZ) + far);
        setFrontView(lookFromZ);
        update();
        if(Math.abs(lookFromZ) < Base.screenRatio){
            throw new RuntimeException("Monkey's camera near atr. is set to Base.screenRatio, so lookFromZ atr. must be higher..\n" +
                    "Use set3DProjection method to handle it..");
        }
    }

    /**
     * creates perspective came projection
     * @param width requested screen width
     * @param far clipping plane
     * */
    public void set3DCamera(float width, float far){

        width *= 0.5f;
        set3DProjection(Base.screenRatio, width + far);
        setFrontView(-width);
        update();
    }

    /**
     * creates perspective camera based on Field Of View Y
     * @param viewAngleY fovy
     * @param width requested screen width
     * @param far clipping plane
     * */
    public void set3DCamera(float viewAngleY, float width, float far){

        set3DCamera(viewAngleY, width, Base.screenRatio, far);
    }

    /**
     * creates perspective camera based on Field Of View Y
     * @param viewAngleY fovy
     * @param width requested screen width
     * @param far clipping plane
     * */
    public void set3DCamera(float viewAngleY, float width, float near, float far){

        width *= 0.5f;
        float lookFrom = -(float)((width/Base.screenRatio) / (Math.tan(Math.toRadians(viewAngleY*0.5f))));

        set3DProjection(viewAngleY, near, Math.abs(lookFrom)+far);
        setFrontView(lookFrom);
        calcScreen(width);
        update();
    }

    /** creates 2D ortographical camera. camera view is set as screen width and height */
    public void set2DProjection(){

        final float hWidth = Base.screenWidth/2.0f;
        final float hHeight = Base.screenHeight/2.0f;
                           //  left,   right,   bottom,  top,     near,   far
        set2DProjection(hWidth, -hWidth, -hHeight, hHeight, hWidth, hWidth + 1);
    }

    /**
     * creates 2D ortographical camera projection
     * @param hWidth half width of camera view
     * @param hHeight half height of camera view
     * */
    public void set2DProjection(float hWidth, float hHeight, float near, float far){

        set2DProjection(hWidth, -hWidth, -hHeight, hHeight, near, far);
    }

    /**
     * creates ortographical camera projection matrix
     * check Matrix.orthoM
     * */
    public void set2DProjection(float left, float right, float bottom, float top, float near, float far){

        nz = near;
        fz = far;
        Matrix.orthoM(VPMatrix[1], 0, left, right, bottom, top, near, far);
    }

    /** creates 3D projection camera. camera view width as ratio and heigh is 1.0f, near as ratio, far as screen width*/
	public void set3DProjection(){

		final float ratio = Base.screenRatio;

		set3DProjection(ratio, -ratio, -1.0f, 1.0f, ratio, Base.screenWidth);
	}

    /**
     * creates 3D projection camera. camera view width as ratio and height is 1.0f
     * @param near,far in this area objects are visible
     * */
	public void set3DProjection(float near, float far){

        final float ratio = Base.screenRatio;

        set3DProjection(ratio, -ratio, -1.0f, 1.0f, near, far);
    }

    /**
     * creates 3D projection camera matrix
     * check Matrix.frustumM
     * */
    public void set3DProjection(float left, float right, float bottom, float top, float near, float far){

        nz = near;
        fz = far;
        Matrix.frustumM(VPMatrix[1], 0, left, right, bottom, top, near, far);
    }

    /**
     * creates 3D projection camera matrix
     * check Matrix.perspectiveM
     * */
    public void set3DProjection(float angle, float near, float far){

        nz = near;
        fz = far;
        Matrix.perspectiveM(VPMatrix[1], 0, angle, -Base.screenRatio, near, far);
    }

    void calcScreen(float screenSemiWidth){

        width = Math.abs(screenSemiWidth);
        height = width/Base.screenRatio;
        px = (width*2.0f)/Base.screenWidth;
        ratioX = (width*2.0f)/Base.screenWidth;
        ratioY = (height*2.0f)/Base.screenHeight;
        reverseRatio = Base.screenWidth/(width*2.0f);
    }

    /** creates view matrix front view based on z axe */
	private void setFrontView(float lookFrom){

        setLook(0, 0, lookFrom, 0, 0, 0, 0, 1.0f, 0);

        calcScreen(lookFrom);
	}

    /**
     * creates view matrix
     * check Matrix.setLookAtM
     * */
	public void setLook(float lookFromX, float lookFromY, float lookFromZ, float lookAtX, float lookAtY, float lookAtZ, float upX, float upY, float upZ){
		
		this.lookFrom.x = lookFromX;
		this.lookFrom.y = lookFromY;
		this.lookFrom.z = lookFromZ;
		this.lookAt.x = lookAtX;
		this.lookAt.y = lookAtY;
		this.lookAt.z = lookAtZ;
		this.up.x = upX;
		this.up.y = upY;
		this.up.z = upZ;

        Point3.copy(lookFrom, pos);

		reloadViewMatrix();
	}

    /**
     * creates view matrix
     * check Matrix.setLookAtM
     * */
	public void setLookFrom(float lookFromX, float lookFromY, float lookFromZ){
		
		this.lookFrom.x = lookFromX;
		this.lookFrom.y = lookFromY;
		this.lookFrom.z = lookFromZ;

        Point3.copy(lookFrom, pos);
		
		reloadViewMatrix();
	}

    /**
     * creates view matrix
     * check Matrix.setLookAtM
     * */
	public void setLookAt(float lookAtX, float lookAtY, float lookAtZ){
		
		this.lookAt.x = lookAtX;
		this.lookAt.y = lookAtY;
		this.lookAt.z = lookAtZ;
		
		reloadViewMatrix();
	}

    /**
     * creates view matrix
     * check Matrix.setLookAtM
     * */
	public void setDirection(float upX, float upY, float upZ){
		
		this.up.x = upX;
		this.up.y = upY;
		this.up.z = upZ;
		
		reloadViewMatrix();
	}

    /**
     * calculate view matrix
     * check Matrix.setLookAtM
     * */
    private void reloadViewMatrix(){

        Matrix.setLookAtM(VPMatrix[0], 0, lookFrom.x, lookFrom.y, lookFrom.z, lookAt.x, lookAt.y, lookAt.z, up.x, up.y, up.z);
    }
	
	/**
	 * resets View Matrix to its original position and rotation
	 */
	public void setIdentity(){
		
		setLook(0, 0, -width / 2.0f, 0, 0, 0, 0, -1.0f, 0);
	}

    /**
     * move with camera, apply traslate transformation onto View Matrix
     * @param x,y,z position
     * */
	public void translate(float x, float y, float z){

        pos.x = x;
        pos.y = y;
        pos.z = z;

        x -= lookFrom.x;
        y -= lookFrom.y;
        z -= lookFrom.z;

        BaseMatrix.translate(transformationMatrix, -x, -y, -z);
	}

    /** sets camera position, apply translate transformation onto View Matrix */
    public void translate(float x, float y){

        translate(x, y, pos.z);
    }

    /** gets current position of camera */
    public Point3 getPosition(){

        return pos;
    }

    public void setPosition(float x, float y, float z){

        pos.set(x, y, z);
    }

    public void clearRotations(){

        BaseMatrix.setIdentity(rotationMatrix);
    }

    /**
     *
     * */
	public void rotateOnOrbitY(float angle){

        Matrix.rotateM(rotationMatrix, 0, angle, 0, 1, 0);
	}

	/**
     *
     * */
	 public void rotateOnOrbitX(float angle){

        Matrix.rotateM(rotationMatrix, 0, angle, 1, 0, 0);
	}

    /**
     *
     * */
    public void rotateOnOrbitZ(float angle){

        Matrix.rotateM(rotationMatrix, 0, angle, 0, 0, 1);
    }

    public void rotate(float x, float y, float z){

        BaseMatrix.rotate(rotationMatrix, x, y, z);
    }

    /**
	 * apply scale transformation onto view matrix,
	 * zoom in/out, value 1.0f is no zoom, 2.0f is double size, 0.5f is half size, -1.0f is from opposite side 
	 * */
    public void zoom(float value){

        Matrix.scaleM(transformationMatrix, 0, value, value, value);
    }

    /** update camera, multiply view and projection matrix into final VPMatix */
    public void update(){

        Matrix.multiplyMM(transformationMatrix, 0, transformationMatrix, 0, rotationMatrix, 0);
        Matrix.multiplyMM(transformationMatrix, 0, VPMatrix[0], 0, transformationMatrix, 0);
        Matrix.multiplyMM(mVPMatrix, 0, VPMatrix[1], 0, transformationMatrix, 0);
        BaseMatrix.setIdentity(transformationMatrix);
    }

    public void billboard(float[] matrix){

        BaseMatrix.copy3(rotationMatrix, matrix);
    }

	/** sets multiplayed ViewProjectionMatrix */
    public void setVPMatrix(float[] multiplayedVPMatix){

        mVPMatrix = multiplayedVPMatix;
    }

    /** sets view matrix */
	public void setViewMatrix(float[] viewMatrix){
		
		VPMatrix[0] = viewMatrix;
	}

    /** sets projection matrix */
	public void setProjectionMatrix(float[] projectionMatrix){
		
		VPMatrix[1] = projectionMatrix;
	}

    /** sets multiplayed ViewProjectionMatrix */
	public void setVPMatrix(float[][] ViewProjectionMatrix){
		
		this.VPMatrix = VPMatrix;
	}

    /** get view matrix */
    public float[] getViewMatrix(){

        return VPMatrix[0];
    }

    /** get projection matrix */
    public float[] getProjectionMatrix(){

        return VPMatrix[1];
    }

    /**
     * get view and projection matrix,
     * [0] - view matrix
     * [1] - projection matrix
     * */
    public float[][] getViewProjectionMatrix(){

        return VPMatrix;
    }

    /** returns multiplayed ViewProjectionMatrix */
    public float[] getVPMatrix(){

        return mVPMatrix;
    }

    /** returns view semi width at x ax */
    public float getSemiWidth(){

        return width;
    }

    /** returns view semi height at y ax */
    public float getSemiHeight(){

        return height;
    }

    public float getWidth(){

        return width*2.0f;
    }

    public float getHeight(){

        return height*2.0f;
    }

    public float getSmallerSideSize(){

        return width < height ? width*2.0f : height*2.0f;
    }

    public float getLargerSideSize(){

        return width > height ? width*2.0f : height*2.0f;
    }

    /** returns value that represent one pixel at center of axes */
    public float getOnePx(){

        return px;
    }

    /** returns ratio to transform screen width into camera width */
    public float getRatioX(){

        return ratioX;
    }

    /** returns ratio to transform screen height into camera height */
    public float getRatioY(){

        return ratioY;
    }

    public float getNearZ() {

        return nz;
    }

    public float getFarZ() {

        return fz - Math.abs(lookFrom.z);
    }

    public Point3 getLookFrom(){

        return lookFrom;
    }

    public float getFarRatio(float reqZ){

        float z = Math.abs(lookFrom.z);
        return  (reqZ+z)/z;
    }

    /**
     * required distance from camera (-lookFrom.z - 1.0f will calculate ratio for position in -1.0f from center) ->
     * method getNearNegativeRatio will solve this for negative camera pos
     */
    public float getNearRatio(float reqZ){

        float z = Math.abs(lookFrom.z);
        return (reqZ)/z;
    }

    /**
     * works only for negative positioned camera
     * */
    public float getNearNegativeRatio(float distFromCenter){

        return getNearRatio(-lookFrom.z-Math.abs(distFromCenter));
    }

    public boolean equalsToScreenDimension(){

        return Base.screenWidth * ratioX == getWidth() && Base.screenHeight * ratioY == getHeight();
    }

    /* --- STATIC CONSTRUCTORS --- */

    public static BaseCamera ortho(float width){

        BaseCamera camera = new BaseCamera();
        camera.set2DCamera(width);

        return camera;
    }

    public static BaseCamera ortho(float width, float far){

        BaseCamera camera = new BaseCamera();
        camera.set2DCamera(width, far);

        return camera;
    }

    public static BaseCamera ortho(float width, float near, float far){

        BaseCamera camera = new BaseCamera();
        camera.set2DCamera(width, near, far);

        return camera;
    }

    public static BaseCamera ortho(float width, float height, float near, float far){

        BaseCamera camera = new BaseCamera();
        camera.set2DCamera(width, height, near, far);

        return camera;
    }

    public static BaseCamera frustrum(float width, float far){

        BaseCamera camera = new BaseCamera();
        camera.set3DCamera(width, far);

        return camera;
    }

    public static BaseCamera perspective(float viewAngle, float width, float far){

        BaseCamera camera = new BaseCamera();
        camera.set3DCamera(viewAngle, width, far);

        return camera;
    }

    public static BaseCamera perspective(float viewAngle, float width, float near, float far){

        BaseCamera camera = new BaseCamera();
        camera.set3DCamera(viewAngle, width, near, far);

        return camera;
    }
}
