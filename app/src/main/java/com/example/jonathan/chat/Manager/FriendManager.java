package com.example.jonathan.chat.Manager;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.jonathan.chat.FriendActivity;
import com.example.jonathan.chat.Model.User;
import com.example.jonathan.chat.R;
import com.example.jonathan.chat.Utils.SocketServer;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.socket.emitter.Emitter;

/**
 * Created by Jonathan on 02/10/2015.
 */
public class FriendManager {

    private Activity context;

    public FriendManager(Activity context){
        this.context = context;
    }

    public void haveRequest(){
        SocketServer.getInstance().getSocket().on("have_friend_request", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {

                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        Log.d("ListActivityLog", "have_friend_request args = " + args[0]);
                        if(args[0] != null){
                            snackbar();
                        }
                    }
                });
            }
        });
    }

    private void snackbar(){
        Log.d("ListActivityLog", "snackbar method called");

        // event for the snackbar
        final View.OnClickListener clickListener = new View.OnClickListener() {
            public void onClick(View v) {
                // when we click on the button, we are redirected to the friend activity
                context.startActivity(new Intent(context.getApplicationContext(), FriendActivity.class));
            }
        };

        final View coordinatorLayoutView = context.findViewById(R.id.snackbarPosition);

        // make list view of friends request
        Snackbar snackbar = Snackbar.make(coordinatorLayoutView, "You have new friend requests", Snackbar.LENGTH_INDEFINITE)
                .setAction("See", clickListener).setActionTextColor(context.getResources().getColor(R.color.primary));

        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);

        snackbar.show();
    }

    public void getFriend(final ArrayList<User> listFriends){
        // get list friend request
        SocketServer.getInstance().getSocket().on("get_friend", new Emitter.Listener() {

            @Override
            public void call(final Object... args) {
                String username;
                int idUsername;

                Log.d("friendFragment", "get_friends args = " + args[0]);

                JSONObject data = (JSONObject) args[0];
                try {
                    username = data.getString("username");
                    idUsername = data.getInt("id");
                } catch (JSONException e) {
                    return;
                }

                User user = new User();
                user.setId(idUsername);
                user.setUsername(username);

                listFriends.add(user);
            }
        });
    }

    public void getFriendRequest(final ArrayList<User> listRequest){
        SocketServer.getInstance().getSocket().on("get_friend_request", new Emitter.Listener() {

            @Override
            public void call(final Object... args) {
                String username;
                int idUsername;

                Log.d("friendFragment", "get_friend_request args = " + args[0]);

                JSONObject data = (JSONObject) args[0];
                try {
                    username = data.getString("username");
                    idUsername = data.getInt("id");
                } catch (JSONException e) {
                    return;
                }

                User user = new User();
                user.setId(idUsername);
                user.setUsername(username);

                listRequest.add(user);
            }
        });
    }

    public void isConnected(final TextView statusText){
        SocketServer.getInstance().getSocket().on("is_connected", new Emitter.Listener() {

            @Override
            public void call(final Object... args) {
                if (context != null) {
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("profileFragmentLog", "is connected args = " + args[0]);
                            if ((boolean) args[0])
                                statusText.setText("Status: online");
                            else
                                statusText.setText("Status: offline");
                        }
                    });
                }
            }
        });
    }

}
