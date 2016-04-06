package com.base.lib.engine.common.file;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import com.base.lib.engine.Base;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

public class FileHelper {

    /**
     * save private file by application context
     * @param file name
     * @param content data to write
     * */
	public static void saveInternal(String file, String content){

		saveInternal(file, content.getBytes());
	}

    /**
     * save private file by application context
     * @param file name
     * @param bytes byte array data to write
     * */
    public static void saveInternal(String file, byte[] bytes){

        try {
            BufferedOutputStream bos = new BufferedOutputStream(Base.appContext.openFileOutput(file, Context.MODE_PRIVATE));

            bos.write(bytes);

            bos.flush();
            bos.close();
        } catch (IOException e) {
            Base.logE("File: "+file+" cannot be write: "+e.getMessage());
        }
    }

    /**
     * load private file by application context
     * @param file name
     * @return file content in byte array
     * */
    public static byte[] loadInternal(String file){

        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        if(existInternal(file, false)){
            try {
                FileInputStream is = Base.appContext.openFileInput(file);
                BufferedReader br = new BufferedReader(new InputStreamReader(is));

                int b;
                while((b = br.read()) != -1){
                    bos.write(b);
                }

                br.close();
                is.close();
            } catch (IOException e) {
                Base.logE("File: "+file+" cannot be read: "+e.getMessage());
            }
        } else {
            return null;
        }

        return bos.toByteArray();
    }

    public static Bitmap loadInternalBitmap(String file){

        if(existInternal(file, false)){
            try {
                FileInputStream is = Base.appContext.openFileInput(file);

                Bitmap bitmap = BitmapFactory.decodeStream(is);

                is.close();
                return bitmap;
            } catch (IOException e) {
                Base.logE("File: "+file+" cannot be read: "+e.getMessage());
            }
        }

        return null;
    }

    /**
     * load private file by application context
     * @param file name
     * @return file content
     * */
	public static String loadInternalText(String file){

		StringBuilder fileContent = new StringBuilder();

		if(existInternal(file, false)){
			try {
                int ch;
                FileInputStream is = Base.appContext.openFileInput(file);
                BufferedReader br = new BufferedReader(new InputStreamReader(is));

				while((ch = br.read()) != -1){
					fileContent.append((char) ch);
				}

                br.close();
				is.close();
			} catch (IOException e) {
				Base.logE("File: "+file+" cannot be read: "+e.getMessage());
			}
		}

		return fileContent.toString();
	}

    /**
     * delete private file by application context
     * @param file name
     * */
	public static void deleteInternal(String file){
		
		Base.appContext.deleteFile(file);
	}

    /**
     * check if file exist by application context
     * @param file name
     * @param createIfNotExist true - to create file if not exist
     * */
	public static boolean existInternal(String file, boolean createIfNotExist){
		
		File outFile = Base.appContext.getFileStreamPath(file);
		if(outFile.exists()){
			return true;
		} else {
            if(createIfNotExist){
			    saveInternal(file, new byte[]{});
            }
			return false;
		}
	}

    /**
     * loads private file names
     * @return list of files
     * */
    public static String[] internalFiles(){

        return Base.appContext.getFilesDir().list(null);
    }

    /**
     * loads private file names
     * @return list of files
     * */
    public static String[] internalFiles(FilenameFilter filter){

        return Base.appContext.getFilesDir().list(filter);
    }

    /**
     * read text from resource xml file (eg. R.string.id)
     * @param resourceId resource id
     * @return resource content
     * */
    public static String resourceString(int resourceId){

        return Base.appContext.getResources().getString(resourceId);
    }

    /**
     * read file from application resources ( eg. R.raw.id)
     * @param resourceId resource id
     * @return file content
     * */
    public static String resourceText(int resourceId){

        StringBuilder body = new StringBuilder();

        try {
            InputStream is = Base.appContext.getResources().openRawResource(resourceId);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            String line;
            while ((line = br.readLine()) != null)
            {
                body.append(line);
                body.append('\n');
            }

            br.close();
            is.close();
        } catch (IOException e) {
            return null;
        }

        return body.toString();
    }

    /**
     * read file from application resources ( eg. R.raw.glid)
     * @param resourceId resource glid
     * @return file content in byte array
     * */
    public static byte[] resource(int resourceId){

        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try {
            InputStream is = Base.appContext.getResources().openRawResource(resourceId);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            int b;
            while ((b = br.read()) != -1) {
                bos.write(b);
            }

            br.close();
            is.close();
        } catch (IOException e) {
            return null;
        }

        return bos.toByteArray();
    }

    /**
     * read file from application assets folder
     * @param file path in assets folder
     * @return file content
     * */
    public static String assetsText(String file){

        StringBuilder fileContent = new StringBuilder();

        try {
            InputStream is = Base.appContext.getAssets().open(file, AssetManager.ACCESS_STREAMING);
            BufferedReader br = new BufferedReader(new InputStreamReader(new DataInputStream(is)));

            String line;
            while ((line = br.readLine()) != null){
                fileContent.append(line);
            }

            br.close();
            is.close();
        } catch (IOException e) {
            Base.logE("File: "+file+" cannot be read: "+e.getMessage());
        }

        return fileContent.toString();
    }

    /**
     * read file from application assets folder
     * @param file path in assets folder
     * @return file content in byte array
     * */
    public static byte[] assets(String file){ //todo check performace

        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try {
            InputStream is = Base.appContext.getAssets().open(file, AssetManager.ACCESS_STREAMING);
            BufferedReader br = new BufferedReader(new InputStreamReader(new DataInputStream(is)));

            int b;
            while ((b = br.read()) != -1){
                bos.write(b);
            }

            br.close();
            is.close();
        } catch (IOException e) {
            Base.logE("File: "+file+" cannot be read: "+e.getMessage());
        }

        return bos.toByteArray();
    }

    /**
     * check if file exist in application assets file
     * @param file path in assets folder
     * @return true if file exist
     * */
    public static boolean assetsExist(String file){

        boolean exist = false;

        try {
            AssetManager am = Base.appContext.getAssets();
            List<String> list = Arrays.asList(am.list(""));
            exist = list.contains(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return exist;
    }


    /** check if SD Card is available */
    public static boolean sdIsMounted(){

        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    /** check if SD Card is available and we can write data in */
    public static boolean sdIsWriteable(){

        return sdIsMounted() && !Environment.MEDIA_MOUNTED_READ_ONLY.equals(Environment.getExternalStorageState());
    }

    /**
     * check if SD Card is mounted and file exist on
     * @param path file path on sd card
     * @return true if exist
     * */
    public static boolean sdFileExist(String path){

        if(sdIsMounted()){
            return new File(Environment.getExternalStorageDirectory(), path).exists();
        } else {
            Base.logE("Monkey's can't find SD Card...");
            return false;
        }
    }

    /**
     * read text file(if exist) from SD Card
     * @param path file path on sd card
     * @return file content
     * */
    public static String sdReadTextFile(String path){

        return new String(sdReadFile(path));
    }

    /**
     * read text file(if exist) from SD Card
     * @param path file path on sd card
     * @return file content in byte array
     * */
    public static byte[] sdReadFile(String path){

        byte[] bytes = null;

        if (sdFileExist(path)) {
            try {
                File file = new File(Environment.getExternalStorageDirectory() +"/"+ path);
                FileInputStream is = new FileInputStream(file);
                BufferedInputStream br = new BufferedInputStream(is);

                bytes = new byte[(int)file.length()];
                br.read(bytes, 0, bytes.length);

                br.close();
                is.close();
            } catch (IOException e) {
                Base.logE("File: " + path + " cannot be read: " + e.getMessage());
            }
        }

        return bytes;
    }

    /**
     * write text file into SD Card(if is writeable)
     * @param path file path on sd card
     * @param content data to write
     * */
    public static void sdWriteFile(String path, String content){

        sdWriteFile(path, content.getBytes());
    }

    /**
     * write text file into SD Card(if is writeable)
     * @param path file path on sd card
     * @param bytes data to write
     * */
    public static void sdWriteFile(String path, byte[] bytes){

        if (sdIsWriteable()) {
            try {
                int slash = path.lastIndexOf("/")+1;

                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +"/"+ path.substring(0, slash));

                if(!file.exists()){
                    if(!file.mkdirs()){
                        Base.logE("Monkey's can't create folder: " +file.getAbsolutePath());
                    }
                }

                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(new File(file, path.substring(slash, path.length()))));
                bos.write(bytes);
                bos.flush();
                bos.close();
            } catch (IOException e) {
                Base.logE("File: " + path + " cannot be write: " + e.getMessage());
            }
        } else {
            Base.logE("Monkey's can't write to SD Card...");
        }
    }

    /**
     * write text file into SD Card(if is writeable)
     * @param path file path on sd card
     * @param bytes data to write
     * */
    public static void sdWritePrivateFile(String dir, String path, byte[] bytes){

        if (sdIsWriteable()) {
            try {
                int slash = path.lastIndexOf("/")+1;

                File file = new File(Base.appContext.getExternalFilesDir(dir), path);

                if(!file.exists()){
                    if(!file.mkdirs()){
                        Base.logE("Monkey's can't create folder: " +file.getAbsolutePath());
                    }
                }

                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(new File(file, path.substring(slash, path.length()))));
                bos.write(bytes);
                bos.flush();
                bos.close();
            } catch (IOException e) {
                Base.logE("File: " + path + " cannot be write: " + e.getMessage());
            }
        } else {
            Base.logE("Monkey's can't write to SD Card...");
        }
    }

    /**
     * read text file(if exist) from SD Card
     * @param path file path on sd card
     * @return file content in byte array
     * */
    public static byte[] sdReadPrivateFile(String dir, String path){

        byte[] bytes = null;

        if (sdFileExist(path)) {
            try {
                File file = new File(Base.appContext.getExternalFilesDir(dir), path);

                FileInputStream is = new FileInputStream(file);
                BufferedInputStream br = new BufferedInputStream(is);

                bytes = new byte[(int)file.length()];
                br.read(bytes, 0, bytes.length);

                br.close();
                is.close();
            } catch (IOException e) {
                Base.logE("File: " + path + " cannot be read: " + e.getMessage());
            }
        }

        return bytes;
    }

    /**
     * delete file on SD Card
     * @param path file path on sd card
     * @return true if deleted properly
     * */
    public static boolean sdDeleteFile(String path){

        return new File(Environment.getExternalStorageDirectory().getAbsolutePath() +"/"+ path).delete();
    }

    public static File[] sdDirectoryFiles(String dir){

        return new File(Environment.getExternalStorageDirectory().getAbsolutePath() +"/"+ dir).listFiles();
    }

    public static File[] sdDirectoryFiles(String dir, FilenameFilter filter){

        if(filter != null){
            return new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + dir).listFiles(filter);
        } else {
            return new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + dir).listFiles();
        }
    }

    /**
     * reads data from file
     * @param file File
     * @return file content in byte array
     * */
    public static byte[] readFile(File file){

        byte[] bytes = null;
            try {
                FileInputStream is = new FileInputStream(file);
                BufferedInputStream br = new BufferedInputStream(is);

                bytes = new byte[(int)file.length()];
                br.read(bytes, 0, bytes.length);

                br.close();
                is.close();
            } catch (IOException e) {
                Base.logE("File: " + file.getName() + " cannot be read: " + e.getMessage());
            }

        return bytes;
    }

}
