package com.yw.unlimitedproxy.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.content.res.AppCompatResources;

import com.bumptech.glide.Glide;
import com.bumptech.glide.util.Util;
import com.yw.unlimitedproxy.R;
import com.yw.unlimitedproxy.model.ServerBean;
import com.yw.unlimitedproxy.utils.OpenVPNUtils;
import com.yw.unlimitedproxy.utils.Utils;

import java.util.List;

public class ServerBeanAdapter extends BaseAdapter {

    private List<ServerBean> serverBeans;
    private ServerBean selectedBean = OpenVPNUtils.getInstance().getServer();
    private OnItemClickListener onItemClickListener;
    private boolean showBean = true;

    public ServerBeanAdapter(List<ServerBean> serverBeans) {
        this.serverBeans = serverBeans;
    }
    public ServerBeanAdapter(List<ServerBean> serverBeans,Boolean showSelected) {
        this.serverBeans = serverBeans;
        this.showBean = showSelected;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setSelectedBean(ServerBean selectedBean) {
        this.selectedBean = selectedBean;
    }

    @Override
    public int getCount() {
        return serverBeans.size();
    }

    @Override
    public Object getItem(int position) {
        return serverBeans.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ServiceAdapter.ViewHolder viewHolder;
        ServerBean serverBean = (ServerBean) getItem(position);
        if (convertView == null) {
            viewHolder = new ServiceAdapter.ViewHolder();
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.state_list_item, parent, false);
            viewHolder.flagIm = (ImageView) convertView.findViewById(R.id.flag_im);
            viewHolder.stateName = (TextView) convertView.findViewById(R.id.state_name);
            viewHolder.checkbox = (CheckBox) convertView.findViewById(R.id.checkbox);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ServiceAdapter.ViewHolder) convertView.getTag();
        }
        Glide.with(parent.getContext())
                .load(serverBean.getFlag_url())
                .into(viewHolder.flagIm);

        viewHolder.stateName.setText(serverBean.getCountry());

        if (showBean) {
            if (selectedBean.getCountry().equals(serverBean.getCountry())) {
                viewHolder.checkbox.setChecked(true);
                convertView.setBackground(AppCompatResources.getDrawable(parent.getContext(), R.drawable.shape_teal_12));
            } else {
                viewHolder.checkbox.setChecked(false);
                convertView.setBackground(AppCompatResources.getDrawable(parent.getContext(), R.drawable.shape_gray_transparent_12));
            }
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClicked(position, parent);
                }
            }
        });

        return convertView;
    }

    public static class ViewHolder {
        ImageView flagIm;
        TextView stateName;
        CheckBox checkbox;
    }

    public interface OnItemClickListener {
        void onItemClicked(int position, ViewGroup parent);
    }
}
