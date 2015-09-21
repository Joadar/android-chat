package com.example.jonathan.chat.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jonathan.chat.Model.Room;
import com.example.jonathan.chat.R;
import com.example.jonathan.chat.Utils.ImageLoadTask;

import java.util.ArrayList;

/**
 * Created by Jonathan on 17/09/15.
 */

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.MyViewHolder> {

    private LayoutInflater inflater;
    private ArrayList<Room> rooms;

    public RoomAdapter(Context context, ArrayList<Room> rooms){
        inflater = LayoutInflater.from(context);
        this.rooms = rooms;
    }

    public void delete(int position){
        rooms.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.room_item, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Room current = rooms.get(position);
        holder.name.setText(current.getName());
        holder.space.setText("User : " + current.getNbUser() + "/" + current.getSpace());
        new ImageLoadTask(current.getImage(), holder.image).execute(); // put the string image in the image view
    }

    @Override
    public int getItemCount() {
        return rooms.size();
    }

    /***** Creating OnItemClickListener *****/

    // Define listener member variable
    private OnItemClickListener listener;

    // Define the listener interface
    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    // Define the method that allows the parent activity or fragment to define the listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView name;
        public TextView space;
        public ImageView image;
        public ImageView like;

        public MyViewHolder(View itemView){
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.name);
            space = (TextView) itemView.findViewById(R.id.space);
            image = (ImageView) itemView.findViewById(R.id.imageRoom);
            like = (ImageView) itemView.findViewById(R.id.likeRoom);
            like.setTag(R.mipmap.nolike);

            itemView.setOnClickListener(this);

            like.setOnClickListener(this);
        }

        // Handles the row being being clicked
        @Override
        public void onClick(View view) {
            if(view == itemView) {
                int position = getLayoutPosition(); // gets item position
                // We can access the data within the views
                listener.onItemClick(itemView, getLayoutPosition());
            } else if (view == like){
                //like.setImageResource(R.mipmap.like);
                listener.onItemClick(like, getLayoutPosition());
            }
        }
    }
}
