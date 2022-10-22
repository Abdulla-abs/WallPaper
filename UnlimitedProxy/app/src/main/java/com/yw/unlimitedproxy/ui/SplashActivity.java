package com.yw.unlimitedproxy.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.yw.unlimitedproxy.R;
import com.yw.unlimitedproxy.listener.DialogBtnListener;
import com.yw.unlimitedproxy.model.DialogManager;
import com.yw.unlimitedproxy.model.ServerBean;
import com.yw.unlimitedproxy.model.VPNDataManager;
import com.yw.unlimitedproxy.ui.base.BaseActivity;
import com.yw.unlimitedproxy.utils.Constants;
import com.yw.unlimitedproxy.utils.OpenVPNUtils;
import com.yw.unlimitedproxy.utils.PermissionUtils;
import com.yw.unlimitedproxy.utils.SPUtils;

import java.io.File;

/**
 * 加载界面
 * 初始化服务列表
 * 申请存储权限
 * 第一次下载配置文件
 */
public class SplashActivity extends BaseActivity /*implements BaseActivity.OnCheckOrDownLoadCallBack*/ {

    private static final int UPDATE_PB = 0x2055;
    //加载进度条完毕
    private final int LOADED_COMPLETE = 0x2056;
    private ImageView splashIm;
    private LinearLayout temp1ContainerLl;
    private TextView textView;
    private ImageView imageView;
    private TextView temp2Tv;
    private ProgressBar pb;
    private TextView progressTv;
    //解决重复下载和更新pb的问题
    boolean hasInit = false;

    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case LOADED_COMPLETE:    //加载完毕调用
                    pb.setProgress(100);
                    progressTv.setText("100%");
                    //判断是否同意隐私条款
                    SPUtils spUtils = new SPUtils();
                    if (spUtils.getBoolean(Constants.SP_KEY_ACCESS_PROTOCOL, false)) {
                        //已同意 主页
                        migrateTo(HomeActivity.class);
                    } else {
                        //未同意
                        migrateTo(TermsActivity.class);
                    }
                    SplashActivity.this.finish();
                    break;
                case UPDATE_PB:
                    pb.setProgress(pb.getProgress()+20);
                    progressTv.setText(pb.getProgress()+"%");
                    break;
            }
        }
    };

    @Override
    protected int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void init() {
        splashIm = (ImageView) findViewById(R.id.splash_im);
        temp1ContainerLl = (LinearLayout) findViewById(R.id.temp1_container_ll);
        textView = (TextView) findViewById(R.id.textView);
        imageView = (ImageView) findViewById(R.id.imageView);
        temp2Tv = (TextView) findViewById(R.id.temp2_tv);
        pb = (ProgressBar) findViewById(R.id.pb);
        progressTv = (TextView) findViewById(R.id.progress_tv);

    }


    @Override
    protected void initView() {
        //权限申请
        requestWRStoragePermission(new OnPermissionCallBack() {
            @Override
            public void onComplete() {

                hasInit = true;
                //5秒钟跳转
                updatePB();
                ServerBean.Server server = OpenVPNUtils.getInstance().getServer().getServer().get(0);
                //当前服务没有找到配置文件
                if (!server.isAvailable()) {
                    startDownConfigFile();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            if(!hasInit){
                startDownConfigFile();
                updatePB();
            }
        }
    }

    private void updatePB() {
        if(pb.getProgress()==0){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (pb.getProgress()<100){
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        handler.sendEmptyMessage(UPDATE_PB);
                    }
                    handler.sendEmptyMessage(LOADED_COMPLETE);
                }
            }).start();
        }

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
                    showToast("Storage permissions have been denied, please open it yourself");
                    AlertDialog dialog = DialogManager.showPermissionIntroduceDialog(SplashActivity.this, new DialogBtnListener() {
                        @Override
                        public void confirmClick() {
//                            requestPermissions(permissions, PERMISSION_REQUEST_CODE);
                            PermissionUtils.toAppSetting(SplashActivity.this);
                        }
                    });
                    dialog.show();
                } else {
                    //5秒钟跳转
                    updatePB();
                    startDownConfigFile();
                }
                break;
            default:
                break;
        }
    }

    private void startDownConfigFile() {
        ServerBean.Server server = OpenVPNUtils.getInstance().getServer().getServer().get(0);
        if (!server.isAvailable()){
            downloadOvpnConfigFile(server.getServer_address(),
                    server.getOvpnFileName(),
                    null);
        }
    }



    /*@Override
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
    }*/
}