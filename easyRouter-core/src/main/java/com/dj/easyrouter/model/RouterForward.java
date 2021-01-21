package com.dj.easyrouter.model;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;

import com.dj.easyrouter.EasyRouter;
import com.dj.easyrouter.callback.NavigationCallback;
import com.dj.easyrouter.inter.IProvider;

import java.util.ArrayList;

/**
 * 用于构建跳转信息相关
 */
public class RouterForward extends EasyRouteMeta{
    private Bundle mBundle;
    private Bundle optionsCompat;
    private int flag = -1;
    //进场、离场动画
    private int enterAnim;
    private int exitAnim;
    /**
     * 是否是绿色通道（不需要走拦截器）
     */
    private boolean greenChannel;
    private IProvider provider;

    public RouterForward(String path, String group) {
        this(path, group, null);
    }

    public RouterForward(String path, String group, Bundle bundle) {
        setPath(path);
        setGroup(group);
        this.mBundle = (null == bundle ? new Bundle() : bundle);
    }

    /**
     * Intent.FLAG_ACTIVITY**
     * @param flag
     * @return
     */
    public RouterForward withFlag(int flag) {
        this.flag = flag;
        return this;
    }

    public int getFlag() {
        return flag;
    }

    public Bundle getExtras() {
        return mBundle;
    }

    /**
     * 转场动画
     *
     * @param enterAnim
     * @param exitAnim
     * @return
     */
    public RouterForward withTransition(int enterAnim, int exitAnim) {
        this.enterAnim = enterAnim;
        this.exitAnim = exitAnim;
        return this;
    }

    /**
     * 获取进场动画
     * @return
     */
    public int getEnterAnim() {
        return enterAnim;
    }

    /**
     * 获取离场动画
     * @return
     */
    public int getExitAnim() {
        return exitAnim;
    }

    public boolean isGreenChannel() {
        return greenChannel;
    }

    public RouterForward greenChannel() {
        this.greenChannel = true;
        return this;
    }


    public RouterForward withString(@Nullable String key, @Nullable String value) {
        mBundle.putString(key, value);
        return this;
    }


    public RouterForward withBoolean(@Nullable String key, boolean value) {
        mBundle.putBoolean(key, value);
        return this;
    }


    public RouterForward withShort(@Nullable String key, short value) {
        mBundle.putShort(key, value);
        return this;
    }


    public RouterForward withInt(@Nullable String key, int value) {
        mBundle.putInt(key, value);
        return this;
    }


    public RouterForward withLong(@Nullable String key, long value) {
        mBundle.putLong(key, value);
        return this;
    }


    public RouterForward withDouble(@Nullable String key, double value) {
        mBundle.putDouble(key, value);
        return this;
    }


    public RouterForward withByte(@Nullable String key, byte value) {
        mBundle.putByte(key, value);
        return this;
    }


    public RouterForward withChar(@Nullable String key, char value) {
        mBundle.putChar(key, value);
        return this;
    }


    public RouterForward withFloat(@Nullable String key, float value) {
        mBundle.putFloat(key, value);
        return this;
    }


    public RouterForward withParcelable(@Nullable String key, @Nullable Parcelable value) {
        mBundle.putParcelable(key, value);
        return this;
    }


    public RouterForward withStringArray(@Nullable String key, @Nullable String[] value) {
        mBundle.putStringArray(key, value);
        return this;
    }


    public RouterForward withBooleanArray(@Nullable String key, boolean[] value) {
        mBundle.putBooleanArray(key, value);
        return this;
    }


    public RouterForward withShortArray(@Nullable String key, short[] value) {
        mBundle.putShortArray(key, value);
        return this;
    }


    public RouterForward withIntArray(@Nullable String key, int[] value) {
        mBundle.putIntArray(key, value);
        return this;
    }


    public RouterForward withLongArray(@Nullable String key, long[] value) {
        mBundle.putLongArray(key, value);
        return this;
    }


    public RouterForward withDoubleArray(@Nullable String key, double[] value) {
        mBundle.putDoubleArray(key, value);
        return this;
    }


    public RouterForward withByteArray(@Nullable String key, byte[] value) {
        mBundle.putByteArray(key, value);
        return this;
    }


    public RouterForward withCharArray(@Nullable String key, char[] value) {
        mBundle.putCharArray(key, value);
        return this;
    }


    public RouterForward withFloatArray(@Nullable String key, float[] value) {
        mBundle.putFloatArray(key, value);
        return this;
    }


    public RouterForward withParcelableArray(@Nullable String key, @Nullable Parcelable[] value) {
        mBundle.putParcelableArray(key, value);
        return this;
    }

    public RouterForward withParcelableArrayList(@Nullable String key, @Nullable ArrayList<? extends Parcelable> value) {
        mBundle.putParcelableArrayList(key, value);
        return this;
    }

    public RouterForward withIntegerArrayList(@Nullable String key, @Nullable ArrayList<Integer> value) {
        mBundle.putIntegerArrayList(key, value);
        return this;
    }

    public RouterForward withStringArrayList(@Nullable String key, @Nullable ArrayList<String> value) {
        mBundle.putStringArrayList(key, value);
        return this;
    }

    /**
     * 转场动画
     *
     * @param compat
     * @return
     */
    public RouterForward withOptionsCompat(ActivityOptionsCompat compat) {
        if (null != compat) {
            this.optionsCompat = compat.toBundle();
        }
        return this;
    }

    public Bundle getOptionsBundle() {
        return optionsCompat;
    }

    public IProvider getProvider() {
        return provider;
    }

    public void setProvider(IProvider provider) {
        this.provider = provider;
    }

    /**
     * 用于停止service
     * @return
     */
    public Object stopNavigation(){
        return EasyRouter.getInstance().stopNavigation(null, this, null);
    }

    /**
     * 用于停止service
     * @return
     */
    public Object stopNavigation(Context context){
        return EasyRouter.getInstance().stopNavigation(context, this, null);
    }

    /**
     * 简单跳转
     * @return
     */
    public Object navigation() {
        return EasyRouter.getInstance().navigation(null, this, -1, null);
    }

    /**
     * 简单跳转
     * @return
     */
    public Object navigation(Context context) {
        return EasyRouter.getInstance().navigation(context, this, -1, null);
    }

    /**
     * 页面跳转
     * @param context
     * @param callback 跳转事件的回调（注意不是页面跳转返回的回调）
     * @return
     */
    public Object navigation(Context context, NavigationCallback callback) {
        return EasyRouter.getInstance().navigation(context, this, -1, callback);
    }

    /**
     * 页面跳转 forResult
     * @param context
     * @param requestCode
     * @return
     */
    public Object navigationForResult(Context context, int requestCode) {
        return EasyRouter.getInstance().navigation(context, this, requestCode, null);
    }

    /**
     * 页面跳转 forResult
     * @param context
     * @param requestCode
     * @param callback 跳转事件的回调（注意不是页面跳转返回的回调）
     * @return
     */
    public Object navigationForResult(Context context, int requestCode, NavigationCallback callback) {
        return EasyRouter.getInstance().navigation(context, this, requestCode, callback);
    }
}
