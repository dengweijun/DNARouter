package com.ben.skinsupport.core;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.TextureView;

/**
 * 换肤资源管理，处理换肤操作
 * 用于从app包中加载资源
 *
 * @author: BD
 */
public class SkinResources {

    private static SkinResources instance;

    // App自己的Resources
    private Resources mAppResources;

    // 加载了皮肤包后创建的Resources
    private Resources mSkinResources;

    // 皮肤包名
    private String mSkinPackageName;

    // 是否为app默认的皮肤包
    private boolean idDefaultSkin = true;

    private SkinResources(Context context) {
        mAppResources = context.getResources();
    }

    public static void init(Context context) {
        if (instance == null) {
            synchronized (SkinResources.class) {
                if (instance == null) {
                    instance = new SkinResources(context);
                }
            }
        }
    }

    public static SkinResources getInstance() {
        return instance;
    }

    /**
     * 恢复到默认的皮肤设置
     */
    public void reset() {
        mSkinResources = null;
        mSkinPackageName = "";
        idDefaultSkin = true;
    }

    /**
     * 提交皮肤包资源属性配置
     *
     * @param resources
     * @param skinPackageName
     */
    public void applySkin(Resources resources, String skinPackageName) {
        mSkinResources = resources;
        mSkinPackageName = skinPackageName;
        idDefaultSkin = TextUtils.isEmpty(skinPackageName) || resources == null;
    }

    /**
     * 通过原app的resId，获取皮肤包中的资源Id
     *
     * @param resId
     * @return
     */
    public int getIdentifier(int resId) {
        if (idDefaultSkin) {
            // 默认的皮肤，直接用原resId
            return resId;
        }
        String resourceEntryName = mAppResources.getResourceEntryName(resId);
        String resourceTypeName = mAppResources.getResourceTypeName(resId);
        // 通过此方法，获取皮肤包中对应的resId
        return mSkinResources.getIdentifier(resourceEntryName, resourceTypeName, mSkinPackageName);
    }

    /**
     * 获取皮肤包中color的值
     *
     * @param resId
     * @return
     */
    public int getColor(int resId) {
        if (idDefaultSkin) {
            // 默认的皮肤，直接获取color的值
            return mAppResources.getColor(resId);
        }
        int skinResId = getIdentifier(resId);
        if (skinResId == 0) {
            // 当皮肤包中，没有配置对应的颜色值时，依然使用原app的color值
            return mAppResources.getColor(resId);
        }
        return mSkinResources.getColor(skinResId);
    }

    public ColorStateList getColorStateList(int resId) {
        if (idDefaultSkin) {
            return mAppResources.getColorStateList(resId);
        }
        int skinResId = getIdentifier(resId);
        if (skinResId == 0) {
            return mAppResources.getColorStateList(resId);
        }
        return mSkinResources.getColorStateList(skinResId);
    }

    public Drawable getDrawable(int resId) {
        if (idDefaultSkin) {
            return mAppResources.getDrawable(resId);
        }
        int skinResId = getIdentifier(resId);
        if (skinResId == 0) {
            return mAppResources.getDrawable(resId);
        }
        return mSkinResources.getDrawable(skinResId);
    }

    /**
     * 可能是color，也可能是Drawable
     *
     * @param resId
     * @return
     */
    public Object getBackground(int resId) {
        String resourceTypeName = mAppResources.getResourceTypeName(resId);
        // color
        if (resourceTypeName.equals("color")) {
            return getColor(resId);
        }
        // drawable
        return getDrawable(resId);
    }


    public String getString(int resId) {
        try {
            if (idDefaultSkin) {
                return mAppResources.getString(resId);
            }
            int skinResId = getIdentifier(resId);
            if (skinResId == 0) {
                return mAppResources.getString(resId);
            }
            return mSkinResources.getString(skinResId);
        } catch (Resources.NotFoundException e) {
            // e.printStackTrace();
        }
        return null;
    }

    /**
     * 更新自定义字体
     *
     * @param resId
     * @return
     */
    public Typeface getTypeface(int resId) {
        // 自定义的字体路径，需要定义在string.xml里面
        // 在这里就可以通过getString来获取了
        String typefacePath = getString(resId);
        if (TextUtils.isEmpty(typefacePath)) {
            return Typeface.DEFAULT;
        }
        try {
            if (idDefaultSkin) {
                return Typeface.createFromAsset(mAppResources.getAssets(), typefacePath);
            }
            return Typeface.createFromAsset(mSkinResources.getAssets(), typefacePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Typeface.DEFAULT;
    }

}
