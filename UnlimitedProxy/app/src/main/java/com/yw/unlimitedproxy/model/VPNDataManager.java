package com.yw.unlimitedproxy.model;

import android.content.Context;
import android.content.res.AssetManager;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.yw.unlimitedproxy.utils.LogUtils;
import com.yw.unlimitedproxy.utils.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class VPNDataManager {

    private static final String TAG = VPNDataManager.class.getSimpleName();
    private final Gson gson;
    private List<ServerBean> allBeans;
    private List<ServerBean> fastBeans;

    public List<ServerBean> getAllBeans() {
        return allBeans;
    }

    public List<ServerBean> getFastBeans() {
        return fastBeans;
    }

    private static class SingletonInstance {
        private static final VPNDataManager INSTANCE = new VPNDataManager();
    }

    public static VPNDataManager getInstance() {
        return SingletonInstance.INSTANCE;
    }

    private VPNDataManager() {
        gson = new Gson();

        //获取本地的app配置文件
        String localServerDatas = getJson(Utils.getContext(), "masterUnlimitedProxy_localserver.json");
        updateAppConfig(localServerDatas);

        //获取远程配置文件
        //updateAppConfig(RemoteConfig.INSTANCE.getDynamicConfigJson());
    }

    public String getJson(Context context, String fileName) {

        StringBuilder stringBuilder = new StringBuilder();
        AssetManager assetManager = context.getAssets();
        try {
            BufferedReader bf = new BufferedReader(new InputStreamReader(
                    assetManager.open(fileName)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
            bf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    public void updateAppConfig(String appConfigJson) {

        LogUtils.d(TAG, "appConfigJson : " + appConfigJson);
        if (TextUtils.isEmpty(appConfigJson)) return;
        try {
            //asset获取服务列表数据
            ServerData serverData = gson.fromJson(appConfigJson, ServerData.class);
            allBeans = serverData.getAll();
            fastBeans = serverData.getFastServers();

            LogUtils.d(TAG, "serverData : " + serverData.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
