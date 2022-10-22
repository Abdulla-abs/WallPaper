package com.yw.unlimitedproxy.ui.base;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gyf.immersionbar.ImmersionBar;
import com.yw.unlimitedproxy.App;
import com.yw.unlimitedproxy.R;
import com.yw.unlimitedproxy.model.ServerBean;
import com.yw.unlimitedproxy.ui.HomeActivity;
import com.yw.unlimitedproxy.utils.ActivityUtils;
import com.yw.unlimitedproxy.utils.DensityUtil;
import com.yw.unlimitedproxy.utils.DownloadUtil;
import com.yw.unlimitedproxy.utils.FileUtils;
import com.yw.unlimitedproxy.utils.JsonUtils;
import com.yw.unlimitedproxy.utils.LogUtils;
import com.yw.unlimitedproxy.utils.OpenVPNUtils;
import com.yw.unlimitedproxy.utils.PermissionUtils;
import com.yw.unlimitedproxy.utils.Utils;

import java.io.File;
import java.util.List;

public abstract class BaseActivity extends AppCompatActivity {

    protected final String TAG = this.getClass().getSimpleName();
    protected final int PERMISSION_REQUEST_CODE = 0x0054; //权限请求码
    protected final int OPEN_VPN_REQUEST_CODE = 0x5154; //开启vpn请求码

    //handler
    protected final int CONNECTION_CONNECTING = 0x0004;
    protected final int CONNECTION_SUCCESS = 0x0005;  //连接成功
    protected final int CONNECTION_FAILURE = 0x0006;  //连接失败
    protected final int CONNECTION_OUT = 0x0007;  //连接断开
    protected final int FILE_EXISTS = 0x1297;
    protected final int DOWNLOAD_SUCCESS = 0X015;
    protected final int DOWNLOADING = 0x0564;
    protected final int DOWNLOAD_FAILURE = 0x0545;
    protected final int PERMISSION_STORAGE_DENIED = 0x5152;

    protected static List<ServerBean> allBeans; //服务列表数据

//    protected AlertDialog connectingDialog;   //连接弹窗

    //读写权限
    protected final String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};

//    protected ProgressDialog loadingDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(getLayoutId());

        if (enableImmersion()) {
            ImmersionBar.with(this)
                    .statusBarColor(android.R.color.transparent)
                    .statusBarView(R.id.status_bar_dimen)
                    .init();
        }

        init();
        initView();
    }

    protected abstract int getLayoutId();

    protected abstract void init();

    protected abstract void initView();

    /**
     * 是否开启沉浸式，子类重写此方法选择不开启沉浸式
     * @return 默认true
     */
    protected boolean enableImmersion() {
        return true;
    }

    /**
     * 显示连接弹窗
     */
//    protected void showConnectionDialog() {
//        if (connectingDialog == null) {
//            connectingDialog = new AlertDialog.Builder(this)
//                    .setView(R.layout.dialog_connecting)
//                    .setCancelable(false)
//                    .create();
//        }
//        /**
//         * E/AndroidRuntime: FATAL EXCEPTION: main
//         *     Process: com.yw.unlimitedproxy, PID: 28696
//         *     android.view.WindowManager$BadTokenException: Unable to add window -- token android.os.BinderProxy@fe13c1e is not valid; is your activity running?
//         *         at android.view.ViewRootImpl.setView(ViewRootImpl.java:1195)
//         *         at android.view.WindowManagerGlobal.addView(WindowManagerGlobal.java:475)
//         *         at android.view.WindowManagerImpl.addView(WindowManagerImpl.java:130)
//         *         at android.app.Dialog.show(Dialog.java:578)
//         *         at com.yw.unlimitedproxy.ui.HomeActivity.showConnectionDialog(HomeActivity.java:267)
//         */
//        connectingDialog.show();
//        connectingDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
//    }

//    protected void showLoadingDialog(){
//        if (loadingDialog == null){
//            loadingDialog = new ProgressDialog(this);
//            loadingDialog.setCancelable(false);
//            loadingDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//            loadingDialog.setTitle("Loading");
//            loadingDialog.setMessage("Downloading configuration file...");
//            loadingDialog.setMax(100);
//        }
//        loadingDialog.show();
//    }

    @Override
    protected void onStart() {
        super.onStart();
        LogUtils.e(getPackageName(), "onStart");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 跳转至 ?.class
     * @param clazz class
     */
    protected void migrateTo(Class<?> clazz) {
        startActivity(new Intent(this, clazz));
    }

    /**
     * 短时toast
     * @param tip 展示的text
     */
    protected void showToast(String tip) {
        Toast.makeText(this, tip, Toast.LENGTH_SHORT).show();
    }

//    protected void showTimeWarmingDialog(){
//        View view = LayoutInflater.from(this).inflate(R.layout.dialog_warming, null, false);
//        Button yes, no;
//        yes = view.findViewById(R.id.yes_bt);
//        no = view.findViewById(R.id.no_bt);
//        AlertDialog dialog = new AlertDialog.Builder(this)
//                .setView(view)
//                .create();
//        no.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
//        yes.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
//        dialog.show();
//        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
//    }

//    protected void showTimeOverDialog() {
//        View view = LayoutInflater.from(this).inflate(R.layout.dialog_time_over, null, false);
//        Button yes, no;
//        yes = view.findViewById(R.id.yes_bt);
//        no = view.findViewById(R.id.no_bt);
//        AlertDialog dialog = new AlertDialog.Builder(this)
//                .setView(view)
//                .create();
//        no.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
//        yes.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
//        dialog.show();
//        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
//    }

    /**
     * 申请存储权限（仅申请
     * @param callBack 权限申请回调
     */
    protected void requestWRStoragePermission(OnPermissionCallBack callBack){
        //无权限
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
            requestPermissions(permissions,PERMISSION_REQUEST_CODE);
        }else {
            //有权限
            callBack.onComplete();
        }
    }

    protected void downloadOvpnConfigFile(String fileUrl,String fileName,OnCheckOrDownLoadCallBack callBack) {
        //下载文件
        DownloadUtil.get().download(
                fileUrl,//下载文件地址
                App.dir,//存储路径
                fileName,//文件名
                new DownloadUtil.OnDownloadListener() {//下载回调
                    @Override
                    public void onDownloadSuccess(File file) {//下载完成
                        if(callBack!=null)
                            callBack.onFileLoadingSusses(file);

                    }

                    @Override
                    public void onDownloading(int progress) {//下载中
                        if(callBack!=null)
                            callBack.onFileInLoading(progress);
                    }

                    @Override
                    public void onDownloadFailed(Exception e) {//下载失败
                        if(callBack!=null)
                            callBack.onFileLoadingFailure(e);
                    }
                });
    }

    /**
     * 左上角返回键统一管理，重写此方法请记得访问父类方法 | super.onOptionsItemSelected
     * @param item item
     * @return ?
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setDialogWidthAndHeight(Dialog dialog, int widthDp, int heightDp) {
        //WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        //DisplayMetrics dm = new DisplayMetrics();
        //wm.getDefaultDisplay().getMetrics(dm);
        //int densityDpi = dm.densityDDensityUtilpi;//屏幕密度dpi（120 / 160 / 240）
        dialog.getWindow().setLayout(DensityUtil.dip2px(Utils.getContext(), widthDp), DensityUtil.dip2px(Utils.getContext(), heightDp));
    }



    //统一处理开启vpn权限
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OPEN_VPN_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                LogUtils.e("onActivityResult", "------------准备开始vpn------------");
            } else {
                showToast("Denying permission will prevent you from using the software!");
            }
        }
    }

    public interface OnPermissionCallBack {
        void onComplete();
    }

    public interface OnCheckOrDownLoadCallBack{

        void onFileInLoading(int progress);

        void onFileLoadingSusses(File file);

        void onFileLoadingFailure(Exception e);
    }

}
