package com.example.jonathan.chat.Fragment;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jonathan.chat.FriendActivity;
import com.example.jonathan.chat.Model.User;
import com.example.jonathan.chat.R;
import com.example.jonathan.chat.RoomActivity;
import com.example.jonathan.chat.Utils.SocketServer;
import com.example.jonathan.chat.Utils.Tools;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.emitter.Emitter;

/**
 * Created by Jonathan on 26/09/15.
 */
public class ProfileFragment extends DialogFragment implements View.OnClickListener {

    private ImageView gUserAvatar;
    private TextView gUsername;
    private TextView gUserMessages;
    private TextView gStatus;
    private Button friendButton;
    private int userId;

    private TextView mLinkManageFriend;

    private String usernameProfile;

    public static ProfileFragment newInstance(int userId) {
        ProfileFragment dialog = new ProfileFragment();

        Bundle args = new Bundle();
        args.putInt("userId", userId);
        dialog.setArguments(args);

        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set style to dialog
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.MyDialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        userId = getArguments().getInt("userId");

        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        gUserAvatar = (ImageView) v.findViewById(R.id.userAvatar);
        gUsername = (TextView) v.findViewById(R.id.username);
        gUserMessages = (TextView) v.findViewById(R.id.userMessages);
        gStatus = (TextView) v.findViewById(R.id.status);
        friendButton = (Button) v.findViewById(R.id.sendRequest);
        mLinkManageFriend = (TextView) v.findViewById(R.id.linkManageFriends);

        friendButton.setOnClickListener(this);
        mLinkManageFriend.setOnClickListener(this);

        getDialog().setTitle(getArguments().getString("userId"));

        getUser(); // get informations about this current user

        isFriend(); // check if the current user and "me" are friends

        isConnected(); // check if the user is connected

        return v;
    }

    @Override
    public void onClick(View v) {
        if(v == friendButton){

            Log.d("profileLog","v.getTag() = " + v.getTag());
            if(v.getTag() != null) {
                if (v.getTag() == 1) { // users are friends

                    SocketServer.getInstance().getSocket().emit("decline_request_friend", userId);
                    friendButton.setText("Send a friendship request");
                    friendButton.setTag(2);

                } else if (v.getTag() == 2) { // no request between them

                    SocketServer.getInstance().getSocket().emit("send_friend_request", userId);
                    friendButton.setText("Revoke request");
                    friendButton.setTag(3);

                } else if (v.getTag() == 3) { // session sent a request to user

                    SocketServer.getInstance().getSocket().emit("decline_request_friend", userId);
                    friendButton.setText("Send a friendship request");
                    friendButton.setTag(2);

                } else if (v.getTag() == 4) { // user sent a request to session

                    SocketServer.getInstance().getSocket().emit("decline_request_friend", userId);
                    friendButton.setText("Send a friendship request");
                    friendButton.setTag(1);

                }
            }
        } else if (v ==mLinkManageFriend){
            startActivity(new Intent(getActivity().getApplicationContext(), FriendActivity.class));
        }
    }

    private void getUser(){
        // ask informations about the current user
        SocketServer.getInstance().getSocket().emit("get_user", userId);

        // get informations ask above
        SocketServer.getInstance().getSocket().on("get_user", new Emitter.Listener() {

            @Override
            public void call(final Object... args) {
                if(getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            JSONObject data = (JSONObject) args[0];

                            String username;
                            int nb_messages;
                            boolean sex;

                            try {
                                username = data.getString("username");
                                nb_messages = data.getInt("nb_messages");
                                sex = Tools.getBooleanFromInt(data.getInt("sexe"));
                            } catch (JSONException e) {
                                return;
                            }

                            usernameProfile = username;

                            gUsername.setText(username);
                            gUserMessages.setText("Messages: " + nb_messages);

                            // if is a boy
                            if (sex)
                                gUserAvatar.setImageResource(R.mipmap.boy);
                            else
                                gUserAvatar.setImageResource(R.mipmap.girl);


                            // if the user is me (I can't send a request to myself so we hide the button)

                            Log.d("profilLog", "usernameProfile = " + usernameProfile);
                            if (getActivity() != null) {
                                if (getActivity().getApplicationContext() != null || Tools.readFromPreferences(getActivity().getApplicationContext(), "username", null) != null) {
                                    if (usernameProfile.equals(Tools.readFromPreferences(getActivity().getApplicationContext(), "username", null))) {
                                        friendButton.setVisibility(View.GONE);
                                    }
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    private void isFriend(){
        SocketServer.getInstance().getSocket().emit("is_friend", userId);

        // get informations ask above
        SocketServer.getInstance().getSocket().on("is_friend", new Emitter.Listener() {

            @Override
            public void call(final Object... args) {
                if(getActivity() != null) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("profileLog", "value of args = " + args[0]);

                            boolean isFriend;
                            int mUserId = 0;
                            int mFriendId = 0;

                            JSONObject data = (JSONObject) args[0];
                            Log.d("profileLog", "data.length = " + data.length());

                            if (data.length() != 0) {
                                try {
                                    isFriend = Tools.getBooleanFromInt(data.getInt("valid"));
                                    mUserId = data.getInt("user_id");
                                    mFriendId = data.getInt("friend_id");
                                } catch (JSONException e) {
                                    return;
                                }

                                Log.d("profileLog", "isFriend = " + isFriend + " || mUserId = " + mUserId + " || mFriendId = " + mFriendId);

                                if (isFriend) { // if the users are friends
                                    friendButton.setText("Break friendship?");
                                    friendButton.setTag(1);
                                    Log.d("profileLog", "isFriend");
                                } else { // else if there are not friends but a request exist between them
                                    if (userId == mFriendId) { // else if session sent a request before
                                        friendButton.setText("Revoke request");
                                        friendButton.setTag(3);
                                        Log.d("profileLog", "userid is friendid");
                                    } else if (userId == mUserId) { // else if user of the current profil sent a request to session before
                                        friendButton.setTag(4);
                                        friendButton.setText("Decline request");
                                        Log.d("profileLog", "isFriend");
                                    }
                                }
                            } else {
                                Log.d("profileLog", "none request");
                                friendButton.setText("Send a friendship request");
                                friendButton.setTag(2);
                            }
                        }
                    });
                }
            }
        });
    }

   private void isConnected(){

       SocketServer.getInstance().getSocket().emit("is_connected", userId);

       SocketServer.getInstance().getSocket().on("is_connected", new Emitter.Listener() {

           @Override
           public void call(final Object... args) {
               if (getActivity() != null) {
                   getActivity().runOnUiThread(new Runnable() {
                       @Override
                       public void run() {
                           Log.d("profileFragmentLog", "is connected args = " + args[0]);
                            if((boolean) args[0])
                                gStatus.setText("Status: online");
                           else
                                gStatus.setText("Status: offline");
                       }
                   });
               }
           }
       });
   }
}