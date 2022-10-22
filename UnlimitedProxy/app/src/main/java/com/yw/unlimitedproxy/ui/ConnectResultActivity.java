package com.yw.unlimitedproxy.ui;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.yw.unlimitedproxy.App;
import com.yw.unlimitedproxy.R;
import com.yw.unlimitedproxy.listener.DialogBtnListener;
import com.yw.unlimitedproxy.listener.VPNStatusListener;
import com.yw.unlimitedproxy.model.DialogManager;
import com.yw.unlimitedproxy.ui.base.BaseActivity;
import com.yw.unlimitedproxy.utils.Constants;
import com.yw.unlimitedproxy.utils.LogUtils;
import com.yw.unlimitedproxy.utils.OpenVPNUtils;
import com.yw.unlimitedproxy.vpn.VPNStatusReceiver;


/**
 * 连接结果界面
 * 分为：连接成功，显示连接成功，当前服务器区域，网络速率
 * 连接失败，显示连接失败，点击按钮重新连接
 * 连接断开，显示连接断开，当前服务器区域，网络速率
 */
public class ConnectResultActivity extends BaseActivity implements OpenVPNUtils.OnVpnListener, VPNStatusListener {

    //跳转过来的值
    public static final String KEY_CONNECTION_RESULT = "result";
    public static final int KEY_CONNECTION_SUCCESS = 1;
    public static final int KEY_CONNECTION_FAILURE = 2;
    public static final int KEY_CONNECTION_OUT = 3;

    private View statusBarDimen;
    private Toolbar tool;
    private TextView titleTv;
    private ImageView stateIm;
    private TextView stateTv;
    private LinearLayout outContainerLl;
    private LinearLayout successContainerLl;
    private LinearLayout speedContainerLl;
    private TextView sucServerTv;
    private TextView sucSpeedTv;
    private Button moreServerBt;
    private Button reconnectBt;

    //当前连接结果
    private int connectionResult;
    private VPNStatusReceiver receiver;

    private AlertDialog connectingDialog;
    private ProgressDialog loadingDialog;

    private OpenVPNUtils openVPNUtils;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_connect_result;
    }

    @Override
    protected void init() {
        statusBarDimen = (View) findViewById(R.id.status_bar_dimen);
        tool = (Toolbar) findViewById(R.id.tool);
        titleTv = (TextView) findViewById(R.id.title_tv);
        stateIm = (ImageView) findViewById(R.id.state_im);
        stateTv = (TextView) findViewById(R.id.state_tv);
        outContainerLl = (LinearLayout) findViewById(R.id.out_container_ll);
        successContainerLl = (LinearLayout) findViewById(R.id.success_container_ll);
        speedContainerLl = findViewById(R.id.speed_container_ll);
        sucServerTv = (TextView) findViewById(R.id.suc_server_tv);
        sucSpeedTv = (TextView) findViewById(R.id.suc_speed_tv);
        moreServerBt = (Button) findViewById(R.id.more_server_bt);
        reconnectBt = (Button) findViewById(R.id.reconnect_bt);

        setSupportActionBar(tool);
        getSupportActionBar().setTitle("");
        titleTv.setText(getResources().getString(R.string.state));

        openVPNUtils = OpenVPNUtils.getInstance();
        receiver = new VPNStatusReceiver(this);
        //获取跳转结果
        connectionResult = getIntent().getIntExtra(KEY_CONNECTION_RESULT, 2);
        if (connectionResult == KEY_CONNECTION_SUCCESS) {
            //连接成功
            onConnectionChange(true);
        } else if (connectionResult == KEY_CONNECTION_FAILURE) {
            //连接失败
            onConnectionFailure();
        } else if (connectionResult == KEY_CONNECTION_OUT) {
            //断开连接
            onConnectionChange(false);
        }

    }

    @Override
    protected void initView() {
        //连接成功按钮---》更多服务
        moreServerBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                migrateTo(ServiceActivity.class);
                ConnectResultActivity.this.finish();
            }
        });
        //连接失败、连接断开
        reconnectBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (connectionResult == KEY_CONNECTION_FAILURE) {
                    //连接失败
                    retryConnectVpn();
                } else if (connectionResult == KEY_CONNECTION_OUT) {
                    //断开连接
                    retryConnectVpn();
                }
            }
        });
    }

    //重新连接vpn
    private void retryConnectVpn() {
        if (connectingDialog == null) {
            connectingDialog = DialogManager.showVPNConnectDialog(this);
        }
        connectingDialog.show();
        handler.sendEmptyMessageDelayed(RETRY_CONNECT, 1500);
    }

    @Override
    protected void onResume() {
        openVPNUtils.registerVpnListener(this);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("connectionState"));
        super.onResume();
    }

    @Override
    protected void onPause() {
        LogUtils.e("onPause", "--------openVPNUtils.isStart()-----" + openVPNUtils.isStart());
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        super.onPause();
    }

    @Override
    protected void onStop() {
        LogUtils.e("onStop", "--------openVPNUtils.isStart()-----" + openVPNUtils.isStart());
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        openVPNUtils.unRegisterVpnListener(this);
        super.onDestroy();
    }

    /**
     * 连接成功或者断开连接
     */
    private void onConnectionChange(boolean isConnected) {
        //当前隐藏？
        if (successContainerLl.getVisibility() == View.GONE) {
            successContainerLl.setVisibility(View.VISIBLE);
        }
        stateIm.setImageResource(isConnected ? R.mipmap.connection_success_icon :
                R.mipmap.connection_out_icon);

        stateTv.setText(isConnected ? getResources().getString(R.string.connection_success)
                : getResources().getString(R.string.disconnection_success));
        //显示当前vpn连接点
        sucServerTv.setText(openVPNUtils.getServer().getCountry());
        //显示速率
        if (isConnected) {
            if (speedContainerLl.getVisibility() == View.GONE)
                speedContainerLl.setVisibility(View.VISIBLE);
            sucSpeedTv.setText("0kb/S");
        } else {
            speedContainerLl.setVisibility(View.GONE);
        }
        //是否是连接成功
        if (isConnected) {//连接成功
            if (reconnectBt.getVisibility() == View.VISIBLE) {
                reconnectBt.setVisibility(View.GONE);
            }
            moreServerBt.setText(getString(R.string.more_service));
            moreServerBt.setVisibility(View.VISIBLE);
        } else {//连接断开
            reconnectBt.setVisibility(View.VISIBLE);
            reconnectBt.setBackground(AppCompatResources.getDrawable(this, R.drawable.shape_e_25));
        }
    }

    /**
     * 连接失败
     */
    private void onConnectionFailure() {
        stateIm.setImageResource(R.mipmap.connection_failure_icon);
        stateTv.setText(getResources().getString(R.string.connection_failure));
        if (outContainerLl.getVisibility() == View.GONE) {
            outContainerLl.setVisibility(View.VISIBLE);
        }
        if (reconnectBt.getVisibility() == View.GONE) {
            reconnectBt.setVisibility(View.VISIBLE);
            reconnectBt.setText(getResources().getString(R.string.retry));
            reconnectBt.setBackground(AppCompatResources.getDrawable(this, R.drawable.shape_red_25));
        }
    }

    private final int RETRY_CONNECT = 0x8745;

    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case RETRY_CONNECT:
                    openVPNUtils.startVpn();
                    openVPNUtils.registerVpnListener(ConnectResultActivity.this);
                    break;
                case CONNECTION_SUCCESS:
                    onConnectionChange(true);
                    break;
            }
        }
    };

    @Override
    public void onVpnConnecting() {

    }

    @Override
    public void onVpnStarted() {
        if (connectingDialog != null && connectingDialog.isShowing()) {
            connectingDialog.dismiss();
        }
        handler.sendEmptyMessage(CONNECTION_SUCCESS);
    }

    @Override
    public void onVpnDisconnected() {

    }

    @Override
    public void changed(String connectionState, String duration, String lastPacketReceive, String byteIn, String byteOut) {
        String[] split = byteIn.split("-");
        String substring = split[0].substring(1) + "/S";
        sucSpeedTv.setText(substring);

        if (openVPNUtils.isWaringTime()) {

            //距离上次弹框超过10分钟，则再弹一次
            if(System.currentTimeMillis() - App.spUtils.getLong(Constants.SP_WARING_TIME, 0) > 10*60*1000){
                DialogManager.showTimeWaringDialog(this, new DialogBtnListener() {
                    @Override
                    public void confirmClick() {

                    }
                });
                //记录弹框时间
                App.spUtils.putLong(Constants.SP_WARING_TIME, System.currentTimeMillis());
            }
        }
        if (openVPNUtils.isTimeOver()) {
            DialogManager.showTimeOverDialog(this, new DialogBtnListener() {
                @Override
                public void confirmClick() {

                }
            });
        }
    }

}