package com.base.lib.engine.audio;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.net.Uri;

import com.base.lib.engine.Base;
import com.base.lib.interfaces.ActivityStateListener;

import java.io.IOException;

public class BaseAudio implements ActivityStateListener {

    private MediaPlayer player;
	private String audioFile;
    private float leftVolume;
    private float rightVolume;
    private boolean looping;
    private boolean playOnResume; //todo why ?
    private int currentTime;

    /** create new instance of Media Player and prepare audio file from assets folder */
	public BaseAudio(String file){

        //Base.activity.addActivityStateListener(this); //// TODO: 22. 1. 2016  

        init();
        audioFile = file;
		prepareAssetsAudio();
	}

    private void init(){

        playOnResume = false;
        looping = false;
        leftVolume = 1.0f;
        rightVolume = 1.0f;
    }

    private void prepareAudio(){

        release();

        player = new MediaPlayer();
        player.setVolume(leftVolume, rightVolume);
        player.setLooping(looping);
    }

    /** prepare audio file by file descriptor from assets folder */
	private void prepareAssetsAudio(){

        prepareAudio();
		
		try {
			final AssetFileDescriptor descriptor = Base.appContext.getAssets().openFd(audioFile);
			player.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
			descriptor.close();

			player.prepare();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

    private void prepareUriAudio(){

        try {
            player.setDataSource(Base.appContext, Uri.parse(audioFile));
            player.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void prepareResourceAudio(){

        try {
            player = MediaPlayer.create(Base.appContext, Integer.parseInt(audioFile));
            player.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** returns current instance of MediaPlayer */
    public MediaPlayer getPlayer(){

        return player;
    }

    /** sets player to looping or not */
    public void setLooping(boolean looping){

        this.looping = looping;
        if(player != null){
            player.setLooping(looping);
        }
    }

    /** read MediaPlayer.setVolume(float, float) */
    public void setVolume(float volume){

        leftVolume = volume;
        rightVolume = volume;
        if(player != null){
            player.setVolume(volume, volume);
        }
    }

    /** read MediaPlayer.setVolume(float, float) */
    public void setVolume(float left, float right){

        leftVolume = left;
        rightVolume = right;
        if(player != null){
            player.setVolume(left, right);
        }
    }

    /** check if player is not null and start player then */
	public void play(){
		
		if(player != null){
			player.start();
		}
	}

    /** check if player is playing and pause player then */
	public void pause(){
		
		if(player.isPlaying()){
			player.pause();
		}
	}

    /** check if player is playing and stop player then */
	public void stop(){
		
		if(player.isPlaying()){
			player.stop();
		}
	}

    /** check if player is not null and stops it, realease player then */
	public void release(){
		
		if(player != null){
			stop();
			player.release();
			player = null;
		}
	}
	
	/** release player if not null and prepare again with same audio file*/
	public void reload(){

        prepareAudio();
		prepareAssetsAudio();
	}
	
	/** release current player and prepare new one */
	public void changeAudioFile(String file){

        audioFile = file;
		reload();
	}

    @Override
	public void destroy() {
		
		release();
	}

    @Override
    public void onPause() {

        if(player != null){
            if(player.isPlaying()){
                playOnResume = true;
                currentTime = player.getCurrentPosition();
            }
        }
        release();
    }

    @Override
    public void onResume() {

        reload();
        if(playOnResume){
            player.seekTo(currentTime);
            play();
            playOnResume = false;
        }
    }
}
