package com.example.jonathan.chat.Adapter;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.jonathan.chat.Fragment.ProfileFragment;
import com.example.jonathan.chat.Manager.FriendManager;
import com.example.jonathan.chat.Model.User;
import com.example.jonathan.chat.R;
import com.example.jonathan.chat.Utils.SocketServer;

import java.util.ArrayList;

/**
 * Created by Jonathan on 27/09/15.
 */
public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.FriendHolder> {

    private FriendManager friendManager;

    private Context context;
    private LayoutInflater inflater;

    private ArrayList<User> friends;
    private boolean isRequest;

    public FriendAdapter(Context context, Activity activity, ArrayList<User> friends, boolean isRequest){
        friendManager = new FriendManager(activity);

        inflater = LayoutInflater.from(context);
        this.context = context;
        this.friends = friends;
        this.isRequest = isRequest;
    }

    @Override
    public FriendAdapter.FriendHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.friend_item, null);

        FriendHolder holder = new FriendHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(FriendAdapter.FriendHolder holder, int position) {
        User current = friends.get(position);
        holder.username.setText(current.getUsername());

        // ask the server if this user is connected
        SocketServer.getInstance().getSocket().emit("is_connected", current.getId());
        Log.d("friendAdapterLog", "is connected called");
        // if user is connected, change the status text
        friendManager.isConnected(holder.status);

        if(!isRequest){
            holder.buttonValid.setVisibility(View.GONE);
            holder.buttonDecline.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    public class FriendHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView username;
        public TextView status;
        public Button buttonValid;
        public Button buttonDecline;

        public FriendHolder(View itemView){
            super(itemView);

            username = (TextView) itemView.findViewById(R.id.username);
            status = (TextView) itemView.findViewById(R.id.status);
            buttonValid = (Button) itemView.findViewById(R.id.actionButtonValid);
            buttonDecline = (Button) itemView.findViewById(R.id.actionButtonDecline);

            username.setOnClickListener(this);
            buttonValid.setOnClickListener(this);
            buttonDecline.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(v == username){
                FragmentManager fm = ((Activity) context).getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();

                Fragment prev = fm.findFragmentByTag("dialog");
                if (prev != null) {
                    ft.remove(prev);
                }

                ft.addToBackStack(null);

                // Create and show the dialog.
                DialogFragment newFragment = ProfileFragment.newInstance(friends.get(getAdapterPosition()).getId());
                newFragment.show(ft, "dialog");
            } else if(v == buttonValid){
                SocketServer.getInstance().getSocket().emit("accept_request_friend", (friends.get(getAdapterPosition()).getId()));
                removeItem(getAdapterPosition());
            } else if(v == buttonDecline){
                SocketServer.getInstance().getSocket().emit("decline_request_friend", (friends.get(getAdapterPosition()).getId()));
                removeItem(getAdapterPosition());
            }
        }

        private void removeItem(int position){
            friends.remove(position);
            notifyItemRemoved(position);
        }
    }
}
