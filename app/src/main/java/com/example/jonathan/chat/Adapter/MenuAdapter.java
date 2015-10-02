package com.example.jonathan.chat.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jonathan.chat.FriendActivity;
import com.example.jonathan.chat.ListActivity;
import com.example.jonathan.chat.Model.MenuItem;
import com.example.jonathan.chat.R;

import java.util.Collections;
import java.util.List;

/**
 * Created by Jonathan on 27/09/15.
 */
public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MyViewHolder> {

    private LayoutInflater inflater;
    List<MenuItem> data = Collections.emptyList(); // initialise list to an empty list

    private Context context;

    public MenuAdapter(Context context, List<MenuItem> data){
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.data = data;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.menu_item, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        MenuItem current = data.get(position);
        holder.title.setText(current.getTitle());
        holder.icon.setImageResource(current.getIcon());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView title;
        ImageView icon;

        public MyViewHolder(View itemView){
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            icon = (ImageView) itemView.findViewById(R.id.icon);
            title.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(v == title){
                if(getPosition() == 0){
                    context.startActivity(new Intent(context, ListActivity.class));
                } else if(getPosition() == 1){
                    context.startActivity(new Intent(context, FriendActivity.class));
                }
            }
        }
    }
}
