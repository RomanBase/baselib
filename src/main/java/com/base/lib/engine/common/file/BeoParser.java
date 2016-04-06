package com.base.lib.engine.common.file;

import android.content.res.AssetManager;
import android.os.Environment;

import com.base.lib.engine.Base;
import com.base.lib.engine.animation.BaseSkelet;
import com.base.lib.engine.animation.Bone;
import com.base.lib.engine.common.BaseDrawableData;
import com.base.lib.engine.common.other.Point3;
import com.base.lib.engine.common.other.TrainedMonkey;
import com.base.lib.engine.animation.VertGroup;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;


public class BeoParser {

    private static final double PARSERVERSION = 0.256;

    private static final String OBJECTINFO = "ob ";
    private static final String FILEVERSION = "version ";
    private static final String COORDSPERVERTICE = "cpv ";
    private static final String BOUNCINGBOX = "bbox ";

    private static final String FRAME = "#f";
    private static final String VERTICES = "v ";
    private static final String NORMALS = "n ";
    private static final String TEXTURES = "t ";
    private static final String FACES = "f ";
    private static final String TEXTUREFILE = "texture ";

    private static final String FRAMETION = "af ";
    private static final String ANIMATION = "a ";
    private static final String WEIGHT = "w ";
    private static final String SKELET = "skelet ";
    private static final String FRAMESCOUNT = "frames ";

    private static final String BLOCKENDS = "}";

    private BaseDrawableData data[];
    private Bone[] bones;
    private short[][] faceOrder;
    private VertGroup[] groups;
    private int objectsCount;
    private int cFaceIndex;
    private int cFrameIndex;
    private boolean skeletAnim = false;
    private boolean frameAnim = false;

    private BeoParser(){}

    public BeoParser(String file) {

        try {
            InputStream is = Base.appContext.getAssets().open(file, AssetManager.ACCESS_STREAMING);
            readFileFromIS(new BufferedInputStream(is));
            is.close();
        } catch (IOException e) {
            Base.logE("BeoParser", e.getMessage());
        }

    }

    public BeoParser(int resourceID) {

        try {
            InputStream is = Base.appContext.getResources().openRawResource(resourceID);
            readFileFromIS(new BufferedInputStream(is));
            is.close();
        } catch (IOException e) {
            Base.logE("BeoParser", e.getMessage());
        }
    }

    public BeoParser(byte[] bytes) {

        try {
            parser256(new BufferedReader(new StringReader(new String(bytes))));
        } catch (IOException e) {
            Base.logE("BeoParser", e.getMessage());
        }
    }

    private void readFileFromIS(BufferedInputStream is) throws IOException {

        BufferedReader br = new BufferedReader(new InputStreamReader(new DataInputStream(is)));
        parser256(br);
        br.close();
        is.close();
    }

    private void parser256(BufferedReader br) throws IOException {

        data = new BaseDrawableData[0];
        int currentIndex = -1;
        String line;

        while ((line = br.readLine()) != null) {

            /** v */
            if (line.startsWith(VERTICES)) {

                String[] token = line.split(" ");
                int count = Integer.parseInt(token[1]);
                data[currentIndex].vertices = new float[count];

                if (skeletAnim) {
                    int index = 0;
                    int groupIndex = 0;
                    while (!BLOCKENDS.equals(line = br.readLine())) {

                        String[] vtoken = line.split(" ");
                        int vcount = Integer.parseInt(vtoken[1]);
                        groups[groupIndex] = new VertGroup();
                        groups[groupIndex].name = vtoken[0];
                        groups[groupIndex].verts = new float[vcount];
                        for (int i = 0; i < vcount; i++) {
                            float vert = Float.parseFloat(vtoken[i + 2]);
                            data[currentIndex].vertices[index++] = vert;
                            groups[groupIndex].verts[i] = vert;
                        }
                        groupIndex++;
                    }
                } else if (frameAnim) {
                    int index = 0;
                    int groupIndex = 0;
                    while (!BLOCKENDS.equals(line = br.readLine())) {

                        String[] vtoken = line.split(" ");
                        groups[groupIndex] = new VertGroup();
                        groups[groupIndex].name = vtoken[0];
                        groups[groupIndex].verts = new float[count];
                        for (int i = 0; i < count; i++) {
                            float vert = Float.parseFloat(vtoken[i + 1]);
                            groups[groupIndex].verts[i] = vert;
                        }
                        groupIndex++;
                    }
                } else {
                    for (int i = 0; i < count; i++) {
                        data[currentIndex].vertices[i] = Float.parseFloat(token[i + 3]);
                    }
                }

                continue;
            }

            /** n */
            if(line.startsWith(NORMALS)){
                String[] token = line.split(" ");

                int count = Integer.parseInt(token[1]);
                data[currentIndex].normals = new float[count];

                for (int i = 0; i < count; i++) {
                    data[currentIndex].normals[i] = Float.parseFloat(token[i + 3]);
                }

                continue;
            }

            /** t */
            if (line.startsWith(TEXTURES)) {

                String[] token = line.split(" ");

                int count = Integer.parseInt(token[1]);
                data[currentIndex].textures = new float[count];

                for (int i = 0; i < count; i++) {
                    data[currentIndex].textures[i] = Float.parseFloat(token[i + 3]);
                }

                continue;
            }

            /** f */
            if (line.startsWith(FACES)) {

                String[] token = line.split(" ");

                int count = Integer.parseInt(token[1]);
                if (objectsCount == 0) {
                    data[currentIndex].faceOrder = new short[count];

                    for (int i = 0; i < count; i++) {
                        data[currentIndex].faceOrder[i] = Short.parseShort(token[i + 3]);
                    }
                } else {
                    faceOrder[cFaceIndex] = new short[count];
                    for (int i = 0; i < count; i++) {
                        faceOrder[cFaceIndex][i] = Short.parseShort(token[i + 3]);
                    }
                    cFaceIndex++;
                }

                continue;
            }

            /** #f */ //static animation
            if (line.startsWith(FRAME)) {

                String[] token = line.split(" ");

                int count = Integer.parseInt(token[1]);
                float[] frame = new float[count];

                for (int i = 0; i < count; i++) {
                    frame[i] = Float.parseFloat(token[i + 1]);
                }

                groups[++cFrameIndex].verts = frame;
            }

            /** w */
            if (line.startsWith(WEIGHT)){

                continue;
            }

            /** skelet */
            if (line.startsWith(SKELET)){

                String[] skeletInfo = line.split(" ");

                int count = Integer.parseInt(skeletInfo[1]);
                bones = new Bone[count];

                int i = 0;
                while (!BLOCKENDS.equals(line = br.readLine())){

                    String[] token = line.split(" ");
                    Bone bone = new Bone();

                    int parentIndex = Integer.parseInt(token[1]);
                    if(parentIndex != -1){
                        bone.setParent(bones[parentIndex]);
                    }

                    //bone.weight = Float.parseFloat(token[2]);

                    bone.setHead(new Point3(
                            Float.parseFloat(token[4]),
                            Float.parseFloat(token[5]),
                            Float.parseFloat(token[6]))
                    );

                    bone.setTail(new Point3(
                            Float.parseFloat(token[7]),
                            Float.parseFloat(token[8]),
                            Float.parseFloat(token[9]))
                    );

                    bone.setName(token[0]);
                    bones[i++] = bone;
                }

                continue;
            }

            /** frameAction */
            if (line.startsWith(FRAMESCOUNT)) {

                String[] actionInfo = line.split(" ");
                int count = Integer.parseInt(actionInfo[1]);

                for (Bone bone : bones){
                    Point3[] points = new Point3[count];
                    for(int i = 0; i<points.length; i++){
                        points[i] = new Point3();
                    }
                    bone.setFramesAction(points);
                }

                int f = 0;
                while (!BLOCKENDS.equals(line = br.readLine())){

                    String[] token = line.split(" ");
                    int i = 0;
                    for (Bone bone : bones) {
                        bone.setFrameAction(f,
                                Float.parseFloat(token[i++]),
                                Float.parseFloat(token[i++]),
                                Float.parseFloat(token[i++])
                        );
                    }
                    f++;
                }
            }

            /**--------- HEADER ------------*/
            /** ob */
            if (line.startsWith(OBJECTINFO)) {

                data = TrainedMonkey.arrayUp(data, new BaseDrawableData());
                data[++currentIndex].name = line.split(" ")[1];
                continue;
            }

            /** cpv */
            if (line.startsWith(COORDSPERVERTICE)) {

                int cpv = Integer.parseInt(line.substring(line.indexOf(" ") + 1));
                if (cpv == 2) {
                    data[currentIndex].use2Dvertices();
                } else if (cpv == 3) {
                    data[currentIndex].use3Dvertices();
                } else {
                    throw new RuntimeException("Error parsing .beo file. \n" + "Monkeys can't resolve 'cpv' attribute in .beo file (cpv must be 2 or 3)");
                }
                continue;
            }

            /** bbox */
            if (line.startsWith(BOUNCINGBOX)) {

                String[] token = line.split(" ");

                data[currentIndex].sizeX = Float.parseFloat(token[1]);
                data[currentIndex].sizeY = Float.parseFloat(token[2]);
                data[currentIndex].sizeZ = Float.parseFloat(token[3]);

                continue;
            }

            /** a */
            if (line.startsWith(ANIMATION)) {

                skeletAnim = true;
                String[] token = line.split(" ");
                groups = new VertGroup[Integer.parseInt(token[1])];
            }

            /** af */
            if (line.startsWith(FRAMETION)){

                frameAnim = true;
                String[] token = line.split(" ");
                groups = new VertGroup[Integer.parseInt(token[1])];
            }

            /** texture */ //unused
            if (line.startsWith(TEXTUREFILE)) {

                //data.textureFile.add(line.substring(line.indexOf(" ")+1));
                continue;
            }

            /** version */
            if (line.startsWith(FILEVERSION)) {
                final double version = Double.parseDouble(line.substring(line.indexOf(" ") + 1));
                if (version != PARSERVERSION) {
                    Base.logE("BeoParser", "!"
                            + "\nParser version: " + PARSERVERSION
                            + "\nFile version: " + version
                            + "\nIt's possible that Monkeys don't read this file preciously..");
                }
            }

        }
    }

    public BaseDrawableData getBaseDrawableData() {

        return data[0];
    }

    public BaseDrawableData getBaseDrawableData(int index) {

        return data[index];
    }

    public BaseDrawableData[] getData(){

        return data;
    }

    public BaseSkelet getSkelet(){

        return new BaseSkelet(bones, groups);
    }

    public VertGroup[] getVertGroups(){

        return groups;
    }

    public BaseDrawableData getObject(String name){

        for(BaseDrawableData bdd : data){
            if(name.equals(bdd.name)){
                return bdd;
            }
        }

        return null;
    }

    public static BeoParser assets(String path){

        return new BeoParser(path);
    }

    public static BaseDrawableData dataAssets(String path){

        return new BeoParser(path).getBaseDrawableData();
    }

    public static BaseDrawableData[] multiDataAssts(String path){

        return new BeoParser(path).getData();
    }

    public static BeoParser resource(int resourceId){

        return new BeoParser(resourceId);
    }

    public static BeoParser internal(String name){

        return new BeoParser(FileHelper.loadInternal(name));
    }

    public static BeoParser sd(String path) {

        BeoParser parser = null;

        if (FileHelper.sdFileExist(path)) {

            try {
                File file = new File(Environment.getExternalStorageDirectory() + "/" + path);
                BufferedInputStream br = new BufferedInputStream(new FileInputStream(file));

                parser = new BeoParser();
                parser.readFileFromIS(br);

            } catch (IOException e) {
                Base.logE("File: " + path + " cannot be read: " + e.getMessage());
            }
        }

        return parser;
    }

}
