package com.yw.unlimitedproxy.view;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;

import com.yw.unlimitedproxy.R;

/**
 * 列表条目，与ListView搭配使用实现单选
 * 条目点击将触发 setChecked
 */
public class CheckItemView extends RelativeLayout implements Checkable {
    private ImageView flagIm;
    private TextView stateName;
    private CheckBox checkbox;

    public CheckItemView(@NonNull Context context) {
        super(context);
        View.inflate(context, R.layout.state_list_item, this);
        flagIm = (ImageView) findViewById(R.id.flag_im);
        stateName = (TextView) findViewById(R.id.state_name);
        checkbox = (CheckBox) findViewById(R.id.checkbox);
    }

    @Override
    public void setChecked(boolean checked) {
//        Toast.makeText(getContext(),"调用setChecked "+checked,Toast.LENGTH_SHORT).show();

        if (checked) {
            setBackground(AppCompatResources.getDrawable(getContext(), R.drawable.shape_teal_12));
        } else {
            setBackground(AppCompatResources.getDrawable(getContext(), R.drawable.shape_gray_transparent_12));
        }
        checkbox.setChecked(checked);

    }

    @Override
    public boolean isChecked() {
        return checkbox.isChecked();
    }

    @Override
    public void toggle() {
        checkbox.toggle();
    }

    public ImageView getFlagIm() {
        return flagIm;
    }

    public void setFlagIm(int id) {
        this.flagIm.setImageResource(id);
    }

    public TextView getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName.setText(stateName);
    }

    public CheckBox getCheckbox() {
        return checkbox;
    }

    public void setCheckbox(boolean checked) {
//        Toast.makeText(getContext(),"调用setCheckbox "+checked,Toast.LENGTH_SHORT).show();
//        checkbox.setChecked(checked);
//        if (checked) {
//            setBackground(AppCompatResources.getDrawable(getContext(), R.drawable.shape_teal_12));
//        } else {
//            setBackground(AppCompatResources.getDrawable(getContext(), R.drawable.shape_gray_transparent_12));
//        }
    }

    public interface OnCheckItemCheckListener {
        void onCheck(boolean isCheck);
    }
}
