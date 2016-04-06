package com.base.lib.engine.animation;

/**
 * 18 Created by doctor on 3.2.14.
 */
public class VertGroup {

    public String name;
    public float[] verts;
    public int startIndex;

    public VertGroup(){

    }

    public VertGroup(String name){
        this.name = name;
    }

    public VertGroup(String name, float[] verts){
        this.name = name;
        this.verts = verts;
    }

    public static VertGroup getGroup(String name, VertGroup[] groups){

        for(VertGroup group : groups){
            if(name.equals(group.name)){
                return group;
            }
        }

        return null;
    }

    public static int startIndex(VertGroup group, VertGroup[] groups){

        int index = 0;
        for (VertGroup g : groups){
            if(g == group){
                break;
            } else {
                index += g.verts.length;
            }
        }

        return index;
    }

    public static float[][] getVerts(VertGroup[] groups){

        float[][] out = new float[groups.length][];
        for(int i = 0; i<groups.length; i++){
            out[i] = groups[i].verts;
        }

        return out;
    }

    public static int getVertsCount(VertGroup[] groups){

        int out = 0;
        for(VertGroup group : groups){
            out += group.verts.length;
        }

        return out;
    }
}
