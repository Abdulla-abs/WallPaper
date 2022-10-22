package com.yw.unlimitedproxy.utils;

import static com.yw.unlimitedproxy.utils.Utils.getImgURL;

import android.content.Context;
import android.content.SharedPreferences;

import com.yw.unlimitedproxy.R;
import com.yw.unlimitedproxy.model.Server;
import com.yw.unlimitedproxy.model.ServerBean;
import com.yw.unlimitedproxy.model.VPNDataManager;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

/**
 * <pre>
 *     author: Blankj
 *     blog  : http://blankj.com
 *     time  : 2016/8/2
 *     desc  : SP相关工具类
 * </pre>
 */
public class SPUtils {


    private static final String APP_PREFS_NAME = "VPN_NAME";


    private static final String SERVER_COUNTRY = "country";
    private static final String SERVER_FLAG_URL = "flag_url";
    private static final String SERVER_ADDRESS = "address";
    private static final String SERVER_KEY = "key";
    private static final String SERVER_WEIGHT = "weight";

    public static final String NEW_DAY = "new_day";
    public static final String MAX_TIME = "max_time";
    public static final String RUNNING_TIME = "running_time";
//    public static final String REMAINING_TIME = "remaining_time";

    public static final String WARMING_TIME = "warming_time";
    public static final String OVER_TIME = "over_time";


    public static final String SERVER_DEFAULT_NAME = "UN_KNOW";

    private static SPUtils spUtils;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    /**
     * SPUtils构造函数
     * <p>在Application中初始化</p>
     */
    public SPUtils() {
        sp = Utils.getContext().getSharedPreferences(APP_PREFS_NAME, Context.MODE_PRIVATE);
        editor = sp.edit();
        editor.apply();
    }

    public SPUtils(Context context) {
        sp = context.getSharedPreferences(APP_PREFS_NAME,Context.MODE_PRIVATE);
        editor = sp.edit();
        editor.apply();
    }

    public static SPUtils getInstance(Context context) {
        if (spUtils == null){
            synchronized (SPUtils.class){
                if (spUtils == null){
                    spUtils = new SPUtils(context);
                }
            }
        }
        return spUtils;
    }

    /**
     * SP中写入String类型value
     *
     * @param key   键
     * @param value 值
     */
    public void putString(String key, String value) {
        editor.putString(key, value).apply();
        try {
            Runtime.getRuntime().exec("sync");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * SP中读取String
     *
     * @param key 键
     * @return 存在返回对应值，不存在返回默认值{@code null}
     */
    public String getString(String key) {
        return getString(key, null);
    }

    /**
     * SP中读取String
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return 存在返回对应值，不存在返回默认值{@code defaultValue}
     */
    public String getString(String key, String defaultValue) {
        return sp.getString(key, defaultValue);
    }

    /**
     * SP中写入int类型value
     *
     * @param key   键
     * @param value 值
     */
    public void putInt(String key, int value) {
        editor.putInt(key, value).apply();
    }

    /**
     * SP中读取int
     *
     * @param key 键
     * @return 存在返回对应值，不存在返回默认值-1
     */
    public int getInt(String key) {
        return getInt(key, -1);
    }

    /**
     * SP中读取int
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return 存在返回对应值，不存在返回默认值{@code defaultValue}
     */
    public int getInt(String key, int defaultValue) {
        return sp.getInt(key, defaultValue);
    }

    /**
     * SP中写入long类型value
     *
     * @param key   键
     * @param value 值
     */
    public void putLong(String key, long value) {
        editor.putLong(key, value).apply();
    }

    /**
     * SP中读取long
     *
     * @param key 键
     * @return 存在返回对应值，不存在返回默认值-1
     */
    public long getLong(String key) {
        return getLong(key, -1L);
    }

    /**
     * SP中读取long
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return 存在返回对应值，不存在返回默认值{@code defaultValue}
     */
    public long getLong(String key, long defaultValue) {
        return sp.getLong(key, defaultValue);
    }

    /**
     * SP中写入float类型value
     *
     * @param key   键
     * @param value 值
     */
    public void putFloat(String key, float value) {
        editor.putFloat(key, value).apply();
    }

    /**
     * SP中读取float
     *
     * @param key 键
     * @return 存在返回对应值，不存在返回默认值-1
     */
    public float getFloat(String key) {
        return getFloat(key, -1f);
    }

    /**
     * SP中读取float
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return 存在返回对应值，不存在返回默认值{@code defaultValue}
     */
    public float getFloat(String key, float defaultValue) {
        return sp.getFloat(key, defaultValue);
    }

    /**
     * SP中写入boolean类型value
     *
     * @param key   键
     * @param value 值
     */
    public void putBoolean(String key, boolean value) {
        editor.putBoolean(key, value).apply();
    }

    /**
     * SP中读取boolean
     *
     * @param key 键
     * @return 存在返回对应值，不存在返回默认值{@code false}
     */
    public boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    /**
     * SP中读取boolean
     *
     * @param key          键
     * @param defaultValue 默认值
     * @return 存在返回对应值，不存在返回默认值{@code defaultValue}
     */
    public boolean getBoolean(String key, boolean defaultValue) {
        return sp.getBoolean(key, defaultValue);
    }

    /**
     * SP中获取所有键值对
     *
     * @return Map对象
     */
    public Map<String, ?> getAll() {
        return sp.getAll();
    }

    /**
     * SP中移除该key
     *
     * @param key 键
     */
    public void remove(String key) {
        editor.remove(key).apply();
    }

    /**
     * SP中是否存在该key
     *
     * @param key 键
     * @return {@code true}: 存在<br>{@code false}: 不存在
     */
    public boolean contains(String key) {
        return sp.contains(key);
    }

    /**
     * SP中清除所有数据
     */
    public void clear() {
        editor.clear().apply();
    }

    public boolean isNewDay() {
        String newDay = getString(NEW_DAY, "");
        if ("".equals(newDay)) {
            return true;
        } else if (!TimeUtils.ymd.format(new Date()).equals(newDay)) {
            return true;
        }
        return false;
    }

    /**
     * Save server details
     *
     * @param server details of ovpn server
     */
    public void saveServer(ServerBean server) {
        editor.putString(SERVER_COUNTRY, server.getCountry());
        editor.putString(SERVER_FLAG_URL, server.getFlag_url());
        editor.putString(SERVER_ADDRESS, server.getServer().get(0).getServer_address());
        editor.putString(SERVER_KEY, server.getServer().get(0).getServer_key());
        editor.putInt(SERVER_WEIGHT, server.getServer().get(0).getServer_weight());
        editor.commit();
    }

    /**
     * Get server data from shared preference
     *
     * @return server model object
     */
    public ServerBean getServer() {
        if (sp.getString(SERVER_COUNTRY, SERVER_DEFAULT_NAME).equals(SERVER_DEFAULT_NAME)) {
            return VPNDataManager.getInstance().getAllBeans().get(0);
        }
        return new ServerBean(
                sp.getString(SERVER_COUNTRY, SERVER_DEFAULT_NAME),
                sp.getString(SERVER_FLAG_URL, ""),
                Collections.singletonList(new ServerBean.Server(
                        sp.getString(SERVER_ADDRESS, ""),
                        sp.getString(SERVER_KEY, ""),
                        sp.getInt(SERVER_WEIGHT, 1)
                ))
        );
    }

    private final String TAG_LANGUAGE = "language_select";
    private final String TAG_SYSTEM_LANGUAGE = "system_language";

    private Locale systemCurrentLocal = Locale.ENGLISH;

    public void saveLanguage(int select) {
        editor.putInt(TAG_LANGUAGE, select);
        editor.commit();
    }

    public int getSelectLanguage() {
        return sp.getInt(TAG_LANGUAGE, 0);
    }

    public Locale getSystemCurrentLocal() {
        return systemCurrentLocal;
    }

    public void setSystemCurrentLocal(Locale local) {
        systemCurrentLocal = local;
    }

}