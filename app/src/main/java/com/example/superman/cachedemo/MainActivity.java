package com.example.superman.cachedemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    public static final String path = "http://e.hiphotos.baidu.com/image/h%3D300/sign=2c4e309869380cd7f91ea4ed9144ad14/ca1349540923dd54ca7e6840dd09b3de9c82488d.jpg";
    FileCache fileCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fileCache = new FileCache(this, path);
        boolean result = fileCache.judgeFile();
        Log.e("TAG===加密文件", result + "");
    }

    public void action(View view) {
        fileCache.downLoad();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        boolean result = fileCache.deleteDecryptFile();
        Log.e("TAG===删除文件", result + "");
    }
}
