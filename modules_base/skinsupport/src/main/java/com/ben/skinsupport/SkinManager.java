package com.ben.skinsupport;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.Log;

import com.ben.skinsupport.core.SkinActivityLifecycleCallback;
import com.ben.skinsupport.core.SkinPreference;
import com.ben.skinsupport.core.SkinResources;

import java.lang.reflect.Method;
import java.util.Observable;

/**
 * 换肤管理类，app程序初始化入口
 *
 * @author: BD
 */
public class SkinManager extends Observable {
    private Application mContext;
    private static SkinManager instance;

    public static SkinManager getInstance() {
        return instance;
    }

    /**
     * 初始化传入application
     *
     * @param application
     */
    public static void init(Application application) {
        if (instance == null) {
            synchronized (SkinManager.class) {
                if (instance == null) {
                    instance = new SkinManager(application);
                }
            }
        }
    }

    private SkinManager(Application application) {
        mContext = application;

        // 注册activity生命周期监听
        SkinActivityLifecycleCallback callback = new SkinActivityLifecycleCallback();
        application.registerActivityLifecycleCallbacks(callback);

        // 资源管理器，用于从app/皮肤包中加载资源
        SkinResources.init(application);

        // 初始化SkinPreference，用于记录当前使用的皮肤
        SkinPreference.init(application);

        // 获取上次保存的皮肤包路径
        String skinPath = getSkinPath();
        // 加载皮肤并应用
        loadSkin(skinPath);

    }

    /**
     * 核心方法：
     * 加载皮肤并应用
     *
     * @param skinPath 皮肤路径 如果为空则使用默认皮肤
     */
    public void loadSkin(String skinPath) {
        Log.i("TAG", "loadSkin：" + skinPath);
        if (TextUtils.isEmpty(skinPath)) {
            // 记录使用默认皮肤
            SkinPreference.getInstance().setSkinPath("");
            // 清空资源管理器中皮肤资源管理属性
            SkinResources.getInstance().reset();
        } else {
            try {
                // 反射创建AssetManager 与 Resource
                AssetManager assetManager = AssetManager.class.newInstance();

                // addAssetPath这个方法是hide的
                // 需要通过反射来调用：设置资源路径目录或压缩包
                Method method = assetManager.getClass().getMethod("addAssetPath", String.class);
                method.invoke(assetManager, skinPath);

                // 根据当前的显示与配置(横竖屏、语言等)创建这个assetManager对应的Resources，
                // 用来获取皮肤包中相应的配置属性值
                Resources resources = new Resources(assetManager, mContext.getResources().getDisplayMetrics(), mContext.getResources().getConfiguration());

                // 获取外部Apk(皮肤包)包名
                PackageManager pm = mContext.getPackageManager();
                PackageInfo info = pm.getPackageArchiveInfo(skinPath, PackageManager.GET_ACTIVITIES);
                String packageName = info.packageName;

                // 保存新的皮肤包路径
                SkinPreference.getInstance().setSkinPath(skinPath);

                // 加载新的皮肤包资源
                SkinResources.getInstance().applySkin(resources, packageName);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 设置状态改变，被观察者改变通知所有观察者
        // 被采集的View更新皮肤
        setChanged();
        notifyObservers();
    }

    /**
     * 获取皮肤包路径
     *
     * @return
     */
    public String getSkinPath() {
        return SkinPreference.getInstance().getSkinPath();
    }
}
