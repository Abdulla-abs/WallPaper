package com.yw.unlimitedproxy.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yw.unlimitedproxy.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 主页侧滑栏适配器
 * 由于使用了headerView，list列表由1开始。
 */
public class NaviAdapter extends BaseAdapter {

    private Context mContext;
    private List<NaviItem> naviItems = new ArrayList<>(
            Arrays.asList(//显示数据
//                    new NaviItem(R.mipmap.share_icon,"Share"),
                    new NaviItem(R.mipmap.fqa_icon,"FAQ"),
                    new NaviItem(R.mipmap.privacy_icon,"Privacy Policy"),
                    new NaviItem(R.mipmap.about_icon,"About"),
//                    new NaviItem(R.drawable.ic_baseline_settings_24,"language"),
                    new NaviItem(R.mipmap.exit_icon,"Exit"),
                    new NaviItem(R.drawable.ic_baseline_settings_24,"测试")
                    )
    );

    public NaviAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return naviItems.size();
    }

    @Override
    public Object getItem(int position) {
        return naviItems.get(position);
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.navi_item,parent,false);
            viewHolder.icon = (ImageView) convertView.findViewById(R.id.icon);
            viewHolder.name = (TextView) convertView.findViewById(R.id.name);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.icon.setImageResource(naviItems.get(position).getId());
        viewHolder.name.setText(naviItems.get(position).getName());

        return convertView;
    }

    public class ViewHolder {
        ImageView icon;
        TextView name;
    }

    public class NaviItem {
        public NaviItem(int id, String name) {
            this.id = id;
            this.name = name;
        }

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

        private int id;
        private String name;
    }
}
