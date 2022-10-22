package com.yw.unlimitedproxy.vpn;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.yw.unlimitedproxy.App;
import com.yw.unlimitedproxy.listener.VPNStatusListener;
import com.yw.unlimitedproxy.utils.LogUtils;
import com.yw.unlimitedproxy.utils.OpenVPNUtils;
import com.yw.unlimitedproxy.utils.SPUtils;
import com.yw.unlimitedproxy.utils.TimeUtils;

public class VPNStatusReceiver extends BroadcastReceiver {

    private static final String TAG = VPNStatusReceiver.class.getSimpleName();
    private VPNStatusListener listener;

    public VPNStatusReceiver(VPNStatusListener vpnStatusListener) {
        this.listener = vpnStatusListener;
    }

//    private int allRunTime = OpenVPNUtils.getInstance().getRunningTime();
    private String duration = "00:00:00";
//    private String remainingTime = OpenVPNUtils.getInstance().getRemainingTime();

    @Override
    public void onReceive(Context context, Intent intent) {

        String connectionState = intent.getStringExtra("state");
        ;
        String duration = intent.getStringExtra("duration");
        String lastPacketReceive = intent.getStringExtra("lastPacketReceive");
        String byteIn = intent.getStringExtra("byteIn");
        String byteOut = intent.getStringExtra("byteOut");

        LogUtils.e(TAG, "========start========= ");
        LogUtils.e(TAG, "connectionState = " + connectionState);
        LogUtils.e(TAG, "duration = " + duration);
        LogUtils.e(TAG, "lastPacketReceive = " + lastPacketReceive);
        LogUtils.e(TAG, "byteIn = " + byteIn);
        LogUtils.e(TAG, "byteOut = " + byteOut);
        LogUtils.e(TAG, "========end=================== ");


        //更新连接状态
//        VPNUtils.getInstance().setStatus(connectionState);
//        OpenVPNUtils.getInstance().setStatus(connectionState);
//        LogUtils.e(TAG,  !OpenVPNUtils.getInstance().isStart()+"");
//        if (connectionState !=null && connectionState.equals("DISCONNECTED")){
//            Log.e("change","拦截广播分发");
//            return;
//        }

        try {
            if (duration == null) {
                duration = VPNStatusReceiver.this.duration;
            }else {
                VPNStatusReceiver.this.duration = duration;
            }
            if (connectionState == null) connectionState = " ";
            if (lastPacketReceive == null) lastPacketReceive = "0";
            if (byteIn == null) byteIn = " ";
            if (byteOut == null) byteOut = " ";

            if (connectionState.equals("CONNECTED")) {
                VPNStatusReceiver.this.duration = "00:00:00";
            }
                Log.e("duration", duration);

            //计算所有运行时间
            int allRunTime = TimeUtils.culAllRunTime(OpenVPNUtils.getInstance().getLastRunningTime(), duration);
            saveRunningTime(allRunTime);

            LogUtils.e("全局运行时间：", allRunTime);
            LogUtils.e("全局此次时间：", VPNStatusReceiver.this.duration);
            LogUtils.e("此次运行时间：", duration);

            //计算剩余时间
            String remainingTime = TimeUtils.convertRunningTimeToRemainingTime(allRunTime, VPNUtils.MAX_TIME);
            OpenVPNUtils.getInstance().setRemainingTime(remainingTime);
            //广播回调到页面
            if (listener != null)
                listener.changed(connectionState, remainingTime, lastPacketReceive, byteIn, byteOut);

            if (allRunTime-OpenVPNUtils.MAX_TIME >= 0){
                OpenVPNUtils.getInstance().stopVpn();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void saveRunningTime(int allRunTime) {
        App.spUtils.putInt(SPUtils.RUNNING_TIME, allRunTime);
        LogUtils.e("最终缓存",""+ TimeUtils.convertRunningTimeToRemainingTime(allRunTime, VPNUtils.MAX_TIME));
    }

//    public int getAllRunTime() {
//        return allRunTime;
//    }
//
//    public String getRemainingTime() {
//        return remainingTime;
//    }

}
