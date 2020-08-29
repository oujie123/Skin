package com.example.lib.utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;

/**
 * @Author: Jack Ou
 * @CreateDate: 2020/8/29 16:52
 * @UpdateUser: 更新者
 * @UpdateDate: 2020/8/29 16:52
 * @UpdateRemark: 更新说明
 */
public class SkinResources {

    private static volatile SkinResources mInstance;
    private String mSkinPkgName;
    private boolean isDefaultSkin = true;
    private Context mContext;

    // app原始的resource
    private Resources mAppResources;
    // 皮肤包的resource
    private Resources mSkinResources;

    private SkinResources(Context context) {
        mAppResources = context.getResources();
        this.mContext = context;
    }

    public static void init(Context context) {
        if (mInstance == null) {
            synchronized (SkinResources.class) {
                if (mInstance == null) {
                    mInstance = new SkinResources(context);
                }
            }
        }
    }

    public static SkinResources getInstance() {
        return mInstance;
    }

    /**
     * 复位释放资源
     */
    public void reset() {
        mSkinResources = null;
        mSkinPkgName = "";
        isDefaultSkin = true;
    }

    /**
     * 皮肤生效
     *
     * @param resources 皮肤资源
     * @param pkgName   皮肤包包名，用于保存修改后的皮肤到sp中
     */
    public void applySkin(Resources resources, String pkgName) {
        mSkinResources = resources;
        mSkinPkgName = pkgName;
        //是否使用默认皮肤
        isDefaultSkin = TextUtils.isEmpty(pkgName) || resources == null;
    }

    /**
     * 1.通过原始app中的resId(R.color.XX)获取到自己的 名字
     * 2.根据名字和类型获取皮肤包中的ID
     */
    public int getIdentifier(int resId) {
        if (isDefaultSkin) {
            return resId;
        }
        String resName = mAppResources.getResourceEntryName(resId);
        String resType = mAppResources.getResourceTypeName(resId);
        return mSkinResources.getIdentifier(resName, resType, mSkinPkgName);
    }

    /**
     * 通过传入宿主颜色的resId,获得应该设置的颜色对应的Id值
     *
     * @param resId 宿主app的resourceId值
     * @return 颜色值
     */
    public int getColor(int resId) {
        //TODO 默认皮肤是否可以直接返回resId
        //不可以，因为此处返回的是具体资源内容了通过id查到资源内容返回
        if (isDefaultSkin) {
            return mAppResources.getColor(resId, mContext.getTheme());
        }
        int skinId = getIdentifier(resId);
        if (skinId == 0) {
            return mAppResources.getColor(resId, mContext.getTheme());
        }
        return skinId;
    }

    //getColorStateList该方法如果没有主题可以传空
    public ColorStateList getColorStateList(int resId) {
        if (isDefaultSkin) {
            return mAppResources.getColorStateList(resId, mContext.getTheme());
        }
        int skinId = getIdentifier(resId);
        if (skinId == 0) {
            return mAppResources.getColorStateList(resId, mContext.getTheme());
        }
        return mSkinResources.getColorStateList(skinId, null);
    }

    public Drawable getDrawable(int resId) {
        if (isDefaultSkin) {
            return mAppResources.getDrawable(resId, mContext.getTheme());
        }
        //通过 app的resource 获取id 对应的 资源名 与 资源类型
        //找到 皮肤包 匹配 的 资源名资源类型 的 皮肤包的 资源 ID
        int skinId = getIdentifier(resId);
        if (skinId == 0) {
            return mAppResources.getDrawable(resId, mContext.getTheme());
        }
        return mSkinResources.getDrawable(skinId, null);
    }


    /**
     * 可能是Color 也可能是drawable
     *
     * @return
     */
    public Object getBackground(int resId) {
        String resourceTypeName = mAppResources.getResourceTypeName(resId);

        if ("color".equals(resourceTypeName)) {
            return getColor(resId);
        } else {
            // drawable
            return getDrawable(resId);
        }
    }
}
