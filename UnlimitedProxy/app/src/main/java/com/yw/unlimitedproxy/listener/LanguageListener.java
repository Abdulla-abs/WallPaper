package com.yw.unlimitedproxy.listener;

import android.content.Context;

import java.util.Locale;

public interface LanguageListener {
    /**
     * 获取选择设置语言
     *
     * @param context
     * @return
     */
    Locale getSetLanguageLocale(Context context);
}
