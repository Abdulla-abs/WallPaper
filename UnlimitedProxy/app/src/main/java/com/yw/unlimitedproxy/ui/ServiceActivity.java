package com.yw.unlimitedproxy.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.VpnService;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yw.unlimitedproxy.App;
import com.yw.unlimitedproxy.R;
import com.yw.unlimitedproxy.adapter.ServerBeanAdapter;
import com.yw.unlimitedproxy.adapter.ServiceAdapter;
import com.yw.unlimitedproxy.adapter.StateAdapter;
import com.yw.unlimitedproxy.listener.DialogBtnListener;
import com.yw.unlimitedproxy.model.DialogManager;
import com.yw.unlimitedproxy.model.Server;
import com.yw.unlimitedproxy.model.ServerBean;
import com.yw.unlimitedproxy.model.VPNDataManager;
import com.yw.unlimitedproxy.ui.base.BaseActivity;
import com.yw.unlimitedproxy.utils.DownloadUtil;
import com.yw.unlimitedproxy.utils.FileUtils;
import com.yw.unlimitedproxy.utils.JsonUtils;
import com.yw.unlimitedproxy.utils.LogUtils;
import com.yw.unlimitedproxy.utils.OpenVPNUtils;
import com.yw.unlimitedproxy.utils.PermissionUtils;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * 服务界面
 * 显示所有vpn地域，ListView单选
 */
public class ServiceActivity extends BaseActivity implements OpenVPNUtils.OnVpnListener, BaseActivity.OnCheckOrDownLoadCallBack {

    private View statusBarDimen;
    private Toolbar tool;
    private TextView titleTv;
    private TextView allTv;
    private ListView fastList;
    private ListView allList;

    private ServerBean selectBean = OpenVPNUtils.getInstance().getServer();
    private ServerBean clickBean = selectBean;

    private ServerBeanAdapter fastAdapter;
    private ServerBeanAdapter allAdapter;
    private List<ServerBean> fastBeans;

    private AlertDialog connectDialog;
    private AlertDialog changeServiceDialog;
    private ProgressDialog loadingDialog;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_state;
    }

    @Override
    protected void init() {
        statusBarDimen = (View) findViewById(R.id.status_bar_dimen);
        tool = (Toolbar) findViewById(R.id.tool);
        titleTv = (TextView) findViewById(R.id.title_tv);
        fastList = (ListView) findViewById(R.id.fast_list);
        allList = (ListView) findViewById(R.id.all_list);
        allTv = findViewById(R.id.all_tv);

        titleTv.setText("State");

        setSupportActionBar(tool);
        getSupportActionBar().setTitle("");
    }

    @Override
    protected void initView() {
        fastBeans = VPNDataManager.getInstance().getFastBeans();
        allBeans = VPNDataManager.getInstance().getAllBeans();

        resetListData();
        allTv.setText("All(" + allBeans.size() + ")");
    }

    private void showChangeDialog(int position) {
        if (clickBean.getCountry().equals(OpenVPNUtils.getInstance().getServer().getCountry())) return;
        if (changeServiceDialog == null) {
            changeServiceDialog = DialogManager.showChangeDialog(ServiceActivity.this,
                    new DialogBtnListener() {
                        @Override
                        public void confirmClick() {
                            requestWRStoragePermission(new OnPermissionCallBack() {
                                @Override
                                public void onComplete() {
                                    checkDownLoadFile();
                                }
                            });
                        }
                    });
            changeServiceDialog.show();
        }
        changeServiceDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        OpenVPNUtils.getInstance().registerVpnListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        OpenVPNUtils.getInstance().unRegisterVpnListener(this);
    }

    private void resetListData() {
        LogUtils.e("resetListData------->", selectBean.getCountry() + " " + clickBean.getCountry() + " " + OpenVPNUtils.getInstance().getServer().getCountry());
        selectBean = OpenVPNUtils.getInstance().getServer();
        clickBean = OpenVPNUtils.getInstance().getServer();
        fastAdapter = new ServerBeanAdapter(fastBeans);
        fastList.setAdapter(fastAdapter);

        fastAdapter.setOnItemClickListener(new ServerBeanAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(int position, ViewGroup parent) {
                clickBean = fastBeans.get(position);
                showChangeDialog(position);
            }
        });

        String country = OpenVPNUtils.getInstance().getServer().getCountry();
        long count = fastBeans.stream()
                .filter((fastBean) -> fastBean.getCountry().equals(country))
                .count();
        if (count > 0) {
            allAdapter = new ServerBeanAdapter(allBeans, false);
        } else {
            allAdapter = new ServerBeanAdapter(allBeans);
        }
        allList.setAdapter(allAdapter);
        allAdapter.setOnItemClickListener(new ServerBeanAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(int position, ViewGroup parent) {
                clickBean = allBeans.get(position);
                showChangeDialog(position);
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean permissionDenied = true;
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        permissionDenied = true;
                    } else {
                        permissionDenied = false;
                    }
                }
                if (permissionDenied) {
                    changeServiceDialog.dismiss();
                    showToast("Storage permissions have been denied, please open it yourself");
                    handler.sendEmptyMessageDelayed(PERMISSION_STORAGE_DENIED, 1500);
                } else {
                    checkDownLoadFile();
                }
                break;
            default:
                break;
        }
    }


    private void checkDownLoadFile() {
        ServerBean.Server clickServer = clickBean.getServer().get(0);
        if (clickServer.isAvailable()) {

            //第一次开启权限
            Intent intent = VpnService.prepare(getApplicationContext());
            if (intent != null) {
                startActivityForResult(intent, OPEN_VPN_REQUEST_CODE);
                return;
            }
            if (OpenVPNUtils.getInstance().isStart()) {
                OpenVPNUtils.getInstance().setServerAndStart(clickBean);
                LogUtils.e("关闭vpn");
            }else {
                if (connectDialog == null) {
                    connectDialog = DialogManager.showVPNConnectDialog(this);
                }
                connectDialog.show();
                LogUtils.e("显示连接弹窗");
                OpenVPNUtils.getInstance().setServer(clickBean);
                OpenVPNUtils.getInstance().startVpn();
            }

        } else {
            if (loadingDialog == null) {
                loadingDialog = DialogManager.showDownLoadDialog(this);
            }
            loadingDialog.show();
            downloadOvpnConfigFile(clickServer.getServer_address(),
                    clickServer.getOvpnFileName(), ServiceActivity.this);
        }
    }

    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case DOWNLOAD_SUCCESS:
                    showToast("Download configuration file successfully");
                    changeServiceDialog.dismiss();
                    loadingDialog.dismiss();
                    OpenVPNUtils.getInstance().setServerAndStart(clickBean);
                    break;
                case DOWNLOADING:
                    loadingDialog.setProgress((int) msg.obj);
                    break;
                case DOWNLOAD_FAILURE:
                    showToast("Download configuration file failure");
                    if (changeServiceDialog.isShowing()) {
                        changeServiceDialog.dismiss();
                    }
                    loadingDialog.dismiss();
                    break;
                case PERMISSION_STORAGE_DENIED:
                    PermissionUtils.toAppSetting(ServiceActivity.this);
                    break;
                case CONNECTION_CONNECTING:
                    if (connectDialog == null){
                        connectDialog = DialogManager.showVPNConnectDialog(ServiceActivity.this);
                    }
                    if (!connectDialog.isShowing()){
                        connectDialog.show();
                    }
                    break;
                case CONNECTION_SUCCESS:
                    showToast("VPN Start");
                    resetListData();
                    if (connectDialog != null && connectDialog.isShowing()){
                        connectDialog.dismiss();
                    }
                    break;
                case CONNECTION_FAILURE:
                    showToast("VPN Disconnected");
                    if (connectDialog != null && connectDialog.isShowing()){
                        connectDialog.dismiss();
                    }
                    break;
            }
        }
    };

    @Override
    public void onVpnConnecting() {
        handler.sendEmptyMessage(CONNECTION_CONNECTING);
    }

    @Override
    public void onVpnStarted() {
        LogUtils.e("连接成功回调");
        handler.sendEmptyMessage(CONNECTION_SUCCESS);
    }

    @Override
    public void onVpnDisconnected() {
        LogUtils.e("连接断开回调");
        handler.sendEmptyMessage(CONNECTION_FAILURE);
    }

    @Override
    public void onFileInLoading(int progress) {
        Message message = handler.obtainMessage();
        message.what = DOWNLOADING;
        message.obj = progress;
        handler.sendMessage(message);
    }

    @Override
    public void onFileLoadingSusses(File file) {
        handler.sendEmptyMessage(DOWNLOAD_SUCCESS);
    }

    @Override
    public void onFileLoadingFailure(Exception e) {
        handler.sendEmptyMessage(DOWNLOAD_FAILURE);
    }
}