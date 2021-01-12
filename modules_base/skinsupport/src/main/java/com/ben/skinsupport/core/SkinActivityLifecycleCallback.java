package com.ben.skinsupport.core;

import android.app.Activity;
import android.app.Application;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;

import com.ben.skinsupport.SkinManager;
import com.ben.skinsupport.utils.SkinUtils;

import java.lang.reflect.Field;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.LayoutInflaterCompat;

/**
 * activity生命周期监听
 *
 * @author: BD
 */
public class SkinActivityLifecycleCallback implements Application.ActivityLifecycleCallbacks {

    // 对activity设置了自定义的布局加载工厂后用来保存这个factory对象
    // 后面可以通过activity找到
    private ArrayMap<Activity, SkinLayoutFactory> mSkinLayoutFactoryMap = new ArrayMap<>();

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

        // 在这里做更新布局的视图!!

        // 更新状态栏颜色
        SkinUtils.updateStatusBarColor(activity);

        // 获取要更新的字体
        Typeface typeface = SkinUtils.getSkinTypeface(activity);

        // 获得Activity的布局加载器
        LayoutInflater layoutInflater = LayoutInflater.from(activity);
        // 在Activity创建的时候，加载布局
        SkinLayoutFactory factory = new SkinLayoutFactory(activity, typeface);
        // 通过反射，解决mFactorySet在Android Q中被非SDK接口限制的问题 !!!!
        forceSetFactory2(layoutInflater, factory);

        // 此方法只适合android 28，在android Q只能用上面的方法进行强制设置
//        try {
//            // Android 布局加载器 使用 mFactorySet 标记是否设置过Factory
//            // 如设置过则会抛出异常，这里需要处理下
//            // 设置 mFactorySet 标签为false
//            Field field = LayoutInflater.class.getDeclaredField("mFactorySet");
//            field.setAccessible(true);
//            field.setBoolean(layoutInflater, false);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        // 将factory添加到缓存中
        mSkinLayoutFactoryMap.put(activity, factory);

        // 添加观察者
        SkinManager.getInstance().addObserver(factory);
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {

    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {

    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        // 从集合中删除并取消观察
        SkinLayoutFactory factory = mSkinLayoutFactoryMap.get(activity);
        mSkinLayoutFactoryMap.remove(factory);
        // activity销毁时，清除观察者
        SkinManager.getInstance().deleteObserver(factory);
    }

    /**
     * 解决mFactorySet在Android Q中被非SDK接口限制的问题 !!!!!!
     * 具体原因，可查看博客：https://blog.csdn.net/qq_25412055/article/details/100033637
     *
     * @param inflater
     * @param factory
     */
    private void forceSetFactory2(LayoutInflater inflater, SkinLayoutFactory factory) {
        Class<LayoutInflaterCompat> compatClass = LayoutInflaterCompat.class;
        Class<LayoutInflater> inflaterClass = LayoutInflater.class;
        try {
            Field sCheckedField = compatClass.getDeclaredField("sCheckedField");
            sCheckedField.setAccessible(true);
            sCheckedField.setBoolean(inflater, false);
            Field mFactory2 = inflaterClass.getDeclaredField("mFactory2");
            mFactory2.setAccessible(true);
            if (inflater.getFactory2() != null) {
                factory.setInterceptFactory2(inflater.getFactory2());
            }
            mFactory2.set(inflater, factory);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }


}
