package com.base.lib.engine.audio;

import android.content.Context;
import android.media.AudioManager;
import android.os.Build;

import com.base.lib.engine.Base;

/**
 *
 */
public class AudioHelper {

    /**lastly updated at API 14*/
    public static boolean isSupported(String format){

        if(!format.startsWith(".")){
            format = "."+format;
        }

        if(format.equals(".mp3")){
            return true;
        } else if(format.equals(".ogg")){
            return true;
        } else if(format.equals(".wav")){
            return true;
        } else if(format.equals(".3gp")){
            return true;
        } else if(format.equals(".mp4")){
            return true;
        } else if(format.equals(".m4a")){
            return true;
        } else if(format.equals(".aac") && Build.VERSION.SDK_INT >= 12){
            return true;
        } else if(format.equals(".ts") && Build.VERSION.SDK_INT >= 11){
            return true;
        } else if(format.equals(".flac")){
            return true;
        } else if(format.equals(".mid")){
            return true;
        } else if(format.equals(".xmf")){
            return true;
        } else if(format.equals(".mxmf")){
            return true;
        } else if(format.equals(".rtttl")){
            return true;
        } else if(format.equals(".rtx")){
            return true;
        } else if(format.equals(".ota")){
            return true;
        } else if(format.equals(".mkv") && Build.VERSION.SDK_INT >= 14){
            return true;
        }

        return false;
    }

    public static float getCurrentVolume(){

        AudioManager audioManager = (AudioManager) Base.appContext.getSystemService(Context.AUDIO_SERVICE);
        float actualVolume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        float maxVolume = (float) audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        return actualVolume / maxVolume;
    }
}
