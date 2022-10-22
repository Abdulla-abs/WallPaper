package com.yw.unlimitedproxy.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yw.unlimitedproxy.R;
import com.yw.unlimitedproxy.ui.base.BaseActivity;
import com.yw.unlimitedproxy.utils.ScreenUtils;
import com.yw.unlimitedproxy.view.NoScrollWebView;

import java.util.Objects;

/**
 * 侧滑栏----》协议界面
 */
public class ProtocolActivity extends BaseActivity {

    //协议键值
    public static final String PROTOCOL_TYPE = "type";
    public static final int PRIVACY_POLICY = 1;
    public static final int TERMS_OF_SERVICE = 2;

    private View statusBarDimen;
    private Toolbar tool;
    private TextView titleTv;
    private NoScrollWebView contentWv;



    /**
     * Master & Unlimited Proxy
     * 服务条款
     * https://www.masterproxies.live/exhibition/mup_s.html
     * 隐私协议
     * https://www.masterproxies.live/exhibition/mup_p.html
     * @return
     */

    @Override
    protected int getLayoutId() {
        return R.layout.activity_protocol;
    }

    @Override
    protected void init() {
        statusBarDimen = (View) findViewById(R.id.status_bar_dimen);
        tool = (Toolbar) findViewById(R.id.tool);
        titleTv = (TextView) findViewById(R.id.title_tv);
        contentWv = (NoScrollWebView) findViewById(R.id.content_wv);

        setSupportActionBar(tool);
        getSupportActionBar().setTitle("");
    }

    @Override
    protected void initView() {
        //判断当前是哪种协议
        int type = getIntent().getIntExtra(PROTOCOL_TYPE,-1);
        if (type == PRIVACY_POLICY){
            //隐私政策
            titleTv.setText(getResources().getString(R.string.privacy_policy));
            contentWv.loadUrl("https://www.masterproxies.live/exhibition/mup_p.html");
        }else if (type == TERMS_OF_SERVICE){
            //服务政策
            titleTv.setText(getResources().getString(R.string.terms_of_service_title));
            contentWv.loadUrl("https://www.masterproxies.live/exhibition/mup_s.html");
        }else if (type == -1){
            //数据有误

        }
    }
}