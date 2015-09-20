package com.example.jonathan.chat.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.jonathan.chat.Model.Room;
import com.example.jonathan.chat.R;

import java.util.ArrayList;

/**
 * Created by Jonathan on 17/09/15.
 */
public class RoomAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;

    private ArrayList<Room> rooms;

    public RoomAdapter(Context context, ArrayList<Room> rooms){
        this.context = context;
        this.rooms = rooms;

        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return rooms.size();
    }

    @Override
    public Object getItem(int position) {
        return rooms.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if(convertView == null){
            convertView = inflater.inflate(R.layout.list_item, null);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.space = (TextView) convertView.findViewById(R.id.space);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.name.setText(rooms.get(position).getName());
        holder.space.setText("User : " + rooms.get(position).getNbUser() + "/" + rooms.get(position).getSpace());

        return convertView;
    }

    class ViewHolder {
        public TextView name;
        public TextView space;
    }
}
