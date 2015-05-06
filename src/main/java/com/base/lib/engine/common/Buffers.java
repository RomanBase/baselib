package com.base.lib.engine.common;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;

public class Buffers {


    public static final int BYTESPERCHAR = 2;
    public static final int BYTESPERSHORT = 2;
    public static final int BYTESPERINT = 4;
    public static final int BYTESPERFLOAT = 4;
    public static final int BYTESPERDOUBLE = 8;
    public static final int BYTESPERLONG = 8;


    /**
     * creates a ByteBuffer, allocate memory block, put byte array into buffer and sets position to 0
     * @param array bites to put
     * @return a ByteBuffer
     * */
    public static ByteBuffer byteBuffer(byte[] array) {

        ByteBuffer buffer;
        buffer = ByteBuffer.allocateDirect(array.length)
                .order(ByteOrder.nativeOrder());
        buffer.put(array).position(0);

        return buffer;
    }

    /**
     * creates a ByteBuffer as CharBuffer, allocate memory block, put char array into buffer and sets position to 0
     * @param array bites to put
     * @return a ByteBuffer
     * */
    public static CharBuffer charBuffer(char[] array) {

        CharBuffer buffer;
        buffer = ByteBuffer.allocateDirect(array.length * BYTESPERCHAR)
                .order(ByteOrder.nativeOrder()).asCharBuffer();
        buffer.put(array).position(0);

        return buffer;
    }

    /**
     * creates a ByteBuffer as ShortBuffer, allocate memory block, put short array into buffer and sets position to 0
     * @param shortArray array to put
     * @return a ShortBuffer
     * */
    public static ShortBuffer shortBuffer(short[] shortArray) {

        ShortBuffer buffer;
        buffer = ByteBuffer.allocateDirect(shortArray.length * BYTESPERSHORT)
                .order(ByteOrder.nativeOrder()).asShortBuffer();
        buffer.put(shortArray).position(0);

        return buffer;
    }

    /**
     * creates a ByteBuffer, allocate memory block, put byte array into buffer and sets position to 0
     * @param array bites to put
     * @return a ByteBuffer
     * */
    public static IntBuffer intBuffer(int[] array) {

        IntBuffer buffer;
        buffer = ByteBuffer.allocateDirect(array.length * BYTESPERINT)
                .order(ByteOrder.nativeOrder()).asIntBuffer();
        buffer.put(array).position(0);

        return buffer;
    }

    /**
     * creates a ByteBuffer as FloatBuffer, allocate memory block, put float array into buffer and sets position to 0
     * @param floatArray array to put
     * @return a FloatBuffer
     * */
	public static FloatBuffer floatBuffer(float[] floatArray) {

		FloatBuffer buffer;
		buffer = ByteBuffer.allocateDirect(floatArray.length * BYTESPERFLOAT)
				.order(ByteOrder.nativeOrder()).asFloatBuffer();
		buffer.put(floatArray).position(0);
		
		return buffer;
	}

    /**
     * creates a ByteBuffer as DoubleBuffer, allocate memory block, put double array into buffer and sets position to 0
     * @param array bites to put
     * @return a ByteBuffer
     * */
    public static DoubleBuffer doubleBuffer(double[] array) {

        DoubleBuffer buffer;
        buffer = ByteBuffer.allocateDirect(array.length * BYTESPERDOUBLE)
                .order(ByteOrder.nativeOrder()).asDoubleBuffer();
        buffer.put(array).position(0);

        return buffer;
    }

    /**
     * creates a ByteBuffer as LongBuffer, allocate memory block, put long array into buffer and sets position to 0
     * @param array bites to put
     * @return a ByteBuffer
     * */
    public static LongBuffer longBuffer(long[] array) {

        LongBuffer buffer;
        buffer = ByteBuffer.allocateDirect(array.length * BYTESPERLONG)
                .order(ByteOrder.nativeOrder()).asLongBuffer();
        buffer.put(array).position(0);

        return buffer;
    }

    public static FloatBuffer floatStrideBuffer(float[] vertices, float[] textures){

        float[] data = new float[vertices.length + textures.length];
        int iv = 0;
        int it = 0;
        for(int i = 0; i<data.length;){
            data[i++] = vertices[iv++];
            data[i++] = vertices[iv++];
            data[i++] = vertices[iv++];

            data[i++] = textures[it++];
            data[i++] = textures[it++];
        }

        return floatBuffer(data);
    }

    public static FloatBuffer floatStrideBuffer(float[] vertices, float[] normals, float[] textures){

        float[] data = new float[vertices.length + normals.length + textures.length];
        int iv = 0;
        int in = 0;
        int it = 0;
        for(int i = 0; i<data.length;){
            data[i++] = vertices[iv++];
            data[i++] = vertices[iv++];
            data[i++] = vertices[iv++];

            data[i++] = normals[in++];
            data[i++] = normals[in++];
            data[i++] = normals[in++];

            data[i++] = textures[it++];
            data[i++] = textures[it++];
        }

        return floatBuffer(data);
    }
}
