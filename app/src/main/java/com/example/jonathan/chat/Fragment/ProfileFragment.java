package com.example.jonathan.chat.Fragment;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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
    private Button friendButton;
    private int userId;

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
        friendButton = (Button) v.findViewById(R.id.sendRequest);
        friendButton.setOnClickListener(this);

        getDialog().setTitle(getArguments().getString("userId"));

        getUser(); // get informations about this current user

        isFriend(); // check if the current user and "me" are friends

        return v;
    }

    @Override
    public void onClick(View v) {
        if(v == friendButton){
            // if we are not already friend OR if I haven't already sent a request
            SocketServer.getInstance().getSocket().emit("send_friend_request", userId);

            friendButton.setText("Friend request sent!");
            friendButton.setBackgroundResource(R.color.buttonDisabled);
            friendButton.setEnabled(false);
            //friendButton.setClickable(false);
        }
    }

    private void getUser(){
        // ask informations about the current user
        SocketServer.getInstance().getSocket().emit("get_user", userId);

        // get informations ask above
        SocketServer.getInstance().getSocket().on("get_user", new Emitter.Listener() {

            @Override
            public void call(final Object... args) {
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

                if(usernameProfile.equals(Tools.readFromPreferences(getActivity().getParent(), "username", null))){
                    friendButton.setVisibility(View.GONE);
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

                boolean isFriend = (boolean) args[0];

                if(isFriend){
                    friendButton.setText("Friend request sent!");
                    friendButton.setBackgroundResource(R.color.buttonDisabled);
                    friendButton.setEnabled(false);
                }
            }
        });
    }
}