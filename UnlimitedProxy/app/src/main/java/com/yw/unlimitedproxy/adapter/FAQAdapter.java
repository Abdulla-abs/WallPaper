package com.yw.unlimitedproxy.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yw.unlimitedproxy.R;

import java.util.List;

/**
 * 侧滑栏 ————> 疑问
 * 一个类似聊天列表的ListView适配器，有两个布局，类似
 */
public class FAQAdapter extends BaseAdapter {

    private List<String> contents;

    public FAQAdapter(List<String> contents) {
        this.contents = contents;
    }

    @Override
    public int getCount() {
        return contents.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if(position % 2 == 0){
            return 0;
        }else {
            return 1;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LeftViewHolder leftViewHolder = null;
        RightViewHolder rightViewHolder = null;
        if (convertView == null){
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            switch (getItemViewType(position)){
                case 0:
                    leftViewHolder = new LeftViewHolder();
                    convertView = inflater.inflate(R.layout.faq_left_item, parent, false);
                    leftViewHolder.content = convertView.findViewById(R.id.content_tv);
                    convertView.setTag(leftViewHolder);
                    break;
                case 1:
                    rightViewHolder = new RightViewHolder();
                    convertView = inflater.inflate(R.layout.faq_right_item, parent, false);
                    rightViewHolder.content = convertView.findViewById(R.id.content_tv);
                    convertView.setTag(rightViewHolder);
                    break;
            }
        }else {
            switch (getItemViewType(position)){
                case 0:
                    leftViewHolder = (LeftViewHolder) convertView.getTag();
                    break;
                case 1:
                    rightViewHolder = (RightViewHolder) convertView.getTag();
                    break;
            }
        }
//        switch (getItemViewType(position)){
//            case 0:
//                if (convertView == null){
//                    leftViewHolder = new LeftViewHolder();
//                    convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fqa_left_item, parent, false);
//                    leftViewHolder.content = convertView.findViewById(R.id.content_tv);
//                    convertView.setTag(leftViewHolder);
//                }else {
//                    leftViewHolder = (LeftViewHolder) convertView.getTag();
//                }
//                break;
//            case 1:
//                if (convertView == null){
//                    rightViewHolder = new RightViewHolder();
//                    convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fqa_right_item, parent, false);
//                    rightViewHolder.content = convertView.findViewById(R.id.content_tv);
//                    convertView.setTag(rightViewHolder);
//                }else {
//                    rightViewHolder = (RightViewHolder) convertView.getTag();
//                }
//                break;
//        }
        switch (getItemViewType(position)){
            case 0:
                leftViewHolder.content.setText(contents.get(position));
                break;
            case 1:
                rightViewHolder.content.setText(contents.get(position));
                break;
        }
        return convertView;
    }

    static class LeftViewHolder{
        private TextView content;
    }

    static class RightViewHolder{
        private TextView content;
    }


}
