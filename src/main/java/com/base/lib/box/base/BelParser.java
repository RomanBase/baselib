package com.base.lib.box.base;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;

import com.base.lib.engine.Base;
import com.base.lib.engine.common.file.FileHelper;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;

/**
 *
 */
public class BelParser {

    private static final double PARSERVERSION = 0.256;
    private static final String OBJECTINFO = "ob ";
    private static final String FILEVERSION = "version ";

    private static final String LINES = "l ";
    private static final String SENSORS = "s ";
    private static final String BLOCKENDS = "}";

    private BaseLine[] lines;
    private BaseSensor[] sensors;

    private BelParser(){}

    public BelParser(Context context, String file) {

        try {
            InputStream is = context.getAssets().open(file, AssetManager.ACCESS_STREAMING);
            readFileFromIS(new BufferedInputStream(is));
        } catch (IOException e) {
            Base.logE("BelParser", e.getMessage());
        }

    }

    public BelParser(Context context, int resourceID) {

        try {
            InputStream is = context.getResources().openRawResource(resourceID);
            readFileFromIS(new BufferedInputStream(is));
        } catch (IOException e) {
            Base.logE("BelParser", e.getMessage());
        }
    }

    public BelParser(byte[] bytes) {

        try {
            parser256(new BufferedReader(new StringReader(new String(bytes))));
        } catch (IOException e) {
            Base.logE("BelParser", e.getMessage());
        }
    }

    private void readFileFromIS(BufferedInputStream is) throws IOException {

        BufferedReader br = new BufferedReader(new InputStreamReader(new DataInputStream(is)));
        parser256(br);
        br.close();
        is.close();
    }

    private void parser256(BufferedReader br) throws IOException {

        lines = new BaseLine[256];
        int currentIndex = -1;
        int cpv = 2;
        String line;

        while ((line = br.readLine()) != null) {

            /** l */
            if(line.startsWith(LINES)){

                String[] token = line.split(" ");

                String[] verts = new String[Integer.parseInt(token[1])];
                System.arraycopy(token, 3, verts, 0, verts.length);

                int index = 0;

                lines[currentIndex].vertices = new float[verts.length];
                lines[currentIndex].length = verts.length/cpv - 1;
                for(int i = 0; i<verts.length; i++){

                    lines[currentIndex].vertices[i] = Float.parseFloat(verts[i]);
                }
            }

            /** s */
            if(line.startsWith(SENSORS)){

                String[] token = line.split(" ");

                sensors = new BaseSensor[Integer.parseInt(token[1])];

                int index = 0;
                while (!BLOCKENDS.equals(line = br.readLine())){
                    token = line.split(" ");

                    BaseSensor sensor = new BaseSensor();
                    sensor.name = token[0];
                    sensor.posX = Float.parseFloat(token[1]);
                    sensor.posY = Float.parseFloat(token[2]);
                    sensor.sizeX = Float.parseFloat(token[3]);
                    sensor.sizeY = Float.parseFloat(token[4]);
                    sensor.rotZ = Float.parseFloat(token[5]);

                    sensors[index++] = sensor;
                }
            }

            /**--------- HEADER ------------*/
            /** ob */
            if (line.startsWith(OBJECTINFO)) {

                String[] token = line.split(" ");
                lines[++currentIndex] = new BaseLine(token[1]);
                if(token.length > 2){
                    cpv = Integer.parseInt(token[2]);
                }
                continue;
            }

            /** version */
            if (line.startsWith(FILEVERSION)) {
                final double version = Double.parseDouble(line.substring(line.indexOf(" ") + 1));
                if (version != PARSERVERSION) {
                    Base.logE("BelParser", "!"
                            + "\nParser version: " + PARSERVERSION
                            + "\nFile version: " + version
                            + "\nIt's possible that Monkeys don't read this file preciously..");
                }
            }
        }

        BaseLine[] temp = new BaseLine[currentIndex+1];
        System.arraycopy(lines, 0, temp, 0, temp.length);
        lines = temp;
    }

    public BaseLine[] getLines(){

        return lines;
    }

    public BaseLine getLine(String name){

        for(BaseLine line : lines){
            if(line.name.equals(name)){
                return line;
            }
        }

        return null;
    }

    public BaseLineGroup getLineGroup(){

        return new BaseLineGroup(lines);
    }

    public BaseSensor[] getSensors(){

        return sensors;
    }

    public BaseSensorGroup getSensorGroup(){

        return new BaseSensorGroup(sensors);
    }

    public static BelParser assets(Context context, String path){

        return new BelParser(context, path);
    }

    public static BelParser resource(Context context, int resourceId){

        return new BelParser(context, resourceId);
    }

    public static BelParser internal(String name){

        return new BelParser(FileHelper.loadInternal(name));
    }

    public static BelParser sd(String path) {

        BelParser parser = null;

        if (FileHelper.sdFileExist(path)) {

            try {
                File file = new File(Environment.getExternalStorageDirectory() + "/" + path);
                BufferedInputStream br = new BufferedInputStream(new FileInputStream(file));

                parser = new BelParser();
                parser.readFileFromIS(br);

            } catch (IOException e) {
                Base.logE("File: " + path + " cannot be read: " + e.getMessage());
            }
        }

        return parser;
    }
}
