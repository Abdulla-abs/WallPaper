package com.yw.unlimitedproxy;

import static com.yw.unlimitedproxy.utils.HandlerHeart.MAX_TIME;

import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.yw.unlimitedproxy.listener.LanguageListener;
import com.yw.unlimitedproxy.model.VPNDataManager;
import com.yw.unlimitedproxy.ui.base.BaseActivity;
import com.yw.unlimitedproxy.utils.LanguageUtils;
import com.yw.unlimitedproxy.utils.LogUtils;
import com.yw.unlimitedproxy.utils.MultiLanguage;
import com.yw.unlimitedproxy.utils.OpenVPNUtils;
import com.yw.unlimitedproxy.utils.SPUtils;
import com.yw.unlimitedproxy.utils.TimeUtils;
import com.yw.unlimitedproxy.utils.Utils;

import java.io.File;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 *
 */
public class App extends Application {

    private static boolean isNewDay = false;    //新的一天，重置时间
    private static boolean isWaringTime = false;    //是否是警告时间
    private static boolean isTimeOver = false;  //是否是结束时间

    public static SPUtils spUtils;
    //app文件夹地址
    public static String dir;

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化
        Utils.init(this);
        spUtils = new SPUtils();
        OpenVPNUtils.init();

        VPNDataManager.getInstance();
        OpenVPNUtils.getInstance();

        dir = getExternalFilesDir(null).getPath() + File.separator;

        MultiLanguage.init(new LanguageListener() {
            @Override
            public Locale getSetLanguageLocale(Context context) {
                return LanguageUtils.getSetLanguageLocale(context);
            }
        });
        MultiLanguage.setApplicationLanguage(this);

        //是否新的一天
        if (spUtils.isNewDay()) {
            isNewDay = true;
            spUtils.putString(SPUtils.NEW_DAY, TimeUtils.ymd.format(new Date()));
            spUtils.putInt(SPUtils.RUNNING_TIME, 0);
            spUtils.putBoolean(SPUtils.WARMING_TIME, false);
            spUtils.putBoolean(SPUtils.OVER_TIME, false);
        }


        //openVpn速度监听回调
//        OpenVPNUtils.getInstance().registerStatusListener(this);
        //注册广播以监听速度
//        LocalBroadcastManager.getInstance(this).registerReceiver(OpenVPNUtils.getInstance().getBroadcastReceiver(), new IntentFilter("connectionState"));
    }

    /**
     * first entry app to save system language
     * @param base context
     */
    @Override
    protected void attachBaseContext(Context base) {
        LanguageUtils.saveSystemCurrentLanguage(base);
        super.attachBaseContext(base);
    }

    /**
     * user at system language setting changed,to save language
     * @param newConfig
     */
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LanguageUtils.saveSystemCurrentLanguage(getApplicationContext(),newConfig);
        MultiLanguage.onConfigurationChanged(getApplicationContext());
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        LogUtils.e("onLowMemory");
    }


}
