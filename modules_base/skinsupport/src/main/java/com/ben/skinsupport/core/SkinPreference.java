package com.ben.skinsupport.core;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 用来保存已经换过的皮肤配置
 *
 * @author: BD
 */
public class SkinPreference {
    private static SkinPreference instance;
    private final SharedPreferences mSp;
    private final static String SP_NAME = "SPSkinPath";
    private final static String KEY_SKIN_PATH = "SkinPath";

    private SkinPreference(Context context) {
        mSp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
    }

    public static void init(Context context) {
        if (instance == null) {
            synchronized (SkinPreference.class) {
                if (instance == null) {
                    instance = new SkinPreference(context);
                }
            }
        }
    }

    public static SkinPreference getInstance() {
        return instance;
    }

    /**
     * 保存换过的皮肤包路径
     *
     * @param skinPath
     */
    public void setSkinPath(String skinPath) {
        mSp.edit().putString(KEY_SKIN_PATH, skinPath).apply();
    }

    /**
     * 获取上次保存过的皮肤包路径
     *
     * @return
     */
    public String getSkinPath() {
        return mSp.getString(KEY_SKIN_PATH, null);
    }

}
