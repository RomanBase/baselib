package com.base.lib.engine.font;

import com.base.lib.engine.BaseTexture;
import com.base.lib.engine.common.file.BaseParser;

import java.io.BufferedReader;
import java.io.IOException;

/**
 *
 */
public class BitmapFontParser extends BaseParser {

    private BitmapFont font;
    private String textureName;

    public BitmapFontParser(String file) {
        super(file);
    }

    public BitmapFontParser(int resourceID) {
        super(resourceID);
    }

    public BitmapFontParser(byte[] bytes) {
        super(bytes);
    }

    @Override
    protected void parse(BufferedReader br) throws IOException {

        font = new BitmapFont();
        int textureWidth = 0;
        int textureHeight = 0;
        String line;
        String[] token;
        while ((line = br.readLine()) != null){
            token = line.split(" ");

            if(line.startsWith("chars ")){
                font.initCharArray(valueOf(token[1])+1);
                parseChar(br, textureWidth, textureHeight);
                continue;
            }

            if(line.startsWith("page ")){
                textureName = token[2].split("=")[1].replace('"', ' ').trim();
                continue;
            }

            if(line.startsWith("common ")){
                textureWidth = valueOf(token[3]);
                textureHeight = valueOf(token[4]);
                font.setLineProperties(valueOf(token[1]), valueOf(token[2]));
                continue;
            }

            if(line.startsWith("info ")){
                font.size = valueOf(line.split(" ")[2]);
            }
        }

        font.reArrange();
    }

    private void parseChar(BufferedReader br, int textureWidth, int textureHeight) throws IOException {

        int count = font.chars.length;
        String[] token;
        for (int i = 0; i<count; i++) {
            token = br.readLine().split(" ");
            BitmapLetter ch = font.newChar(i);
            ch.setCh((char) valueOf(token[1]));
            ch.initTextureCoords(valueOf(token[2]), valueOf(token[3]),
                                 valueOf(token[4]), valueOf(token[5]),
                                 textureWidth, textureHeight);
            ch.setPositioning(valueOf(token[6]), valueOf(token[7]), valueOf(token[8]));
        }
    }

    private int valueOf(String token){

        return Integer.parseInt(token.split("=")[1]);
    }

    public String getTextureName() {
        return textureName;
    }

    public BitmapFont getFont(){

        return font;
    }

    public BitmapFont getFont(BaseTexture texture){

        font.setTexture(texture);

        return font;
    }
}
