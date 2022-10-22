package com.yw.unlimitedproxy.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.content.res.AppCompatResources;

import com.yw.unlimitedproxy.R;

import java.util.List;

/**
 * 服务列表适配器
 *
 */
public class StateAdapter extends BaseAdapter {

    private List<State> states;
    private Context mContext;
    private OnCheckListener listener;

    public StateAdapter(List<State> states, Context mContext) {
        this.states = states;
        this.mContext = mContext;
    }

    public void setListener(OnCheckListener listener) {
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return states.size();
    }

    @Override
    public Object getItem(int position) {
        return states.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.state_list_item,parent,false);
            viewHolder.flagIm = (ImageView) convertView.findViewById(R.id.flag_im);
            viewHolder.stateName = (TextView) convertView.findViewById(R.id.state_name);
            viewHolder.checkbox = (CheckBox) convertView.findViewById(R.id.checkbox);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

//        viewHolder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked){
//                    states.get(position).setCheck(false);
//                    if (listener != null){
//                        listener.onCheckChanged(false);
//                    }
//                    StateAdapter.this.notifyDataSetChanged();
//                }
//            }
//        });

        viewHolder.flagIm.setImageResource(states.get(position).getId());
        viewHolder.stateName.setText(states.get(position).getName());
        viewHolder.checkbox.setChecked(states.get(position).isCheck());

        if (states.get(position).isCheck()){
            convertView.setBackground(AppCompatResources.getDrawable(mContext,R.drawable.shape_teal_12));
        }else {
            convertView.setBackground(AppCompatResources.getDrawable(mContext,R.drawable.shape_gray_transparent_12));
        }

        return convertView;
    }

    public static class ViewHolder{
        ImageView flagIm;
        TextView stateName;
        CheckBox checkbox;
    }

    public interface OnCheckListener{
        void onCheckChanged(boolean isChecked);
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
