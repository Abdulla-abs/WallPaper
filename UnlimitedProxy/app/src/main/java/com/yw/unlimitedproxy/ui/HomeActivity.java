package com.yw.unlimitedproxy.ui;

import static com.yw.unlimitedproxy.utils.OpenVPNUtils.MAX_TIME;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.VpnService;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yw.unlimitedproxy.App;
import com.yw.unlimitedproxy.R;
import com.yw.unlimitedproxy.adapter.NaviAdapter;
import com.yw.unlimitedproxy.listener.DialogBtnListener;
import com.yw.unlimitedproxy.listener.VPNStatusListener;
import com.yw.unlimitedproxy.model.DialogManager;
import com.yw.unlimitedproxy.model.ServerBean;
import com.yw.unlimitedproxy.ui.base.BaseActivity;
import com.yw.unlimitedproxy.utils.Constants;
import com.yw.unlimitedproxy.utils.LogUtils;
import com.yw.unlimitedproxy.utils.MultiLanguage;
import com.yw.unlimitedproxy.utils.NetworkUtils;
import com.yw.unlimitedproxy.utils.OpenVPNUtils;
import com.yw.unlimitedproxy.utils.SPUtils;
import com.yw.unlimitedproxy.utils.TimeUtils;
import com.yw.unlimitedproxy.view.CountDownTextView;
import com.yw.unlimitedproxy.view.SwitchView;
import com.yw.unlimitedproxy.view.ToolBar;
import com.yw.unlimitedproxy.vpn.VPNStatusReceiver;

import java.io.File;
import java.util.Arrays;

/**
 * ??????
 * <p>
 * ????????????????????????ListView????????????ToolBar?????????
 */
public class HomeActivity extends BaseActivity implements View.OnClickListener, OpenVPNUtils.OnVpnListener,
        BaseActivity.OnCheckOrDownLoadCallBack, VPNStatusListener {

    private DrawerLayout drawer;
    private View statusBarDimen;
    private ToolBar tool;
    private ListView navi;
    private ImageButton back;
    private RelativeLayout serverContainerRl;
    private ImageView flagIm;
    private CountDownTextView runningTimeTv;
    private ImageView successRipperIm;
    private SwitchView toggleSv;
    private TextView addTimeTv;
    private TextView serverNameTv;

    private long onShareClickTime = 0;
    private final SPUtils spUtils = new SPUtils();
    private final OpenVPNUtils openVPNUtils = OpenVPNUtils.getInstance();
    private VPNStatusReceiver receiver;
    private AlertDialog connectingDialog;
    private ProgressDialog loadFileDialog;


    private void setTimeColor() {
        if (openVPNUtils.isWaringTime()) {
            runningTimeTv.setTextColor(getColor(R.color.yellow));
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("connectionState"));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_home;
    }

    @Override
    protected void init() {
        drawer = (DrawerLayout) findViewById(R.id.drawer);
        statusBarDimen = (View) findViewById(R.id.status_bar_dimen);
        tool = (ToolBar) findViewById(R.id.tool);
        navi = findViewById(R.id.navi);
        serverContainerRl = (RelativeLayout) findViewById(R.id.server_container_rl);
        successRipperIm = (ImageView) findViewById(R.id.success_ripper_im);
        toggleSv = (SwitchView) findViewById(R.id.toggle_sv);
        flagIm = findViewById(R.id.flag_im);
        runningTimeTv = (CountDownTextView) findViewById(R.id.running_time_tv);
        addTimeTv = findViewById(R.id.add_time_tv);
        serverNameTv = findViewById(R.id.server_name);

        receiver = new VPNStatusReceiver(this);
        //???????????????
        setUpDrawer();
        //???????????????
        setSupportActionBar(tool);
        //?????????
        getSupportActionBar().setTitle("");
        //?????????????????????????????????
        updateServerIcon();
        //???????????????????????????
        /*if (openVPNUtils.isNewDay()) {
            //????????????
            onTimeAdd(120);
        } else {
            runningTimeTv.setText(openVPNUtils.getRemainingTime());
            LogUtils.e("create???????????????",""+receiver.getRemainingTime());
        }*/
    }

    @Override
    protected void initView() {
        //toolbar?????????icon?????????????????????
        tool.setNaviIconListener(this);
        //toolbar?????????icon????????????
        tool.setToolSwitchContainerLlListener(this);
        //???????????????
        serverContainerRl.setOnClickListener(this);

        //??????vpn??????
        toggleSv.setListener(new SwitchView.OnCheckedListener() {
            @Override
            public void onClick(boolean isChange) { //????????????
                //???????????????????????????????????????vpn
                if (!isChange) {
                    prepareStartVpn();
                } else {
                    //???????????????????????????????????? ????????????????????????
                    DialogManager.closeVPNDialog(HomeActivity.this, new DialogBtnListener() {
                        @Override
                        public void confirmClick() {
                            //????????????
                            if (openVPNUtils.stopVpn()) {
                                //????????????
                                toggleSv.toggle();
                            }
                        }
                    });
                }
            }

            @Override
            public void onCheckChanged(boolean isChange) { //????????????????????????
                //????????????????????????
                if (isChange) {
                    //??????vpn????????????
                    successRipperIm.setVisibility(View.VISIBLE);
                    //??????vpn
                    if (connectingDialog == null) {
                        connectingDialog = DialogManager.showVPNConnectDialog(HomeActivity.this);
                    }
                    if (!openVPNUtils.isStart()) {
                        connectingDialog.show();
                        openVPNUtils.startVpn();
                    } else {
                        if (connectingDialog.isShowing()) {
                            connectingDialog.dismiss();
                        }
                    }
                } else {
                    //????????????????????????
                    successRipperIm.setVisibility(View.GONE);
                }
            }
        });
        //?????????????????????(????????????????????????????????????listview
        navi.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 1://??????
                        migrateTo(FAQActivity.class);
                        break;
                    case 2://????????????
                        Intent protocolIntent = new Intent(HomeActivity.this, ProtocolActivity.class);
                        protocolIntent.putExtra(ProtocolActivity.PROTOCOL_TYPE, ProtocolActivity.PRIVACY_POLICY);
                        startActivity(protocolIntent);
                        break;
                    case 3://??????
                        migrateTo(AboutActivity.class);
                        break;
//                    case 4:
//                        AlertDialog Ldialog = new AlertDialog.Builder(HomeActivity.this)
//                                .setSingleChoiceItems(new String[]{"English","??????"},
//                                        spUtils.getSelectLanguage(),
//                                        new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        spUtils.saveLanguage(which);
//                                        MultiLanguage.
//                                    }
//                                })
//                                .create();
//                        Ldialog.show();
//                        break;
                    case 4://????????????
                        DialogManager.showExitDialog(HomeActivity.this,
                                new DialogBtnListener() {
                                    @Override
                                    public void confirmClick() {
                                        HomeActivity.this.finish();
                                    }
                                });
                        break;
                    case 5:
                        View testView = LayoutInflater.from(HomeActivity.this).inflate(R.layout.test, null, false);
                        EditText maxEt = (EditText) testView.findViewById(R.id.max_et);
                        EditText runEt = (EditText) testView.findViewById(R.id.run_et);

                        AlertDialog dialog = new AlertDialog.Builder(HomeActivity.this)
                                .setView(testView)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        openVPNUtils.test(
                                                maxEt.getText().toString().equals("") ? 0 : Integer.parseInt(maxEt.getText().toString()),
                                                runEt.getText().toString().equals("") ? 0 : Integer.parseInt(runEt.getText().toString())
                                        );
                                    }
                                })
                                .create();

                        dialog.show();

                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void prepareStartVpn(){
        //??????????????????????????????
        if (openVPNUtils.isTimeOver()) {
            DialogManager.showTimeOverDialog(HomeActivity.this, new DialogBtnListener() {
                @Override
                public void confirmClick() {
                    //??????????????????
                }
            });
            return;
        }
        //???????????????
        if (!NetworkUtils.isConnected()) {
            onNetUnConnected();
            return;
        }
        //??????????????????opvn???????????????
        if (!openVPNUtils.getServer().getServer().get(0).isAvailable()) {
            //??????
            if (loadFileDialog == null) {
                loadFileDialog = DialogManager.showDownLoadDialog(HomeActivity.this);
            }
            loadFileDialog.show();
            ServerBean.Server server = openVPNUtils.getServer().getServer().get(0);
            downloadOvpnConfigFile(server.getServer_address(),
                    server.getOvpnFileName(),
                    HomeActivity.this);
        }
        //?????????????????????
        Intent intent = VpnService.prepare(getApplicationContext());
        if (intent != null) {
            startActivityForResult(intent, OPEN_VPN_REQUEST_CODE);
            return;
        }
        //?????????????????????
        if (openVPNUtils.isStart()) {
            onVpnIsStarted();
            return;
        }
        toggleSv.toggle();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_ib://?????????header????????????
                drawer.closeDrawer(GravityCompat.START);
                break;
            case R.id.navi_icon://????????????????????????
                if (drawer.isDrawerOpen(navi)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    drawer.openDrawer(GravityCompat.START);
                }
                break;
            case R.id.tool_switch_container_ll://???????????????
                if (System.currentTimeMillis() - onShareClickTime < 1500) {
                    return;
                }
                onShareClickTime = System.currentTimeMillis();
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, "TEST SHARE");
                intent.setType("text/plain");
                startActivity(Intent.createChooser(intent, "Share"));
                break;
            case R.id.server_container_rl://??????????????????????????????
                migrateTo(ServiceActivity.class);
                break;
        }
    }

    /**
     * ?????????????????????
     * ListView + Header
     */
    private void setUpDrawer() {
        View headerView = LayoutInflater.from(this).inflate(R.layout.home_header, null, false);
        back = headerView.findViewById(R.id.back_ib);
        navi.addHeaderView(headerView);
        navi.setAdapter(new NaviAdapter(this));
        back.setOnClickListener(this);
    }

    /**
     * ????????????vpn??????icon
     */
    private void updateServerIcon() {

        if(OpenVPNUtils.getInstance().getServer().getServer().get(0).isAvailable()){
            Glide.with(this)
                    .load(OpenVPNUtils.getInstance().getServer().getFlag_url())
                    .into(flagIm);
            serverNameTv.setText(OpenVPNUtils.getInstance().getServer().getCountry());
        }else{
            flagIm.setImageResource(R.mipmap.icon);
            serverNameTv.setText("Auto");
        }
    }

    /**
     * ??????vpn??????
     */
    @Override
    protected void onResume() {
        //??????vpn????????????
        openVPNUtils.registerVpnListener(this);
        LogUtils.e("onResume", "----------??????openVpn----------");
        checkTimeStatues();
        updateServerIcon();

        super.onResume();
    }

    @Override
    protected void onPause() {
        //????????????
        openVPNUtils.unRegisterVpnListener(this);
        super.onPause();
    }

    @Override
    protected void onStop() {
        //????????????
        spUtils.saveServer(OpenVPNUtils.getInstance().getServer());
        LogUtils.e("onStop", "----------?????????????????????----------");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        LogUtils.e("onDestroy", "------??????vpn???????????????------------");
        if (OpenVPNUtils.getInstance().isStart()) {
            OpenVPNUtils.getInstance().stopVpn();
        }
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        super.onDestroy();
    }


    /**
     * ?????????????????????????????????vpn????????????????????????????????????
     */
    private void checkTimeStatues() {
        if ((openVPNUtils.isStart() && !toggleSv.isChecked()) || (!openVPNUtils.isStart() && toggleSv.isChecked())) {
            toggleSv.toggle();
        }

        if (openVPNUtils.isStart()) {
            //????????????
            runningTimeTv.start(OpenVPNUtils.getInstance().getRemainingTime());
        }else{

            if(runningTimeTv.isRun())
                runningTimeTv.stop();
        }
    }

    /**
     * ????????????
     *
     * @param min ??????
     */
    private void onTimeAdd(int min) {
        addTimeTv.setText("+" + min);
        Animation moveUp = AnimationUtils.loadAnimation(this, R.anim.move_up);
        moveUp.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
//                runningTimeTv.setText(heart.getRemainingTime());
                handler.sendEmptyMessageDelayed(ADD_TIME_ANIMATION_END, 2000);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        addTimeTv.setVisibility(View.VISIBLE);
        addTimeTv.startAnimation(moveUp);
    }

    /**
     * ????????????????????????
     */
    private void setAddTimeDismiss() {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.move_down);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                addTimeTv.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        addTimeTv.startAnimation(animation);
    }


    private final int ADD_TIME_ANIMATION_END = 0x57661;

    private final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                //??????????????????
                case CONNECTION_SUCCESS:
                    if (connectingDialog.isShowing()) {
                        connectingDialog.dismiss();
                    }
                    updateServerIcon();
                    Intent connectionSuccess = new Intent(HomeActivity.this, ConnectResultActivity.class);
                    connectionSuccess.putExtra(ConnectResultActivity.KEY_CONNECTION_RESULT, ConnectResultActivity.KEY_CONNECTION_SUCCESS);
                    startActivity(connectionSuccess);
                    break;
                //??????????????????
                case CONNECTION_FAILURE:
                    if (connectingDialog.isShowing()) {
                        connectingDialog.dismiss();
                    }
                    //???????????????
                    runningTimeTv.stop();
                    Intent connectionFailure = new Intent(HomeActivity.this, ConnectResultActivity.class);
                    connectionFailure.putExtra(ConnectResultActivity.KEY_CONNECTION_RESULT, ConnectResultActivity.KEY_CONNECTION_FAILURE);
                    startActivity(connectionFailure);
                    break;
                //??????????????????
                case CONNECTION_OUT:
                    if (toggleSv.isChecked()) {
                        toggleSv.toggle();
                    }
                    //???????????????
                    runningTimeTv.stop();
                    updateServerIcon();
                    Intent connectionOut = new Intent(HomeActivity.this, ConnectResultActivity.class);
                    connectionOut.putExtra(ConnectResultActivity.KEY_CONNECTION_RESULT, ConnectResultActivity.KEY_CONNECTION_OUT);
                    startActivity(connectionOut);
                    break;
                case ADD_TIME_ANIMATION_END:
                    setAddTimeDismiss();
                    break;
                default:
                    break;
            }
        }
    };

    //?????????
    @Override
    public void onBackPressed() {
        DialogManager.showExitDialog(HomeActivity.this,
                new DialogBtnListener() {
                    @Override
                    public void confirmClick() {
                        HomeActivity.this.finish();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OPEN_VPN_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                LogUtils.e("onActivityResult", "------------????????????vpn------------");
                prepareStartVpn();
            } else {
                showToast("You clicked cancel");
            }
        }
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        boolean permissionDenied = true;
//        switch (requestCode) {
//            case PERMISSION_REQUEST_CODE:
//                for (int i = 0; i < permissions.length; i++) {
//                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
//                        permissionDenied = true;
//                    } else {
//                        permissionDenied = false;
//                    }
//                }
//                if (permissionDenied) {
//                    showToast("Storage permissions have been denied, please open it yourself");
//                    PermissionUtils.toAppSetting(this);
//                } else {
//                    if (loadFileDialog == null) {
//                        loadFileDialog = DialogManager.showDownLoadDialog(HomeActivity.this);
//                    }
//                    loadFileDialog.show();
//                    ServerBean.Server server = openVPNUtils.getServer().getServer().get(0);
//                    downloadOvpnConfigFile(server.getServer_address(),
//                            server.getOvpnFileName(), HomeActivity.this);
//                }
//                break;
//            default:
//                break;
//        }
//    }

    @Override
    public void onVpnConnecting() {

    }

    @Override
    public void onVpnStarted() {
        LogUtils.e("onVpnStarted??????", "-------dialog isShow---------");
        //ui?????????????????? ?????????1.5?????????????????????????????????
        handler.sendEmptyMessageDelayed(CONNECTION_SUCCESS, 500);
    }

    public void onVpnIsStarted() {
        if (connectingDialog.isShowing()) {
            connectingDialog.dismiss();
        }
    }

    @Override
    public void onVpnDisconnected() {
        LogUtils.e("onVpnDisconnected??????", "-------????????????---------");
        handler.sendEmptyMessageDelayed(CONNECTION_OUT, 1000);
    }

    //?????????
    public void onNetUnConnected() {
        if (toggleSv.isChecked()) {
            toggleSv.toggle();
        }
        if (connectingDialog.isShowing()) {
            connectingDialog.dismiss();
        }
        showToast("The network is not connected");
        handler.sendEmptyMessage(CONNECTION_FAILURE);
    }

    @Override
    public void onFileInLoading(int progress) {
        loadFileDialog.setProgress(progress);
    }

    @Override
    public void onFileLoadingSusses(File file) {
        if (loadFileDialog != null && loadFileDialog.isShowing()) {
            loadFileDialog.dismiss();
        }
        prepareStartVpn();
    }

    @Override
    public void onFileLoadingFailure(Exception e) {
        if (loadFileDialog != null && loadFileDialog.isShowing()) {
            loadFileDialog.dismiss();
        }
        showToast("DownLoad Config File Failure");
    }

    @Override
    public void changed(String connectionState, String duration, String lastPacketReceive, String byteIn, String byteOut) {

        Log.e("openVPNUtils.isStart()", openVPNUtils.isStart() + "");

        if ("CONNECTED".equals(connectionState)) {
            //??????????????????????????????????????????????????????????????????????????????
            //??????????????????
            String remainingTime = TimeUtils.convertRunningTimeToRemainingTime(OpenVPNUtils.getInstance().getLastRunningTime(), MAX_TIME);
            runningTimeTv.start(remainingTime);
        } else if ("DISCONNECTED".equals(connectionState)) {
            //???????????????
            runningTimeTv.stop();
        }

        if (openVPNUtils.isWaringTime()) {

            //????????????????????????10????????????????????????
            if(System.currentTimeMillis() - App.spUtils.getLong(Constants.SP_WARING_TIME, 0) > 10*60*1000){
                DialogManager.showTimeWaringDialog(HomeActivity.this, new DialogBtnListener() {
                    @Override
                    public void confirmClick() {

                    }
                });
                //??????????????????
                App.spUtils.putLong(Constants.SP_WARING_TIME, System.currentTimeMillis());
            }
        }
        if (openVPNUtils.isTimeOver()) {
            DialogManager.showTimeOverDialog(HomeActivity.this, new DialogBtnListener() {
                @Override
                public void confirmClick() {

                }
            });
        }

        if (openVPNUtils.isStart()&& openVPNUtils.isTimeOver()){
            openVPNUtils.stopVpn();
        }
    }
}