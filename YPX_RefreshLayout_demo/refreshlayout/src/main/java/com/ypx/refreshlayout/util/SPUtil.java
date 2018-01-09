package com.ypx.refreshlayout.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SPUtil {

    /**
     * 保存Preference的name
     */
    public static final String PREFERENCE_NAME = "sp";
    private static SharedPreferences mSharedPreferences;
    private static SPUtil mPreferenceUtils;
    private static SharedPreferences.Editor editor;
    Context context;

    private SPUtil(Context cxt) {
        this.context = cxt;
        mSharedPreferences = cxt.getSharedPreferences(PREFERENCE_NAME,
                Context.MODE_PRIVATE);
    }

    /**
     * 单例模式，获取instance实例
     *
     * @param cxt
     * @return
     */
    public static SPUtil getInstance(Context cxt) {
        if (mPreferenceUtils == null) {
            mPreferenceUtils = new SPUtil(cxt);
        }
        editor = mSharedPreferences.edit();
        return mPreferenceUtils;
    }

    //str_name ："history_search"
    public void setHistorySearch(String str_name, String str_value) {
        editor.putString(str_name, str_value);
        editor.commit();
    }

    public String getHistorySearch(String str_name) {

        return mSharedPreferences.getString(str_name, "");

    }

    //str_name ："flowtipprogress"//"paytipprogress"
    public void setProgressSP(String str_name, String str_value) {
        editor.putString(str_name, str_value);
        editor.commit();
    }

    public String getProgressSP(String str_name) {

        return mSharedPreferences.getString(str_name, "");

    }

    //str_name ："loginstate_remember"//"loginstate_autologin"
    public void setLoginState(String str_name, boolean str_value) {
        editor.putBoolean(str_name, str_value);
        editor.commit();
    }

    //FlowDetail
    public String getFlowDetail(String str_name) {
        return mSharedPreferences.getString(str_name, "");
    }

    //str_name ："loginstate_remember"//"loginstate_autologin"
    public void setFlowDetail(String str_name, String str_value) {
        editor.putString(str_name, str_value);
        editor.commit();
    }

    //FlowDetail
    public String getMessageDetail(String str_name) {
        return mSharedPreferences.getString(str_name, "");
    }

    //str_name ："loginstate_remember"//"loginstate_autologin"
    public void setMessageDetail(String str_name, String str_value) {
        editor.putString(str_name, str_value);
        editor.commit();
    }


    public boolean getLoginState(String str_name) {
        return mSharedPreferences.getBoolean(str_name, false);
    }


    //存手机号
    public void setTelNum(String str_name, String str_value) {
        editor.putString(str_name, str_value);
        editor.commit();
    }

    //   存密码
    public void setPwd(String str_name, String str_value) {
        editor.putString(str_name, str_value);
        editor.commit();
    }

    //取手机号码
    public String getTelNum(String str_name) {
        return mSharedPreferences.getString(str_name, "");
    }

    //取密码
    public String getTelPwd(String str_name) {
        return mSharedPreferences.getString(str_name, "");
    }


    public void setUpdataState(boolean str_value) {
        editor.putBoolean("NEED_UPDATA", str_value);
        editor.commit();
    }

    public boolean getUpdataState() {
        return mSharedPreferences.getBoolean("NEED_UPDATA", false);
    }

    //str_name ："type1/type2/type3/lastmax/nowmax"
    public void setMessageMax(String str_name, int str_value) {
        editor.putInt(str_name, str_value);
        editor.commit();
    }


    public int getMessageMax(String str_name) {
        return mSharedPreferences.getInt(str_name, 0);
    }

    //str_name ："MyMobile"//"loginstate_autologin"
    public void setRefreshTime(String str_name, String str_value) {
        editor.putString(str_name, str_value);
        editor.commit();
    }


    public String getRefreshTime(String str_name) {
        return mSharedPreferences.getString(str_name, "");
    }

}
