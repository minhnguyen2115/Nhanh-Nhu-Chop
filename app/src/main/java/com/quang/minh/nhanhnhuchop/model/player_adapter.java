package com.quang.minh.nhanhnhuchop.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.login.widget.ProfilePictureView;
import com.quang.minh.nhanhnhuchop.R;

import java.util.ArrayList;

public class player_adapter extends BaseAdapter {
    private Context context;
    private int layout;
    private ArrayList<player> player_list;

    public player_adapter(Context context, int layout, ArrayList<player> player_list) {
        this.context = context;
        this.layout = layout;
        this.player_list = player_list;
    }

    @Override
    public int getCount() {
        return player_list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public class viewHolder{
        TextView tv_rank, tv_point, tv_name;
        ProfilePictureView img_avatar;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        viewHolder holder;
        if(convertView==null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layout,null);
            holder = new viewHolder();
            holder.tv_rank =(TextView) convertView.findViewById(R.id.tv_rank);
            holder.img_avatar =(ProfilePictureView) convertView.findViewById(R.id.img_avatar);
            holder.tv_name =(TextView) convertView.findViewById(R.id.tv_name);
            holder.tv_point =(TextView) convertView.findViewById(R.id.tv_point);
            convertView.setTag(holder);
        }
        else {
            holder = (viewHolder) convertView.getTag();
        }
        player player = player_list.get(position);
        holder.tv_rank.setText(player.getRank()+"");
        holder.img_avatar.setProfileId(player.getId());
        holder.tv_name.setText(player.getName());
        holder.tv_point.setText(player.getPoint()+"");

//        byte[] hinhAnh = player.getAvatar();
//        Bitmap bitmap = BitmapFactory.decodeByteArray(hinhAnh, 0 , hinhAnh.length);

        return convertView;

    }
}
