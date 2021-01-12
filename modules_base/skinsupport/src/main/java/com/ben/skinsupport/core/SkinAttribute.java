package com.ben.skinsupport.core;

import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ben.skinsupport.utils.SkinUtils;

import java.util.ArrayList;
import java.util.List;

import androidx.core.view.ViewCompat;

/**
 * 加载属性的辅助类
 *
 * @author: BD
 */
public class SkinAttribute {

    /**
     * 需要换皮肤的属性集合
     */
    private static final List<String> mAttributes = new ArrayList<>();

    /**
     * 存放采集的对象集合
     */
    private static final List<SkinView> mSkinViews = new ArrayList<>();


    // 当前字体
    private Typeface typeface;

    static {

        mAttributes.add("aaaColor");// 如果需要修改第三方的自定义控件，可以这样处理

        mAttributes.add("background");
        mAttributes.add("src");
        mAttributes.add("textColor");

        mAttributes.add("drawableLeft");
        mAttributes.add("drawableRight");
        mAttributes.add("drawableTop");
        mAttributes.add("drawableBottom");

        mAttributes.add("skinTypeface");
    }

    public SkinAttribute(Typeface typeface) {
        this.typeface = typeface;
    }

    public void setTypeface(Typeface typeface) {
        this.typeface = typeface;
    }

    /**
     * 核心方法：采集View的属性和属性值，
     * 并封装对象保存在List<SkinView>
     *
     * @param view
     * @param attrs
     */
    public void load(View view, AttributeSet attrs) {
        List<SkinPair> pairsList = new ArrayList<>();
        for (int i = 0; i < attrs.getAttributeCount(); i++) {
            // 获取属性名
            String attributeName = attrs.getAttributeName(i);
            // 判断需要换肤的属性，过滤掉不需要换肤的属性
            if (!mAttributes.contains(attributeName)) {
                continue;
            }
            String attributeValue = attrs.getAttributeValue(i);
            Log.i("TAG", "attributeValue-->" + attributeValue);
            // 如果是写死的color颜色值，则不替换
            if (attributeValue.startsWith("#")) {
                continue;
            }
            int resId;
            // 如果是系统的属性
            if (attributeValue.startsWith("?")) {
                // 使用系统的属性值
                int attrId = Integer.parseInt(attributeValue.substring(1));
                resId = SkinUtils.getResId(view.getContext(), new int[]{attrId})[0];
            } else {
                // 正常以@开头
                resId = Integer.parseInt(attributeValue.substring(1));
            }
            SkinPair skinPair = new SkinPair(attributeName, resId);
            pairsList.add(skinPair);
        }

        // 如果有属性需要换肤
        if (!pairsList.isEmpty()) {
            SkinView skinView = new SkinView(view, pairsList);
            skinView.applySkin(typeface);
            mSkinViews.add(skinView);
        } else if (view instanceof TextView || view instanceof SkinViewSupport) {
            // 虽然没有属性满足，但需要修改字体 || 自定义View需要更新
            SkinView skinView = new SkinView(view, pairsList);
            skinView.applySkin(typeface);
            mSkinViews.add(skinView);
        }
    }

    /**
     * 更换皮肤时，调用此方法
     */
    public void applySkin() {
        for (SkinView skinView : mSkinViews) {
            skinView.applySkin(typeface);
        }
    }

    /**
     * 封装view和对应的attribute属性集合
     */
    class SkinView {
        private View view;
        private List<SkinPair> pairsList;

        public SkinView(View view, List<SkinPair> pairsList) {
            this.view = view;
            this.pairsList = pairsList;
        }

        /**
         * 核心方法：
         * 遍历这个View的[属性及属性值]集合，给控件设值
         *
         * @param typeface
         */
        public void applySkin(Typeface typeface) {
            // 实现全部字体替换，就在这里
            applyTypeface(typeface);
            // 设置字体
            applySkinSupport();
            for (SkinPair skinPair : pairsList) {
                Drawable left = null, top = null, right = null, bottom = null;
                switch (skinPair.attributeName) {
                    case "background":
                        Object background = SkinResources.getInstance().getBackground(skinPair.resId);
                        if (background instanceof Integer) {
                            view.setBackgroundColor((int) background);
                        } else {
                            ViewCompat.setBackground(view, (Drawable) background);
                        }
                        break;
                    case "src":
                        background = SkinResources.getInstance().getBackground(skinPair.resId);
                        if (background instanceof Integer) {
                            ((ImageView) view).setImageDrawable(new ColorDrawable((Integer) background));
                        } else {
                            ((ImageView) view).setImageDrawable((Drawable) background);
                        }
                        break;
                    case "textColor":
                        int colorResId = SkinResources.getInstance().getColor(skinPair.resId);
                        ((TextView) view).setTextColor(colorResId);
                        break;
                    case "drawableLeft":
                        left = SkinResources.getInstance().getDrawable(skinPair.resId);
                        break;
                    case "drawableTop":
                        top = SkinResources.getInstance().getDrawable(skinPair.resId);
                        break;
                    case "drawableRight":
                        right = SkinResources.getInstance().getDrawable(skinPair.resId);
                        break;
                    case "drawableBottom":
                        bottom = SkinResources.getInstance().getDrawable(skinPair.resId);
                        break;
                    case "skinTypeface":
//                        applyTypeface(SkinResources.getInstance().getTypeface(skinPair.resId));
                        break;
                    case "aaacolor":

                        break;
                    default:
                        break;
                }
                // 设置TextView的drawable属性
                if (null != left || null != top || null != right || null != bottom) {
                    ((TextView) view).setCompoundDrawablesRelativeWithIntrinsicBounds(left, top, right, bottom);
                }
            }

        }

        /**
         * 自定义view
         */
        private void applySkinSupport() {
            if (view instanceof SkinViewSupport) {
                ((SkinViewSupport) view).applySkin();
            }
        }

        /**
         * 设值字体
         *
         * @param typeface
         */
        private void applyTypeface(Typeface typeface) {
            if (view instanceof TextView) {
                ((TextView) view).setTypeface(typeface);
            }
        }
    }

    /**
     * 属性名称和属性值ID
     */
    class SkinPair {
        private String attributeName;
        private int resId;

        public SkinPair(String attributeName, int resId) {
            this.attributeName = attributeName;
            this.resId = resId;
        }
    }
}
