package com.example.jonathan.chat.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.jonathan.chat.Model.User;
import com.example.jonathan.chat.R;

import java.util.Collections;
import java.util.List;

/**
 * Created by Jonathan on 21/08/15.
 */
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.MyViewHolder> {

    private LayoutInflater inflater;
    List<User> data = Collections.emptyList(); // initialise list to an empty list

    public UserAdapter(Context context, List<User> data){
        inflater = LayoutInflater.from(context);
        this.data = data;
    }

    public void delete(int position){
        data.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.user_item, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        User current = data.get(position);
        holder.username.setText(current.username);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView username;

        public MyViewHolder(View itemView){
            super(itemView);
            username = (TextView) itemView.findViewById(R.id.username);
            username.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.d("test", "click = " + getPosition());
            delete(getPosition());
        }
    }
}
