package com.example.skin;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.lib.SkinManager;
import com.example.lib.utils.Constants;

import java.io.File;


public class SkinActivity extends Activity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //LayoutInflater.from(this).setFactory2();
        setContentView(R.layout.activity_skin);

//        findViewById(R.id.tabLayout);
//        Resources resources = getResources();
//        new Resources()


    }

    public void change(View view) {
        //换肤，收包裹，皮肤包是独立的apk包，可以来自网络下载
        String path = getExternalCacheDir().getPath() + File.separator + "skin-debug.apk";
        Log.e(Constants.TAG, "change: path ===" + path);
        SkinManager.getInstance().loadSkin(path);
    }

    public void restore(View view) {
        SkinManager.getInstance().loadSkin(null);
    }
}
