package com.yw.unlimitedproxy.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.yw.unlimitedproxy.R;
import com.yw.unlimitedproxy.ui.base.BaseActivity;

/**
 * 关于
 */
public class AboutActivity extends BaseActivity {

    private View statusBarDimen;
    private Toolbar tool;
    private TextView titleTv;
    private TextView appNameVersionTv;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_about;
    }

    @Override
    protected void init() {
        statusBarDimen = (View) findViewById(R.id.status_bar_dimen);
        tool = (Toolbar) findViewById(R.id.tool);
        titleTv = (TextView) findViewById(R.id.title_tv);
        appNameVersionTv = (TextView) findViewById(R.id.app_name_version_tv);

        setSupportActionBar(tool);
        getSupportActionBar().setTitle("");
        titleTv.setText("About");
    }

    @Override
    protected void initView() {

    }
}