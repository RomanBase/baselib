package com.base.lib.engine.common.file;

import android.content.res.AssetManager;

import com.base.lib.engine.Base;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;

/**
 *
 */
public abstract class BaseParser {

    public BaseParser(String file) {

        try {
            InputStream is = Base.appContext.getAssets().open(file, AssetManager.ACCESS_STREAMING);
            readFileFromIS(new BufferedInputStream(is));
            is.close();
        } catch (IOException e) {
            Base.logE("BeoParser", e.getMessage());
        }

    }

    public BaseParser(int resourceID) {

        try {
            InputStream is = Base.appContext.getResources().openRawResource(resourceID);
            readFileFromIS(new BufferedInputStream(is));
            is.close();
        } catch (IOException e) {
            Base.logE("BeoParser", e.getMessage());
        }
    }

    public BaseParser(byte[] bytes) {

        try {
            parse(new BufferedReader(new StringReader(new String(bytes))));
        } catch (IOException e) {
            Base.logE("BeoParser", e.getMessage());
        }
    }

    private void readFileFromIS(BufferedInputStream is) throws IOException {

        BufferedReader br = new BufferedReader(new InputStreamReader(new DataInputStream(is)));
        parse(br);
        br.close();
        is.close();
    }

    protected abstract void parse(BufferedReader br) throws IOException;

}
