package com.example.lib;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;

import androidx.core.view.LayoutInflaterCompat;

import com.example.lib.utils.Constants;
import com.example.lib.utils.SkinThemeUtils;

import java.lang.reflect.Field;
import java.util.Observable;

public class ApplicationActivityLifecycle implements Application.ActivityLifecycleCallbacks {

    private Observable mObserable;
    private ArrayMap<Activity, SkinLayoutInflaterFactory> mLayoutInflaterFactories = new
            ArrayMap<>();

    public ApplicationActivityLifecycle(Observable observable) {
        mObserable = observable;
    }

    /**
     * 如果是Android P以及以后的时候，需要用onActivityPreCreated
     * 如果是P以前的，就可以在onActivityCreated中实现
     */
    @Override
    public void onActivityPreCreated(Activity activity, Bundle savedInstanceState) {
        /**
         *  更新状态栏
         */
        SkinThemeUtils.updateStatusBarColor(activity);

        /**
         *  更新布局视图
         */
        //获得Activity的布局加载器
        LayoutInflater layoutInflater = activity.getLayoutInflater();

        try {
            //Android 布局加载器 使用 mFactorySet 标记是否设置过Factory
            //如设置过抛出一次
            //设置 mFactorySet 标签为false
            Field field = LayoutInflater.class.getDeclaredField("mFactorySet");
            Log.e(Constants.TAG,"mFactorySet = " + field.getName());
            field.setAccessible(true);
            field.setBoolean(layoutInflater, false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //使用factory2 设置布局加载工程
        SkinLayoutInflaterFactory skinLayoutInflaterFactory = new SkinLayoutInflaterFactory
                (activity);
        LayoutInflaterCompat.setFactory2(layoutInflater, skinLayoutInflaterFactory);
        mLayoutInflaterFactories.put(activity, skinLayoutInflaterFactory);

        mObserable.addObserver(skinLayoutInflaterFactory);
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        SkinLayoutInflaterFactory observer = mLayoutInflaterFactories.remove(activity);
        SkinManager.getInstance().deleteObserver(observer);
    }
}
