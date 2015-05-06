package com.base.lib.engine.common;

public class BaseDrawableData {

    public String name;

	public float[] vertices;
    public float[] textures;
    public float[] normals;

    public short[] faceOrder;

    public float sizeX;
    public float sizeY;
    public float sizeZ;

    public int cpv; // coords per vertice ( 2 - 2D, 3 - 3D )
	
	public void setDrawableData(BaseDrawableData data){

        vertices = data.vertices;
        textures = data.textures;
        normals = data.normals;
        faceOrder = data.faceOrder;

		sizeX = data.sizeX;
		sizeY = data.sizeY;
		sizeZ = data.sizeZ;
        cpv = data.cpv;
	}

    public void addDrawableData(BaseDrawableData data){

        if (vertices == null) {
            setDrawableData(data);
        } else {

            int faceMul = vertices.length / cpv;
            for (int i = 0; i < data.faceOrder.length; i++) {
                data.faceOrder[i] += faceMul;
            }

            short[] temps = new short[faceOrder.length + data.faceOrder.length];
            System.arraycopy(faceOrder, 0, temps, 0, faceOrder.length);
            System.arraycopy(data.faceOrder, 0, temps, faceOrder.length, data.faceOrder.length);
            faceOrder = null;
            faceOrder = temps;

            float[] temp = new float[vertices.length + data.vertices.length];
            System.arraycopy(vertices, 0, temp, 0, vertices.length);
            System.arraycopy(data.vertices, 0, temp, vertices.length, data.vertices.length);
            vertices = null;
            vertices = temp;


            if (data.textures != null) {
                temp = new float[textures.length + data.textures.length];
                System.arraycopy(textures, 0, temp, 0, textures.length);
                System.arraycopy(data.textures, 0, temp, textures.length, data.textures.length);
                textures = null;
                textures = temp;
            }
        }
    }

    // face normal
    public void calculateNormals(){ //todo

        normals = new float[faceOrder.length];
        int index = 0;
        for(int i = 0; i<faceOrder.length;){
            int i1 = faceOrder[i++]*3; //v1
            Point3 v1 = new Point3(vertices[i1], vertices[i1+1], vertices[i1+2]);
            int i2 = faceOrder[i++]*3; //v2 -> u
            Point3 u = new Point3(vertices[i2], vertices[i2+1], vertices[i2+2]);
            int i3 = faceOrder[i++]*3; //v3 -> v
            Point3 v = new Point3(vertices[i3], vertices[i3+1], vertices[i3+2]);

            Point3.sub(u, v1);
            Point3.sub(v, v1);

            Point3 n = new Point3(u.y*v.z - u.z*v.y,
                                  u.z*v.x - u.x*v.z,
                                  u.x*v.y - u.y*v.x);
            n.normalize();

            putNormal(n, index++);
        }
    }

    private void putNormal(Point3 n, int position){

        normals[position  ] = n.x;
        normals[position+1] = n.y;
        normals[position+2] = n.z;
    }

    public void use2Dvertices(){

        cpv = 2;
    }

    public void use3Dvertices(){

        cpv = 3;
    }

	public void mirrorX(){

		for(int i = 0; i<vertices.length; i += cpv){
			
			vertices[i] *= -1.0f;
		}
		
		flipDrawOrder();
	}

    public void mirrorY(){

        for(int i = 1; i<vertices.length; i += cpv){

            vertices[i] *= -1.0f;
        }

        flipDrawOrder();
    }

    public void mirrorZ(){

        for(int i = 2; i<vertices.length; i += cpv){

            vertices[i] *= -1.0f;
        }

        flipDrawOrder();
    }

    public void flipDrawOrder() {

        int dSize = faceOrder.length;
        short[] fliped = new short[dSize];

        for (int i = 0; i < dSize; i++) {

            fliped[dSize - 1 - i] = faceOrder[i];
        }

        faceOrder = fliped;
    }

    public void setOrigin(float x, float y){

        x *= -1;
        y *= -1;
        for(int i = 0; i<vertices.length; i+= cpv){

            vertices[i] += x;
            vertices[i+1] += y;
        }
    }

    public void setOrigin(float x, float y, float z){

        x *= -1;
        y *= -1;
        z *= -1;

        for(int i = 0; i<vertices.length; i+= cpv){

            vertices[i] += x;
            vertices[i+1] += y;
            vertices[i+2] += z;
        }
    }

    public void convertTo3D(){

        float[] temp = new float[vertices.length/cpv*3];

        int index = 0;
        for (int i = 0; i < vertices.length; i += cpv) {

            temp[index++] += vertices[i];
            temp[index++] += vertices[i+1];
            temp[index++] += 0;
        }

        cpv = 3;
        vertices = temp;
    }
	
	public void preScale(float scale){
		
		sizeX *= scale;
		sizeY *= scale;
		sizeZ *= scale;
		
		for(int i = 0; i<vertices.length; i++){
			
			vertices[i] *= scale;
		}
	}
	
	public void preScaleX(float scale){
		
		sizeX *= scale;
		
		for(int i = 0; i<vertices.length; i += cpv){
			
			vertices[i] *= scale;
		}
	}
	
	public void preScaleY(float scale){
		
		sizeY *= scale;
		
		for(int i = 1; i<vertices.length; i += cpv){
			
			vertices[i] *= scale;
		}
	}
	
	public void preScaleZ(float scale){
		
		sizeZ *= scale;
		
		for(int i = 2; i<vertices.length; i += cpv){
			
			vertices[i] *= scale;
		}
	}
	
	public void setSizeX(float size, boolean keepAspectRatio){
		
		float scale = size / sizeX;
		
		if(keepAspectRatio){
			preScale(scale);
		} else {
			preScaleX(scale);
		}
	}
	
	public void setSizeY(float size, boolean keepAspectRatio){
		
		float scale = size / sizeY;
		
		if(keepAspectRatio){
			preScale(scale);
		} else {
			preScaleY(scale);
		}
	}
	
	public void setSizeZ(float size, boolean keepAspectRatio){
		
		float scale = size / sizeZ;
		
		if(keepAspectRatio){
			preScale(scale);
		} else {
			preScaleZ(scale);
		}
	}

    public void setSizeX(float size){

        this.sizeX = size;
    }

    public   void setSizeY(float size){

        this.sizeY = size;
    }

    public void setSizeZ(float size){

        this.sizeZ = size;
    }
	
	public float getSizeX(){

		return sizeX;
	}
	
	public float getSizeY(){

		return sizeY;
	}
	
	public float getSizeZ(){

		return sizeZ;
	}

    public float[] getVertices() {
        return vertices;
    }

    public float[] getTextures() {
        return textures;
    }

    public short[] getFaceOrder() {
        return faceOrder;
    }

    public void setVertices(float[] vertices) {
        this.vertices = vertices;
    }

    public void setTextures(float[] textures) {
        this.textures = textures;
    }

    public void setFaceOrder(short[] faceOrder) {
        this.faceOrder = faceOrder;
    }

    public float[] getNormals() {
        return normals;
    }

    public void setNormals(float[] normals) {
        this.normals = normals;
    }

    public BaseDrawableData getData(){

        return this;
    }
}


