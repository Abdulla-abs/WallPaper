package com.yw.unlimitedproxy.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.yw.unlimitedproxy.R;
import com.yw.unlimitedproxy.view.CheckItemView;

import java.util.List;

/**
 * 服务列表适配器2
 * 使用了ListView的选择模式，条目使用自定义View，条目都使用了Checkable借口
 * @see CheckItemView
 * 轻松实现单选模式等，但与本软件需求不符，暂时不使用
 */
public class StateArrayAdapter extends ArrayAdapter<StateArrayAdapter.State> {

    private Context context;
    private CheckItemView.OnCheckItemCheckListener listener;

    public void setListener(CheckItemView.OnCheckItemCheckListener listener) {
        this.listener = listener;
    }

    public StateArrayAdapter(@NonNull Context context, int resource, @NonNull List<State> objects) {
        super(context, resource, objects);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        CheckItemView checkItemView = null;
        if (convertView == null){
            checkItemView = new CheckItemView(context);
        }else {
            checkItemView = (CheckItemView) convertView;
        }

        checkItemView.setFlagIm(R.mipmap.emtpy_flag);
        checkItemView.setChecked(getItem(position).isCheck());
        checkItemView.setStateName(getItem(position).getName());
        return checkItemView;

    }

    public static class State{
        private int id;
        private String name;
        private boolean isCheck;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isCheck() {
            return isCheck;
        }

        public void setCheck(boolean check) {
            isCheck = check;
        }
    }

}
