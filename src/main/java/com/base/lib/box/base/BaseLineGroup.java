package com.base.lib.box.base;

import com.base.lib.engine.common.TrainedMonkey;

/**
 *
 */
public class BaseLineGroup {

    public BaseLine[] lines;
    private int index;

    public BaseLineGroup(){}

    public BaseLineGroup(BaseLine[] lines){
        this.lines = lines;
    }

    public void initB2ChainByName(String name, Object userData, float offsetPosX, float offsetPosY){

        for(BaseLine line : lines){
            if(name.equals(line.name)){
                line.intoB2Chain(userData, offsetPosX, offsetPosY);
            }
        }
    }

    /** note: return null if no more object with requested name */
    public BaseLine getNext(String name){

        if(index < lines.length) {
            for (int i = index; i < lines.length; i++) {
                if (name.equals(lines[i].name)) {
                    index = i + 1;
                    return lines[i];
                }
            }

        }

        index = 0;
        return null;
    }

    /** note: slow if lots of objects */
    public static String[] getUniqueNames(BaseLineGroup group){

        String[] out = new String[1];
        out[0] = group.lines[0].name;

        for(int i = 1; i<group.lines.length; i++){
            boolean contains = false;
            for(String in : out){
                if(group.lines[i].name.equals(in)){
                    contains = true;
                    break;
                }
            }
            if(!contains){
                out = TrainedMonkey.arrayUp(out, group.lines[i].name);
            }
        }

        return out;
    }
}
