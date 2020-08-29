package com.example.lib;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.view.ViewCompat;

import com.example.lib.utils.SkinResources;
import com.example.lib.utils.SkinThemeUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Jack Ou
 * @CreateDate: 2020/8/29 17:35
 * @UpdateUser: 更新者
 * @UpdateDate: 2020/8/29 17:35
 * @UpdateRemark: 更新说明
 */
public class SkinAttribute {

    //需要换肤的属性
    private static final List<String> mAttributes = new ArrayList<>();
    static {
        mAttributes.add("background");
        mAttributes.add("src");
        mAttributes.add("textColor");
        mAttributes.add("drawableLeft");
        mAttributes.add("drawableTop");
        mAttributes.add("drawableRight");
        mAttributes.add("drawableBottom");
    }

    //保存所有需要换肤的view
    private List<SkinView> mSkinViews = new ArrayList<>();

    //记录下一个VIEW身上哪几个属性需要换肤textColor/src
    public void look(View view, AttributeSet attrs) {
        List<SkinPair> mSkinPars = new ArrayList<>();

        for (int i = 0; i < attrs.getAttributeCount(); i++) {
            //获得属性名  textColor/background
            String attributeName = attrs.getAttributeName(i);
            if (mAttributes.contains(attributeName)) {
                // #
                // ?722727272
                // @722727272
                String attributeValue = attrs.getAttributeValue(i);
                // 比如color 以#开头表示写死的颜色 不可用于换肤
                if (attributeValue.startsWith("#")) {
                    continue;
                }
                int resId;
                // 以 ？开头的表示使用 属性
                if (attributeValue.startsWith("?")) {
                    int attrId = Integer.parseInt(attributeValue.substring(1));
                    resId = SkinThemeUtils.getResId(view.getContext(), new int[]{attrId})[0];
                } else {
                    // 正常以 @ 开头
                    resId = Integer.parseInt(attributeValue.substring(1));
                }

                SkinPair skinPair = new SkinPair(attributeName, resId);
                mSkinPars.add(skinPair);
            }
        }

        if (!mSkinPars.isEmpty() || view instanceof SkinViewSupport) {
            SkinView skinView = new SkinView(view, mSkinPars);
            // 如果选择过皮肤 ，调用 一次 applySkin 加载皮肤的资源
            skinView.applySkin();
            mSkinViews.add(skinView);
        }
    }

    /*
       对所有的view中的所有的属性进行皮肤修改
     */
    public void applySkin() {
        for (SkinView mSkinView : mSkinViews) {
            mSkinView.applySkin();
        }
    }

    /**
     * 一个view对应的所有需要修改的属性
     */
    static class SkinView {
        View view;
        List<SkinPair> skinPairs;

        public SkinView(View view, List<SkinPair> skinPairs) {
            this.view = view;
            this.skinPairs = skinPairs;
        }

        public void applySkin() {
            applySkinSupport();
            for (SkinPair skinPair : skinPairs) {
                Drawable left = null, top = null, right = null, bottom = null;
                switch (skinPair.attributeName) {
                    case "background":
                        Object background = SkinResources.getInstance().getBackground(skinPair
                                .resId);
                        //背景可能是 @color 也可能是 @drawable
                        if (background instanceof Integer) {
                            view.setBackgroundColor((int) background);
                        } else {
                            ViewCompat.setBackground(view, (Drawable) background);
                        }
                        break;
                    case "src":
                        background = SkinResources.getInstance().getBackground(skinPair
                                .resId);
                        if (background instanceof Integer) {
                            ((ImageView) view).setImageDrawable(new ColorDrawable((Integer)
                                    background));
                        } else {
                            ((ImageView) view).setImageDrawable((Drawable) background);
                        }
                        break;
                    case "textColor":
                        ((TextView) view).setTextColor(SkinResources.getInstance().getColorStateList(skinPair.resId));
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
                    default:
                        break;
                }
                if (null != left || null != right || null != top || null != bottom) {
                    ((TextView) view).setCompoundDrawablesWithIntrinsicBounds(left, top, right,
                            bottom);
                }
            }
        }
        //自定义控件也支持换肤
        private void applySkinSupport() {
            if (view instanceof SkinViewSupport) {
                ((SkinViewSupport) view).applySkin();
            }
        }
    }

    /**
     * 每一个view中需要修改内容的属性名与值
     */
    static class SkinPair {
        //属性名
        String attributeName;
        //属性对应的Id值
        int resId;

        public SkinPair(String attributeName, int resId) {
            this.attributeName = attributeName;
            this.resId = resId;
        }
    }
}
