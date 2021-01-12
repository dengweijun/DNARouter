package com.ben.skinsupport.core;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.ben.skinsupport.utils.SkinUtils;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 创建布局的工厂
 *
 * @author: BD
 */
public class SkinLayoutFactory implements LayoutInflater.Factory2, Observer {

    private LayoutInflater.Factory2 mViewCreateFactory2;

    public void setInterceptFactory2(LayoutInflater.Factory2 factory) {
        mViewCreateFactory2 = factory;
    }

    /**
     * android api 自带View的包前缀
     */
    private static final String[] mClassPrefix = {"android.view.", "android.widget.", "android.webkit."};

    /**
     * key：View的名称，value：View的构造方法
     */
    private static final Map<String, Constructor<? extends View>> mConstructorMap = new HashMap<>();

    /**
     * View的构造方法参数类型集合
     */
    private static final Class<?>[] mClassSignature = {Context.class, AttributeSet.class};

    private Activity activity;
    private SkinAttribute skinAttribute;

    public SkinLayoutFactory(Activity activity, Typeface typeface) {
        this.activity = activity;
        this.skinAttribute = new SkinAttribute(typeface);
    }

    @Nullable
    @Override
    public View onCreateView(@Nullable View parent, @NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        View view = null;
        if (mViewCreateFactory2 != null) {
            view = mViewCreateFactory2.onCreateView(parent, name, context, attrs);
        }
        if (null == view) {
            view = createViewFromTag(name, context, attrs);
        }
        if (null != view) {
            // 在这里加载View的属性AttributeSet
            skinAttribute.load(view, attrs);
        }
        return view;
    }

    /**
     * 创建View
     *
     * @param name：View的名称，SimpleName
     * @param context
     * @param attrs
     * @return
     */
    private View createViewFromTag(String name, Context context, AttributeSet attrs) {
        // View的名称如果包含了"."，说明是自定义的View或者androidx的View；
        // 因为是全称（包含了包名），直接传入name；
        if (name.contains(".")) {
            return createView(name, context, attrs);
        } else {
            View view = null;
            // 如果是android api 自带的View控件，则需要加上前面的包名
            for (int i = 0; i < mClassPrefix.length; i++) {
                view = createView(mClassPrefix[i] + name, context, attrs);
                if (view != null) {
                    return view;
                }
            }
            return view;
        }
    }

    /**
     * 创建View
     *
     * @param name：View的全称，包含了包名
     * @param context
     * @param attrs
     * @return
     */
    private View createView(String name, Context context, AttributeSet attrs) {
        // 拿到缓存中的构造方法
        Constructor<? extends View> constructor = findConstructor(name, context);
        try {
            // 通过构造方法的反射，创建View
            return constructor.newInstance(context, attrs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 拿到缓存中的构造方法
     *
     * @param name
     * @param context
     * @return
     */
    private Constructor<? extends View> findConstructor(String name, Context context) {
        Constructor<? extends View> constructor = mConstructorMap.get(name);
        if (constructor == null) {
            try {
                Class<? extends View> clazz = context.getClassLoader().loadClass(name).asSubclass(View.class);
                constructor = clazz.getConstructor(mClassSignature);
                mConstructorMap.put(name, constructor);
            } catch (ClassNotFoundException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        return constructor;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        return null;
    }

    @Override
    public void update(Observable o, Object arg) {
        // 通知被观察者更新

        // 更新状态栏颜色
        SkinUtils.updateStatusBarColor(activity);
        // 设置新字体
        Typeface typeface = SkinUtils.getSkinTypeface(activity);
        skinAttribute.setTypeface(typeface);
        // 更新皮肤包
        skinAttribute.applySkin();
    }
}
