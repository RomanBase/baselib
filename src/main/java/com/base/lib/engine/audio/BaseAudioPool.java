package com.base.lib.engine.audio;

import android.content.res.AssetManager;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;

import com.base.lib.engine.Base;
import com.base.lib.engine.common.TrainedMonkey;
import com.base.lib.interfaces.ActivityStateListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BaseAudioPool implements ActivityStateListener {

    private SoundPool soundpool;
    private int[] audioID;
    private float leftVolume;
    private float rightVolume;
    private String[] audioList;

    /**
     * loads all .mp3, .ogg, .wav files from selected assets folder
     * fill and prepare SoundPool
     * listens activity life cycle and coresponds with corect actions
     */
    public BaseAudioPool(String assetFolder) {

        List<String> audioList = new ArrayList<String>();
        try {
            String fileList[] = Base.context.getResources().getAssets().list(assetFolder);

            for (String file : fileList) {
                String suffix = file.substring(file.lastIndexOf("."), file.length());

                if (AudioHelper.isSupported(suffix)) {
                    audioList.add(assetFolder + "/" + file);
                }
            }

            initAssets(TrainedMonkey.toStringArray(audioList));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * loads all audio files from String[] array ( file path in assets folder )
     * fill and prepare SoundPool
     */
    public BaseAudioPool(String[] audioFiles) {

        try {
            initAssets(audioFiles);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initAssets(String[] audioFiles) throws IOException {
        Base.activity.addActivityStateListener(this);

        final int count = audioFiles.length;
        audioList = audioFiles;

        if (Build.VERSION.SDK_INT >= 21) {
            soundpool = new SoundPool.Builder().setAudioAttributes(
                    new AudioAttributes.Builder()
                            .setFlags(AudioAttributes.FLAG_AUDIBILITY_ENFORCED)
                            .setUsage(AudioAttributes.USAGE_GAME).build()
            ).setMaxStreams(16).build();
        } else {
            soundpool = new SoundPool(16, AudioManager.STREAM_MUSIC, 0);
        }

        audioID = new int[count];

        AssetManager am = Base.context.getAssets();
        for (int i = 0; i < count; i++) {
            audioID[i] = soundpool.load(am.openFd(audioFiles[i]), 1);
        }

        rightVolume = 1.0f;
        leftVolume = 1.0f;
    }

    /**
     * returns an instance of SoundPool
     */
    public SoundPool getPool() {

        return soundpool;
    }

    public void setOnLoadCompleteListener(SoundPool.OnLoadCompleteListener listener) {

        soundpool.setOnLoadCompleteListener(listener);
    }

    /**
     * find and play audio file by name
     * repeatCount 0 = play once
     */
    public void play(String audioName, int repeatCount) {

        for (int i = 0; i < audioList.length; i++) {
            if (audioList[i].equals(audioName)) {

                play(audioID[i], repeatCount);
                break;
            }
        }
    }

    /**
     * play audio file by audioID ( 1st audio file in folder = 1 ID )
     * audioID 1 -> N
     * repeatCount 0 = play once
     */
    public void play(int audioID, int repeatCount) {

        soundpool.play(audioID, leftVolume, rightVolume, 1, repeatCount, 1.0f);
    }

    /**
     * play audio file once by index
     * @param index 0 -> N
     */
    public void play(int index) {

        soundpool.play(audioID[index], leftVolume, rightVolume, 1, 0, 1.0f);
    }

    public void playByID(int audioID) {

        soundpool.play(audioID, leftVolume, rightVolume, 1, 0, 1.0f);
    }

    private int nextID = 0;

    /**
     * play next file from SoundPool ( looped )
     */
    public void playNext() {

        soundpool.play(audioID[nextID++], leftVolume, rightVolume, 1, 0, 1.0f);

        if (nextID == audioID.length) {
            nextID = 0;
        }
    }

    /**
     * set volume for each side
     */
    public void setVolume(float leftVolume, float rightVolume) {

        this.leftVolume = leftVolume;
        this.rightVolume = rightVolume;
    }

    /**
     * set volume for each side
     */
    public void setVolume(float volume) {

        this.leftVolume = volume;
        this.rightVolume = volume;
    }

    /**
     * returns audioID of last file in SoundPool ( files count, 1st file = 1 )
     */
    public int getLastIndex() {

        return audioID[audioID.length];
    }

    /**
     * returns List of audio files
     */
    public String[] getAudioFilesList() {

        return audioList;
    }

    /**
     * release pool
     */
    public void release() {

        soundpool.release();
    }

    @Override
    public void destroy() {

        release();
    }

    @Override
    public void onPause() {

        for (int id : audioID) {
            soundpool.pause(id);
        }
    }

    @Override
    public void onResume() {

        for (int id : audioID) {
            soundpool.resume(id);
        }
    }
}
