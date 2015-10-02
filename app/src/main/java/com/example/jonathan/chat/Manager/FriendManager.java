package com.example.jonathan.chat.Manager;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;

import com.example.jonathan.chat.FriendActivity;
import com.example.jonathan.chat.R;
import com.example.jonathan.chat.Utils.SocketServer;

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
        Snackbar.make(coordinatorLayoutView, "You have new friend requests", Snackbar.LENGTH_INDEFINITE)
                .setAction("See", clickListener)
                .show();
    }

}
