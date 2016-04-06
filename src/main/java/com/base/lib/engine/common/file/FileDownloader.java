package com.base.lib.engine.common.file;

import android.os.AsyncTask;

import com.base.lib.engine.Base;
import com.base.lib.engine.Type;
import com.base.lib.engine.common.other.TrainedMonkey;
import com.base.lib.interfaces.DownloadDoneListener;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Download file from server/page.
 * Based on url (http://images.com/image.png)
 * Downloader has two modes - SYNC and ASYNC
 * SYNC works on backround thread and all urls are downloaded one by one
 * ASYNC creates for every url new Thread and downloads all in once
 */
public class FileDownloader {

    private List<Downloader> downloaderList;
    private DownloadDoneListener listener;
    private float currProgress;
    private int doneCount;
    private int count;
    private Type type;

    /**
     * initialize downloader
     * */
    public FileDownloader(){

        //Base.destroyer.addToDestroy(this);

        downloaderList = new ArrayList<Downloader>();
        resetProgress();
        type = Type.DOWNLOADER_SYNC;
    }

    /**
     * initialize downloader
     * @param type Type.DOWNLOADER_...
     * */
    public FileDownloader(Type type){

        this();
        setType(type);
    }

    /**
     * sets downloader type - SYNC, ASYNC
     * @param type Type.DOWNLOADER_...
     * */
    public void setType(Type type){

        this.type = type;
    }

    /**
     * sets downloader listener to handle with byte result
     * @param listener handler
     * */
    public void setListener(DownloadDoneListener listener){

        this.listener = listener;
    }

    /**
     * invoke listener
     * */
    private void fileDownloadDone(long dIndex, byte[] result){

        updateProgress();
        if(listener != null){
            listener.downloadDone(dIndex, result);
        }
    }

    /**
     * invoke to download data, downloader index is set to 0
     * @param urls URLs to download
     * */
    public void download(String... urls){

        download(type, 0, urls);
    }

    /**
     * invoke to download data, downloader index is set to 0
     * @param urls URLs to download
     * */
    public void download(URL... urls){

        download(type, 0, urls);
    }

    /**
     * invoke to download data
     * @param dIndex downloader index
     * @param urls URLs to download
     * */
    public void download(long dIndex, String... urls){

        download(type, dIndex, urls);
    }

    /**
     * invoke to download data
     * @param dIndex downloader index
     * @param urls URLs to download
     * */
    public void download(long dIndex, URL... urls){

        download(type, dIndex, urls);
    }

    /**
     * invoke to download data, convert strings to URLs
     * @param type Type.DOWNLOADER_...
     * @param dIndex downloader index
     * @param urls URLs to download
     * */
    public void download(Type type, long dIndex, String... urls){

        URL[] toDownload = new URL[urls.length];
        for(int i = 0; i<toDownload.length; i++){
            try {
                toDownload[i] = new URL(urls[i]);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

        download(type, dIndex, toDownload);
    }

    /**
     * invoke to download data
     * @param type Type.DOWNLOADER_...
     * @param dIndex downloader index
     * @param urls URLs to download
     * */
    public void download(Type type, long dIndex, URL... urls){

        count += urls.length;
        switch (type){
            case DOWNLOADER_SYNC:
                downloaderList.add(new AsyncDownloader(dIndex, urls));
                break;
            case DOWNLOADER_ASYNC:
                downloaderList.add(new ThreadDownloader(dIndex, urls));
                break;
        }
    }

    /** invoke when all files are downloaded */
    private void downloaderDone(Downloader downloader){ //todo synch ?

        downloaderList.remove(downloader);
        downloader = null;
    }

    private void updateProgress(){

        if(++doneCount == count){
            currProgress = 1.0f;
            //todo done
        } else {
            currProgress = (float)doneCount/(float)count;
        }
    }

    /** get current progress */
    public float getProgress(){

        return currProgress;
    }

    /** reset current progress */
    public void resetProgress(){ //todo reset when progress

        currProgress = 0.0f;
        doneCount = 0;
        count = 0;
    }

    /** cencel all downloads */
    public void cencel(){

        for(Downloader downloader : downloaderList){
            downloader.cancel();
            downloader = null;
        }

        downloaderList.clear();
    }

    /** cencel all downloads by downloader glid */
    public void cencel(long dIndex){

        Iterator<Downloader> iterator = downloaderList.iterator();
        while(iterator.hasNext()){
            Downloader downloader = iterator.next();
            if(dIndex == downloader.getId()){
                downloader.cancel();
                iterator.remove();
                downloader = null;
            }
        }
    }

    /** pause all current downloads */
    public void pause(){

        for(Downloader downloader : downloaderList){
            downloader.pause();
        }
    }

    /** pause all downloads by downloader glid */
    public void pause(long dIndex){

        for(Downloader downloader : downloaderList){
            if(dIndex == downloader.getId()){
                downloader.pause();
            }
        }
    }

    /** resume all paused downloads */
    public void resume(){

        for(Downloader downloader : downloaderList){
            downloader.resume();
        }
    }

    /** resume all paused downloads by downloader glid */
    public void resume(long dIndex){

        for(Downloader downloader : downloaderList){
            if(dIndex == downloader.getId()){
                downloader.resume();
            }
        }
    }

    /** stop handling downloads and cencel all running downloads */
    public void destroy() {

        listener = null;
        cencel();
    }

    /** downloader based on AsyncTask - tasks are executed on a single background thread - serially*/
    private class AsyncDownloader extends AsyncTask<URL, Float, Void> implements Downloader {

        private long dIndex;

        protected AsyncDownloader(long dIndex, URL... urls){

            this.dIndex = dIndex;
            execute(urls);
        }

        protected Void doInBackground(URL... urls) {

            for (URL url : urls){
                try {
                    if(isCancelled()) break;

                    BufferedInputStream is = new BufferedInputStream(url.openConnection().getInputStream());
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();

                    int b;
                    while ((b = is.read()) != -1) {
                        bos.write(b);
                    }

                    bos.flush();
                    fileDownloadDone(dIndex, bos.toByteArray());

                    // bos.close() function is empty
                    is.close();
                } catch (IOException e) {
                    Base.logE("FileDownloader: " + url + e.getMessage());
                    fileDownloadDone(dIndex, null);
                }
            }
            return null;
        }

        protected void onPostExecute(Void result) {

            downloaderDone(this);
        }

        @Override
        public long getId() {

            return dIndex;
        }

        @Override
        public void cancel(){
            cancel(true);
        }

        @Override
        public void pause(){

            TrainedMonkey.wait(this);
        }

        @Override
        public void resume(){

            TrainedMonkey.notify(this);
        }
    }

    /** dowloader based on Thread, every task has own thread - unserially */
    private class ThreadDownloader implements Downloader {

        private Thread thread;
        private long dIndex;

        protected ThreadDownloader(long dIndex, final URL... urls){

            this.dIndex = dIndex;

            thread = new Thread("FileDownloader: "+dIndex){
                @Override
                public void run() {
                    startdownloding(urls);
                }
            };
            thread.start();
        }

        private void startdownloding(URL... urls) {

            for (URL url : urls) {
                try {

                    BufferedInputStream is = new BufferedInputStream(url.openConnection().getInputStream());
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();

                    int b;
                    while ((b = is.read()) != -1) {
                        bos.write(b);
                    }

                    bos.flush();
                    fileDownloadDone(dIndex, bos.toByteArray());

                    // bos.close() function is empty
                    is.close();
                } catch (IOException e) {
                    Base.logE("FileDownloader: " + url + e.getMessage());
                    fileDownloadDone(dIndex, null);
                }

            }

            downloaderDone(this);
        }

        @Override
        public long getId() {

            return dIndex;
        }

        @Override
        public void cancel(){

            if(thread.isAlive()){
                thread.interrupt();
                thread = null;
            }
        }

        @Override
        public void pause(){

            TrainedMonkey.wait(thread);
        }

        @Override
        public void resume(){

            TrainedMonkey.notify(thread);
        }
    }


    private interface Downloader {

        long getId();
        void cancel();
        void pause();
        void resume();
    }

}
