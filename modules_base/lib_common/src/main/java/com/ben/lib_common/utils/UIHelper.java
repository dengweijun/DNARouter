package com.ben.lib_common.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class UIHelper {

    /**
     * 启动界面
     *
     * @param context
     * @param clz
     */
    public static void startActivity(Context context, Class<?> clz) {
        Intent intent = new Intent(context, clz);
        context.startActivity(intent);
    }

    /**
     * 启动界面-带bundle
     *
     * @param context
     * @param clz
     * @param data
     */
    public static void startActivity(Context context, Class<?> clz, Bundle data) {
        Intent intent = new Intent(context, clz);
        intent.putExtras(data);
        context.startActivity(intent);
    }

    /**
     * 添加fragment
     *
     * @param fm
     * @param fragment
     * @param container
     * @return
     */
    public static Fragment addFragment(FragmentManager fm, Fragment fragment,
                                       int container) {
        if (!fragment.isAdded()) {
            FragmentTransaction transaction = fm.beginTransaction();
            transaction.add(container, fragment).commit();
        }
        return fragment;
    }

    /**
     * 切换页面的重载，优化了fragment的切换
     */
    public static Fragment showFragment(FragmentManager fm, Fragment from, Fragment to, int contentLayout) {
        if (from == null && to != null) {
            return addFragment(fm, to, contentLayout);
        }
        if (from == null ||
                to == null || from == to) return from;
        FragmentTransaction transaction = fm.beginTransaction();
        if (!to.isAdded()) {
            // 隐藏当前的fragment，add下一个到Activity中
            transaction.hide(from).add(contentLayout, to).commit();
        } else {
            // 隐藏当前的fragment，显示下一个
            transaction.hide(from).show(to).commit();
        }
        return to;
    }

}