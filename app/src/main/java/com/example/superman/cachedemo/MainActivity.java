package com.example.superman.cachedemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    //    public static final String path = "http://e.hiphotos.baidu.com/image/h%3D300/sign=2c4e309869380cd7f91ea4ed9144ad14/ca1349540923dd54ca7e6840dd09b3de9c82488d.jpg";
    public static final String path = "http://www.hujlin.com:8080/manman.mp3";
//    public static final String path = "http://www.hujlin.com:8080/kotlin.pdf";
    FileCache fileCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TextView text = findViewById(R.id.text);
        fileCache = new FileCache(this, path);
        boolean result = fileCache.judgeFile();
        Log.e("TAG===加密文件", result + "");
        fileCache.setDownloadProgreeListener(new FileCache.DownloadProgreeListener() {
            @Override
            public void onProgress(int progress) {
                text.setText(progress + "%");
            }
        });
        Log.e("TTTTT",isRoot()+"");
    }

    public void action(View view) {
        fileCache.downLoad();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        boolean result = fileCache.deleteDecryptFile();
        Log.e("TAG===删除文件", result + "");
        if (fileCache != null) {
            fileCache.cancel();
        }
    }

    /**
     * 判断手机是否ROOT
     */
    public static boolean isRoot() {

        boolean root = false;

        try {
            if ((!new File("/system/bin/su").exists())
                    && (!new File("/system/xbin/su").exists())) {
                root = false;
            } else {
                root = true;
            }

        } catch (Exception e) {
        }

        return root;
    }
}
