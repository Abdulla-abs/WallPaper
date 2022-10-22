package com.yw.unlimitedproxy.utils;

import android.content.Context;
import android.content.Intent;
import android.os.RemoteException;
import android.util.Log;

import com.yw.unlimitedproxy.App;
import com.yw.unlimitedproxy.model.ServerBean;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.blinkt.openvpn.OpenVpnApi;
import de.blinkt.openvpn.core.ConnectionStatus;
import de.blinkt.openvpn.core.OpenVPNService;
import de.blinkt.openvpn.core.OpenVPNThread;
import de.blinkt.openvpn.core.VpnStatus;

public class OpenVPNUtils {

    private Context context;

    //当前连接服务
    private ServerBean server;

    public static int MAX_TIME = TimeUtils.hour * 2;
    private boolean isNewDay = false;
    private final SPUtils spUtils = new SPUtils();

    private int lastRunningTime = 0;
    private String remainingTime = TimeUtils.convertIntTimeToStringTime(MAX_TIME);

    //状态监听
    private final List<OnVpnListener> vpnListeners = new ArrayList<>();

    //是否启动
    private boolean isStart = false;

    //vpn状态
    private String status = "";

    //初始化
    private OpenVPNUtils() {
        context = Utils.getContext();
        server = spUtils.getServer();
        //是否新的一天
        if (spUtils.isNewDay()) {
            isNewDay = true;
            spUtils.putString(SPUtils.NEW_DAY, TimeUtils.ymd.format(new Date()));
            spUtils.putInt(SPUtils.RUNNING_TIME, lastRunningTime);
            spUtils.putBoolean(SPUtils.WARMING_TIME, false);
            spUtils.putBoolean(SPUtils.OVER_TIME, false);
        } else {
            lastRunningTime = spUtils.getInt(SPUtils.RUNNING_TIME, MAX_TIME);
            Log.e("OpenVPN初始化", lastRunningTime +"");

//            isWaringTime = MAX_TIME - runningTime < 30 * TimeUtils.min;
//            isTimeOver = runningTime - MAX_TIME >= 0;
//            isWaringTime = spUtils.getBoolean(SPUtils.WARMING_TIME, true);
//            isTimeOver = spUtils.getBoolean(SPUtils.OVER_TIME, true);

            remainingTime = TimeUtils.convertRunningTimeToRemainingTime(
                    lastRunningTime,
                    MAX_TIME
            );
        }
        VpnStatus.addStateListener(new VpnStatus.StateListener() {
            @Override
            public void updateState(String state, String logmessage, int localizedResId, ConnectionStatus level, Intent Intent) {
                setStatus(state);
                LogUtils.e("-----------状态改变-------》         ", state + logmessage + localizedResId + level);
            }

            @Override
            public void setConnectedVPN(String uuid) {

            }
        });
        LogUtils.e("OpenVPNUtils构造", "----------" + server.toString() + "----------");
        VpnStatus.initLogCache(context.getCacheDir());
    }

    private static final class InstanceHolder {
        //单例
        static final OpenVPNUtils instance = new OpenVPNUtils();
    }

    public static void init() {
    }

    public static OpenVPNUtils getInstance() {
        return InstanceHolder.instance;
    }

    /**
     * 检查vpn状态
     */
    private void checkService() {
        status = OpenVPNService.getStatus();
    }

    public void setStatus(String status) {
        if (status == null || "null".equals(status)) return;
        this.status = status;
        switch (status) {
            case "CONNECTING":
                LogUtils.e("CONNECTING", "---------CONNECTING--------");
                break;
            case "WAIT":
                LogUtils.e("WAIT", "---------WAIT--------");
                for (OnVpnListener vpnListener : vpnListeners) {
                    vpnListener.onVpnConnecting();
                }
                break;
            case "AUTH":
                LogUtils.e("AUTH", "---------AUTH--------");
                break;
            case "CONNECTED":
                //标记启动
                isStart = true;
                //通知启动
                for (OnVpnListener vpnListener : vpnListeners) {
                    vpnListener.onVpnStarted();
                }
                LogUtils.e("CONNECTED", "---------CONNECTED--------");
                break;
            case "RECONNECTING":
                LogUtils.e("RECONNECTING", "---------RECONNECTING--------");
                break;
            case "DISCONNECTED":
                //标记
//                isStart = false;
//                OpenVPNService.setDefaultStatus();
//                //通知
//                for (OnVpnListener vpnListener : vpnListeners) {
//                    vpnListener.onVpnDisconnected();
//                }
                LogUtils.e("DISCONNECTED", "---------DISCONNECTED--------");
//                break;
            case "EXITING":
                //标记
//                isStart = false;
//                OpenVPNService.setDefaultStatus();
//                //通知
//                for (OnVpnListener vpnListener : vpnListeners) {
//                    vpnListener.onVpnDisconnected();
//                }
                LogUtils.e("EXITING", "---------EXITING--------");
            case "NOPROCESS":
                restRunningTime();
                //标记
                isStart = false;
                OpenVPNService.setDefaultStatus();
                //通知
                for (OnVpnListener vpnListener : vpnListeners) {
                    vpnListener.onVpnDisconnected();
                }
                break;
            case "RESOLVE":
        }
    }

    /**
     * 开启vpn
     */
    public void startVpn() {
        //需要重置服务请调用restServerAndStart
        LogUtils.e("startVpn", "---------startVpn开始-------isStart?-----" + (isStart));

        try {
            //加载端口文件
            String[] address = server.getServer().get(0).getServer_address().split("/");
            String fileName = address[address.length - 1];
            String filePath = App.dir + File.separator + fileName;
            String config = FileUtils.readFile2String(filePath, "utf-8");

            //启动vpn
            OpenVpnApi.startVpn(context, config, server.getCountry(), "", server.getServer().get(0).getServer_key());

        } catch (RemoteException e) {
            e.printStackTrace();
        }

        LogUtils.e("startVpn", "---------startVpn结束------");

    }

    /**
     * 停止vpn
     *
     * @return 是否停止
     */
    public boolean stopVpn() {
        LogUtils.e("stopVpn", "---------stopVpn-------");
        try {
            //停止
            OpenVPNThread.stop();
            OpenVPNService.setDefaultStatus();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public void registerVpnListener(OnVpnListener vpnListener) {
        vpnListeners.add(vpnListener);
    }

    public void unRegisterVpnListener(OnVpnListener vpnListener) {
        vpnListeners.remove(vpnListener);
    }

    public ServerBean getServer() {
        return server;
    }

    public void setServer(ServerBean server) {
        this.server = server;
    }

    /**
     * 设置服务区域（重置服务区域（服务更改将会直接开启cpn
     *
     * @param server 服务区域bean
     */
    public void setServerAndStart(ServerBean server) {
        this.server = server;
        //若服务已启动，服务停止
        if (isStart) {
            stopVpn();
        }
        spUtils.saveServer(server);
        startVpn();
        LogUtils.e("setServerAndStart", "---------重置服务>" + server.getCountry());
    }

    public void setLastRunningTime(int lastRunningTime) {
        this.lastRunningTime = lastRunningTime;
    }

    public void restRunningTime(){
        lastRunningTime = spUtils.getInt(SPUtils.RUNNING_TIME,MAX_TIME);
    }
    public boolean isStart() {
        return isStart;
    }

    public boolean isNewDay() {
        return isNewDay;
    }

    public int getLastRunningTime() {
        return lastRunningTime;
    }

    public static int getMaxTime() {
        return MAX_TIME;
    }

    public void setRemainingTime(String remainingTime) {
        this.remainingTime = remainingTime;
    }

    public String getRemainingTime() {
        return remainingTime;
    }

    public String getStatus() {
        return status;
    }

    public boolean isWaringTime(){

        return TimeUtils.convertStringTimeToLong(remainingTime) <  TimeUtils.hour/2;
    }

    public boolean isTimeOver(){
        return TimeUtils.convertStringTimeToLong(remainingTime) <= 0;
    }

    public void test(int maxTime,int runTime) {
        if (runTime != 0) {
            lastRunningTime = runTime * TimeUtils.min;
        }
        if (maxTime != 0) {
            MAX_TIME = maxTime * TimeUtils.min;
        }
        spUtils.putInt(SPUtils.RUNNING_TIME, lastRunningTime);
    }

    public interface OnVpnListener {

        /**
         * 连接当中
         */
        void onVpnConnecting();

        /**
         * vpn开启将会回调此方法
         */
        void onVpnStarted();

        /**
         * vpn断开连接回调
         */
        void onVpnDisconnected();

    }

    public interface OnVpnStatusListener {
        /**
         * vpn开启后服务发送广播回调
         *
         * @param duration          此次vpn开启时间 00：00：00
         * @param lastPacketReceive ？
         * @param byteIn            输入-输出
         * @param byteOut           输入-输出
         */
        void changed(String duration, String lastPacketReceive, String byteIn, String byteOut);
    }

}
