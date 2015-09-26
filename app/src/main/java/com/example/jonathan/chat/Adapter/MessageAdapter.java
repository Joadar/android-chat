package com.example.jonathan.chat.Adapter;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jonathan.chat.Fragment.ProfileFragment;
import com.example.jonathan.chat.Model.Message;
import com.example.jonathan.chat.R;
import com.example.jonathan.chat.Utils.Tools;

import java.util.ArrayList;

/**
 * Created by Jonathan on 17/09/15.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder> {

    private LayoutInflater inflater;
    private ArrayList<Message> messages;
    private Context context;

    public MessageAdapter(Context context, ArrayList<Message> messages){
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.messages = messages;
    }

    public void delete(int position){
        messages.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public int getItemViewType(int position) {
        //Implement your logic here
        Message vo = messages.get(position);

        int value = 0;
        if(vo.isInformation()){
            value = 0;
        } else {
            if(vo.getAuthor().getUsername().equals(Tools.readFromPreferences(context, "username", null))){
                value = 1; // 1 for right
            } else {
                value = 2; // 2 for left
            }
        }

        return value;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;

        // if is not an information about the user (join or left the message)
        if(viewType == 1) {
            view = inflater.inflate(R.layout.message_item_right, null);
        } else if (viewType == 2){
            view = inflater.inflate(R.layout.message_item_left, null);
        } else {
            view = inflater.inflate(R.layout.message_item_center, null);
        }

        // to center informations on the screen we need to "force" the windows size
        WindowManager windowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        int width = windowManager.getDefaultDisplay().getWidth();
        int height = windowManager.getDefaultDisplay().getHeight();
        view.setLayoutParams(new RecyclerView.LayoutParams(width, RecyclerView.LayoutParams.MATCH_PARENT));

        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MessageAdapter.MyViewHolder holder, int position) {
        Message current = messages.get(position);
        holder.author.setText(current.getAuthor().getUsername());
        holder.message.setText(current.getMessage());

        if(!current.isInformation()) {
            // if it's a boy
            if (current.getAuthor().isSexe()) {
                // change the background of the username to blue
                holder.layoutAuthor.setBackgroundResource(R.drawable.author_boy);
            } else {
                // change the background of the username to pink
                holder.layoutAuthor.setBackgroundResource(R.drawable.author_girl);
            }
        }

    }

    @Override
    public int getItemCount() {
        return messages.size();
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

        public TextView author;
        public TextView message;
        public RelativeLayout layoutAuthor;

        public MyViewHolder(View itemView){
            super(itemView);

            author = (TextView) itemView.findViewById(R.id.author);
            message = (TextView) itemView.findViewById(R.id.message);
            layoutAuthor = (RelativeLayout) itemView.findViewById(R.id.layoutAuthor);

            author.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(v == author){
                // if is not an information message (join or left)
                if(!messages.get(getAdapterPosition()).isInformation()) {
                    FragmentManager fm = ((Activity) context).getFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();

                    Fragment prev = fm.findFragmentByTag("dialog");
                    if (prev != null) {
                        ft.remove(prev);
                    }

                    ft.addToBackStack(null);

                    // Create and show the dialog.
                    DialogFragment newFragment = ProfileFragment.newInstance(messages.get(getAdapterPosition()).getAuthor().getId());
                    newFragment.show(ft, "dialog");
                }
            }
        }
    }
}
