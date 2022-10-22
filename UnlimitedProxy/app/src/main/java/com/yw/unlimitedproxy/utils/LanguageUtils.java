package com.yw.unlimitedproxy.utils;

import android.content.Context;
import android.content.res.Configuration;

import com.yw.unlimitedproxy.R;

import java.util.Locale;

public class LanguageUtils {
    private static final String TAG = "LocalManageUtil";

    /**
     * 获取系统的locale
     *
     * @return Locale对象
     */
    public static Locale getSystemLocale(Context context) {
        return SPUtils.getInstance(context).getSystemCurrentLocal();
    }
//
//    public static String getSelectLanguage(Context context) {
//        switch (SPUtils.getInstance().getSelectLanguage()) {
//            case 0:
//                return context.getString(R.string.language_auto);
//            case 1:
//                return context.getString(R.string.language_cn);
//            case 2:
//                return context.getString(R.string.language_traditional);
//            case 3:
//            default:
//                return context.getString(R.string.language_en);
//        }
//    }

    /**
     * 获取选择的语言设置
     *
     * @param context
     * @return
     */
    public static Locale getSetLanguageLocale(Context context) {
        switch (SPUtils.getInstance(context).getSelectLanguage()) {
            case 0:
                return getSystemLocale(context);
            case 1:
                return Locale.CHINA;
            default:
                return Locale.ENGLISH;
        }
    }


    public static void saveSystemCurrentLanguage(Context context) {
        SPUtils.getInstance(context).setSystemCurrentLocal(MultiLanguage.getSystemLocal(context));
    }

    /**
     * 保存系统语言
     * @param context
     * @param newConfig
     */
    public static void saveSystemCurrentLanguage(Context context, Configuration newConfig) {

        SPUtils.getInstance(context).setSystemCurrentLocal(MultiLanguage.getSystemLocal(newConfig));
    }

    public static void saveSelectLanguage(Context context, int select) {
        SPUtils.getInstance(context).saveLanguage(select);
        MultiLanguage.setApplicationLanguage(context);
    }
}