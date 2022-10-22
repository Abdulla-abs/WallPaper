package com.yw.unlimitedproxy.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.yw.unlimitedproxy.R;

public class ToolBar extends Toolbar {

    private LayoutInflater inflater;
    private View view;
    private ImageButton naviIcon;
    private LinearLayout toolSwitchContainerLl;


    public ToolBar(@NonNull Context context) {
        this(context,null);
    }

    public ToolBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ToolBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();


    }

    private void init(){
        if (view == null){
            inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.toolbar2,null);

            naviIcon = (ImageButton) view.findViewById(R.id.navi_icon);
            toolSwitchContainerLl = (LinearLayout) view.findViewById(R.id.tool_switch_container_ll);

            LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL);
            addView(view,lp);

        }
    }

    public void setNaviIconListener(View.OnClickListener listener){
        if (naviIcon != null){
            naviIcon.setOnClickListener(listener);
        }
    }

    public void setToolSwitchContainerLlListener(View.OnClickListener listener){
        if (toolSwitchContainerLl != null){
            toolSwitchContainerLl.setOnClickListener(listener);
        }
    }
}
