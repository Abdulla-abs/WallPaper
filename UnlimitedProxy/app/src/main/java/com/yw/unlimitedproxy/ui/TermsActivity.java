
package com.yw.unlimitedproxy.ui;

import androidx.annotation.NonNull;

import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.yw.unlimitedproxy.R;
import com.yw.unlimitedproxy.ui.base.BaseActivity;
import com.yw.unlimitedproxy.utils.Constants;
import com.yw.unlimitedproxy.utils.SPUtils;

/**
 * 同意协议界面，在引导页之后
 * 同意后进入主页
 * 不会显示第二次
 *
 */
public class TermsActivity extends BaseActivity {

    private TextView proIndTv;
    private Button agreeBt;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_terms;
    }

    @Override
    protected void init() {
        proIndTv = (TextView) findViewById(R.id.pro_ind_tv);
        agreeBt = (Button) findViewById(R.id.agree_bt);


        SpannableStringBuilder builder = new SpannableStringBuilder(getString(R.string.terms_of_service));
        //这个一定要记得设置，不然点击不生效
        proIndTv.setMovementMethod(LinkMovementMethod.getInstance());
        builder.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {

            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                ds.setColor(getResources().getColor(R.color.basic_e));
            }
        }, 0, 24, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        proIndTv.setText(builder);
    }

    @Override
    protected void initView() {
        agreeBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //同意协议
                SPUtils spUtils = new SPUtils();
                spUtils.putBoolean(Constants.SP_KEY_ACCESS_PROTOCOL,true);
                migrateTo(HomeActivity.class);
                TermsActivity.this.finish();
            }
        });
    }

}