package com.xcyo.simple.simadapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xutils.x;
import com.xcyo.simple.R;
import com.xcyo.yoyo.record.server.YoyoSingerRoomRecord;

import java.util.List;

/**
 * Created by bear on 2016/10/11.
 */

public class SimpleAdapter extends BaseAdapter {

    private Context context;
    private List<YoyoSingerRoomRecord> list;

    public SimpleAdapter(Context context , List<YoyoSingerRoomRecord> list){
        this.context = context;
        this.list = list;
    }

    public void setList(List<YoyoSingerRoomRecord> list){
        if(list!=null){
            this.list.clear();
            this.list.addAll(list);
            notifyDataSetChanged();
        }
    }

    public List<YoyoSingerRoomRecord> getList() {
        return list;
    }

    @Override
    public int getCount() {
        int count = 0;
        if(list!=null){
            count = list.size();
        }
        return count;
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        ViewHolder vh = null;
        if(convertView==null){
            view = LayoutInflater.from(context).inflate(R.layout.item_simple_now,parent,false);
            vh = new ViewHolder();
            vh.image = (ImageView) view.findViewById(R.id.anchor_img);
            vh.alias = (TextView) view.findViewById(R.id.anchor_name);
            vh.title = (TextView) view.findViewById(R.id.anchor_title);
//            vh.roomId = (TextView) view.findViewById(R.id.room_id);
            vh.isLive = (TextView) view.findViewById(R.id.listener_num);
            vh.imageLive = (TextView) view.findViewById(R.id.anchor_room_living);
            view.setTag(vh);
        }else {
            view = convertView;
            vh = (ViewHolder) view.getTag();
        }
        vh.image.setTag(list.get(position).getCover());
        if(!TextUtils.isEmpty(list.get(position).getCover())){
            if(list.get(position).getCover().equals(vh.image.getTag()+"")){
                x.image().bind(vh.image,list.get(position).getCover());
            }
        }
        vh.alias.setText(list.get(position).getAlias());
        vh.title.setText(list.get(position).getTitle());
        if(!TextUtils.isEmpty(list.get(position).getIsLive())){
            if("1".equals(list.get(position).getIsLive())){
                vh.imageLive.setVisibility(View.VISIBLE);
            }else {
                vh.imageLive.setVisibility(View.GONE);
            }
        }
        vh.isLive.setText(list.get(position).getMemberNum());
        return view;
    }

    class ViewHolder{
        TextView imageLive;
        ImageView image;
        TextView title;
        TextView roomId;
        TextView alias;
        TextView isLive;
    }
}
