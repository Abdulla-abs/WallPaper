package com.yw.unlimitedproxy.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.yw.unlimitedproxy.R;
import com.yw.unlimitedproxy.utils.TimeUtils;

@SuppressLint("AppCompatCustomView")
public class CountDownTextView extends TextView implements Runnable{

    private boolean isRun = false;

    public boolean isRun() {
        return isRun;
    }

    public CountDownTextView(Context context) {
        super(context);
    }

    public CountDownTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CountDownTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CountDownTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    public void start(String startTime){
        setText(startTime);
        this.isRun = true;

        removeCallbacks(this);
        postDelayed(this, 1000);
    }

    public void stop() {
        this.isRun = false;
        this.removeCallbacks(this);//暂停时移除当前线程，否则会加快运行
    }


    @Override
    public void run() {
        int next = TimeUtils.convertStringTimeToLong(getText().toString()) - 1;
        if( next <= 0 ){
            stop();
        }else if(isRun){

            if(next<30*60){
                //setTextColor(getResources().getColor(R.color.yellow));
                //setTextColor(Color.parseColor("#FFFFD754"));
                //setTextColor( this.getResources().getColorStateList( R.color.yellow) );
                setTextColor(getContext().getColor(R.color.yellow));
            }
            setText(TimeUtils.convertIntTimeToStringTime(next));
            this.postDelayed(this, 1000);
        }
    }


}
