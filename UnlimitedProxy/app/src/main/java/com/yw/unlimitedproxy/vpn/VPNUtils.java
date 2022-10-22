package com.yw.unlimitedproxy.vpn;

import android.content.Context;
import android.content.Intent;

import com.yw.unlimitedproxy.App;
import com.yw.unlimitedproxy.model.ServerBean;
import com.yw.unlimitedproxy.utils.FileUtils;
import com.yw.unlimitedproxy.utils.LogUtils;
import com.yw.unlimitedproxy.utils.NetworkUtils;
import com.yw.unlimitedproxy.utils.OpenVPNUtils;
import com.yw.unlimitedproxy.utils.SPUtils;
import com.yw.unlimitedproxy.utils.TimeUtils;
import com.yw.unlimitedproxy.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.blinkt.openvpn.OpenVpnApi;
import de.blinkt.openvpn.core.ConnectionStatus;
import de.blinkt.openvpn.core.OpenVPNService;
import de.blinkt.openvpn.core.OpenVPNThread;
import de.blinkt.openvpn.core.VpnStatus;

public class VPNUtils {

    private static final String TAG = VPNUtils.class.getSimpleName();
    private Context context;
    //服务线程
    private OpenVPNThread vpnThread = new OpenVPNThread();
    private OpenVPNService vpnService = new OpenVPNService();
    private SPUtils spUtils = new SPUtils();

    public int lastRunningTime = 0;
    //修改上限时间？ 修改此值
    public static int MAX_TIME = TimeUtils.hour * 2;

    //当前连接服务
    private ServerBean server;

    private boolean isNewDay = false;
    private boolean isWaringTime = false;    //是否是警告时间
    private boolean isTimeOver = false;  //是否是结束时间
    private int runningTime = 0;
    private String remainingTime = TimeUtils.convertIntTimeToStringTime(MAX_TIME);

    //状态监听
//    private final List<OpenVPNUtils.OnVpnListener> vpnListeners = new ArrayList<>();

    //是否启动
    private boolean isStart = false;

    //vpn状态
    private String status = "";

    public void setServer(ServerBean server) {
        this.server = server;
    }

    public ServerBean getServer() {
        return server;
    }

    public boolean isStart() {
        return isStart;
    }

    private static class SingletonInstance{
        private static final VPNUtils INSTANCE = new VPNUtils();
    }

    public static VPNUtils getInstance(){
        return SingletonInstance.INSTANCE;
    }

    private VPNUtils(){
        context = Utils.getContext();
        server = spUtils.getServer();
        //是否新的一天
        if (spUtils.isNewDay()) {
            isNewDay = true;
            spUtils.putString(SPUtils.NEW_DAY, TimeUtils.ymd.format(new Date()));
            spUtils.putInt(SPUtils.RUNNING_TIME, runningTime);
            spUtils.putBoolean(SPUtils.WARMING_TIME, false);
            spUtils.putBoolean(SPUtils.OVER_TIME, false);
        } else {
            runningTime = spUtils.getInt(SPUtils.RUNNING_TIME,MAX_TIME);
            isWaringTime = MAX_TIME - runningTime < 30 * TimeUtils.min;
            isTimeOver = runningTime - MAX_TIME >= 0;
            isWaringTime = spUtils.getBoolean(SPUtils.WARMING_TIME, true);
            isTimeOver = spUtils.getBoolean(SPUtils.OVER_TIME, true);
            remainingTime = TimeUtils.convertRunningTimeToRemainingTime(
                    runningTime,
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

    /**
     * 开启vpn
     */
    public void startVpn(VpnConnectListener vpnConnectListener) {

        try {
            //加载端口文件
            String[] address = server.getServer().get(0).getServer_address().split("/");
            String fileName = address[address.length-1];
            String filePath = App.dir + File.separator + fileName;
            String config = FileUtils.readFile2String(filePath, "utf-8");//TODO 这里如果没读到怎么办？

            if(config != null && config.length()>0){
                //启动vpn
                OpenVpnApi.startVpn(context, config, server.getCountry(), "", server.getServer().get(0).getServer_key());
                //标记启动
                isStart = true;
                //通知启动
                if(vpnConnectListener != null)
                    vpnConnectListener.onSuccess();

            }else{
                if(vpnConnectListener != null)
                    vpnConnectListener.onFailed();
            }
        } catch (Exception e) {

            e.printStackTrace();
            if(vpnConnectListener != null)
                vpnConnectListener.onFailed();
        }

    }

    /**
     * 停止vpn
     * @return 是否停止
     */
    public boolean stopVpn() {
        try {
            //停止
            OpenVPNThread.stop();
            OpenVPNService.setDefaultStatus();

            isStart = false;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        LogUtils.e("stopVpn", "---------stopVpn--------");

        return false;
    }

    /**
     * Status change with corresponding vpn connection status
     * @param connectionState
     */
    public void setStatus(String connectionState) {
        LogUtils.e(TAG, "connectionState = " + connectionState);
        if (connectionState!= null)
            switch (connectionState) {
                case "DISCONNECTED":
                    //status("connect");
                    isStart = false;
                    vpnService.setDefaultStatus();

                    break;
                case "CONNECTED":
                    isStart = true;// it will use after restart this activity
                    lastRunningTime = App.spUtils.getInt(SPUtils.RUNNING_TIME, 0);

                    //保存服务
                    App.spUtils.saveServer(OpenVPNUtils.getInstance().getServer());
                    LogUtils.e("onStop", "----------保存server----------");
                    break;
                case "WAIT":
                    //binding.logTv.setText("waiting for server connection!!");
                    break;
                case "AUTH":
                    //binding.logTv.setText("server authenticating!!");
                    break;
                case "RECONNECTING":
                    //status("connecting");
                    //binding.logTv.setText("Reconnecting...");
                    break;
                case "NONETWORK":
                    //binding.logTv.setText("No network connection");
                    break;
            }
    }

    public String getRemainingTime(){
        int lastRunningTime = spUtils.getInt(SPUtils.RUNNING_TIME, 0);
        return TimeUtils.convertRunningTimeToRemainingTime(lastRunningTime, VPNUtils.MAX_TIME);
    }

    public static boolean hasNetWork(){
        return NetworkUtils.isConnected() && NetworkUtils.isAvailableByPing();
    }

    public interface VpnConnectListener {

        void onSuccess();

        void onFailed();
    }
}
