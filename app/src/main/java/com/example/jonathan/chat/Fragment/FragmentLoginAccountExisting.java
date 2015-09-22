package com.example.jonathan.chat.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jonathan.chat.ListActivity;
import com.example.jonathan.chat.R;
import com.example.jonathan.chat.Utils.SocketServer;
import com.example.jonathan.chat.Utils.Tools;

import org.json.JSONObject;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by Jonathan on 22/09/15.
 */
public class FragmentLoginAccountExisting extends Fragment implements View.OnClickListener{

    private ImageView avatar;
    private TextView username;
    private Button loginButton;
    private TextView changeAccount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login_account_existing, container, false);

        avatar = (ImageView) view.findViewById(R.id.userAvatar);
        username = (TextView) view.findViewById(R.id.userName);
        loginButton = (Button) view.findViewById(R.id.loginButton);
        changeAccount = (TextView) view.findViewById(R.id.changeAccount);

        // add event click on button and text
        loginButton.setOnClickListener(this);
        changeAccount.setOnClickListener(this);

        username.setText(Tools.readFromPreferences(getContext(), "username", null));

        // change the button color and the avatar with the sexe user
        if(Tools.readFromPreferences(getContext(), "sexe", "0").equals("boy")){
            loginButton.setBackgroundResource(R.drawable.button_boy); // put the button color in blue
            avatar.setImageResource(R.mipmap.boy);
        } else {
            loginButton.setBackgroundResource(R.drawable.button_girl);
            avatar.setImageResource(R.mipmap.girl);
        }

        return view;
    }

    @Override
    public void onClick(View v) {
        if(v == loginButton){
            // login
            // if the server is not disconnected, we can access to the rest
            if(!SocketServer.getInstance().isDisconnected()) {

                Tools.saveToPreferences(getContext(), "connected", "true");

                // Sending an object
                JSONObject obj = new JSONObject();
                try {
                    obj.put("username", Tools.readFromPreferences(getContext(), "username", null));
                    obj.put("sexe", Tools.readFromPreferences(getContext(), "sexe", "0"));
                } catch(Exception e){

                }

                // emit the new user
                SocketServer.getInstance().getSocket().emit("new_user", obj);

                Intent intent = new Intent(getContext(), ListActivity.class);
                getActivity().finish();
                startActivity(intent);
            }

        } else if (v == changeAccount) { // display fragment login new account
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            FragmentLoginNewAccount nextFrag = new FragmentLoginNewAccount();
            ft.replace(R.id.accountFragment, nextFrag, null);

            // Start the animated transition.
            ft.commit();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0); // hide the keyboard
    }
}
