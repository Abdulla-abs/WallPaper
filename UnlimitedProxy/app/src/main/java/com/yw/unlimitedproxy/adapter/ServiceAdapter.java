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

import com.bumptech.glide.Glide;
import com.yw.unlimitedproxy.R;
import com.yw.unlimitedproxy.model.Server;
import com.yw.unlimitedproxy.utils.OpenVPNUtils;

import java.util.List;

/**
 * 服务列表适配器
 */
public class ServiceAdapter extends BaseAdapter {

    private List<Server> servers;
    private Context mContext;
    private Server server;

    public ServiceAdapter(List<Server> servers, Context mContext) {
        this.servers = servers;
        this.mContext = mContext;
//        server = OpenVPNUtils.getInstance().getServer();
    }

    public void setServer(Server server) {
        servers = Server.getServerList();
        this.server = server;
        this.notifyDataSetChanged();
    }

    public void notifyServiceChanged(){
        servers = Server.getServerList();
//        server = OpenVPNUtils.getInstance().getServer();
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return servers.size();
    }

    @Override
    public Object getItem(int position) {
        return servers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.state_list_item, parent, false);
            viewHolder.flagIm = (ImageView) convertView.findViewById(R.id.flag_im);
            viewHolder.stateName = (TextView) convertView.findViewById(R.id.state_name);
            viewHolder.checkbox = (CheckBox) convertView.findViewById(R.id.checkbox);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Glide.with(mContext)
                .load(servers.get(position).getFlagUrl())
                .into(viewHolder.flagIm);
        viewHolder.stateName.setText(servers.get(position).getCountry());
        viewHolder.checkbox.setChecked(servers.get(position).getOvpn().equals(server.getOvpn()));

        if (servers.get(position).getOvpn().equals(server.getOvpn())) {
            convertView.setBackground(AppCompatResources.getDrawable(mContext, R.drawable.shape_teal_12));
        } else {
            convertView.setBackground(AppCompatResources.getDrawable(mContext, R.drawable.shape_gray_transparent_12));
        }

        return convertView;
    }

    public static class ViewHolder {
        ImageView flagIm;
        TextView stateName;
        CheckBox checkbox;
    }

}
