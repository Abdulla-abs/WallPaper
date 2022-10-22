package com.yw.unlimitedproxy.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;

import com.yw.unlimitedproxy.R;
import com.yw.unlimitedproxy.utils.LogUtils;

public class SwitchView extends RelativeLayout {

    private TextView topTv,bottomTv;
    private ImageView topIm,bottomIm,button;

    private boolean isRunning = false;
    private boolean isChecked = false;

    private OnCheckedListener listener;

    public SwitchView(Context context) {
        this(context,null);
    }

    public SwitchView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,-1);
    }

    public SwitchView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View rootView = View.inflate(context, R.layout.switch_view, null);
        topTv = rootView.findViewById(R.id.top_tag);
        bottomTv = rootView.findViewById(R.id.bottom_tag);

        topIm = rootView.findViewById(R.id.top_img);
        bottomIm = rootView.findViewById(R.id.bottom_img);
        button = rootView.findViewById(R.id.btn);
        addView(rootView,new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));

        init();
    }

    private void init(){
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener!=null){
                    listener.onClick(isChecked);
                }
            }
        });
    }

    public void startMove(){
        if (isRunning) return;
        ValueAnimator valueAnimator;
        if (isChecked){
            valueAnimator = ValueAnimator.ofFloat(0, button.getTop());
        }else {
            valueAnimator = ValueAnimator.ofFloat((float) (button.getTop()*0.9),0);
        }
        LogUtils.e("startMove","isChecked--------"+isChecked);
        valueAnimator.setDuration(500);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatedValue = (float) animation.getAnimatedValue();
                button.setY(animatedValue);
                button.requestLayout();
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                isChecked = !isChecked;
                if (isChecked){
                    SwitchView.this.setBackground(AppCompatResources.getDrawable(getContext(),R.drawable.shape_switch_on_70));
                    button.setImageResource(R.mipmap.switch_on);
                    topIm.setVisibility(GONE);
                    topTv.setVisibility(GONE);
                }else {
                    SwitchView.this.setBackground(AppCompatResources.getDrawable(getContext(),R.drawable.shape_switch_70));
                    button.setImageResource(R.mipmap.switch_off);
                    bottomIm.setVisibility(GONE);
                    bottomTv.setVisibility(GONE);
                }
                isRunning = false;
                LogUtils.e("startMove","isChecked--------"+isChecked);

                if (listener!=null){
                    listener.onCheckChanged(isChecked);
                }
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                isRunning = true;
                if (!isChecked){
                    bottomIm.setVisibility(VISIBLE);
                    bottomTv.setVisibility(VISIBLE);
                }else {
                    topIm.setVisibility(VISIBLE);
                    topTv.setVisibility(VISIBLE);
                }
            }
        });

        valueAnimator.start();
    }

    public void setCheck(boolean check){
        if (isChecked == check) return;
        isChecked = check;
        startMove();
    }

    public void toggle(){
        startMove();
    }

    public boolean isRunning() {
        return isRunning;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setListener(OnCheckedListener listener) {
        this.listener = listener;
    }

    public interface OnCheckedListener{
        void onClick(boolean isChange);
        void onCheckChanged(boolean isChange);
    }
}
