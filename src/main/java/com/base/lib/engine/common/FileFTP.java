package com.base.lib.engine.common;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import com.base.lib.engine.Base;

/**
 * 18 Created by doctor on 6.9.13.
 */
public class FileFTP { //todo check/create folder on server  //todo switch to apache common FTPClient lib?

    private String ftp;
    private String root;
    private List<UploadThread> uploadList;
    private List<DownloadThread> downloadList;

    /** ftp://user:password@host:port/filePath;type=i.*/
    public FileFTP(String server, String user, String password) {

        this(server, 21, user, password);
    }

    public FileFTP(String server, int port, String user, String password) {

        if(server.lastIndexOf("/") == server.length()-1){
            server = server.substring(0, server.length()-1);
        }

        if(server.substring(0, 6).equals("ftp://")){
            server = server.substring(6, server.length());
        }

        ftp = "ftp://" + user + ":" + password + "@" + server + ":" + port + "/";
    }

    public void setRootDirectory(String dir){

        if(dir.lastIndexOf("/") == dir.length()-1){
            root = dir;
        } else {
            root = dir+"/";
        }
    }

    public boolean isConectable(){

        try{
            new URL(ftp+root).openConnection();
            Base.logV("FTP: " + "connectedable");
            return true;
        } catch (IOException e){
            Base.logE("FTP: " + e.getMessage());
            return false;
        }
    }

    public void uploadInBackground(String file, byte[] content){

        new UploadThread(file, content);
    }

    public void uploadInBackground(File... files){

        new UploadThread(files);
    }

    public void downloadInBackground(String... files){

        new DownloadThread(files);
    }

    protected void fileUploadDone(String file){

    }

    protected void fileDownloadDone(byte[] result){

    }

    private class DownloadThread { //todo pause resume cencel

        private Thread thread;

        protected DownloadThread(final String... files){

            thread = new Thread(){
                @Override
                public void run() {

                    download(files);
                }
            };

            thread.start();
        }

        private void download(String... files){

            for (String file : files) {
                try {
                    BufferedInputStream is = new BufferedInputStream(new URL(ftp +root+ file).openConnection().getInputStream());
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();

                    int b;
                    while ((b = is.read()) != -1) {
                        bos.write(b);
                    }

                    bos.flush();
                    fileDownloadDone(bos.toByteArray());
                    bos.close();
                    is.close();
                } catch (IOException e) {

                    Base.logE("FTP download: " + e.getMessage());
                }
            }
        }
    }

    private class UploadThread {

        private Thread thread;

        protected UploadThread(final String file, final byte[] content){

            thread = new Thread(){
                @Override
                public void run() {
                    upload(file, content);
                }
            };

            thread.start();
        }

        protected UploadThread(final File... files){

            thread = new Thread(){
                @Override
                public void run() {
                    for(File file : files){
                        upload(file.getName(), FileHelper.readFile(file));
                    }
                }
            };

            thread.start();
        }

        void upload(String file, byte[] content){
            try{
                URL url = new URL(ftp +root+ file);
                URLConnection con = url.openConnection();
                con.setDoOutput(true);

                BufferedOutputStream bos = new BufferedOutputStream(con.getOutputStream());

                bos.write(content);
                bos.flush();
                bos.close();
                fileUploadDone(file);
            } catch (IOException e){
                Base.logE("FTP upload: " + e.getMessage());
            }
        }
    }

    public String getConnectionString() {
        return ftp;
    }

    public String getRootDirectory() {
        return root;
    }
}
