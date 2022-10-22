package com.yw.unlimitedproxy.model;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.appcompat.app.AlertDialog;

import com.yw.unlimitedproxy.R;
import com.yw.unlimitedproxy.listener.DialogBtnListener;
import com.yw.unlimitedproxy.utils.OpenVPNUtils;

public class DialogManager {

    /**
     * 断开VPN前的弹窗
     *
     * @param activity
     * @param btnListener
     */
    public static void closeVPNDialog(Activity activity, DialogBtnListener btnListener) {
        View exitDialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_connect_out, null, false);
        //是否关闭按钮
        Button no, yes;
        no = exitDialogView.findViewById(R.id.no_bt);
        yes = exitDialogView.findViewById(R.id.yes_bt);
        AlertDialog dialog = new AlertDialog.Builder(activity)
                .setView(exitDialogView)
                .create();

        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });
        //关闭vpn
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (btnListener != null)
                    btnListener.confirmClick();

                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    /**
     * 超时弹窗
     *
     * @param activity
     * @param btnListener
     */
    public static void showTimeOverDialog(Activity activity, DialogBtnListener btnListener) {
        //停止vpn
        if (OpenVPNUtils.getInstance().isStart()) {
            OpenVPNUtils.getInstance().stopVpn();
        }
        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_time_over, null, false);
        Button yes, no;
        yes = view.findViewById(R.id.yes_bt);
        no = view.findViewById(R.id.no_bt);
        AlertDialog dialog = new AlertDialog.Builder(activity)
                .setView(view)
                .create();
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (btnListener != null)
                    btnListener.confirmClick();

                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    /**
     * 退出弹窗
     *
     * @param activity
     * @param btnListener
     */
    public static void showExitDialog(Activity activity, DialogBtnListener btnListener) {
        View exitDialog = LayoutInflater.from(activity).inflate(R.layout.dialog_exit, null, false);
        Button yes, no;
        yes = exitDialog.findViewById(R.id.yes_bt);
        no = exitDialog.findViewById(R.id.no_bt);
        AlertDialog dialog = new AlertDialog.Builder(activity)
                .setView(exitDialog)
                .create();

        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                if (btnListener != null)
                    btnListener.confirmClick();
            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    /**
     * 显示连接弹窗
     */
    public static AlertDialog showVPNConnectDialog(Activity activity) {
        AlertDialog connectingDialog = new AlertDialog.Builder(activity)
                .setView(R.layout.dialog_connecting)
                .setCancelable(false)
                .create();

        connectingDialog.show();
        connectingDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        return connectingDialog;
    }

    public static AlertDialog showChangeDialog(Activity activity, DialogBtnListener btnListener) {
        View dialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_change_state, null, false);
        Button cancel, replace;
        cancel = dialogView.findViewById(R.id.cancel_bt);
        replace = dialogView.findViewById(R.id.replace_bt);
        AlertDialog changeServiceDialog = new AlertDialog.Builder(activity)
                .setView(dialogView)
                .create();
        changeServiceDialog.show();
        changeServiceDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeServiceDialog.dismiss();

            }
        });
        //点击替换服务区域
        replace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                changeServiceDialog.dismiss();
                if (btnListener != null)
                    btnListener.confirmClick();
            }
        });
        return changeServiceDialog;
    }

    public static void showTimeWaringDialog(Activity activity, DialogBtnListener btnListener) {
        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_warming, null, false);
        Button yes, no;
        yes = view.findViewById(R.id.yes_bt);
        no = view.findViewById(R.id.no_bt);
        AlertDialog dialog = new AlertDialog.Builder(activity)
                .setView(view)
                .create();
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (btnListener != null) {
                    btnListener.confirmClick();
                }
            }
        });
        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }

    public static ProgressDialog showDownLoadDialog(Activity activity) {
        ProgressDialog loadingDialog = new ProgressDialog(activity);
        loadingDialog.setCancelable(false);
        loadingDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        loadingDialog.setTitle("Loading");
        loadingDialog.setMessage("Downloading configuration file...");
        loadingDialog.setMax(100);
        return loadingDialog;
    }

    public static AlertDialog showPermissionIntroduceDialog(Activity activity, DialogBtnListener btnListener){
        AlertDialog alertDialog = new AlertDialog.Builder(activity)
                .setCancelable(false)
                .setTitle("Warming")
                .setMessage("We need permission to store files, please reauthorize")
                .setNegativeButton("Exit app", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.exit(0);
                    }
                })
                .setPositiveButton("reauthorize", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        btnListener.confirmClick();
                    }
                })
                .create();
        return alertDialog;
    }
}
