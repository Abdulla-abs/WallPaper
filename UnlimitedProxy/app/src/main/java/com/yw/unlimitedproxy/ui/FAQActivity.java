package com.yw.unlimitedproxy.ui;

import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.webkit.WebView;
import android.widget.ListView;
import android.widget.TextView;

import com.yw.unlimitedproxy.R;
import com.yw.unlimitedproxy.adapter.FAQAdapter;
import com.yw.unlimitedproxy.ui.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 侧滑栏-----》疑问界面
 *
 */
public class FAQActivity extends BaseActivity {

    private View statusBarDimen;
    private Toolbar tool;
    private TextView titleTv;
    private ListView fqaList;
    private WebView webView;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_faqactivity;
    }

    @Override
    protected void init() {
        statusBarDimen = (View) findViewById(R.id.status_bar_dimen);
        tool = (Toolbar) findViewById(R.id.tool);
        titleTv = (TextView) findViewById(R.id.title_tv);
        fqaList = (ListView) findViewById(R.id.fqa_list);
        webView = findViewById(R.id.wv);

        setSupportActionBar(tool);
        getSupportActionBar().setTitle("");
        titleTv.setText("FAQ");
    }

    @Override
    protected void initView() {

        webView.setBackgroundColor(0);
//        webView.getBackground().setAlpha(2);
        webView.loadUrl("https://www.masterproxies.live/exhibition/FAQ.html");

//        test();
    }

    private void test(){
        List<String> contents = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            int i1 = new Random().nextInt(20)+3;
            StringBuilder stringBuilder = new StringBuilder("");
            for (int i2 = 0; i2 < i1; i2++) {
                stringBuilder.append("Hello World");
            }
            contents.add(stringBuilder.toString());
        }
        fqaList.setAdapter(new FAQAdapter(contents));
    }
}