package com.example.superman.cachedemo;
/**
 * Created by Administrator on 2018/1/25 10:39.
 */

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * 缓存文件
 */
public class FileCache {
    private final String path;
    private final Context context;
    //下载的原始文件    //解密后的零时文件
    private File originalFile;
    //加密后的文件
    private File decryptFile;


    private String seed = "asd1234fasadsgaqtqtq";


    public boolean judgeFile() {
        File f = getDiskCacheDir(context, hashKeyForDisk(path));  //加密后的文件
        //存在并且有内容
        if (f.exists() && f.length() > 0) {
            try {
                //解密
                return decryptFile();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    /**
     * 解密
     * @return
     * @throws IOException
     */
    public boolean decryptFile() throws IOException {
        boolean result;
//        File temp = getDiskCacheDir(context, "temp");
//        if (!temp.exists()) {
//            temp.mkdirs();
//        }
        File f = getDiskCacheDir(context, hashKeyForDisk(path).concat("_temp").concat(path.substring(path.lastIndexOf("."))));
        if (!f.exists()) {
            f.createNewFile();
        }
        //解密
        byte[] oldByte = new byte[(int) decryptFile.length()];
        try {
            FileInputStream fis = new FileInputStream(decryptFile);
            fis.read(oldByte);
            byte[] newByte = AESUtils.decryptVoice(seed, oldByte);
            // 解密
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(newByte);
            f.renameTo(originalFile);
            result = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            result = false;
        } catch (IOException e) {
            e.printStackTrace();
            result = false;
        } catch (Exception e) {
            e.printStackTrace();
            result = false;
        }
        return result;
    }


    public FileCache(Context context, String path) {
        this.path = path;
        this.context = context;
        originalFile = getDiskCacheDir(context, hashKeyForDisk(path).concat(path.substring(path.lastIndexOf("."))));
        decryptFile = getDiskCacheDir(context, hashKeyForDisk(path));
        if (!originalFile.exists()) {
            try {
                originalFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (!decryptFile.exists()) {
            try {
                decryptFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void downLoad() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    downloadUrlToStream(path, new FileOutputStream(originalFile));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }

    private boolean downloadUrlToStream(String urlString, OutputStream outputStream) {

        HttpURLConnection urlConnection = null;
        BufferedOutputStream out = null;
        BufferedInputStream in = null;
        int fileSizeDownloaded = 0;
        try {
            final URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            in = new BufferedInputStream(urlConnection.getInputStream(), 8 * 1024);


            int total = urlConnection.getContentLength();
            out = new BufferedOutputStream(outputStream, 8 * 1024);
            byte[] b = new byte[1024];
            int len;
            while ((len = in.read(b)) != -1) {
                try {
                    out.write(b, 0, len);
                    fileSizeDownloaded += len;
                    Log.e("TTT", "下载进度：" + fileSizeDownloaded);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            long time1 = System.currentTimeMillis();
            FileInputStream fis = new FileInputStream(originalFile);
            byte[] oldByte = new byte[(int) originalFile.length()];
            fis.read(oldByte); // 读取
            try {
                byte[] newByte = AESUtils.encryptVoice(seed, oldByte);
                // 加密
                FileOutputStream fos = new FileOutputStream(decryptFile);
                fos.write(newByte);
//                originalFile.delete();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.e("Time",(System.currentTimeMillis()-time1)+"");
            return true;
        } catch (final IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    //缓存文件夹
    private File getDiskCacheDir(Context context, String uniqueName) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        File f = new File(cachePath + File.separator + uniqueName);
        return f;
    }

    /**
     * MD5转换url
     *
     * @param key 文件url
     * @return
     */
    private String hashKeyForDisk(String key) {
        String cacheKey;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(key.getBytes());
            cacheKey = bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(key.hashCode());
        }
        return cacheKey;
    }

    private String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    /**
     * 删除解密文件
     * @return
     */
    public boolean deleteDecryptFile(){
       if (originalFile.exists()){
          return originalFile.delete();
       }
       return false;
    }


}
